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

package org.F11.scada.server.io.nio;

import java.sql.Timestamp;
import java.util.List;

import org.F11.scada.data.LoggingRowData;

/**
 * LoggingDataのリストを返します。
 * @author maekawa
 */
public interface LogTableSelectService {
	/**
	 * LoggingDataのリストを返します。
	 * @param tableName テーブル名
	 * @param dataHolders 抽出するデータホルダーのリスト
	 * @param limit レコード件数の最大値
	 * @return LoggingDataのリストを返します。
	 */
	List select(String tableName, List dataHolders, int limit);

	/**
	 * 引数で指定した日時より新しいレコードのLoggingDataのリストを返します。
	 * @param tableName テーブル名
	 * @param dataHolders 抽出するデータホルダーのリスト
	 * @param time 抽出するデータホルダーの検索条件日時
	 * @return 引数で指定した日時より新しいレコードのLoggingDataのリストを返します。
	 */
	List select(String tableName, List dataHolders, Timestamp time);

	/**
	 * テーブル内の最古レコードを返します。
	 * @param tableName テーブル名
	 * @param dataHolders 抽出するデータホルダーのリスト
	 * @return テーブル内の最古レコードを返します。
	 */
	LoggingRowData selectFirst(String tableName, List dataHolders);

	/**
	 * テーブル内の最新レコードを返します。
	 * @param tableName テーブル名
	 * @param dataHolders 抽出するデータホルダーのリスト
	 * @return テーブル内の最新レコードを返します。
	 */
	LoggingRowData selectLast(String tableName, List dataHolders);
	/**
	 * 引数の日時の前後のレコードを抽出して返します
	 * @param name テーブル名
	 * @param dataHolders 抽出するデータホルダーのリスト
	 * @param start 日時
	 * @param limit 最大件数
	 * @return 引数の日時の前後のレコードを抽出して返します
	 */
	List selectBeforeAfter(String name, List dataHolders, Timestamp start, int limit);

	/**
	 * 引数で指定した日時でCSV文字列のリストを返します。
	 * @param tableName テーブル名
	 * @param dataHolders 抽出するデータホルダーのリスト
	 * @param startTime 抽出するデータホルダーの検索条件日時
	 * @param endTime 抽出するデータホルダーの検索条件日時
	 * @return 引数で指定した日時より新しいレコードのLoggingDataのリストを返します。
	 */
	List select(String tableName, List dataHolders, Timestamp startTime, Timestamp endTime);
}