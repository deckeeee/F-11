/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.io.postgresql.padding;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.F11.scada.server.register.HolderString;

/**
 * 欠損ロギングレコードを補完するロジックです。
 * 
 * @author maekawa
 *
 */
public interface PaddingLogic {
	/**
	 * 補完レコードを挿入します。
	 * 
	 * @param con コネクション
	 * @param table テーブル名
	 * @param holderList ロギング対象ホルダのリスト
	 * @param timestamp カレントレコードのタイムスタンプ
	 * @throws SQLException
	 */
	void insertPadding(
			Connection con,
			String table,
			List<HolderString> holderList,
			Timestamp timestamp) throws SQLException;
	

	/**
	 * 引数の一単位後のタイムスタンプを返します。
	 * 
	 * @param timestamp 基準となるタイムスタンプ
	 * @return 引数の一単位後のタイムスタンプを返します。
	 */
	Timestamp afterTime(Timestamp timestamp);

	/**
	 * 引数の一単位前のタイムスタンプを返します。
	 * 
	 * @param timestamp 基準となるタイムスタンプ
	 * @return 引数の一単位前のタイムスタンプを返します。
	 */
	Timestamp beforeTime(Timestamp timestamp);
}
