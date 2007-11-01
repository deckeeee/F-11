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
 * �Z���N�^���Ǘ�����N���X�ł��B
 */
public final class PortSelector implements Runnable {
	private final static Logger log = Logger.getLogger(PortSelector.class);

	/** �Z���N�^���Ď�����X���b�h */
	private Thread thread;
	/** �Z���N�^ */
	private AbstractSelector selector;
	/** �Z���N�^�o�^�ς�key�̃}�b�v */
	private Map enterKeys = new HashMap();

	/** ��������̏����v����ێ�����L���[ */
	private LinkedList commands = new LinkedList();
	/** �ێ�����ő吔 */
	private static final int COMMAND_MAX = 2000;

	/**
	 * �R���X�g���N�^ �Ď��X���b�h���J�n���܂��B
	 */
	public PortSelector() throws IOException {
		startup();
	}

	/*
	 * �Z���N�^���쐬���A�Ď��X���b�h���J�n���܂��B
	 */
	private void startup() throws IOException {
		log.debug("startup.");
		selector = SelectorProvider.provider().openSelector();
		thread = new Thread(this);
		thread.setName("PortSelector");
		thread.start();
	}

	/**
	 * ���X�i�[���o�^����Ă���� True
	 */
	public synchronized boolean isActive() {
		if (enterKeys.isEmpty())
			return false;
		return true;
	}

	/**
	 * �`�����l�����Z���N�^�ɓo�^���A���X�i�[�o�^���܂��B �o�^�����܂ŕԂ�܂���B
	 * 
	 * @param listener
	 *            �o�^���郊�X�i�[
	 * @return �o�^��̃Z���N�V�����L�[
	 */
	public void addListener(PortListener listener) throws InterruptedException {
		log.debug("addListener.");

		// �o�^�v��
		enterCommand(new OpenChannel(listener));
		// �o�^�����܂őҋ@
		synchronized (this) {
			while (!enterKeys.containsKey(listener)) {
				selector.wakeup();
				wait(1000);
			}
		}
	}

	/**
	 * �w��Z���N�V�����L�[�̃`�����l������A�Z���N�^����폜���܂��B �폜�����܂ŕԂ�܂���B
	 * 
	 * @param selectionKey
	 *            �Z���N�V�����L�[
	 * @return �S�Ẵ`�����l���������ꍇ�ATrue
	 */
	public void removeListener(PortListener listener)
			throws InterruptedException {
		log.debug("removeListener.");
		// �폜�v��
		enterCommand(new CloseChannel(listener));
		// �Z���N�V�����L�[���폜�����܂őҋ@
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
	 * �w��Z���N�V�����L�[�̑ΏۃZ�b�g��ύX���܂��B �ύX������҂����ɕԂ�܂��B
	 * 
	 * @param selectionKey
	 *            �Z���N�V�����L�[
	 * @param ops
	 *            �ΏۃZ�b�g
	 */
	public void setInterestOps(PortListener listener, int ops)
			throws InterruptedException {
		log.debug("setInterestOps.");
		// �ύX�v��
		enterCommand(new SetInterestOpsChannel(listener, ops));
	}
	public void resetInterestOps(PortListener listener, int ops)
			throws InterruptedException {
		log.debug("resetInterestOps.");
		// �ύX�v��
		enterCommand(new ResetInterestOpsChannel(listener, ops));
	}

	/**
	 * �w��̃`�����l���֍ăI�[�v���v�����܂��B�ύX������҂����ɕԂ�܂��B
	 * 
	 * @param listener
	 * @throws InterruptedException
	 */
	public void reopenChannel(PortListener listener)
			throws InterruptedException {
		log.debug("reopenChannel.");
		// �ăI�[�v���v��
		enterCommand(new ReopenChannel(listener));
	}

	/**
	 * �Z���N�^�̊Ď����[�v�ł��B
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
				// �����v��
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
		// �L���[���󂭂܂őҋ@
		while (COMMAND_MAX <= commands.size()) {
			wait(1000);
		}
		commands.addLast(command);
		selector.wakeup();
	}

	/*
	 * �����v���L���[�̐擪�����s���܂��B
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
	 * �����v���̃C���^�[�t�F�[�X�ł��B
	 */
	private interface SelectCommand {
		public SelectCommand execute() throws Exception;
	}

	/*
	 * �I�[�v���v�������s����N���X�ł��B
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
	 * �N���[�Y�v�������s����N���X�ł��B
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
	 * �ăI�[�v���v�������s����N���X�ł��B
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
	 * �ύX�v�������s����N���X�ł��B
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
	 * �ύX�v�������s����N���X�ł��B
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