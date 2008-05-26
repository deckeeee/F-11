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

package org.F11.scada.tool.conf;

import java.util.List;

import org.F11.scada.tool.conf.io.TimeSetBean;
import org.F11.scada.tool.conf.io.TimeSetTaskBean;

/**
 * TimeSet.xmlのストリームインターフェイスです
 * 
 * @author maekawa
 *
 */
public interface TimeSetManager {
	/**
	 * 対象タスクのプロパティを返します
	 * 
	 * @param name 対象タスク
	 * @param key プロパティ名
	 * @param def プロパティ初期値
	 * @return 対象タスクのプロパティを返します
	 */
	String getTimeSet(String name, String key, String def);

	/**
	 * 対象タスクの処理リストを返します
	 * @param name 対象タスク
	 * @return 対象タスクの処理リストを返します
	 */
	List<TimeSetBean> getTimeSetBeansList(String name);

	/**
	 * 対象タスクのプロパティを設定します
	 * @param name 対象タスク
	 * @param key プロパティ名
	 * @param value プロパティ値 
	 */
	void setTimeSet(String name, String key, String value);

	/**
	 * 対象タスクに処理リストを設定します
	 * 
	 * @param name 対象タスク
	 * @param list 処理リスト
	 */
	void setTimeSetBeansList(String name, List<TimeSetBean> list);

	/**
	 * タスクを設定します
	 * 
	 * @param bean タスク
	 */
	void setTimeSetTask(TimeSetTaskBean bean);

	/**
	 * 対象タスクを削除します
	 * @param bean 対象タスク
	 * @return 削除したタスク
	 */
	TimeSetTaskBean removeTimeSetTask(TimeSetTaskBean bean);
	
	/**
	 * タスクのリストを返します
	 * 
	 * @return タスクのリストを返します
	 */
	List<TimeSetTaskBean> getTimeSetTask();
}