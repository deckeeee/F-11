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

package org.F11.scada.server.alarm.table.postgresql;

import java.sql.Timestamp;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.table.AlarmIndividualSettingDao;
import org.F11.scada.server.alarm.table.AlarmIndividualSettingDto;
import org.F11.scada.server.alarm.table.SoundStrategy;
import org.seasar.dao.unit.S2DaoTestCase;

public class SoundStrategyImplTest extends S2DaoTestCase {
	private SoundStrategy strategy;
	private AlarmIndividualSettingDao dao;

	public SoundStrategyImplTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("SoundStrategyImplTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetSoundTypeNoDbData() throws Exception {
		DataValueChangeEventKey key = new DataValueChangeEventKey(0, "P1",
				"H1", Boolean.FALSE, new Timestamp(System.currentTimeMillis()));
		assertEquals(new Integer(0), strategy.getSoundType(0, key));
		assertEquals(new Integer(1), strategy.getSoundType(1, key));
	}
	
	public void testGetSoundTypeWithDbDataTx() throws Exception {
		AlarmIndividualSettingDto dto = new AlarmIndividualSettingDto();
		dto.setProvider("P1");
		dto.setHolder("H1");
		dto.setType(1);
		assertEquals(1, dao.insertAlarmIndividualSetting(dto));

		DataValueChangeEventKey key = new DataValueChangeEventKey(0, "P1",
				"H1", Boolean.FALSE, new Timestamp(System.currentTimeMillis()));
		assertEquals(new Integer(1), strategy.getSoundType(0, key));
		assertEquals(new Integer(1), strategy.getSoundType(1, key));
	}
}
