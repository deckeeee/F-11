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

package org.F11.scada.server.converter;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.F11.scada.WifeException;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.event.WifeCommand;

/**
 * Wife�ʐM���W���[����MC3E�R�}���h�����̎����N���X�B
 */
public class MC1E implements Converter {
	/** MC1E�R�}���h �w�b�_�� */
	public static final int MC1E_COMMAND_HEADER_LENGTH = 1;
	/** MC1E�R�}���h �I���R�[�h�� */
	public static final int MC1E_COMMAND_ENDCODE_LENGTH = 1;

	/** ���������  D �f�[�^���W�X�^��\���܂��B */
	public static final Integer D_AREA = new Integer(0);
	/** ���������  M ���������[�G���A��\���܂��B */
	public static final Integer M_AREA = new Integer(1);
	/** ���������  X ���̓����[�G���A��\���܂��B */
	public static final Integer X_AREA = new Integer(2);
	/** ���������  Y �o�̓����[�G���A��\���܂��B */
	public static final Integer Y_AREA = new Integer(3);
	/** ���������  B �����N�����[�G���A��\���܂��B */
	public static final Integer B_AREA = new Integer(4);
	/** ���������  ZR �t�@�C�����W�X�^��\���܂��B */
	public static final Integer R_AREA = new Integer(10);
	/** ���������  W �����N���W�X�^��\���܂��B */
	public static final Integer W_AREA = new Integer(11);

	/** �R�}���h��ʂ̃}�b�v */
	private static final Map memoryModeMap;
	static {
		memoryModeMap = new HashMap();
		memoryModeMap.put(D_AREA, new ComndKindWorddev(0x20, 0x44));
		memoryModeMap.put(M_AREA, new ComndKindBitdev(0x20, 0x4d));
		memoryModeMap.put(X_AREA, new ComndKindBitdev(0x20, 0x58));
		memoryModeMap.put(Y_AREA, new ComndKindBitdev(0x20, 0x59));
		memoryModeMap.put(B_AREA, new ComndKindBitdev(0x20, 0x42));
		memoryModeMap.put(R_AREA, new ComndKindWorddev(0x20, 0x52));
		memoryModeMap.put(W_AREA, new ComndKindWorddev(0x20, 0x57));
	}

	/** PC�ԍ� */
	private byte pcno = (byte) 0x00;
	/** CPU�Ď��^�C�} */
	private byte[] cpu = {(byte) 0x00, (byte) 0x00 };

	/**
	 *  �f�t�H���g�R���X�g���N�^
	 */
	public MC1E() {
	}

	/** ����ݒ肵�A���X�|���X�w�b�_��Ԃ��B*/
	public byte[] setEnvironment(Environment device) {
		pcno = (byte) (device.getPlcNodeNo() & 0xff);
		cpu[0] = (byte) (device.getPlcWatchWait() % 0x100);
		cpu[1] = (byte) (device.getPlcWatchWait() / 0x100);

		return new byte[] {(byte) 0x81, (byte) 0x00 };
	}

	/** �Ǎ��݃R�}���h��ݒ肷��B*/
	public void setReadCommand(WifeCommand commdef) throws WifeException {
		mc1eCommand = READ_MC1ECOMMAND;
		mc1eCommand.setCommand(commdef, pcno, cpu, null);
	}
	/** �����݃R�}���h��ݒ肷��B*/
	public void setWriteCommand(WifeCommand commdef, byte[] data)
		throws WifeException {
		mc1eCommand = WRITE_MC1ECOMMAND;
		mc1eCommand.setCommand(commdef, pcno, cpu, data);
	}

	/** �R�}���h���擾�\���H */
	public boolean hasCommand() {
		return mc1eCommand.hasCommand();
	}

	/** �R�}���h���쐬���A����̃R�}���h���������܂��B */
	public void nextCommand(ByteBuffer sendBuffer) {
		mc1eCommand.nextCommand(sendBuffer);
	}

	/** �O����s�R�}���h���쐬���܂��B */
	public void retryCommand(ByteBuffer sendBuffer) {
		mc1eCommand.retryCommand(sendBuffer);
	}

	/** ���M�f�[�^�Ǝ�M�f�[�^�̐��������������܂��B */
	public WifeException checkCommandResponce(ByteBuffer recvBuffer)
		throws WifeException {
		byte[] err = { 0, 0 };
		if (recvBuffer.remaining()
			< (MC1E_COMMAND_HEADER_LENGTH + MC1E_COMMAND_ENDCODE_LENGTH)) {
			StringBuffer sb = new StringBuffer();
			sb.append("RecvData (");
			sb.append(WifeUtilities.toString(recvBuffer));
			sb.append(") is short!");
			return new WifeException(
				WifeException.WIFE_ERROR,
				WifeException.WIFE_NET_RESPONCE_ERROR,
				sb.toString() + mc1eCommand.toString());
		}
		// �I���R�[�h
		if (recvBuffer.get(1) != (byte) 0x00) {
			StringBuffer sb = new StringBuffer();
			sb.append("End code error: ");
			sb.append(" RecvData (");
			sb.append(WifeUtilities.toString(recvBuffer));
			sb.append(")");
			return new WifeException(
				WifeException.WIFE_ERROR,
				WifeException.WIFE_NET_RESPONCE_ENDCODE_ERROR,
				err,
				sb.toString() + mc1eCommand.toString());
		}
		return null;
	}

