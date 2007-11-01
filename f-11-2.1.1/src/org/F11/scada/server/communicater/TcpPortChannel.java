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
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.F11.scada.WifeUtilities;
import org.apache.log4j.Logger;

/**
 * TCPポートを管理するクラスです。
 */
public final class TcpPortChannel implements PortListener, PortChannel {
	private final static Logger log = Logger.getLogger(TcpPortChannel.class);

	/** セレクタへの参照 */
	private final PortSelector selector;
	/** TCPチャンネル */
	private SocketChannel channel;
	/** 相手先アドレス */
	private final InetSocketAddress targetAddress;

	/** 送信要求キュー */
	private volatile LinkedList writeRequest = new LinkedList();
	/** キュー最大値 */
	private static final int QUE_MAX = 2000;
	/** 今回の送信要求オブジェクト */
	private SendData sendData;

	/** 受信バッファ */
	private final ByteBuffer recvBuffer = ByteBuffer.allocateDirect(2048);

	/** リスナーのマップ */
	private final Map id2listenerMap = Collections
			.synchronizedMap(new HashMap());

	/**
	 * コンストラクタ UDPチャンネルをオープンし、セレクタへ登録します。
	 * 
	 * @param local
	 *            ホストアドレス
	 * @param selector
	 *            セレクタ
	 */
	public TcpPortChannel(PortSelector selector, InetSocketAddress targetAddress)
			throws IOException, InterruptedException {
		this.selector = selector;
		this.targetAddress = targetAddress;
		selector.addListener(this);
	}

	// @see
	// org.F11.scada.server.communicater.PortChannel#addListener(org.F11.scada.server.communicater.RecvListener)
	public void addListener(RecvListener listener) {
		String idkey = makeIDKey(ByteBuffer.wrap(listener.getRecvHeader()));
		if (idkey == null) {
			throw new IllegalArgumentException("ID is not right.");
		}
		log.debug("addListener :" + idkey);
		id2listenerMap.put(idkey, listener);
	}

	// @see
	// org.F11.scada.server.communicater.PortChannel#removeListener(org.F11.scada.server.communicater.RecvListener)
	public boolean removeListener(RecvListener listener) {
		String idkey = makeIDKey(ByteBuffer.wrap(listener.getRecvHeader()));
		log.debug("removeListener :" + idkey);
		if (id2listenerMap.remove(idkey) == null) {
			throw new IllegalArgumentException("[" + idkey
					+ "] does not exist.");
		}
		return id2listenerMap.isEmpty();
	}

	// @see org.F11.scada.server.communicater.PortListener#close()
	public void closeReq() throws InterruptedException {
		selector.removeListener(this);
	}

	/**
	 * セレクタに送信要求します。
	 * 
	 * @param target
	 *            相手先アドレス
	 * @param sendBuffer
	 *            送信バッファ
	 */
	public synchronized void sendRequest(InetSocketAddress target,
			ByteBuffer sendBuffer) throws IOException, InterruptedException {
		// 送信要求キュー空き待ち
		while (QUE_MAX <= writeRequest.size()) {
			wait();
		}
		log.debug("sendRequest");
		writeRequest.addLast(new SendData(target, sendBuffer));

		// 対象セットにOP_WRITEを追加
		selector.setInterestOps(this, SelectionKey.OP_WRITE);
	}

	// @see org.F11.scada.server.communicater.PortListener#onWrite()
	public synchronized void onWrite() throws IOException, InterruptedException {
		if (writeRequest.size() <= 0) {
			selector.resetInterestOps(this, SelectionKey.OP_WRITE);
			notifyAll();
			return;
		}
		log.debug("onWrite");
		if (sendData == null) {
			sendData = (SendData) writeRequest.removeFirst();
		}
		channel.write(sendData.sendBuffer);
		if (!sendData.hasRemaining()) {
			sendData = null;
			notifyAll();
		}
	}

