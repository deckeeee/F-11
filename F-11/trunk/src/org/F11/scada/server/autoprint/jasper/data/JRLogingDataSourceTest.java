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

package org.F11.scada.server.autoprint.jasper.data;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.test.util.TestUtil;
import org.F11.scada.test.util.TimestampUtil;
import org.F11.scada.util.ConnectionUtil;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import dori.jasper.engine.design.JRDesignField;

/**
 * JRLogingDataSourceのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class JRLogingDataSourceTest extends DatabaseTestCase {
	private static final String BASE = "src/org/F11/scada/server/autoprint/jasper/data/";
	private static final String SQL = "SELECT * FROM log_table_daily";

	/**
	 * Constructor for JRLogingDataSourceTest.
	 * @param arg0
	 */
	public JRLogingDataSourceTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		DataProvider dp = TestUtil.createDataProvider();
		Manager.getInstance().addDataProvider(dp);
	}

	protected void tearDown() throws Exception {
		DataProvider dp = Manager.getInstance().getDataProvider("P1");
		Manager.getInstance().removeDataProvider(dp);
		super.tearDown();
	}

	protected IDatabaseConnection getConnection() throws Exception {
		return new DatabaseConnection(ConnectionUtil.getConnection());
	}

	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(new FileInputStream(BASE + "log_table_daily.xml"));
	}

	public void testGetFieldValue() throws Exception {
		Connection con = getConnection().getConnection();
		PreparedStatement st = con.prepareStatement(SQL + " WHERE f_date BETWEEN ? AND ?");
		st.setTimestamp(1, TimestampUtil.parse("2004/05/19 00:01:00"));
		st.setTimestamp(2, TimestampUtil.parse("2004/05/19 23:01:00"));
		ResultSet rs = st.executeQuery();

		JRLogingDataSource src = new JRLogingDataSource(rs);
		assertTrue(src.next());
		JRDesignField f = new JRDesignField();
		f.setName("f_date");
		f.setValueClassName("java.sql.Timestamp");
		assertEquals(TimestampUtil.parse("2004/05/19 00:01:00"), src.getFieldValue(f));
		f.setName("f_revision");
		f.setValueClassName("java.lang.Integer");
		assertEquals(new Integer(0), src.getFieldValue(f));
		f.setName("f_P1_D_500_BcdSingle");
		f.setValueClassName("java.lang.Double");
		assertEquals(new Double(50.00), src.getFieldValue(f));
		f.setName("f_P1_D_501_BcdSingle");
		assertEquals(new Double(52.50), src.getFieldValue(f));
		f.setName("f_P1_D_502_BcdSingle");
		assertEquals(new Double(55.00), src.getFieldValue(f));
		f.setName("f_P1_D_503_BcdSingle");
		assertEquals(new Double(57.50), src.getFieldValue(f));
		f.setName("f_P1_D_504_BcdSingle");
		assertEquals(new Double(60.00), src.getFieldValue(f));
		f.setName("f_P1_D_505_BcdSingle");
		assertEquals(new Double(62.50), src.getFieldValue(f));
		f.setName("f_P1_D_506_BcdSingle");
		assertEquals(new Double(65.00), src.getFieldValue(f));
		f.setName("f_P1_D_507_BcdSingle");
		assertEquals(new Double(67.50), src.getFieldValue(f));
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertTrue(src.next());
		assertFalse(src.next());
	}
}
