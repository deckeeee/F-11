/*
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

package org.F11.scada.applet.schedule.point;

import java.rmi.RemoteException;

import javax.swing.table.TableModel;

import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.server.schedule.point.dto.SchedulePointDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.server.schedule.point.dto.ScheduleSearchDto;

/**
 * �X�P�W���[���@��ꗗ�̃e�[�u�����f��
 * 
 * @author maekawa
 * 
 */
public interface SchedulePointTableModel extends TableModel {
	/**
	 * ���������Ńe�[�u�����f���𐶐����Ȃ����A�������ʂ́u�����R�[�h��(count)�v�u���݌����I�t�Z�b�g(offset)�v�u�ő匟�����R�[�h��(limit)�v��Ԃ��܂��B
	 * 
	 * @param dto ��������
	 * @return �����I����̌���������Ԃ��܂��B
	 */
	SchedulePointDto find(ScheduleSearchDto dto) throws RemoteException;

	/**
	 * ���������Ńe�[�u�����f���𐶐����Ȃ����A�������ʂ́u�����R�[�h��(count)�v�u���݌����I�t�Z�b�g(offset)�v�u�ő匟�����R�[�h��(limit)�v��Ԃ��܂��B
	 * 
	 * @param dto ��������
	 * @return �����I����̌���������Ԃ��܂��B
	 */
	SchedulePointDto findByGroup(ScheduleSearchDto dto) throws RemoteException;

	/**
	 * AbstractTableModel#fireTableDataChanged�����̂܂܎��s���܂��B
	 * 
	 */
	void fireTableDataChanged();

	/**
	 * �s�f�[�^��Ԃ��܂��B
	 * 
	 * @param row �s
	 * @return �s�f�[�^��Ԃ��܂��B
	 */
	SchedulePointRowDto getSchedulePointRowDto(int row);

	/**
	 * �Ώۍs�̃f�[�^���X�V���܂�
	 * 
	 * @param dto �X�V�f�[�^
	 * @param row �X�V�f�[�^�s
	 */
	void setSchedulePointRowDto(SchedulePointRowDto dto, int row);

	/**
	 * �Ώۍs�̃X�P�W���[���l��Ԃ��܂��B
	 * @param row �Ώۂ̍s
	 * @return �Ώۍs�̃X�P�W���[���l��Ԃ��܂��B
	 */
	WifeDataSchedule getSeparateSchedule(int row);
}
