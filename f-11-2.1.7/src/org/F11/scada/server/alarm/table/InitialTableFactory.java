/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/InitialTableFactory.java,v 1.1.6.1 2006/08/11 02:24:33 frdm Exp $
 * $Revision: 1.1.6.1 $
 * $Date: 2006/08/11 02:24:33 $
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

import org.F11.scada.server.alarm.AlarmException;

/**
 * ���������ꂽ DefaultTableModel �I�u�W�F�N�g�𐶐�����A�t�@�N�g���[�N���X�ł��B
 * �T�u�N���X�ł͎g�p���郊�\�[�X����A�K�v�ȃf�[�^���擾���ăe�[�u�����f����
 * ���������ĕԂ��܂��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
abstract public class InitialTableFactory {
	/**
	 * �w�肵���t�@�N�g���[�I�u�W�F�N�g�𐶐����܂��B
	 * @param cls �����t�@�N�g���[�N���X
	 * @return InitialTableFactory �t�@�N�g���[�N���X
	 * @throws InstantiationException �N���X��������Ȃ��ꍇ
	 * @throws IllegalAccessException �N���X�̃A�N�Z�X�Ɏ��s�����ꍇ
	 */
	public static InitialTableFactory createInitialTableFactory(Class cls)
			throws InstantiationException, IllegalAccessException {
		return (InitialTableFactory) cls.newInstance();
	}

	/**
	 * ���������������f�[�^�̃e�[�u�����f����Ԃ��܂��B
	 * @return DefaultTableModel ���������������f�[�^�̃e�[�u�����f��
	 * @throws AlarmException �e�[�u���������ɗ�O�����������ꍇ
	 */
	abstract public AlarmTableModel createCareer() throws AlarmException;

	/**
	 * �����������q�X�g���[�f�[�^�̃e�[�u�����f����Ԃ��܂��B
	 * @return DefaultTableModel �����������q�X�g���[�f�[�^�̃e�[�u�����f��
	 * @throws AlarmException �e�[�u���������ɗ�O�����������ꍇ
	 */
	abstract public AlarmTableModel createHistory() throws AlarmException;

	/**
	 * �����������T�}���[�f�[�^�̃e�[�u�����f����Ԃ��܂��B
	 * @return DefaultTableModel �����������T�}���[�f�[�^�̃e�[�u�����f��
	 * @throws AlarmException �e�[�u���������ɗ�O�����������ꍇ
	 */
	abstract public AlarmTableModel createSummary() throws AlarmException;

	/**
	 * �����������������f�[�^�̃e�[�u�����f����Ԃ��܂��B
	 * @return DefaultTableModel �����������������f�[�^�f�[�^�̃e�[�u�����f��
	 * @throws AlarmException �e�[�u���������ɗ�O�����������ꍇ
	 */
	abstract public AlarmTableModel createOccurrence() throws AlarmException;

	/**
	 * �������������m�F�f�[�^�̃e�[�u�����f����Ԃ��܂��B
	 * @return DefaultTableModel �������������m�F�f�[�^�f�[�^�̃e�[�u�����f��
	 * @throws AlarmException �e�[�u���������ɗ�O�����������ꍇ
	 */
	abstract public AlarmTableModel createNoncheck() throws AlarmException;
}
