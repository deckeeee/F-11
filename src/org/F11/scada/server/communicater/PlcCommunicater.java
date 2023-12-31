/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.F11.scada.server.communicater;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.F11.scada.WifeException;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.converter.Converter;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.event.WifeEventListener;
import org.apache.log4j.Logger;

/**
 * PLCとの通信を行うクラスです。
 */
public final class PlcCommunicater implements Communicater {
	private final static Logger log = Logger.getLogger(PlcCommunicater.class);
	/** リードライトロックオブジェクト */
	private final ReadWriteLock lock = new ReadWriteLock();
	/** デバイス情報 */
	private Environment device;
	/** 送信受信待ちオブジェクト */
	private ReplyWaiter waiter;
	/** プロトコルコンバータ */
	private Converter converter;
	/** 通信コマンド集合管理オブジェクト */
	private volatile LinkageCommand linkageCommand;

	/** 送信バッファ */
//	private ByteBuffer sendBuffer = ByteBuffer.allocate(2048);
	/** 受信バッファ */
	private ByteBuffer recvBuffer = ByteBuffer.allocate(2048);
	/** 読込みデータバッファ */
	private ByteBuffer recvData = ByteBuffer.allocate(2048);
	/** FINS/TCPプロトコル通信の有無 */
	private boolean isFinsTcp;

	/**
	 * コンストラクター
	 * @param device デバイス情報
	 * @param converter プロトコルのコンバータ
	 * @param listener 受信データ送付先リスナー
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public PlcCommunicater(Environment device, Converter converter)
			throws IOException, InterruptedException {
		if ("UDP".equals(device.getDeviceKind())) {
			byte[] header = converter.setEnvironment(device);
			waiter = new UdpReplyWaiter(device, header);
		} else if ("TCP".equals(device.getDeviceKind())) {
			byte[] header = converter.setEnvironment(device);
			waiter = new TcpReplyWaiter(device, header);
		} else {
			throw new IllegalArgumentException("not support "
					+ device.getDeviceKind());
		}

		this.device = device;
		this.converter = converter;
		this.linkageCommand = new LinkageCommand(converter);
		this.isFinsTcp = isFinsTcp(device);
	}

	private boolean isFinsTcp(Environment device) {
		return "TCP".equalsIgnoreCase(device.getDeviceKind())
			&& "FINSTCP".equalsIgnoreCase(device.getPlcCommKind());
	}

	// @see org.F11.scada.server.communicater.Communicater#close()
	public void close() throws InterruptedException {
		lock.writeLock();
		try {
			log.debug("close()");
			waiter.close();
		} finally {
			lock.writeUnlock();
		}
	}

	public void addReadCommand(Collection<WifeCommand> commands) {
		linkageCommand.addDefine(commands);
	}

	public void removeReadCommand(Collection<WifeCommand> commands) {
		linkageCommand.removeDefine(commands);
	}

	// @see
	// org.F11.scada.server.communicater.Communicater#syncRead(java.util.Collection)
	public Map<WifeCommand, byte[]> syncRead(Collection<WifeCommand> commands)
			throws InterruptedException, IOException, WifeException {

		return syncRead(commands, true);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.F11.scada.server.communicater.Communicater#syncRead(java.util.Collection
	 * , boolean)
	 */
	public Map<WifeCommand, byte[]> syncRead(Collection<WifeCommand> commands,
			boolean sameDataBalk) throws InterruptedException, IOException,
			WifeException {
		lock.writeLock();
		try {
			log.debug("syncRead(" + commands.size() + ")");
			// 集合コマンドを取得
			Collection<WifeCommand> lk_commands = linkageCommand.getDefines(commands);
			HashMap<WifeCommand, byte[]> commandDataMap = new HashMap<WifeCommand, byte[]>(
					lk_commands.size());
			for (Iterator<WifeCommand> it = lk_commands.iterator(); it.hasNext();) {
				WifeCommand lk_comm = it.next();

				// 読込み通信
				converter.setReadCommand(lk_comm);
				sendrecv();

				if (sameDataBalk
						&& !linkageCommand.updateDefine(lk_comm, recvData)) {
					continue;
				}

				// 集合コマンドから全ての元コマンドを取得（逆参照）
				Collection<WifeCommand> dh_commands = linkageCommand.getHolderCommands(lk_comm);
				// 要求されていない元コマンドを削除
				dh_commands.retainAll(commands);
				for (Iterator<WifeCommand> it2 = dh_commands.iterator(); it2.hasNext();) {
					WifeCommand dh_comm = it2.next();
					// 要求済みのコマンドについて、データを送付
					int byteOffset = (int) (dh_comm.getMemoryAddress() - lk_comm.getMemoryAddress()) * 2;
					byte[] cutdata = new byte[dh_comm.getWordLength() * 2];
					recvData.position(byteOffset);
					recvData.get(cutdata);
					commandDataMap.put(dh_comm, cutdata);
				}
			}
			return commandDataMap;
		} finally {
			lock.writeUnlock();
		}
	}

