/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/postgresql/PostgreSQLStoreHandler.java,v 1.9.2.12 2007/04/13 01:47:14 frdm Exp $
 * $Revision: 1.9.2.12 $
 * $Date: 2007/04/13 01:47:14 $
 * 
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

package org.F11.scada.server.io.postgresql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.Service;
import org.F11.scada.server.dao.DatabaseMetaDataUtil;
import org.F11.scada.server.dao.MultiRecordDefineDao;
import org.F11.scada.server.entity.MultiRecordDefine;
import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.LoggingDataEventQueue;
import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.io.postgresql.padding.Daily;
import org.F11.scada.server.io.postgresql.padding.Hour;
import org.F11.scada.server.io.postgresql.padding.Minute;
import org.F11.scada.server.io.postgresql.padding.Monthly;
import org.F11.scada.server.io.postgresql.padding.Second;
import org.F11.scada.server.io.postgresql.padding.PaddingLogic;
import org.F11.scada.server.io.postgresql.padding.Yearly;
import org.F11.scada.server.logging.LoggingTask;
import org.F11.scada.util.ConnectionUtil;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

/**
 * �f�[�^�x�[�X�ɕۊǂ���A�f�[�^�i�����N���X�ł��B
 */
public class PostgreSQLStoreHandler implements Runnable, LoggingDataListener,
		Service {
	/** ���M���O�I�u�W�F�N�g */
	private static Logger logger;
	/** �g�p����f�o�C�X��(�e�[�u����) */
	private final String deviceName;
	/** �X���b�h�I�u�W�F�N�g */
	private Thread thread;
	/** �C�x���g�L���[ */
	private final LoggingDataEventQueue queue;
	/** SQL���[�e�B���e�B�[ */
	private final PostgreSQLUtility utility;
	/**  */
	private boolean tableCheck;

	private MultiRecordDefineDao dao_;

	private final Map<String, PaddingLogic> logicMap;

	/**
	 * �R���X�g���N�^
	 */
	public PostgreSQLStoreHandler(String deviceName) {
		logger = Logger.getLogger(getClass().getName());
		this.deviceName = deviceName;
		queue = new LoggingDataEventQueue();
		S2Container container = S2ContainerUtil.getS2Container();
		this.utility =
			(PostgreSQLUtility) container.getComponent(PostgreSQLUtility.class);
		this.dao_ =
			(MultiRecordDefineDao) container
				.getComponent(MultiRecordDefineDao.class);
		logicMap = createLogicMap();
		start();
	}

	private Map<String, PaddingLogic> createLogicMap() {
		HashMap<String, PaddingLogic> map = new HashMap<String, PaddingLogic>();
		map.put("MINUTE", new Minute(utility, 1));
		map.put("TENMINUTE", new Minute(utility, 10));
		map.put("HOUR", new Hour(utility));
		map.put("DAILY", new Daily(utility));
		map.put("MONTHLY", new Monthly(utility));
		map.put("YEARLY", new Yearly(utility));
		map.put("ONESECOND", new Second(utility, 1));
		map.put("QMINUTE", new Minute(utility, 15));
		map.put("FIVEMINUTE", new Minute(utility, 5));
		map.put("THIRTYMINUTE", new Minute(utility, 30));
		map.put("SIXTYMINUTE", new Minute(utility, 60));
		map.put("ONEHOURMONTHOUT", new Hour(utility));
		map.put("MONTHLYMONTHOUT", new Monthly(utility));
		map.put("GODA", new Minute(utility, 10));
		map.put("GODA01", new Minute(utility, 1));
		map.put("GODA05", new Minute(utility, 5));
		map.put("GODA10", new Minute(utility, 10));
		map.put("GODA30", new Minute(utility, 30));
		map.put("GODA60", new Minute(utility, 60));
		map.put("ONEMINUTE", new Minute(utility, 1));
		map.put("BMS", new Minute(utility, 1));
		map.put("MINUTEHOUROUT", new Minute(utility, 1));
		return map;
	}

	/*
	 * @seeorg.F11.scada.server.event.LoggingDataListener#changeLoggingData(
	 * LoggingDataEvent)
	 */
	public void changeLoggingData(LoggingDataEvent event) {
		queue.enqueue(event);
	}

	/**
	 * �Ώۃf�[�^�z���_�����M���O�f�[�^�Ƃ��ĉi�������܂��B
	 * 
	 * @param name ���M���O�Ώۖ�
	 * @param dataHolders �i��������f�[�^�z���_�̃��X�g
	 */
	private void store(LoggingDataEvent event) throws SQLException {
		Connection con = null;
		Statement st = null;
		try {
			con = ConnectionUtil.getConnection();
			List dataHolders = event.getHolders();
			if (logger.isDebugEnabled()) {
				logger.debug(dataHolders);
			}
			Timestamp timestamp = event.getTimeStamp();
			checkTableName(dataHolders, con);
			// checkColumnCount(dataHolders, con);
			Object obj = event.getSource();
			if (obj instanceof LoggingTask) {
				LoggingTask lt = (LoggingTask) obj;
				if (logicMap.containsKey(lt.getSchedule())) {
					PaddingLogic logic = logicMap.get(lt.getSchedule());
					logic
						.insertPadding(con, deviceName, dataHolders, timestamp);
				}
			}

			MultiRecordDefine multiRecord = getMultiRecordDefine(con);
			if (multiRecord != null) {
				// �����R�[�h���M���O
				List calval =
					utility.getColumnValueString(
						deviceName,
						dataHolders,
						multiRecord,
						con);
				Iterator it = calval.iterator();
				String[] columnNames = (String[]) it.next();
				for (; it.hasNext();) {
					Object[] values = (Object[]) it.next();
					String sql =
						utility
							.getInsertString(deviceName, columnNames, values);
					if (logger.isDebugEnabled()) {
						logger.debug(sql);
					}
					st = con.createStatement();
					st.executeUpdate(sql);
				}
			} else {
				int revision =
					utility.checkRevision(deviceName, timestamp, con);
				String sql =
					utility.getInsertString(
						deviceName,
						dataHolders,
						timestamp,
						revision);
				if (logger.isDebugEnabled()) {
					logger.debug(sql);
				}
				st = con.createStatement();
				st.executeUpdate(sql);
			}
		} finally {
			if (st != null) {
				st.close();
			}
			if (con != null) {
				con.close();
			}
		}
	}

	/**
	 * �����̃e�[�u�������݂��邩�������܂��B�����A���݂��Ȃ���΁A�f�[�^�z���_�[�̃��X�g���� ��ŁA�e�[�u�����쐬���܂��B
	 * 
	 * @param name �e�[�u����
	 * @param dataHolders �f�[�^�z���_�[�̃��X�g
	 * @exception SQLException �f�[�^�x�[�X�G���[�����������ꍇ
	 */
	private void checkTableName(List dataHolders, Connection con)
			throws SQLException {

		if (!tableCheck) {
			Statement st = null;
			ResultSet rs = null;
			try {
				st = con.createStatement();
				DatabaseMetaData metaData = con.getMetaData();
				rs =
					DatabaseMetaDataUtil.getTables(
						metaData,
						"",
						"",
						deviceName,
						null);
				// �e�[�u�������݂��邩����
				rs.last();
				if (rs.getRow() <= 0) {
					logger.info("TRY TABLE CREATE!! : " + deviceName);
					String sql =
						utility.getCreateString(deviceName, dataHolders);
					logger.info(sql);
					st.executeUpdate(sql);
				} else {
					tableCheck = true;
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			}
		}
	}

	/**
	 * �L���[�ɓ����ꂽ�A���M���O�f�[�^�ύX�C�x���g�����o���āA�f�[�^�̊i�[���������܂��B
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Thread ct = Thread.currentThread();

		while (ct == thread) {
			LoggingDataEvent event = (LoggingDataEvent) queue.dequeue();
			try {
				store(event);
			} catch (SQLException e) {
				logger.error("���M���O�f�[�^�}�����ɃG���[���������܂��� : " + deviceName, e);
				continue;
			}
		}
	}

	/**
	 * ���̃n���h���̓�����J�n���܂��B
	 */
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.setName(getClass().getName());
			thread.start();
		}
	}

	/**
	 * ���̃n���h���̓�����I�����܂��B�������̃C�x���g�͏������ꂸ�ɁA�����ɏI�����܂��B
	 */
	public void stop() {
		if (thread != null) {
			Thread st = thread;
			thread = null;
			st.interrupt();
		}
	}

	private MultiRecordDefine getMultiRecordDefine(Connection con)
			throws SQLException {
		MultiRecordDefine multiRecord = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = con.createStatement();
			DatabaseMetaData metaData = con.getMetaData();
			rs =
				DatabaseMetaDataUtil.getTables(
					metaData,
					"",
					"",
					"multi_record_define_table",
					null);
			// �e�[�u�������݂��邩����
			rs.last();
			if (0 < rs.getRow()) {
				rs.close();
				rs = null;
				st.close();
				st = null;
				// �Ǎ���
				multiRecord = dao_.getMultiRecordDefine(deviceName);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
		}
		return multiRecord;
	}

}
