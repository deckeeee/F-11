/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.F11.scada.applet.ngraph.editor.TrendRuleSetTest;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class LoggingRuleSetTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParse() throws Exception {
		Digester digester = new Digester();
		digester.addRuleSet(new LoggingRuleSet());
		HashMap<String, Task> map = new HashMap<String, Task>();
		digester.push(map);

		BufferedReader xml =
			new BufferedReader(
				new InputStreamReader(
					TrendRuleSetTest.class
						.getResourceAsStream("/org/F11/scada/server/logging/Logging.xml"),
					"Windows-31J"));
		try {
			digester.parse(xml);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		assertEquals(8, map.size());

		Task task = map.get("log_table_minute_demand");
		assertEquals("log_table_minute_demand", task.getName());
		assertNull(task.getTables());

		task = map.get("log_table_minute");
		assertEquals("log_table_minute", task.getName());
		assertEquals("log_table_minute,log_table_minute_demand", task
			.getTables());

		List<Column> columns = task.getColumns();
		assertEquals(74, columns.size());

		Column column = columns.get(0);
		assertEquals(new Integer(0), column.getIndex());
		assertEquals("P1", column.getProvider());
		assertEquals("D_500_BcdSingle", column.getHolder());
	}
}
