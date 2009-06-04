/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.command;

import java.util.Date;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

/**
 * Executor で実行されるタスクのクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WriteTermCommandTask implements Runnable {
	/** ロギングAPI */
	private final Logger log = Logger.getLogger(WriteTermCommandTask.class);
	/** 固定値 */
	private static final byte[] TRUE_DATA = { (byte) 0xFF, (byte) 0xFF };
	private static final byte[] FALSE_DATA = { (byte) 0x00, (byte) 0x00 };
	/** データ変更イベントの参照 */
	private final DataValueChangeEventKey evt;
	/** プロバイダ名 */
	private final String provider;
	/** ホルダ名 */
	private final String holder;
	/** 書き込む値 */
	private final String value;

	/**
	 * タスクを初期化します
	 * 
	 * @param evt データ変更イベント
	 */
	WriteTermCommandTask(
			DataValueChangeEventKey evt,
			String provider,
			String holder,
			String value) {
		this.evt = evt;
		this.provider = provider;
		this.holder = holder;
		this.value = value;
	}

	/**
	 * Executor により実行されるメソッドです。
	 */
	public void run() {
		if (evt.getValue().booleanValue()) {
			DataHolder dh =
				Manager.getInstance().findDataHolder(provider, holder);
			if (dh != null) {
				WifeData wd = (WifeData) dh.getValue();
				if (wd instanceof WifeDataAnalog) {
					writeAnalog(dh, wd);
				} else if (wd instanceof WifeDataDigital) {
					writeDigital(dh, wd);
				} else {
					log.error("デジタル又はアナログ以外のデータが指定されています。 : "
						+ provider
						+ "_"
						+ holder);
				}
			} else {
				log.warn(provider + "_" + holder + " が登録されていません");
			}
		}
	}

	private void writeDigital(DataHolder dh, WifeData wd) {
		WifeDataDigital dd = (WifeDataDigital) wd;
		dh.setValue(
			(WifeData) dd.valueOf(getSendData(value)),
			new Date(),
			WifeQualityFlag.GOOD);
		log.info(provider + "_" + holder + " に " + value + " を書き込み");
		try {
			dh.syncWrite();
		} catch (Exception e) {
			log.error("デジタルデータ書き込みエラー", e);
		}
	}

	private void writeAnalog(DataHolder dh, WifeData wd) {
		WifeDataAnalog da = (WifeDataAnalog) wd;
		ConvertValue conv =
			(ConvertValue) dh.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
		double doubleValue = conv.convertInputValue(value);
		dh.setValue(da.valueOf(doubleValue), new Date(), WifeQualityFlag.GOOD);
		log.info(provider + "_" + holder + " に " + doubleValue + " を書き込み");
		try {
			dh.syncWrite();
		} catch (Exception e) {
			log.error("アナログデータ書き込みエラー", e);
		}
	}

	private byte[] getSendData(String value) {
		return Boolean.valueOf(value).booleanValue() ? TRUE_DATA : FALSE_DATA;
	}
}
