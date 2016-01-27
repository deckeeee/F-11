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

package org.F11.scada.server.frame;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Map;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.server.frame.editor.FrameEditHandler;

/**
 * FrameEditHandler オブジェクトを生成するファクトリークラスです。
 * Preperence.properties の「/server/FrameEditHandler」で設定します。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FrameEditHandlerFactory {
	private final int port;
	private final FrameDefineManager frameDefineManager;
	private final Map taskMap;

	/**
	 * FrameEditHandlerFactory オブジェクトファクトリークラスを初期化します
	 * @param port RMIオブジェクト転送ポート番号
	 * @param defineManager ページ定義マネージャー
	 * @param taskMap ロギングタスクのマップ
	 */
	public FrameEditHandlerFactory(
			int port,
			FrameDefineManager defineManager,
			Map taskMap) {
		this.port = port;
		this.frameDefineManager = defineManager;
		this.taskMap = taskMap;
	}

	/**
	 * 設定した FrameEditHandler オブジェクトを生成し返します。
	 * @return 設定した FrameEditHandler オブジェクトを生成し返します。
	 * @throws RemoteException RMIレジストリ登録でエラー
	 * @throws MalformedURLException RMIレジストリ登録でエラー
	 */
	public FrameEditHandler createFrameEditHandler()
			throws RemoteException, MalformedURLException {

		FrameEditHandler handler = null;

		String editmode = EnvironmentManager.get("/server/FrameEditHandler", "");
		if ("XmlFrameDefineManager".equals(editmode)) {
			handler = new XmlFrameEditManager(port, taskMap);
		} else {
			handler = new FrameEditManager(port, frameDefineManager, taskMap);
		}
		
		return handler;
	}
}
