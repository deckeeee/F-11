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

/**
 * ロギングテーブルを操作するDaoインターフェイスです。
 * @author maekawa
 */
public interface LogTableDao {
	/**
	 * 指定したテーブルへLogDtoのリストの内容をバッチ挿入します。
	 * @param tableName テーブル名
	 * @param dtos LogDtoのリスト
	 * @return 挿入したレコード件数
	 */
	int insert(String tableName, List dtos);
	/**
	 * リビジョンを取得します。
	 * @param tableName テーブル名
	 * @param time イベント発生日時
	 * @param holderId ホルダID
	 * @return リビジョンを取得します。
	 */
	int getRevision(String tableName, Timestamp time, String holderId);
	/**
	 * 指定したログテーブルの全レコードを返します。
	 * @param tableName テーブル名
	 * @return 指定したログテーブルの全レコードを返します。
	 */
	List select(String tableName);
	/**
	 * 指定したログテーブルから、引数のデータホルダと最大レコード数でレコードを抽出します。
	 * @param tableName テーブル名
	 * @param holderId データホルダID
	 * @param limit 最大レコード数
	 * @return 指定したログテーブルから、引数のデータホルダと最大レコード数でレコードを抽出します。
	 */
	List select(String tableName, String holderId, int limit);
	/**
	 * 引数で指定した日時より新しいレコードのリストを返します。
	 * @param tableName テーブル名
	 * @param dataHolders 抽出するデータホルダーID
	 * @param time 抽出するデータホルダーの検索条件日時
	 * @return 引数で指定した日時より新しいレコードのリストを返します。
	 */
	List select(String tableName, String holderId, Timestamp time);
	/**
	 * テーブル内の最古レコードを返します。
	 * @param tableName テーブル名
	 * @param holderId データホルダーID
	 * @return テーブル内の最古レコードを返します。
	 */
	List selectFirst(String tableName, String holderId);
	/**
	 * テーブル内の最新レコードを返します。
	 * @param tableName テーブル名
	 * @param holderId データホルダーID
	 * @return テーブル内の最新レコードを返します。
	 */
	List selectLast(String tableName, String holderId);

	/**
	 * 引数日時より古いレコードを抽出します
	 * @param tableName テーブル名
	 * @param holderId ホルダID
	 * @param start 日時
	 * @param limit レコード件数
	 * @return 引数日時より古いレコードを抽出します
	 */
	List selectBefore(String tableName, String holderId, Timestamp start, int limit);

	/**
	 * 引数日時以上新しいレコードを抽出します
	 * @param tableName テーブル名
	 * @param holderId ホルダID
	 * @param start 日時
	 * @param limit レコード件数
	 * @return 引数日時以上新しいレコードを抽出します
	 */
	List selectAfter(String tableName, String holderId, Timestamp start, int limit);

	/**
	 * 引数で指定した日時でレコードのLoggingDataのリストを返します。
	 * @param tableName テーブル名
	 * @param holderId 抽出するデータホルダー
	 * @param startTime 抽出するデータホルダーの検索条件日時
	 * @param endTime 抽出するデータホルダーの検索条件日時
	 * @return 引数で指定した日時より新しいレコードのLoggingDataのリストを返します。
	 */
	List select(String tableName, String holderId, Timestamp startTime, Timestamp endTime);
}
