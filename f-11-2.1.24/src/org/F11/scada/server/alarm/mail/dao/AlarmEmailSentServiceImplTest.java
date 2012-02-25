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
import java.util.List;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.test.util.TimestampUtil;
import org.seasar.dao.unit.S2DaoTestCase;

public class AlarmEmailSentServiceImplTest extends S2DaoTestCase {
	AlarmEmailSentService service;
	AlarmEmailSentDao alarmEmailSentDao;
	AlarmEmailSentAddressesDao addressesDao;

	public AlarmEmailSentServiceImplTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("AlarmEmailSentServiceImplTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSetAlarmEmailSentTx() throws Exception {
		DataValueChangeEventKey key = new DataValueChangeEventKey(1, "P1",
				"H1", Boolean.TRUE, TimestampUtil.parse("2006/01/01 01:00:00"));
		Collection addresses = getAddresses();
		service.setAlarmEmailSent(key, addresses);
		
		AlarmEmailSentDto alarmEmailSentDto =
			alarmEmailSentDao.getAlarmEmailSent(getAlarmEmailSent(key));
		assertNotNull(alarmEmailSentDto);
		assertEquals("P1", alarmEmailSentDto.getProvider());
		assertEquals("H1", alarmEmailSentDto.getHolder());
		assertEquals(TimestampUtil.parse("2006/01/01 01:00:00"), alarmEmailSentDto.getSentdate());
		assertEquals(true, alarmEmailSentDto.isValue());
		
		List sentAddresses = addressesDao
				.getAlarmEmailSentAddresses(alarmEmailSentDto
						.getAlarmEmailSentId());
		assertNotNull(sentAddresses);
		assertEquals(5, sentAddresses.size());
		AlarmEmailSentAddressesDto addressesDto =
			(AlarmEmailSentAddressesDto) sentAddresses.get(0);
		assertEquals("bar@example.com", addressesDto.getAddress());
		addressesDto =
			(AlarmEmailSentAddressesDto) sentAddresses.get(1);
		assertEquals("foo@example.com", addressesDto.getAddress());
		addressesDto =
			(AlarmEmailSentAddressesDto) sentAddresses.get(2);
		assertEquals("foo@example.com", addressesDto.getAddress());
	}

	private AlarmEmailSentDto getAlarmEmailSent(DataValueChangeEventKey key) {
		AlarmEmailSentDto dto = new AlarmEmailSentDto();
		dto.setProvider(key.getProvider());
		dto.setHolder(key.getHolder());
		dto.setSentdate(key.getTimeStamp());
		dto.setValue(true);
		return dto;
	}

	private ArrayList getAddresses() {
		ArrayList addresses = new ArrayList();
		addresses.add("hoge@example.com");
		addresses.add("foo@example.com");
		addresses.add("bar@example.com");
		return addresses;
	}
}
