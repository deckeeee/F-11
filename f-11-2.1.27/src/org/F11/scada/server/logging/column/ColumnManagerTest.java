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
package org.F11.scada.server.logging.column;

import junit.framework.TestCase;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ColumnManagerTest extends TestCase {
	ColumnManager manager;

	/**
	 * Constructor for ColumnManagerTest.
	 * @param arg0
	 */
	public ColumnManagerTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		manager = new ColumnManager("LoggingTest.xml");
	}

	public void testGetColumnIndex() {
		try {
			manager.getColumnIndex("", "", "");
			fail();
		} catch (IllegalStateException ex) {
		}
		try {
			manager.getColumnIndex("log_table_minute", "", "");
			fail();
		} catch (IllegalStateException ex) {
		}
		try {
			manager.getColumnIndex("log_table_minute", "P1", "");
			fail();
		} catch (IllegalStateException ex) {
		}
		assertEquals(0, manager.getColumnIndex("log_table_minute", "P1", "D_500_BcdSingle"));
		assertEquals(1, manager.getColumnIndex("log_table_minute", "P1", "D_501_BcdSingle"));

		assertEquals(0, manager.getColumnIndex("log_table_hour", "P1", "D_500_BcdSingle"));
		assertEquals(1, manager.getColumnIndex("log_table_hour", "P1", "D_501_BcdSingle"));

		assertEquals(0, manager.getColumnIndex("log_table_daily", "P1", "D_500_BcdSingle"));
		assertEquals(1, manager.getColumnIndex("log_table_daily", "P1", "D_501_BcdSingle"));
	}

}
