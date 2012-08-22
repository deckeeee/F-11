/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataDaySchedule.java,v 1.3.6.1 2005/07/06 02:20:44 frdm Exp $
 * $Revision: 1.3.6.1 $
 * $Date: 2005/07/06 02:20:44 $
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

package org.F11.scada.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * ���͈͂̃������G���A���w�肵��ONOFF�񐔂Ɠ����ŁA�Ǘ�����X�P�W���[���f�[�^�N���X�ł��B
 * �X�P�W���[���f�[�^���擾�E�ݒ肷��̂Ɏg�p���܂��B
 */
public final class WifeDataDaySchedule implements WifeData, Serializable {
	private static final long serialVersionUID = -1239012272356993819L;
	/** On/Off �̃^�C���e�[�u���ł� */
	private final OnOffTime[] timeTable;
	/** ���̃C���X�^���X�̃n�b�V���R�[�h�ł� */
	private transient volatile int hashCode;
	/** ���̃C���X�^���X�̃��[�h�T�C�Y�ł� */
	private transient volatile int wordSize;

	/**
	 * �v���C�x�[�g�E�R���X�g���N�^
	 * @param number On/Off �X�P�W���[���̉�
	 */
	private WifeDataDaySchedule(int number) {
		timeTable = new OnOffTime[number];
		Arrays.fill(timeTable, new OnOffTime(0, 0));
	}

	/**
	 * �v���C�x�[�g�R���X�g���N�^
	 * @param timeTable On/Off �̃^�C���e�[�u���̔z��
	 */
	private WifeDataDaySchedule(OnOffTime[] timeTable) {
		this.timeTable = timeTable;
	}

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getClass().getName());
		buffer.append("{");
		for (int i = 0; i < timeTable.length; i++) {
			buffer.append(timeTable[i].toString());
			if (i != (timeTable.length - 1)) {
			    buffer.append(" ");
			}
		}
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����������܂��B
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WifeDataDaySchedule)) {
			return false;
		}
		WifeDataDaySchedule sc = (WifeDataDaySchedule)obj;

		if (!Arrays.equals(this.timeTable, sc.timeTable))
			return false;
		return true;
	}

	/**
	 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			for (int i = 0; i < timeTable.length; i++) {
				result = 37 * result + timeTable[i].hashCode();
			}
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * �o�C�g�z����f�[�^�ɕϊ����܂��B
	 * @param b �o�C�g�z��
	 */
	public WifeData valueOf(byte[] b) {
		int byteSize = getWordSize() * 2;
		if (byteSize != b.length)
			throw new IllegalArgumentException("DataSize : " +  byteSize + "  Argument : " + b.length);

		OnOffTime[] timeTable =
			new OnOffTime[this.timeTable.length];

		ByteArrayInputStream is = new ByteArrayInputStream(b);
		for (int i = 0; i < timeTable.length; i++) {
			byte[] onOffByte = new byte[this.timeTable[i].getWordSize() * 2];
			is.read(onOffByte, 0, this.timeTable[i].getWordSize() * 2);
			timeTable[i] = this.timeTable[i].valueOf(onOffByte);
		}
		return new WifeDataDaySchedule(timeTable);
	}

	/**
	 * ���̃f�[�^�̒l���o�C�g�z��ϊ����ĕԂ��܂��B
	 * @return �o�C�g�z��
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream(getWordSize() * 2);

		for (int i = 0; i < timeTable.length; i++) {
			try {
				os.write(timeTable[i].toByteArray());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return os.toByteArray();
	}

	/**
	 * ���̃f�[�^�̃��[�h����Ԃ��܂��B
	 * @return ���̃f�[�^�̃��[�h��
	 */
	public int getWordSize() {
		if (wordSize == 0) {
			int size = 0;
			for (int i = timeTable.length - 1; i >= 0; i--) {
				size += timeTable[i].getWordSize();
			}
			wordSize = size;
		}
		return wordSize;
	}

	/**
	 * WifeDataDaySchedule �N���X�̃t�@�N�g�����\�b�h�ł��B
	 * ���������ꂽ�I�u�W�F�N�g��Ԃ��܂��B
	 * @param number On/Off �X�P�W���[���̉�
	 * @return WifeDataDaySchedule �̃C���X�^���X
	 */
	public static WifeDataDaySchedule valueOf(int number) {
		return new WifeDataDaySchedule(number);
	}

	/**
	 * �����Ŏw�肳�ꂽ�X�P�W���[���f�[�^��Ԃ��܂��B
	 */
	public int getOnTime(int number) {
		numberCheck(number);
		return timeTable[number].getOnTime();
	}

	/**
	 * �����Ŏw�肳�ꂽ�X�P�W���[���f�[�^��Ԃ��܂��B
	 */
	public int getOffTime(int number) {
		numberCheck(number);
		return timeTable[number].getOffTime();
	}

	/**
	 * �����Ŏw�肳�ꂽ�X�P�W���[���f�[�^��ݒ肵�� <code>WifeDataDaySchedule</code> ��Ԃ��܂��B
	 */
	public WifeDataDaySchedule setOnTime(int number, int time) {
		numberCheck(number);

		timeTable[number] =
			timeTable[number].setOnTime(time);
		return new WifeDataDaySchedule(this.timeTable);
	}

	/**
	 * �����Ŏw�肳�ꂽ�X�P�W���[���f�[�^��ݒ肵�� <code>WifeDataDaySchedule</code> ��Ԃ��܂��B
	 */
	public WifeDataDaySchedule setOffTime(int number, int time) {
		numberCheck(number);

		timeTable[number] =
			timeTable[number].setOffTime(time);
		return new WifeDataDaySchedule(this.timeTable);
	}

	/**
	 * �������`�F�b�N���܂��B
	 */
	private void numberCheck(int number) {
		if (number < 0 || number >= timeTable.length) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("number = ").append(number);
			buffer.append(" [numberMax = ").append(timeTable.length).append("]");
			throw new IllegalArgumentException(buffer.toString());
		}
	}

	/**
	 * �X�P�W���[���f�[�^�񐔂̑�����Ԃ��܂��B
	 */
	public int getNumberSize() {
		return timeTable.length;
	}	

	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new  WifeDataDaySchedule(timeTable);
	}
}
