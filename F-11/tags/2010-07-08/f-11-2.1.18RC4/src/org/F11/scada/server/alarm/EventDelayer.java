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
package org.F11.scada.server.alarm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataValueChangeEvent;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * チャタリング防止タイマークラス
 * @author Hideaki Maekawa
 */
public class EventDelayer {
	/** 親のデータ変更リスナー */
	private final DelayDataValueChangeListener listener;
	/** ホルダ名:タイマータスクのマップ */
	private final Map tasks;
	private final Timer timer;

	/**
	 * コンストラクタ
	 * @param listener 親のデータ変更リスナー
	 */
	public EventDelayer(DelayDataValueChangeListener listener) {
		this.listener = listener;
		tasks = new HashMap();
		timer = new Timer(true);
	}

	/**
	 * ディレイ値(チャタリング防止タイマー)が設定されている場合は、オフタイマー処理を実行する。オフタイマーが動作している間は、データ変更イベントを無視する。
	 * @param evt データ変更イベント
	 */
	public void fireDelayedDataValueChange(DataValueChangeEvent evt) {
		DataHolder dh = (DataHolder) evt.getSource();

		if (isStartTimer(dh)) {
			if (tasks.containsKey(getHolderId(dh))) {
				runningTask(evt, dh);
			} else {
				stopedTask(evt, dh);
			}
		} else {
			listener.delayedDataValueChanged(evt);
		}
	}

	private String getHolderId(DataHolder dh) {
		DataProvider dp = dh.getDataProvider();
		return dp.getDataProviderName() + "_" + dh.getDataHolderName();
	}

	private void stopedTask(DataValueChangeEvent evt, DataHolder dh) {
		if (isOnOffDigital(evt, false)) {
			Integer delay = (Integer) dh.getParameter(WifeDataProvider.PARA_NAME_OFFDELAY);
			DelayerTask task = new DelayerTask(evt, listener);
			timer.schedule(task, delay.longValue() * 1000L);
			tasks.put(getHolderId(dh), task);
		} else {
			listener.delayedDataValueChanged(evt);
		}
	}

	private void runningTask(DataValueChangeEvent evt, DataHolder dh) {
		String holderId = getHolderId(dh);
		DelayerTask task = (DelayerTask) tasks.get(holderId);
		if (task.isTimerStoped() && isOnOffDigital(evt, true)) {
			listener.delayedDataValueChanged(evt);
			tasks.remove(holderId);
		} else if (!task.isTimerStoped() && isOnOffDigital(evt, true)) {
			task.cancel();
			tasks.remove(holderId);
		}
	}

	private boolean isStartTimer(DataHolder dh) {
		Integer delay = (Integer) dh.getParameter(WifeDataProvider.PARA_NAME_OFFDELAY);
		return null != delay && delay.intValue() > 0L;
	}

	private boolean isOnOffDigital(DataValueChangeEvent evt, boolean b) {
		WifeDataDigital wd = (WifeDataDigital) evt.getValue();
		return wd.isOnOff(b);
	}

	/**
	 * オフタイマーのタスク
	 */
	private static class DelayerTask extends TimerTask {
		private boolean isTimerStoped;
		private final DataValueChangeEvent evt;
		private final DelayDataValueChangeListener listener;

		DelayerTask(DataValueChangeEvent evt, DelayDataValueChangeListener listener) {
			this.evt = evt;
			this.listener = listener;
		}

		public boolean isTimerStoped() {
			return isTimerStoped;
		}

		public void run() {
			listener.delayedDataValueChanged(
					new DataValueChangeEvent(
						evt.getSource(),
						evt.getValue(),
						new Date(),
						evt.getQualityFlag()));
			isTimerStoped = true;
		}
	}
}
