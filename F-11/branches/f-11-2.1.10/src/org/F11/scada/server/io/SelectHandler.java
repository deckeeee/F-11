package org.F11.scada.server.io;

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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.F11.scada.data.LoggingRowData;

/**
 * 検索条件のクエリー及び全データのレコードセットを返すインターフェイスです。
 */
public interface SelectHandler {
	/** テーブルが存在しない場合の例外文字列です */
	public static final String TABLE_NOT_FOUND = "Table not found";

	/**
	 * 指定された列の LoggingRowDataのリストを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト(列の情報)
	 * @return 指定された列の LoggingRowDataのリストを返します。
	 * @exception SQLException SQLエラーが発生した場合
	 */
	public List select(String name, List dataHolders) throws SQLException;

	/**
	 * 指定された列の LoggingRowDataのリストを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト(列の情報)
	 * @return 指定された列の LoggingRowDataのリストを返します。
	 * @exception SQLException SQLエラーが発生した場合
	 */
	public List select(String name, List dataHolders, int limit)
			throws SQLException;

	/**
	 * 指定された列の LoggingRowDataのtime以降のリストを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト
	 * @param time 返すデータの検索条件日時
	 * @return 指定された列の LoggingRowDataのリストを返します。
	 * @throws SQLException
	 */
	public List select(String name, List dataHolders, Timestamp time)
			throws SQLException;

	/**
	 * テーブルの最も古いレコードを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト(列の情報)
	 * @return テーブルの最も古いレコードを返します。
	 * @throws SQLException
	 */
	public LoggingRowData first(String name, List dataHolders)
			throws SQLException;

	/**
	 * テーブルの最も新しいレコードを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト(列の情報)
	 * @return テーブルの最も新しいレコードを返します。
	 * @throws SQLException
	 */
	public LoggingRowData last(String name, List dataHolders)
			throws SQLException;

	/**
	 * 指定された列の LoggingRowDataの指定した日時間のリストを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト
	 * @param start 返すデータの検索条件中間の日時
	 * @param limit 最大レコード件数
	 * @return 指定された列の LoggingRowDataの指定した日時間のリストを返します。
	 * @throws SQLException
	 */
	public List selectBeforeAfter(
			String name,
			List dataHolders,
			Timestamp start,
			int limit) throws SQLException;

	/**
	 * 指定された列の LoggingRowDataのリストを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト(列の情報)
	 * @param limit 取得データレコード数
	 * @param table 使用するテーブル名
	 * @return 指定された列の LoggingRowDataのリストを返します。
	 * @exception SQLException SQLエラーが発生した場合
	 */
	public List<LoggingRowData> select(
			String name,
			List dataHolders,
			int limit,
			List<String> table) throws SQLException;

	/**
	 * 指定された列の LoggingRowDataのtime以降のリストを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト
	 * @param time 返すデータの検索条件日時
	 * @param tables 使用するテーブル名
	 * @return 指定された列の LoggingRowDataのリストを返します。
	 * @throws SQLException
	 */
	public List select(
			String name,
			List dataHolders,
			Timestamp time,
			List<String> tables) throws SQLException;

	/**
	 * 指定された列の LoggingRowDataの指定した日時間のリストを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト
	 * @param start 返すデータの検索条件中間の日時
	 * @param limit 最大レコード件数
	 * @param tables 使用するテーブル名
	 * @return 指定された列の LoggingRowDataの指定した日時間のリストを返します。
	 * @throws SQLException
	 */
	public List selectBeforeAfter(
			String name,
			List dataHolders,
			Timestamp start,
			int limit,
			List<String> tables) throws SQLException;

	/**
	 * テーブルの最も古いレコードを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト(列の情報)
	 * @return テーブルの最も古いレコードを返します。
	 * @throws SQLException
	 */
	public LoggingRowData first(
			String name,
			List dataHolders,
			List<String> tables) throws SQLException;

	/**
	 * テーブルの最も新しいレコードを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト(列の情報)
	 * @return テーブルの最も新しいレコードを返します。
	 * @throws SQLException
	 */
	public LoggingRowData last(
			String name,
			List dataHolders,
			List<String> tables) throws SQLException;

}
