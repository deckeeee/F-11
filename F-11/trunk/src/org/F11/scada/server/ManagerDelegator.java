/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/ManagerDelegator.java,v 1.20.2.17 2007/04/24 00:46:51 frdm Exp $
 * $Revision: 1.20.2.17 $
 * $Date: 2007/04/24 00:46:51 $
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

package org.F11.scada.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;
import jp.gr.javacons.jim.QualityFlag;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.DataProviderDesc;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.CreateHolderData;
import org.F11.scada.data.DataAccessable;
import org.F11.scada.data.HolderData;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataAnalog4;
import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmTableJournal;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.HistoryCheck;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.server.alarm.table.PointTableBean;
import org.F11.scada.server.alarm.table.TableUtil;
import org.F11.scada.server.demand.DemandDataReferencer;
import org.F11.scada.server.frame.FramePageEditTimeSupport;
import org.F11.scada.server.invoke.AddCheckJournal;
import org.F11.scada.server.invoke.GetCheckEventJournal;
import org.F11.scada.server.invoke.HistoryAllCheck;
import org.F11.scada.server.invoke.InvokeHandler;
import org.F11.scada.server.invoke.SetNoncheckTable;
import org.F11.scada.server.invoke.UnitSerachServiceImpl;
import org.F11.scada.server.io.postgresql.PostgreSQLAlarmDataStore;
import org.F11.scada.server.operationlog.OperationLoggingService;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.xwife.applet.Session;
import org.F11.scada.xwife.server.AlarmDataProvider;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