	// @see org.F11.scada.server.communicater.PortListener#onRead()
	public synchronized void onRead() throws IOException {
		if (!channel.isConnected())
			return;
		log.debug("onRead");
		recvBuffer.clear();
		int len = 0;
		len = channel.read(recvBuffer);
		if (0 < len) {
			recvBuffer.flip();
			String idkey = makeIDKey(recvBuffer);
			RecvListener listener = null;
			listener = (RecvListener) id2listenerMap.get(idkey);
			if (listener != null) {
				listener.recvPerformed(recvBuffer);
			}
		}
	}

	// @see org.F11.scada.server.communicater.PortListener#onAccept()
	public void onAccept() {
		log.debug("onAccept");
	}

	// @see org.F11.scada.server.communicater.PortListener#onConnect()
	public void onConnect() throws IOException, InterruptedException {
		log.debug("onConnect");

		// 通信ポートの接続シーケンス完了
		boolean ret = false;
		try {
			ret = channel.finishConnect();
		} catch (ConnectException e) {
			log.warn("TCP接続が拒否された。"
					+ targetAddress.getAddress().getHostAddress());
			selector.reopenChannel(this);
			return;
		}

		if (ret) {
			log.info("TCP通信開始。" + targetAddress.getAddress().getHostAddress());
			selector.resetInterestOps(this, SelectionKey.OP_CONNECT);
			selector.setInterestOps(this, SelectionKey.OP_READ
					| SelectionKey.OP_WRITE);
		} else {
			selector.setInterestOps(this, SelectionKey.OP_CONNECT);
		}
	}

	// @see org.F11.scada.server.communicater.PortListener#open()
	public SelectableChannel open() throws IOException, InterruptedException {
		channel = SelectorProvider.provider().openSocketChannel();
		channel.configureBlocking(false);
		channel.socket().setKeepAlive(true);

		channel.connect(targetAddress);

		selector.setInterestOps(this, SelectionKey.OP_CONNECT);
		return channel;
	}

	// @see org.F11.scada.server.communicater.PortListener#close()
	public void close() throws IOException {
		if (channel.isConnected())
			channel.socket().shutdownOutput();
		channel.close();
	}

	/*
	 * 指定されたデータからリスナー特定用の文字列を作成します。 @param data 受信バッファ @return キー文字列
	 */
	protected String makeIDKey(ByteBuffer data) {
		if ((data.get(0) == (byte) 0xc0 || data.get(0) == (byte) 0xc1)
				&& data.get(1) == (byte) 0x00) {
			byte[] id = new byte[6];
			data.position(3);
			data.get(id).position(0);
			return "FINS_" + WifeUtilities.toString(id);
		} else if (data.get(0) == (byte) 0xd0 && data.get(1) == (byte) 0x00) {
			byte[] id = new byte[5];
			data.position(2);
			data.get(id).position(0);
			return "MC3E_" + WifeUtilities.toString(id);
		} else if (data.get(0) == (byte) 0x81 || data.get(0) == (byte) 0x83) {
			return "MC1E_";
		}
		return null;
	}

	// @see java.lang.Object#toString()
	public String toString() {
		return "TCP target[" + targetAddress.getAddress().getHostAddress()
				+ ":" + targetAddress.getPort() + "]";
	}

	/*
	 * 送信要求用のクラスです。
	 */
	private final class SendData {
		/* 相手先アドレス */
		//private final InetSocketAddress target;
		/* 送信バッファへの参照 */
		private final ByteBuffer sendBuffer;

		/*
		 * コンストラクタ
		 */
		public SendData(InetSocketAddress target, ByteBuffer sendBuffer) {
			//this.target = target;
			this.sendBuffer = sendBuffer;
		}

		/*
		 * 送信バッファに残りがあれば True
		 */
		public boolean hasRemaining() {
			return sendBuffer.hasRemaining();
		}
	}

}
