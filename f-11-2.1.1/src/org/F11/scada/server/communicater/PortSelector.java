/*
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.F11.scada.server.communicater;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * セレクタを管理するクラスです。
 */
public final class PortSelector implements Runnable {
	private final static Logger log = Logger.getLogger(PortSelector.class);

	/** セレクタを監視するスレッド */
	private Thread thread;
	/** セレクタ */
	private AbstractSelector selector;
	/** セレクタ登録済みkeyのマップ */
	private Map enterKeys = new HashMap();

	/** 内部からの処理要求を保持するキュー */
	private LinkedList commands = new LinkedList();
	/** 保持する最大数 */
	private static final int COMMAND_MAX = 2000;

	/**
	 * コンストラクタ 監視スレッドを開始します。
	 */
	public PortSelector() throws IOException {
		startup();
	}

	/*
	 * セレクタを作成し、監視スレッドを開始します。
	 */
	private void startup() throws IOException {
		log.debug("startup.");
		selector = SelectorProvider.provider().openSelector();
		thread = new Thread(this);
		thread.setName("PortSelector");
		thread.start();
	}

	/**
	 * リスナーが登録されていれば True
	 */
	public synchronized boolean isActive() {
		if (enterKeys.isEmpty())
			return false;
		return true;
	}

	/**
	 * チャンネルをセレクタに登録し、リスナー登録します。 登録完了まで返りません。
	 * 
	 * @param listener
	 *            登録するリスナー
	 * @return 登録後のセレクションキー
	 */
	public void addListener(PortListener listener) throws InterruptedException {
		log.debug("addListener.");

		// 登録要求
		enterCommand(new OpenChannel(listener));
		// 登録完了まで待機
		synchronized (this) {
			while (!enterKeys.containsKey(listener)) {
				selector.wakeup();
				wait(1000);
			}
		}
	}

	/**
	 * 指定セレクションキーのチャンネルを閉じ、セレクタから削除します。 削除完了まで返りません。
	 * 
	 * @param selectionKey
	 *            セレクションキー
	 * @return 全てのチャンネルが閉じた場合、True
	 */
	public void removeListener(PortListener listener)
			throws InterruptedException {
		log.debug("removeListener.");
		// 削除要求
		enterCommand(new CloseChannel(listener));
		// セレクションキーが削除されるまで待機
		synchronized (this) {
			while (enterKeys.containsKey(listener)) {
				selector.wakeup();
				wait(1000);
			}
			if (selector.keys().isEmpty()) {
				selector.wakeup();
				thread.interrupt();
				thread = null;
				log.debug("shutdown.");
			}
		}
	}

	/**
	 * 指定セレクションキーの対象セットを変更します。 変更完了を待たずに返ります。
	 * 
	 * @param selectionKey
	 *            セレクションキー
	 * @param ops
	 *            対象セット
	 */
	public void setInterestOps(PortListener listener, int ops)
			throws InterruptedException {
		log.debug("setInterestOps.");
		// 変更要求
		enterCommand(new SetInterestOpsChannel(listener, ops));
	}
	public void resetInterestOps(PortListener listener, int ops)
			throws InterruptedException {
		log.debug("resetInterestOps.");
		// 変更要求
		enterCommand(new ResetInterestOpsChannel(listener, ops));
	}

	/**
	 * 指定のチャンネルへ再オープン要求します。変更完了を待たずに返ります。
	 * 
	 * @param listener
	 * @throws InterruptedException
	 */
	public void reopenChannel(PortListener listener)
			throws InterruptedException {
		log.debug("reopenChannel.");
		// 再オープン要求
		enterCommand(new ReopenChannel(listener));
	}

	/**
	 * セレクタの監視ループです。
	 */
	public void run() {
		log.debug("start.");
		Thread th = thread;
		try {
			while (th != null) {
				int n = 0;
				try {
					n = selector.select();
				} catch (Exception e) {
					if (!selector.isOpen()) {
			            log.fatal("Exception caught: ", e);
						break;
					}
				}
				if (n > 0) {
					Iterator keyIterator = selector.selectedKeys().iterator();

					while (keyIterator.hasNext()) {
						SelectionKey key = (SelectionKey) keyIterator.next();
						keyIterator.remove();
						if (!key.isValid()) {
							continue;
						}
						PortListener listener = (PortListener) key.attachment();
						if (listener == null) {
							continue;
						}
						try {
							if (key.isAcceptable()) {
								listener.onAccept();
							} else if (key.isConnectable()) {
								listener.onConnect();
							} else if (key.isReadable()) {
								listener.onRead();
							} else if (key.isWritable()) {
								listener.onWrite();
							}
						} catch (Exception e) {
				            log.warn("Exception caught: ", e);

							synchronized(this) {
								commands.addLast(new ReopenChannel(listener));
							}
						}
					}
				}
				// 処理要求
				executeCommands();
				th = thread;
			}
		} catch (Exception e) {
            log.fatal("Exception caught: ", e);
		} finally {
			th = null;
			try {
				selector.close();
			} catch (IOException e) {
	            log.fatal("Exception caught: ", e);
			}
		}
		log.debug("stop.");
	}

