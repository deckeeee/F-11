/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.xwife.applet.alarm;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.xwife.applet.PageChangeEvent;
import org.F11.scada.xwife.applet.PageChanger;
import org.F11.scada.xwife.applet.alarm.event.CheckEvent;
import org.F11.scada.xwife.applet.alarm.event.CheckTableListener;
import org.apache.log4j.Logger;

/**
 * 優先順位による自動ジャンプを制御するクラスです。
 * 
 * @author maekawa
 * 
 */
public class PriorityController implements TableModelListener,
		CheckTableListener {
	private final Logger logger = Logger.getLogger(PriorityController.class);
	/** ページ制御されるオブジェクト */
	private final PageChanger pageChanger;
	/** 優先順位による自動ジャンプを制御の有無 */
	private final boolean isPriorityControl;
	/** 現在最高優先順位のページ情報 */
	private TableRowModel currentAlarm;
	/** 警報音停止タイマー */
	private AlarmTimer timer = new AlarmTimer();
	/** クライアント設定 */
	private final ClientConfiguration configuration;

	public PriorityController(PageChanger pageChanger) {
		this(pageChanger, new ClientConfiguration());
	}

	PriorityController(
			PageChanger pageChanger,
			ClientConfiguration configuration) {
		this.pageChanger = pageChanger;
		this.configuration = configuration;
		currentAlarm = TableRowModel.INIT_ROW_MODEL;
		isPriorityControl = getPriorityControl();
	}

	private boolean getPriorityControl() {
		return configuration.getBoolean(
			"org.F11.scada.xwife.applet.alarm.PriorityController",
			false);
	}

	public void tableChanged(TableModelEvent e) {
		// Shift Key を押下してスクリーンセイバーを解除
		pageChanger.pressShiftKey();
		TableModel model = (TableModel) e.getSource();
		synchronized (currentAlarm) {
			if (TableModelEvent.INSERT == e.getType()) {
				changePage(model);
				playSound(model);
				clearPriority(model);
			}
		}
	}

	private void changePage(TableModel model) {
		if (isChangePage(model)) {
			if (WifeUtilities.isTrue(model.getValueAt(0, 11))) {
				TableRowModel row = new TableRowModel(model);
				if (currentAlarm.comparePriority(row)) {
					currentAlarm = row;
				}
				String path = (String) model.getValueAt(0, 0);
				PageChangeEvent evt = new PageChangeEvent(this, path, true);
				pageChanger.changePage(evt);
			}
		}
	}

	private boolean isChangePage(TableModel model) {
		return isPriorityControl
			? WifeUtilities.isTrue(model.getValueAt(0, 1))
				&& currentAlarm.comparePriority(new TableRowModel(model))
			: WifeUtilities.isTrue(model.getValueAt(0, 1));
	}

	private void clearPriority(TableModel model) {
		TableRowModel row = new TableRowModel(model);
		if (isClear(row)) {
			currentAlarm = TableRowModel.INIT_ROW_MODEL;
		}
	}

	private boolean isClear(TableRowModel row) {
		if (logger.isDebugEnabled()) {
			logger.debug("currentAlarm=" + currentAlarm);
			logger.debug("            row=" + row);
		}
		return TableRowModel.INIT_ROW_MODEL != currentAlarm
			&& currentAlarm.equalsKey(row)
			&& !row.isOnoff();
	}

	private void playSound(TableModel model) {
		int sound_type = ((Integer) model.getValueAt(0, 7)).intValue();
		if (sound_type != 0) {
			String sound_path = (String) model.getValueAt(0, 8);
			pageChanger.playAlarm(sound_path);
			boolean soundTimer =
				configuration.getBoolean(
					"org.F11.scada.xwife.applet.alarm.soundTimer",
					false);
			long soundTimerTime =
				configuration.getLong(
					"org.F11.scada.xwife.applet.alarm.soundTimerTime",
					5000L);
			if (soundTimer) {
				timer.playAlarm(pageChanger, soundTimerTime);
			}
		}
	}

	public void checkedEvent(CheckEvent evt) {
		if (logger.isDebugEnabled()) {
			logger.debug(evt);
		}
		synchronized (currentAlarm) {
			if (currentAlarm.equalsKey(evt)) {
				currentAlarm = TableRowModel.INIT_ROW_MODEL;
			}
		}
	}

	/**
	 * テスト用メソッド
	 * 
	 * @return カレントのテーブルモデルを返します。
	 */
	TableRowModel getTableRowModel() {
		synchronized (currentAlarm) {
			return currentAlarm;
		}
	}

	/**
	 * 指定されたミリ秒で警報音停止するタイマークラスです。
	 * 
	 * @author maekawa
	 * 
	 */
	private static class AlarmTimer {
		private Timer timer;
		private StopTask currentTask;

		public AlarmTimer() {
			timer = new Timer(true);
		}

		void playAlarm(PageChanger pageChanger, long period) {
			if (null != currentTask) {
				currentTask.cancel();
			}
			currentTask = new StopTask(pageChanger);
			timer.schedule(currentTask, period);
		}

		/**
		 * 指定されたミリ秒で警報音停止するタイマータスクです。
		 * 
		 * @author maekawa
		 * 
		 */
		private static class StopTask extends TimerTask {
			private final PageChanger pageChanger;

			public StopTask(PageChanger pageChanger) {
				this.pageChanger = pageChanger;
			}

			@Override
			public void run() {
				pageChanger.stopAlarm();
			}
		}
	}
}
