/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/postgresql/PostgreSQLValueListHandler.java,v 1.13.2.9 2006/05/15 09:50:30 frdm Exp $
 * $Revision: 1.13.2.9 $
 * $Date: 2006/05/15 09:50:30 $
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

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Service;
import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.LoggingRowData;
import org.F11.scada.server.dao.DatabaseMetaDataUtil;
import org.F11.scada.server.dao.MultiRecordDefineDao;
import org.F11.scada.server.entity.MultiRecordDefine;
import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.LoggingDataEventQueue;
import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.SelectHandler;
import org.F11.scada.server.io.ValueListHandlerElement;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.util.ConnectionUtil;
import org.F11.scada.util.ThreadUtil;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

/**
 * ���M���O�f�[�^�̃n���h���N���X�ł��B ��`���ꂽ���M���O�f�[�^���f�[�^�X�g���[�W���ǂ݂Ƃ�܂��B
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PostgreSQLValueListHandler implements Runnable,
		ValueListHandlerElement, LoggingDataListener, Service {
	/** �f�o�C�X��(�ʏ�̓e�[�u����) */
	private String loggingTableName;
	/** �f�[�^�z���_�[�̃��X�g */
	private List dataHolders;
	/** �f�[�^�Z���N�g�n���h�� */
	private SelectHandler selectHandler;

	/** ��{�ɂȂ�\�[�g�ς݃}�b�v */
	private SortedMap masterSortedMap;
	/** �l�i�V���[�Y�f�[�^���X�g�j�̔����q */
	private Iterator valueIterator;
	/** �L�[�i�^�C���X�^���v�j�̔����q */
	private Iterator keyIterator;

	/** ���M���O�C�x���g�L���[ */
	private LoggingDataEventQueue queue;
	/** �X���b�h�I�u�W�F�N�g */
	private Thread thread;

	private final ItemUtil util;

	/** ���M���OAPI */
	private final Logger logger = Logger
		.getLogger(PostgreSQLValueListHandler.class);;

	/** �����ێ����郍�M���O�f�[�^�̍ő僌�R�[�h�� */
	public static final int MAX_MAP_SIZE;
	static {
		String maxRecord =
			EnvironmentManager.get("/server/logging/maxrecord", "4096");
		MAX_MAP_SIZE = Integer.parseInt(maxRecord);
	}

	/** �����ێ��f�[�^�X�V��ʒm�惊�X�g */
	private List loggingDataListeners;

	private MultiRecordDefineDao dao_;

	/**
	 * �N���C�A���g�n���h���C���^�[�t�F�C�X�I�u�W�F�N�g�𐶐����܂��B
	 *
	 * @param loggingTableName �e�[�u����
	 * @param dataHolders �f�[�^�z���_�[�̃��X�g
	 * @param selectHandler �f�[�^�Z���N�g�n���h��
	 * @throws RemoteException ���W�X�g���ɐڑ��ł��Ȃ��ꍇ
	 * @throws MalformedURLException ���O���K�؂Ȍ`���� URL �łȂ��ꍇ
	 */
	public PostgreSQLValueListHandler(String loggingTableName,
			List dataHolders,
			SelectHandler selectHandler) {
		super();
		this.loggingTableName = loggingTableName;
		this.dataHolders = dataHolders;
		this.selectHandler = selectHandler;
		queue = new LoggingDataEventQueue();
		loggingDataListeners = new ArrayList();
		S2Container container = S2ContainerUtil.getS2Container();
		util = (ItemUtil) container.getComponent("itemutil");
		dao_ =
			(MultiRecordDefineDao) container
				.getComponent(MultiRecordDefineDao.class);

		try {
			createMasterSortedMap();
		} catch (SQLException e) {
			String msg = e.getMessage();
			if (msg != null && msg.equals(SelectHandler.TABLE_NOT_FOUND)) {
				masterSortedMap = new TreeMap();
			} else {
				logger.error("", e);
			}
		}
		start();
		logger.info("max map size : " + MAX_MAP_SIZE);
	}

	private synchronized void createMasterSortedMap() throws SQLException {
		/** ��{�ɂȂ�\�[�g�ς݃}�b�v�̐��� */
		masterSortedMap = new TreeMap();

		List list = selectHandler.select(loggingTableName, dataHolders);

		for (Iterator it = list.iterator(); it.hasNext();) {
			LoggingRowData data = (LoggingRowData) it.next();
			masterSortedMap.put(data.getTimestamp(), data.getList());
		}
		createValueIterator(masterSortedMap);
	}

	private void createValueIterator(SortedMap map) {
		SortedMap valueSortedMap = new TreeMap(map);
		Collection collection = valueSortedMap.values();
		valueIterator = collection.iterator();
		Set valueSet = valueSortedMap.keySet();
		keyIterator = valueSet.iterator();
	}

	/**
	 * ���̃��R�[�h�����݂���ꍇ�� true ��Ԃ��܂��B
	 *
	 * @return ���̃��R�[�h�����݂���ꍇ�� true �𑶍݂��Ȃ��ꍇ�� false ��Ԃ��܂��B
	 */
	public boolean hasNext() {
		return valueIterator.hasNext();
	}

	/**
	 * ���R�[�h�I�u�W�F�N�g��Ԃ��A�|�C���^�����ɐi�߂܂��B
	 *
	 * @return ���R�[�h�I�u�W�F�N�g
	 */
	public Object next() {
		return new LoggingData(
			(Timestamp) keyIterator.next(),
			(DoubleList) valueIterator.next());
	}

	/**
	 * ���R�[�h�̍ŏ��̃L�[�i�^�C���X�^���v�j��Ԃ��܂��B
	 *
	 * @return ���R�[�h�̍ŏ��̃L�[�i�^�C���X�^���v�j
	 */
	public Object firstKey() {
		return masterSortedMap.firstKey();
	}

	/**
	 * ���R�[�h�̍Ō�̃L�[�i�^�C���X�^���v�j��Ԃ��܂��B
	 *
	 * @return ���R�[�h�̍Ō�̃L�[�i�^�C���X�^���v�j
	 */
	public Object lastKey() {
		return masterSortedMap.lastKey();
	}

	/**
	 * �^�C���X�^���v������ key �ȑO�̃��R�[�h���������A�|�C���^���ʒu�Â��܂��B
	 *
	 * @param key �������郌�R�[�h�̃^�C���X�^���v
	 */
	public void findRecord(Timestamp key) {
		ThreadUtil.sleep(5000L);
		if (isCreateMaster()) {
			masterSortedMap.clear();
			try {
				createMasterSortedMap();
			} catch (SQLException e) {
				String msg = e.getMessage();
				if (msg != null && msg.equals(SelectHandler.TABLE_NOT_FOUND)) {
					masterSortedMap = new TreeMap();
				} else {
					logger.error("", e);
				}
			}
		}
		SortedMap tailMap = masterSortedMap.tailMap(key);
		SortedMap headMap = masterSortedMap.headMap(key);
		SortedMap sortedMap = null;
		if (tailMap.containsKey(key) || headMap.isEmpty()) {
			sortedMap = new TreeMap(tailMap);
		} else {
			Object lastKey = headMap.lastKey();
			Object lastValue = headMap.get(lastKey);
			sortedMap = new TreeMap(tailMap);
			sortedMap.put(lastKey, lastValue);
		}
		createValueIterator(sortedMap);
	}

	private boolean isCreateMaster() {
		return Boolean
			.valueOf(EnvironmentManager.get("/server/isCreateMaster", "true"))
			.booleanValue();
	}

	/*
	 * @see org.F11.scada.server.event.LoggingDataListener#changeLoggingData(
	 * LoggingDataEvent)
	 */
	public void changeLoggingData(LoggingDataEvent event) {
		queue.enqueue(event);
	}

	/**
	 * �C�x���g�L���[�ɓ����ꂽ�A�ύX�C�x���g�����o���}�b�v�I�u�W�F�N�g���X�V���܂��B
	 */
	public void run() {
		Thread ct = Thread.currentThread();

		while (ct == thread) {
			LoggingDataEvent event = (LoggingDataEvent) queue.dequeue();
			updateMasterSortedMap(event); //findRecord�ōēǍ�����וs�v�ɂȂ���?
			fireChangeLoggingData(event);
		}
	}

	private void updateMasterSortedMap(LoggingDataEvent event) {
		logger.debug("updateSortedMap method." + event);
		List holders = event.getHolders();
		MultiRecordDefine multiRecord = getMultiRecordDefine();
		if (multiRecord != null) {
			Map valuesMap =
				util.createDateHolderValuesMap(
					holders,
					loggingTableName,
					multiRecord);
			synchronized (this) {
				masterSortedMap.putAll(valuesMap);
				if (masterSortedMap.size() > MAX_MAP_SIZE) {
					masterSortedMap.remove(masterSortedMap.firstKey());
				}
				createValueIterator(masterSortedMap);
			}
		} else {
			Timestamp timestamp = event.getTimeStamp();
			DoubleList values =
				util.createHolderValue(holders, loggingTableName);
			synchronized (this) {
				masterSortedMap.put(timestamp, values);
				if (masterSortedMap.size() > MAX_MAP_SIZE) {
					masterSortedMap.remove(masterSortedMap.firstKey());
				}
				createValueIterator(masterSortedMap);
			}
		}
	}

	/**
	 * key�Ŏw�肳�ꂽ�����ȍ~�̃��M���O�f�[�^��Map�C���X�^���X�ŕԂ��܂��B
	 */
	public Map getUpdateLoggingData(Timestamp key) {
		Timestamp searchKey = new Timestamp(key.getTime() + 1);
		logger.debug("searchKey:" + searchKey);
		Map tailMap = null;
		synchronized (this) {
			tailMap = new HashMap(masterSortedMap.tailMap(searchKey));
		}

		logger.debug("update data:" + tailMap);
		return tailMap;
	}

	/**
	 * �������p�f�[�^��SortedMap��Ԃ��܂��B
	 *
	 * @return �������p�f�[�^��SortedMap��Ԃ��܂��B
	 */
	public SortedMap getInitialData() {
		return new TreeMap(masterSortedMap);
	}

	/**
	 * �ێ����Ă���f�[�^���X�V���ꂽ��ɒʒm������ǉ����܂��B
	 *
	 * @param listener
	 */
	public synchronized void addLoggingDataListener(LoggingDataListener listener) {
		loggingDataListeners.add(listener);
	}

	/**
	 * �ʒm����폜���܂��B
	 *
	 * @param listener
	 */
	public synchronized void removeLoggingDataListener(LoggingDataListener listener) {
		loggingDataListeners.remove(listener);
	}

	/**
	 * �X�V�C�x���g�𔭉΂��܂��B
	 *
	 * @param event
	 */
	private synchronized void fireChangeLoggingData(LoggingDataEvent event) {
		for (Iterator it = loggingDataListeners.iterator(); it.hasNext();) {
			LoggingDataListener listener = (LoggingDataListener) it.next();
			listener.changeLoggingData(event);
		}
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.setName(getClass().getName());
			thread.start();
		}
	}

	public void stop() {
		if (thread != null) {
			Thread th = thread;
			thread = null;
			th.interrupt();
		}
	}

	private MultiRecordDefine getMultiRecordDefine() {
		MultiRecordDefine multiRecord = null;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
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
				multiRecord = dao_.getMultiRecordDefine(loggingTableName);
			}
		} catch (SQLException e) {
			logger.error("", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
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
		return multiRecord;
	}

	public List<HolderString> getHolders() {
		return dataHolders;
	}
}
