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

package org.F11.scada.tool.conf.io;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * タスクのBeanクラスです。
 * 
 * @author maekawa
 * 
 */
public class TimeSetTaskBean {
	/** タスクのプロパティ */
	private Map<String, String> attribute = new HashMap<String, String>();
	/** 処理のリスト */
	private List<TimeSetBean> timeList;

	/**
	 * プロパティを返します。
	 * 
	 * @param key プロパティ名
	 * @return プロパティを返します。
	 */
	public String get(String key) {
		return attribute.get(key);
	}

	/**
	 * プロパティを設定します。
	 * 
	 * @param key プロパティ名
	 * @param value プロパティ値
	 * @return 設定以前のプロパティ値
	 */
	public String put(String key, String value) {
		return attribute.put(key, value);
	}

	/**
	 * 指定プロパティがあれば true をなければ false を返します。
	 * 
	 * @param key プロパティ名
	 * @return 指定プロパティがあれば true をなければ false を返します。
	 */
	public boolean containsKey(String key) {
		return attribute.containsKey(key);
	}

	/**
	 * タスク処理のリストを返します。
	 * 
	 * @return 処理のリストを返します。
	 */
	public List<TimeSetBean> getTimeList() {
		return timeList;
	}

	/**
	 * タスク処理のリストを設定します。
	 * 
	 * @param timeList タスク処理のリスト
	 */
	public void setTimeList(List<TimeSetBean> timeList) {
		this.timeList = timeList;
	}

	@Override
	public String toString() {
		return "attribute=" + attribute.toString() + " ,timeList=" + timeList;
	}
}