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

package org.F11.scada.server.schedule.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.server.schedule.SchedulePointCommunicator;
import org.F11.scada.server.schedule.SchedulePointService;
import org.F11.scada.server.schedule.point.dao.ScheduleGroupDao;
import org.F11.scada.server.schedule.point.dao.SchedulePointDao;
import org.F11.scada.server.schedule.point.dto.DuplicateSeparateScheduleDto;
import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.server.schedule.point.dto.ScheduleSearchDto;
import org.F11.scada.util.RmiUtil;
import org.apache.log4j.Logger;

public class SchedulePointServiceImpl implements SchedulePointService {
	private final Logger logger = Logger.getLogger(SchedulePointServiceImpl.class);
	private SchedulePointDao schedulePointDao;
	private ScheduleGroupDao scheduleGroupDao;
	private SchedulePointCommunicator communicator;
	private List currentSchePointRows;

	public SchedulePointServiceImpl() {
		RmiUtil.registryServer(this, SchedulePointService.class);
	}

	public void init() {
		List dto = schedulePointDao.getAllSchedulePoint();
//		if (null == currentSchePointRows) {
			currentSchePointRows = communicator.getHolderData(dto);
			//schePointRowsはソート済み
			for (Iterator i = currentSchePointRows.iterator(); i.hasNext();) {
				SchedulePointRowDto rowDto = (SchedulePointRowDto) i.next();
				schedulePointDao.updateScheduleGroupNo(rowDto);
			}
//		} else {
//			List schePointRows = communicator.getHolderData(dto);
//			//schePointRowsはソート済み
//			for (Iterator i = select(schePointRows).iterator(); i.hasNext();) {
//				SchedulePointRowDto rowDto = (SchedulePointRowDto) i.next();
//				schedulePointDao.updateScheduleGroupNo(rowDto);
//			}
//		}
	}

	private List select(List schePointRows) {
		ArrayList dtos = new ArrayList(schePointRows.size());
		for (int i = 0; i < schePointRows.size(); i++) {
			SchedulePointRowDto dto = (SchedulePointRowDto) schePointRows.get(i);
			SchedulePointRowDto cdto = (SchedulePointRowDto) currentSchePointRows.get(i);
			if (!dto.getGroupNo().equals(cdto.getGroupNo())) {
				dtos.add(dto);
				currentSchePointRows.set(i, dto);
			}
		}
		return dtos;
	}

	public void setSchedulePointDao(SchedulePointDao schedulePointDao) {
		this.schedulePointDao = schedulePointDao;
	}

	public void setGroupDao(ScheduleGroupDao scheduleGroupDao) {
		this.scheduleGroupDao = scheduleGroupDao;
	}

	public void setCommunicator(SchedulePointCommunicator communicator) {
		this.communicator = communicator;
	}

	public SchedulePointDto getSchedulePoint(ScheduleSearchDto dto) {
		List list = schedulePointDao.getSchedulePoint(dto);
		return new SchedulePointDto(list, dto);
	}

	public SchedulePointDto getSchedulePointByGroup(ScheduleSearchDto dto)
			throws RemoteException {
		List list = schedulePointDao.getSchedulePointByGroup(dto);
		return new SchedulePointDto(list, dto);
	}

	public List getScheduleGroup(ScheduleGroupDto dto) {
		return scheduleGroupDao.getScheduleGroup(dto);
	}

	public int updateSchedulePoint(SchedulePointRowDto dto)
			throws RemoteException {
		communicator.setHolder(dto);
		return schedulePointDao.updateSchedulePoint(dto);
	}

	public void duplicateSeparateSchedule(
			ScheduleGroupDto src,
			SchedulePointRowDto[] dest) throws RemoteException {
		communicator.duplicateSeparateSchedule(src, dest);
	}

	public void duplicateSeparateSchedule(DuplicateSeparateScheduleDto dto)
			throws RemoteException {
		communicator.duplicateSeparateSchedule(dto);
	}

	public WifeDataSchedule getSeparateSchedule(SchedulePointRowDto dto) {
		return communicator.getSeparateSchedule(dto);
	}

	public void updateSeperateSchedule(
			SchedulePointRowDto dto,
			Date date,
			WifeDataSchedule data) throws RemoteException {
		communicator.updateSeperateSchedule(dto, date, data);
	}
}
