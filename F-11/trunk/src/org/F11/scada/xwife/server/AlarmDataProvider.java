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
 * �x��E��Ԉꗗ�f�[�^���Ǘ�����f�[�^�v���o�C�_�ł��B
 * �N�����Ƀf�[�^�\�[�X���ߋ��f�[�^��Ǎ��Č����܂��B�ȍ~�͒l�ύX�C�x���g���󂯎��A�ێ��e�[�u�����X�V���܂��B �ێ��e�[�u���� Collector
 * �I�u�W�F�N�g�ɂ��Q�Ƃ���܂��B
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmDataProvider extends AbstractDataProvider implements
		Runnable, AlarmDataStore, Service {

	static final long serialVersionUID = -5613319387656656937L;
	/** �f�[�^�v���o�C�_���ێ�����N���X */
	private static final Class[][] TYPE_INFO = { {
		DataHolder.class,
		AlarmTableModel.class,
		WifeData.class,
		String.class } };
	/** �f�[�^�ύX�C�x���g�L���[ */
	private final LoggingDataEventQueue queue;
	/** �q�X�g���ꗗ�e�[�u���̎Q�� */
	private AlarmTableModel historyTableModel;
	/** ���m�F�ꗗ�e�[�u���̎Q�� */
	private AlarmTableModel noncheckTableModel;

	/** �T�}���̃e�[�u���ύX�A���S���Y�� */
	private final RowDataStrategy summaryStrategy;
	/** �q�X�g���̃e�[�u���ύX�A���S���Y�� */
	private final RowDataStrategy historyStrategy;
	/** �����̃e�[�u���ύX�A���S���Y�� */
	private final RowDataStrategy careerStrategy;
	/** �������̃e�[�u���ύX�A���S���Y�� */
	private final RowDataStrategy occurrenceStrategy;
	/** ���m�F�̃e�[�u���ύX�A���S���Y�� */
	private final RowDataStrategy noncheckStrategy;

	/** ���C���X���b�h�̎Q�� */
	private Thread thread;

	/** �x��v���o�C�_���� */
	public static final String PROVIDER_NAME = "ALARM_PROVIDER";
	/** �T�}���f�[�^�z���_�� */
	public static final String SUMMARY = "SUMMARY";
	/** �q�X�g���f�[�^�z���_�� */
	public static final String HISTORY = "HISTORY";
	/** �����f�[�^�z���_�� */
	public static final String CAREER = "CAREER";
	/** �������f�[�^�z���_�� */
	public static final String OCCURRENCE = "OCCURRENCE";
	/** ���m�F�f�[�^�z���_�� */
	public static final String NONCHECK = "NONCHECK";
	/** �����x��z���_�� */
	public static final String INIT_ALARM = "INIT_ALARM_HOLDER";
	/** �����x��WAVE�t�@�C���� */
	public static final String INIT_ALARM_SOUND = "INIT_ALARM_SOUND";

	/** Logging API */
	private static Logger logger;

	/**
	 * �R���X�g���N�^ �e�x��ꗗ�e�[�u�����f����DB��萶�����f�[�^�z���_�ɓo�^���A �x��v���p�C�_�����������AJIM
	 * Manager�Ƀv���o�C�_�o�^���܂��B
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
	 * �N�����x�񉹃t���O����
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
			// �L���[����̊Ԃ�wait���܂��B
			DataValueChangeEventKey evt =
				(DataValueChangeEventKey) queue.dequeue();

			try {
				careerStrategy.renewRow(evt);
				historyStrategy.renewRow(evt);
				summaryStrategy.renewRow(evt);
				occurrenceStrategy.renewRow(evt);
				noncheckStrategy.renewRow(evt);
			} catch (AlarmException e) {
				logger.error("�x��ꗗ�X�V���G���[:", e);
			}
		}
	}

	/**
	 * �f�[�^�ύX�C�x���g�l�𓊓����܂��B
	 *
	 * @param key �f�[�^�ύX�C�x���g�l
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
