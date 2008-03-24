/*
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

package org.F11.scada.tool.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.F11.scada.server.ServerConstruction;
import org.apache.commons.logging.Log;

abstract public class ServerConstructionUtil {

	/**
	 * カレントマシンがメインであれば true をそうでなければ false を返します。
	 * 
	 * @param construction サーバー・サブシステム構成
	 * @param log ログAPI
	 * @return カレントマシンがメインであれば true をそうでなければ false を返します。
	 */
	public static boolean isMainSystem(ServerConstruction construction, Log log) {
		try {
			return construction.isMainSystem(InetAddress.getLocalHost()
					.getHostAddress());
		} catch (UnknownHostException e) {
			// ローカルホストのgetHostAddress()なので、例外が発生することは無い。
			log.error("ドメインあるいはIPアドレスの形式が不正です", e);
			return false;
		}
	}
}
