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

package org.F11.scada.tool.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import junit.framework.TestCase;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.WifeUtilities;

/**
 * PageListDAO のテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageListDAOTest extends TestCase {
	/** テストデータベースコネクション */
	private Connection con;

	/**
	 * Constructor for PageListDAOTest.
	 * @param arg0
	 */
	public PageListDAOTest(String arg0) {
		super(arg0);
		try {
			Class.forName(WifeUtilities.getJdbcDriver());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		String url = "jdbc:"
			+ EnvironmentManager.get("/server/jdbc/dbmsname", "")
			+ "://"
			+ EnvironmentManager.get("/server/jdbc/servername", "")
			+ "/"
			+ "test" + EnvironmentManager.get("/server/jdbc/dbname", "");
		con = DriverManager.getConnection(url, "wifeuser", "wifeuser");

		Statement st = null;
		try {
			st = con.createStatement();
			st.execute(
				"DROP TABLE page_define_table");
		} catch (SQLException e) {
		} finally {
			if (st != null) {
				st.close();
			}
		}
		try {
			st = con.createStatement();
			st.execute(
				"CREATE TABLE page_define_table (page_name VARCHAR(100)" +				" NOT NULL,page_xml_path text,PRIMARY KEY (page_name))");
		} finally {
			if (st != null) {
				st.close();
			}
		}
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		if (con != null) {
			con.close();
		}
	}

	/**
	 * 3件挿入した状態でのテスト。
	 * @throws Exception
	 */
	public void testGetPageList() throws Exception {
		Statement st = null;
		PageListDAO dao = null;
		try {
			st = con.createStatement();
			st.execute("INSERT INTO page_define_table (page_name, page_xml_path) " +				"VALUES('空調平面図', 'pagedefine/XWifeAppletDefine.xml')");
			st.execute("INSERT INTO page_define_table (page_name, page_xml_path) " +				"VALUES('トレンドグラフ', 'pagedefine/trend.xml')");
			st.execute("INSERT INTO page_define_table (page_name, page_xml_path) " +				"VALUES('空調平面図2', 'pagedefine/2/XWifeAppletDefine.xml')");
			dao = new PageListDAO(con);
			Collection c = dao.getPageList();
			assertEquals(3, c.size());
			Iterator it = c.iterator();
			PageListBean b = (PageListBean) it.next();
			assertEquals("トレンドグラフ", b.getPageName());
			assertEquals("pagedefine/trend.xml", b.getPageXmlPath());
			b = (PageListBean) it.next();
			assertEquals("空調平面図", b.getPageName());
			assertEquals("pagedefine/XWifeAppletDefine.xml", b.getPageXmlPath());
			b = (PageListBean) it.next();
			assertEquals("空調平面図2", b.getPageName());
			assertEquals("pagedefine/2/XWifeAppletDefine.xml", b.getPageXmlPath());
		} finally {
			if (st != null) {
				st.close();
			}
			if (dao != null) {
				dao.close();
			}
		}
	}

	/**
	 * page_define_table が存在しない時のテスト
	 * @throws Exception
	 */
	public void testEmptyList() throws Exception {
		Statement st = null;
		try {
			st = con.createStatement();
			st.execute(
				"DROP TABLE page_define_table");
		} catch (SQLException e) {
		} finally {
			if (st != null) {
				st.close();
			}
		}

		PageListDAO dao = null;
		try {
			dao = new PageListDAO(con);
			Collection c = dao.getPageList();
			assertEquals(0, c.size());
			assertSame(Collections.EMPTY_LIST, c);
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
	}

	/**
	 * page_define_tableは存在するが0件の時のテスト
	 * @throws Exception
	 */	
	public void testZeroSizeList() throws Exception {
		Statement st = null;
		PageListDAO dao = null;
		try {
			dao = new PageListDAO(con);
			Collection c = dao.getPageList();
			assertEquals(0, c.size());
			assertNotSame(Collections.EMPTY_LIST, c);
		} finally {
			if (st != null) {
				st.close();
			}
			if (dao != null) {
				dao.close();
			}
		}
	}
}
