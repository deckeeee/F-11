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
package org.F11.scada.server.io.postgresql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.server.dao.DatabaseMetaDataUtil;
import org.F11.scada.server.io.DataSelector;
import org.F11.scada.server.io.SelectHandler;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.util.ConnectionUtil;
import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.log4j.Logger;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class PostgreSQLDataSelector implements DataSelector {
    private static Logger logger = Logger.getLogger(PostgreSQLDataSelector.class);
    private Calendar cal = Calendar.getInstance();

    /* (non-Javadoc)
     * @see org.F11.scada.server.io.DataSelector#getSelectData(java.lang.String, java.util.List, java.lang.String, java.util.Map)
     */
    public List getSelectData(String name, List dataHolders, String sql,
            Map converValueMap) throws SQLException {
        Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList resultList = new ArrayList();
		try {
			con = ConnectionUtil.getConnection();
			stmt = con.createStatement();
			DatabaseMetaData metaData = con.getMetaData();
			rs = DatabaseMetaDataUtil.getTables(metaData, "", "", name, null);
			// ÉeÅ[ÉuÉãÇ™ë∂ç›Ç∑ÇÈÇ©í≤ç∏
			ResultSetMetaData rsMeta = rs.getMetaData();
			rs.last();
			int rowCount = rs.getRow();
			if (rowCount <= 0) {
				rs.close();
				logger.info(SelectHandler.TABLE_NOT_FOUND);
				return Collections.EMPTY_LIST;
			}
			rs.close();

			rs = stmt.executeQuery(sql);
			rsMeta = rs.getMetaData();
			resultList = new ArrayList(rowCount);
			while (rs.next()) {
				ArrayDoubleList values = new ArrayDoubleList(rsMeta.getColumnCount());
				for (Iterator it = dataHolders.iterator(); it.hasNext();) {
				    HolderString hs = (HolderString) it.next();
				    ConvertValue conv = (ConvertValue) converValueMap.get(hs);
				    String columnName = "f_" + hs.getProvider() + "_" + hs.getHolder();
					values.add(conv.convertDoubleValue(getDouble(rs, columnName)));
				}
				resultList.add(
					new LoggingData(
						getTimestamp(rs),
						values));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (con != null) {
				con.close();
			}
		}
		
		return resultList;
    }
    
    private double getDouble(ResultSet rs, String columnName) throws SQLException {
    	try {
			return rs.getDouble(columnName);
		} catch (SQLException e) {
			return rs.getBoolean(columnName) ? 1D : 0D;
		}
    }

    private Timestamp getTimestamp(ResultSet rs) throws SQLException {
        cal.setTimeInMillis(rs.getTimestamp(PostgreSQLUtility.DATE_FIELD_NAME).getTime());
        cal.set(Calendar.MILLISECOND, 0);
        
        return new Timestamp(cal.getTimeInMillis());
    }
}
