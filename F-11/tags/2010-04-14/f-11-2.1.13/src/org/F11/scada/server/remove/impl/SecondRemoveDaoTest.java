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

package org.F11.scada.server.remove.impl;

import org.F11.scada.server.remove.RemoveDao;
import org.F11.scada.server.remove.RemoveDto;
import org.seasar.extension.unit.S2TestCase;

public class SecondRemoveDaoTest extends S2TestCase {
	RemoveDao dao;

	public SecondRemoveDaoTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		include("org/F11/scada/server/remove/impl/SecondRemoveDaoTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'org.F11.scada.server.remove.impl.RemoveDaoImpl.remove(RemoveDto)'
	 */
	public void testRemoveTx() throws Exception {
		RemoveDto dto = new RemoveDto();
		dto.setTableName("log_table_daily");
		dto.setDateFieldName("f_date");
		dto.setRemoveValue(315360000);
		assertEquals(0 ,dao.remove(dto));

		dto.setTableName("log_table_daily");
		dto.setDateFieldName("f_date");
		dto.setRemoveValue(10);
		assertEquals(24 ,dao.remove(dto));
	}
	
	public void testRemoveAlarmEmailTx() throws Exception {
		RemoveDto dto = new RemoveDto();
		dto.setTableName("alarm_email_sent_table");
		dto.setDateFieldName("sentdate");
		dto.setRemoveValue(315360000);
		assertEquals(0 ,dao.remove(dto));

		dto = new RemoveDto();
		dto.setTableName("alarm_email_sent_table");
		dto.setDateFieldName("sentdate");
		dto.setRemoveValue(10);
		assertEquals(2 ,dao.remove(dto));
	}
}
