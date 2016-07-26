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

package org.F11.scada.server.edit;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.autoprint.AutoPrintEditor;

/**
 * 自動印刷編集機能を持つオブジェクトに、変更イベントを発生させるクラスです
 * @author hori
 */
public class ServerEditManager extends UnicastRemoteObject
		implements ServerEditHandler {

	private static final long serialVersionUID = 944970692114214286L;
	/** 自動印刷エディターオブジェクト */
	private AutoPrintEditor editor;

	/**
	 * 自動印刷の変更イベントを管理するマネージャーを初期化します
	 * @param port RMI使用ポート
	 * @param editor 自動印刷エディターオブジェクト
	 * @throws RemoteException リモートエラー発生時
	 * @throws MalformedURLException リモートエラー発生時
	 */
	public ServerEditManager(int port, AutoPrintEditor editor)
			throws RemoteException, MalformedURLException {

		super(port);
		Naming.rebind(WifeUtilities.createRmiServerEditManager(), this);
		this.editor = editor;
	}

	/**
	 * 自動印字設定パラメータの変更をサーバーに通知します
	 * @exception RemoteException リモートエラー発生時
	 */
	public void editAutoPrint() throws RemoteException {
		editor.reloadAutoPrint();
	}

	/**
	 * 自動印刷のサーバー名称を返します。
	 * @return 自動印刷のサーバー名称
	 */
	public String getServerName() throws RemoteException {
		return editor.getServerName();
	}
}
