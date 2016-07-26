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

package org.F11.scada.server.autoprint.jasper.iterator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import org.F11.scada.scheduling.DailyIterator;
import org.F11.scada.scheduling.ScheduleIterator;
import org.F11.scada.scheduling.SchedulerTask;
import org.F11.scada.util.BooleanUtil;
import org.F11.scada.util.ConnectionUtil;
import org.apache.log4j.Logger;

/**
 * DailyIterator �� JDBC ��菉�������郉�b�p�[�N���X�ł�
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DailyIteratorJDBCWrapper implements ScheduleIterator {
	static final String PROPERTY_TABLE_NAME = "propertyTable";
	static final String PROPERTY_TASK_NAME = "taskName";
	/** DailyIterator �̎Q�� */
	private DailyIterator dailyIterator;
	/** ���̃C�e���[�^�[�̃v���p�e�B�[ */
	private final Properties properties;
	/** Logging API */
	private static Logger log = Logger.getLogger(DailyIteratorJDBCWrapper.class);

	/**
	 * �f�t�H���g�R���X�g���N�^
	 */
	public DailyIteratorJDBCWrapper() {
		this.properties = new Properties();
	}

	/**
	 * �e�[�u���ƃ^�X�N���w�肵�āA�C�e���[�^�����������܂��B
	 * @param key �v���p�e�B�[��
	 * @param value �v���p�e�B�[�l
	 */
	public void setProperty(String key, String value) {
		this.properties.setProperty(key, value);
	}

	/**
	 * ���� {@link SchedulerTask} �����s���������Ԃ��܂��B
	 * @return ���Ɏ��s��������B
	 */
	public Date next() {
		if (this.dailyIterator == null) {
			if (!resetIterator()) {
				return null;
			}
		}
		return this.dailyIterator.next();
	}

	private boolean resetIterator() {
		if (this.properties.containsKey(PROPERTY_TASK_NAME)
			&& this.properties.containsKey(PROPERTY_TABLE_NAME)) {
			try {
				this.dailyIterator =
					createDailyIterator(
						this.properties.getProperty(PROPERTY_TASK_NAME),
						this.properties.getProperty(PROPERTY_TABLE_NAME));
				return this.dailyIterator != null;
			} catch (SQLException e) {
	            log.error("Exception caught: ", e);
			}
		}
		return false;
	}

	private DailyIterator createDailyIterator(
			String taskName,
			String propertyTable)
			throws SQLException {
		Connection con = null;
		DailyIterator iterator = null;
		try {
			con = ConnectionUtil.getConnection();
			iterator = initDailyIterator(taskName, propertyTable, con);
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return iterator;
	}
	
	private DailyIterator initDailyIterator(
			String taskName,
			String propertyTable,
			Connection con)
			throws SQLException {

		String sql = "SELECT value FROM " + propertyTable + " WHERE task_name = ? AND property = 'auto_flag'";
		PreparedStatement st = null;
		ResultSet rs = null;
		DailyIterator iterator = null;
		try {
			st = con.prepareStatement(sql);
			st.setString(1, taskName);
			rs = st.executeQuery();
			rs.next();
			boolean auto = BooleanUtil.isBoolean(rs);
			if (auto) {
				rs.close();
				st.close();
				sql = "SELECT value FROM " + propertyTable + " WHERE task_name = ? AND property = 'hour'";
				st = con.prepareStatement(sql);
				st.setString(1, taskName);
				rs = st.executeQuery();
				rs.next();
				int hour = rs.getInt("value");

				rs.close();
				st.close();
				sql = "SELECT value FROM " + propertyTable + " WHERE task_name = ? AND property = 'minute'";
				st = con.prepareStatement(sql);
				st.setString(1, taskName);
				rs = st.executeQuery();
				rs.next();
				int minute = rs.getInt("value");
				iterator = new DailyIterator(hour, minute, 0);
			}
		} finally {
			if(rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
		}
		return iterator;
	}
}
