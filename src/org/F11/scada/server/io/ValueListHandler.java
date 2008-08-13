/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/ValueListHandler.java,v 1.7 2003/10/15 08:36:51 frdm Exp $
 * $Revision: 1.7 $
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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.SortedMap;

/**
 * ロギングデータのハンドラ・リモートインターフェイスです。
 * 定義されたロギングデータをRMIを経由して、データストレージより読みとります。
 */
public interface ValueListHandler extends Remote {
	/**
	 * レコードオブジェクトを返します。
	 * @param name ハンドラ名
	 * @return レコードオブジェクト
	 */
	public Object next(String name) throws RemoteException;

	/**
	 * 次のレコードオブジェクトが存在する時は、true を返します。
	 * @param name ハンドラ名
	 * @return 次のレコードオブジェクトが存在する時は、true をそうでない場合は false を返します。
	 */
	public boolean hasNext(String name) throws RemoteException;

	/**
	 * 最初のタイムスタンプを返します。
	 * @param name ハンドラ名
	 */
	public Object firstKey(String name) throws RemoteException;

	/**
	 * 最後のタイムスタンプを返します。
	 * @param name ハンドラ名
	 */
	public Object lastKey(String name) throws RemoteException;

	/**
	 * タイムスタンプが引数 key 以前のレコードを検索し、ポインタを位置づけます。
	 * このメソッドで位置づけられたポインタは、 key 以前のレコードを１つ含みます。
	 * 但し、key が先頭レコード以前のレコードを示す場合は、先頭レコードからになります。
	 * @param name ハンドラ名
	 * @param key 検索するレコードのタイムスタンプ
	 */
	public void findRecord(String name, Timestamp key) throws RemoteException;
	
	/**
	 * keyで指定された時刻以降のロギングデータをMapインスタンスで返します。
	 * @param name ハンドラ名
	 * @param key 更新時刻
	 */
	public Map getUpdateLoggingData(String name, Timestamp key) throws RemoteException;
	
	/**
	 * 初期化用データのSortedMapを返します。
	 * @param name ハンドラ名
	 * @return 初期化用データのSortedMapを返します。
	 */
	public SortedMap getInitialData(String name) throws RemoteException;

	/**
	 * 引数のハンドラを返します。
	 * 
	 * @param name ハンドラ名
	 * @return 引数のハンドラを返します。
	 */
	ValueListHandlerElement getValueListHandlerElement(String name) throws RemoteException;

}
