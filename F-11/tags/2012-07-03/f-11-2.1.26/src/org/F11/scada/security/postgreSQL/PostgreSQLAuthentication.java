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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.F11.scada.security.WifePrincipal;
import org.F11.scada.security.auth.Authentication;
import org.F11.scada.security.auth.Subject;
import org.F11.scada.util.ConnectionUtil;

/**
 * postgreSQL �̃e�[�u�����g�p���āA���[�U�[�F�؂���N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PostgreSQLAuthentication implements Authentication {

	/**
	 * �f�[�^�x�[�X�ڑ������������āA���[�U�[�F�؃I�u�W�F�N�g�𐶐����܂��B
	 * @throws SQLException
	 */
	public PostgreSQLAuthentication() {
	}

	/**
	 * ���[�U�[�̔F�؂��s���܂��B
	 * �F�؂����������ꍇ�A�F�؂������[�U�[�̃v�����V�p�����֘A�Â��� Subject ��Ԃ��܂��B
	 * �F�؂Ɏ��s�����ꍇ�� null ��Ԃ��܂��B
	 * @param user �F�؂��郆�[�U�[��
	 * @param password �F�؂��郆�[�U�[�̈Í������ꂽ�p�X���[�h
	 * @return �F�؂����������ꍇ�A�F�؂������[�U�[�̃v�����V�p�����֘A�Â��� Subject ��Ԃ��܂��B
	 * �F�؂Ɏ��s�����ꍇ�� null ��Ԃ��܂��B
	 */
	public Subject checkAuthentication(String user, String password) {
		Subject ret = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs =
				stmt.executeQuery(
					PostgreSQLUtilities.createAuthenticationString(
						user,
						password));
			rs.last();
			if (rs.getRow() <= 0) {
				return null;
			}
			rs.beforeFirst();
			rs.next();
			String userName = rs.getString("username");
			String defpass = rs.getString("password");
			if (!password.equals(defpass)) {
				return null;
			}

			Set newPrincipal = new HashSet();
			newPrincipal.add(new WifePrincipal(userName));

			rs =
				stmt.executeQuery(
					PostgreSQLUtilities.createSelectGroupString(user));
			while (rs.next()) {
				newPrincipal.add(new WifePrincipal(rs.getString("groupname")));
			}
			ret = Subject.createSubject(newPrincipal, user);

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
				} catch (Exception e) {
				}
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
				stmt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}
		return ret;
	}
}
