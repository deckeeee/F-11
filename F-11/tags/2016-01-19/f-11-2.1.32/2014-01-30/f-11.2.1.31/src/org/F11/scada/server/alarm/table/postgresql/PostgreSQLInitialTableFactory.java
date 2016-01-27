/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/postgresql/PostgreSQLInitialTableFactory.java,v 1.5.2.4 2006/08/16 08:53:05 frdm Exp $
 * $Revision: 1.5.2.4 $
 * $Date: 2006/08/16 08:53:05 $
 *
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
package org.F11.scada.server.alarm.table.postgresql;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.table.DefaultTableModel;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.alarm.AlarmException;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.server.alarm.table.InitialTableFactory;
import org.F11.scada.util.AlarmTableTitleUtil;
import org.F11.scada.util.ConnectionUtil;

/**
 * PostgreSQLを使用したテーブルモデルイニシャライザーです。 SQLFactorySQLFactory#RESOURCE_FILE
 * を使用して、テーブルのタイトルを生成、 SQLを展開して行データを生成します。
 *
 * このクラスは　final　宣言されています。クラスの機能を利用したい時は、 継承ではなく委譲モデルを使用して下さい。
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class PostgreSQLInitialTableFactory extends InitialTableFactory {
	private static final String NONCHECK = "NONCHECK";
	private static final String OCCURRENCE = "OCCURRENCE";
	public static final String SUMMARY = "SUMMARY";
	private static final String HISTORY = "HISTORY";
	private static final String CAREER = "CAREER";
	/** プロパティファイル名 */
	private static final String RESOURCE_FILE =
		"/resources/Sqldefine.properties";
	/** プロパティセット */
	private final Properties properties;
	private final String LIMIT_COUNT;

	public PostgreSQLInitialTableFactory() throws IOException {
		properties = new Properties();
		URL url = getClass().getResource(RESOURCE_FILE);
		if (url == null) {
			throw new IllegalStateException("resource file not found : "
				+ RESOURCE_FILE);
		}
		InputStream is = null;
		try {
			is = url.openStream();
			properties.load(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
		LIMIT_COUNT =
			"LIMIT " + EnvironmentManager.get("/server/alarm/maxrow", "5000");
	}

	private AlarmTableModel createTabelModel(
		String title,
		String sql,
		boolean isHistory,
		String tableName) throws SQLException {
		if (title == null) {
			throw new IllegalArgumentException("title is null.");
		}
		if (sql == null) {
			throw new IllegalArgumentException("sql is null.");
		}

		AlarmTableTitleUtil alarmTableTitleUtil = new AlarmTableTitleUtil();
		StringTokenizer tokenizer =
			new StringTokenizer(alarmTableTitleUtil.repraceStrings(title), ",");
		Object[] titleRow = new Object[tokenizer.countTokens()];
		HashMap<String, Integer> titleMap =
			new HashMap<String, Integer>(tokenizer.countTokens());

		for (int i = 0; tokenizer.hasMoreTokens(); i++) {
			String titleItem = tokenizer.nextToken().trim();
			titleRow[i] = titleItem;
			titleMap.put(titleItem, new Integer(i));
		}

		Object[][] data = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt =
				con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsMeta = rs.getMetaData();

			if (titleRow.length != rsMeta.getColumnCount()) {
				throw new IllegalStateException(
						"data count is different from title count. See `Sqldefine.properties' file. : "
							+ "title:"
							+ titleRow.length
							+ " data:"
							+ rsMeta.getColumnCount());
			}

			rs.last();
			data = new Object[rs.getRow()][rsMeta.getColumnCount()];

			rs.beforeFirst();
			for (int row = 0; rs.next(); row++) {
				for (int i = 1, column = 0, columnCount =
					rsMeta.getColumnCount(); i <= columnCount; i++, column++) {
					// System.out.println(rsMeta.getColumnClassName(i) + " : " +
					// rsMeta.getColumnType(i));
					// PostgreSQL, MySQLはこれでいけるもよう。
					switch (rsMeta.getColumnType(i)) {
					case Types.VARCHAR:
					case Types.LONGVARCHAR:
						data[row][column] = rs.getString(i);
						break;
					case Types.INTEGER:
						data[row][column] = new Integer(rs.getInt(i));
						break;
					case Types.DOUBLE:
						data[row][column] = new Double(rs.getDouble(i));
						break;
					case Types.FLOAT:
						data[row][column] = new Float(rs.getFloat(i));
						break;
					case Types.BOOLEAN:
					case Types.BIT:
					case Types.TINYINT:
						if (rs.wasNull()) {
							data[row][column] = null;
						} else {
							data[row][column] =
								Boolean.valueOf(rs.getBoolean(i));
						}
						break;
					case Types.TIMESTAMP:
						data[row][column] = rs.getTimestamp(i);
						break;
					default:
						data[row][column] = rs.getString(i);
						break;
					}
				}
			}
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					stmt = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					con = null;
				}
			}
		}

		if (isHistory) {
			return PostgreSQLAlarmTableModel.createHistoryAlarmTableModel(
					new DefaultTableModel(data, titleRow), titleMap, tableName);
		} else {
			return PostgreSQLAlarmTableModel.createDefaultAlarmTableModel(
					new DefaultTableModel(data, titleRow), titleMap, tableName);
		}
	}

	public AlarmTableModel createCareer() throws AlarmException {
		String title =
			properties.getProperty("/"
				+ WifeUtilities.getDBMSName()
				+ "/career/title");
		String sql =
			properties.getProperty(
					"/" + WifeUtilities.getDBMSName() + "/career/sql")
					.replaceFirst("\\$LIMIT", LIMIT_COUNT);
		try {
			return createTabelModel(title, sql, false, CAREER);
		} catch (SQLException e) {
			throw new AlarmException(e);
		}
	}

	public AlarmTableModel createHistory() throws AlarmException {
		String title =
			properties.getProperty("/"
				+ WifeUtilities.getDBMSName()
				+ "/history/title");
		String sql =
			properties.getProperty(
					"/" + WifeUtilities.getDBMSName() + "/history/sql")
					.replaceFirst("\\$LIMIT", LIMIT_COUNT);
		try {
			return createTabelModel(title, sql, true, HISTORY);
		} catch (SQLException e) {
			throw new AlarmException(e);
		}
	}

	public AlarmTableModel createSummary() throws AlarmException {
		String title =
			properties.getProperty("/"
				+ WifeUtilities.getDBMSName()
				+ "/summary/title");
		String sql =
			properties.getProperty(
					"/" + WifeUtilities.getDBMSName() + "/summary/sql")
					.replaceFirst("\\$LIMIT", "");
		try {
			return createTabelModel(title, sql, false, SUMMARY);
		} catch (SQLException e) {
			throw new AlarmException(e);
		}
	}

	public AlarmTableModel createOccurrence() throws AlarmException {
		String title =
			properties.getProperty("/"
				+ WifeUtilities.getDBMSName()
				+ "/occurrence/title");
		String sql =
			properties.getProperty(
					"/" + WifeUtilities.getDBMSName() + "/occurrence/sql")
					.replaceFirst("\\$LIMIT", LIMIT_COUNT);
		try {
			return createTabelModel(title, sql, false, OCCURRENCE);
		} catch (SQLException e) {
			throw new AlarmException(e);
		}
	}

	public AlarmTableModel createNoncheck() throws AlarmException {
		String title =
			properties.getProperty("/"
				+ WifeUtilities.getDBMSName()
				+ "/noncheck/title");
		String sql =
			properties.getProperty(
					"/" + WifeUtilities.getDBMSName() + "/noncheck/sql")
					.replaceFirst("\\$LIMIT", LIMIT_COUNT);
		try {
			return createTabelModel(title, sql, true, NONCHECK);
		} catch (SQLException e) {
			throw new AlarmException(e);
		}
	}
}
