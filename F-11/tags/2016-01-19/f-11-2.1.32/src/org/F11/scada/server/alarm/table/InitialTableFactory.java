/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/InitialTableFactory.java,v 1.1.6.1 2006/08/11 02:24:33 frdm Exp $
 * $Revision: 1.1.6.1 $
 * $Date: 2006/08/11 02:24:33 $
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

import org.F11.scada.server.alarm.AlarmException;

/**
 * 初期化された DefaultTableModel オブジェクトを生成する、ファクトリークラスです。
 * サブクラスでは使用するリソースから、必要なデータを取得してテーブルモデルを
 * 初期化して返します。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
abstract public class InitialTableFactory {
	/**
	 * 指定したファクトリーオブジェクトを生成します。
	 * @param cls 実装ファクトリークラス
	 * @return InitialTableFactory ファクトリークラス
	 * @throws InstantiationException クラスが見つからない場合
	 * @throws IllegalAccessException クラスのアクセスに失敗した場合
	 */
	public static InitialTableFactory createInitialTableFactory(Class cls)
			throws InstantiationException, IllegalAccessException {
		return (InitialTableFactory) cls.newInstance();
	}

	/**
	 * 初期化した履歴データのテーブルモデルを返します。
	 * @return DefaultTableModel 初期化した履歴データのテーブルモデル
	 * @throws AlarmException テーブル生成時に例外が発生した場合
	 */
	abstract public AlarmTableModel createCareer() throws AlarmException;

	/**
	 * 初期化したヒストリーデータのテーブルモデルを返します。
	 * @return DefaultTableModel 初期化したヒストリーデータのテーブルモデル
	 * @throws AlarmException テーブル生成時に例外が発生した場合
	 */
	abstract public AlarmTableModel createHistory() throws AlarmException;

	/**
	 * 初期化したサマリーデータのテーブルモデルを返します。
	 * @return DefaultTableModel 初期化したサマリーデータのテーブルモデル
	 * @throws AlarmException テーブル生成時に例外が発生した場合
	 */
	abstract public AlarmTableModel createSummary() throws AlarmException;

	/**
	 * 初期化した未復旧データのテーブルモデルを返します。
	 * @return DefaultTableModel 初期化した未復旧データデータのテーブルモデル
	 * @throws AlarmException テーブル生成時に例外が発生した場合
	 */
	abstract public AlarmTableModel createOccurrence() throws AlarmException;

	/**
	 * 初期化した未確認データのテーブルモデルを返します。
	 * @return DefaultTableModel 初期化した未確認データデータのテーブルモデル
	 * @throws AlarmException テーブル生成時に例外が発生した場合
	 */
	abstract public AlarmTableModel createNoncheck() throws AlarmException;
}
