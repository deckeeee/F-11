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

package org.F11.scada.server.io.postgresql.padding;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.F11.scada.server.io.postgresql.PostgreSQLUtility;
import org.F11.scada.server.register.HolderString;
import org.apache.log4j.Logger;

/**
 * レコード補完ロジックの基底クラスです。
 *
 * @author maekawa
 *
 */
public abstract class AbstractPaddingLogic implements PaddingLogic {
	private final Logger logger = Logger.getLogger(AbstractPaddingLogic.class);
	/** SQLユーティリティー */
	private final PostgreSQLUtility utility;

	public AbstractPaddingLogic(PostgreSQLUtility utility) {
		this.utility = utility;
	}

	public void insertPadding(
			Connection con,
			String table,
			List<HolderString> holderList,
			Timestamp timestamp) throws SQLException {
		String sql = "SELECT MAX(f_date) AS maxdate FROM " + table;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = con.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				Timestamp maxTime = rs.getTimestamp("maxdate"); //DBの最新タイムスタンプ
				Timestamp beforeTime = beforeTime(timestamp); //現在ロギングのタイムスタンプの一つ前
				if (!rs.wasNull() && beforeTime.after(maxTime)) {
					insert(con, table, holderList, beforeTime, maxTime);
				}
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
		}
	}

	private void insert(
			Connection con,
			String table,
			List<HolderString> holderList,
			Timestamp timestamp,
			Timestamp maxTime) throws SQLException {
		logger.info("insert:" + "TABLE = " + table + " " + afterTime(maxTime) + "～" + timestamp);
		Statement st = null;
		try {
			st = con.createStatement();
			for (Timestamp t = afterTime(maxTime); t.compareTo(timestamp) <= 0; t =
				afterTime(t)) {
				String sql = utility.getPaddingSql(table, holderList, t, 0);
//				logger.info(sql);
				st.execute(sql);
			}
		} finally {
			if (st != null) {
				st.close();
			}
		}
	}
}