	/** ��M�f�[�^����f�[�^�����擾���܂��B */
	public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
		mc1eCommand.getResponceData(recvBuffer, recvData);
	}

	/** �ʐM���̍ő咷��Ԃ��܂��B */
	public int getPacketMaxSize(WifeCommand commdef) {
		ComndKind kind =
			(ComndKind) memoryModeMap.get(new Integer(commdef.getMemoryMode()));
		return kind.getReadMaxLen() / 2;
	}

	/** 
	 * ������\����Ԃ��܂��B 
	 */
	public String toString() {
		return mc1eCommand.toString();
	}

	/**
	 * �R�}���h�ϊ��w���p�[�N���X�̃C���^�[�t�F�C�X�ł��B 
	 */
	private interface Mc1eCommand {
		/** ��������R�}���h��ݒ肷��B*/
		public void setCommand(
			WifeCommand commdef,
			byte pcno,
			byte[] cpu,
			byte[] data)
			throws WifeException;
		/** �R�}���h���擾�\���H */
		public boolean hasCommand();
		/** �R�}���h���쐬���A����̃R�}���h���������܂��B */
		public void nextCommand(ByteBuffer sendBuffer);
		/** �O����s�R�}���h���쐬���܂��B */
		public void retryCommand(ByteBuffer sendBuffer);

		/** ��M�f�[�^����f�[�^�����擾���܂��B */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData);
	}

	/** 
	 * �Ǎ��݃R�}���h�ϊ��w���p�[�N���X�̃C���X�^���X�ł��B
	 */
	private final Mc1eCommand READ_MC1ECOMMAND = new Mc1eCommand() {
		/** PC�ԍ� */
		private byte pcno;
		/** CPU�Ď��^�C�} */
		private byte[] cpu;
		/** PLC��������� */
		private byte[] memoryMode;
		/** PLC�������A�h���X */
		private long memoryAddress;
		/** �������ׂ�PLC�f�[�^�̎c��o�C�g�� */
		private int restByteLength = 0;
		/** �����PLC�f�[�^�o�C�g�� */
		private int thisByteLength = 0;
		/** �ő�PLC�f�[�^�o�C�g�� */
		private int maxByteLen = 0;
		/** �f�o�C�X��ʃw���p�[�N���X�ւ̎Q�� */
		private ComndKind kind;

		/** ��������R�}���h�̃l�^��ݒ肷��B*/
		public void setCommand(
			WifeCommand commdef,
			byte pcno,
			byte[] cpu,
			byte[] data)
			throws WifeException {
			this.pcno = pcno;
			this.cpu = cpu;
			// �f�o�C�X��ʃw���p�[�N���X
			kind =
				(ComndKind) memoryModeMap.get(
					new Integer(commdef.getMemoryMode()));
			if (kind == null) {
				throw new WifeException(
					WifeException.WIFE_ERROR,
					WifeException.WIFE_NET_COMMAND_ERROR,
					"Not supported memory mode " + commdef.getMemoryMode());
			}
			memoryMode = kind.getMemKind();
			memoryAddress = commdef.getMemoryAddress();
			restByteLength = commdef.getWordLength() * 2;
			thisByteLength = 0;
			maxByteLen = kind.getReadMaxLen();
		}

		/** �R�}���h���擾�\���H */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}
		/** �R�}���h���쐬���A����̃R�}���h���������܂��B */
		public void nextCommand(ByteBuffer sendBuffer) {
			long addr = kind.getAddress(memoryAddress);

			// �f�o�C�X�Ɉˑ����钷���𒴂��Ă����
			if (maxByteLen < restByteLength) {
				thisByteLength = maxByteLen;
				restByteLength -= maxByteLen;
			} else {
				thisByteLength = restByteLength;
				restByteLength = 0;
			}

			// MC1E�w�b�_�̍쐬
			sendBuffer.put((byte) 0x01); // ���[�h�Ǎ���
			sendBuffer.put(pcno);
			sendBuffer.put(cpu);
			// �f�o�C�X�A�h���X
			sendBuffer.put((byte) (addr % 0x100));
			sendBuffer.put((byte) ((addr / 0x100) % 0x100));
			sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
			sendBuffer.put((byte) 0x00);
			// �f�o�C�X�R�[�h
			sendBuffer.put(memoryMode);
			// ����
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			sendBuffer.put((byte) 0x00);

			// ����̃A�h���X
			memoryAddress += (thisByteLength / 2);
		}
		/** �O����s�R�}���h���쐬���܂��B */
		public void retryCommand(ByteBuffer sendBuffer) {
			long addr = kind.getAddress(memoryAddress - (thisByteLength / 2));

			// MC1E�w�b�_�̍쐬
			sendBuffer.put((byte) 0x01); // ���[�h�Ǎ���
			sendBuffer.put(pcno);
			sendBuffer.put(cpu);
			// �f�o�C�X�A�h���X
			sendBuffer.put((byte) (addr % 0x100));
			sendBuffer.put((byte) ((addr / 0x100) % 0x100));
			sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
			sendBuffer.put((byte) 0x00);
			// �f�o�C�X�R�[�h
			sendBuffer.put(memoryMode);
			// ����
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			sendBuffer.put((byte) 0x00);
		}

		/** ��M�f�[�^����f�[�^�����擾���܂��B */
		public void getResponceData(
			ByteBuffer recvBuffer,
			ByteBuffer recvData) {
			for (int i =
				MC1E_COMMAND_HEADER_LENGTH + MC1E_COMMAND_ENDCODE_LENGTH;
				i < recvBuffer.remaining();
				i += 2) {
				recvData.put(recvBuffer.get(i + 1));
				recvData.put(recvBuffer.get(i + 0));
			}
		}

		/** 
		 * ���̃C���X�^���X�̕�����\����Ԃ��܂��B
		 */
		public String toString() {
			StringBuffer s = new StringBuffer();
			s.append("ReadMode:").append("\n");
			s.append("memoryMode:").append(memoryMode).append("\n");
			s.append("memoryAddress:").append(memoryAddress).append("\n");
			s.append("restByteLength:").append(restByteLength).append("\n");
			s.append("thisByteLength:").append(thisByteLength).append("\n");
			return s.toString();
		}
	};

	/** 
	 * �����݃R�}���h�ϊ��w���p�[�N���X�̃C���X�^���X�ł��B
	 */
	private final Mc1eCommand WRITE_MC1ECOMMAND = new Mc1eCommand() {
		/** PC�ԍ� */
		private byte pcno;
		/** CPU�Ď��^�C�} */
		private byte[] cpu;
		/** PLC��������� */
		private byte[] memoryMode;
		/** PLC�������A�h���X */
		private long memoryAddress;
		/** �����݃f�[�^ */
		private byte[] writeData;
		/** �����݃f�[�^�ʒu */
		private int writePos;
		/** �������ׂ�PLC�f�[�^�̎c��o�C�g�� */
		private int restByteLength = 0;
		/** �����PLC�f�[�^�o�C�g�� */
		private int thisByteLength = 0;
		/** �ő�PLC�f�[�^�o�C�g�� */
		private int maxByteLen = 0;
		/** �f�o�C�X��ʃw���p�[�N���X�ւ̎Q�� */
		private ComndKind kind;

		/** ��������R�}���h��ݒ肷��B*/
		public void setCommand(
			WifeCommand commdef,
			byte pcno,
			byte[] cpu,
			byte[] data)
			throws WifeException {
			this.pcno = pcno;
			this.cpu = cpu;
			// �f�o�C�X��ʃw���p�[�N���X
			kind =
				(ComndKind) memoryModeMap.get(
					new Integer(commdef.getMemoryMode()));
			if (kind == null) {
				throw new WifeException(
					WifeException.WIFE_ERROR,
					WifeException.WIFE_NET_COMMAND_ERROR,
					"Not supported memory mode " + commdef.getMemoryMode());
			}
			memoryMode = kind.getMemKind();
			memoryAddress = commdef.getMemoryAddress();
			writeData = data;
			writePos = 0;
			restByteLength = commdef.getWordLength() * 2;
			thisByteLength = 0;
			maxByteLen = kind.getWriteMaxLen();
		}

		/** �R�}���h���擾�\���H */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}

		/** �R�}���h���쐬���A����̃R�}���h���������܂��B */
		public void nextCommand(ByteBuffer sendBuffer) {
			long addr = kind.getAddress(memoryAddress);

			// �f�o�C�X�Ɉˑ����钷���𒴂��Ă����
			if (maxByteLen < restByteLength) {
				thisByteLength = maxByteLen;
				restByteLength -= maxByteLen;
			} else {
				thisByteLength = restByteLength;
				restByteLength = 0;
			}

			// MC3E�w�b�_�̍쐬
			sendBuffer.put((byte) 0x03); // ���[�h������
			sendBuffer.put(pcno);
			sendBuffer.put(cpu);
			// �f�o�C�X�A�h���X
			sendBuffer.put((byte) (addr % 0x100));
			sendBuffer.put((byte) ((addr / 0x100) % 0x100));
			sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
			sendBuffer.put((byte) 0x00);
			// �f�o�C�X�R�[�h
			sendBuffer.put(memoryMode);
			// ����
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			sendBuffer.put((byte) 0x00);
			// �����݃f�[�^
			for (int i = writePos; i < (writePos + thisByteLength); i += 2) {
				sendBuffer.put(writeData[i + 1]);
				sendBuffer.put(writeData[i + 0]);
			}

			// ����̃A�h���X
			memoryAddress += (thisByteLength / 2);
			writePos += thisByteLength;
		}

		/** �O����s�R�}���h���쐬���܂��B */
		public void retryCommand(ByteBuffer sendBuffer) {
			long addr = kind.getAddress(memoryAddress - (thisByteLength / 2));
			int pos = writePos - thisByteLength;

			// MC3E�w�b�_�̍쐬
			sendBuffer.put((byte) 0x03); // ���[�h������
			sendBuffer.put(pcno);
			sendBuffer.put(cpu);
			// �f�o�C�X�A�h���X
			sendBuffer.put((byte) (addr % 0x100));
			sendBuffer.put((byte) ((addr / 0x100) % 0x100));
			sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
			sendBuffer.put((byte) 0x00);
			// �f�o�C�X�R�[�h
			sendBuffer.put(memoryMode);
			// ����
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			sendBuffer.put((byte) 0x00);
			// �����݃f�[�^
			for (int i = pos; i < (pos + thisByteLength); i += 2) {
				sendBuffer.put(writeData[i + 1]);
				sendBuffer.put(writeData[i + 0]);
			}
		}

		/** ��M�f�[�^����f�[�^�����擾���܂��B */
		public void getResponceData(
			ByteBuffer recvBuffer,
			ByteBuffer recvData) {
		}

		/** 
		 * ���̃C���X�^���X�̕�����\����Ԃ��܂��B
		 */
		public String toString() {
			StringBuffer s = new StringBuffer();
			s.append("WriteMode:").append("\n");
			s.append("memoryMode:").append(memoryMode).append("\n");
			s.append("memoryAddress:").append(memoryAddress).append("\n");
			s.append("restByteLength:").append(restByteLength).append("\n");
			s.append("thisByteLength:").append(thisByteLength).append("\n");
			return s.toString();
		}
	};

	/** �R�}���h�ϊ��N���X */
	private Mc1eCommand mc1eCommand = READ_MC1ECOMMAND;

	/**
	 * �f�o�C�X��ʃw���p�[�N���X
	 */
	private abstract static class ComndKind {
		private final byte[] memKind;

		public ComndKind(int kd1, int kd2) {
			this.memKind = new byte[2];
			this.memKind[0] = (byte) (kd1 & 0xff);
			this.memKind[1] = (byte) (kd2 & 0xff);
		}

		public byte[] getMemKind() {
			return memKind;
		}

		abstract public int getReadMaxLen();
		abstract public int getWriteMaxLen();
		abstract public long getAddress(long addr);
	}

	/**
	 * �r�b�g�f�o�C�X���
	 */
	private static class ComndKindBitdev extends ComndKind {
		/** �r�b�g�f�o�C�X��ʂ̑���M�p�P�b�g�ő�T�C�Y��\���萔�ł� */
		public static final int BITREAD_MC1E_PACKET_MAX_SIZE = 128 * 2;
		public static final int BITWRITE_MC1E_PACKET_MAX_SIZE = 40 * 2;
		public ComndKindBitdev(int kd1, int kd2) {
			super(kd1, kd2);
		}
		public int getReadMaxLen() {
			return BITREAD_MC1E_PACKET_MAX_SIZE;
		}
		public int getWriteMaxLen() {
			return BITWRITE_MC1E_PACKET_MAX_SIZE;
		}
		public long getAddress(long addr) {
			return addr * 16;
		}
	}

	/**
	 * ���[�h�f�o�C�X���
	 */
	private static class ComndKindWorddev extends ComndKind {
		/** ���[�h�f�o�C�X��ʂ̑���M�p�P�b�g�ő�T�C�Y��\���萔�ł� */
		public static final int WORD_MC1E_PACKET_MAX_SIZE = 256 * 2;
		public ComndKindWorddev(int kd1, int kd2) {
			super(kd1, kd2);
		}
		public int getReadMaxLen() {
			return WORD_MC1E_PACKET_MAX_SIZE;
		}
		public int getWriteMaxLen() {
			return WORD_MC1E_PACKET_MAX_SIZE;
		}
		public long getAddress(long addr) {
			return addr;
		}
	}

}
