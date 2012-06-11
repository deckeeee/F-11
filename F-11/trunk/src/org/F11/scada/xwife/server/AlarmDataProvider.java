/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/xwife/server/AlarmDataProvider.java,v 1.7.2.6 2006/08/11 02:24:30 frdm Exp $
 * $Revision: 1.7.2.6 $
 * $Date: 2006/08/11 02:24:30 $
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

package org.F11.scada.xwife.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.Service;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.AlarmException;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.server.alarm.table.InitialTableFactory;
import org.F11.scada.server.alarm.table.RowDataStrategy;
import org.F11.scada.server.alarm.table.StrategyFactory;
import org.F11.scada.server.alarm.table.postgresql.PostgreSQLInitialTableFactory;
import org.F11.scada.server.event.LoggingDataEventQueue;
import org.apache.log4j.Logger;

/**
 * 警報・状態一覧データを管理するデータプロバイダです。
 * 起動時にデータソースより過去データを読込再現します。以降は値変更イベントを受け取り、保持テーブルを更新します。 保持テーブルは Collector
 * オブジェクトにより参照されます。
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmDataProvider extends AbstractDataProvider implements
		Runnable, AlarmDataStore, Service {

	static final long serialVersionUID = -5613319387656656937L;
	/** データプロバイダが保持するクラス */
	private static final Class[][] TYPE_INFO = { {
		DataHolder.class,
		AlarmTableModel.class,
		WifeData.class,
		String.class } };
	/** データ変更イベントキュー */
	private final LoggingDataEventQueue queue;
	/** ヒストリ一覧テーブルの参照 */
	private AlarmTableModel historyTableModel;
	/** 未確認一覧テーブルの参照 */
	private AlarmTableModel noncheckTableModel;

	/** サマリのテーブル変更アルゴリズム */
	private final RowDataStrategy summaryStrategy;
	/** ヒストリのテーブル変更アルゴリズム */
	private final RowDataStrategy historyStrategy;
	/** 履歴のテーブル変更アルゴリズム */
	private final RowDataStrategy careerStrategy;
	/** 未復旧のテーブル変更アルゴリズム */
	private final RowDataStrategy occurrenceStrategy;
	/** 未確認のテーブル変更アルゴリズム */
	private final RowDataStrategy noncheckStrategy;

	/** メインスレッドの参照 */
	private Thread thread;

	/** 警報プロバイダ名称 */
	public static final String PROVIDER_NAME = "ALARM_PROVIDER";
	/** サマリデータホルダ名 */
	public static final String SUMMARY = "SUMMARY";
	/** ヒストリデータホルダ名 */
	public static final String HISTORY = "HISTORY";
	/** 履歴データホルダ名 */
	public static final String CAREER = "CAREER";
	/** 未復旧データホルダ名 */
	public static final String OCCURRENCE = "OCCURRENCE";
	/** 未確認データホルダ名 */
	public static final String NONCHECK = "NONCHECK";
	/** 初期警報ホルダ名 */
	public static final String INIT_ALARM = "INIT_ALARM_HOLDER";
	/** 初期警報WAVEファイル名 */
	public static final String INIT_ALARM_SOUND = "INIT_ALARM_SOUND";

	/** Logging API */
	private static Logger logger;

	/**
	 * コンストラクタ 各警報一覧テーブルモデルをDBより生成しデータホルダに登録し、 警報プロパイダを初期化し、JIM
	 * Managerにプロバイダ登録します。
	 *
	 * @throws DataProviderDoesNotSupportException
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws AlarmException
	 */
	public AlarmDataProvider(StrategyFactory factory) throws DataProviderDoesNotSupportException,
			InstantiationException,
			IllegalAccessException,
			AlarmException {
		super();
		logger = Logger.getLogger(getClass().getName());
		queue = new LoggingDataEventQueue();

		InitialTableFactory tableFactory =
			InitialTableFactory
				.createInitialTableFactory(PostgreSQLInitialTableFactory.class);

		setDataProviderName(PROVIDER_NAME);

		long now = System.currentTimeMillis();
		careerStrategy = createCareerHolder(tableFactory, factory, now);
		historyStrategy = createHistoryHolder(tableFactory, factory, now);
		summaryStrategy = createSummaryHolder(tableFactory, factory, now);
		occurrenceStrategy = createOccurrenceHolder(tableFactory, factory, now);
		noncheckStrategy = createNoncheckHolder(tableFactory, factory, now);

		Manager.getInstance().addDataProvider(this);
		logger.info("AlarmDataProvider start.");
		setInitAlarm();

		start();
	}

	/**
	 * 起動時警報音フラグ準備
	 *
	 * @throws DataProviderDoesNotSupportException
	 */
	private void setInitAlarm() throws DataProviderDoesNotSupportException {
		setInitialAlarmHolder();
		setInitialSoundHolder();
	}

	private void setInitialAlarmHolder()
			throws DataProviderDoesNotSupportException {
		DataHolder initAlarmHolder = new DataHolder();
		initAlarmHolder.setValueClass(WifeData.class);
		initAlarmHolder.setValue(
			WifeDataDigital.valueOfFalse(0),
			new Date(),
			WifeQualityFlag.INITIAL);
		initAlarmHolder.setDataHolderName(INIT_ALARM);
		initAlarmHolder.setParameter(
			WifeDataProvider.PARA_NAME_CYCLEREAD,
			Boolean.FALSE);
		addDataHolder(initAlarmHolder);
	}

	private void setInitialSoundHolder()
			throws DataProviderDoesNotSupportException {
		DataHolder initAlarmSound = new DataHolder();
		initAlarmSound.setValueClass(WifeData.class);
		initAlarmSound.setValue("", new Date(), WifeQualityFlag.INITIAL);
		initAlarmSound.setDataHolderName(INIT_ALARM_SOUND);
		initAlarmSound.setParameter(
			WifeDataProvider.PARA_NAME_CYCLEREAD,
			Boolean.FALSE);
		addDataHolder(initAlarmSound);
	}

	private RowDataStrategy createCareerHolder(InitialTableFactory tableFactory,
			StrategyFactory factory,
			long now) throws DataProviderDoesNotSupportException,
			AlarmException {

		AlarmTableModel tableModel = tableFactory.createCareer();
		addDataHolder(createDataHolder(now, tableModel, CAREER));
		return factory.createCareerStrategy(tableModel);
	}

	private RowDataStrategy createHistoryHolder(InitialTableFactory tableFactory,
			StrategyFactory factory,
			long now) throws DataProviderDoesNotSupportException,
			AlarmException {

		historyTableModel = tableFactory.createHistory();
		addDataHolder(createDataHolder(now, historyTableModel, HISTORY));
		return factory.createHistoryStrategy(historyTableModel);
	}

	private RowDataStrategy createSummaryHolder(InitialTableFactory tableFactory,
			StrategyFactory factory,
			long now) throws DataProviderDoesNotSupportException,
			AlarmException {

		AlarmTableModel tableModel = tableFactory.createSummary();
		addDataHolder(createDataHolder(now, tableModel, SUMMARY));
		return factory.createSummaryStrategy(tableModel);
	}

	private RowDataStrategy createOccurrenceHolder(InitialTableFactory tableFactory,
			StrategyFactory factory,
			long now) throws AlarmException,
			DataProviderDoesNotSupportException {

		AlarmTableModel tableModel = tableFactory.createOccurrence();
		addDataHolder(createDataHolder(now, tableModel, OCCURRENCE));
		return factory.createOccurrenceStrategy(tableModel);
	}

	private RowDataStrategy createNoncheckHolder(InitialTableFactory tableFactory,
			StrategyFactory factory,
			long now) throws AlarmException,
			DataProviderDoesNotSupportException {

		noncheckTableModel = tableFactory.createNoncheck();
		addDataHolder(createDataHolder(now, noncheckTableModel, NONCHECK));
		return factory.createNoncheckStrategy(noncheckTableModel);
	}

	private DataHolder createDataHolder(long now,
			AlarmTableModel tableModel,
			String holderName) {
		DataHolder dataHolder = new DataHolder();
		dataHolder.setDataHolderName(holderName);
		dataHolder.setValueClass(AlarmTableModel.class);
		dataHolder.setValue(tableModel, new Date(now), WifeQualityFlag.INITIAL);
		return dataHolder;
	}

	public Class[][] getProvidableDataHolderTypeInfo() {
		return TYPE_INFO;
	}

	public void run() {
		Thread ct = Thread.currentThread();
		while (ct == thread) {
			// キューが空の間はwaitします。
			DataValueChangeEventKey evt =
				(DataValueChangeEventKey) queue.dequeue();

			try {
				careerStrategy.renewRow(evt);
				historyStrategy.renewRow(evt);
				summaryStrategy.renewRow(evt);
				occurrenceStrategy.renewRow(evt);
				noncheckStrategy.renewRow(evt);
			} catch (AlarmException e) {
				logger.error("警報一覧更新時エラー:", e);
			}
		}
	}

	/**
	 * データ変更イベント値を投入します。
	 *
	 * @param key データ変更イベント値
	 */
	public void put(DataValueChangeEventKey key) {
		queue.enqueue(key);
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
			Thread old = thread;
			thread = null;
			old.interrupt();
		}
	}

	// Non used methods
	public void asyncRead(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncWrite(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncWrite(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void syncRead(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void syncRead(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncRead(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void syncWrite(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void syncWrite(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		// throw new DataProviderDoesNotSupportException();
		logger.info("NOP");
	}
}
