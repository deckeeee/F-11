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

import java.security.PermissionCollection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.F11.scada.security.PolicyMapFactory;
import org.F11.scada.security.WifePermission;
import org.F11.scada.security.WifePrincipal;
import org.F11.scada.util.ConnectionUtil;

/**
 * PostgreSQL �̃e�[�u�����g�p�����A�|���V�[�}�b�v�̎����ł��B
 */
public class PostgreSQLPolicyMap extends PolicyMapFactory {
	/** �|���V�[�}�b�v�ł� */
	private Map policyMap;

	/**
	 * �f�[�^�x�[�X�ւ̐ڑ��ƃ|���V�[�}�b�v�����������ăC���X�^���X�𐶐����܂��B
	 * @throws SQLException �f�[�^�x�[�X�ڑ��Ɏ��s�����ꍇ
	 */
	public PostgreSQLPolicyMap() {
		policyMap = new HashMap();
	}

	/**
	 * PostgreSQL ��ǂݍ��݂܂��B
	 * Principal ���L�[�� PermissionCollection ��l�ɂ��� HashMap �I�u�W�F�N�g�𐶐����܂��B
	 * @return �|���V�[ HashMap
	 */
	public Map createMap() {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		// create HashMap key:Principal value:PermissionCollection
		try {
			con = ConnectionUtil.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(PostgreSQLUtilities.createSelectString());
			while (rs.next()) {
				WifePrincipal principal = new WifePrincipal(rs.getString("principal"));
				WifePermission permission =
						new WifePermission(rs.getString("name"), rs.getString("permission"));
				policyMapPut(principal, permission);
			}
			rs.close();
			stmt.close();
			con.close();
		} catch (SQLException e) {
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
		return policyMap;
	}

	private void policyMapPut(WifePrincipal principal, WifePermission permission) {
		Object o = policyMap.get(principal);
		if (o == null) {
			PermissionCollection pc = permission.newPermissionCollection();
			pc.add(permission);
			policyMap.put(principal, pc);
		} else {
			PermissionCollection pc = (PermissionCollection) o;
			if (!pc.implies(permission)) {
				pc.add(permission);
			}
		}
	}
}
