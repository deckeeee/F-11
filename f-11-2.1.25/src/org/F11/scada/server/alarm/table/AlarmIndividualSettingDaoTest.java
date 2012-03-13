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

package org.F11.scada.server.alarm.table;

import org.seasar.dao.unit.S2DaoTestCase;

public class AlarmIndividualSettingDaoTest extends S2DaoTestCase {
	private AlarmIndividualSettingDao dao;

	public AlarmIndividualSettingDaoTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("AlarmIndividualSettingDaoTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCRUDTx() throws Exception {
		AlarmIndividualSettingDto dto = new AlarmIndividualSettingDto();
		dto.setProvider("P1");
		dto.setHolder("H1");
		dto.setType(1);
		
		assertEquals(1, dao.insertAlarmIndividualSetting(dto));
		
		AlarmIndividualSettingDto selectDto =
			dao.getAlarmIndividualSetting("P1", "H1");
		assertEquals("P1" ,selectDto.getProvider());
		assertEquals("H1" ,selectDto.getHolder());
		assertEquals(1 ,selectDto.getType());
		
		selectDto.setType(0);
		assertEquals(1, dao.updateAlarmIndividualSetting(selectDto));
		selectDto =
			dao.getAlarmIndividualSetting("P1", "H1");
		assertEquals("P1" ,selectDto.getProvider());
		assertEquals("H1" ,selectDto.getHolder());
		assertEquals(0 ,selectDto.getType());
		
		assertEquals(1, dao.deleteAlarmIndividualSetting(selectDto));
		
		assertNull(dao.getAlarmIndividualSetting("P1", "H1"));
	}
}
