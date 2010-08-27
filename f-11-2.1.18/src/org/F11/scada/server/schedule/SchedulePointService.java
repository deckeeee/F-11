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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.server.schedule.point.dto.DuplicateSeparateScheduleDto;
import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.server.schedule.point.dto.ScheduleSearchDto;

/**
 * �X�P�W���[������̃T�[�o�[�T�[�r�X
 * 
 * @author maekawa
 * 
 */
public interface SchedulePointService extends Remote {
	/**
	 * ������DTO�����ŃX�P�W���[���@��ꗗ�̃��R�[�h��Ԃ��܂��B
	 * 
	 * @param dto ��������
	 * @return ������DTO�����ŃX�P�W���[���@��ꗗ�̃��R�[�h��Ԃ��܂��B
	 * @throws RemoteException
	 */
	SchedulePointDto getSchedulePoint(ScheduleSearchDto dto)
			throws RemoteException;

	/**
	 * ������DTO�����ŃX�P�W���[���@��ꗗ�̃��R�[�h��Ԃ��܂��B
	 * 
	 * @param dto ��������
	 * @return ������DTO�����ŃX�P�W���[���@��ꗗ�̃��R�[�h��Ԃ��܂��B
	 * @throws RemoteException
	 */
	SchedulePointDto getSchedulePointByGroup(ScheduleSearchDto dto)
			throws RemoteException;

	/**
	 * �T�[�r�X�����������܂��B
	 * 
	 * @throws RemoteException
	 */
	void init() throws RemoteException;

	/**
	 * �X�P�W���[���O���[�v�̃��R�[�h��Ԃ��܂��B
	 * 
	 * @return �X�P�W���[���O���[�v�̃��R�[�h��Ԃ��܂��B
	 * @throws RemoteException
	 */
	List getScheduleGroup(ScheduleGroupDto dto) throws RemoteException;

	/**
	 * �ΏۃX�P�W���[���O���[�vNo.���X�V���APLC�̏������݂��s���܂��B
	 * 
	 * @param dto
	 * @return
	 * @throws RemoteException
	 */
	int updateSchedulePoint(SchedulePointRowDto dto) throws RemoteException;

	/**
	 * �}�X�^�X�P�W���[������ʃX�P�W���[���փf�[�^���R�s�[���܂��B
	 * 
	 * @param src �R�s�[���}�X�^�X�P�W���[���z���_��
	 * @param dest �R�s�[��ʃX�P�W���[���z���_���̔z��
	 * @throws RemoteException
	 * @deprecated {@link duplicateSeparateSchedule(DuplicateSeparateScheduleDto)}���g�p���Ă��������B
	 */
	void duplicateSeparateSchedule(
			ScheduleGroupDto src,
			SchedulePointRowDto[] dest) throws RemoteException;

	/**
	 * �}�X�^�X�P�W���[������ʃX�P�W���[���փf�[�^���R�s�[���܂��B
	 * 
	 * @param dto �X�P�W���[���R�s�[�I�u�W�F�N�g
	 * @throws RemoteException
	 */
	void duplicateSeparateSchedule(DuplicateSeparateScheduleDto dto)
			throws RemoteException;

	/**
	 * �ʃX�P�W���[��(�X�P�W���[��)�l��Ԃ��܂��B
	 * 
	 * @param dto �z���_���̐ݒ肳�ꂽ�s�f�[�^
	 * @return �ʃX�P�W���[��(�X�P�W���[��)�l��Ԃ��܂��B
	 */
	WifeDataSchedule getSeparateSchedule(SchedulePointRowDto dto)
			throws RemoteException;

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
			WifeDataSchedule data) throws RemoteException;
}
