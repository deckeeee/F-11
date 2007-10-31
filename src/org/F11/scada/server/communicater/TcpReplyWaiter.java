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

import org.apache.log4j.Logger;

/**
 * UDPポートとデータをやり取りするクラスです。
 * 複数スレッドからの通信要求には対応していません。
 */
public final class TcpReplyWaiter implements RecvListener, ReplyWaiter {
	private final static Logger log = Logger.getLogger(TcpReplyWaiter.class);

	/** タイムアウト値（ミリ秒） */
	private final long timeout;
	/** ホストのアドレス */
	private final InetSocketAddress local;
	/** 相手先のアドレス */
	private final InetSocketAddress target;
	/** TCPポート管理オブジェクト */
	private final TcpPortChannel portChannel;
	/** 受信すべきデータのヘッダー */
	private final byte[] header;
	/** 受信バッファへの参照 */
	private ByteBuffer recvBuffer;

	/** 受信待ちフラグ */
	private boolean recvWaiting = false;

	/**
	 * コンストラクタ
	 * 通信するTCPポートをマネージャーから取得します。
	 * @param device デバイス情報
	 * @param header 受信すべきヘッダー
	 */
	public TcpReplyWaiter(Environment device, byte[] header)
		throws IOException, InterruptedException {
		this.header = header;
		timeout = device.getPlcTimeout();
		local =
			new InetSocketAddress(
				device.getHostIpAddress(),
				device.getHostPortNo());
		target =
			new InetSocketAddress(
				device.getPlcIpAddress(),
				device.getPlcPortNo());
		portChannel =
			(TcpPortChannel) PortChannelManager.getInstance().addPortListener(
				device.getDeviceKind(),
				target,
				local,
				this);
	}

	// @see org.F11.scada.server.communicater.ReplyWaiter#close()
	public void close() throws InterruptedException {
		PortChannelManager.getInstance().removePortListener(target, this);
	}

	// @see org.F11.scada.server.communicater.ReplyWaiter#syncSendRecv(java.nio.ByteBuffer, java.nio.ByteBuffer)
	public void syncSendRecv(ByteBuffer sendBuffer, ByteBuffer recvBuffer)
		throws IOException, InterruptedException {
		log.debug("syncSendRecv()");
		this.recvBuffer = recvBuffer;
		recvBuffer.clear().flip();
		// ポートへ送信要求
		portChannel.sendRequest(target, sendBuffer);
		synchronized (this) {
			// 受信待ち
			recvWaiting = true;
			wait(timeout);
		}
	}

	// @see org.F11.scada.server.communicater.RecvListener#getRecvAddress()
	public InetSocketAddress getRecvAddress() {
		return target;
	}

	// @see org.F11.scada.server.communicater.RecvListener#getRecvHeader()
	public byte[] getRecvHeader() {
		return header;
	}

	// @see org.F11.scada.server.communicater.RecvListener#recvPerformed(java.nio.ByteBuffer)
	public synchronized void recvPerformed(ByteBuffer data) {
		log.debug("recvPerformed()");
		if (recvWaiting) {
			log.debug("recvPerformed() recved.");
			recvBuffer.clear();
			recvBuffer.put(data).flip();
			recvWaiting = false;
			notifyAll();
		}
	}

}
