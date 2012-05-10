/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/xwife/server/impl/Attic/WifeDataProviderImpl.java,v 1.1.2.34 2007/10/19 10:07:00 frdm Exp $
 * $Revision: 1.1.2.34 $
 * $Date: 2007/10/19 10:07:00 $
 *
 * =============================================================================
 * Projrct    F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All
 * Rights Reserved.
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

package org.F11.scada.xwife.server.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JComponent;

import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Globals;
import org.F11.scada.data.BCDConvertException;
import org.F11.scada.data.HolderData;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.communicater.CommunicaterFactory;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.demand.DemandDataReferencer;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.frame.SendRequestSupport;
import org.F11.scada.server.register.HolderRegisterBuilder;
import org.F11.scada.util.SingletonSortedMap;
import org.F11.scada.util.TimeIncrementWrapper;
import org.F11.scada.xwife.applet.Session;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

/**
 * データプロバイダクラスです。通信でPLCからデータを取得して、データホルダーに値を設定していきます。
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifeDataProviderImpl extends AbstractDataProvider implements
		WifeDataProvider {
	private static final long serialVersionUID = -4052507689962832517L;
	private static Logger logger = Logger.getLogger(WifeDataProviderImpl.class);
	private static final Class[][] TYPE_INFO = { {
		DataHolder.class,
		WifeData.class } };

	/* スレッド */
	private Thread thread;

	/** 通信モジュールです。 */
	private final Communicater communicater;
	/** 通信定義からデータホルダーへのマップです。 */
	private final Map<WifeCommand, Map<String, DataHolder>> def2holder =
		new ConcurrentHashMap<WifeCommand, Map<String, DataHolder>>();

	private final SortedMap holderJurnal;
	private final ItemDao itemDao;
	private final HolderRegisterBuilder builder;
	private SendRequestSupport sendRequestSupport;
	private final long communicateWaitTime;
	private final Lock lock = new ReentrantLock();
	/** クライアントとの差分データ取得時のオフセット(時間がずれる為少し前からジャーナルを取得する) */
	private final int getDataOffset;
	private final boolean isPageChangeInterrupt;
	/** 割り込みで飛ばされた通信コマンド */
	private final Queue<Set<WifeCommand>> unExecuteCommands;
	private final long initWaitTime;

	/**
	 * コンストラクタ
	 */
	public WifeDataProviderImpl(Environment plc,
			ItemDao itemDao,
			HolderRegisterBuilder builder,
			AlarmReferencer alarm,
			AlarmReferencer demand,
			CommunicaterFactory communicaterFactory) throws Exception {
		this(32, plc, itemDao, builder, alarm, demand, communicaterFactory);
	}

	public WifeDataProviderImpl(int holderSize,
			Environment plc,
			ItemDao itemDao,
			HolderRegisterBuilder builder,
			AlarmReferencer alarm,
			AlarmReferencer demand,
			CommunicaterFactory communicaterFactory) throws Exception {
		super(holderSize);
		setDataProviderName(plc.getDeviceID());
		this.itemDao = itemDao;
		this.builder = builder;
		holderJurnal =
			Collections.synchronizedSortedMap(new SingletonSortedMap());

		Manager.getInstance().addDataProvider(this);
		try {
			communicater = communicaterFactory.createCommunicator(plc);
		} catch (ClassNotFoundException ex) {
			throw new NoClassDefFoundError(ex.getMessage());
		}

		setParameter(PARA_NAME_ALARM, alarm);
		setParameter(PARA_NAME_DEMAND, demand);
		createHolders();
		long wait =
			Long.parseLong(EnvironmentManager.get(
				"/server/communicateWaitTime",
				"500"));
		communicateWaitTime = Math.max(500, wait);
		int offset =
			Integer.parseInt(EnvironmentManager.get(
				"/server/getDataOffset",
				"-5000"));
		getDataOffset = Math.min(-5000, offset);
		logger.info("getDataOffset=" + getDataOffset);
		isPageChangeInterrupt =
			Boolean
				.valueOf(
					EnvironmentManager.get(
						"/server/isPageChangeInterrupt",
						"true")).booleanValue();
		unExecuteCommands = new LinkedBlockingQueue<Set<WifeCommand>>();
		initWaitTime = getInitWaitTime();
		logger.info("initWaitTime=" + initWaitTime);
	}

	private long getInitWaitTime() {
		try {
			return Long.parseLong(EnvironmentManager.get(
				"/server/initWaitTime",
				"500"));
		} catch (NumberFormatException e) {
			return 500L;
		}
	}

	/**
	 * データホルダをこのプロバイダに追加します。パフォーマンスを維持する為、あえて排他処理をしていません。
	 * スレッドが起動した後にホルダの追加や削除をする場合は、かならず{@link #lock()}
	 * メソッドを呼び出して、スレッドのロックを取得してください。追加や削除が完了した後はや{@link #unlock()}
	 * メソッドを呼び出してロックを外してください。
	 *
	 * @param dh 追加するデータホルダ
	 *
	 * @return データホルダの追加ができた場合 true をそうでない場合 false を返します。
	 */
	public boolean addDataHolder(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		if (isCycleRead(dh)) {
			WifeCommand dh_define =
				(WifeCommand) dh.getParameter(PARA_NAME_COMAND);

			if (def2holder.containsKey(dh_define)) {
				Map<String, DataHolder> dhmap = def2holder.get(dh_define);
				if (!dhmap.containsKey(dh.getDataHolderName())) {
					dhmap.put(dh.getDataHolderName(), dh);
				}
			} else {
				LinkedHashMap<String, DataHolder> dhmap =
					new LinkedHashMap<String, DataHolder>();
				dhmap.put(dh.getDataHolderName(), dh);
				def2holder.put(dh_define, dhmap);
			}

			ArrayList<WifeCommand> defines = new ArrayList<WifeCommand>();
			defines.add(dh_define);
			communicater.addReadCommand(defines);
		}
		return super.addDataHolder(dh);
	}

	/**
	 * データホルダをこのプロバイダから削除します。パフォーマンスを維持する為、あえて排他処理をしていません。
	 * スレッドが起動した後にホルダの追加や削除をする場合は、かならず{@link #lock()}
	 * メソッドを呼び出して、スレッドのロックを取得してください。追加や削除が完了した後はや{@link #unlock()}
	 * メソッドを呼び出してロックを外してください。
	 *
	 * @param dh 削除するデータホルダ
	 *
	 * @return データホルダの削除ができた場合 true をそうでない場合 false を返します。
	 */
	public boolean removeDataHolder(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		if (isCycleRead(dh)) {
			WifeCommand dh_define =
				(WifeCommand) dh.getParameter(PARA_NAME_COMAND);
			if (def2holder.containsKey(dh_define)) {
				Map<String, DataHolder> dhmap = def2holder.get(dh_define);
				if (dhmap.containsKey(dh.getDataHolderName())) {
					dhmap.remove(dh.getDataHolderName());
				}
				if (dhmap.isEmpty()) {
					def2holder.remove(dh_define);
				}
			}
			ArrayList<WifeCommand> defines = new ArrayList<WifeCommand>();
			defines.add(dh_define);
			communicater.removeReadCommand(defines);
		}

		return super.removeDataHolder(dh);
	}

	public void syncRead(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		LinkedHashSet list = new LinkedHashSet();
		list.add(dh.getParameter(PARA_NAME_COMAND));
		syncRead(list, false);
	}

	/**
	 * ページ切り替え再表示の時に呼び出される。
	 *
	 * @param dhs 再読み込みするデータホルダ
	 */
	public void syncRead(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		if (isNetError()) {
			return;
		}
		LinkedHashSet defines = new LinkedHashSet(dhs.length);
		for (int i = 0; i < dhs.length; i++) {
			DataHolder dh = dhs[i];
			if (isCycleRead(dh)) {
				defines.add(dh.getParameter(PARA_NAME_COMAND));
			}
		}
		syncRead(defines, false, true);
	}

	private boolean isNetError() {
		return isNetError(
			getDataHolder(Globals.ERR_HOLDER),
			WifeDataDigital.valueOfTrue(0));
	}

	/**
	 * ページ切り替えの初期読み出しでのみ呼び出し可能です。このメソッドを呼び出す前にlockを持っていなければなりません。
	 *
	 * @param defines
	 * @param sameDataBalk
	 * @param waitInitWait
	 */
	private void syncRead(Set defines,
			boolean sameDataBalk,
			boolean waitInitWait) {
		DataHolder errdh = getDataHolder(Globals.ERR_HOLDER);
		Map bytedataMap = Collections.EMPTY_MAP;
		try {
			if (waitInitWait) {
				Thread.sleep(initWaitTime);
			}
			bytedataMap = communicater.syncRead(defines, sameDataBalk);
			if (sameDataBalk
				&& isNetError(errdh, WifeDataDigital.valueOfTrue(0))) {
				setErrorHolder(errdh, WifeDataDigital.valueOfFalse(0));
			}
		} catch (InterruptedException e) {
			unExecuteCommands.offer(defines);
			return;
		} catch (Exception e) {
			if (sameDataBalk
				&& isNetError(errdh, WifeDataDigital.valueOfFalse(0))) {
				setErrorHolder(errdh, WifeDataDigital.valueOfTrue(0));
			}
			logger.warn("通信エラー", e);
		}
		for (Iterator it = bytedataMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			WifeCommand wc = (WifeCommand) entry.getKey();
			byte[] data = (byte[]) entry.getValue();
			setByteData(wc, data);
		}
	}

	private void syncRead(Set defines, boolean sameDataBalk) {
		// ここでは interrupt をかけないこと
		lock.lock();
		try {
			syncRead(defines, sameDataBalk, false);
		} finally {
			lock.unlock();
		}
	}

	public void syncRead(Set defines) {
		syncRead(defines, true);
	}

	private void setErrorHolder(DataHolder errdh, WifeData value) {
		long entryDate = System.currentTimeMillis();
		errdh.setValue(value, new Date(entryDate), WifeQualityFlag.GOOD);
		synchronized (holderJurnal) {
			TimeIncrementWrapper.put(entryDate, new HolderData(
				Globals.ERR_HOLDER,
				value.toByteArray(),
				entryDate,
				null), holderJurnal);
		}
	}

	private boolean isNetError(DataHolder errdh, WifeDataDigital digital) {
		return errdh != null && digital.equals(errdh.getValue());
	}

	/**
	 * PLCに非同期書込
	 */
	public void syncWrite(DataHolder dh) {
		Map defdata = new HashMap();
		WifeCommand def_h = (WifeCommand) dh.getParameter(PARA_NAME_COMAND);
		WifeData wd = (WifeData) dh.getValue();
		defdata.put(def_h, wd.toByteArray());

		DataHolder errdh = getDataHolder(Globals.ERR_HOLDER);
		try {
			communicater.syncWrite(defdata);
			if (isNetError(errdh, WifeDataDigital.valueOfTrue(0))) {
				setErrorHolder(errdh, WifeDataDigital.valueOfFalse(0));
			}
		} catch (Exception e) {
			if (isNetError(errdh, WifeDataDigital.valueOfFalse(0))) {
				setErrorHolder(errdh, WifeDataDigital.valueOfTrue(0));
			}
			logger.warn("書込み通信エラー:", e);
		}

		// 他のクライアントへ通知する
		long entryTime = dh.getTimeStamp().getTime();
		synchronized (holderJurnal) {
			TimeIncrementWrapper.put(
				entryTime,
				new HolderData(
					dh.getDataHolderName(),
					wd.toByteArray(),
					entryTime,
					(Map) dh.getParameter(DemandDataReferencer.GRAPH_DATA)),
				holderJurnal);
		}
	}

	/**
	 * スタート
	 */
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.setName(getClass().getName() + "-" + getDataProviderName());
			thread.start();
		}
	}

	/**
	 * ストップ
	 */
	public void stop() {
		if (thread != null) {
			Thread th = thread;
			thread = null;
			th.interrupt();
		}
	}

	/**
	 * 一定周期で通信要求をキューに登録します。
	 */
	public void run() {
		Thread thisThread = Thread.currentThread();

		while (thread == thisThread) {
			if (isNetError()) {
				long waitTime = getNodeErrorWaitTime();
				try {
					logger.info("通信エラー " + waitTime + "ミリ秒スレッド停止");
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					continue;
				}
			}
			if (unExecuteCommands.isEmpty()) {
				syncRead(getCommandDefines());
			} else {
				syncRead(unExecuteCommands.poll(), false);
			}
			try {
				Thread.sleep(communicateWaitTime);
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

	private long getNodeErrorWaitTime() {
		try {
			return Long.parseLong(EnvironmentManager.get(
				"/server/nodeErrorWaitTime",
				"0"));
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	private Set getCommandDefines() {
		LinkedHashSet reqdhs = new LinkedHashSet(dataHolders.values().size());
		for (Iterator i = dataHolders.values().iterator(); i.hasNext();) {
			DataHolder dh = (DataHolder) i.next();
			if (isCycleRead(dh)) {
				reqdhs.add(dh.getParameter(PARA_NAME_COMAND));
			}
		}
		return reqdhs;
	}

	/**
	 * 通信モジュールからの通信完了イベントを処理します。
	 */
	private void setByteData(WifeCommand define, byte[] readData) {
		// 受信データをDataHolderに設定
		if (!def2holder.containsKey(define)) {
			return;
		}
		Map<String, DataHolder> dhs = def2holder.get(define);
		for (Iterator<DataHolder> it = dhs.values().iterator(); it.hasNext();) {
			long entryTime = System.currentTimeMillis();
			DataHolder dh = it.next();
			WifeData wdata = (WifeData) dh.getValue();
			WifeQualityFlag flag = (WifeQualityFlag) dh.getQualityFlag();
			WifeData cutwdata = null;
			// System.out.println("setValue : " + dh.getDataHolderName());
			try {
				cutwdata = wdata.valueOf(readData);
				if (WifeQualityFlag.INITIAL == flag || !wdata.equals(cutwdata)) {
					if (WifeQualityFlag.INITIAL == flag) {
						if (!wdata.equals(cutwdata)) {
							dh.setValue(
								cutwdata,
								new Date(entryTime),
								WifeQualityFlag.GOOD);
						} else {
							dh.setValue(
								cutwdata,
								new Date(entryTime),
								WifeQualityFlag.GOOD,
								true);
						}
					} else {
						dh.setValue(
							cutwdata,
							new Date(entryTime),
							WifeQualityFlag.GOOD);
					}
					synchronized (holderJurnal) {
						setJurnal(readData, entryTime, dh);
					}
					// TODO 警報抜け調査
					if (logger.isDebugEnabled()) {
						if (cutwdata instanceof WifeDataDigital) {
							WifeDataDigital wdd = (WifeDataDigital) cutwdata;
							FastDateFormat f =
								FastDateFormat
									.getInstance("yyyy/MM/dd HH:mm:ss");
							logger.debug("Holder="
								+ dh.getDataHolderName()
								+ " Time="
								+ f.format(dh.getTimeStamp())
								+ " Data="
								+ wdd.toString());
						}
					}
				}
			} catch (BCDConvertException e) {
				if (dh.getQualityFlag() != WifeQualityFlag.BAD) {
					dh
						.setValue(
							wdata,
							new Date(entryTime),
							WifeQualityFlag.BAD);
					synchronized (holderJurnal) {
						setJurnal(readData, entryTime, dh);
					}
					logger.error(
						"setValue BAD (BCD Convert error) "
							+ dh.getDataHolderName()
							+ dh.getTimeStamp().toString(),
						e);
				}
			}
		}
	}

	private void setJurnal(byte[] readData, long entryTime, DataHolder dh) {
		TimeIncrementWrapper.put(
			entryTime,
			new HolderData(
				dh.getDataHolderName(),
				readData,
				entryTime,
				(Map) dh.getParameter(DemandDataReferencer.GRAPH_DATA)),
			holderJurnal);
	}

	public Class[][] getProvidableDataHolderTypeInfo() {
		return TYPE_INFO;
	}

	public JComponent getDataParameterEditor(DataHolder dh) {
		throw new java.lang.UnsupportedOperationException();
	}

	private void createHolders() {
		Item[] items = itemDao.getSystemItems(getDataProviderName(), true);
		builder.register(items);
	}

	private boolean isCycleRead(DataHolder dh) {
		Boolean cr = (Boolean) dh.getParameter(PARA_NAME_CYCLEREAD);
		return cr.booleanValue();
	}

	/**
	 * 引数のlong値(更新日付のlong値)より上のHolderDataを返します。
	 *
	 * @param t
	 * @return HolderData[]
	 */
	public List getHoldersData(long t, Session session) {
		if (sendRequestSupport == null) {
			throw new IllegalStateException("sendRequestSupport noting.");
		}
		List list = Collections.EMPTY_LIST;
		synchronized (holderJurnal) {
			SortedMap smap = holderJurnal.tailMap(new Long(t + getDataOffset));
			list = new ArrayList(smap.values());
		}
		sendRequestSupport.setSendRequestDateMap(
			session,
			System.currentTimeMillis());
		return list;
	}

	public String toString() {
		return "dataProvider=" + getDataProviderName();
	}

	public void setSendRequestSupport(SendRequestSupport sendRequestSupport) {
		this.sendRequestSupport = sendRequestSupport;
	}

	// Not used Methods
	public void asyncRead(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncRead(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncWrite(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncWrite(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void syncWrite(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void lock() {
		if (isPageChangeInterrupt) {
			thread.interrupt();
		}
		lock.lock();
	}

	public void unlock() {
		lock.unlock();
	}
}
