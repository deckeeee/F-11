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

package org.F11.scada.server.alarm.print;

import java.sql.SQLException;
import java.util.List;

import org.F11.scada.server.alarm.DataValueChangeEventKey;


/**
 * 警報メッセージ印刷データを操作するDAOインターフェイス
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface AlarmPrintDAO {
	/**
	 * 未印刷のデータを全て返します。
	 * @return PrintLineDataオブジェクトのリスト
	 * @exception SQLException データベースエラー発生時
	 */
	public List findAll() throws SQLException;

	/**
	 * リストの内容をデータベースに挿入します
	 * @param key データ変更イベント
	 * @exception SQLException データベースエラー発生時
	 */
	public void insert(DataValueChangeEventKey key) throws SQLException;

	/**
	 * 引数のイベントをキーにして印刷データオブジェクトを返します
	 * @param key データ変更イベント
	 * @return 印刷データオブジェクト
	 * @exception SQLException データベースエラー発生時
	 */
	public PrintLineData find(DataValueChangeEventKey key) throws SQLException;

	/**
	 * 警報メッセージ印刷データを全て削除します
	 * @exception SQLException データベースエラー発生時
	 */
	public void deleteAll() throws SQLException;

	/**
	 * データ変更イベントが警報メッセージ印刷の対象かどうかを判定します。
	 * @param key データ変更イベント
	 * @return 警報メッセージ印刷対象なら true を 対象でなければ false を返します
	 * @exception SQLException データベースエラー発生時
	 */
	public boolean isAlarmPrint(DataValueChangeEventKey key) throws SQLException;
}