	// @see
	// org.F11.scada.server.communicater.Communicater#syncWrite(java.util.Collection,
	// java.util.Map)
	public void syncWrite(Map<WifeCommand, byte[]> commands)
			throws InterruptedException, IOException, WifeException {
		lock.writeLock();
		try {
			log.debug("syncWrite(" + commands.size() + ")");
			for (Iterator<WifeCommand> it = commands.keySet().iterator(); it.hasNext();) {
				WifeCommand comm = it.next();
				byte[] data = commands.get(comm);
				if (data == null) {
					throw new IllegalArgumentException("datas not found");
				}

				// 書込み通信
				converter.setWriteCommand(comm, data);
				sendrecv();
			}
		} finally {
			lock.writeUnlock();
		}
	}

	/*
	 * コンバータの結果に基き通信します。 コンバータが設定済みであること。 試行回数超過時に、最終原因の例外が発生します。
	 */
	private void sendrecv() throws IOException, WifeException,
			InterruptedException {
		recvData.clear();
		while (converter.hasCommand()) {
			ByteBuffer sendBuffer = ByteBuffer.allocate(2048);
			sendBuffer.clear();
			converter.nextCommand(sendBuffer);
			sendBuffer.flip();
			// 送信後受信待ち
			waiter.syncSendRecv(sendBuffer, recvBuffer);
			WifeException ex = checkError(sendBuffer);
			// エラー発生なら試行を繰り返す
			for (int i = 0; i < device.getPlcRetryCount() && ex != null; i++) {
				if (ex != null) {
					log.warn("ID[" + device.getDeviceID() + "] try["
							+ String.valueOf(i) + "] error[" + ex.getMessage()
							+ "]");
				}
				sendBuffer.clear();
				converter.retryCommand(sendBuffer);
				sendBuffer.flip();
				// 送信後受信待ち
				waiter.syncSendRecv(sendBuffer, recvBuffer);
				ex = checkError(sendBuffer);
			}
			// エラー発生ならば、二重化PLCと通信
			if (ex != null && device.getPlcIpAddress2() != null
					&& 0 < device.getPlcIpAddress2().length()) {

				// 通信先IPを切り替える
				waiter.change2sub();

				recvData.clear();
				sendBuffer.clear();
				converter.retryCommand(sendBuffer);
				sendBuffer.flip();
				// 送信後受信待ち
				waiter.syncSendRecv(sendBuffer, recvBuffer);
				ex = checkError(sendBuffer);
				// エラー発生なら試行を繰り返す
				for (int i = 0; i < device.getPlcRetryCount() && ex != null; i++) {
					if (ex != null) {
						log.warn("ID[" + device.getDeviceID()
								+ "] changed try[" + String.valueOf(i)
								+ "] error[" + ex.getMessage() + "]");
					}
					sendBuffer.clear();
					converter.retryCommand(sendBuffer);
					sendBuffer.flip();
					// 送信後受信待ち
					waiter.syncSendRecv(sendBuffer, recvBuffer);
					ex = checkError(sendBuffer);
				}
			}
			// FINS/TCPでエラー発生ならばポートを再オープン
			if (ex != null && isFinsTcp) {
				// 通信先IPを切り替える
				waiter.reOpenPort();

				recvData.clear();
				sendBuffer.clear();
				converter.retryCommand(sendBuffer);
				sendBuffer.flip();
				// 送信後受信待ち
				waiter.syncSendRecv(sendBuffer, recvBuffer);
				ex = checkError(sendBuffer);
				// エラー発生なら試行を繰り返す
				for (int i = 0; i < device.getPlcRetryCount() && ex != null; i++) {
					if (ex != null) {
						log.warn("ID[" + device.getDeviceID()
								+ "] changed try[" + String.valueOf(i)
								+ "] error[" + ex.getMessage() + "]");
					}
					sendBuffer.clear();
					converter.retryCommand(sendBuffer);
					sendBuffer.flip();
					// 送信後受信待ち
					waiter.syncSendRecv(sendBuffer, recvBuffer);
					ex = checkError(sendBuffer);
				}
			}

			if (ex != null) {
				log.warn("ID[" + device.getDeviceID() + "] error decision["
						+ ex.getMessage() + "]");
				throw ex;
			}
			// 受信データを追加
			converter.getResponceData(recvBuffer, recvData);
		}
		recvData.flip();
	}

	private WifeException checkError(ByteBuffer sendBuffer) throws WifeException,
			InterruptedException {
		WifeException ex = null;
		if (recvBuffer.remaining() <= 0) {
			// 無データはタイムアウト
			sendBuffer.flip();
			ex = new WifeException(0, 0, "Recved time out. "
					+ WifeUtilities.toString(sendBuffer));
		} else {
			ex = converter.checkCommandResponce(recvBuffer);
			if (ex != null) {
				// タイムアウト以外のエラー発生ならば試行の前に待つ
				Thread.sleep(device.getPlcTimeout());
			}
		}
		return ex;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.F11.scada.server.communicater.Communicater#addWifeEventListener(org
	 * .F11.scada.server.event.WifeEventListener)
	 */
	public void addWifeEventListener(WifeEventListener l) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.F11.scada.server.communicater.Communicater#removeWifeEventListener
	 * (org.F11.scada.server.event.WifeEventListener)
	 */
	public void removeWifeEventListener(WifeEventListener l) {
		throw new UnsupportedOperationException();
	}
}