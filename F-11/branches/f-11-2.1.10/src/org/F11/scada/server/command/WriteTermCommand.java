/*
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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * 指定されたパスにビット情報を出力するクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WriteTermCommand implements Command {
	/** 固定値 */
	private static byte[] TRUE_DATA = {(byte) 0xFF, (byte) 0xFF};
	private static byte[] FALSE_DATA = {(byte) 0x00, (byte) 0x00};
	/** Logging API */
	private static Logger log = Logger.getLogger(WriteTermCommand.class);
	/** スレッドプール実行クラス */
	private static Executor executor = Executors.newCachedThreadPool();

	/** プロバイダ名 */
	private String provider;
	/** ホルダ名 */
	private String holder;
	/** 書き込む値 */
	private String value;

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * コマンドを実行します
	 * @param evt データ変更イベント
	 */
	public void execute(DataValueChangeEventKey evt) {
		if (null == provider) {
			throw new IllegalStateException("providerが設定されていません");
		}
		if (null == holder) {
			throw new IllegalStateException("holderが設定されていません");
		}
		if (null == value) {
			throw new IllegalStateException("valueが設定されていません");
		}

		try {
			executor.execute(new WriteTermCommandTask(evt));
		} catch (RejectedExecutionException e) {}
	}

	/**
	 * Executor で実行されるタスクのクラスです。
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	private class WriteTermCommandTask implements Runnable {
		/** データ変更イベントの参照 */
		private final DataValueChangeEventKey evt;

		/**
		 * タスクを初期化します
		 * @param evt データ変更イベント
		 */
		WriteTermCommandTask(DataValueChangeEventKey evt) {
			this.evt = evt;
		}

		/**
		 * Executor により実行されるメソッドです。
		 */
		public void run() {
			if (evt.getValue().booleanValue()) {
				DataHolder dh = Manager.getInstance().findDataHolder(provider, holder);
				if (dh != null) {
					WifeData wd = (WifeData) dh.getValue();
					if (wd instanceof WifeDataAnalog) {
						writeAnalog(dh, wd);
					} else if (wd instanceof WifeDataDigital) {
						writeDigital(dh, wd);
					} else {
						log.error("デジタル又はアナログ以外のデータが指定されています。 : " + provider + "_" + holder);
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
				log.error("デジタルデータ書き込みエラー" ,e);
			}
		}

		private void writeAnalog(DataHolder dh, WifeData wd) {
			WifeDataAnalog da = (WifeDataAnalog) wd;
			ConvertValue conv = (ConvertValue) dh.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
			double doubleValue = conv.convertInputValue(value);
			dh.setValue(
				da.valueOf(doubleValue),
				new Date(),
				WifeQualityFlag.GOOD);
			log.info(provider + "_" + holder + " に " + doubleValue + " を書き込み");
			try {
				dh.syncWrite();
			} catch (Exception e) {
				log.error("アナログデータ書き込みエラー" ,e);
			}
		}

		private byte[] getSendData(String value) {
		    return Boolean.valueOf(value).booleanValue() ? TRUE_DATA : FALSE_DATA;
		}
	}
}
