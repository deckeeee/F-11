package org.F11.scada.security.postgreSQL;

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

import org.apache.log4j.Logger;

/**
 * セキュリティーポリシー PostgreSQL データソースの選択。
 */
public class PostgreSQLUtilities {
	private static Logger logger = Logger.getLogger(PostgreSQLUtilities.class);

	private PostgreSQLUtilities() {}

	/**
	 * セキュリティポリシーの選択 SQL を返します。
	 * @return セキュリティポリシーの選択 SQL
	 */
	public static String createSelectString() {
		return "SELECT principal, name, permission FROM policy_define_table";
	}

	public static String createAuthenticationString(String user, String password) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT username, password FROM user_define_table");
		buffer.append(" WHERE ").append("username=").append("'").append(user).append("'");
		logger.debug(buffer.toString());
		return buffer.toString();
	}

	public static String createSelectGroupString(String user) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT g.groupname FROM user_define_table AS u, group_define_table AS g");
		buffer.append(" WHERE ").append("u.username=").append("'").append(user).append("'");
		buffer.append(" AND u.username=g.username");
		logger.debug(buffer.toString());
		return buffer.toString();
	}

	public static String createUpdatePassword(String user, String password) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("UPDATE user_define_table SET password=").append("'").append(password).append("'");
		buffer.append(" WHERE ").append("username=").append("'").append(user).append("'");
		return buffer.toString();
	}
}
