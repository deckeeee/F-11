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

package org.F11.scada.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.WifeUtilities;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * データベースコネクションユーティリティクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class ConnectionUtil {
	/** データソースの参照 */
	private static BasicDataSource dataSource;
	/** データソースの初期化 */
	static {
		dataSource = new BasicDataSource();
		dataSource.setDriverClassName(WifeUtilities.getJdbcDriver());
		dataSource.setUrl(WifeUtilities.createJdbcUri());
		dataSource.setUsername(EnvironmentManager.get("/server/jdbc/username", ""));
		dataSource.setPassword(EnvironmentManager.get("/server/jdbc/password", ""));
		dataSource.setMaxActive(200);
		dataSource.setMaxIdle(50);
		dataSource.setMaxWait(5000);
//		dataSource.setPoolPreparedStatements(true);
	}
	
	private ConnectionUtil() {}

	/**
	 * コネクションプーリングからデータソースからコネクションを返します。
	 * @return データベースコネクション。
	 */
	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