/**
 * JIMマネージャーのリモート操作を代理実行するクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ManagerDelegator extends UnicastRemoteObject implements
		DataAccessable, FramePageEditTimeSupport, ScheduleHolderOwner {

	private static final long serialVersionUID = -2373449482456797261L;
	/** ManagerDelegator 登録名 */
	private static final String mainServer =
		WifeUtilities.createRmiManagerDelegator();
	/** ヒストリチェックオブジェクト */
	private HistoryCheck historyCheck;
	/** Logging API */
	private static Logger logger;
	/** ポイント変更イベントマップ */
	private final SortedMap pointMap;
	/** ポイント変更イベントマップ最大数 */
	private static final int POINT_MAP_MAX_COUNT = 1000;
	/** データ更新ログのサービスオブジェクト */
	private OperationLoggingService service;
	/** ポイントID, スケジュールデータのデータホルダのマップです。 */
	private Map schedules;
	/** サーバーコマンドのマップです */
	private final Map<String, InvokeHandler> commands;

	/**
	 * このオブジェクトを初期化します。
	 * 
	 * @param recvPort オブジェクト転送ポート番号
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws SQLException
	 */
	public ManagerDelegator(int recvPort) throws RemoteException,
			MalformedURLException {
		super(recvPort);
		logger = Logger.getLogger(getClass().getName());
		pointMap = Collections.synchronizedSortedMap(new TreeMap());

		historyCheck = new PostgreSQLAlarmDataStore();
		logger.info("ManagerDelegator : " + mainServer);
		Naming.rebind(mainServer, this);
		logger.info("ManagerDelegator bound in registry");
		commands = createCommand();
	}

	private Map<String, InvokeHandler> createCommand() {
		HashMap<String, InvokeHandler> map =
			new HashMap<String, InvokeHandler>();
		map.put("SetNoncheckTable", new SetNoncheckTable(historyCheck));
		map.put("GetCheckEventJournal", new GetCheckEventJournal());
		map.put("AddCheckJournal", new AddCheckJournal());
		map.put("HistoryAllCheck", new HistoryAllCheck());
		map.put("UnitSearchService", new UnitSerachServiceImpl());
		return map;
	}

	public void setService(OperationLoggingService service) {
		this.service = service;
	}

	public Object getValue(String dpname, String dhname) {
		DataProvider dp = Manager.getInstance().getDataProvider(dpname);
		if (dp == null) {
			return null;
		}
		DataHolder dh = dp.getDataHolder(dhname);
		if (dh == null) {
			return null;
		}
		return dh.getValue();
	}

	public QualityFlag getQualityFlag(String dpname, String dhname) {
		DataProvider dp = Manager.getInstance().getDataProvider(dpname);
		if (dp == null) {
			return null;
		}
		DataHolder dh = dp.getDataHolder(dhname);
		if (dh == null) {
			return null;
		}
		return dh.getQualityFlag();
	}

	public void setValue(String dpname, String dhname, Object dataValue) {
		DataHolder dh = getDataHolder(dpname, dhname);
		setValue(dataValue, dh);
	}

	public void setValue(
			String dpname,
			String dhname,
			Object dataValue,
			String user,
			String ip) throws RemoteException {
		DataHolder dh = getDataHolder(dpname, dhname);
		// 操作ログ処理
		if (isSetValue(dh, dataValue)) {
			service.logging(dh, dataValue, user, ip, new Timestamp(System
				.currentTimeMillis()));
			setValue(dataValue, dh);
		}
	}

	private boolean isSetValue(DataHolder dh, Object dataValue) {
		return true;
	}

	private DataHolder getDataHolder(String dpname, String dhname) {
		// logger.debug("Dp:" + dpname + " Dh:" + dhname);
		DataProvider dp = Manager.getInstance().getDataProvider(dpname);
		if (dp == null) {
			throw new NullPointerException("DataProvider Not Found. : "
				+ dpname);
		}
		DataHolder dh = dp.getDataHolder(dhname);
		if (dh == null) {
			throw new NullPointerException("DataHolder Not Found. : " + dhname);
		}
		return dh;
	}

	private void setValue(Object dataValue, DataHolder dh) {
		dh.setValue(dataValue, new Date(), WifeQualityFlag.GOOD);
		try {
			dh.syncWrite();
		} catch (DataProviderDoesNotSupportException ex) {
			ex.printStackTrace();
		}
	}

	public void setHistoryTable(
			Integer point,
			String provider,
			String holder,
			Timestamp date,
			Integer row) {

		DataHolder dh =
			Manager.getInstance().findDataHolder(
				AlarmDataProvider.PROVIDER_NAME,
				AlarmDataProvider.HISTORY);
		dh.setParameter("row", row);
		AlarmTableModel model = (AlarmTableModel) dh.getValue();

		int columnCount = model.getColumnCount();
		Object[] rows = getRows(model, row);

		rows[columnCount - 1] = "＊＊＊＊";
		if (logger.isDebugEnabled()) {
			logger.debug("rows:" + Arrays.asList(rows));
		}
		DataValueChangeEventKey key =
			new DataValueChangeEventKey(
				point.intValue(),
				provider,
				holder,
				Boolean.TRUE,
				date);
		model.setValueAt(rows, row.intValue(), columnCount - 1, key);
		dh.setValue(model, date, WifeQualityFlag.GOOD);
		setNoncheck(row, model, key, date);
		try {
			historyCheck.doHistoryCheck(
				point,
				provider,
				holder,
				(Timestamp) rows[7]);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Object[] getRows(AlarmTableModel model, Integer row) {
		int columnCount = model.getColumnCount();
		Object[] rows = new Object[columnCount];
		for (int i = 0; i < columnCount - 1; i++) {
			rows[i] = model.getValueAt(row.intValue(), i);
		}
		return rows;
	}

	private void setNoncheck(
			Integer row,
			AlarmTableModel model,
			DataValueChangeEventKey key,
			Timestamp date) {
		DataHolder dh =
			Manager.getInstance().findDataHolder(
				AlarmDataProvider.PROVIDER_NAME,
				AlarmDataProvider.NONCHECK);
		AlarmTableModel noncheckModel = (AlarmTableModel) dh.getValue();
		Object[] rows = getRows(model, row);
		noncheckModel.setJournal(AlarmTableJournal.createRowDataRemoveOpe(
			key,
			rows));
		dh.setValue(noncheckModel, date, WifeQualityFlag.GOOD);
	}

	/**
	 * 指定されたデータホルダを返します。
	 * 
	 * @param dpname データプロバイダ名
	 * @param dhname データホルダ名
	 * @return 存在した場合は DataHolder オブジェクト、無い場合は null を返します。
	 * @throws RemoteException RemoteException RMI エラーが発生したとき
	 */
	public DataHolder findDataHolder(String dpname, String dhname) {
		return Manager.getInstance().findDataHolder(dpname, dhname);
	}

	public Object getParameta(String dpname, String dhname, String paraName) {
		DataHolder dh = Manager.getInstance().findDataHolder(dpname, dhname);
		if (dh != null) {
			return dh.getParameter(paraName);
		}
		logger.warn("Parameta Not Found : DP-"
			+ dpname
			+ " DH-"
			+ dhname
			+ " Para-"
			+ paraName);
		return null;
	}

	/**
	 * データホルダ更新データを返します。
	 * 
	 * @param provider データプロバイダ名
	 * @return データホルダ更新データのListオブジェクト
	 */
	public List getHoldersData(String provider) {
		DataProvider dp = Manager.getInstance().getDataProvider(provider);
		ArrayList retData = new ArrayList(dp.getDataHolderCount());
		for (Iterator i = dp.getDataHolderValues().iterator(); i.hasNext();) {
			DataHolder dh = (DataHolder) i.next();
			Object obj = dh.getValue();
			if (obj instanceof WifeData) {
				WifeData wd = (WifeData) obj;
				retData.add(new HolderData(dh.getDataHolderName(), wd
					.toByteArray(), dh.getTimeStamp().getTime(), (Map) dh
					.getParameter(DemandDataReferencer.GRAPH_DATA)));
			}
		}
		return retData;
	}

	/**
	 * データホルダ更新データを返します。
	 * 
	 * @param provider データプロバイダ名
	 * @param t 保持データの最新日付の long 値
	 * @return データホルダ更新データのListオブジェクト
	 */
	public List getHoldersData(String provider, long t, Session session) {
		DataProvider dp = Manager.getInstance().getDataProvider(provider);
		WifeDataProvider wdp = (WifeDataProvider) dp;
		return wdp.getHoldersData(t, session);
	}

	/**
	 * サーバーで実行されている、データプロバイダ名の配列を返します。
	 * 
	 * @return String[] データプロバイダ名の配列
	 */
	public List getDataProviders() {
		DataProvider[] dps = Manager.getInstance().getDataProviders();
		ArrayList retData = new ArrayList(dps.length);
		for (int i = 0; i < dps.length; i++) {
			DataProvider dp = dps[i];
			DataProviderDesc desc =
				new DataProviderDesc(dp.getDataProviderName(), getClass(dp));
			retData.add(desc);
		}
		return retData;
	}

	private Class getClass(DataProvider dp) {
		return WifeDataProvider.class.isAssignableFrom(dp.getClass())
			? WifeDataProvider.class
			: dp.getClass();
	}

	/**
	 * データホルダー生成オブジェクトの配列を返します。
	 * 
	 * @param dataProvider データプロバイダ名
	 * @return CreateHolderData[] データホルダー生成オブジェクトの配列
	 */
	public List getCreateHolderDatas(String dataProvider)
			throws RemoteException {
		DataProvider dp = Manager.getInstance().getDataProvider(dataProvider);
		ArrayList datas = new ArrayList(dp.getDataHolderCount());
		for (Iterator i = dp.getDataHolderValues().iterator(); i.hasNext();) {
			DataHolder dh = (DataHolder) i.next();
			String s = dh.getDataHolderName();
			Object obj = dh.getValue();
			if (obj instanceof WifeData) {
				WifeData wd = (WifeData) dh.getValue();
				ConvertValue cv = null;
				Map demand = null;
				if (wd instanceof WifeDataAnalog
					|| wd instanceof WifeDataAnalog4) {
					cv =
						(ConvertValue) dh
							.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
					demand =
						(Map) dh.getParameter(DemandDataReferencer.GRAPH_DATA);
				}
				Date date = dh.getTimeStamp();
				QualityFlag qualityFlag = dh.getQualityFlag();
				CreateHolderData data =
					new CreateHolderData(
						s,
						wd,
						cv,
						demand,
						date,
						qualityFlag,
						dataProvider);
				if (logger.isDebugEnabled()) {
					logger.debug("CreateHolderData:" + data);
				}
				datas.add(data);
			}
		}
		return datas;
	}

	/**
	 * @see org.F11.scada.data.DataAccessable#getAlarmJournal(java.sql.Timestamp,
	 *      java.lang.String, java.lang.String)
	 */
	public SortedMap getAlarmJournal(long t, String provider, String holder)
			throws RemoteException {
		DataHolder dh = Manager.getInstance().findDataHolder(provider, holder);
		AlarmTableModel model = (AlarmTableModel) dh.getValue();
		return model.getAlarmJournal(t);
	}

	/**
	 * そのポイントが含まれるオブジェクトを設定します。
	 * 
	 * @param nb 新ポイント情報
	 * @param ob 旧ポイント情報
	 * @since 1.0.3
	 */
	public void setPageEditTime(PointTableBean nb, PointTableBean ob)
			throws RemoteException {

		logger.info("Change : " + ob + " -> " + nb);

		addPoint(nb, ob);
		setSchedule(nb);

		DataHolder dh =
			Manager.getInstance().findDataHolder(
				AlarmDataProvider.PROVIDER_NAME,
				AlarmDataProvider.CAREER);
		AlarmTableModel model = (AlarmTableModel) dh.getValue();
		TableUtil.setPoint(model, nb, ob);

		dh =
			Manager.getInstance().findDataHolder(
				AlarmDataProvider.PROVIDER_NAME,
				AlarmDataProvider.HISTORY);
		model = (AlarmTableModel) dh.getValue();
		TableUtil.setPoint(model, nb, ob);

		dh =
			Manager.getInstance().findDataHolder(
				AlarmDataProvider.PROVIDER_NAME,
				AlarmDataProvider.SUMMARY);
		model = (AlarmTableModel) dh.getValue();
		TableUtil.setPoint(model, nb, ob);
	}

	private void setSchedule(PointTableBean nb) {
		Integer key = new Integer(nb.getPoint());
		if (null != schedules && schedules.containsKey(key)) {
			DataHolder dh = (DataHolder) schedules.get(key);
			WifeDataSchedule data = (WifeDataSchedule) dh.getValue();
			dh.setValue(
				data.setGroupName(nb.getName()),
				new Date(),
				WifeQualityFlag.GOOD);
		}
	}

	private void addPoint(PointTableBean point, PointTableBean old) {
		long now = System.currentTimeMillis();
		Long t = null;
		for (long i = 0; i < Long.MAX_VALUE; i++) {
			t = new Long(now + i);
			if (!pointMap.containsKey(t)) {
				break;
			}
		}
		PointTableBean[] tbs = new PointTableBean[2];
		tbs[0] = point;
		tbs[1] = old;
		pointMap.put(t, tbs);
		trimJournal();
	}

	private void trimJournal() {
		for (int size = pointMap.size(), cnt = size - POINT_MAP_MAX_COUNT; cnt > 0; cnt--) {
			pointMap.remove(pointMap.firstKey());
		}
	}

	/**
	 * タイムスタンプで指定された以上のジャーナルデータを返します。
	 * 
	 * @param t タイムスタンプのLong値
	 * @return SortedMap 更新日時と PointTableBean オブジェクトのマップ
	 * @since 1.0.3
	 */
	public SortedMap getPointJournal(long t) throws RemoteException {
		return new TreeMap(pointMap.tailMap(new Long(t + 1)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.data.DataAccessable#getCreateHolderDatas(org.F11.scada.server.register.HolderString[])
	 */
	public List getCreateHolderDatas(Collection holderStrings)
			throws RemoteException {

		if (logger.isDebugEnabled()) {
			logger.debug(holderStrings);
		}

		ArrayList datas = new ArrayList(holderStrings.size());
		Manager manager = Manager.getInstance();
		for (Iterator i = holderStrings.iterator(); i.hasNext();) {
			HolderString holderString = (HolderString) i.next();
			DataProvider dp =
				manager.getDataProvider(holderString.getProvider());
			if (dp == null) {
				logger.error("null provider : " + holderString.getProvider());
			}
			if (logger.isDebugEnabled()) {
				if (holderString.getHolder() == null) {
					logger.debug(holderString);
				}
			}
			DataHolder dh = dp.getDataHolder(holderString.getHolder());
			if (dh == null) {
				logger.error("null holder : " + holderString.getHolder());
				continue;
			}
			Object obj = dh.getValue();
			if (obj instanceof WifeData) {
				WifeData wd = (WifeData) dh.getValue();
				ConvertValue cv = null;
				Map demand = null;
				if (wd instanceof WifeDataAnalog
					|| wd instanceof WifeDataAnalog4) {
					cv =
						(ConvertValue) dh
							.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
					demand =
						(Map) dh.getParameter(DemandDataReferencer.GRAPH_DATA);
				}
				Date date = dh.getTimeStamp();
				QualityFlag qualityFlag = dh.getQualityFlag();
				CreateHolderData data =
					new CreateHolderData(
						dh.getDataHolderName(),
						wd,
						cv,
						demand,
						date,
						qualityFlag,
						dp.getDataProviderName());
				datas.add(data);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(datas);
		}
		return datas;
	}

	/**
	 * スケジュールデータを含むデータホルダをマネージャーに設定します。
	 * 
	 * @param point ポイントID
	 * @param dh データホルダ
	 * @return 設定したデータホルダ
	 * @version 2.0.21
	 */
	public DataHolder putScheduleHolder(Integer point, DataHolder dh) {
		if (null == schedules) {
			schedules = new HashMap();
		}
		return (DataHolder) schedules.put(point, dh);
	}

	/**
	 * スケジュールデータを含むデータホルダをマネージャーから削除します。
	 * 
	 * @param point ポイントID
	 * @return 削除したデータホルダ
	 * @version 2.0.21
	 */
	public DataHolder removeScheduleHolder(Integer point) {
		if (null == schedules) {
			return null;
		}
		return (DataHolder) schedules.remove(point);
	}

	/**
	 * サーバーのコマンドを呼び出します。
	 * 
	 * @param command コマンド名
	 * @param args 引数
	 * @return 戻り値があるばあいはオブジェクトが返される、そうでない場合は null が返される。
	 * @throws IllegalArgumentException 指定されたコマンドがサーバーに存在しない場合スローされる
	 */
	public synchronized Object invoke(String command, Object[] args) {
		if (commands.containsKey(command)) {
			InvokeHandler handler = commands.get(command);
			return handler.invoke(args);
		} else {
			throw new IllegalArgumentException("コマンドがサーバーに存在しません : " + command);
		}
	}
}
