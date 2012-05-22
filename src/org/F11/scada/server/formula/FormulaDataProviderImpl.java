/*
 * $Header:
 * /cvsroot/f-11/F-11/src/org/F11/scada/xwife/server/impl/Attic/WifeDataProviderImpl.java,v
 * 1.1.2.34 2007/10/19 10:07:00 frdm Exp $ $Revision: 1.1.2.34 $ $Date:
 * 2007/10/19 10:07:00 $
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.F11.scada.server.formula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JComponent;

import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Globals;
import org.F11.scada.WifeException;
import org.F11.scada.applet.expression.Expression;
import org.F11.scada.data.HolderData;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.demand.DemandDataReferencer;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.formula.dto.ItemFormulaDto;
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
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FormulaDataProviderImpl extends AbstractDataProvider
		implements
			DataValueChangeListener,
			WifeDataProvider {
	private static final long serialVersionUID = 5430950773728378252L;
	private static Logger logger = Logger.getLogger(FormulaDataProviderImpl.class);
	private static final String PROVIDER_NAME = "FORMULA";
	private static final Class[][] TYPE_INFO = {{DataHolder.class,
			WifeData.class}};

	/** 通知元データホルダ名から保持データホルダ名へのマップです。 */
	private final Map<String, Set<String>> ref2dhset = new HashMap<String, Set<String>>();

	private final SortedMap holderJurnal;
	private final ItemDao itemDao;
	private final HolderRegisterBuilder builder;
	private SendRequestSupport sendRequestSupport;
	private final Lock lock = new ReentrantLock();
	/** クライアントとの差分データ取得時のオフセット(時間がずれる為少し前からジャーナルを取得する) */
	private final int getDataOffset;
	private final ItemFormulaService itemFormulaService;

	/**
	 * コンストラクタ
	 */
	public FormulaDataProviderImpl(ItemDao itemDao,
			HolderRegisterBuilder builder, AlarmReferencer alarm,
			AlarmReferencer demand, ItemFormulaService service)
			throws Exception {
		this(32, itemDao, builder, alarm, demand, service);
	}

	public FormulaDataProviderImpl(int holderSize, ItemDao itemDao,
			HolderRegisterBuilder builder, AlarmReferencer alarm,
			AlarmReferencer demand, ItemFormulaService service)
			throws Exception {
		super(holderSize);
		setDataProviderName(PROVIDER_NAME);
		this.itemDao = itemDao;
		this.builder = builder;
		this.holderJurnal = Collections.synchronizedSortedMap(new SingletonSortedMap());
		this.itemFormulaService = service;

		Manager.getInstance().addDataProvider(this);

		setParameter(PARA_NAME_ALARM, alarm);
		setParameter(PARA_NAME_DEMAND, demand);
		createHolders();
		int offset = Integer.parseInt(EnvironmentManager.get(
				"/server/getDataOffset", "-5000"));
		getDataOffset = Math.min(-5000, offset);
		logger.info("getDataOffset=" + getDataOffset);
	}

	public void start() {
		List<ItemFormulaDto> list = itemFormulaService.findAll();
		for (Iterator<ItemFormulaDto> it = list.iterator(); it.hasNext();) {
			ItemFormulaDto formulaDto = it.next();
			// 計算ホルダを検索
			DataHolder dh = getDataHolder(formulaDto.getHolder());
			if (dh == null) {
				logger.warn(PROVIDER_NAME + " 格納ホルダ無し " + formulaDto.getHolder());
				continue;
			}

			// データホルダに計算式を保持
			Expression expr = new Expression();
			expr.toPostfix(formulaDto.getFormula());
			dh.setParameter(WifeDataProvider.PARA_NAME_EXPRESSION, expr);

			// 式中のデータホルダ名をキーに、データホルダを追加
			for (Iterator<String> ref_it = expr.getProviderHolderNames().iterator(); ref_it.hasNext();) {
				String name = ref_it.next();
				DataHolder ref_dh = Manager.getInstance().findDataHolder(name);
				if (ref_dh == null) {
					logger.warn(PROVIDER_NAME + " 参照ホルダなし " + name);
					continue;
				}
				String key = ref_dh.getDataProvider().getDataProviderName()
						+ WifeDataProvider.SEPARATER
						+ ref_dh.getDataHolderName();
				Set<String> dhset = ref2dhset.get(key);
				if (dhset == null) {
					dhset = new HashSet<String>();
					ref2dhset.put(key, dhset);
				}
				dhset.add(dh.getDataHolderName());
			}
		}
		// 式中のデータホルダのDataValueChangeListenerへ自身を登録
		for (Iterator<String> it = ref2dhset.keySet().iterator(); it.hasNext();) {
			DataHolder ref_dh = Manager.getInstance().findDataHolder(
					(String) it.next());
			ref_dh.addDataValueChangeListener(this);
		}
	}

	public void stop() {
	}

	public void run() {
	}

	private void setJurnal(byte[] readData, long entryTime, DataHolder dh) {
		TimeIncrementWrapper.put(entryTime, new HolderData(
				dh.getDataHolderName(), readData, entryTime,
				(Map) dh.getParameter(DemandDataReferencer.GRAPH_DATA)),
				holderJurnal);
	}

	public Class[][] getProvidableDataHolderTypeInfo() {
		return TYPE_INFO;
	}

	private void createHolders() {
		Item[] items = itemDao.getSystemItems(getDataProviderName(), true);
		builder.register(items);
	}

	/**
	 * 引数のlong値(更新日付のlong値)より上のHolderDataを返します。
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
		sendRequestSupport.setSendRequestDateMap(session,
				System.currentTimeMillis());
		return list;
	}

	public String toString() {
		return "dataProvider=" + getDataProviderName();
	}

	public void setSendRequestSupport(SendRequestSupport sendRequestSupport) {
		this.sendRequestSupport = sendRequestSupport;
	}

	public void lock() {
		lock.lock();
	}

	public void unlock() {
		lock.unlock();
	}

	public void addJurnal(long entryDate, WifeData value) {
		synchronized (holderJurnal) {
			TimeIncrementWrapper.put(entryDate, new HolderData(
				Globals.ERR_HOLDER,
				value.toByteArray(),
				entryDate,
				null), holderJurnal);
		}
	}

	public void dataValueChanged(DataValueChangeEvent evt) {
		lock();
		try {
			DataHolder src_dh = (DataHolder) evt.getSource();
			String key = src_dh.getDataProvider().getDataProviderName()
					+ WifeDataProvider.SEPARATER + src_dh.getDataHolderName();
			Set<String> dhset = ref2dhset.get(key);
			if (dhset == null) {
				// 更新すべきデータホルダが無い
				logger.warn(PROVIDER_NAME + " 格納ホルダ無し " + key);
				return;
			}
			// データホルダ更新
			for (Iterator<String> it = dhset.iterator(); it.hasNext();) {
				DataHolder dh = getDataHolder(it.next());
				Expression expr = (Expression) dh.getParameter(WifeDataProvider.PARA_NAME_EXPRESSION);
				long entryTime = System.currentTimeMillis();
				try {
					WifeData wdata;
					if (expr.booleanValue())
						wdata = WifeDataDigital.valueOfTrue(0);
					else
						wdata = WifeDataDigital.valueOfFalse(0);
					if (!wdata.equals(dh.getValue())) {
						dh.setValue(
								wdata,
								new Date(entryTime),
								WifeQualityFlag.GOOD,
								(WifeQualityFlag.INITIAL == dh.getQualityFlag()));
						synchronized (holderJurnal) {
							setJurnal(wdata.toByteArray(), entryTime, dh);
						}
						// TODO 警報抜け調査
						if (logger.isDebugEnabled()) {
							if (wdata instanceof WifeDataDigital) {
								WifeDataDigital wdd = (WifeDataDigital) wdata;
								FastDateFormat f = FastDateFormat.getInstance("yyyy/MM/dd HH:mm:ss");
								logger.debug("Holder=" + dh.getDataHolderName()
										+ " Time="
										+ f.format(dh.getTimeStamp())
										+ " Data=" + wdd.toString());
							}
						}
					}
				} catch (WifeException e) {
					logger.debug("参照先ホルダが未更新、又は結果が論理値で無い Holder="
							+ dh.getDataHolderName(), e);
				} catch (Exception e) {
					logger.error(
							"データホルダ更新エラー Holder=" + dh.getDataHolderName(), e);
				}
			}
		} finally {
			unlock();
		}
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

	public void syncRead(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void syncRead(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void syncRead(Set defines)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void syncWrite(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public JComponent getDataParameterEditor(DataHolder dh) {
		throw new java.lang.UnsupportedOperationException();
	}

}
