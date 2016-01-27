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

import org.F11.scada.test.util.TimestampUtil;
import org.seasar.dao.unit.S2DaoTestCase;

public class AlarmEmailSentDaoTest extends S2DaoTestCase {
	AlarmEmailSentDao dao;

	public AlarmEmailSentDaoTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("AlarmEmailSentDaoTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInsertSelectTx() throws Exception {
		AlarmEmailSentDto dto = new AlarmEmailSentDto();
		dto.setProvider("P1");
		dto.setHolder("H1");
		dto.setSentdate(TimestampUtil.parse("2006/01/01 02:02:03"));
		dto.setValue(true);
		
		assertEquals(1, dao.insert(dto));
		
		AlarmEmailSentDto selectDto = dao.getAlarmEmailSent(dto);
		assertNotNull(selectDto);
	}
}
