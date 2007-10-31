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
 * UDP�|�[�g�ƃf�[�^������肷��N���X�ł��B
 * �����X���b�h����̒ʐM�v���ɂ͑Ή����Ă��܂���B
 */
public final class TcpReplyWaiter implements RecvListener, ReplyWaiter {
	private final static Logger log = Logger.getLogger(TcpReplyWaiter.class);

	/** �^�C���A�E�g�l�i�~���b�j */
	private final long timeout;
	/** �z�X�g�̃A�h���X */
	private final InetSocketAddress local;
	/** �����̃A�h���X */
	private final InetSocketAddress target;
	/** TCP�|�[�g�Ǘ��I�u�W�F�N�g */
	private final TcpPortChannel portChannel;
	/** ��M���ׂ��f�[�^�̃w�b�_�[ */
	private final byte[] header;
	/** ��M�o�b�t�@�ւ̎Q�� */
	private ByteBuffer recvBuffer;

	/** ��M�҂��t���O */
	private boolean recvWaiting = false;

	/**
	 * �R���X�g���N�^
	 * �ʐM����TCP�|�[�g���}�l�[�W���[����擾���܂��B
	 * @param device �f�o�C�X���
	 * @param header ��M���ׂ��w�b�_�[
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
		// �|�[�g�֑��M�v��
		portChannel.sendRequest(target, sendBuffer);
		synchronized (this) {
			// ��M�҂�
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
