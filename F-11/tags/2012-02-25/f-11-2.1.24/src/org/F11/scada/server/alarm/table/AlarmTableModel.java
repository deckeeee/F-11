/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/AlarmTableModel.java,v 1.5.2.2 2006/08/16 08:53:06 frdm Exp $
 * $Revision: 1.5.2.2 $
 * $Date: 2006/08/16 08:53:06 $
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
package org.F11.scada.server.alarm.table;

import java.util.SortedMap;

import javax.swing.table.TableModel;

import org.F11.scada.server.alarm.AlarmTableJournal;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.xwife.applet.alarm.event.CheckEvent;
import org.F11.scada.xwife.applet.alarm.event.CheckTable;

/**
 * �x��E��ԃe�[�u�����f���̃C���^�[�t�F�C�X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface AlarmTableModel extends TableModel, CheckTable {
	/**
	 * �����Ŏw�肵���^�C���X�^���v���傫���W���[�i���f�[�^�̃��X�g��Ԃ��܂��B
	 * @param t �^�C���X�^���v��Long�l
	 * @return SortedMap �W���[�i���f�[�^�̃}�b�v
	 */
	public SortedMap<Long, AlarmTableJournal> getAlarmJournal(long t);

	/**
	 * �w�肵���s�E�J�����Ƀf�[�^��ݒ肵�܂��B���̌�W���[�i���f�[�^��ǉ����܂��B
	 * @param data �ݒ肷��f�[�^�̔z��
	 * @param rowIndex �f�[�^��ݒ肷��s
	 * @param columnIndex �f�[�^��ݒ肷��J����
	 * @param key �f�[�^�ύX�C�x���g�l
	 */
	public void setValueAt(
			Object[] data,
			int rowIndex,
			int columnIndex,
			DataValueChangeEventKey key);

	/**
	 * �w�肵���s�Ƀf�[�^��}�����܂��B���̌�W���[�i���f�[�^��ǉ����܂��B
	 * @param row �f�[�^��}������s
	 * @param data �}������f�[�^�̔z��
	 * @param key �f�[�^�ύX�C�x���g�l
	 */
	public void insertRow(int row, Object[] data, DataValueChangeEventKey key);

	/**
	 * �w�肵���s�̃f�[�^���폜���܂��B���̌�W���[�i���f�[�^��ǉ����܂��B
	 * @param row �폜����s
	 * @param key �f�[�^�ύX�C�x���g�l
	 */
	public void removeRow(int row, DataValueChangeEventKey key);

	/**
	 * �W���[�i���f�[�^�̃��X�g���e�[�u�����f���ɔ��f���A�W���[�i���f�[�^�ɒǉ����܂��B
	 * @param value �W���[�i���f�[�^�̃\�[�g�}�b�v
	 */
	public void setValue(SortedMap<Long, AlarmTableJournal> value);

	/**
	 * �Ō�̃W���[�i���f�[�^��Ԃ��܂��B
	 * @return �Ō�̃W���[�i���f�[�^
	 */
	public AlarmTableJournal getLastJournal();

	/**
	 * �L�[���܂ލŏ��̍s��Ԃ��܂��B
	 * @param key �f�[�^�ύX�C�x���g�l�L�[�I�u�W�F�N�g
	 * @return int �L�[�̍s�����݂����ꍇ�́A���̍s��Ԃ��܂��B���݂��Ȃ��ꍇ�͕���(-1)��Ԃ��܂��B
	 */
	public int searchRow(DataValueChangeEventKey key);

	/**
	 * �w�肵���s�Ƀf�[�^��}�����܂��B
	 * @param row �f�[�^��}������s
	 * @param data �}������f�[�^�̔z��
	 */
	public void insertRow(int row, Object[] data);

	/**
	 * �x��ύX������ǉ����܂�
	 * @param aj �x��ύX����
	 */
	void setJournal(AlarmTableJournal aj);

	/**
	 * �����Ŏw�肵���^�C���X�^���v���傫���m�F�C�x���g�W���[�i���f�[�^�̃��X�g��Ԃ��܂��B
	 * @param t �^�C���X�^���v��Long�l
	 * @return SortedMap �m�F�C�x���g�W���[�i���f�[�^�̃}�b�v
	 */
	SortedMap<Long, CheckEvent> getCheckJournal(long t);

	/**
	 * �Ō�̌x��m�F�W���[�i���f�[�^��Ԃ��܂��B
	 * @return �Ō�̌x��m�F�W���[�i���f�[�^
	 */
	CheckEvent getLastCheckEvent();

	/**
	 * �s���Ɨ񖼂���e�[�u���̒l�����o���܂��B
	 * @param row �s
	 * @param columnName �J������
	 * @return �l�I�u�W�F�N�g
	 */
	Object getValueAt(int row, String columnName);

	/**
	 * �J����������񐔂�Ԃ��܂��B
	 * @param columnName �J������
	 * @return ��
	 */
	int getColumn(String columnName);


	/**
	 * �w�肵���s�̃f�[�^���폜���܂��B
	 * @param row �폜����s
	 */
	void removeRow(int row);
}
