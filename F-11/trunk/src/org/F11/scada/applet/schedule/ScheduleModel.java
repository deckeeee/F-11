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

import java.beans.PropertyChangeListener;

import org.F11.scada.applet.symbol.Editable;
import org.F11.scada.data.WifeDataSchedule;

/**
 * スケジュールデータモデルインターフェイスです。
 */
public interface ScheduleModel extends Editable {
	/**
	 * リモートオブジェクトに値を設定します。
	 */
	public void setValue();

	/**
	 * データが編集された場合に true を返します。
	 */
	public boolean isEditing();

	/**
	 * 保持しているスケジュールデータを更新します。
	 */
	public void writeData();

	/**
	 * 保持しているスケジュールデータをアンドゥします。
	 */
	public void undoData();

	/**
	 * スケジュールデータのグループを指定したインデックスに変更します。
	 * 
	 * @param index グループインデックス
	 */
	public void setGroupNo(int index);

	/**
	 * グループNo を返します
	 */
	public int getGroupNo();

	/**
	 * グループNo の最大数を返します
	 */
	public int getGroupNoMax();

	/**
	 * スケジュールパターンのインデックスを返します。
	 * 
	 * @param index
	 */
	public int getDayIndex(int index);

	/**
	 * スケジュールパターンのインデックス名を返します。
	 * 
	 * @param index
	 */
	public String getDayIndexName(int index);

	/**
	 * 特殊日のインデックスを返します。
	 * 
	 * @param index
	 */
	public int getSpecialDayOfIndex(int index);

	/**
	 * 項目パターンのサイズを返します。
	 * 
	 * @return 項目パターンのサイズ
	 */
	public int getPatternSize();

	/**
	 * スケジュール On/Off の最大回数を返します。
	 * 
	 * @return スケジュール On/Off の最大回数
	 */
	public int getNumberSize();

	/**
	 * スケジュール行モデルを返します。
	 * 
	 * @param index 返す行
	 * @return スケジュール行モデルを返します。
	 */
	public ScheduleRowModel getScheduleRowModel(int index);

	/**
	 * データモデルが変更されるたびに通知されるリストにリスナーを追加します。
	 * 
	 * @param listener PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * データモデルが変更されるたびに通知されるリストからリスナーを削除します。
	 * 
	 * @param listener PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * グループを追加します。
	 * 
	 * @param dataProvider
	 * @param dataHolder
	 * @param flagHolder
	 */
	public void addGroup(String dataProvider, String dataHolder, String flagHolder);

	/**
	 * グループ名称を返します
	 * 
	 * @return グループ名称を返します
	 */
	public String getGroupName();

	/**
	 * データリファレンサーを切断します。
	 */
	public void disConnect();

	/**
	 * カレントのスケジュールデータを対象のグループに複製します。
	 * 
	 * @param dest 複数先のスケジュールグループ
	 */
	void duplicateGroup(int[] dest);

	/**
	 * カレントのスケジュールデータの曜日分データを対象の曜日に複製します。
	 * 
	 * @param src 複製元曜日番号
	 * @param dest 複製先曜日番号
	 */
	void duplicateWeekOfDay(int src, int[] dest);

	/**
	 * グループエレメントの配列を返します。
	 * 
	 * @return グループエレメントの配列
	 */
	GroupElement[] getGroupNames();

	/**
	 * スケジュール画面で上方にくる日の個数(今日明日なら2, 未来7日なら7など)を返します。
	 * 
	 * @return スケジュール画面で上方にくる日の個数(今日明日なら2, 未来7日なら7など)を返します。
	 */
	int getTopSize();

	/**
	 * モデルの中のスケジュール値を返します。
	 * 
	 * @return モデルの中のスケジュール値を返します。
	 */
	WifeDataSchedule getDataSchedule();

	/**
	 * モデルにスケジュール値を設定します。
	 * 
	 * @param schedule モデルにスケジュール値を設定します。
	 */
	void setDataSchedule(WifeDataSchedule schedule);

	/**
	 * データ変更イベントを発火します。
	 * @param oldValue 変更前のオブジェクト
	 * @param newValue 変更後のオブジェクト
	 */
	void firePropertyChange(Object oldValue, Object newValue);
}
