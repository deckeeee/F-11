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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.F11.scada.server.entity.Item;
import org.F11.scada.server.frame.editor.TaskItemDefine;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.util.ConnectionUtil;
import org.seasar.framework.container.S2Container;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hori
 */
public class PointNameDataStore {
	private final StrategyUtility utility;
	/** ポイント名取得のパラメータ名です */
	public static final String PARA_NAME_POINT = "point";
	private final Map itemPool;
	private final ItemUtil util;

	/**
	 * 
	 */
	public PointNameDataStore() throws IOException {
		utility = new StrategyUtility();
		itemPool = new ConcurrentHashMap();
		S2Container container = S2ContainerUtil.getS2Container();
		util = (ItemUtil) container.getComponent("itemutil");
	}

	public TaskItemDefine getAnalogName(String providerName, String holderName) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		TaskItemDefine ret = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt =
				con.prepareStatement(
					utility.getPrepareStatement(
						"/tool/editor/analog/name/read"));
			stmt.setString(1, providerName);
			stmt.setString(2, holderName);
			rs = stmt.executeQuery();

			double verticalMinimum = 0;
			double verticalMaximum = 0;
			double verticalInputMinimum = 0;
			double verticalInputMaximum = 0;
			int pointNo = 0;
			String pointUnit = null;
			String pointName = null;
			String pointUnitMark = null;
			if (rs.next()) {
				verticalMinimum = rs.getDouble("convert_min");
				verticalMaximum = rs.getDouble("convert_max");
				verticalInputMinimum = rs.getDouble("input_min");
				verticalInputMaximum = rs.getDouble("input_max");
				pointNo = rs.getInt("point");
				pointUnit = rs.getString("unit");
				pointName = rs.getString("name");
				pointUnitMark = rs.getString("unit_mark");
			}
			ret =
				new TaskItemDefine(
					verticalMinimum,
					verticalMaximum,
					verticalInputMinimum,
					verticalInputMaximum,
					providerName + "_" +
					holderName,
					pointNo,
					pointUnit,
					pointName,
					pointUnitMark);
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

	public List getAnalogNameList(List dataHolderList) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List ret = new ArrayList();
		try {
			con = ConnectionUtil.getConnection();
			stmt =
				con.prepareStatement(
					utility.getPrepareStatement(
						"/tool/editor/analog/name/readpoint"));
			Item[] items = util.getItems(dataHolderList, itemPool);

			for (int i = 0; i < items.length; i++) {
				String provider = items[i].getProvider();
				String holder = items[i].getHolder();

				stmt.setInt(1, items[i].getPoint().intValue());
				stmt.setString(2, provider);
				stmt.setString(3, holder);
				rs = stmt.executeQuery();

				if (rs.next()) {
					ret.add(
						new TaskItemDefine(
							rs.getDouble("convert_min"),
							rs.getDouble("convert_max"),
							rs.getDouble("input_min"),
							rs.getDouble("input_max"),
							provider + "_" +
							holder,
							rs.getInt("point"),
							rs.getString("unit"),
							rs.getString("name"),
							rs.getString("unit_mark")));
				}
				rs.close();
				rs = null;
			}
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
