/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/postgresql/Attic/PostgreSQLValueListHandler2.java,v 1.1.2.1 2007/04/24 00:46:35 frdm Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2007/04/24 00:46:35 $
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.LoggingRowData;
import org.F11.scada.server.dao.DatabaseMetaDataUtil;
import org.F11.scada.server.dao.MultiRecordDefineDao;
import org.F11.scada.server.entity.MultiRecordDefine;
import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.io.SelectHandler;
import org.F11.scada.server.io.ValueListHandlerElement;
import org.F11.scada.util.ConnectionUtil;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

/**
 * ���M���O�f�[�^�̃n���h���N���X�ł��B ��`���ꂽ���M���O�f�[�^���f�[�^�X�g���[�W���ǂ݂Ƃ�܂��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PostgreSQLValueListHandler2 implements ValueListHandlerElement {
	/** �f�o�C�X��(�ʏ�̓e�[�u����) */
	private String loggingTableName;
	/** �f�[�^�z���_�[�̃��X�g */
	private List dataHolders;
	/** �f�[�^�Z���N�g�n���h�� */
	private SelectHandler selectHandler;

	/** �l�i�V���[�Y�f�[�^���X�g�j�̔����q */
	private Iterator valueIterator;
	/** �L�[�i�^�C���X�^���v�j�̔����q */
	private Iterator keyIterator;

	/** ���M���OAPI */
	private final Logger logger = Logger
			.getLogger(PostgreSQLValueListHandler2.class);

	private MultiRecordDefineDao dao_;

	/** �����ێ��f�[�^�X�V��ʒm�惊�X�g */
	private List loggingDataListeners;

	/**
	 * �N���C�A���g�n���h���C���^�[�t�F�C�X�I�u�W�F�N�g�𐶐����܂��B
	 * 
	 * @param loggingTableName �e�[�u����
	 * @param dataHolders �f�[�^�z���_�[�̃��X�g
	 * @param selectHandler �f�[�^�Z���N�g�n���h��
	 * @throws RemoteException ���W�X�g���ɐڑ��ł��Ȃ��ꍇ
	 * @throws MalformedURLException ���O���K�؂Ȍ`���� URL �łȂ��ꍇ
	 */
	public PostgreSQLValueListHandler2(
			String loggingTableName,
			List dataHolders,
			SelectHandler selectHandler) {
		super();
		this.loggingTableName = loggingTableName;
		this.dataHolders = dataHolders;
		this.selectHandler = selectHandler;
		S2Container container = S2ContainerUtil.getS2Container();
		dao_ = (MultiRecordDefineDao) container
				.getComponent(MultiRecordDefineDao.class);
		loggingDataListeners = new ArrayList();
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
		throw new UnsupportedOperationException();
	}

	/**
	 * ���R�[�h�̍Ō�̃L�[�i�^�C���X�^���v�j��Ԃ��܂��B
	 * 
	 * @return ���R�[�h�̍Ō�̃L�[�i�^�C���X�^���v�j
	 */
	public Object lastKey() {
		throw new UnsupportedOperationException();
	}

	/**
	 * �^�C���X�^���v������ key �ȑO�̃��R�[�h���������A�|�C���^���ʒu�Â��܂��B
	 * 
	 * @param key �������郌�R�[�h�̃^�C���X�^���v
	 */
	public void findRecord(Timestamp key) {
		SortedMap map = new TreeMap();
		try {
			List list = selectHandler
					.select(loggingTableName, dataHolders, key);
			logger.info(list);
			for (Iterator it = list.iterator(); it.hasNext();) {
				LoggingRowData data = (LoggingRowData) it.next();
				map.put(data.getTimestamp(), data.getList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		createValueIterator(map);
	}

	private void createValueIterator(SortedMap map) {
		SortedMap valueSortedMap = new TreeMap(map);
		Collection collection = valueSortedMap.values();
		valueIterator = collection.iterator();
		Set valueSet = valueSortedMap.keySet();
		keyIterator = valueSet.iterator();
	}

	public void changeLoggingData(LoggingDataEvent event) {
		logger.info(event);
	}

	/**
	 * key�Ŏw�肳�ꂽ�����ȍ~�̃��M���O�f�[�^��Map�C���X�^���X�ŕԂ��܂��B
	 */
	public Map getUpdateLoggingData(Timestamp key) {
		throw new UnsupportedOperationException();
	}

	/**
	 * �������p�f�[�^��SortedMap��Ԃ��܂��B
	 * 
	 * @return �������p�f�[�^��SortedMap��Ԃ��܂��B
	 */
	public SortedMap getInitialData() {
		throw new UnsupportedOperationException();
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
	public synchronized void removeLoggingDataListener(
			LoggingDataListener listener) {
		loggingDataListeners.remove(listener);
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
			rs = DatabaseMetaDataUtil.getTables(
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
			e.printStackTrace();
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
}
