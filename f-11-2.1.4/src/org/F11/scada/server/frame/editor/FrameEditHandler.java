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

package org.F11.scada.server.frame.editor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author hori
 */
public interface FrameEditHandler extends Remote {

	/**
	 * nameで指定されたページ定義をXMLで返します。
	 * @param name ページ名
	 * @return String ページ定義のXML表現。ページ名無しの場合null
	 */
	public String getPageXml(String name) throws RemoteException; 

	/**
	 * nameで指定したページ定義を設定します。
	 * @param name ページ名
	 * @param xml ページ定義
	 */
	public void setPageXml(String name, String xml) throws RemoteException;

	/**
	 * loggingNameで指定したロギングファイルに保存される項目の属性リストを返します。
	 * @param loggingName ロギングファイル名
	 * @return 項目の属性リスト
	 */
	public List getLoggingHolders(String loggingName) throws RemoteException;
}
