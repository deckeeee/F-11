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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.F11.scada.WifeUtilities;
import org.apache.log4j.Logger;

/**
 * UDPポートを管理するクラスです。
 */
public final class UdpPortChannel implements PortListener, PortChannel {
	private final static Logger log = Logger.getLogger(UdpPortChannel.class);

	/** ホストのアドレス */
	private final InetSocketAddress local;
	/** セレクタへの参照 */
	private final PortSelector selector;
	/** UDPチャンネル */
	private DatagramChannel channel;

	/** 送信要求キュー */
	private volatile LinkedList<SendData> writeRequest = new LinkedList<SendData>();
	/** キュー最大値 */
	private static final int QUE_MAX = 2000;
	/** 今回の送信要求オブジェクト */
	private SendData sendData;

	/** 受信バッファ */
	private final ByteBuffer recvBuffer = ByteBuffer.allocateDirect(2048);

	/** リスナーのマップ */
	private final Map<String, RecvListener> id2listenerMap =
		Collections.synchronizedMap(new HashMap<String, RecvListener>());

	/**
	 * コンストラクタ
	 * UDPチャンネルをオープンし、セレクタへ登録します。
	 * @param local ホストアドレス
	 * @param selector セレクタ
	 */
	public UdpPortChannel(InetSocketAddress local, PortSelector selector)
		throws IOException, InterruptedException {
		this.local = local;
		this.selector = selector;
		selector.addListener(this);
	}

	// @see org.F11.scada.server.communicater.PortChannel#addListener(org.F11.scada.server.communicater.RecvListener)
	public void addListener(RecvListener listener) {
		String idkey =
			makeIDKey(
				listener.getRecvAddress(),
				ByteBuffer.wrap(listener.getRecvHeader()));
		if (idkey == null) {
			throw new IllegalArgumentException("ID is not right.");
		}
		log.debug("addListener :" + idkey);
		id2listenerMap.put(idkey, listener);
	}

	// @see org.F11.scada.server.communicater.PortChannel#removeListener(org.F11.scada.server.communicater.RecvListener)
	public boolean removeListener(RecvListener listener) {
		String idkey =
			makeIDKey(
				listener.getRecvAddress(),
				ByteBuffer.wrap(listener.getRecvHeader()));
		log.debug("removeListener :" + idkey);
		if (id2listenerMap.remove(idkey) == null) {
			throw new IllegalArgumentException(
				"[" + idkey + "] does not exist.");
		}
		return id2listenerMap.isEmpty();
	}

	// @see org.F11.scada.server.communicater.PortChannel#closeReq()
	public void closeReq() throws InterruptedException {
		selector.removeListener(this);
	}

	/**
	 * セレクタに送信要求します。
	 * @param target 相手先アドレス
	 * @param sendBuffer 送信バッファ
	 */
	public synchronized void sendRequest(
		InetSocketAddress target,
		ByteBuffer sendBuffer)
		throws InterruptedException {
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
	public synchronized void onWrite()
		throws IOException, InterruptedException {
		if (writeRequest.size() <= 0) {
			selector.resetInterestOps(this, SelectionKey.OP_WRITE);
			notifyAll();
			return;
		}
		log.debug("onWrite");
		if (sendData == null) {
			sendData = writeRequest.removeFirst();
		}
		sendData.send(channel);
		if (!sendData.hasRemaining()) {
			sendData = null;
			notifyAll();
		}
	}

	// @see org.F11.scada.server.communicater.PortListener#onRead()
	public synchronized void onRead() throws IOException {
		if (!channel.isOpen())
			return;
		log.debug("onRead");
		recvBuffer.clear();
		InetSocketAddress addr =
			(InetSocketAddress) channel.receive(recvBuffer);
		recvBuffer.flip();
		if (addr != null && recvBuffer.hasRemaining()) {
			String idkey = makeIDKey(addr, recvBuffer);
			RecvListener listener = null;
			listener = id2listenerMap.get(idkey);
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
	public void onConnect() {
		log.debug("onConnect");
	}

	// @see org.F11.scada.server.communicater.PortListener#open()
	public SelectableChannel open() throws IOException, InterruptedException {
		channel = SelectorProvider.provider().openDatagramChannel();
		channel.configureBlocking(false);
		channel.socket().setBroadcast(true);
		log.info("local address : " + local.toString());
		channel.socket().bind(local);

		selector.setInterestOps(this, SelectionKey.OP_READ);

		return channel;
	}

	// @see org.F11.scada.server.communicater.PortListener#close()
	public void close() throws IOException {
		channel.close();
	}

	/*
	 * 指定されたデータからリスナー特定用の文字列を作成します。
	 * @param data 受信バッファ
	 * @return キー文字列
	 */
	protected String makeIDKey(InetSocketAddress addr, ByteBuffer data) {
		if ((data.get(0) == (byte) 0xc0 || data.get(0) == (byte) 0xc1)
			&& data.get(1) == (byte) 0x00) {
			byte[] id = new byte[6];
			data.position(3);
			data.get(id).position(0);
			return addr.getAddress().getHostAddress()
				+ ":FINS_"
				+ WifeUtilities.toString(id);
		} else if (data.get(0) == (byte) 0xd0 && data.get(1) == (byte) 0x00) {
			byte[] id = new byte[5];
			data.position(2);
			data.get(id).position(0);
			return addr.getAddress().getHostAddress()
				+ ":MC3E_"
				+ WifeUtilities.toString(id);
		} else if (
			data.get(0) == (byte) 0x81
				&& (data.get(1) == (byte) 0x0a || data.get(1) == (byte) 0x0b)) {
			return addr.getAddress().getHostAddress() + ":BACNET_";
		} else if (data.get(0) == (byte) 0x81 || data.get(0) == (byte) 0x83) {
			return addr.getAddress().getHostAddress() + ":MC1E_";
		}
		return null;
	}

	// @see java.lang.Object#toString()
	public String toString() {
		return "UDP local["
			+ local.getAddress().getHostAddress()
			+ ":"
			+ local.getPort()
			+ "]";
	}

	/*
	 * 送信要求用のクラスです。
	 */
	private final class SendData {
		/* 相手先アドレス */
		private final InetSocketAddress target;
		/* 送信バッファへの参照 */
		private final ByteBuffer sendBuffer;

		/*
		 * コンストラクタ
		 */
		public SendData(InetSocketAddress target, ByteBuffer sendBuffer) {
			this.target = target;
			this.sendBuffer = sendBuffer;
		}

		/*
		 * 指定チャンネルへ送信します。
		 */
		public void send(DatagramChannel channel) throws IOException {
			channel.send(sendBuffer, target);
		}

		/*
		 * 送信バッファに残りがあれば True
		 */
		public boolean hasRemaining() {
			return sendBuffer.hasRemaining();
		}
	}

}
