/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/AlarmTableModel.java,v 1.5.2.2 2006/08/16 08:53:06 frdm Exp $
 * $Revision: 1.5.2.2 $
 * $Date: 2006/08/16 08:53:06 $
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
package org.F11.scada.server.alarm.table;

import java.util.SortedMap;

import javax.swing.table.TableModel;

import org.F11.scada.server.alarm.AlarmTableJournal;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.xwife.applet.alarm.event.CheckEvent;
import org.F11.scada.xwife.applet.alarm.event.CheckTable;

/**
 * 警報・状態テーブルモデルのインターフェイスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface AlarmTableModel extends TableModel, CheckTable {
	/**
	 * 引数で指定したタイムスタンプより大きいジャーナルデータのリストを返します。
	 * @param t タイムスタンプのLong値
	 * @return SortedMap ジャーナルデータのマップ
	 */
	public SortedMap<Long, AlarmTableJournal> getAlarmJournal(long t);

	/**
	 * 指定した行・カラムにデータを設定します。その後ジャーナルデータを追加します。
	 * @param data 設定するデータの配列
	 * @param rowIndex データを設定する行
	 * @param columnIndex データを設定するカラム
	 * @param key データ変更イベント値
	 */
	public void setValueAt(
			Object[] data,
			int rowIndex,
			int columnIndex,
			DataValueChangeEventKey key);

	/**
	 * 指定した行にデータを挿入します。その後ジャーナルデータを追加します。
	 * @param row データを挿入する行
	 * @param data 挿入するデータの配列
	 * @param key データ変更イベント値
	 */
	public void insertRow(int row, Object[] data, DataValueChangeEventKey key);

	/**
	 * 指定した行のデータを削除します。その後ジャーナルデータを追加します。
	 * @param row 削除する行
	 * @param key データ変更イベント値
	 */
	public void removeRow(int row, DataValueChangeEventKey key);

	/**
	 * ジャーナルデータのリストをテーブルモデルに反映し、ジャーナルデータに追加します。
	 * @param value ジャーナルデータのソートマップ
	 */
	public void setValue(SortedMap<Long, AlarmTableJournal> value);

	/**
	 * 最後のジャーナルデータを返します。
	 * @return 最後のジャーナルデータ
	 */
	public AlarmTableJournal getLastJournal();

	/**
	 * キーを含む最初の行を返します。
	 * @param key データ変更イベント値キーオブジェクト
	 * @return int キーの行が存在した場合は、その行を返します。存在しない場合は負数(-1)を返します。
	 */
	public int searchRow(DataValueChangeEventKey key);

	/**
	 * 指定した行にデータを挿入します。
	 * @param row データを挿入する行
	 * @param data 挿入するデータの配列
	 */
	public void insertRow(int row, Object[] data);

	/**
	 * 警報変更履歴を追加します
	 * @param aj 警報変更履歴
	 */
	void setJournal(AlarmTableJournal aj);

	/**
	 * 引数で指定したタイムスタンプより大きい確認イベントジャーナルデータのリストを返します。
	 * @param t タイムスタンプのLong値
	 * @return SortedMap 確認イベントジャーナルデータのマップ
	 */
	SortedMap<Long, CheckEvent> getCheckJournal(long t);

	/**
	 * 最後の警報確認ジャーナルデータを返します。
	 * @return 最後の警報確認ジャーナルデータ
	 */
	CheckEvent getLastCheckEvent();

	/**
	 * 行数と列名からテーブルの値を取り出します。
	 * @param row 行
	 * @param columnName カラム名
	 * @return 値オブジェクト
	 */
	Object getValueAt(int row, String columnName);

	/**
	 * カラム名から列数を返します。
	 * @param columnName カラム名
	 * @return 列数
	 */
	int getColumn(String columnName);


	/**
	 * 指定した行のデータを削除します。
	 * @param row 削除する行
	 */
	void removeRow(int row);
}
