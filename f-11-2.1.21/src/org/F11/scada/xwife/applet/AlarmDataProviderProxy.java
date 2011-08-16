/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/xwife/applet/AlarmDataProviderProxy.java,v 1.21.2.12 2007/10/18 09:48:43 frdm Exp $
 * $Revision: 1.21.2.12 $
 * $Date: 2007/10/18 09:48:43 $
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

package org.F11.scada.xwife.applet;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.DataProviderProxy;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.data.DataAccessable;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmTableJournal;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.server.alarm.table.PointTableBean;
import org.F11.scada.server.alarm.table.TableUtil;
import org.F11.scada.util.ThreadUtil;
import org.F11.scada.xwife.applet.alarm.event.CheckEvent;
import org.F11.scada.xwife.server.AlarmDataProvider;
import org.apache.log4j.Logger;

/**
 * 警報・状態一覧表テーブルモデルを管理する、代理データプロバイダクラスです。 起動時にサーバーにあるテーブルモデルを受信し、一定間隔でサーバーのテーブルモデ
 * ルと同期をとります。
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmDataProviderProxy extends AbstractDataProvider implements
		DataProviderProxy, Runnable {
	private static final long serialVersionUID = 8882469370969606556L;
	private static final Class<?>[][] TYPE_INFO =
		{ { DataHolder.class, AlarmTableModel.class } };

	public static final String CHECK_EVENT =
		"org.F11.scada.xwife.applet.AlarmDataProviderProxy.checkEvent";
	public static final String ROW_DATAS =
		"org.F11.scada.xwife.applet.AlarmDataProviderProxy.rowDatas";

	private static Logger logger;
	/* スレッド */
	private Thread thread;
	/* リモート参照 */
	private DataAccessable alarmRef;

	private long cycleTime = 1000L;

	/**
	 * 最終ポイント変更日時
	 *
	 * @since 1.0.3
	 */
	private volatile long lastPointEditTime = System.currentTimeMillis();

	/** サーバーコネクションエラーの有無 */
	private Exception serverError;

	public AlarmDataProviderProxy()
			throws DataProviderDoesNotSupportException,
			RemoteException {
		super();
		logger = Logger.getLogger(getClass().getName());
		ClientConfiguration config = new ClientConfiguration();
		cycleTime = config.getLong("xwife.applet.Applet.proxy.cycleTime", 1000);

		for (int i = 1; i <= Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				lookup();
				break;
			} catch (Exception e) {
				logger.info(WifeUtilities.createRmiCollector()
					+ " retry rmi lookup. ("
					+ i
					+ "/"
					+ Globals.RMI_CONNECTION_RETRY_COUNT
					+ ")");
				serverError = e;
				try {
					Thread.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				} catch (InterruptedException e1) {
				}
				continue;
			}
		}

		if (alarmRef == null) {
			logger.error("通信エラー:", serverError);
			throw ServerErrorUtil.createException(serverError);
		}

		DataProvider dataProvider =
			Manager.getInstance().getDataProvider(
					AlarmDataProvider.PROVIDER_NAME);
		if (dataProvider == null) {
			init();
		}
	}

	private void lookup()
		throws MalformedURLException,
		RemoteException,
		NotBoundException {
		String collectorServer = WifeUtilities.createRmiManagerDelegator();
		logger.debug("collectorServer : " + collectorServer);

		alarmRef = (DataAccessable) Naming.lookup(collectorServer);
	}

	public Class<?>[][] getProvidableDataHolderTypeInfo() {
		return TYPE_INFO;
	}

	public void run() {
		Thread thisThread = Thread.currentThread();

		while (thread == thisThread) {
			syncRead();
			ThreadUtil.sleep(cycleTime);
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

	private void init()
		throws DataProviderDoesNotSupportException,
		RemoteException {

		setDataProviderName(AlarmDataProvider.PROVIDER_NAME);
		addDataHolder(AlarmDataProvider.SUMMARY);
		addDataHolder(AlarmDataProvider.HISTORY);
		addDataHolder(AlarmDataProvider.CAREER);
		addDataHolder(AlarmDataProvider.OCCURRENCE);
		addDataHolder(AlarmDataProvider.NONCHECK);
		Manager.getInstance().addDataProvider(this);
	}

	private void addDataHolder(String providerName)
		throws RemoteException,
		DataProviderDoesNotSupportException {
		DataHolder dh = new DataHolder();
		dh.setDataHolderName(providerName);
		dh.setValueClass(AlarmTableModel.class);
		AlarmTableModel model =
			(AlarmTableModel) alarmRef.getValue(
					AlarmDataProvider.PROVIDER_NAME, providerName);
		dh.setValue(model, new Date(), WifeQualityFlag.INITIAL);
		addDataHolder(dh);
	}

	public synchronized void syncRead() {
		if (serverError != null) {
			return;
		}
		SortedMap<Long, PointTableBean[]> pointMap =
			getPointJournal(lastPointEditTime);
		setCareer(pointMap);
		setJournal(pointMap, AlarmDataProvider.HISTORY);
		setJournal(pointMap, AlarmDataProvider.SUMMARY);
		setJournal(pointMap, AlarmDataProvider.OCCURRENCE);
		setJournal(pointMap, AlarmDataProvider.NONCHECK);
		setCheckJournal();
	}

	private SortedMap<Long, PointTableBean[]> setCareer(
		SortedMap<Long, PointTableBean[]> pointMap) {
		DataProvider dataProvider =
			Manager.getInstance().getDataProvider(
					AlarmDataProvider.PROVIDER_NAME);
		DataHolder dh = dataProvider.getDataHolder(AlarmDataProvider.CAREER);
		AlarmTableModel model = (AlarmTableModel) dh.getValue();
		AlarmTableJournal jn = model.getLastJournal();
		if (pointMap != null && pointMap.size() != 0) {
			logger.info("point Map = " + pointMap);
			lastPointEditTime = ((Long) pointMap.lastKey()).longValue();
		}

		setPointJournal(model, pointMap);

		if (jn != null) {
			SortedMap<Long, AlarmTableJournal> data =
				getAlarmJournal(jn.getTimestamp().getTime(),
						AlarmDataProvider.PROVIDER_NAME, dh.getDataHolderName());
			model.setValue(data);
		} else {
			long ts = 0;
			if (model.getRowCount() > 0 && model.getValueAt(0, "日時") != null) {
				ts = ((Timestamp) model.getValueAt(0, "日時")).getTime();
			}
			SortedMap<Long, AlarmTableJournal> data =
				getAlarmJournal(ts, AlarmDataProvider.PROVIDER_NAME, dh
						.getDataHolderName());
			model.setValue(data);
		}
		return pointMap;
	}

	private void setJournal(
		SortedMap<Long, PointTableBean[]> pointMap,
		String holderName) {
		DataProvider dataProvider =
			Manager.getInstance().getDataProvider(
					AlarmDataProvider.PROVIDER_NAME);
		DataHolder dh = dataProvider.getDataHolder(holderName);
		AlarmTableModel model = (AlarmTableModel) dh.getValue();

		setPointJournal(model, pointMap);

		AlarmTableJournal jn = model.getLastJournal();
		if (jn != null) {
			SortedMap<Long, AlarmTableJournal> data =
				getAlarmJournal(jn.getTimestamp().getTime(),
						AlarmDataProvider.PROVIDER_NAME, dh.getDataHolderName());
			model.setValue(data);
		} else {
			long ts = 0;
			if (model.getRowCount() > 0) {
				long ts1 = 0;
				Timestamp tsv = (Timestamp) model.getValueAt(0, "発生・運転");
				if (tsv != null)
					ts1 = tsv.getTime();
				tsv = (Timestamp) model.getValueAt(0, "復旧・停止");
				long ts2 = 0;
				if (tsv != null)
					ts2 = tsv.getTime();
				ts = Math.max(ts1, ts2);
			}
			SortedMap<Long, AlarmTableJournal> data =
				getAlarmJournal(ts, AlarmDataProvider.PROVIDER_NAME, dh
						.getDataHolderName());
			model.setValue(data);
		}
	}

	private void setCheckJournal() {
		DataProvider dataProvider =
			Manager.getInstance().getDataProvider(
					AlarmDataProvider.PROVIDER_NAME);
		DataHolder dh = dataProvider.getDataHolder(AlarmDataProvider.NONCHECK);
		AlarmTableModel model = (AlarmTableModel) dh.getValue();

		CheckEvent evt = model.getLastCheckEvent();
		if (evt != null) {
			SortedMap<Long, CheckEvent> data =
				getCheckJournal(evt.getTimestamp().getTime(),
						AlarmDataProvider.PROVIDER_NAME, dh.getDataHolderName());
			fireCheckEvent(model, data);
		} else {
			long ts = 0;
			if (model.getRowCount() > 0) {
				long ts1 = 0;
				Timestamp tsv = (Timestamp) model.getValueAt(0, "発生・運転");
				if (tsv != null)
					ts1 = tsv.getTime();
				tsv = (Timestamp) model.getValueAt(0, "復旧・停止");
				long ts2 = 0;
				if (tsv != null)
					ts2 = tsv.getTime();
				ts = Math.max(ts1, ts2);
			}
			SortedMap<Long, CheckEvent> data =
				getCheckJournal(ts, AlarmDataProvider.PROVIDER_NAME, dh
						.getDataHolderName());
			fireCheckEvent(model, data);
		}
	}

	private void fireCheckEvent(
		AlarmTableModel model,
		SortedMap<Long, CheckEvent> data) {
		for (CheckEvent event : data.values()) {
			model.fireCheckEvent(event);
		}
	}

	private SortedMap<Long, AlarmTableJournal> getAlarmJournal(
		long ts,
		String providerName,
		String holderName) {
		SortedMap<Long, AlarmTableJournal> data = null;
		for (int i = 1;; i++) {
			if (i == Globals.RMI_METHOD_RETRY_COUNT) {
				if (serverError != null) {
					ServerErrorUtil.invokeServerError();
					logger.fatal("Exception caught: ", serverError);
				}
			}

			try {
				data = alarmRef.getAlarmJournal(ts, providerName, holderName);
				if (serverError != null && i >= Globals.RMI_METHOD_RETRY_COUNT) {
					ServerErrorUtil.invokeServerRepair();
					logger.error("Exception caught: ", serverError);
				}
				serverError = null;
				break;
			} catch (Exception e) {
				serverError = e;
				logger.info("Get AlarmJournal retry rmi lookup. (" + i + ")");
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				try {
					Thread.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				} catch (InterruptedException e2) {
				}
				continue;
			}
		}

		return data;
	}

	private SortedMap<Long, CheckEvent> getCheckJournal(
		long ts,
		String providerName,
		String holderName) {
		SortedMap<Long, CheckEvent> data = new TreeMap<Long, CheckEvent>();
		for (int i = 1;; i++) {
			if (i == Globals.RMI_METHOD_RETRY_COUNT) {
				if (serverError != null) {
					ServerErrorUtil.invokeServerError();
					logger.fatal("Exception caught: ", serverError);
				}
			}

			try {
				Object[] args = new Object[3];
				args[0] = providerName;
				args[1] = holderName;
				args[2] = new Long(ts);
				data =
					(SortedMap<Long, CheckEvent>) alarmRef.invoke(
							"GetCheckEventJournal", args);
				if (serverError != null && i >= Globals.RMI_METHOD_RETRY_COUNT) {
					ServerErrorUtil.invokeServerRepair();
					logger.error("Exception caught: ", serverError);
				}
				serverError = null;
				break;
			} catch (Exception e) {
				serverError = e;
				logger.info("Get CheckJournal retry rmi lookup. (" + i + ")");
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				try {
					Thread.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				} catch (InterruptedException e2) {
				}
				continue;
			}
		}

		return data;
	}

	private SortedMap<Long, PointTableBean[]> getPointJournal(long ts) {
		SortedMap<Long, PointTableBean[]> data = null;
		for (int i = 1;; i++) {
			if (i == Globals.RMI_METHOD_RETRY_COUNT) {
				if (serverError != null) {
					ServerErrorUtil.invokeServerError();
					logger.fatal("Exception caught: ", serverError);
				}
			}

			try {
				data = alarmRef.getPointJournal(ts);
				if (serverError != null && i >= Globals.RMI_METHOD_RETRY_COUNT) {
					ServerErrorUtil.invokeServerRepair();
					logger.error("Exception caught: ", serverError);
				}
				serverError = null;
				break;
			} catch (Exception e) {
				logger.info("Get AlarmJournal retry rmi lookup. (" + i + ")");
				serverError = e;
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				try {
					Thread.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				} catch (InterruptedException e2) {
				}
				continue;
			}
		}

		return data;
	}

	private void setPointJournal(
		AlarmTableModel model,
		SortedMap<Long, PointTableBean[]> pointMap) {
		if (pointMap != null) {
			for (Iterator<PointTableBean[]> it = pointMap.values().iterator(); it
					.hasNext();) {
				PointTableBean[] b = (PointTableBean[]) it.next();
				TableUtil.setPoint(model, b[0], b[1]);
			}
		}
	}

	public synchronized void syncWrite(DataHolder dh)
		throws DataProviderDoesNotSupportException {
		if (serverError != null) {
			return;
		}

		if (AlarmDataProvider.HISTORY.equals(dh.getDataHolderName())) {
			checkHistory(dh);
		} else if (AlarmDataProvider.NONCHECK.equals(dh.getDataHolderName())) {
			checkNoncheck(dh);
		}

		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			logger.fatal("Exception caught: ", serverError);
		}
	}

	private void checkHistory(DataHolder dh) {
		int row = ((Integer) dh.getParameter("row")).intValue();
		AlarmTableModel model = (AlarmTableModel) dh.getValue();

		for (int i = 0; i < Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				Timestamp now = new Timestamp(System.currentTimeMillis());
				alarmRef.setHistoryTable((Integer) model.getValueAt(row,
						"point"), (String) model.getValueAt(row, "provider"),
						(String) model.getValueAt(row, "holder"), now,
						(Integer) dh.getParameter("row"));
				alarmRef.invoke("AddCheckJournal",
						new Object[] { new CheckEvent(dh.getDataHolderName(),
								model, row, now) });
				serverError = null;
				break;
			} catch (Exception ex) {
				try {
					lookup();
				} catch (Exception e) {
					serverError = e;
				}
				serverError = ex;
				continue;
			}
		}
	}

	private void checkNoncheck(DataHolder dh) {
		Object[] args =
			new Object[] {
				dh.getParameter(AlarmDataProviderProxy.CHECK_EVENT),
				dh.getParameter(AlarmDataProviderProxy.ROW_DATAS) };
		for (int i = 0; i < Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				alarmRef.invoke("SetNoncheckTable", args);
				alarmRef.invoke("AddCheckJournal", args);
				serverError = null;
				break;
			} catch (Exception ex) {
				logger.error("ネットワークエラー", ex);
				try {
					lookup();
				} catch (Exception e) {
					logger.error("ネットワークエラー", e);
					serverError = e;
				}
				serverError = ex;
				continue;
			}
		}
	}

	// Non Used Methods
	public void asyncRead(DataHolder dh)
		throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncRead(DataHolder[] dhs)
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

	public void syncWrite(DataHolder[] dhs)
		throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void setValueChangeNewestTime(long l) {
	}

	public void lock() {
	}

	public void unlock() {
	}
}
