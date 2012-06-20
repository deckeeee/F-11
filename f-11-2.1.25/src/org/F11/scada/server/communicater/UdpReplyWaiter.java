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
 * UDP�|�[�g�ƃf�[�^������肷��N���X�ł��B �����X���b�h����̒ʐM�v���ɂ͑Ή����Ă��܂���B
 */
public final class UdpReplyWaiter implements RecvListener, ReplyWaiter {
	private final static Logger log = Logger.getLogger(UdpReplyWaiter.class);

	/** �^�C���A�E�g�l�i�~���b�j */
	private final long timeout;
	/** �z�X�g�̃A�h���X */
	private final InetSocketAddress local;
	/** �����̃A�h���X */
	private final InetSocketAddress target1;
	/** �����̃A�h���X(��d���p) */
	private final InetSocketAddress target2;
	/** ��M���ׂ��f�[�^�̃w�b�_�[ */
	private final byte[] header;
	/** ��M�o�b�t�@�ւ̎Q�� */
	private ByteBuffer recvBuffer;
	/** �ʏ�A�h���X���Q�ƒ� */
	private boolean isTarget1;
	/** UDP�|�[�g�Ǘ��I�u�W�F�N�g */
	private UdpPortChannel portChannel;

	/** ��M�҂��t���O */
	private volatile boolean recvWaiting = false;

	/**
	 * �R���X�g���N�^ �ʐM����UDP�|�[�g���}�l�[�W���[����擾���܂��B
	 * @param device �f�o�C�X���
	 * @param header ��M���ׂ��w�b�_�[
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
		// ��M�҂�
		recvWaiting = true;
		this.recvBuffer = recvBuffer;
		recvBuffer.clear().flip();
		// �|�[�g�֑��M�v��
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
