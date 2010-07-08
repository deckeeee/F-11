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

package org.F11.scada.server.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.F11.scada.util.ConnectionUtil;
import org.apache.log4j.Logger;
import org.seasar.framework.exception.SQLRuntimeException;

/**
 * DatabaseMetaData操作に関する、ユーティリティークラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
abstract public class DatabaseMetaDataUtil {
	private static Logger log = Logger.getLogger(DatabaseMetaDataUtil.class);

	public static ResultSet getTables(
			DatabaseMetaData metaData,
			String catalog,
			String schemaPattern,
			String tableNamePattern,
			String[] types) {
		try {
			String tableName = org.seasar.extension.jdbc.util.DatabaseMetaDataUtil
					.convertIdentifier(metaData, tableNamePattern);
			return metaData.getTables(catalog, schemaPattern, tableName, types);
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
	}

	/**
	 * 引数のテーブルが存在するか判定します。テーブルが存在する場合は、trueをしない場合はfalseを返します。
	 * 
	 * @param table 判定するテーブル
	 * @return テーブルが存在する場合は、trueをしない場合はfalseを返します。
	 */
	public static boolean existsTable(String table) {
		Connection con = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			DatabaseMetaData metaData = con.getMetaData();
			rs = getTables(metaData, "", "", table, null);
			rs.last();
			return rs.getRow() > 0;
		} catch (SQLException e) {
			log.fatal("metadata not accessable.", e);
			throw new SQLRuntimeException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getDatabaseProductName() {
		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			DatabaseMetaData metaData = con.getMetaData();
			return metaData.getDatabaseProductName();
		} catch (SQLException e) {
			log.fatal("metadata not accessable.", e);
			throw new SQLRuntimeException(e);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
