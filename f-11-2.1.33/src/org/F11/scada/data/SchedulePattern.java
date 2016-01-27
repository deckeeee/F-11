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

package org.F11.scada.data;


/**
 * スケジュールパターンのインターフェイスです。
 */
public interface SchedulePattern {
	/**
	 * このスケジュールパターンの総数を返します。
	 */
	public int size();
	/**
	 * 引数の項目にあたるインデックスを返します。
	 * @param 項目の種類
	 */
	public int getDayIndex(int n);
	/**
	 * 引数の項目にあたるインデックス名を返します。
	 * @param 項目の種類
	 */
	public String getDayIndexName(int n);
	/**
	 * 引数で指定された特殊日のインデックスを返します。
	 * @param 特殊日の種類番号
	 */
	public int getSpecialDayOfIndex(int n);

	/**
	 * スケジュール画面で上方にくる日の個数(今日明日なら2, 未来7日なら7など)を返します。
	 * @return スケジュール画面で上方にくる日の個数(今日明日なら2, 未来7日なら7など)を返します。
	 */
	int getTopSize();
}
