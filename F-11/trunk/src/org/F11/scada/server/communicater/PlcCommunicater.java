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
 * PLC�Ƃ̒ʐM���s���N���X�ł��B
 */
public final class PlcCommunicater implements Communicater {
	private final static Logger log = Logger.getLogger(PlcCommunicater.class);
	/** ���[�h���C�g���b�N�I�u�W�F�N�g */
	private final ReadWriteLock lock = new ReadWriteLock();
	/** �f�o�C�X��� */
	private Environment device;
	/** ���M��M�҂��I�u�W�F�N�g */
	private ReplyWaiter waiter;
	/** �v���g�R���R���o�[�^ */
	private Converter converter;
	/** �ʐM�R�}���h�W���Ǘ��I�u�W�F�N�g */
	private volatile LinkageCommand linkageCommand;

	/** ���M�o�b�t�@ */
	private ByteBuffer sendBuffer = ByteBuffer.allocateDirect(2048);
	/** ��M�o�b�t�@ */
	private ByteBuffer recvBuffer = ByteBuffer.allocateDirect(2048);
	/** �Ǎ��݃f�[�^�o�b�t�@ */
	private ByteBuffer recvData = ByteBuffer.allocateDirect(2048);

	/**
	 * �R���X�g���N�^�[
	 * 
	 * @param device �f�o�C�X���
	 * @param converter �v���g�R���̃R���o�[�^
	 * @param listener ��M�f�[�^���t�惊�X�i�[
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

	public void addReadCommand(Collection commands) {
		linkageCommand.addDefine(commands);
	}

	public void removeReadCommand(Collection commands) {
		linkageCommand.removeDefine(commands);
	}

	// @see
	// org.F11.scada.server.communicater.Communicater#syncRead(java.util.Collection)
	public Map syncRead(Collection commands)
			throws InterruptedException,
			IOException,
			WifeException {

		return syncRead(commands, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.server.communicater.Communicater#syncRead(java.util.Collection,
	 *      boolean)
	 */
	public Map syncRead(Collection commands, boolean sameDataBalk)
			throws InterruptedException,
			IOException,
			WifeException {
		lock.readLock();
		try {
			log.debug("syncRead(" + commands.size() + ")");
			// �W���R�}���h���擾
			Collection lk_commands = linkageCommand.getDefines(commands);
			HashMap commandDataMap = new HashMap(lk_commands.size());
			for (Iterator it = lk_commands.iterator(); it.hasNext();) {
				WifeCommand lk_comm = (WifeCommand) it.next();

				// �Ǎ��ݒʐM
				converter.setReadCommand(lk_comm);
				sendrecv();

				if (sameDataBalk
						&& !linkageCommand.updateDefine(lk_comm, recvData)) {
					continue;
				}

				// �W���R�}���h����S�Ă̌��R�}���h���擾�i�t�Q�Ɓj
				Collection dh_commands = linkageCommand
						.getHolderCommands(lk_comm);
				// �v������Ă��Ȃ����R�}���h���폜
				dh_commands.retainAll(commands);
				for (Iterator it2 = dh_commands.iterator(); it2.hasNext();) {
					WifeCommand dh_comm = (WifeCommand) it2.next();
					// �v���ς݂̃R�}���h�ɂ��āA�f�[�^�𑗕t
					int byteOffset = (int) (dh_comm.getMemoryAddress() - lk_comm
							.getMemoryAddress()) * 2;
					byte[] cutdata = new byte[dh_comm.getWordLength() * 2];
					recvData.position(byteOffset);
					recvData.get(cutdata);
					commandDataMap.put(dh_comm, cutdata);
				}
			}
			return commandDataMap;
		} finally {
			lock.readUnlock();
		}
	}

	// @see
	// org.F11.scada.server.communicater.Communicater#syncWrite(java.util.Collection,
	// java.util.Map)
	public void syncWrite(Map commands)
			throws InterruptedException,
			IOException,
			WifeException {
		lock.writeLock();
		try {
			log.debug("syncWrite(" + commands.size() + ")");
			for (Iterator it = commands.keySet().iterator(); it.hasNext();) {
				WifeCommand comm = (WifeCommand) it.next();
				byte[] data = (byte[]) commands.get(comm);
				if (data == null) {
					throw new IllegalArgumentException("datas not found");
				}

				// �����ݒʐM
				converter.setWriteCommand(comm, data);
				sendrecv();
			}
		} finally {
			lock.writeUnlock();
		}
	}

	/*
	 * �R���o�[�^�̌��ʂɊ�ʐM���܂��B �R���o�[�^���ݒ�ς݂ł��邱�ƁB ���s�񐔒��ߎ��ɁA�ŏI�����̗�O���������܂��B
	 */
	private void sendrecv()
			throws IOException,
			WifeException,
			InterruptedException {
		recvData.clear();
		while (converter.hasCommand()) {
			sendBuffer.clear();
			converter.nextCommand(sendBuffer);
			sendBuffer.flip();
			// ���M���M�҂�
			waiter.syncSendRecv(sendBuffer, recvBuffer);
			WifeException ex = null;
			if (recvBuffer.remaining() <= 0) {
				// ���f�[�^�̓^�C���A�E�g
				sendBuffer.flip();
				ex = new WifeException(0, 0, "Recved time out. "
						+ WifeUtilities.toString(sendBuffer));
			} else {
				ex = converter.checkCommandResponce(recvBuffer);
				if (ex != null) {
					// �^�C���A�E�g�ȊO�̃G���[�����Ȃ�Ύ��s�̑O�ɑ҂�
					Thread.sleep(device.getPlcTimeout());
				}
			}
			// �G���[�����Ȃ玎�s���J��Ԃ�
			for (int i = 0; i < device.getPlcRetryCount() && ex != null; i++) {
				if (ex != null) {
					log.warn("ID[" + device.getDeviceID() + "] try["
							+ String.valueOf(i) + "] error[" + ex.getMessage()
							+ "]");
				}
				sendBuffer.clear();
				converter.retryCommand(sendBuffer);
				sendBuffer.flip();
				// ���M���M�҂�
				waiter.syncSendRecv(sendBuffer, recvBuffer);
				if (recvBuffer.remaining() <= 0) {
					// ���f�[�^�̓^�C���A�E�g
					sendBuffer.flip();
					ex = new WifeException(0, 0, "Recved time out. "
							+ WifeUtilities.toString(sendBuffer));
				} else {
					ex = converter.checkCommandResponce(recvBuffer);
					if (ex != null) {
						// �^�C���A�E�g�ȊO�̃G���[�����Ȃ�Ύ��s�̑O�ɑ҂�
						Thread.sleep(device.getPlcTimeout());
					}
				}
			}
			if (ex != null) {
				log.warn("ID[" + device.getDeviceID() + "] error decision["
						+ ex.getMessage() + "]");
				throw ex;
			}
			// ��M�f�[�^��ǉ�
			converter.getResponceData(recvBuffer, recvData);
		}
		recvData.flip();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.server.communicater.Communicater#addWifeEventListener(org.F11.scada.server.event.WifeEventListener)
	 */
	public void addWifeEventListener(WifeEventListener l) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.server.communicater.Communicater#removeWifeEventListener(org.F11.scada.server.event.WifeEventListener)
	 */
	public void removeWifeEventListener(WifeEventListener l) {
		throw new UnsupportedOperationException();
	}
}