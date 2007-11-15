/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/demand/DemandGraphModel.java,v 1.9.2.3 2005/03/11 06:50:47 frdm Exp $
 * $Revision: 1.9.2.3 $
 * $Date: 2005/03/11 06:50:47 $
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
package org.F11.scada.applet.graph.demand;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;

import org.F11.scada.applet.graph.AbstractGraphModel;
import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.server.demand.DemandDataReferencer;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.collections.primitives.DoubleList;

/**
 * デマンドグラフ用のデータホルダより、グラフモデルを生成します。
 * 契約電力 　- (0, 0, グラフの右端, 契約電力値)
 * 目標電力 　- (0, 0, グラフの右端, 目標電力値)
 * 各警報　 　- (警報値, グラフ最大値, 警報値, グラフ最大値)
 * 予想電力　 - (カウンタ値, 現在値, グラフの右端, 予想電力値)
 * 過去電力値 - カウンタ値のパラメタ graphdata(LinkedHashMap) より、
 * 　　　　　   カウンタ値未満の値を抽出しグラフモデルデータを生成。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DemandGraphModel
		extends AbstractGraphModel
		implements DataReferencerOwner, DataValueChangeListener {

	/** DataHolderタイプ情報です。 */
	private static final Class[][] WIFE_TYPE_INFO =
		{ { DataHolder.class, WifeData.class }
	};
	/** 参照するリファレンサです。 */
	private DataReferencer[] dataReferencers;
	private LoggingData[] demandDatas;
	private volatile int rowIndex;

	//------------------------------------------------------//
	// デマンドグラフ固有のプロパティ
	//------------------------------------------------------//
	/** 契約電力 */
	private double contractElectricity;
	/** 目標電力 */
	private double targetElectricity;
	/** 予想電力 */
	private double forecastElectricity;
	/** 警報発生時間 x 座標 */
	private double[] alarmTimes;
	/** カウンタ値 */
	private int counter;
	/** x 座標がカウンタのデータ値 */
	private double currentElectricity;

	/**
	 * デマンドグラフモデルを生成します。
	 */
	public DemandGraphModel(DataReferencer[] dataReferencers) {
		super();
		this.dataReferencers = dataReferencers;
		init();
	}

	private void init() {
		for (int i = 0; i < dataReferencers.length; i++) {
			dataReferencers[i].connect(this);
		}
		changeData();
	}

	public boolean next(String name) {
		synchronized (this) {
			return demandDatas.length > rowIndex;
		}
	}

	public Object get(String name) {
		synchronized (this) {
			return demandDatas[rowIndex++];
		}
	}

	/**
	 * 最初レコードのタイムスタンプを返します。
	 * @return 最初レコードのタイムスタンプ
	 */
	public Object firstKey(String name) {
		synchronized (this) {
			return demandDatas[0].getTimestamp();
		}
	}

	/**
	 * 最終レコードのタイムスタンプを返します。
	 * @return 最終レコードのタイムスタンプ
	 */
	public Object lastKey(String name) {
		synchronized (this) {
			return demandDatas[demandDatas.length - 1].getTimestamp();
		}
	}

	/**
	 * タイムスタンプが引数 key 以前のレコードを検索し、ポインタを位置づけます。
	 * このメソッドで位置づけられたポインタは、 key 以前のレコードを１つ含みます。
	 * 但し、key が先頭レコード以前のレコードを示す場合は、先頭レコードからになります。
	 * @param key 検索するレコードのタイムスタンプ
	 */
	public void findRecord(String name, Timestamp key) {
		rowIndex = 0;
	}

	/**
	 * DataHolderタイプ情報を返します。
	 */
	public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
		return WIFE_TYPE_INFO;
	}

	/**
	 * データ変更イベント処理
	 */
	public void dataValueChanged(DataValueChangeEvent evt) {
		changeData();
		firePropertyChange(null, null);
	}

	private void changeData() {
//		logger.debug("");
		contractElectricity = doConvert(dataReferencers[0]);
		targetElectricity = doConvert(dataReferencers[1]);

		alarmTimes = new double[2];
		alarmTimes[0] = doConvert(dataReferencers[2]);
		alarmTimes[1] = doConvert(dataReferencers[3]);

		currentElectricity = doConvert(dataReferencers[4]);
		forecastElectricity = doConvert(dataReferencers[5]);
		counter = (int) getValue(dataReferencers[6]);

		DataHolder dh = dataReferencers[6].getDataHolder();
		if (dh == null) {
			logger.debug("counter data id null");
			return;
		}
		Map map = (Map) dh.getParameter(DemandDataReferencer.GRAPH_DATA);
		if (map == null) {
			logger.debug("demand graph data id null");
			return;
		}
		synchronized (this) {
			demandDatas = new LoggingData[counter + 1];
			for (int i = 0; i < counter; i++) {
				DoubleList list = new ArrayDoubleList();
				Object obj = map.get(new Integer(i));
				if (obj == null) {
					list.add(0);
				} else {
				    Double d = (Double)obj;
					list.add(d.doubleValue());
				}
				demandDatas[i] = new LoggingData(convertTimestamp(i), list);
			}
			DoubleList list = new ArrayDoubleList();
			list.add(currentElectricity);
			demandDatas[counter] = new LoggingData(convertTimestamp(counter), list);
		}
	}

	private double getValue(DataReferencer dr) {
		DataHolder dh = dr.getDataHolder();
		if (dh == null) {
			logger.debug("DataHolder Not found");
			return 0;
		} else {
			WifeDataAnalog wa = (WifeDataAnalog) dh.getValue();
			return wa.doubleValue();
		}
	}
	
	private double doConvert(DataReferencer dr) {
		DataHolder dh = dr.getDataHolder();
		if (dh == null) {
			logger.debug("DataHolder Not found");
			return 0;
		} else {
			WifeDataAnalog wa = (WifeDataAnalog) dh.getValue();
			ConvertValue convertValue =
				(ConvertValue) dh.getParameter(WifeDataProvider.PARA_NAME_CONVERT);

			return convertValue.convertDoubleValue(wa.doubleValue());
		}
	}

	// カウンタをタイムスタンプに変換する
	private Timestamp convertTimestamp(int count) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);

		for (int i = 0; i < count; i++) {
			cal.add(Calendar.SECOND, 30);
		}

		return new Timestamp(cal.getTimeInMillis());
	}

	public double getContractElectricity() {
		return contractElectricity;
	}

	public double getTargetElectricity() {
		return targetElectricity;
	}

	public double getForecastElectricity() {
		return forecastElectricity;
	}

	public double[] getAlarmTimes() {
		return alarmTimes;
	}

	public int getCounter() {
		return counter;
	}

	public double getCurrentElectricity() {
		return currentElectricity;
	}

	public void disConnect() {
		for (int i = 0; i < dataReferencers.length; i++) {
			dataReferencers[i].disconnect(this);
		}
	}
	
    public void start() {
    }
    
    public void stop() {
    }
}
