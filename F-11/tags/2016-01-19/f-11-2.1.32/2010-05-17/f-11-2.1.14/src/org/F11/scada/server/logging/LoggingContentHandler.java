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
package org.F11.scada.server.logging;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public interface LoggingContentHandler extends Remote {
	/**
	 * ロギングタスクのマップを返します。
	 * 
	 * @return ロギングタスクのマップを返します。
	 */
	Map getTaskMap() throws RemoteException;

	/**
	 * ロギングタスクのマップに put します。
	 * 
	 * @param name タスク名
	 * @param task タスク
	 */
	void putTaskMap(String name, LoggingTask task) throws RemoteException;

	/**
	 * 引数のロギングタスクで使用しているホルダーを返します。
	 * 
	 * @param taskName ロギングタスク名
	 * @return 引数のロギングタスクで使用しているホルダーを返します。
	 * @throws RemoteException
	 */
	List getHolderStrings(String taskName) throws RemoteException;

	/**
	 * ファクトリー名を返します。
	 * 
	 * @param taskName ロギングタスク名
	 * @return ファクトリー名を返します。
	 * @throws RemoteException
	 */
	String getFactoryName(String taskName) throws RemoteException;

	/**
	 * 結合するテーブル名のリスト。
	 * 
	 * @param taskName メインのテーブル名
	 * @return 結合するテーブル名のリスト
	 */
	List<String> getTables(String taskName) throws RemoteException;
}
