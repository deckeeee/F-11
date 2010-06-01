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

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.test.util.TestConnectionUtil;
import org.F11.scada.test.util.TimestampUtil;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * AlarmPrintDAOImplのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmPrintDAOImplTest extends DatabaseTestCase {
	private static final String PATH =
		"org/F11/scada/server/alarm/print/AlarmPrintDAOImplTest.dicon";
	private AlarmPrintDAO dao;

	/**
	 * Constructor for AlarmPrintDAOImplTest.
	 * @param arg0
	 */
	public AlarmPrintDAOImplTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
	    try {
	        Connection con = TestConnectionUtil.getTestConnection();
	        IDatabaseConnection dbCon = new DatabaseConnection(con);
	        IDataSet set = dbCon.createDataSet();
	        FlatXmlDataSet.write(set, new FileOutputStream("all.xml"));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		this.dao = new AlarmPrintDAOImpl(PATH);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	protected IDatabaseConnection getConnection() throws Exception {
		return new DatabaseConnection(TestConnectionUtil.getTestConnection());
	}

	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(new FileInputStream("src/org/F11/scada/server/alarm/print/career_print_temp.xml"));
//		return new FlatXmlDataSet(new FileInputStream("src/org/F11/scada/server/alarm/print/all.xml"));
	}

	public void testFindAll() throws Exception {
		List list = this.dao.findAll();
		assertEquals(2, list.size());
		PrintLineData data = (PrintLineData) list.get(0);
		assertEquals("2004-01-01 01:00:00.0  AHU-1-1  1F A室 空調機  警報  発生  ", data.toString());
		assertEquals(Color.RED, data.getColor());
		data = (PrintLineData) list.get(1);
		assertEquals("2004-01-01 02:00:00.0  AHU-1-1  1F A室 空調機  警報  復旧  ", data.toString());
		assertEquals(Color.BLACK, data.getColor());
	}

	public void testInsert() throws Exception {
		DataValueChangeEventKey key =
			new DataValueChangeEventKey(0, "P1", "D_1900000_Digital", Boolean.TRUE, TimestampUtil.parse("2000/01/01 03:00:00"));
		this.dao.insert(key);
		PrintLineData data = dao.find(key);
		assertEquals("2000-01-01 03:00:00.0  AHU-1-1  1F A室 空調機  警報  発生  ", data.toString());
		assertEquals(Color.RED, data.getColor());
	}

	public void testFind() throws Exception {
		DataValueChangeEventKey key =
			new DataValueChangeEventKey(0, "P1", "D_1900000_Digital", Boolean.TRUE, TimestampUtil.parse("2000/01/01 01:00:00"));
		PrintLineData data = this.dao.find(key);
		assertEquals("2004-01-01 01:00:00.0  AHU-1-1  1F A室 空調機  警報  発生  ", data.toString());
		assertEquals(Color.RED, data.getColor());

		key =
			new DataValueChangeEventKey(0, "P1", "D_1900000_Digital", Boolean.FALSE, TimestampUtil.parse("2000/01/01 02:00:00"));
		data = this.dao.find(key);
		assertEquals("2004-01-01 02:00:00.0  AHU-1-1  1F A室 空調機  警報  復旧  ", data.toString());
		assertEquals(Color.BLACK, data.getColor());
	}

	public void testDeleteAll() throws Exception {
		this.dao.deleteAll();
		List list = this.dao.findAll();
		assertEquals(0, list.size());
	}

	public void testIsAlarmPrint() throws Exception {
		DataValueChangeEventKey key =
			new DataValueChangeEventKey(0, "P1", "D_1900000_Digital", Boolean.TRUE, TimestampUtil.parse("2000/01/01 01:00:00"));
		assertTrue(this.dao.isAlarmPrint(key));
		key =
			new DataValueChangeEventKey(36, "P1", "D_500_BcdSingle", Boolean.TRUE, TimestampUtil.parse("2000/01/01 01:00:00"));
		assertFalse(this.dao.isAlarmPrint(key));
		key =
			new DataValueChangeEventKey(8, "P1", "D_1900014_Digital", Boolean.TRUE, TimestampUtil.parse("2000/01/01 01:00:00"));
		assertFalse(this.dao.isAlarmPrint(key));
	}
}
