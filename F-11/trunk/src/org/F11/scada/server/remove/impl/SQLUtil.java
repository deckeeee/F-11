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

package org.F11.scada.server.remove.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.F11.scada.server.remove.RemoveDto;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;
import org.seasar.extension.jdbc.util.DatabaseMetaDataUtil;
import org.seasar.framework.container.S2Container;

public abstract class SQLUtil {
	public static String replace(RemoveDto dto, String srcsql) {
		String sql =
			srcsql.replaceAll("\\$table", dto.getTableName()).replaceAll(
				"\\$datefield",
				dto.getDateFieldName());
		return sql;
	}

	public static String getDatabaseProductName(S2Container container) {
		DataSource dataSource =
			(DataSource) container.getComponent(DataSource.class);
		return getDbmsName(dataSource);
	}

	public static String getDbmsName(DataSource dataSource) {
		String dbms = null;
		Connection con = DataSourceUtil.getConnection(dataSource);
		try {
			DatabaseMetaData dmd = ConnectionUtil.getMetaData(con);
			dbms = DatabaseMetaDataUtil.getDatabaseProductName(dmd);
		} finally {
			ConnectionUtil.close(con);
		}
		return dbms;
	}

	public static String getSql(String sql, Object deleteDate) {
		return sql.replaceFirst("\\?", deleteDate.toString());
	}
}
