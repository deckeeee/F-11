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
import java.util.List;

import org.seasar.dao.unit.S2DaoTestCase;

public class AlarmEmailSentAddressesDaoTest extends S2DaoTestCase {
	AlarmEmailSentAddressesDao dao;

	public AlarmEmailSentAddressesDaoTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("AlarmEmailSentAddressesDaoTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInsertAndSelectTx() throws Exception {
		AlarmEmailSentAddressesDto dto =
			new AlarmEmailSentAddressesDto();
		dto.setAlarmEmailSentId(0);
		dto.setAddress("hoge@example.com");
		AlarmEmailSentAddressesDto dto2 =
			new AlarmEmailSentAddressesDto();
		dto2.setAlarmEmailSentId(0);
		dto2.setAddress("foo@example.com");

		ArrayList dtos = new ArrayList();
		dtos.add(dto);
		dtos.add(dto2);
		
		assertEquals(2, dao.insert(dtos));

		List data = dao.getAlarmEmailSentAddresses(0);
		assertNotNull(data);
		assertEquals(3, data.size());
		AlarmEmailSentAddressesDto selectDto = (AlarmEmailSentAddressesDto) data.get(0);
		assertNotNull(selectDto);
		assertEquals("foo@example.com", selectDto.getAddress());
		selectDto = (AlarmEmailSentAddressesDto) data.get(1);
		assertNotNull(selectDto);
		assertEquals("hoge@example.com", selectDto.getAddress());
	}
}
