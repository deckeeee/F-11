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
 *
 */
package org.F11.scada.server.io;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.F11.scada.server.alarm.print.PrintLineData;
import org.F11.scada.util.ConnectionUtil;

/**
 * @author hori
 */
public class AlarmPrintDataStore {
	private final StrategyUtility utility;

	/**
	 * 
	 */
	public AlarmPrintDataStore() throws IOException {
		utility = new StrategyUtility();
	}

	public Timestamp getLastPrint() {
		Timestamp ret = new Timestamp(0);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt =
				con.prepareStatement(
					utility.getPrepareStatement("/alarm/print/lastdate/read"));
			rs = stmt.executeQuery();
			if (rs.next()) {
				ret = rs.getTimestamp("last_date");
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			con.close();
			con = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
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
		return ret;
	}

	public void setLastPrint(Timestamp time) {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt =
				con.prepareStatement(
					utility.getPrepareStatement(
						"/alarm/print/lastdate/update"));
			if (time == null) {
				System.err.println("time is null");
			}
			stmt.setTimestamp(1, time);
			stmt.executeUpdate();
			stmt.close();
			stmt = null;
			con.close();
			con = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					stmt = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
	}

	public int getPrintCount(Timestamp time) {
		int ret = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt =
				con.prepareStatement(
					utility.getPrepareStatement("/alarm/print/count"));
			stmt.setTimestamp(1, time);
			rs = stmt.executeQuery();
			if (rs.next()) {
				ret = rs.getInt("count");
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			con.close();
			con = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					stmt = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
		return ret;
	}

	public List getPrintList(Timestamp time) {
		List ret = new ArrayList();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt =
				con.prepareStatement(
					utility.getPrepareStatement("/alarm/print/list"));
			stmt.setTimestamp(1, time);
			rs = stmt.executeQuery();
			for (int i = 0; rs.next(); i++) {
				PrintLineData data =
					new PrintLineData(
						rs.getString("printer_color"),
						rs.getTimestamp("entrydate"),
						rs.getString("unit"),
						rs.getString("kikiname"),
						rs.getString("alarmname"),
						rs.getString("message"));
				ret.add(data);
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			con.close();
			con = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					stmt = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
		return ret;
	}
}
