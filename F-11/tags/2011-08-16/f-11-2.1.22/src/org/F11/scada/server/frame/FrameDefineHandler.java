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
 *
 */
package org.F11.scada.server.frame;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.F11.scada.parser.tree.TreeDefine;
import org.F11.scada.xwife.applet.Session;

/**
 * 画面定義のハンドラ・リモートインターフェイスです。
 * 定義をRMIを経由して読みとります。
 */
public interface FrameDefineHandler extends Remote {
	/**
	 * keyで指定された時刻以降にページ定義が変更されていれば、XMLで定義を返します。
	 * @param name ページ名
	 * @param key 更新時刻
	 * @param session 要求クライアントのセッション情報
	 * @return String ページ定義のXML表現。変更無し又はページ名無しの場合null
	 */
	public PageDefine getPage(String name, long key, Session session) throws RemoteException;

	/**
	 * keyで指定された時刻以降にステータスバー定義が変更されていれば、XMLで定義を返します。
	 * @param key 更新時刻
	 * @return String ステータスバー定義のXML表現。変更無しの場合null
	 */
	public PageDefine getStatusbar(long key) throws RemoteException;

	/**
	 * ユーザー毎のメニューツリーを返します。 指定ユーザーにメニュー定義が無ければ、デフォルトのメニューツリーを返します。
	 * @param user ユーザー名
	 * @return メニューツリーの定義
	 * @throws RemoteException
	 */
	public TreeDefine getMenuTreeRoot(String user) throws RemoteException;
	
	/**
	 * キャッシュ指定されているページ名のリストを返します。
	 * @return キャッシュ指定されているページ名のリストを返します。
	 */
	List getCachePages() throws RemoteException;
}
