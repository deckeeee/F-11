/*
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
package org.F11.scada.xwife.server.io;

import junit.framework.TestCase;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DataProviderSQLUtilityTest extends TestCase {

	/**
	 * Constructor for DataProviderSQLUtilityTest.
	 * @param arg0
	 */
	public DataProviderSQLUtilityTest(String arg0) {
		super(arg0);
	}

	public void testCreateAllAlarmSQL() {
//		System.out.println(DataProviderSQLUtility.createAllAlarmSQL("P1"));
		String sql ="SELECT i.point, i.provider, i.holder, i.com_cycle, i.com_cycle_mode, i.com_memory_kinds, i.com_memory_address, s.bit_value, i.data_argv, i.off_delay FROM item_table as i, summary_table as s WHERE i.point=s.point AND i.provider=s.provider AND i.holder=s.holder AND i.provider='P1' AND i.data_type=0";
		assertEquals(sql, DataProviderSQLUtility.createAllAlarmSQL("P1"));
	}

	public void testCreateAlarmSQL() {
//		System.out.println(DataProviderSQLUtility.createAlarmSQL("P1"));
//		String sql = "SELECT point, provider, holder, com_cycle, com_cycle_mode, com_memory_kinds, com_memory_address, data_argv FROM item_table WHERE provider='P1' AND data_type=0 EXCEPT SELECT i.point, i.provider, i.holder, i.com_cycle, i.com_cycle_mode, i.com_memory_kinds, i.com_memory_address, i.data_argv FROM item_table as i, summary_table as s WHERE i.point=s.point AND i.provider=s.provider AND i.holder=s.holder AND i.provider='P1' AND i.data_type=0";
		String sql = "SELECT i.point, i.provider, i.holder, i.com_cycle, i.com_cycle_mode, i.com_memory_kinds, i.com_memory_address, i.data_argv, i.off_delay FROM item_table as i LEFT OUTER JOIN summary_table as s ON (i.point = s.point AND i.provider = s.provider AND i.holder = s.holder) WHERE i.provider = 'P1' AND i.data_type = 0 AND s.point IS NULL AND s.provider IS NULL AND s.holder IS NULL";
		assertEquals(sql, DataProviderSQLUtility.createAlarmSQL("P1"));
	}

	public void testCreateAnalogSQL() {
//		System.out.println(DataProviderSQLUtility.createAnalogSQL("P1"));
		String sql = "SELECT i.point, i.provider, i.holder, i.com_cycle, i.com_cycle_mode, i.com_memory_kinds, i.com_memory_address, i.data_type, i.data_argv, t.convert_min, t.convert_max, t.input_min, t.input_max, t.format, t.convert_type FROM item_table AS i, analog_type_table AS t WHERE i.provider='P1' AND i.data_type BETWEEN 1 AND 14 AND i.analog_type_id = t.analog_type_id";
		assertEquals(sql, DataProviderSQLUtility.createAnalogSQL("P1"));
	}

}
