package org.F11.scada.applet.schedule;

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
 *
 */

/**
 * WifeScheduleData の任意の一列を操作するデータモデルです。
 */
public interface ScheduleRowModel {
	/**
	 * スケジュール回数の数（列数）を返します
	 */
	public int getColumnCount();

	/**
	 * On 時間を返します
	 * @param column 列番号
	 */
	public int getOnTime(int column);

	/**
	 * Off 時間を返します
	 * @param column 列番号
	 */
	public int getOffTime(int column);

	/**
	 * On 時間を設定します
	 * @param column 列番号
	 * @param time 時間
	 */
	public void setOnTime(int column, int time);

	/**
	 * Off 時間を設定します
	 * @param column 列番号
	 * @param time 時間
	 */
	public void setOffTime(int column, int time);

	/**
	 * このモデルの項目名（行名）を返します。
	 */
	public String getDayIndexName();

	/**
	 * リスナーにバウンズプロパティ変更イベントを通知します。
	 * @param oldValue 変更前の値
	 * @param newValue 変更後の値
	 */
	public void firePropertyChange(Object oldValue, Object newValue);
}
