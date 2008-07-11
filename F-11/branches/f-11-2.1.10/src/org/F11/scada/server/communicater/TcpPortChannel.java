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
 * TCP�|�[�g���Ǘ�����N���X�ł��B
 */
public final class TcpPortChannel implements PortListener, PortChannel {
	private final static Logger log = Logger.getLogger(TcpPortChannel.class);

	/** �Z���N�^�ւ̎Q�� */
	private final PortSelector selector;
	/** TCP�`�����l�� */
	private SocketChannel channel;
	/** �����A�h���X */
	private final InetSocketAddress targetAddress;

	/** ���M�v���L���[ */
	private volatile LinkedList writeRequest = new LinkedList();
	/** �L���[�ő�l */
	private static final int QUE_MAX = 2000;
	/** ����̑��M�v���I�u�W�F�N�g */
	private SendData sendData;

	/** ��M�o�b�t�@ */
	private final ByteBuffer recvBuffer = ByteBuffer.allocateDirect(2048);

	/** ���X�i�[�̃}�b�v */
	private final Map id2listenerMap = Collections
			.synchronizedMap(new HashMap());

	/**
	 * �R���X�g���N�^ UDP�`�����l�����I�[�v�����A�Z���N�^�֓o�^���܂��B
	 * 
	 * @param local
	 *            �z�X�g�A�h���X
	 * @param selector
	 *            �Z���N�^
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
	 * �Z���N�^�ɑ��M�v�����܂��B
	 * 
	 * @param target
	 *            �����A�h���X
	 * @param sendBuffer
	 *            ���M�o�b�t�@
	 */
	public synchronized void sendRequest(InetSocketAddress target,
			ByteBuffer sendBuffer) throws IOException, InterruptedException {
		// ���M�v���L���[�󂫑҂�
		while (QUE_MAX <= writeRequest.size()) {
			wait();
		}
		log.debug("sendRequest");
		writeRequest.addLast(new SendData(target, sendBuffer));

		// �ΏۃZ�b�g��OP_WRITE��ǉ�
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

		// �ʐM�|�[�g�̐ڑ��V�[�P���X����
		boolean ret = false;
		try {
			ret = channel.finishConnect();
		} catch (ConnectException e) {
			log.warn("TCP�ڑ������ۂ��ꂽ�B"
					+ targetAddress.getAddress().getHostAddress());
			selector.reopenChannel(this);
			return;
		}

		if (ret) {
			log.info("TCP�ʐM�J�n�B" + targetAddress.getAddress().getHostAddress());
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
	 * �w�肳�ꂽ�f�[�^���烊�X�i�[����p�̕�������쐬���܂��B @param data ��M�o�b�t�@ @return �L�[������
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
	 * ���M�v���p�̃N���X�ł��B
	 */
	private final class SendData {
		/* �����A�h���X */
		//private final InetSocketAddress target;
		/* ���M�o�b�t�@�ւ̎Q�� */
		private final ByteBuffer sendBuffer;

		/*
		 * �R���X�g���N�^
		 */
		public SendData(InetSocketAddress target, ByteBuffer sendBuffer) {
			//this.target = target;
			this.sendBuffer = sendBuffer;
		}

		/*
		 * ���M�o�b�t�@�Ɏc�肪����� True
		 */
		public boolean hasRemaining() {
			return sendBuffer.hasRemaining();
		}
	}

}
