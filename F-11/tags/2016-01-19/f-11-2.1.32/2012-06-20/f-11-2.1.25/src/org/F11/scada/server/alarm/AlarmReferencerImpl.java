/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/Attic/AlarmReferencerImpl.java,v 1.1.2.5 2007/10/03 10:24:00 frdm Exp $
 * $Revision: 1.1.2.5 $
 * $Date: 2007/10/03 10:24:00 $
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

package org.F11.scada.server.alarm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;
import jp.gr.javacons.jim.Manager;
import jp.gr.javacons.jim.QualityFlag;

import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.table.SoundStrategy;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.xwife.server.AlarmDataProvider;
import org.apache.log4j.Logger;

/**
 * 警報・状態をデータベースに反映するリファレンサです。
 * AbstractTableModelを継承しているため、内容をJTableに一覧表示することができます。
 * TabelModelインプリメントは本番では不要？
 */
public class AlarmReferencerImpl extends AbstractTableModel implements
		DataReferencerOwner, DataValueChangeListener,
		DelayDataValueChangeListener, AlarmReferencer {
	private static final long serialVersionUID = -4066513908135069275L;
	private static final Class[][] TYPE_INFO = { {
		DataHolder.class,
		WifeData.class } };
	/**
	 * 取得するデータのリファレンサ
	 */
	private SortedSet referencers;
	private volatile boolean isCreateTableModel;
	private List rowList;
	private String[] title = { "ポイント", "プロバイダ名", "ホルダ名", "値" };
	private List alarmDataStores;
	private EventDelayer delayer;
	private final Logger logger = Logger.getLogger(AlarmReferencerImpl.class);
	private volatile boolean isInit = true;
	/** 警報音発生ロジック */
	private SoundStrategy soundStrategy;
	private ItemDao itemDao;

	public AlarmReferencerImpl() {
		super();
		alarmDataStores = new ArrayList();
		delayer = new EventDelayer(this);
	}

	/**
	 * @deprecated Ver.2からDI(seasar)を使用したオブジェクト生成に変更されました。
	 * @param alarmDataStores
	 */
	public AlarmReferencerImpl(Collection alarmDataStores) {
		this();
		if (alarmDataStores == null) {
			throw new IllegalArgumentException("alarmDataStores is null.");
		}
		this.alarmDataStores = new ArrayList(alarmDataStores);
	}

	public void setSoundStrategy(SoundStrategy soundStrategy) {
		logger.info("SoundStrategy Initialize.");
		this.soundStrategy = soundStrategy;
	}

	public void setItemDao(ItemDao itemDao) {
		logger.info("ItemDao Initialize.");
		this.itemDao = itemDao;
	}

	private void fireDataValueChangeEventKey(DataValueChangeEventKey evt) {
		initAlarm(evt);

		for (Iterator it = alarmDataStores.iterator(); it.hasNext();) {
			AlarmDataStore st = (AlarmDataStore) it.next();
			st.put(evt);
		}
	}

	private void initAlarm(DataValueChangeEventKey evt) {
		if (isInit) {
			Item item = itemDao.getItem(getHolderString(evt));
			// 音有り警報が入った場合、初期警報音フラグをオン
			logger.info("new event = " + evt.toString());
			Integer sountType =
				soundStrategy.getSoundType(
					item.getAttribute().getSoundType(),
					evt);
			if (evt.getValue().booleanValue() && sountType != 0) {
				DataHolder dh =
					Manager.getInstance().findDataHolder(
						AlarmDataProvider.PROVIDER_NAME,
						AlarmDataProvider.INIT_ALARM);
				dh.setValue(
					WifeDataDigital.valueOfTrue(0),
					new Date(),
					WifeQualityFlag.GOOD);

				DataHolder sdh =
					Manager.getInstance().findDataHolder(
						AlarmDataProvider.PROVIDER_NAME,
						AlarmDataProvider.INIT_ALARM_SOUND);
				sdh.setValue(
					item.getOnSoundPath(),
					new Date(),
					WifeQualityFlag.GOOD);

				isInit = false;
			}
		}
	}

	private HolderString getHolderString(DataValueChangeEventKey evt) {
		return new HolderString(evt.getProvider(), evt.getHolder());
	}

	public void addReferencer(DataReferencer rf) {
		if (referencers == null) {
			referencers = new TreeSet(new AlarmComparator());
		}
		rf.connect(this);
		referencers.add(rf);
		isCreateTableModel = true;
	}

	public void removeReferencer(DataReferencer rf) {
		if (referencers != null) {
			rf.disconnect(this);
			referencers.remove(rf);
		}
	}

	public boolean addDataStore(AlarmDataStore store) {
		return alarmDataStores.add(store);
	}

	public SortedSet getReferencers() {
		if (referencers == null) {
			return new TreeSet(Collections.EMPTY_SET);
		}
		return Collections.unmodifiableSortedSet(referencers);
	}

	public void dataValueChanged(DataValueChangeEvent evt) {
		Object o = evt.getSource();
		if (!(o instanceof DataHolder)) {
			return;
		}
		if (((DataHolder) o).getQualityFlag().getQuality() != QualityFlag.GOOD) {
			return;
		}
		if (!evt.isInit2good()) {
			delayer.fireDelayedDataValueChange(evt);
		}
	}

	public void delayedDataValueChanged(DataValueChangeEvent evt) {
		fireDataValueChangeEventKey(new DataValueChangeEventKey(evt));
		fireTableChanged(new TableModelEvent(this));
	}

	public int getColumnCount() {
		return title.length;
	}

	public Object getValueAt(int row, int col) {
		if (referencers == null) {
			return null;
		}
		if (isCreateTableModel) {
			rowList = new ArrayList(referencers);
			isCreateTableModel = false;
		}
		DataReferencer dr = (DataReferencer) rowList.get(row);
		switch (col) {
		case 0:
			if (dr.getDataHolder() != null) {
				return dr.getDataHolder().getParameter("point");
			}
		case 1:
			return dr.getDataProviderName();
		case 2:
			return dr.getDataHolderName();
		case 3:
			if (dr.getDataHolder() != null) {
				Object o = dr.getDataHolder().getValue();
				if (o instanceof WifeDataDigital) {
					if (!((WifeDataDigital) o).toString().equals("false")) {
						return "ON";
					} else {
						return "OFF";
					}
				} else {
					logger.error("デジタルホルダではありません = "
						+ dr.getDataProviderName()
						+ "_"
						+ dr.getDataHolderName());
					return "エラー";
				}
			} else {
				logger.info("hoder null = "
					+ dr.getDataProviderName()
					+ "_"
					+ dr.getDataHolderName());
				return "エラー";
			}
		default:
			logger.info("カラム番号が異常です = " + col);
			return "エラー";
		}
	}

	public int getRowCount() {
		if (referencers == null) {
			return 0;
		}
		return referencers.size();
	}

	public String getColumnName(int col) {
		return title[col];
	}

	public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
		return TYPE_INFO;
	}
}
