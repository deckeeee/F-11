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

package org.F11.scada.server.schedule;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.server.schedule.point.dto.DuplicateSeparateScheduleDto;
import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;

public interface SchedulePointCommunicator {
	/**
	 * �O���[�v�ԍ��z���_���X�V���܂��B
	 * 
	 * @param dto �s�f�[�^
	 */
	void setHolder(SchedulePointRowDto dto);

	/**
	 * �Ώۃz���_�̒ʐM�f�[�^��Ԃ��܂��B
	 * 
	 * @param dto �Ώۃz���_
	 * @return �Ώۃz���_�̒ʐM�f�[�^��Ԃ��܂��B
	 */
	List getHolderData(List dto);

	/**
	 * �}�X�^�X�P�W���[������ʃX�P�W���[���փf�[�^���R�s�[���܂��B
	 * 
	 * @param src �R�s�[���}�X�^�X�P�W���[��
	 * @param dest �R�s�[��ʃX�P�W���[���z���_�̔z��
	 * @deprecated
	 */
	void duplicateSeparateSchedule(
			ScheduleGroupDto src,
			SchedulePointRowDto[] dest);

	/**
	 * �}�X�^�X�P�W���[������ʃX�P�W���[���փf�[�^���R�s�[���܂��B
	 * 
	 * @param dto �X�P�W���[���R�s�[�I�u�W�F�N�g
	 */
	void duplicateSeparateSchedule(DuplicateSeparateScheduleDto dto);

	/**
	 * �ʃX�P�W���[��(�X�P�W���[��)�l��Ԃ��܂��B
	 * 
	 * @param dto �z���_���̐ݒ肳�ꂽ�I�u�W�F�N�g
	 * @return �ʃX�P�W���[��(�X�P�W���[��)�l��Ԃ��܂��B
	 */
	WifeDataSchedule getSeparateSchedule(SchedulePointRowDto dto);

	/**
	 * �ʃX�P�W���[���l���X�V���܂��B
	 * 
	 * @param dto �z���_���̐ݒ肳�ꂽ�s�f�[�^
	 * @param date �X�V����
	 * @param data �X�V�f�[�^�l
	 * @throws RemoteException
	 */
	void updateSeperateSchedule(
			SchedulePointRowDto dto,
			Date date,
			WifeDataSchedule data);

}
