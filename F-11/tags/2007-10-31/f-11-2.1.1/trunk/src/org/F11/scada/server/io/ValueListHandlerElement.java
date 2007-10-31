/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/ValueListHandlerElement.java,v 1.3 2003/10/15 08:36:51 frdm Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/15 08:36:51 $
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
package org.F11.scada.server.io;

import java.sql.Timestamp;
import java.util.Map;
import java.util.SortedMap;

import org.F11.scada.server.event.LoggingDataListener;

/**
 * ロギングデータのハンドラエレメントのインターフェイスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface ValueListHandlerElement extends LoggingDataListener {
	/**
	 * レコードオブジェクトを返します。
	 * @return レコードオブジェクト
	 */
	public Object next();

	/**
	 * 次のレコードオブジェクトが存在する時は、true を返します。
	 * @return 次のレコードオブジェクトが存在する時は、true をそうでない場合は false を返します。
	 */
	public boolean hasNext();

	/**
	 * 最初のタイムスタンプを返します。
	 */
	public Object firstKey();

	/**
	 * 最後のタイムスタンプを返します。
	 */
	public Object lastKey();

	/**
	 * タイムスタンプが引数 key 以前のレコードを検索し、ポインタを位置づけます。
	 * このメソッドで位置づけられたポインタは、 key 以前のレコードを１つ含みます。
	 * 但し、key が先頭レコード以前のレコードを示す場合は、先頭レコードからになります。
	 * @param key 検索するレコードのタイムスタンプ
	 */
	public void findRecord(Timestamp key);
	
	/**
	 * keyで指定された時刻以降のロギングデータをMapインスタンスで返します。
	 * @param key 更新時刻
	 */
	public Map getUpdateLoggingData(Timestamp key);
	
	/**
	 * 保持しているデータが更新された後に通知する先を追加します。
	 * @param listener
	 */
	public void addLoggingDataListener(LoggingDataListener listener);
	
	/**
	 * 通知先を削除します。
	 * @param listener
	 */
	public void removeLoggingDataListener(LoggingDataListener listener);

	/**
	 * 初期化用データのSortedMapを返します。
	 * @return 初期化用データのSortedMapを返します。
	 */
	public SortedMap getInitialData();
}
