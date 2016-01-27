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
 * �f�[�^�x�[�X�R�l�N�V�������[�e�B���e�B�N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class ConnectionUtil {
	/** �f�[�^�\�[�X�̎Q�� */
	private static BasicDataSource dataSource;
	/** �f�[�^�\�[�X�̏����� */
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
	 * �R�l�N�V�����v�[�����O����f�[�^�\�[�X����R�l�N�V������Ԃ��܂��B
	 * @return �f�[�^�x�[�X�R�l�N�V�����B
	 */
	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
