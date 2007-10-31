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
 */

package org.F11.scada.server.alarm.print;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.test.util.TimestampUtil;
import org.seasar.extension.unit.S2TestCase;

/**
 * AlarmPrintServiceのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmPrintServiceTest extends S2TestCase {
	private Timestamp ts;

	private static final String PATH =
		"AlarmPrintServiceTest.dicon";

	/**
	 * Constructor for AlarmPrintServiceTest.
	 * @param arg0
	 */
	public AlarmPrintServiceTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AlarmPrintServiceTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		ts = TimestampUtil.parse("2004/01/02 00:00:00");
		include(PATH);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAlarmPrintService() throws Exception {
		AlarmPrintDAOTestMock DAOMock = new AlarmPrintDAOTestMock();
		AlarmPrintService service =
			new AlarmPrintService(
				DAOMock,
				new AlarmPrinterTestMock());
		DataValueChangeEventKey key =
			new DataValueChangeEventKey(0, "P1", "D_1900000_Digital", Boolean.TRUE, ts);
		service.put(key);
		Thread.sleep(2000L);
		assertEquals(1, DAOMock.insertCount);
		assertEquals(1, DAOMock.deleteAllCount);
	}

	class AlarmPrintDAOTestMock implements AlarmPrintDAO {
		int deleteAllCount;
		int insertCount;
		
		public void deleteAll() throws SQLException {
			this.deleteAllCount++;
			System.out.println("Delete All.");
		}

		public PrintLineData find(DataValueChangeEventKey key)
			throws SQLException {
			return new PrintLineData("red", ts, "AHU-1-1-1", "空調ファン", "発停", "開始");
		}

		public List findAll() throws SQLException {
			ArrayList list = new ArrayList();
			for (int i = 0; i < 46; i++) {
				PrintLineData line =
					new PrintLineData(
						"red",
						TimestampUtil.parse("2004/01/01 00:00:00"),
						"AHU-1-1-" + i,
						"空調ファン",
						"発停",
						"開始");
				list.add(line);
			}

			return list;
		}

		public void insert(DataValueChangeEventKey key) throws SQLException {
			this.insertCount++;
			System.out.println("Insert " + key);
		}

		public boolean isAlarmPrint(DataValueChangeEventKey key) throws SQLException {
			return true;
		}
	}


	class AlarmPrinterTestMock implements AlarmPrinter {
		public void print(List data) {
			for (int i = 0, size = data.size(); i < size; i++) {
				System.out.println("Print data : " + data.get(i));
			}
		}
	}
}
