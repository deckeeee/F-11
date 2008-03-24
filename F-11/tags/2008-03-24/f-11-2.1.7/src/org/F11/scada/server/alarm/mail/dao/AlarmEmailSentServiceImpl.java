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

package org.F11.scada.server.alarm.mail.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.F11.scada.server.alarm.DataValueChangeEventKey;

public class AlarmEmailSentServiceImpl implements AlarmEmailSentService {
	private AlarmEmailSentDao alarmEmailSentDao;
	private AlarmEmailSentAddressesDao sentAddressesDao;
	
	public void setAlarmEmailSentDao(AlarmEmailSentDao alarmEmailSentDao) {
		this.alarmEmailSentDao = alarmEmailSentDao;
	}

	public void setSentAddressesDao(AlarmEmailSentAddressesDao sentAddressesDao) {
		this.sentAddressesDao = sentAddressesDao;
	}

	public void setAlarmEmailSent(DataValueChangeEventKey key,
			Collection addresses) {
		AlarmEmailSentDto insertAlarmEmailSentDto = getAlarmEmailSentDto(key);
		alarmEmailSentDao.insert(insertAlarmEmailSentDto);
		AlarmEmailSentDto alarmEmailSentDto = alarmEmailSentDao
				.getAlarmEmailSent(insertAlarmEmailSentDto);

		sentAddressesDao.insert(getSentAddressesDtos(alarmEmailSentDto, addresses));
	}

	private AlarmEmailSentDto getAlarmEmailSentDto(DataValueChangeEventKey key) {
		AlarmEmailSentDto dto = new AlarmEmailSentDto();
		dto.setProvider(key.getProvider());
		dto.setHolder(key.getHolder());
		dto.setSentdate(key.getTimeStamp());
		dto.setValue(key.getValue().booleanValue());
		return dto;
	}

	private List getSentAddressesDtos(AlarmEmailSentDto alarmEmailSentDto, Collection addresses) {
		ArrayList dtos = new ArrayList();
		for (Iterator i = addresses.iterator(); i.hasNext();) {
			String address = (String) i.next();
			AlarmEmailSentAddressesDto dto = new AlarmEmailSentAddressesDto();
			dto.setAlarmEmailSentId(alarmEmailSentDto.getAlarmEmailSentId());
			dto.setAddress(address);
			dtos.add(dto);
		}
		return dtos;
	}
}
