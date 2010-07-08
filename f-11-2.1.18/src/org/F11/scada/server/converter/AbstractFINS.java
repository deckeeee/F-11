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
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.F11.scada.WifeException;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.event.WifeCommand;

/**
 * Wife�ʐM���W���[����FINS�R�}���h�����̊��N���X�B
 */
public abstract class AbstractFINS implements Converter {
	/** FINS�R�}���h�w�b�_�� */
	private static final int FINS_COMMAND_HEADER_LENGTH = 14;

	/** ��������� DM�̃f�[�^��������\���܂��B */
	public static final Integer DM_AREA = new Integer(0);
	/** ��������� IO�̃����[�G���A��\���܂��B */
	public static final Integer CIO_AREA = new Integer(1);
	/** ��������� HR�̃����[�G���A��\���܂��B */
	public static final Integer HR_AREA = new Integer(2);
	/** ��������� WR�̃����[�G���A��\���܂��B */
	public static final Integer WR_AREA = new Integer(3);
	/** ��������� �̊g���������o���N 0 �` C ��\���܂��B */
	public static final Integer EM0_AREA = new Integer(10);
	public static final Integer EM1_AREA = new Integer(11);
	public static final Integer EM2_AREA = new Integer(12);
	public static final Integer EM3_AREA = new Integer(13);
	public static final Integer EM4_AREA = new Integer(14);
	public static final Integer EM5_AREA = new Integer(15);
	public static final Integer EM6_AREA = new Integer(16);
	public static final Integer EM7_AREA = new Integer(17);
	public static final Integer EM8_AREA = new Integer(18);
	public static final Integer EM9_AREA = new Integer(19);
	public static final Integer EMA_AREA = new Integer(20);
	public static final Integer EMB_AREA = new Integer(21);
	public static final Integer EMC_AREA = new Integer(22);
	/** ���z��������� PLC�X�e�[�^�X��\���܂��B */
	public static final Integer PLCST_AREA = new Integer(90);
	/** ���z��������� PLC���v��\���܂��B */
	public static final Integer PLCTM_AREA = new Integer(91);

	/** ��������ʂ̃}�b�v */
	private static final Map<Integer, Byte> memoryModeMap;
	static {
		memoryModeMap = new HashMap<Integer, Byte>();
		memoryModeMap.put(DM_AREA, new Byte((byte) 0x82));
		memoryModeMap.put(CIO_AREA, new Byte((byte) 0xB0));
		memoryModeMap.put(HR_AREA, new Byte((byte) 0xB2));
		memoryModeMap.put(WR_AREA, new Byte((byte) 0xB1));
		memoryModeMap.put(EM0_AREA, new Byte((byte) 0xA0));
		memoryModeMap.put(EM1_AREA, new Byte((byte) 0xA1));
		memoryModeMap.put(EM2_AREA, new Byte((byte) 0xA2));
		memoryModeMap.put(EM3_AREA, new Byte((byte) 0xA3));
		memoryModeMap.put(EM4_AREA, new Byte((byte) 0xA4));
		memoryModeMap.put(EM5_AREA, new Byte((byte) 0xA5));
		memoryModeMap.put(EM6_AREA, new Byte((byte) 0xA6));
		memoryModeMap.put(EM7_AREA, new Byte((byte) 0xA7));
		memoryModeMap.put(EM8_AREA, new Byte((byte) 0xA8));
		memoryModeMap.put(EM9_AREA, new Byte((byte) 0xA9));
		memoryModeMap.put(EMA_AREA, new Byte((byte) 0xAA));
		memoryModeMap.put(EMB_AREA, new Byte((byte) 0xAB));
		memoryModeMap.put(EMC_AREA, new Byte((byte) 0xAC));
	}
	/** FINS�ʐM�V�[�P���V����ID */
	protected byte sid;

	/** FINS�w�b�_�f�[�^ */
	private byte[] head =
		{
			(byte) 0x80,
			(byte) 0x00,
			(byte) 0x02,
			(byte) 0x00,
			(byte) 0x00,
			(byte) 0x00,
			(byte) 0x00,
			(byte) 0x00,
			(byte) 0x00 };

