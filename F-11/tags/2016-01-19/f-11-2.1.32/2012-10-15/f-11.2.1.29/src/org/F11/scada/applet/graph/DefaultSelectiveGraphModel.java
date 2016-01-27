/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/Attic/DefaultSelectiveGraphModel.java,v 1.1.2.13 2007/10/15 00:22:04 frdm Exp $
 * $Revision: 1.1.2.13 $
 * $Date: 2007/10/15 00:22:04 $
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

package org.F11.scada.applet.graph;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.server.io.SelectiveValueListHandler;
import org.F11.scada.util.ThreadUtil;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * デフォルトの GraphModel 実装クラスです。 ValueListHandler をコンポジションして、ストレージデバイスよりデータを参照します。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DefaultSelectiveGraphModel extends AbstractGraphModel implements
		Runnable {
	/** データハンドラマネージャーです。 */
	private SelectiveValueListHandler valueListHandler;

	/** 基本になるソート済みマップ */
	private Map masterSortedMaps;

	/** 値（シリーズデータリスト）の反復子 */
	private Iterator valueIterator;

	/** キー（タイムスタンプ）の反復子 */
	private Iterator keyIterator;

	/** 現在の値 */
	private Object currentObject;

	/** スレッドオブジェクト */
	private Thread thread;

	/** 内部データ更新周期 */
	private static final long SLEEP_TIME = 30000L;

	private final int maxMapSize;

	private static Logger logger;

	/** サーバーエラー例外です */
	private Exception serverError;

	/** 抽出するデータホルダーのリスト * */
	private List holderStrings;

	/** ハンドラ・ファクトリークラス */
	private final SelectiveValueListHandlerFactory factory;

	/**
	 * コンストラクタ デフォルトのファクトリーで初期化します
	 * 
	 * @param handlerName データハンドラ名
	 * @param holderStrings 抽出するホルダーのリスト
	 * @param model グラフ・プロパティーモデル (このクラスでは使用しません)
	 */
	public DefaultSelectiveGraphModel(String handlerName, List holderStrings,
			GraphPropertyModel model) throws RemoteException {
		this(handlerName, holderStrings,
				new DefaultSelectiveValueListHandlerFactory(), model, Integer
						.parseInt(EnvironmentManager.get(
								"/server/logging/maxrecord", "4096")));
	}

	/**
	 * コンストラクタ デフォルトのファクトリーで初期化します
	 * 
	 * @param handlerName データハンドラ名
	 * @param holderStrings 抽出するホルダーのリスト
	 * @param model グラフ・プロパティーモデル (このクラスでは使用しません)
	 * @param maxMapSize モデル内で保存しておくレコード件数
	 */
	public DefaultSelectiveGraphModel(String handlerName, List holderStrings,
			GraphPropertyModel model, int maxMapSize) throws RemoteException {
		this(handlerName, holderStrings,
				new DefaultSelectiveValueListHandlerFactory(), model, maxMapSize);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param handlerName データハンドラ名
	 * @param holderStrings 抽出するホルダーのリスト
	 * @param factory ハンドラーファクトリー
	 * @param model グラフ・プロパティーモデル (このクラスでは使用しません)
	 * @param maxMapSize モデル内で保存しておくレコード件数
	 * @throws RemoteException
	 */
	public DefaultSelectiveGraphModel(String handlerName, List holderStrings,
			SelectiveValueListHandlerFactory factory, GraphPropertyModel model,
			int maxMapSize) throws RemoteException {
		super();
		this.masterSortedMaps = new ConcurrentHashMap();
		this.maxMapSize = maxMapSize;
		this.holderStrings = new ArrayList(holderStrings);
		logger = Logger.getLogger(getClass().getName());
		this.factory = factory;

		logger.info("ValueListHandler:"
				+ WifeUtilities.createRmiSelectiveValueListHandlerManager());

		for (int i = 0; i < Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				lookup();
				serverError = null;
				break;
			} catch (Exception e) {
				serverError = e;
				try {
					Thread.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				} catch (InterruptedException e1) {
				}
				continue;
			}
		}

		if (serverError != null) {
			throw ServerErrorUtil.createException(serverError);
		}

		createMasterSortedMap(handlerName);

		start();

		logger.debug("DefaultSelectiveGraphModel start.");
		logger.info("max map size : " + maxMapSize);
	}

	private void lookup() {
		valueListHandler = factory.getSelectiveValueListHandler();
	}

	/**
	 * 対象ハンドラーのレコードオブジェクトを返します。
	 * 
	 * @param name
	 *            対象ハンドラー名
	 * @return レコードオブジェクト
	 */
	public Object get(String name) {
		if (currentObject == null) {
			throw new NoSuchElementException();
		}
		return currentObject;
	}

	/**
	 * レコードカーソルを次のレコードに位置づけます。JDBCのResultSetと同じ物です。 次のレコードオブジェクトが存在する時は、true
	 * を返します。
	 * 
	 * @param name
	 *            対象ハンドラー名
	 * @return 次のレコードオブジェクトが存在する時は、true をそうでない場合は false を返します。
	 */
	public boolean next(String name) {
		createMasterSortedMap(name);
		if (valueIterator.hasNext()) {
			currentObject = new LoggingData((Timestamp) keyIterator.next(),
					(DoubleList) valueIterator.next());
			return true;
		} else {
			currentObject = null;
			return false;
		}
	}

	/**
	 * 最初レコードのタイムスタンプを返します。
	 * 
	 * @param name
	 *            対象ハンドラー名
	 * @return 最初レコードのタイムスタンプ
	 */
	public Object firstKey(String name) {
		createMasterSortedMap(name);
		SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
		try {
			return masterSortedMap.firstKey();
		} catch (NoSuchElementException e) {
			return new Timestamp(System.currentTimeMillis());
		}
	}

	/**
	 * 最終レコードのタイムスタンプを返します。
	 * 
	 * @param name
	 *            対象ハンドラー名
	 * @return 最終レコードのタイムスタンプ
	 */
	public Object lastKey(String name) {
		createMasterSortedMap(name);
		SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
		try {
			return masterSortedMap.lastKey();
		} catch (NoSuchElementException e) {
			return new Timestamp(System.currentTimeMillis());
		}
	}

	/**
	 * タイムスタンプが引数 key 以前のレコードを検索し、ポインタを位置づけます。
	 * 
	 * @param name
	 *            対象ハンドラー名
	 * @param key
	 *            検索するレコードのタイムスタンプ
	 */
	public void findRecord(String name, Timestamp key) {
		createMasterSortedMap(name);
		SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
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

	private synchronized void createMasterSortedMap(String name) {
		/** 基本になるソート済みマップの生成 */
		if (!masterSortedMaps.containsKey(name)) {
			masterSortedMaps.put(name, getInitialData(name));
		}

		if (valueIterator == null) {
			createValueIterator((SortedMap) masterSortedMaps.get(name));
		}
	}

	private void createValueIterator(SortedMap map) {
		SortedMap valueSortedMap = new TreeMap(map);
		Collection collection = valueSortedMap.values();
		valueIterator = collection.iterator();
		Set valueSet = valueSortedMap.keySet();
		keyIterator = valueSet.iterator();
	}

	/**
	 * ValueListHandler から最新データをとりだし、保持しているロギングデータを 更新します。
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Thread ct = Thread.currentThread();

		try {
			while (ct == thread) {
				for (Iterator it = masterSortedMaps.keySet().iterator(); it
						.hasNext();) {
					String name = (String) it.next();
					SortedMap masterSortedMap = (SortedMap) masterSortedMaps
							.get(name);
					Map updateData = getUpdateLoggingData(name, masterSortedMap);
					updateMasterSortedMap(updateData);
				}
				ThreadUtil.sleep(SLEEP_TIME);
			}
		} catch (NoSuchElementException e) {
			ThreadUtil.sleep(SLEEP_TIME);
		}
	}

	private void updateMasterSortedMap(Map updateData) {
		for (Iterator it = masterSortedMaps.keySet().iterator(); it.hasNext();) {
			String name = (String) it.next();
			SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
			if ((updateData != null) && (updateData.size() > 0)) {
				if (logger.isDebugEnabled()) {
					logger.debug("updateData:" + updateData);
					logger.debug("master size:" + masterSortedMap.size());
				}

				masterSortedMap.putAll(updateData);
				if (masterSortedMap.size() > maxMapSize) {
					for (int i = 0, n = masterSortedMap.size() - maxMapSize; i < n; i++) {
						logger
								.debug("delete key:"
										+ masterSortedMap.firstKey());
						masterSortedMap.remove(masterSortedMap.firstKey());
					}
				}
				firePropertyChange(null, masterSortedMap);
			}
		}
	}

	private SortedMap getInitialData(String currentHandlerName) {
		if (serverError != null) {
			return new TreeMap();
		}

		SortedMap map = null;
		for (int i = 0; i < Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("currentHandlerName : " + currentHandlerName);
					logger.debug("holderStrings : " + holderStrings);
				}
				map = valueListHandler.getInitialData(currentHandlerName,
						holderStrings, maxMapSize);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				serverError = e;
				continue;
			}
		}

		if (serverError != null) {
			serverError.printStackTrace();
			ServerErrorUtil.invokeServerError();
		}

		return map;
	}

	private Map getUpdateLoggingData(String name, SortedMap masterSortedMap) {
		if (serverError != null) {
			return new HashMap();
		}

		Map map = Collections.EMPTY_MAP;

		for (int i = 1;; i++) {
			if (i == Globals.RMI_METHOD_RETRY_COUNT) {
				if (serverError != null) {
					ServerErrorUtil.invokeServerError();
					serverError.printStackTrace();
				}
			}
			try {
				Timestamp time = null;
				try {
					time = (Timestamp) masterSortedMap.lastKey();
				} catch (NoSuchElementException ex) {
					time = new Timestamp(System.currentTimeMillis());
				}
				map = valueListHandler.getUpdateLoggingData(name, time,
						holderStrings);
				if (serverError != null && i >= Globals.RMI_METHOD_RETRY_COUNT) {
					ServerErrorUtil.invokeServerRepair();
					logger.error("Exception caught: ", serverError);
				}
				serverError = null;
				break;
			} catch (Exception e) {
				serverError = e;
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				continue;
			}
		}

		return map;
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
			Thread ot = thread;
			thread = null;
			ot.interrupt();
		}
	}
}
