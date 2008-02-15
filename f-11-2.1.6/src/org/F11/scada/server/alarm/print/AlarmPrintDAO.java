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
 */

package org.F11.scada.server.alarm.print;

import java.sql.SQLException;
import java.util.List;

import org.F11.scada.server.alarm.DataValueChangeEventKey;


/**
 * �x�񃁃b�Z�[�W����f�[�^�𑀍삷��DAO�C���^�[�t�F�C�X
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface AlarmPrintDAO {
	/**
	 * ������̃f�[�^��S�ĕԂ��܂��B
	 * @return PrintLineData�I�u�W�F�N�g�̃��X�g
	 * @exception SQLException �f�[�^�x�[�X�G���[������
	 */
	public List findAll() throws SQLException;

	/**
	 * ���X�g�̓��e���f�[�^�x�[�X�ɑ}�����܂�
	 * @param key �f�[�^�ύX�C�x���g
	 * @exception SQLException �f�[�^�x�[�X�G���[������
	 */
	public void insert(DataValueChangeEventKey key) throws SQLException;

	/**
	 * �����̃C�x���g���L�[�ɂ��Ĉ���f�[�^�I�u�W�F�N�g��Ԃ��܂�
	 * @param key �f�[�^�ύX�C�x���g
	 * @return ����f�[�^�I�u�W�F�N�g
	 * @exception SQLException �f�[�^�x�[�X�G���[������
	 */
	public PrintLineData find(DataValueChangeEventKey key) throws SQLException;

	/**
	 * �x�񃁃b�Z�[�W����f�[�^��S�č폜���܂�
	 * @exception SQLException �f�[�^�x�[�X�G���[������
	 */
	public void deleteAll() throws SQLException;

	/**
	 * �f�[�^�ύX�C�x���g���x�񃁃b�Z�[�W����̑Ώۂ��ǂ����𔻒肵�܂��B
	 * @param key �f�[�^�ύX�C�x���g
	 * @return �x�񃁃b�Z�[�W����ΏۂȂ� true �� �ΏۂłȂ���� false ��Ԃ��܂�
	 * @exception SQLException �f�[�^�x�[�X�G���[������
	 */
	public boolean isAlarmPrint(DataValueChangeEventKey key) throws SQLException;
}
