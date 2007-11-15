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
 * ロギングデータのハンドラクラスです。 定義されたロギングデータをデータストレージより読みとります。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PostgreSQLValueListHandler2 implements ValueListHandlerElement {
	/** デバイス名(通常はテーブル名) */
	private String loggingTableName;
	/** データホルダーのリスト */
	private List dataHolders;
	/** データセレクトハンドラ */
	private SelectHandler selectHandler;

	/** 値（シリーズデータリスト）の反復子 */
	private Iterator valueIterator;
	/** キー（タイムスタンプ）の反復子 */
	private Iterator keyIterator;

	/** ロギングAPI */
	private final Logger logger = Logger
			.getLogger(PostgreSQLValueListHandler2.class);

	private MultiRecordDefineDao dao_;

	/** 内部保持データ更新後通知先リスト */
	private List loggingDataListeners;

	/**
	 * クライアントハンドラインターフェイスオブジェクトを生成します。
	 * 
	 * @param loggingTableName テーブル名
	 * @param dataHolders データホルダーのリスト
	 * @param selectHandler データセレクトハンドラ
	 * @throws RemoteException レジストリに接続できない場合
	 * @throws MalformedURLException 名前が適切な形式の URL でない場合
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
	 * 次のレコードが存在する場合に true を返します。
	 * 
	 * @return 次のレコードが存在する場合に true を存在しない場合は false を返します。
	 */
	public boolean hasNext() {
		return valueIterator.hasNext();
	}

	/**
	 * レコードオブジェクトを返し、ポインタを次に進めます。
	 * 
	 * @return レコードオブジェクト
	 */
	public Object next() {
		return new LoggingData(
				(Timestamp) keyIterator.next(),
				(DoubleList) valueIterator.next());
	}

	/**
	 * レコードの最初のキー（タイムスタンプ）を返します。
	 * 
	 * @return レコードの最初のキー（タイムスタンプ）
	 */
	public Object firstKey() {
		throw new UnsupportedOperationException();
	}

	/**
	 * レコードの最後のキー（タイムスタンプ）を返します。
	 * 
	 * @return レコードの最後のキー（タイムスタンプ）
	 */
	public Object lastKey() {
		throw new UnsupportedOperationException();
	}

	/**
	 * タイムスタンプが引数 key 以前のレコードを検索し、ポインタを位置づけます。
	 * 
	 * @param key 検索するレコードのタイムスタンプ
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
	 * keyで指定された時刻以降のロギングデータをMapインスタンスで返します。
	 */
	public Map getUpdateLoggingData(Timestamp key) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 初期化用データのSortedMapを返します。
	 * 
	 * @return 初期化用データのSortedMapを返します。
	 */
	public SortedMap getInitialData() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 保持しているデータが更新された後に通知する先を追加します。
	 * 
	 * @param listener
	 */
	public synchronized void addLoggingDataListener(LoggingDataListener listener) {
		loggingDataListeners.add(listener);
	}

	/**
	 * 通知先を削除します。
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
			// テーブルが存在するか調査
			rs.last();
			if (0 < rs.getRow()) {
				rs.close();
				rs = null;
				st.close();
				st = null;
				// 読込み
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
