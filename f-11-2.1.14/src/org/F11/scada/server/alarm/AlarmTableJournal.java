/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/AlarmTableJournal.java,v 1.3 2003/02/21 05:08:55 frdm Exp $
 * $Revision: 1.3 $
 * $Date: 2003/02/21 05:08:55 $
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
package org.F11.scada.server.alarm;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

/**
 * �N���C�A���g�E�T�[�o�[�Ԃ̌x��E��ԃe�[�u�����f���X�V�Ɏg�p����s�f�[�^�N���X�ł��B
 * ���̃N���X�͕s�σN���X�ł��B�N���X�̋@�\������ꍇ�ɁA�g���ł͂Ȃ��Ϗ����f�����g�p���ĉ������B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class AlarmTableJournal implements Serializable {
	private static final long serialVersionUID = -5727184066915388566L;
	/** �ǉ��s�f�[�^��\���܂��B */
	public static final int INSERT_OPERATION = 1;
	/** �폜�s�f�[�^��\���܂��B */
	public static final int REMOVE_OPERATION = 2;
	/** �X�V�s�f�[�^��\���܂��B */
	public static final int MODIFY_OPERATION = 0;

	private final DataValueChangeEventKey key;
	private final int operationType;
	private final Object[] data;

	/**
	 * �s�f�[�^�𐶐����܂��B���̃I�u�W�F�N�g�͌x��ꗗ�̃e�[�u�����f�����A�N���C�A���g�E�T�[�o�[�Ԃœ������邽�߂Ɏg�p���܂��B
	 * @param key �s�̃L�[�I�u�W�F�N�g
	 * @param data �e�[�u�����f���̍s
	 * @param rowType �s������
	 */	
	private AlarmTableJournal(DataValueChangeEventKey key, Object[] data, int rowType) {
		this.key = key;
		this.data = new Object[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length);
		this.operationType = rowType;
	}

	/**
	 * �ǉ��s�f�[�^�𐶐����܂��B
	 * @param key �s�̃L�[�I�u�W�F�N�g
	 * @param data �e�[�u�����f���̍s
	 * @return �ǉ��s�f�[�^
	 */
	public static AlarmTableJournal createRowDataAddOpe(
			DataValueChangeEventKey key,
			Object[] data) {
		checkArgument(key, data);
		return new AlarmTableJournal(key, data, INSERT_OPERATION);
	}
	
	/**
	 * �폜�s�f�[�^�𐶐����܂��B
	 * @param key �s�̃L�[�I�u�W�F�N�g
	 * @param data �e�[�u�����f���̍s
	 * @return �폜�s�f�[�^
	 */
	public static AlarmTableJournal createRowDataRemoveOpe(
			DataValueChangeEventKey key,
			Object[] data) {
		checkArgument(key, data);
		return new AlarmTableJournal(key, data, REMOVE_OPERATION);
	}

	/**
	 * �X�V�s�f�[�^�𐶐����܂��B
	 * @param key �s�̃L�[�I�u�W�F�N�g
	 * @param data �e�[�u�����f���̍s
	 * @return �X�V�s�f�[�^
	 */
	public static AlarmTableJournal createRowDataModifyOpe(
			DataValueChangeEventKey key,
			Object[] data) {
		checkArgument(key, data);
		return new AlarmTableJournal(key, data, MODIFY_OPERATION);
	}
	
	private static void checkArgument(DataValueChangeEventKey key, Object[] data) {
		if (key == null) {
			throw new IllegalArgumentException("key is null.");
		}
		if (data == null) {
			throw new IllegalArgumentException("data is null.");
		}
	}

	/**
	 * �e�[�u�����f���Ɏg�p����s�f�[�^��Ԃ��܂��B
	 * @return �e�[�u�����f���Ɏg�p����s�f�[�^
	 */
	public Object[] getData() {
		Object[] rd = new Object[data.length];
		System.arraycopy(data, 0, rd, 0, data.length);
		return rd;
	}

	/**
	 * �s�f�[�^�̑�����@��Ԃ��܂��B�߂�l�͈ȉ��̒萔�ŕԂ���܂��B
	 * <UL>
	 * <LI>ADD_OPERATION �ǉ��s�f�[�^
	 * <LI>REMOVE_OPERATION �폜�s�f�[�^
	 * <LI>MODIFY_OPERATION �X�V�s�f�[�^
	 * </UL>
	 * @return �s�f�[�^�̑�����@��ADD_OPERATION, REMOVE_OPERATION, MODIFY_OPERATION�̂����ꂩ��
	 * �萔�ŕԂ��܂��B
	 */
	public int getOperationType() {
		return operationType;
	}

	/**
	 * �s�f�[�^�̃^�C���X�^���v��Ԃ��܂��B
	 * @return �s�f�[�^�̃^�C���X�^���v
	 */	
	public Timestamp getTimestamp() {
		return key.getTimeStamp();
	}

	/**
	 * �s�f�[�^�̃^�C���X�^���v��ݒ肵�� AlarmTableJournal �I�u�W�F�N�g��Ԃ��܂��B
	 * @param t �ݒ肷��^�C���X�^���v
	 * @return AlarmTableJournal �I�u�W�F�N�g
	 */	
	public AlarmTableJournal setTimestamp(Timestamp t) {
		if (t == null) {
			throw new IllegalArgumentException("Set Time is null.");
		}
		
		return new AlarmTableJournal(key.setTimeStamp(t), data, operationType);
	}
	
	/**
	 * �s�f�[�^�̃|�C���g��Ԃ��܂��B
	 * @return �s�f�[�^�̃|�C���g
	 */
	public int getPoint() {
		return key.getPoint();
	}
	
	/**
	 * �s�f�[�^�̃f�[�^�v���o�C�_����Ԃ��܂��B
	 * @return �s�f�[�^�̃f�[�^�v���o�C�_��
	 */
	public String getProvider() {
		return key.getProvider();
	}
	
	/**
	 * �s�f�[�^�̃f�[�^�z���_����Ԃ��܂��B
	 * @return �s�f�[�^�̃f�[�^�z���_��
	 */
	public String getHolder() {
		return key.getHolder();
	}
	
	/*
	 * ���̃I�u�W�F�N�g�̃^�C���X�^���v���A�����̃^�C���X�^���v���傫����� true �������łȂ���� false ��Ԃ��܂��B
	 * @param t �^�C���X�^���v
	 * @return ���̃I�u�W�F�N�g�̃^�C���X�^���v���A�����̃^�C���X�^���v���傫����� true �������łȂ���� false ��Ԃ��܂��B
	public boolean isSendData(Timestamp t) {
		Timestamp ts = key.getTimeStamp();
		return (ts.compareTo(t) > 0) ? true : false;
	}
	 */

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 * @return ���̃I�u�W�F�N�g�̕�����\��
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(key.toString());
		buffer.append(",operationType=" + operationType);
		buffer.append(",data=" + Arrays.asList(data).toString());
		return buffer.toString();
	}
	
	/**
	 * ���̃I�u�W�F�N�g�̒l���ׁA�����Ȃ�� true ��Ԃ��܂��B
	 * @param obj ��r�Ώۂ̃I�u�W�F�N�g
	 * @return ���̃I�u�W�F�N�g�̒l���ׁA�����Ȃ�� true ��Ԃ��܂��B
	 */
	public boolean equals(Object obj) {
		if (obj == this) {	
			return true;
		}
		
		if (!(obj instanceof AlarmTableJournal)) {
			return false;
		}
		
		AlarmTableJournal aj = (AlarmTableJournal) obj;
		return key.equals(aj.key)
				&& operationType == aj.operationType
				&& Arrays.equals(data, aj.data);
	}

	/**
	 * ���̃I�u�W�F�N�g�̃n�b�V����Ԃ��܂�
	 * @return ���̃I�u�W�F�N�g�̃n�b�V��
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + key.hashCode();
		result = 37 * result + operationType;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				result = 37 * result + data[i].hashCode();
			}
		}
		return result;
	}

	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new AlarmTableJournal(key, data, operationType);
	}
}
