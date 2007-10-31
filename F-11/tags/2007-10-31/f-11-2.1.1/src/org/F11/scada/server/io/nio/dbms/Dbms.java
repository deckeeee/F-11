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

package org.F11.scada.server.io.nio.dbms;

/**
 * 各DBMS用のSQLを定義するインターフェイスです。
 * @author maekawa
 */
public interface Dbms {
	/**
	 * ロギングテーブル生成SQLを返します
	 * @param tableName テーブル名
	 * @return テーブル生成SQLを返します
	 */
	String getCreateSql(String tableName);
	/**
	 * ロギングテーブル削除SQLを返します
	 * @param tableName テーブル名
	 * @return テーブル削除SQLを返します
	 */
	String getDropSql(String tableName);
	/**
	 * ロギングテーブル索引生成SQLを返します
	 * @param tableName テーブル名
	 * @return ロギングテーブル索引生成SQLを返します
	 */
	String[] getCreateIndexSql(String tableName);
	/**
	 * レコード挿入SQLを返します
	 * @param tableName テーブル名
	 * @return レコード挿入SQLを返します
	 */
	String getInsertSql(String tableName);
	/**
	 * リビジョン取得SQLを返します。
	 * @param tableName テーブル名
	 * @return リビジョン取得SQLを返します。
	 */
	String getRevisionSql(String tableName);
	/**
	 * 最大デフォルト件数でレコードを検索するSQLを返します
	 * @param tableName テーブル名
	 * @return 最大デフォルト件数でレコードを検索するSQLを返します
	 */
	String getSelectSql(String tableName);
	/**
	 * 指定件数でレコードを検索するSQLを返します
	 * @param tableName テーブル名
	 * @param limit 最大レコード数
	 * @return 指定件数でレコードを検索するSQLを返します
	 */
	String getSelectHolderIdSql(String tableName, int limit);
	/**
	 * 日時でレコードを検索するSQLを返します
	 * @param tableName テーブル名
	 * @return 日時でレコードを検索するSQLを返します
	 */
	String getSelectTimeSql(String tableName);

	/**
	 * 最古レコードを検索するSQLを返します
	 * @param tableName テーブル名
	 * @return 最古レコードを検索するSQLを返します
	 */
	String getSelectFirstSql(String tableName);

	/**
	 * 最新レコードを検索するSQLを返します
	 * @param tableName テーブル名
	 * @return 最新レコードを検索するSQLを返します
	 */
	String getSelectLastSql(String tableName);

	/**
	 * 引数日時以上新しいレコードを抽出するSQLを返します
	 * @param tableName テーブル名
	 * @param limit レコード件数
	 * @return 引数日時以上新しいレコードを抽出するSQLを返します
	 */
	String getSelectAfterSql(String tableName, int limit);

	/**
	 * 引数日時より古いレコードを抽出するSQLを返します
	 * @param tableName テーブル名
	 * @param limit レコード件数
	 * @return 引数日時より古いレコードを抽出するSQLを返します
	 */
	String getSelectBeforeSql(String tableName, int limit);

	/**
	 * 日付の間を抽出するSQLを返します
	 * @param tableName テーブル名
	 * @return 日付の間を抽出するSQLを返します
	 */
	String getSelectBetweenTimeSql(String tableName);
}
