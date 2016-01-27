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

package org.F11.scada.server.autoprint.jasper.iterator;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;

import org.F11.scada.util.ConnectionUtil;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

/**
 * MonthlyIteratorJDBCWrapperのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class MonthlyIteratorJDBCWrapperTest extends DatabaseTestCase {
	private static final String BASE = "src/org/F11/scada/server/autoprint/jasper/iterator/";
	private static final String TASK = "month";
	private static final String TABLE = "autoprint_property_table";

	/**
	 * Constructor for MonthlyIteratorJDBCWrapperTest.
	 * @param arg0
	 */
	public MonthlyIteratorJDBCWrapperTest(String arg0) {
		super(arg0);
	}

	protected IDatabaseConnection getConnection() throws Exception {
		return new DatabaseConnection(ConnectionUtil.getConnection());
	}

	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(new FileInputStream(BASE + "property.xml"));
	}
	
	public void testMonthlyIteratorJDBCWrapper() throws Exception {
		// プロパティーが設定されていない時
		MonthlyIteratorJDBCWrapper it = new MonthlyIteratorJDBCWrapper();
		assertNull(it.next());

		// 不正なプロパティーが設定されている時
		it = new MonthlyIteratorJDBCWrapper();
		it.setProperty(MonthlyIteratorJDBCWrapper.PROPERTY_TABLE_NAME, TABLE + "111");
		it.setProperty(MonthlyIteratorJDBCWrapper.PROPERTY_TASK_NAME, TASK + "111");
		assertNull(it.next());

		// auto_flag が 0 の場合は、nullを返す
		it = new MonthlyIteratorJDBCWrapper();
		it.setProperty(MonthlyIteratorJDBCWrapper.PROPERTY_TABLE_NAME, TABLE);
		it.setProperty(MonthlyIteratorJDBCWrapper.PROPERTY_TASK_NAME, TASK);
		assertNull(it.next());

		// 5時10分で設定している為、(徹夜していなければorz)翌日の5時10分が返される
		// 要するに5時10分以降にテストした時成功する。
		IDataSet id = new FlatXmlDataSet(new FileInputStream(BASE + "property2.xml"));
		DatabaseOperation.CLEAN_INSERT.execute(getConnection(), id);
		it = new MonthlyIteratorJDBCWrapper();
		it.setProperty(MonthlyIteratorJDBCWrapper.PROPERTY_TABLE_NAME, TABLE);
		it.setProperty(MonthlyIteratorJDBCWrapper.PROPERTY_TASK_NAME, TASK);
		Object obj = it.next();
		assertNotNull(obj);
		assertTrue((obj instanceof Date));
		Date date = (Date) obj;
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTime(date);
		assertEquals(1, cal.get(Calendar.DATE));
		assertEquals(5, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(10, cal.get(Calendar.MINUTE));
		
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.MONTH, 1);
		assertEquals(tomorrow.get(Calendar.YEAR), cal.get(Calendar.YEAR));
		assertEquals(tomorrow.get(Calendar.MONTH), cal.get(Calendar.MONTH));

		obj = it.next();
		assertNotNull(obj);
		assertTrue((obj instanceof Date));
		cal.setTime((Date) obj);
		assertEquals(1, cal.get(Calendar.DATE));
		assertEquals(5, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(10, cal.get(Calendar.MINUTE));
		
		tomorrow.add(Calendar.MONTH, 1);
		assertEquals(tomorrow.get(Calendar.YEAR), cal.get(Calendar.YEAR));
		assertEquals(tomorrow.get(Calendar.MONTH), cal.get(Calendar.MONTH));
	}
}
