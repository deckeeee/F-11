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

package org.F11.scada.server.invoke;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.F11.scada.server.io.StrategyUtility;
import org.F11.scada.util.ConnectionUtil;
import org.apache.log4j.Logger;

public class StringUtil {
	private static Logger log = Logger.getLogger(StringUtil.class);
	/** ポイント名称文字列変換 開始文字列 */
	private static final String POINT_NAME_BRA = "$(";
	/** ポイント名称文字列変換 終了文字列 */
	private static final String POINT_NAME_CKET = ")";
	/** ポイント名称 セパレータ文字列 */
	private static final int POINT_NAME_SEPA = '_';

	public String replaceAllPointName(String src) {
		StringBuffer sb = new StringBuffer();
		int sp = src.indexOf(POINT_NAME_BRA);
		if (sp < 0)
			return src;

		int cp = 0;
		while (0 <= sp) {
			sb.append(src.substring(cp, sp));
			sp += 2;
			int ep = src.indexOf(POINT_NAME_CKET, sp);
			if (ep < 0)
				break;

			sb.append(findPointName(src.substring(sp, ep)));
			cp = ep + 1;
			sp = src.indexOf(POINT_NAME_BRA, cp);
		}
		sb.append(src.substring(cp));
		return sb.toString();
	}

	private String findPointName(String tag) {
		String pointName = replacePointName(tag);
		return pointName;
	}

	private String replacePointName(String tag) {
		int p = tag.indexOf(POINT_NAME_SEPA);
		if (p < 0)
			throw new NoSuchElementException(tag);

		int no = Integer.parseInt(tag.substring(0, p));
		String key = tag.substring(p + 1);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility utility = new StrategyUtility();
			stmt =
				con.prepareStatement(utility
					.getPrepareStatement("/pointtable/read"));
			stmt.setInt(1, no);
			rs = stmt.executeQuery();
			if (!rs.next()) {
				throw new NoSuchElementException(tag);
			}

			String ret = rs.getString(key);

			return ret;
		} catch (SQLException e) {
			log.error("ポイント変換エラー : " + tag, e);
			return POINT_NAME_BRA + tag + POINT_NAME_CKET;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}
}
