/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/GraphModel.java,v 1.6.6.1 2005/03/11 06:50:42 frdm Exp $
 * $Revision: 1.6.6.1 $
 * $Date: 2005/03/11 06:50:42 $
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

package org.F11.scada.applet.graph;

import java.beans.PropertyChangeListener;
import java.sql.Timestamp;

import org.F11.scada.Service;

/**
 * グラフデータモデルのインターフェイスです。
 */
public interface GraphModel extends Service {
	/**
	 * リスナーを追加します。
	 * @param l PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener l);

	/**
	 * リスナーを削除します。
	 * @param l PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener l);

	/**
	 * 指定のタスクから次のレコードオブジェクトを返します。
	 * @param name タスク名
	 */	
	public Object get(String name);

	/**
	 * 指定のタスクに次のレコードオブジェクトがある場合に true を返します。
	 * @param name タスク名
	 */
	public boolean next(String name);

	/**
	 * 指定のタスクから最初レコードのタイムスタンプを返します。
	 * @param name タスク名
	 * @return 最初レコードのタイムスタンプ
	 */
	public Object firstKey(String name);

	/**
	 * 指定のタスクから最終レコードのタイムスタンプを返します。
	 * @param name タスク名
	 * @return 最終レコードのタイムスタンプ
	 */
	public Object lastKey(String name);

	/**
	 * 指定のタスクからタイムスタンプが引数 key 以前のレコードを検索し、ポインタを位置づけます。
	 * このメソッドで位置づけられたポインタは、 key 以前のレコードを１つ含みます。
	 * 但し、key が先頭レコード以前のレコードを示す場合は、先頭レコードからになります。
	 * @param name タスク名
	 * @param key 検索するレコードのタイムスタンプ
	 */
	public void findRecord(String name, Timestamp key);
}
