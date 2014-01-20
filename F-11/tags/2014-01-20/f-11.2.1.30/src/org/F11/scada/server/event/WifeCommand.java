/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/event/WifeCommand.java,v 1.12.4.4 2006/03/20 07:48:37 frdm Exp $
 * $Revision: 1.12.4.4 $
 * $Date: 2006/03/20 07:48:37 $
 * 
 * =============================================================================
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

package org.F11.scada.server.event;

import java.util.Comparator;

import org.F11.scada.data.WifeData;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.WifeDataUtil;

/**
 * �ʐM�R�}���h��`�f�[�^��\���N���X�ł��B
 * @todo �萔�̂Ƃ���� enum �N���X�p�^�[���Ƀ��t�@�N�^�����O
 */
public final class WifeCommand {
	/**
	 * �\�[�g�p�R���p���[�^
	 */
	public static final Comparator comp = new Comparator() {
		public int compare(Object o1, Object o2) {
			WifeCommand wc1 = (WifeCommand) o1;
			WifeCommand wc2 = (WifeCommand) o2;
			if (wc1.deviceID.compareTo(wc2.deviceID) < 0)
				return -1;
			else if (wc1.deviceID.compareTo(wc2.deviceID) > 0)
				return 1;

			if (wc1.cycleTime < wc2.cycleTime)
				return -1;
			else if (wc1.cycleTime > wc2.cycleTime)
				return 1;

			if (wc1.cycleMode < wc2.cycleMode)
				return -1;
			else if (wc1.cycleMode > wc2.cycleMode)
				return 1;

			if (wc1.memoryMode < wc2.memoryMode)
				return -1;
			else if (wc1.memoryMode > wc2.memoryMode)
				return 1;

			if (wc1.memoryAddress < wc2.memoryAddress)
				return -1;
			else if (wc1.memoryAddress > wc2.memoryAddress)
				return 1;

			if (wc1.wordLength < wc2.wordLength)
				return -1;
			else if (wc1.wordLength > wc2.wordLength)
				return 1;

			return 0;
		}
	};

	/** ���삵�Ȃ��R�}���h��\���܂��B�Ǎ��݂����������Ȃ��R�}���h�Ƃ��ė��p���܂��B */
	private static final WifeCommand NULL_COMMAND =
		new WifeCommand("NullWifeCommand", 0, 0, 0, 0, 0);

	/** �ʐM�Ώۂ�PLCID�ł��B */
	private final String deviceID;
	/** �ʐM�����ł��B */
	private final int cycleTime;
	/** �펞�ʐM��ʂł��B */
	private final int cycleMode;
	/** ��������ʂł��B */
	private final int memoryMode;
	/** ����Ώۂ̃������A�h���X�ł��B */
	private final long memoryAddress;
	/** �ʐM�Ώۃf�[�^�̃��[�h���ł��B */
	private final int wordLength;

	/**
	 * �ʐM�R�}���h��`��\���I�u�W�F�N�g���쐬���܂��B
	 * @param deviceID �f�o�C�XID
	 * @param cycleTime �ʐM����
	 * @param cycleMode �펞�ʐM���
	 * @param memoryMode ���������
	 * @param memoryAddress ����Ώۂ̃������A�h���X
	 * @param wordLength �ʐM�Ώۃf�[�^�̃��[�h��
	 * @param writeData �����f�[�^
	 * @param accessTime �A�N�Z�X�^�C��(�Ō�ɒʐM��������)
	 */
	public WifeCommand(
		String deviceID,
		int cycleTime,
		int cycleMode,
		int memoryMode,
		long memoryAddress,
		int wordLength) {
		if (deviceID == null) {
			throw new IllegalArgumentException("deviceID is null.");
		}

		this.deviceID = deviceID;
		this.cycleTime = cycleTime;
		this.cycleMode = cycleMode;
		this.memoryMode = memoryMode;
		this.memoryAddress = memoryAddress;
		this.wordLength = wordLength;
	}

