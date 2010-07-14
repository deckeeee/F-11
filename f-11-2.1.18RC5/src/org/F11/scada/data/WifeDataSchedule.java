/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataSchedule.java,v 1.4.2.6 2007/03/19 01:12:19 frdm Exp $
 * $Revision: 1.4.2.6 $
 * $Date: 2007/03/19 01:12:19 $
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

import org.F11.scada.Globals;

/**
 * ���͈͂̃������G���A���w�肵��ONOFF�񐔂Ɠ����ŁA�Ǘ�����X�P�W���[���f�[�^�N���X�ł��B �X�P�W���[���f�[�^���擾�E�ݒ肷��̂Ɏg�p���܂��B
 * ���̃N���X�͕s�σN���X�ł��B
 */
public final class WifeDataSchedule implements WifeData, Serializable {
	private static final long serialVersionUID = 9131723169279672709L;
	/** �X�P�W���[�����ڃp�^�[���ł� */
	private final SchedulePattern pattern;
	/** On/Off �̃^�C���e�[�u���ł� */
	private final OnOffTime[][] timeTable;
	/** ���̃C���X�^���X�̃n�b�V���R�[�h�ł� */
	private transient volatile int hashCode;
	/** ���̃C���X�^���X�̃��[�h�T�C�Y�ł� */
	private transient volatile int wordSize;
	/** �O���[�v���� */
	private final String groupName;

	/**
	 * �v���C�x�[�g�E�R���X�g���N�^
	 * 
	 * @param pattern �X�P�W���[���p�^�[��
	 * @param number On/Off �X�P�W���[���̉�
	 * @param groupName �O���[�v����
	 */
	private WifeDataSchedule(
			SchedulePattern pattern,
			int number,
			String groupName) {
		this.pattern = pattern;
		timeTable = new OnOffTime[this.pattern.size()][number];
		for (int i = timeTable.length - 1; i >= 0; i--) {
			Arrays.fill(timeTable[i], new OnOffTime(0, 0));
		}
		if (groupName != null) {
			this.groupName = groupName;
		} else {
			this.groupName = Globals.NULL_STRING;
		}
	}