	/**
	 * �W����FINS�R�}���h��M�p�P�b�g�ő�T�C�Y��Ԃ��܂��B
	 *
	 * @return �W����FINS�R�}���h��M�p�P�b�g�ő�T�C�Y��Ԃ��܂��B
	 */
	protected abstract int getReceiveSize();

	/**
	 * �W����FINS�R�}���h���M�p�P�b�g�ő�T�C�Y��Ԃ��܂��B
	 *
	 * @return �W����FINS�R�}���h���M�p�P�b�g�ő�T�C�Y��Ԃ��܂��B
	 */
	protected abstract int getSendSize();

	/** ����ݒ肵�A���X�|���X�w�b�_��Ԃ��B */
	public byte[] setEnvironment(Environment device) {
		head[3] = (byte) device.getPlcNetNo();
		head[4] = (byte) device.getPlcNodeNo();
		head[5] = (byte) device.getPlcUnitNo();
		head[6] = (byte) device.getHostNetNo();
		head[7] = (byte) device.getHostAddress();

		byte[] resp = new byte[head.length];
		resp[0] = (byte) 0xc0;
		resp[1] = (byte) 0x00;
		resp[2] = (byte) 0x02;
		resp[3] = head[6];
		resp[4] = head[7];
		resp[5] = head[8];
		resp[6] = head[3];
		resp[7] = head[4];
		resp[8] = head[5];
		return resp;
	}

	/** �Ǎ��݃R�}���h��ݒ肷��B */
	public void setReadCommand(WifeCommand commdef) throws WifeException {
		if (PLCST_AREA.intValue() == commdef.getMemoryMode()) {
			// PLC�X�e�[�^�X�Ǎ��݃R�}���h
			finsCommand = STATREAD_FINSCOMMAND;
		} else if (PLCTM_AREA.intValue() == commdef.getMemoryMode()) {
			// ���v�Ǎ��݃R�}���h
			finsCommand = TIMEREAD_FINSCOMMAND;
		} else {
			// �Ǎ��݃R�}���h
			finsCommand = READ_FINSCOMMAND;
		}
		finsCommand.setCommand(commdef, null);
	}

	/** �����݃R�}���h��ݒ肷��B */
	public void setWriteCommand(WifeCommand commdef, byte[] data)
		throws WifeException {
		if (PLCTM_AREA.intValue() == commdef.getMemoryMode()) {
			// ���v�����݃R�}���h
			finsCommand = TIMEWRITE_FINSCOMMAND;
		} else {
			// �����݃R�}���h
			finsCommand = WRITE_FINSCOMMAND;
		}
		finsCommand.setCommand(commdef, data);
	}

	/** �R�}���h���擾�\���H */
	public boolean hasCommand() {
		return finsCommand.hasCommand();
	}

	/** �R�}���h���쐬���A����̃R�}���h���������܂��B */
	public void nextCommand(ByteBuffer sendBuffer) {
		sendBuffer.put(head);
		incrementSid();
		sendBuffer.put(sid);
		finsCommand.nextCommand(sendBuffer);
	}

	/** �O����s�R�}���h���쐬���܂��B */
	public void retryCommand(ByteBuffer sendBuffer) {
		sendBuffer.put(head);
		sendBuffer.put(sid);
		finsCommand.retryCommand(sendBuffer);
	}