	public WifeCommand(Item item) {
	    if (item == null) {
	        throw new IllegalArgumentException("item is null.");
	    }
	    this.deviceID = item.getProvider();
	    this.cycleTime = item.getComCycle();
	    if (item.isComCycleMode()) {
	        this.cycleMode = 1;
	    } else {
	        this.cycleMode = 0;
	    }
	    this.memoryMode = item.getComMemoryKinds();
	    this.memoryAddress = item.getComMemoryAddress();
	    WifeData wd = WifeDataUtil.getWifeData(item);
	    this.wordLength = wd.getWordSize();
	}
	/**
	 * �f�o�C�XID��Ԃ��܂��B
	 */
	public String getDeviceID() {
		return deviceID;
	}

	/**
	 * �펞�ʐM��ʂ�Ԃ��܂��B
	 */
	public int getCycleMode() {
		return cycleMode;
	}

	/**
	 * �펞�ʐM������Ԃ��܂��B
	 */
	public int getCycleTime() {
		return cycleTime;
	}

	/**
	 * ��������ʂ�Ԃ��܂��B
	 */
	public int getMemoryMode() {
		return memoryMode;
	}

	/**
	 * ����Ώۂ̃������A�h���X��Ԃ��܂��B
	 */
	public long getMemoryAddress() {
		return memoryAddress;
	}

	/**
	 * �ʐM�Ώۃf�[�^�̃��[�h����Ԃ��܂��B
	 */
	public int getWordLength() {
		return wordLength;
	}

	/**
	 * �펞�ʐM���Ȃ�true��Ԃ��܂��B
	 */
	public boolean isCycleRead() {
		return cycleMode == 0;
	}

	/**
	 * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����������܂��B
	 * ��r�Ώۂ̃I�u�W�F�N�g�� WifeCommand �ŁA�l�������ꍇ�� true ��Ԃ��܂��B
	 * 
	 * @param obj ��r�ΏۃI�u�W�F�N�g
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WifeCommand)) {
			return false;
		}
		WifeCommand wc = (WifeCommand) obj;

		return deviceID.equals(wc.deviceID)
			&& cycleTime == wc.cycleTime
			&& cycleMode == wc.cycleMode
			&& memoryMode == wc.memoryMode
			&& memoryAddress == wc.memoryAddress
			&& wordLength == wc.wordLength;

	}

	/**
	 * ���� WifeCommand �̃n�b�V���R�[�h��Ԃ��܂��B
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + deviceID.hashCode();
		result = 37 * result + cycleTime;
		result = 37 * result + cycleMode;
		result = 37 * result + memoryMode;
		result = 37 * result + (int) (memoryAddress ^ (memoryAddress >>> 32));
		result = 37 * result + wordLength;

		return result;
	}

	/**
	 * �I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();

		s.append("{DeviceID=").append(deviceID).append(", ");
		s.append("cycleMode=").append(cycleMode).append(", ");
		s.append("memoryMode=").append(memoryMode).append(", ");
		s.append("memoryAddress=").append(memoryAddress).append(", ");
		s.append("wordLength=").append(wordLength).append("}");
		return s.toString();
	}

	/**
	 * ���삵�Ȃ��R�}���h��Ԃ��܂��B�Ǎ��݂����������Ȃ��R�}���h�Ƃ��ė��p���܂��B
	 * @return ���삵�Ȃ��R�}���h(NULL_COMMAND)
	 */
	public static WifeCommand getNullCommand() {
		return NULL_COMMAND;
	}

	/**
	 * �������A�h���X�ƃ��[�h�����w�肵�āA�V����WifeCommand�I�u�W�F�N�g���쐬���܂��B
	 * @param memoryAddress �������A�h���X
	 * @param wordLength ���[�h��
	 * @return �V�����I�u�W�F�N�g
	 */
	public WifeCommand createCommand(long memoryAddress, int wordLength) {
		return new WifeCommand(
			this.deviceID,
			this.cycleTime,
			this.cycleMode,
			this.memoryMode,
			memoryAddress,
			wordLength);
	}
}
