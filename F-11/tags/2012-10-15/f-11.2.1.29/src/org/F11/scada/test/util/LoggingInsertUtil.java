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

package org.F11.scada.test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.F11.scada.server.logging.column.ColumnManager;
import org.apache.log4j.Logger;

public class LoggingInsertUtil {
	private final ColumnManager manager;
	private static Logger log = Logger.getLogger(LoggingInsertUtil.class);

	public LoggingInsertUtil(ColumnManager manager) {
		this.manager = manager;
	}
	
	public void insertDummay(String tableName) throws SQLException {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = getConnection();
			con.setAutoCommit(false);
			st = con.prepareStatement(createSql(tableName));
			Calendar cal = Calendar.getInstance();
			cal.setTime(TimestampUtil.parse("2001/01/01 00:00:00"));
			for (int i = 0; i < 1025280; i++, cal.add(Calendar.MINUTE, 1)) {
				st.setTimestamp(1, getTimestamp(cal.getTime()));
				st.setInt(2, 0);
				setValues(tableName, st);
				st.execute();
				if (isCommit(i)) {
					con.commit();
				}
			}
			con.commit();
		} finally {
			con.rollback();
			if (st != null) {
				st.close();
			}
			if (con != null) {
				con.close();
			}
		}
	}

	private Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection con = DriverManager
				.getConnection(
						"jdbc:mysql://localhost/syouwa?useUnicode=true&amp;characterEncoding=Windows-31J&amp;zeroDateTimeBehavior=convertToNull",
						"root", "freedom9713");
		return con;
	}

	private boolean isCommit(int i) {
		boolean isCommit = (i != 0) && (i % 10000) == 0;
		if (isCommit) {
			log.info((i) + "Œ‘}“ü‚µ‚Ü‚µ‚½B");
		}
		return isCommit;
	}
	
	private void setValues(String tableName, PreparedStatement st) throws SQLException {
		Map taskMap = manager.getTaskMap();
		Map map = (Map) taskMap.get(tableName);
		for (int i = 0, size = map.size(); i < size; i++) {
			st.setDouble(i + 3, 0.0);
		}
	}

	private Timestamp getTimestamp(Date date) {
		return new Timestamp(date.getTime());
	}

	String createSql(String tableName) {
		Map taskMap = manager.getTaskMap();
		StringBuffer column = new StringBuffer(1000);
		StringBuffer value = new StringBuffer(1000);
		column.append("(");
		value.append(" VALUES(");
		column.append("f_date, f_revision, ");
		value.append("?, ?, ");
		Map map = (Map) taskMap.get(tableName);
		for (Iterator i = map.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			column.append("f_").append(key);
			value.append("?");
			if (i.hasNext()) {
				column.append(", ");
				value.append(", ");
			} else {
				column.append(")");
				value.append(")");
			}
		}
		
		return "INSERT INTO " + tableName + column.toString() + value.toString();
	}
	
	public static void main(String[] args) throws SQLException {
		LoggingInsertUtil util = new LoggingInsertUtil(new ColumnManager(
				"/org/F11/scada/test/util/Logging.xml"));
		util.insertDummay("log_table_MINUTE");
//		System.out.println(util.createSql("log_table_MINUTE"));
	}
}