	/** ���M�f�[�^�Ǝ�M�f�[�^�̐��������������܂��B */
	public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
		byte[] err = { 0, 0 };
		if (recvBuffer.remaining() < FINS_COMMAND_HEADER_LENGTH) {
			StringBuffer sb = new StringBuffer();
			sb.append("RecvData (");
			sb.append(WifeUtilities.toString(recvBuffer));
			sb.append(") is short!");
			return new WifeException(WifeException.WIFE_ERROR,
					WifeException.WIFE_NET_RESPONCE_ERROR, sb.toString());
		}
		// SID����
		if (recvBuffer.get(9) != sid) {
			StringBuffer sb = new StringBuffer();
			sb.append("Head error: send ");
			sb.append(WifeUtilities.toString(head));
			byte[] s = new byte[1];
			s[0] = sid;
			sb.append(WifeUtilities.toString(s));
			sb.append(" recv ").append(
					WifeUtilities.toString(recvBuffer, 0, 10));
			return new WifeException(WifeException.WIFE_ERROR,
					WifeException.WIFE_NET_RESPONCE_HEAD_ERROR, sb.toString());
		}
		// �R�}���h�R�[�h
		WifeException ec = finsCommand.checkCommandResponce(recvBuffer);
		if (ec != null) {
			return ec;
		}
		// �I���R�[�h
		if (recvBuffer.get(12) != (byte) 0x00
			|| (recvBuffer.get(13) & 0x3f) != 0x00) {
			err[0] = recvBuffer.get(12);
			err[1] = recvBuffer.get(13);
			StringBuffer sb = new StringBuffer();
			sb.append("End code error: ");
			sb.append(WifeUtilities.toString(err));
			return new WifeException(WifeException.WIFE_ERROR,
					WifeException.WIFE_NET_RESPONCE_ENDCODE_ERROR, err, sb
							.toString());
		}
		// ���X�|���X�f�[�^��
		if (finsCommand.getResponceLength() != recvBuffer.remaining()) {
			StringBuffer sb = new StringBuffer();
			sb.append("Expected length ").append(
					finsCommand.getResponceLength());
			sb.append(" != RecvData length ").append(recvBuffer.remaining());
			return new WifeException(WifeException.WIFE_ERROR,
					WifeException.WIFE_NET_RESPONCE_ERROR, sb.toString());
		}
		return null;
	}

	/** ��M�f�[�^����f�[�^�����擾���܂��B */
	public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
		finsCommand.getResponceData(recvBuffer, recvData);
	}

	/** �ʐM���̍ő咷��Ԃ��܂��B */
	public int getPacketMaxSize(WifeCommand commdef) {
		return getReceiveSize() / 2;
	}

	/**
	 * FINS�ʐM�V�[�P���V����ID�X�V
	 */
	private void incrementSid() {
		sid++;
		if (127 < sid)
			sid = 0;
	}

	/**
	 * ������\����Ԃ��܂��B
	 */
	public String toString() {
		return finsCommand.toString();
	}

	/**
	 * �R�}���h�ϊ��w���p�[�N���X�̃C���^�[�t�F�C�X�ł��B
	 */
	private interface FinsCommand {
		/** ��������R�}���h��ݒ肷��B */
		public void setCommand(WifeCommand commdef, byte[] data)
			throws WifeException;

		/** �R�}���h���擾�\���H */
		public boolean hasCommand();

		/** �R�}���h���쐬���A����̃R�}���h���������܂��B */
		public void nextCommand(ByteBuffer sendBuffer);

		/** �O����s�R�}���h���쐬���܂��B */
		public void retryCommand(ByteBuffer sendBuffer);

		/** �R�}���h���烌�X�|���X�f�[�^�̃o�C�g�����擾���܂��B */
		public int getResponceLength();

		/** ���M�f�[�^�Ǝ�M�f�[�^�̐��������������܂��B */
		public WifeException checkCommandResponce(ByteBuffer recvBuffer);

		/** ��M�f�[�^����f�[�^�����擾���܂��B */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData);
	}

	/**
	 * �Ǎ��݃R�}���h�ϊ��w���p�[�N���X�̃C���X�^���X�ł��B
	 */
	private final FinsCommand READ_FINSCOMMAND = new FinsCommand() {
		/** PLC��������� */
		private byte memoryMode;
		/** PLC�������A�h���X */
		private long memoryAddress;
		/** �������ׂ�PLC�f�[�^�̎c��o�C�g�� */
		private int restByteLength;
		/** �����PLC�f�[�^�o�C�g�� */
		private int thisByteLength;

		/** ��������R�}���h��ݒ肷��B */
		public void setCommand(WifeCommand commdef, byte[] data)
			throws WifeException {
			Byte mm =
				(Byte) memoryModeMap.get(new Integer(commdef.getMemoryMode()));
			if (mm == null) {
				throw new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_COMMAND_ERROR,
						"Not supported memory mode " + commdef.getMemoryMode());
			}
			this.memoryMode = mm.byteValue();

			this.memoryAddress = commdef.getMemoryAddress();
			this.restByteLength = commdef.getWordLength() * 2;
			this.thisByteLength = 0;
		}

		/** �R�}���h���擾�\���H */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}

		/** �R�}���h���쐬���A����̃R�}���h���������܂��B */
		public void nextCommand(ByteBuffer sendBuffer) {
			// �f�o�C�X�Ɉˑ����钷���𒴂��Ă����
			if (getReceiveSize() < restByteLength) {
				thisByteLength = getReceiveSize();
				restByteLength -= getReceiveSize();
			} else {
				thisByteLength = restByteLength;
				restByteLength = 0;
			}

			// PLC�R�}���h���
			sendBuffer.put((byte) 0x01);
			sendBuffer.put((byte) 0x01);
			// �ʏ�R�}���h
			// PLC���������
			sendBuffer.put(memoryMode);
			// �������A�h���X�A����
			sendBuffer.put((byte) (memoryAddress / 0x100));
			sendBuffer.put((byte) (memoryAddress % 0x100));
			sendBuffer.put((byte) 0x00); // �r�b�g�w��A���[�h�Ǎ��̏ꍇ�͏��0
			sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			// ����̃A�h���X
			memoryAddress += (thisByteLength / 2);
		}

		/** �O����s�R�}���h���쐬���܂��B */
		public void retryCommand(ByteBuffer sendBuffer) {
			long addr = memoryAddress - (thisByteLength / 2);

			// PLC�R�}���h���
			sendBuffer.put((byte) 0x01);
			sendBuffer.put((byte) 0x01);
			// �ʏ�R�}���h
			// PLC���������
			sendBuffer.put(memoryMode);
			// �������A�h���X�A����
			sendBuffer.put((byte) (addr / 0x100));
			sendBuffer.put((byte) (addr % 0x100));
			sendBuffer.put((byte) 0x00); // �r�b�g�w��A���[�h�Ǎ��̏ꍇ�͏��0
			sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
		}

		/** �R�}���h���烌�X�|���X�f�[�^�̃o�C�g�����擾���܂��B */
		public int getResponceLength() {
			return FINS_COMMAND_HEADER_LENGTH + thisByteLength;
		}

		/** ���M�f�[�^�Ǝ�M�f�[�^�̐��������������܂��B */
		public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
			// �R�}���h�R�[�h
			if ((byte) 0x01 != recvBuffer.get(10)
				|| (byte) 0x01 != recvBuffer.get(11)) {
				StringBuffer sb = new StringBuffer();
				sb.append("Command error: send 0101 recv ");
				sb.append(WifeUtilities.toString(recvBuffer, 10, 2));
				return new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb
								.toString());
			}
			return null;
		}

		/** ��M�f�[�^����f�[�^�����擾���܂��B */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
			recvBuffer.position(FINS_COMMAND_HEADER_LENGTH);
			recvData.put(recvBuffer);
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
	 * PLC�X�e�[�^�X�Ǎ��݃R�}���h�ϊ��w���p�[�N���X�̃C���X�^���X�ł��B
	 */
	private final FinsCommand STATREAD_FINSCOMMAND = new FinsCommand() {
		/** PLC�������A�h���X */
		private long memoryAddress;
		/** �������ׂ�PLC�f�[�^�̎c��o�C�g�� */
		private int restByteLength;
		/** �����PLC�f�[�^�o�C�g�� */
		private int thisByteLength;

		/** �v�����ꂽ�f�[�^�o�C�g�� */
		// private int rreqByteLength;

		/** ��������R�}���h�̃l�^��ݒ肷��B */
		public void setCommand(WifeCommand commdef, byte[] data) {
			this.memoryAddress = commdef.getMemoryAddress();
			this.restByteLength = 13 * 2;
			// this.rreqByteLength = commdef.getWordLength() * 2;
			this.thisByteLength = 0;
		}

		/** �R�}���h���擾�\���H */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}

		/** �R�}���h���쐬���A����̃R�}���h���������܂��B */
		public void nextCommand(ByteBuffer sendBuffer) {
			// �f�o�C�X�Ɉˑ����钷���𒴂��Ă����
			if (getReceiveSize() < restByteLength) {
				thisByteLength = getReceiveSize();
				restByteLength -= getReceiveSize();
			} else {
				thisByteLength = restByteLength;
				restByteLength = 0;
			}

			// PLC�R�}���h���
			sendBuffer.put((byte) 0x06);
			sendBuffer.put((byte) 0x01);
		}

		/** �O����s�R�}���h���쐬���܂��B */
		public void retryCommand(ByteBuffer sendBuffer) {
			// PLC�R�}���h���
			sendBuffer.put((byte) 0x06);
			sendBuffer.put((byte) 0x01);
		}

		/** �R�}���h���烌�X�|���X�f�[�^�̃o�C�g�����擾���܂��B */
		public int getResponceLength() {
			return FINS_COMMAND_HEADER_LENGTH + thisByteLength;
		}

		/** ���M�f�[�^�Ǝ�M�f�[�^�̐��������������܂��B */
		public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
			// �R�}���h�R�[�h
			if ((byte) 0x06 != recvBuffer.get(10)
				|| (byte) 0x01 != recvBuffer.get(11)) {
				StringBuffer sb = new StringBuffer();
				sb.append("Command error: send 0601 recv ");
				sb.append(WifeUtilities.toString(recvBuffer, 10, 2));
				return new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb
								.toString());
			}
			return null;
		}

		/** ��M�f�[�^����f�[�^�����擾���܂��B */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
			recvBuffer.position(FINS_COMMAND_HEADER_LENGTH
				+ ((int) memoryAddress * 2));
			recvData.put(recvBuffer);
		}

		/**
		 * ���̃C���X�^���X�̕�����\����Ԃ��܂��B
		 */
		public String toString() {
			StringBuffer s = new StringBuffer();
			s.append("StatusReadMode:").append("\n");
			s.append("memoryAddress:").append(memoryAddress).append("\n");
			s.append("restByteLength:").append(restByteLength).append("\n");
			s.append("thisByteLength:").append(thisByteLength).append("\n");
			return s.toString();
		}
	};

	/**
	 * �����݃R�}���h�ϊ��w���p�[�N���X�̃C���X�^���X�ł��B
	 */
	private final FinsCommand WRITE_FINSCOMMAND = new FinsCommand() {
		/** PLC��������� */
		private byte memoryMode;
		/** PLC�������A�h���X */
		private long memoryAddress;
		/** �����݃f�[�^ */
		private byte[] writeData;
		/** �����݃f�[�^�ʒu */
		private int writePos;
		/** �������ׂ�PLC�f�[�^�̎c��o�C�g�� */
		private int restByteLength;
		/** �����PLC�f�[�^�o�C�g�� */
		private int thisByteLength;

		/** ��������R�}���h�̃l�^��ݒ肷��B */
		public void setCommand(WifeCommand commdef, byte[] data)
			throws WifeException {
			Byte mm =
				(Byte) memoryModeMap.get(new Integer(commdef.getMemoryMode()));
			if (mm == null) {
				throw new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_COMMAND_ERROR,
						"Not supported memory mode " + commdef.getMemoryMode());
			}
			this.memoryMode = mm.byteValue();

			this.memoryAddress = commdef.getMemoryAddress();
			this.writeData = data;
			this.writePos = 0;
			this.restByteLength = commdef.getWordLength() * 2;
			this.thisByteLength = 0;
		}

		/** �R�}���h���擾�\���H */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}

		/** �R�}���h���쐬���A����̃R�}���h���������܂��B */
		public void nextCommand(ByteBuffer sendBuffer) {
			// �f�o�C�X�Ɉˑ����钷���𒴂��Ă����
			if (getSendSize() < restByteLength) {
				thisByteLength = getSendSize();
				restByteLength -= getSendSize();
			} else {
				thisByteLength = restByteLength;
				restByteLength = 0;
			}

			// PLC�R�}���h���
			sendBuffer.put((byte) 0x01);
			sendBuffer.put((byte) 0x02);
			// PLC���������
			sendBuffer.put(memoryMode);
			// �������A�h���X�A����
			sendBuffer.put((byte) (memoryAddress / 0x100));
			sendBuffer.put((byte) (memoryAddress % 0x100));
			sendBuffer.put((byte) 0x00); // �r�b�g�w��A���[�h�����̏ꍇ�͏��0
			sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			// �����݃f�[�^
			sendBuffer.put(writeData, writePos, thisByteLength);

			// ����̃A�h���X
			memoryAddress += (thisByteLength / 2);
			writePos += thisByteLength;
		}

		/** �O����s�R�}���h���쐬���܂��B */
		public void retryCommand(ByteBuffer sendBuffer) {
			long addr = memoryAddress - (thisByteLength / 2);
			int pos = writePos - thisByteLength;

			// PLC�R�}���h���
			sendBuffer.put((byte) 0x01);
			sendBuffer.put((byte) 0x02);
			// PLC���������
			sendBuffer.put(memoryMode);
			// �������A�h���X�A����
			sendBuffer.put((byte) (addr / 0x100));
			sendBuffer.put((byte) (addr % 0x100));
			sendBuffer.put((byte) 0x00); // �r�b�g�w��A���[�h�����̏ꍇ�͏��0
			sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			// �����݃f�[�^
			sendBuffer.put(writeData, pos, thisByteLength);
		}

		/** �R�}���h���烌�X�|���X�f�[�^�̃o�C�g�����擾���܂��B */
		public int getResponceLength() {
			return FINS_COMMAND_HEADER_LENGTH;
		}

		/** ���M�f�[�^�Ǝ�M�f�[�^�̐��������������܂��B */
		public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
			// �R�}���h�R�[�h
			if ((byte) 0x01 != recvBuffer.get(10)
				|| (byte) 0x02 != recvBuffer.get(11)) {
				StringBuffer sb = new StringBuffer();
				sb.append("Command error: send 0102 recv ");
				sb.append(WifeUtilities.toString(recvBuffer, 10, 2));
				return new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb
								.toString());
			}
			return null;
		}

		/** ��M�f�[�^����f�[�^�����擾���܂��B */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
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

	/**
	 * ���v�Ǎ��݃R�}���h�ϊ��w���p�[�N���X�̃C���X�^���X�ł��B
	 */
	private final FinsCommand TIMEREAD_FINSCOMMAND = new FinsCommand() {
		/** �������ׂ�PLC�f�[�^�̎c��o�C�g�� */
		private int restByteLength;
		/** �����PLC�f�[�^�o�C�g�� */
		private int thisByteLength;

		/** ��������R�}���h�̃l�^��ݒ肷��B */
		public void setCommand(WifeCommand commdef, byte[] data) {
			this.restByteLength = 7;
			this.thisByteLength = 0;
		}

		/** �R�}���h���擾�\���H */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}

		/** �R�}���h���쐬���A����̃R�}���h���������܂��B */
		public void nextCommand(ByteBuffer sendBuffer) {
			thisByteLength = restByteLength;
			restByteLength = 0;

			// PLC�R�}���h���
			sendBuffer.put((byte) 0x07);
			sendBuffer.put((byte) 0x01);
		}

		/** �O����s�R�}���h���쐬���܂��B */
		public void retryCommand(ByteBuffer sendBuffer) {
			// PLC�R�}���h���
			sendBuffer.put((byte) 0x07);
			sendBuffer.put((byte) 0x01);
		}

		/** �R�}���h���烌�X�|���X�f�[�^�̃o�C�g�����擾���܂��B */
		public int getResponceLength() {
			return FINS_COMMAND_HEADER_LENGTH + thisByteLength;
		}

		/** ���M�f�[�^�Ǝ�M�f�[�^�̐��������������܂��B */
		public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
			// �R�}���h�R�[�h
			if ((byte) 0x07 != recvBuffer.get(10)
				|| (byte) 0x01 != recvBuffer.get(11)) {
				StringBuffer sb = new StringBuffer();
				sb.append("Command error: send 0701 recv ");
				sb.append(WifeUtilities.toString(recvBuffer, 10, 2));
				return new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb
								.toString());
			}
			return null;
		}

		/** ��M�f�[�^����f�[�^�����擾���܂��B */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
			byte[] data = new byte[8 * 2];
			Arrays.fill(data, (byte) 0);
			Calendar cal = new GregorianCalendar();
			cal.setTimeInMillis(System.currentTimeMillis());
			int yh = cal.get(Calendar.YEAR) / 100;
			recvData.put((byte) ((yh / 10) * 16 + (yh % 10)));
			recvData.put(recvBuffer.get(14)); // �N
			recvData.put((byte) 0x00);
			recvData.put(recvBuffer.get(15)); // ��
			recvData.put((byte) 0x00);
			recvData.put(recvBuffer.get(16)); // ��
			recvData.put((byte) 0x00);
			recvData.put(recvBuffer.get(20)); // �j��
			recvData.put((byte) 0x00);
			recvData.put(recvBuffer.get(17)); // ��
			recvData.put((byte) 0x00);
			recvData.put(recvBuffer.get(18)); // ��
			recvData.put((byte) 0x00);
			recvData.put(recvBuffer.get(19)); // �b
			recvData.put((byte) 0x00);
			recvData.put((byte) 0x01); // �t���O
		}

		/**
		 * ���̃C���X�^���X�̕�����\����Ԃ��܂��B
		 */
		public String toString() {
			StringBuffer s = new StringBuffer();
			s.append("TimeReadMode:").append("\n");
			s.append("restByteLength:").append(restByteLength).append("\n");
			s.append("thisByteLength:").append(thisByteLength).append("\n");
			return s.toString();
		}
	};

	/**
	 * ���v�����݃R�}���h�ϊ��w���p�[�N���X�̃C���X�^���X�ł��B
	 */
	private final FinsCommand TIMEWRITE_FINSCOMMAND = new FinsCommand() {
		/** �����݃f�[�^ */
		private byte[] writeData;
		/** �������ׂ�PLC�f�[�^�̎c��o�C�g�� */
		private int restByteLength;
		/** �����PLC�f�[�^�o�C�g�� */
		private int thisByteLength;

		/** ��������R�}���h�̃l�^��ݒ肷��B */
		public void setCommand(WifeCommand commdef, byte[] data) {
			this.writeData = data;
			this.restByteLength = 7;
			this.thisByteLength = 0;
		}

		/** �R�}���h���擾�\���H */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}

		/** �R�}���h���쐬���A����̃R�}���h���������܂��B */
		public void nextCommand(ByteBuffer sendBuffer) {
			thisByteLength = restByteLength;
			restByteLength = 0;

			// PLC�R�}���h���
			sendBuffer.put((byte) 0x07);
			sendBuffer.put((byte) 0x02);
			// �����݃f�[�^
			sendBuffer.put(writeData[1]); // �N
			sendBuffer.put(writeData[3]); // ��
			sendBuffer.put(writeData[5]); // ��
			sendBuffer.put(writeData[9]); // ��
			sendBuffer.put(writeData[11]); // ��
			sendBuffer.put(writeData[13]); // �b
			sendBuffer.put(writeData[7]); // �j��
		}

		/** �O����s�R�}���h���쐬���܂��B */
		public void retryCommand(ByteBuffer sendBuffer) {

			// PLC�R�}���h���
			sendBuffer.put((byte) 0x07);
			sendBuffer.put((byte) 0x02);
			// �����݃f�[�^
			sendBuffer.put(writeData[1]); // �N
			sendBuffer.put(writeData[3]); // ��
			sendBuffer.put(writeData[5]); // ��
			sendBuffer.put(writeData[9]); // ��
			sendBuffer.put(writeData[11]); // ��
			sendBuffer.put(writeData[13]); // �b
			sendBuffer.put(writeData[7]); // �j��
		}

		/** �R�}���h���烌�X�|���X�f�[�^�̃o�C�g�����擾���܂��B */
		public int getResponceLength() {
			return FINS_COMMAND_HEADER_LENGTH;
		}

		/** ���M�f�[�^�Ǝ�M�f�[�^�̐��������������܂��B */
		public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
			// �R�}���h�R�[�h
			if ((byte) 0x07 != recvBuffer.get(10)
				|| (byte) 0x02 != recvBuffer.get(11)) {
				StringBuffer sb = new StringBuffer();
				sb.append("Command error: send 0702 recv ");
				sb.append(WifeUtilities.toString(recvBuffer, 10, 2));
				return new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb
								.toString());
			}
			return null;
		}

		/** ��M�f�[�^����f�[�^�����擾���܂��B */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
		}

		/**
		 * ���̃C���X�^���X�̕�����\����Ԃ��܂��B
		 */
		public String toString() {
			StringBuffer s = new StringBuffer();
			s.append("TimeWriteMode:").append("\n");
			s.append("restByteLength:").append(restByteLength).append("\n");
			s.append("thisByteLength:").append(thisByteLength).append("\n");
			return s.toString();
		}
	};

	/** �R�}���h�ϊ��w���p�[�N���X */
	private FinsCommand finsCommand = READ_FINSCOMMAND;
}
