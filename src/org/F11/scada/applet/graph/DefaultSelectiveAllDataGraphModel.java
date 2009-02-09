/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/Attic/DefaultSelectiveAllDataGraphModel.java,v 1.1.2.6 2007/10/15 00:22:04 frdm Exp $
 * $Revision: 1.1.2.6 $
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.server.io.SelectiveAllDataValueListHandler;
import org.F11.scada.util.ThreadUtil;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

/**
 * デフォルトの GraphModel 実装クラスです。 ValueListHandler をコンポジションして、ストレージデバイスよりデータを参照します。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DefaultSelectiveAllDataGraphModel extends AbstractGraphModel {
	/** データハンドラマネージャーです。 */
	private SelectiveAllDataValueListHandler valueListHandler;
	/** 現在のハンドラ名です。 */
	private String currentHandlerName;

	/** 基本になるソート済みマップ */
	private Map masterSortedMaps;
	/** 値（シリーズデータリスト）の反復子 */
	private Iterator valueIterator;
	/** キー（タイムスタンプ）の反復子 */
	private Iterator keyIterator;
	/** 現在の値 */
	private Object currentObject;
	/** ソート済みマップの最大件数 */
	private final int maxMapSize;

	private static Logger logger;
	/** サーバーエラー例外です */
	private Exception serverError;

	/** 抽出するデータホルダーのリスト **/
	private List holderStrings;
	/** データハンドラのファクトリー */
	private final SelectiveAllDataValueListHandlerFactory factory;
	/** モデル生成時の最も古いレコードのタイムスタンプ */
	private final Timestamp firstTime;
	/** モデル生成時の最も新しいレコードのタイムスタンプ */
	private final Timestamp lastTime;
	/** グラフ・プロパティーモデル */
	private final GraphPropertyModel model;

	/**
	 * コンストラクタ デフォルトのファクトリーで初期化します
	 * 
	 * @param handlerName データハンドラ名
	 * @param holderStrings 抽出するホルダーのリスト
	 * @param model グラフ・プロパティーモデル
	 */
	public DefaultSelectiveAllDataGraphModel(
			String handlerName,
			List holderStrings,
			GraphPropertyModel model,
			int maxMapSize) throws RemoteException {
		this(
			handlerName,
			holderStrings,
			new DefaultSelectiveAllDataValueListHandlerFactory(),
			maxMapSize,
			model);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param handlerName データハンドラ名
	 * @param holderStrings 抽出するホルダーのリスト
	 * @param factory ハンドラファクトリークラス
	 * @param model グラフ・プロパティーモデル
	 */
	public DefaultSelectiveAllDataGraphModel(
			String handlerName,
			List holderStrings,
			SelectiveAllDataValueListHandlerFactory factory,
			GraphPropertyModel model,
			int maxMapSize) throws RemoteException {
		this(handlerName, holderStrings, factory, maxMapSize, model);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param handlerName データハンドラ名
	 * @param holderStrings 抽出するホルダーのリスト
	 * @param factory ハンドラファクトリークラス
	 * @param maxMapSize バッファ用Mapの最大サイズ
	 * @param model グラフ・プロパティーモデル
	 */
	public DefaultSelectiveAllDataGraphModel(
			String handlerName,
			List holderStrings,
			SelectiveAllDataValueListHandlerFactory factory,
			int maxMapSize,
			GraphPropertyModel model) throws RemoteException {
		super();

		this.masterSortedMaps = new ConcurrentHashMap();
		this.maxMapSize = maxMapSize;
		this.holderStrings = new ArrayList(holderStrings);
		logger = Logger.getLogger(getClass().getName());
		this.factory = factory;
		this.model = model;

		logger.info("ValueListHandler:"
			+ WifeUtilities.createRmiSelectiveAllDataValueListHandlerManager());

		for (int i = 0; i < Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				lookup();
				serverError = null;
				break;
			} catch (Exception e) {
				serverError = e;
				ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				continue;
			}
		}

		if (serverError != null) {
			throw ServerErrorUtil.createException(serverError);
		}

		firstTime = valueListHandler.firstTime(handlerName, holderStrings);
		lastTime = valueListHandler.lastTime(handlerName, holderStrings);

		createMasterSortedMap(handlerName);

		start();

		logger.debug("DefaultSelectiveAllDataGraphModel start.");
		logger.info("max map size : " + maxMapSize);
	}

	private void lookup() {
		valueListHandler = factory.getSelectiveAllDataValueListHandler();
	}

	/**
	 * 対象ハンドラーのレコードオブジェクトを返します。
	 * 
	 * @param name 対象ハンドラー名
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
	 * @param name 対象ハンドラー名
	 * @return 次のレコードオブジェクトが存在する時は、true をそうでない場合は false を返します。
	 */
	public boolean next(String name) {
		createMasterSortedMap(name);
		if (valueIterator.hasNext()) {
			currentObject =
				new LoggingData(
					(Timestamp) keyIterator.next(),
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
	 * @param name 対象ハンドラー名
	 * @return 最初レコードのタイムスタンプ
	 */
	public Object firstKey(String name) {
		return firstTime;
	}

	/**
	 * 最終レコードのタイムスタンプを返します。
	 * 
	 * @param name 対象ハンドラー名
	 * @return 最終レコードのタイムスタンプ
	 */
	public Object lastKey(String name) {
		return lastTime;
	}

	/**
	 * タイムスタンプが引数 key 以前のレコードを検索し、ポインタを位置づけます。
	 * 
	 * @param name 対象ハンドラー名
	 * @param key 検索するレコードのタイムスタンプ
	 */
	public void findRecord(String name, Timestamp key) {
		createMasterSortedMap(name);
		key = new Timestamp(key.getTime() - 1L);
		if (key.before(this.firstTime)) {
			key = this.firstTime;
		}
		changeMasterSortedMap(name, key);
		SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
		if (!masterSortedMap.isEmpty()) {
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
	}

	private void changeMasterSortedMap(String name, Timestamp key) {
		// マスターマップ外が要求された場合、マスターマップの切替処理を行う。
		SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
		if (isNotContentKey(masterSortedMap, key)) {
			logger.debug("key:"
				+ key
				+ " "
				+ masterSortedMap.firstKey()
				+ "～"
				+ masterSortedMap.lastKey());
			try {
				SortedMap data =
					trimSortedMap(valueListHandler.getLoggingData(
						name,
						holderStrings,
						key,
						maxMapSize));
				masterSortedMaps.put(name, data);
				createValueIterator(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isNotContentKey(SortedMap map, Timestamp key) {
		if (!map.isEmpty()) {
			Timestamp firstTime = (Timestamp) map.firstKey();
			Timestamp lastTime = (Timestamp) map.lastKey();

			long scale =
				(model.getHorizontalScaleCount() + 1)
					* model.getHorizontalScaleWidth();
			long remainder = lastTime.getTime() - key.getTime();
			logger.debug("scale:" + scale + " remainder:" + remainder);

			return key.before(firstTime)
				|| key.after(lastTime)
				|| (remainder < scale);
		} else {
			return false;
		}
	}

	private SortedMap trimSortedMap(SortedMap map) {
		Timestamp mapLastkey = (Timestamp) map.lastKey();
		TreeMap newMaster = new TreeMap(map);
		Timestamp trimKey = new Timestamp(lastTime.getTime() + 1);
		if (mapLastkey.after(trimKey)) {
			SortedMap retMap = map.tailMap(trimKey);
			for (Iterator i = retMap.keySet().iterator(); i.hasNext();) {
				Timestamp key = (Timestamp) i.next();
				newMaster.remove(key);
			}
		}

		return newMaster;
	}

	private synchronized void createMasterSortedMap(String name) {
		// 基本になるソート済みマップの生成
		if (!masterSortedMaps.containsKey(name)) {
			currentHandlerName = name;
			masterSortedMaps.put(name, getInitialData(currentHandlerName));
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

	private SortedMap getInitialData(String currentHandlerName) {
		if (serverError != null) {
			return new TreeMap();
		}

		SortedMap map = null;
		for (int i = 0; i < Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				logger.debug("currentHandlerName : " + currentHandlerName);
				logger.debug("holderStrings : " + holderStrings);
				map =
					valueListHandler.getInitialData(
						currentHandlerName,
						holderStrings);
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
			ServerErrorUtil.invokeServerError();
			serverError.printStackTrace();
		}

		return map;
	}

	public void start() {
	}

	public void stop() {
	}
}
