/*
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

package org.F11.scada.applet.dialog.schedule;

import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.F11.scada.applet.symbol.TenkeyEditable;

/**
 * OK ボタンクラスです
 */
abstract class AbstractOkButton extends AbstractScheduleButton {
	private final boolean isSort;
	protected final boolean isLenient;

	AbstractOkButton(AbstractScheduleDialog scheduleDialog, boolean isSort, boolean isLenient) {
		super(scheduleDialog);
		this.isSort = isSort;
		this.isLenient = isLenient;
		init();
	}

	private void init() {
		addActionListener(this.scheduleDialog);
		setText("OK");
		setInoutKeyMap("ENTER");
	}

	/**
	 * ボタンが押下された時の処理を記述します。
	 */
	public void pushButton() {
		int columnCount = this.scheduleDialog.model.getColumnCount();

		//一旦配列に開始時刻と終了時刻を格納し、大小関係をチェック後スケジュールモデルに反映
		int[][] times = new int[columnCount][2];
		
		
		Iterator it = this.scheduleDialog.buttonList.iterator();
		for (int i = 0; i < columnCount * 2; i++) {
			int value = 0;
			for (int j = 0; j < 2; j++) {
				TenkeyEditable ed = (TenkeyEditable)it.next();
				if (j == 0) {
					value += Integer.parseInt(ed.getValue()) * 100;
				} else {
					value += Integer.parseInt(ed.getValue());
				}
			}
			if (i % 2 == 0) {
				times[i / 2][0] = value;
			} else {
				times[i / 2][1] = value;
			}
		}
		if (isNotRegalTime(times)) {
			JOptionPane.showMessageDialog(this,
					"不正な時刻が設定されています。\n確認して修正してください。", "時刻設定エラー",
					JOptionPane.ERROR_MESSAGE);
		} else {
			setTimes(times);
		}
	}

	private void setTimes(int[][] times) {
		if (isSort) {
			sort(times);
		}
		for (int i = 0; i < times.length; i++) {
			scheduleDialog.model.setOnTime(i, times[i][0]);
			scheduleDialog.model.setOffTime(i, times[i][1]);
		}
		scheduleDialog.model.firePropertyChange(null, null);
		scheduleDialog.dispose();
	}

	private void sort(int[][] times) {
		int[] onTimes = new int[times.length];
		int[] offTimes = new int[times.length];
		for (int i = 0; i < times.length; i++) {
			onTimes[i] = zeroToMax(times[i][0]);
			offTimes[i] = zeroToMax(times[i][1]);
		}
		Arrays.sort(onTimes);
		Arrays.sort(offTimes);
		for (int i = 0; i < times.length; i++) {
			times[i][0] = maxToZero(onTimes[i]);
			times[i][1] = maxToZero(offTimes[i]);
		}
	}

	private int maxToZero(int i) {
		return i == Integer.MAX_VALUE ? 0 : i;
	}

	private int zeroToMax(int i) {
		return i == 0 ? Integer.MAX_VALUE : i;
	}

	abstract protected boolean isNotRegalTime(int[][] times);
}
