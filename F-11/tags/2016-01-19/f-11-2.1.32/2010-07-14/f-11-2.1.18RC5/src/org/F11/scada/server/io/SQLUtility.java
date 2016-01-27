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
package org.F11.scada.server.io;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public interface SQLUtility {
	String getSelectAllString(String name, List dataHolder, int limit);

	String getSelectTimeString(String name, List dataHolder, Timestamp time);

	String getFirstData(String name, List dataHolder);

	String getLastData(String name, List dataHolder);

	/**
	 * 引数のタイムスタンプより前(引数を含まない)のデータを返します
	 * 
	 * @param name テーブル名
	 * @param data 抽出ホルダのリスト
	 * @param start このタイムスタンプより前のデータを返します
	 * @param limit 最大件数
	 * @return 引数のタイムスタンプより前(引数を含まない)のデータを返します
	 * @see #getSelectAfter(String, List, Timestamp, int)
	 */
	String getSelectBefore(
			String name,
			List dataHolder,
			Timestamp start,
			int limit);

	/**
	 * 引数のタイムスタンプ以上(引数を含む)のデータを返します
	 * 
	 * @param name テーブル名
	 * @param data 抽出ホルダのリスト
	 * @param start このタイムスタンプ以上のデータを返します
	 * @param limit 最大件数
	 * @return 引数のタイムスタンプ以上(引数を含む)のデータを返します
	 * @see #getSelectBefore(String, List, Timestamp, int)
	 */
	String getSelectAfter(
			String name,
			List dataHolder,
			Timestamp start,
			int limit);

	/**
	 * 引数のタイムスタンプより前(引数を含まない)のデータを返します
	 * 
	 * @param name テーブル名
	 * @param data 抽出ホルダのリスト
	 * @param start このタイムスタンプより前のデータを返します
	 * @param limit 最大件数
	 * @param tables 使用するテーブル名
	 * @return 引数のタイムスタンプより前(引数を含まない)のデータを返します
	 * @see #getSelectAfter(String, List, Timestamp, int)
	 */
	String getSelectBefore(
			String name,
			List dataHolder,
			Timestamp start,
			int limit,
			List<String> tables);

	/**
	 * 引数のタイムスタンプ以上(引数を含む)のデータを返します
	 * 
	 * @param name テーブル名
	 * @param data 抽出ホルダのリスト
	 * @param start このタイムスタンプ以上のデータを返します
	 * @param limit 最大件数
	 * @param tables 使用するテーブル名
	 * @return 引数のタイムスタンプ以上(引数を含む)のデータを返します
	 * @see #getSelectBefore(String, List, Timestamp, int)
	 */
	String getSelectAfter(
			String name,
			List dataHolder,
			Timestamp start,
			int limit,
			List<String> tables);

	String getSelectAllString(
			String name,
			List dataHolder,
			int limit,
			List<String> tables);

	String getSelectTimeString(
			String name,
			List dataHolder,
			Timestamp time,
			List<String> tables);

	String getFirstData(String name, List dataHolder, List<String> tables);

	String getLastData(String name, List dataHolder, List<String> tables);

	/**
	 * 引数のタイムスタンプ範囲(start以上,end未満)のデータを返します
	 * @param name テーブル名
	 * @param data 抽出ホルダのリスト
	 * @param start このタイムスタンプ以上
	 * @param end このタイムスタンプ未満
	 * @return 引数のタイムスタンプ範囲のデータを返します
	 * @see #getSelectBefore(String, List, Timestamp, Timestamp)
	 */
	String getSelectPeriod(String name, List dataHolder, Timestamp start,
			Timestamp end);
	String getSelectPeriod(String name, List dataHolder, Timestamp start,
			Timestamp end, List<String> tables);
}
