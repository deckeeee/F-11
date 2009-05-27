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

package org.F11.scada.test.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TestConnectionUtil {
	public static final TestConnectionUtil instance = new TestConnectionUtil();
	/** �f�[�^�\�[�X�̎Q�� */
	private BasicDataSource dataSource;

	/** �f�[�^�\�[�X�̏����� */
	private TestConnectionUtil() {
		dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUrl("jdbc:hsqldb:hsql://localhost/");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		dataSource.setMaxActive(200);
		dataSource.setMaxIdle(50);
		dataSource.setMaxWait(5000);
	}

	/**
	 * �R�l�N�V�����v�[�����O����f�[�^�\�[�X����R�l�N�V������Ԃ��܂��B
	 * @return �f�[�^�x�[�X�R�l�N�V�����B
	 */
	public static Connection getTestConnection() throws SQLException {
		return instance.dataSource.getConnection();
	}
}