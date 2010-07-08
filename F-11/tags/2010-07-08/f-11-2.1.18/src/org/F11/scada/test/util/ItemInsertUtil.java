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
import java.text.DecimalFormat;

import org.apache.log4j.Logger;

public class ItemInsertUtil {
	private static Logger log = Logger.getLogger(ItemInsertUtil.class);
	private DecimalFormat addressFmt = new DecimalFormat("00000");
	private DecimalFormat bitFmt = new DecimalFormat("00");
	
	public ItemInsertUtil() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ItemInsertUtil util = new ItemInsertUtil();
		try {
			util.insert(100);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void insert(int count) throws SQLException {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = getConnection();
			con.setAutoCommit(false);
			st = con.prepareStatement(createSql());

			int BIT_PEAR_WORD = 16;
			int maxAddress = count / BIT_PEAR_WORD;
			for (int i = 0, all = 0; i < maxAddress; i++) {
				for (int j = 0; j < BIT_PEAR_WORD; j++) {
					insert(i, j, st);
					all++;
					if (isCommit(i * j)) {
						con.commit();
					}
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

	private void insert(int i, int j, PreparedStatement st) throws SQLException {
		st.setInt(1, 0);
		st.setString(2, "P1");
		st.setString(3, "H" + addressFmt.format(i));
		st.setInt(4, 0);
		st.setBoolean(5, true);
		st.setInt(6, 0);
		st.setInt(7, i);
		st.setBoolean(8, false);
		st.setInt(9, 0);
		st.setInt(10, 0);
		st.setInt(11, 0);
		st.setString(12, bitFmt.format(j));
		st.setBoolean(13, true);
		st.execute();
	}
	
	private String createSql() {
		return "INSERT INTO item_table " +
				"(point, provider, holder, com_cycle, com_cycle_mode, com_memory_kinds, " +
				"com_memory_address, b_flag, message_id, attribute_id, data_type, data_argv, system) " +
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
}
