/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/DefaultSchedulePattern.java,v 1.3.6.1 2006/12/12 08:43:28 frdm Exp $
 * $Revision: 1.3.6.1 $
 * $Date: 2006/12/12 08:43:28 $
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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * A&A �W���̍��ڃp�^�[���ł��B
 * ���̃N���X�͕s�σN���X�ł��B
 */
public final class DefaultSchedulePattern implements SchedulePattern, Serializable {
	private static final long serialVersionUID = 1855607429019335681L;
	/** ������\���C���f�b�N�X�̒萔�ł��B */
	public static final int TODAY = 0;
	/** ������\���C���f�b�N�X�̒萔�ł��B */
	public static final int TOMORROW = 1;
	/** ���j����\���C���f�b�N�X�̒萔�ł��B */
	public static final int SUNDAY = 2;
	/** ���j����\���C���f�b�N�X�̒萔�ł��B */
	public static final int MONDAY = 3;
	/** �Ηj����\���C���f�b�N�X�̒萔�ł��B */
	public static final int TUESDAY = 4;
	/** ���j����\���C���f�b�N�X�̒萔�ł��B */
	public static final int WEDNESDAY = 5;
	/** �ؗj����\���C���f�b�N�X�̒萔�ł��B */
	public static final int THURSDAY = 6;
	/** ���j����\���C���f�b�N�X�̒萔�ł��B */
	public static final int FRIDAY = 7;
	/** �y�j����\���C���f�b�N�X�̒萔�ł��B */
	public static final int SATURDAY = 8;
	/** �x����\���C���f�b�N�X�̒萔�ł��B */
	public static final int HOLIDAY = 9;
	/** ���ڂ̃C���f�b�N�X���`�B */
	private final List indexs;
	/** ������̐� */
	private final int specialDayCount;
	/** ���̃C���X�^���X�̃n�b�V���R�[�h�ł� */
	private transient volatile int hashCode;

	/**
	 * �R���X�g���N�^
	 * @param specialDayCount ������̐�
	 */
	public DefaultSchedulePattern(int specialDayCount) {
		this.specialDayCount = specialDayCount;
		indexs = new LinkedList();
		init();
	}

	private void init() {
		indexs.add(SchedulePatternItem.TODAY);
		indexs.add(SchedulePatternItem.TOMORROW);
		indexs.add(SchedulePatternItem.SUNDAY);
		indexs.add(SchedulePatternItem.MONDAY);
		indexs.add(SchedulePatternItem.TUESDAY);
		indexs.add(SchedulePatternItem.WEDNESDAY);
		indexs.add(SchedulePatternItem.THURSDAY);
		indexs.add(SchedulePatternItem.FRIDAY);
		indexs.add(SchedulePatternItem.SATURDAY);
		indexs.add(SchedulePatternItem.HOLIDAY);
		for (int i = 0, j = indexs.size(); i < specialDayCount; i++) {
			indexs.add(SchedulePatternItem.valueOf(j++, "�����" + (i + 1)));
		}
	}

	/**
	 * ���̃X�P�W���[���p�^�[���̑�����Ԃ��܂��B
	 * @return �X�P�W���[���p�^�[���̑���
	 */
	public int size() {
		return indexs.size();
	}

	/**
	 * �����̍��ڂɂ�����C���f�b�N�X��Ԃ��܂��B
	 * @param n ���ڂ̎��
	 * @return �����̍��ڂɂ�����C���f�b�N�X
	 */
	public int getDayIndex(int n) {
		checkIndex(n);
		return ((SchedulePatternItem)indexs.get(n)).getIndex();
	}

	/**
	 * �����̍��ڂɂ�����C���f�b�N�X����Ԃ��܂��B
	 * @param n ���ڂ̎��
	 * @return �����̍��ڂɂ�����C���f�b�N�X��
	 */
	public String getDayIndexName(int n) {
		checkIndex(n);
		return ((SchedulePatternItem)indexs.get(n)).getIndexName();
	}

	/**
	 * �����Ŏw�肳�ꂽ������̃C���f�b�N�X��Ԃ��܂��B
	 * @param ������̎�ޔԍ�(0 ����n�܂�܂�)
	 */
	public int getSpecialDayOfIndex(int n) {
		if (n < 0 || n >= specialDayCount)
			throw new IllegalArgumentException("Argument = " + n);
		return size() - specialDayCount + n;
	}

	private void checkIndex(int n) {
		if (n < 0 || n >= indexs.size())
			throw new IllegalArgumentException("Argument = " + n);
	}

	public int getTopSize() {
		return 2;
	}

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("indexs=");
		buffer.append(indexs.toString());
		buffer.append("specialDayCount=" + specialDayCount);
		return buffer.toString();
	}

	/**
	 * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����������܂��B
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DefaultSchedulePattern)) {
			return false;
		}

		DefaultSchedulePattern sp = (DefaultSchedulePattern)obj;

		if (this.specialDayCount != sp.specialDayCount)
			return false;

		return this.indexs.equals(sp.indexs);
	}

	/**
	 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			result = 37 * result + specialDayCount;
			result = 37 * result + indexs.hashCode();
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * ������ȊO��\���^�C�v�Z�[�temun�N���X�ł��B
	 * ��j
	 * �^�C���e�[�u���������A�����A���A�΁A���A�؁A���A�y�A���A�x���ɂ���
	 * �e�A�C�e���� index �� 0, 1, 8, 2, 3, 4, 5, 6, 7, 9 �ɂ���B
	 * @todo ��`���@���O���t�@�C���ŊO�ɏo���H
	 */
	private final static class SchedulePatternItem implements Serializable {
		private static final long serialVersionUID = -6343584860970223998L;

		static final SchedulePatternItem TODAY = new SchedulePatternItem(0, "����");
		static final SchedulePatternItem TOMORROW = new SchedulePatternItem(1, "����");
		static final SchedulePatternItem SUNDAY = new SchedulePatternItem(2, "���j");
		static final SchedulePatternItem MONDAY = new SchedulePatternItem(3, "���j");
		static final SchedulePatternItem TUESDAY = new SchedulePatternItem(4, "�Ηj");
		static final SchedulePatternItem WEDNESDAY = new SchedulePatternItem(5, "���j");
		static final SchedulePatternItem THURSDAY = new SchedulePatternItem(6, "�ؗj");
		static final SchedulePatternItem FRIDAY = new SchedulePatternItem(7, "���j");
		static final SchedulePatternItem SATURDAY = new SchedulePatternItem(8, "�y�j");
		static final SchedulePatternItem HOLIDAY = new SchedulePatternItem(9, "�x��");
/*
		static final SchedulePatternItem SUNDAY = new SchedulePatternItem(8, "���j");
		static final SchedulePatternItem MONDAY = new SchedulePatternItem(2, "���j");
		static final SchedulePatternItem TUESDAY = new SchedulePatternItem(3, "�Ηj");
		static final SchedulePatternItem WEDNESDAY = new SchedulePatternItem(4, "���j");
		static final SchedulePatternItem THURSDAY = new SchedulePatternItem(5, "�ؗj");
		static final SchedulePatternItem FRIDAY = new SchedulePatternItem(6, "���j");
		static final SchedulePatternItem SATURDAY = new SchedulePatternItem(7, "�y�j");
		static final SchedulePatternItem HOLIDAY = new SchedulePatternItem(9, "�x��");
*/

		private final int index;
		private final String indexName;

		private SchedulePatternItem(int index, String indexName) {
			this.index = index;
			this.indexName = indexName;
		}

		int getIndex() {
			return index;
		}

		String getIndexName() {
			return indexName;
		}

		static SchedulePatternItem valueOf(int index, String indexName) {
			return new SchedulePatternItem(index, indexName);
		}

		/**
		 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
		 */
		public String toString() {
			return "index=" + index + ",indexName=" + indexName;
		}

		/**
		 * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����������܂��B
		 */
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof SchedulePatternItem)) {
				return false;
			}

			SchedulePatternItem sp = (SchedulePatternItem)obj;

			return this.index == sp.index && this.indexName.equals(sp.indexName);
		}

		/**
		 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
		 */
		public int hashCode() {
			int result = 17;
			result = 37 * result + index;
			result = 37 * result + indexName.hashCode();
			return result;
		}
	}
}