	private synchronized void enterCommand(SelectCommand command)
			throws InterruptedException {
		// キューが空くまで待機
		while (COMMAND_MAX <= commands.size()) {
			wait(1000);
		}
		commands.addLast(command);
		selector.wakeup();
	}

	/*
	 * 処理要求キューの先頭を実行します。
	 */
	private synchronized void executeCommands() throws Exception {
		List nextCommands = new ArrayList();
		while (!commands.isEmpty()) {
			SelectCommand command = (SelectCommand) commands.removeFirst();
			SelectCommand leftComm = command.execute();
			if (leftComm != null) {
				nextCommands.add(leftComm);
			}
		}
		commands.addAll(nextCommands);
		notifyAll();
	}

	/*
	 * 処理要求のインターフェースです。
	 */
	private interface SelectCommand {
		public SelectCommand execute() throws Exception;
	}

	/*
	 * オープン要求を実行するクラスです。
	 */
	private final class OpenChannel implements SelectCommand {
		private PortListener listener;

		public OpenChannel(PortListener listener) {
			this.listener = listener;
		}
		public SelectCommand execute() throws IOException, InterruptedException {
			if (log.isDebugEnabled()) {
				log.debug("OpenChannel. " + listener.toString());
			}
			SelectableChannel channel = listener.open();
			SelectionKey key = channel.register(selector, 0);
			key.attach(listener);
			enterKeys.put(listener, key);
			return null;
		}

	};

	/*
	 * クローズ要求を実行するクラスです。
	 */
	private final class CloseChannel implements SelectCommand {
		private final PortListener listener;

		public CloseChannel(PortListener listener) {
			this.listener = listener;
		}

		public SelectCommand execute() throws IOException {
			if (log.isDebugEnabled()) {
				log.debug("CloseChannel. " + listener.toString());
			}
			SelectionKey key = (SelectionKey) enterKeys.remove(listener);
			if (key != null) {
				key.cancel();
				listener.close();
				return null;
			} else {
				enterKeys.put(listener, key);
				return this;
			}
		}

	};

	/*
	 * 再オープン要求を実行するクラスです。
	 */
	private final class ReopenChannel implements SelectCommand {
		private final PortListener listener;

		public ReopenChannel(PortListener listener) {
			this.listener = listener;
		}

		public SelectCommand execute() throws IOException, InterruptedException {
			log.debug("ReopenChannel. " + listener.toString());
			SelectionKey key = (SelectionKey) enterKeys.remove(listener);
			if (key != null) {
				key.cancel();
				listener.close();
				return new OpenChannel(listener);
			} else {
				enterKeys.put(listener, key);
				return this;
			} 
		}

	};

	/*
	 * 変更要求を実行するクラスです。
	 */
	private final class SetInterestOpsChannel implements SelectCommand {
		private final PortListener listener;
		private final int ops;

		public SetInterestOpsChannel(PortListener listener, int ops) {
			this.listener = listener;
			this.ops = ops;
		}
		public SelectCommand execute() {
			log.debug("SetInterestOpsChannel. " + listener.toString());
			SelectionKey key = (SelectionKey) enterKeys.get(listener);
			if (key != null && key.isValid()) {
				int old = key.interestOps();
				key.interestOps(old | ops);
			}
			return null;
		}

	};

	/*
	 * 変更要求を実行するクラスです。
	 */
	private final class ResetInterestOpsChannel implements SelectCommand {
		private final PortListener listener;
		private final int ops;

		public ResetInterestOpsChannel(PortListener listener, int ops) {
			this.listener = listener;
			this.ops = ops;
		}
		public SelectCommand execute() {
			log.debug("ResetInterestOpsChannel. " + listener.toString());
			SelectionKey key = (SelectionKey) enterKeys.get(listener);
			if (key != null && key.isValid()) {
				int old = key.interestOps();
				key.interestOps(old & (~ops));
			}
			return null;
		}

	};

}