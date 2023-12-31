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
import java.util.Map;
import java.util.SortedMap;

import org.F11.scada.server.register.HolderString;
import org.apache.commons.collections.primitives.DoubleList;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public interface SelectiveValueListHandlerElement {

	/**
	 * keyで指定された時刻以降のロギングデータをMapインスタンスで返します。
	 * 
	 * @param key 更新時刻
	 * @param holderStrings 抽出するデータホルダーのリスト
	 * @return keyで指定された時刻以降のロギングデータをMapインスタンスで返します。
	 */
	public Map<Timestamp, DoubleList> getUpdateLoggingData(
			Timestamp key,
			List<HolderString> holderStrings);

	/**
	 * 初期化用データのSortedMapを返します。
	 * 
	 * @param holderStrings 抽出するデータホルダーのリスト
	 * @return 初期化用データのSortedMapを返します。
	 */
	public SortedMap<Timestamp, DoubleList> getInitialData(
			List<HolderString> holderStrings);

	/**
	 * 初期化用データのSortedMapを返します。
	 * 
	 * @param holderStrings 抽出するデータホルダーのリスト
	 * @return 初期化用データのSortedMapを返します。
	 */
	public SortedMap<Timestamp, DoubleList> getInitialData(
			List<HolderString> holderStrings,
			int limit);
}
