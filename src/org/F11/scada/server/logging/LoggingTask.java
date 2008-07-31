/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/logging/LoggingTask.java,v 1.13.4.4 2006/05/26 05:51:07 frdm Exp $
 * $Revision: 1.13.4.4 $
 * $Date: 2006/05/26 05:51:07 $
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

package org.F11.scada.server.logging;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.io.HandlerFactory;
import org.F11.scada.server.io.ValueListHandlerElement;
import org.F11.scada.server.io.ValueListHandlerManager;
import org.apache.log4j.Logger;

/**
 * ロギング用のタスククラスです。
 */
public class LoggingTask extends TimerTask {
	/** データホルダのリストです */
	protected List dataHolders;
	/** ロギングリスナーのリスト */
	private List loggingListeners;
	/** ロギングデータハンドラ */
	private ValueListHandlerElement handler;
	/** ハンドラーファトリー名 */
	private final String factoryName;

	/** ロギングAPI */
	private static Logger logger;
	
	/** 他参照するテーブル名 */
	private final List<String> tables;

	/**
	 * コンストラクタ
	 * @param name ロギング名
	 * @param dataHolders データホルダーのリスト
	 * @param factoryName データ永続クラス名
	 * @exception SQLException DBMSに接続できなかったとき
	 */
	public LoggingTask(
			String name,
			List dataHolders,
			String factoryName,
			ValueListHandlerManager handlerManager,
			String schedule,
			List<String> tables)
			throws SQLException, MalformedURLException, RemoteException {
		super();
		this.dataHolders = dataHolders;
		logger = Logger.getLogger(getClass().getName());

		this.factoryName = factoryName;
		HandlerFactory factory = HandlerFactory.getHandlerFactory(factoryName);
		addLoggingListener(factory.createStoreHandler(name));

		// PostgreSQL Value List Handler
		handler = factory.createValueListHandler(name, dataHolders);
		addLoggingListener(handler);
		handlerManager.addValueListHandlerElement(name, handler);
		this.tables = tables;
	}

	/**
	 * スケジュール開始の処理。
	 */
	public void run() {
		logger.debug("data store start!!");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp today = new Timestamp(cal.getTime().getTime());
		//dataHoldersより文字列→データホルダーしイベント発火する。
		LoggingDataEvent dataEvent = new LoggingDataEvent(this, today, dataHolders);
		fireChangeLoggingData(dataEvent);
	}
	
	/**
	 * ロギングデータ変更イベントのリスナー登録します。
	 * @param l ロギングデータ変更イベントのリスナーオブジェクト
	 */
	public synchronized void addLoggingListener(LoggingDataListener l) {
		if (loggingListeners == null) {
			loggingListeners = new LinkedList();
		}
		loggingListeners.add(l);
	}
	
	/**
	 * ロギングデータ変更イベントのリスナーを削除します。
	 * @param l ロギングデータ変更イベントのリスナーオブジェクト
	 */
	public synchronized void removeLoggingListener(LoggingDataListener l) {
		if (loggingListeners == null) {
			return;
		}
		loggingListeners.remove(l);
	}
	
	/**
	 * ロギングデータ変更イベントをリスナーに発火します。
	 */
	protected void fireChangeLoggingData(LoggingDataEvent event) {
		if (loggingListeners == null || loggingListeners.size() <= 0) {
			return;
		}

		synchronized(this) {
			for (Iterator it = loggingListeners.iterator(); it.hasNext();) {
				LoggingDataListener element = (LoggingDataListener) it.next();
				element.changeLoggingData(event);
			}
		}
	}

	public List getDataHolders() {
		return dataHolders; 
	}
	
	/**
	 * ロギングデータハンドラに、更新後リスナーを追加します。
	 * @param l
	 */
	public void addElementLoggingListener(LoggingDataListener l) {
		handler.addLoggingDataListener(l);
	}

	/**
	 * ハンドラーファクトリー名を返します。
	 * @return ハンドラーファクトリー名を返します。
	 */
	public String getFactoryName() {
		return factoryName;
	}
	
	/**
	 * 連結する他テーブル名のリストを返します。
	 * 
	 * @return 連結する他テーブル名のリストを返します。
	 */
	public List<String> getTables() {
		return Collections.unmodifiableList(tables);
	}
}
