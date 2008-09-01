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

package org.F11.scada.server.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.F11.scada.server.autoprint.AutoPrintSchedule;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.server.logging.column.ColumnManager;
import org.F11.scada.util.ConnectionUtil;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

public abstract class AbstractAutoPrintDataService implements
		AutoPrintDataService {
	protected final Logger logger =
		Logger.getLogger(AbstractAutoPrintDataService.class);
	protected final StrategyUtility utility;
	protected final ItemUtil util;
	protected final Map itemPool;
	protected final ColumnManager columnManager;

	/**
	 * 
	 */
	public AbstractAutoPrintDataService() {
		utility = new StrategyUtility();
		S2Container container = S2ContainerUtil.getS2Container();
		util = (ItemUtil) container.getComponent("itemutil");
		itemPool = new ConcurrentHashMap();
		columnManager = new ColumnManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.server.io.AutoPrintDataService#getAutoPrintSchedules()
	 */
	public Map getAutoPrintSchedules() {
		Map ret = new HashMap();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt =
				con.prepareStatement(utility
					.getPrepareStatement("/auto/print/param/read"));
			rs = stmt.executeQuery();
			while (rs.next()) {
				String schedule = rs.getString("schedule");
				Calendar cal = Calendar.getInstance();
				Timestamp ts = rs.getTimestamp("paramdate");
				int startHour = rs.getInt("starthour");
				int startMinute = rs.getInt("startminute");
				if (ts != null)
					cal.setTimeInMillis(ts.getTime());
				else
					cal.clear();
				if (schedule.equals("DAILY")) {
					ret.put(rs.getString("name"), new AutoPrintSchedule.Daily(
						rs.getBoolean("auto_flag"),
						cal.get(Calendar.HOUR_OF_DAY),
						cal.get(Calendar.MINUTE),
						startHour,
						startMinute));
				} else if (schedule.equals("DAILY_ANALOG")) {
					ret.put(
						rs.getString("name"),
						new AutoPrintSchedule.DailyAnalog(rs
							.getBoolean("auto_flag"), cal
							.get(Calendar.HOUR_OF_DAY), cal
							.get(Calendar.MINUTE), startHour, startMinute));
				} else if (schedule.equals("MONTHLY")) {
					ret.put(
						rs.getString("name"),
						new AutoPrintSchedule.Monthly(
							rs.getBoolean("auto_flag"),
							cal.get(Calendar.DATE),
							cal.get(Calendar.HOUR_OF_DAY),
							cal.get(Calendar.MINUTE)));
				} else if (schedule.equals("YEARLY")) {
					ret.put(rs.getString("name"), new AutoPrintSchedule.Yearly(
						rs.getBoolean("auto_flag"),
						cal.get(Calendar.MONTH) + 1,
						cal.get(Calendar.DATE),
						cal.get(Calendar.HOUR_OF_DAY),
						cal.get(Calendar.MINUTE)));
				}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.io.AutoPrintDataService#setAutoPrintSchedule(java
	 * .lang.String, org.F11.scada.server.autoprint.AutoPrintSchedule)
	 */
	public void setAutoPrintSchedule(String name, AutoPrintSchedule schedule) {
		if (name == null || schedule == null)
			return;

		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt =
				con.prepareStatement(utility
					.getPrepareStatement("/auto/print/param/update"));
			stmt.setBoolean(1, schedule.isAutoOn());
			stmt.setTimestamp(2, schedule.getTimestamp());
			stmt.setString(3, name);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.io.AutoPrintDataService#getLoggingHeddarList(java
	 * .lang.String, java.util.List)
	 */
	public List<Map<String, String>> getLoggingHeddarList(
			String tableName,
			List dataHolders) {

		List<Map<String, String>> ret =
			new ArrayList<Map<String, String>>(dataHolders.size() + 1);
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt =
				con.prepareStatement(utility.getPrepareStatement(
					"/pointtable/read",
					tableName));
			Item[] items = util.getItems(dataHolders, itemPool);

			Item[] manageSortedItems =
				columnManager.sortLogging(items, tableName);

			for (int i = 0; i < manageSortedItems.length; i++) {
				stmt.setInt(1, manageSortedItems[i].getPoint().intValue());
				rs = stmt.executeQuery();
				if (rs.next()) {
					Map<String, String> record = new HashMap<String, String>();
					record.put("unit", rs.getString("unit"));
					record.put("name", rs.getString("name"));
					record.put("unit_mark", rs.getString("unit_mark"));
					record.put("attribute1", rs.getString("attribute1"));
					record.put("attribute2", rs.getString("attribute2"));
					record.put("attribute3", rs.getString("attribute3"));
					ret.add(record);
				}
				rs.close();
			}
			stmt.close();
			con.close();
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
				} catch (SQLException e1) {
					con = null;
				}
			}
		}
		return ret;
	}
}
