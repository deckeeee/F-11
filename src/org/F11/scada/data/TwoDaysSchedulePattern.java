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
import java.util.LinkedList;
import java.util.List;

/**
 * @author hori
 */
public class TwoDaysSchedulePattern implements SchedulePattern, Serializable {
	static final long serialVersionUID = -4465547942929089419L;
	/** ������\���C���f�b�N�X�̒萔�ł��B */
	public static final int TODAY = 0;
	/** ������\���C���f�b�N�X�̒萔�ł��B */
	public static final int TOMORROW = 1;
	/** ���ڂ̃C���f�b�N�X���`�B */
	private final List indexs;
	/** ���̃C���X�^���X�̃n�b�V���R�[�h�ł� */
	private transient volatile int hashCode;

	/**
	 * �R���X�g���N�^
	 */
	public TwoDaysSchedulePattern() {
		indexs = new LinkedList();
		init();
	}

	private void init() {
		indexs.add(SchedulePatternItem.TODAY);
		indexs.add(SchedulePatternItem.TOMORROW);
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
		throw new UnsupportedOperationException("Argument = " + n);
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
		return buffer.toString();
	}

	/**
	 * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����������܂��B
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof TwoDaysSchedulePattern)) {
			return false;
		}

		TwoDaysSchedulePattern sp = (TwoDaysSchedulePattern)obj;

		return this.indexs.equals(sp.indexs);
	}

	/**
	 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			result = 37 * result + indexs.hashCode();
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * ������ȊO��\���^�C�v�Z�[�temun�N���X�ł��B
	 * ��j
	 * @todo ��`���@���O���t�@�C���ŊO�ɏo���H
	 */
	private final static class SchedulePatternItem implements Serializable {
		static final long serialVersionUID = -5213928260544704641L;
		static final SchedulePatternItem TODAY = new SchedulePatternItem(0, "����");
		static final SchedulePatternItem TOMORROW = new SchedulePatternItem(1, "����");

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