	/**
	 * �v���C�x�[�g�R���X�g���N�^
	 * 
	 * @param pattern �X�P�W���[���p�^�[��
	 * @param timeTable On/Off �̃^�C���e�[�u���̔z��
	 * @param groupName �O���[�v����
	 */
	private WifeDataSchedule(
			SchedulePattern pattern,
			OnOffTime[][] timeTable,
			String groupName) {
		this.pattern = pattern;
		this.timeTable = new OnOffTime[timeTable.length][timeTable[0].length];
		for (int i = 0; i < timeTable.length; i++) {
			for (int j = 0; j < timeTable[i].length; j++) {
				this.timeTable[i][j] = timeTable[i][j];
			}
		}
		if (groupName != null) {
			this.groupName = groupName;
		} else {
			this.groupName = Globals.NULL_STRING;
		}
	}

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getClass().getName());
		buffer.append("[patten=" + pattern.toString() + ",\ntimeTable:\n");
		for (int i = 0; i < timeTable.length; i++) {
			for (int j = 0; j < timeTable[i].length; j++) {
				buffer.append(timeTable[i][j].toString() + " ");
			}
			buffer.append("\n");
		}
		buffer.append(", groupName=").append(groupName);
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����������܂��B
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WifeDataSchedule)) {
			return false;
		}
		WifeDataSchedule sc = (WifeDataSchedule) obj;

		if (!this.pattern.equals(sc.pattern))
			return false;

		for (int i = 0; i < timeTable.length; i++) {
			if (!Arrays.equals(this.timeTable[i], sc.timeTable[i]))
				return false;
		}

		if (!this.groupName.equals(sc.groupName)) {
			return false;
		}

		return true;
	}

	/**
	 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			result = 37 * result + pattern.hashCode();
			for (int i = 0; i < timeTable.length; i++) {
				for (int j = 0; j < timeTable[i].length; j++) {
					result = 37 * result + timeTable[i][j].hashCode();
				}
			}
			result = 37 * result + groupName.hashCode();
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * �o�C�g�z����f�[�^�ɕϊ����܂��B
	 * 
	 * @param b �o�C�g�z��
	 */
	public WifeData valueOf(byte[] b) {
		int byteSize = getWordSize() * 2;
		if (byteSize != b.length)
			throw new IllegalArgumentException("DataSize : " + byteSize
					+ "  Argument : " + b.length);

		OnOffTime[][] timeTable = new OnOffTime[this.timeTable.length][this.timeTable[0].length];

		ByteArrayInputStream is = new ByteArrayInputStream(b);
		for (int i = 0; i < timeTable.length; i++) {
			for (int j = 0; j < timeTable[i].length; j++) {
				byte[] onOffByte = new byte[this.timeTable[i][j].getWordSize() * 2];
				is.read(onOffByte, 0, this.timeTable[i][j].getWordSize() * 2);
				timeTable[i][j] = this.timeTable[i][j].valueOf(onOffByte);
			}
		}
		return new WifeDataSchedule(this.pattern, timeTable, this.groupName);
	}

	/**
	 * ���̃f�[�^�̒l���o�C�g�z��ϊ����ĕԂ��܂��B
	 * 
	 * @return �o�C�g�z��
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream(getWordSize() * 2);

		for (int i = 0; i < timeTable.length; i++) {
			for (int j = 0; j < timeTable[i].length; j++) {
				try {
					os.write(timeTable[i][j].toByteArray());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return os.toByteArray();
	}

	/**
	 * ���̃f�[�^�̃��[�h����Ԃ��܂��B
	 * 
	 * @return ���̃f�[�^�̃��[�h��
	 */
	public int getWordSize() {
		if (wordSize == 0) {
			int size = 0;
			for (int i = timeTable.length - 1; i >= 0; i--) {
				for (int j = timeTable[i].length - 1; j >= 0; j--) {
					size += timeTable[i][j].getWordSize();
				}
			}
			wordSize = size;
		}
		return wordSize;
	}

	/**
	 * WifeDataSchedule �N���X�̃t�@�N�g�����\�b�h�ł��B A&A �W���̍��ڃp�^�[�����g�p�����A���������ꂽ�I�u�W�F�N�g��Ԃ��܂��B
	 * 
	 * @param special ������̐�
	 * @param number On/Off �X�P�W���[���̉�
	 * @return WifeDataSchedule �̃C���X�^���X
	 * @deprecated {@link #valueOf(int, int, String)}���g�p���ĉ������B
	 */
	public static WifeDataSchedule valueOf(int special, int number) {
		return valueOf(special, number, null);
	}

	/**
	 * WifeDataSchedule �N���X�̃t�@�N�g�����\�b�h�ł��B A&A �W���̍��ڃp�^�[�����g�p�����A���������ꂽ�I�u�W�F�N�g��Ԃ��܂��B
	 * 
	 * @param special ������̐�
	 * @param number On/Off �X�P�W���[���̉�
	 * @param groupName �O���[�v����
	 * @return WifeDataSchedule �̃C���X�^���X
	 */
	public static WifeDataSchedule valueOf(
			int special,
			int number,
			String groupName) {
		return valueOf(new DefaultSchedulePattern(special), number, groupName);
	}

	/**
	 * WifeDataSchedule �N���X�̃t�@�N�g�����\�b�h�ł��B �����Ŏw�肵�����ڃp�^�[�����g�p���āA���������ꂽ�I�u�W�F�N�g��Ԃ��܂��B
	 * 
	 * @param pattern ���ڃp�^�[��
	 * @param number On/Off �X�P�W���[���̉�
	 * @return WifeDataSchedule �̃C���X�^���X
	 */
	public static WifeDataSchedule valueOf(SchedulePattern pattern, int number) {
		return valueOf(pattern, number, null);
	}

	/**
	 * WifeDataSchedule �N���X�̃t�@�N�g�����\�b�h�ł��B �����Ŏw�肵�����ڃp�^�[�����g�p���āA���������ꂽ�I�u�W�F�N�g��Ԃ��܂��B
	 * 
	 * @param pattern ���ڃp�^�[��
	 * @param number On/Off �X�P�W���[���̉�
	 * @param groupName �O���[�v����
	 * @return WifeDataSchedule �̃C���X�^���X
	 */
	public static WifeDataSchedule valueOf(
			SchedulePattern pattern,
			int number,
			String groupName) {
		return new WifeDataSchedule(pattern, number, groupName);
	}

	/**
	 * �O���[�v���ȊO�𕡐������X�P�W���[���I�u�W�F�N�g��Ԃ��܂��B
	 * 
	 * @param src �O���[�v���������܂񂾃X�P�W���[���I�u�W�F�N�g
	 * @return �O���[�v���ȊO�𕡐������X�P�W���[���I�u�W�F�N�g��Ԃ��܂�
	 */
	public WifeDataSchedule duplicate(WifeDataSchedule src) {
		return new WifeDataSchedule(pattern, timeTable, src.groupName);
	}

	/**
	 * �����Ŏw�肳�ꂽ�X�P�W���[���f�[�^��Ԃ��܂��B
	 */
	public int getOnTime(int weekOfDay, int number) {
		numberCheck(number);
		return timeTable[weekOfDay][number].getOnTime();
	}

	/**
	 * �����Ŏw�肳�ꂽ�X�P�W���[���f�[�^��Ԃ��܂��B
	 */
	public int getOffTime(int weekOfDay, int number) {
		numberCheck(number);
		return timeTable[weekOfDay][number].getOffTime();
	}

	/**
	 * �����Ŏw�肳�ꂽ�X�P�W���[���f�[�^��ݒ肵�� <code>WifeDataSchedule</code> ��Ԃ��܂��B
	 */
	public WifeDataSchedule setOnTime(int weekOfDay, int number, int time) {
		numberCheck(number);

		timeTable[weekOfDay][number] = timeTable[weekOfDay][number]
				.setOnTime(time);
		return new WifeDataSchedule(
				this.pattern,
				this.timeTable,
				this.groupName);
	}

	/**
	 * �����Ŏw�肳�ꂽ�X�P�W���[���f�[�^��ݒ肵�� <code>WifeDataSchedule</code> ��Ԃ��܂��B
	 */
	public WifeDataSchedule setOffTime(int weekOfDay, int number, int time) {
		numberCheck(number);

		timeTable[weekOfDay][number] = timeTable[weekOfDay][number]
				.setOffTime(time);
		return new WifeDataSchedule(
				this.pattern,
				this.timeTable,
				this.groupName);
	}

	/**
	 * �����Ŏw�肳�ꂽ�O���[�v����ݒ肵�� <code>WifeDataSchedule</code> ��Ԃ��܂��B
	 * 
	 * @param groupName �O���[�v��
	 * @version 2.0.21
	 */
	public WifeDataSchedule setGroupName(String groupName) {
		return new WifeDataSchedule(this.pattern, this.timeTable, groupName);
	}

	/**
	 * �������`�F�b�N���܂��B
	 */
	private void numberCheck(int number) {
		if (number < 0 || number >= timeTable[0].length) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("number = ").append(number);
			buffer.append(" [numberMax = ").append(timeTable[0].length).append(
					"]");
			throw new IllegalArgumentException(buffer.toString());
		}
	}

	/**
	 * �X�P�W���[�����ڂ̑�����Ԃ��܂��B
	 */
	public int getPatternSize() {
		return pattern.size();
	}

	/**
	 * �X�P�W���[���f�[�^�񐔂̑�����Ԃ��܂��B
	 */
	public int getNumberSize() {
		return timeTable[0].length;
	}

	/**
	 * �����Ŏw�肳�ꂽ������̃C���f�b�N�X��Ԃ��܂��B
	 * 
	 * @param ������̎�ޔԍ�
	 */
	public int getSpecialDayOfIndex(int n) {
		return pattern.getSpecialDayOfIndex(n);
	}

	/**
	 * �����̍���(�j��)�ɂ�����C���f�b�N�X����Ԃ��܂��B
	 * 
	 * @param n ���ڂ̎��
	 * @return �����̍��ڂɂ�����C���f�b�N�X��
	 */
	public String getDayIndexName(int n) {
		return pattern.getDayIndexName(n);
	}

	/**
	 * �����̍���(�j��)�ɂ�����C���f�b�N�X��Ԃ��܂��B
	 * 
	 * @param n ���ڂ̎��(�j��)
	 * @return �����̍��ڂɂ�����C���f�b�N�X
	 */
	public int getDayIndex(int n) {
		return pattern.getDayIndex(n);
	}

	/**
	 * �O���[�v���̂��������܂��B
	 * 
	 * @return �O���[�v���̂��������܂�
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * �����̃I�u�W�F�N�g�̃C���f�b�N�X0����̃f�[�^�����̃I�u�W�F�N�g�ɃR�s�[�������Ԃ��܂��B
	 * 
	 * @param dest �R�s�[����I�u�W�F�N�g
	 * @return �����̃I�u�W�F�N�g�̃C���f�b�N�X0����̃f�[�^�����̃I�u�W�F�N�g�ɃR�s�[�������Ԃ��܂��B
	 */
	public WifeDataSchedule duplicateTodayAndTomorrow(WifeDataSchedule dest) {
		WifeDataSchedule on = duplicate(this);
		for (int i = 0; i < getPatternSize(); i++) {
			for (int j = 0; j < getNumberSize(); j++) {
				on = on.setOnTime(i, j, dest.getOnTime(i, j));
				on = on.setOffTime(i, j, dest.getOffTime(i, j));
			}
		}
		return on;
	}


	/**
	 * �X�P�W���[����ʂŏ���ɂ�����̌�(���������Ȃ�2, ����7���Ȃ�7�Ȃ�)��Ԃ��܂��B
	 * @return �X�P�W���[����ʂŏ���ɂ�����̌�(���������Ȃ�2, ����7���Ȃ�7�Ȃ�)��Ԃ��܂��B
	 */
	public int getTopSize() {
		return pattern.getTopSize();
	}

	/**
	 * �h��IreadResolve���\�b�h�B �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * 
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new WifeDataSchedule(pattern, timeTable, groupName);
	}
}
