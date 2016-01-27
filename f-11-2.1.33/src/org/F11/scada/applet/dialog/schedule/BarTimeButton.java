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

import org.F11.scada.xwife.applet.PageChanger;

/**
 * 時刻設定用のボタンクラスです
 */
class BarTimeButton extends AbstractTimeButton {
	private static final long serialVersionUID = -142952489385714531L;

	/**
	 * コンストラクタ
	 * 
	 * @param dialog スケジュール時刻設定ダイアログの参照
	 * @param time 時間
	 * @param hour 時間・分の種別
	 */
	BarTimeButton(
			AbstractScheduleDialog scheduleDialog,
			int time,
			boolean hour,
			PageChanger changer) {
		super(scheduleDialog, time, hour, changer);
	}

	/**
	 * 最大値を返します
	 */
	public double getConvertMax() {
		if (hour)
			return 24;
		else
			return 59;
	}
}
