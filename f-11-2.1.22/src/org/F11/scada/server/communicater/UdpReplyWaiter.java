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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

/**
 * UDPポートとデータをやり取りするクラスです。 複数スレッドからの通信要求には対応していません。
 */
public final class UdpReplyWaiter implements RecvListener, ReplyWaiter {
	private final static Logger log = Logger.getLogger(UdpReplyWaiter.class);

	/** タイムアウト値（ミリ秒） */
	private final long timeout;
	/** ホストのアドレス */
	private final InetSocketAddress local;
	/** 相手先のアドレス */
	private final InetSocketAddress target1;
	/** 相手先のアドレス(二重化用) */
	private final InetSocketAddress target2;
	/** 受信すべきデータのヘッダー */
	private final byte[] header;
	/** 受信バッファへの参照 */
	private ByteBuffer recvBuffer;
	/** 通常アドレスを参照中 */
	private boolean isTarget1;
	/** UDPポート管理オブジェクト */
	private UdpPortChannel portChannel;

	/** 受信待ちフラグ */
	private volatile boolean recvWaiting = false;

	/**
	 * コンストラクタ 通信するUDPポートをマネージャーから取得します。
	 * @param device デバイス情報
	 * @param header 受信すべきヘッダー
	 */
	public UdpReplyWaiter(Environment device, byte[] header)
			throws IOException, InterruptedException {
		this.header = header;
		timeout = device.getPlcTimeout();
		local = new InetSocketAddress(device.getHostIpAddress(),
				device.getHostPortNo());
		target1 = new InetSocketAddress(device.getPlcIpAddress(),
				device.getPlcPortNo());
		if (device.getPlcIpAddress2() != null
				&& 0 < device.getPlcIpAddress2().length())
			target2 = new InetSocketAddress(device.getPlcIpAddress2(),
					device.getPlcPortNo());
		else
			target2 = null;

		isTarget1 = true;
		portChannel = (UdpPortChannel) PortChannelManager.getInstance().addPortListener(
				"UDP", target1, local, this);
	}

	// @see org.F11.scada.server.communicater.ReplyWaiter#close()
	public void close() throws InterruptedException {
		PortChannelManager.getInstance().removePortListener(local, this);
	}

	// @see
	// org.F11.scada.server.communicater.ReplyWaiter#syncSendRecv(java.nio.ByteBuffer,
	// java.nio.ByteBuffer)
	public void syncSendRecv(ByteBuffer sendBuffer, ByteBuffer recvBuffer)
			throws InterruptedException {
		log.debug("syncSendRecv()");
		// 受信待ち
		recvWaiting = true;
		this.recvBuffer = recvBuffer;
		recvBuffer.clear().flip();
		// ポートへ送信要求
		portChannel.sendRequest((isTarget1 ? target1 : target2), sendBuffer);
		synchronized (this) {
			wait(timeout);
		}
	}

	public void change2sub() throws IOException, InterruptedException {
		if (target2 != null) {
			if (isTarget1) {
				PortChannelManager.getInstance().removePortListener(local, this);
				isTarget1 = false;
				portChannel = (UdpPortChannel) PortChannelManager.getInstance().addPortListener(
						"UDP", target2, local, this);
				log.info("target change to 2nd.");
			} else {
				PortChannelManager.getInstance().removePortListener(local, this);
				isTarget1 = true;
				portChannel = (UdpPortChannel) PortChannelManager.getInstance().addPortListener(
						"UDP", target1, local, this);
				log.info("target change to 1st.");
			}
		}
	}

	// @see org.F11.scada.server.communicater.RecvListener#getRecvAddress()
	public InetSocketAddress getRecvAddress() {
		return (isTarget1 ? target1 : target2);
	}

	// @see org.F11.scada.server.communicater.RecvListener#getRecvHeader()
	public byte[] getRecvHeader() {
		return header;
	}

	// @see
	// org.F11.scada.server.communicater.RecvListener#recvPerformed(java.nio.ByteBuffer)
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

	public void reOpenPort() throws IOException, InterruptedException {
		// No Operation.
	}
}
