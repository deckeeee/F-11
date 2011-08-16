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
package org.F11.scada.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * @author hori
 */
public class SevenDaysSchedulePattern implements SchedulePattern, Serializable {
	private static final long serialVersionUID = -3603146153928950069L;
	/** ���ڂ̃C���f�b�N�X���`�B */
	private final SevenDaysPatternItem[] indexs;
	/** ���̃C���X�^���X�̃n�b�V���R�[�h�ł� */
	private transient volatile int hashCode;

	/**
	 * �R���X�g���N�^
	 */
	public SevenDaysSchedulePattern() {
		indexs = new SevenDaysPatternItem[SevenDaysPatternItem.PATTERN_SIZE];
		init();
	}

	private void init() {
		indexs[0] = SevenDaysPatternItem.DAY0;
		indexs[1] = SevenDaysPatternItem.DAY1;
		indexs[2] = SevenDaysPatternItem.DAY2;
		indexs[3] = SevenDaysPatternItem.DAY3;
		indexs[4] = SevenDaysPatternItem.DAY4;
		indexs[5] = SevenDaysPatternItem.DAY5;
		indexs[6] = SevenDaysPatternItem.DAY6;
	}

	/**
	 * ���̃X�P�W���[���p�^�[���̑�����Ԃ��܂��B
	 * @return �X�P�W���[���p�^�[���̑���
	 */
	public int size() {
		return indexs.length;
	}

	/**
	 * �����̍��ڂɂ�����C���f�b�N�X��Ԃ��܂��B
	 * @param n ���ڂ̎��
	 * @return �����̍��ڂɂ�����C���f�b�N�X
	 */
	public int getDayIndex(int n) {
		checkIndex(n);
		return indexs[n].getIndex();
	}

	/**
	 * �����̍��ڂɂ�����C���f�b�N�X����Ԃ��܂��B
	 * @param n ���ڂ̎��
	 * @return �����̍��ڂɂ�����C���f�b�N�X��
	 */
	public String getDayIndexName(int n) {
		checkIndex(n);
		return indexs[n].getIndexName();
	}

	public int getTopSize() {
		return SevenDaysPatternItem.PATTERN_SIZE;
	}

	/**
	 * �����Ŏw�肳�ꂽ������̃C���f�b�N�X��Ԃ��܂��B
	 * @param ������̎�ޔԍ�(0 ����n�܂�܂�)
	 */
	public int getSpecialDayOfIndex(int n) {
		throw new UnsupportedOperationException("Argument = " + n);
	}

	private void checkIndex(int n) {
		if (n < 0 || n >= indexs.length)
			throw new IllegalArgumentException("Argument = " + n);
	}

	/**
	 * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����������܂��B
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SevenDaysSchedulePattern)) {
			return false;
		}
		SevenDaysSchedulePattern sp = (SevenDaysSchedulePattern) obj;
		return Arrays.equals(indexs, sp.indexs);
	}

	/**
	 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			for (int i = 0; i < indexs.length; i++) {
				result = 37 * result + indexs[i].hashCode();
			}
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("indexs=");
		buffer.append(indexs.toString());
		return buffer.toString();
	}



	/**
	 * 7���ʃX�P�W���[���̃^�C�v�Z�[�temun�N���X�ł��B
	 */
	private final static class SevenDaysPatternItem implements Serializable {
		private static final long serialVersionUID = 5729740520442131274L;
		static final int PATTERN_SIZE = 7;
		static final SevenDaysPatternItem DAY0 = new SevenDaysPatternItem(0);
		static final SevenDaysPatternItem DAY1 = new SevenDaysPatternItem(1);
		static final SevenDaysPatternItem DAY2 = new SevenDaysPatternItem(2);
		static final SevenDaysPatternItem DAY3 = new SevenDaysPatternItem(3);
		static final SevenDaysPatternItem DAY4 = new SevenDaysPatternItem(4);
		static final SevenDaysPatternItem DAY5 = new SevenDaysPatternItem(5);
		static final SevenDaysPatternItem DAY6 = new SevenDaysPatternItem(6);

		private final int index;

		SevenDaysPatternItem(int index) {
			this.index = index;
		}

		int getIndex() {
			return index;
		}

		String getIndexName() {
			SimpleDateFormat format = new SimpleDateFormat("M��d��(E)");
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, index);
			return format.format(calendar.getTime());
		}

		/**
		 * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����������܂��B
		 */
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof SevenDaysPatternItem)) {
				return false;
			}
			SevenDaysPatternItem sp = (SevenDaysPatternItem) obj;
			return this.index == sp.index;
		}

		/**
		 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
		 */
		public int hashCode() {
			return 37 * 17 + index;
		}

		/**
		 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
		 */
		public String toString() {
			return "index=" + index + ", indexName=" + getIndexName();
		}
	}
}
