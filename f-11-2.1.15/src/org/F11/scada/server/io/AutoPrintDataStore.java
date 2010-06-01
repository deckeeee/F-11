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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.util.ConnectionUtil;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * @author hori
 */
public class AutoPrintDataStore extends AbstractAutoPrintDataService {

	public AutoPrintDataStore() {
		super();
	}

	private String format(Timestamp t) {
		return DateFormatUtils.format(t, "yyyy/MM/dd HH:mm:ss");
	}

	public List getLoggingDataList(
			String tableName,
			Timestamp start,
			Timestamp end,
			List dataHolders) {

		logger.info("データ抽出 : " + format(start) + "～" + format(end));
		List ret = new ArrayList(dataHolders.size());

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			st =
				con.prepareStatement(
					utility.getPrepareStatement(
						"/auto/print/logging/read",
						tableName));
			st.setTimestamp(1, start);
			st.setTimestamp(2, end);
			rs = st.executeQuery();
			Map convertMap = util.createConvertValueMap(dataHolders, tableName);

			while (rs.next()) {
				StringBuffer b = new StringBuffer();
				b.append(getTimestamp(rs));
				for (Iterator it = dataHolders.iterator(); it.hasNext();) {
				    HolderString hs = (HolderString) it.next();
				    ConvertValue conv = (ConvertValue) convertMap.get(hs);
					b.append(conv.convertStringValue(rs.getDouble(getFieldName(hs))));
					if (it.hasNext()) {
						b.append(",");
					}
				}
				ret.add(b.toString());
			}
		} catch (Exception e) {
			logger.error("自動印字データ作成中にエラー発生 : ", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					st = null;
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

	private String getFieldName(HolderString hs) {
		return "f_" + hs.getProvider() + "_" + hs.getHolder();
	}

	private String getTimestamp(ResultSet rs) throws SQLException {
		return DateFormatUtils.format(rs.getTimestamp("f_date"), "yyyy/MM/dd,HH:mm:ss,");
	}
}
