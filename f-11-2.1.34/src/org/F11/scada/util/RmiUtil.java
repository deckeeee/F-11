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

package org.F11.scada.util;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Globals;
import org.F11.scada.exception.RemoteRuntimeException;
import org.F11.scada.xwife.server.WifeMain;
import org.apache.log4j.Logger;

/**
 * RMIを使用する際のユーティリティクラスです。
 * 
 * @author maekawa
 * 
 */
abstract public class RmiUtil {
	private static Logger log = Logger.getLogger(RmiUtil.class);

	/**
	 * サーバーインスタンスを登録します。RMI登録名は引数で指定したRMIインターフェイス名です。
	 * 
	 * @param remote 登録するインスタンス
	 * @param klass RMIインターフェイス
	 */
	public static void registryServer(Remote remote, Class klass) {
		registryServer(remote, klass, getServerRegPort(), getServerObjPort());
	}

	public static void registryServer(
			Remote remote,
			Class klass,
			int regPort,
			int objPort) {
		String regName = klass.getName();
		try {
			String host = getServerHost();
			Remote obj = UnicastRemoteObject.exportObject(remote, objPort);
			Registry registry = LocateRegistry.getRegistry(host, regPort);
			registry.rebind(regName, obj);
		} catch (RemoteException e) {
			throw new RemoteRuntimeException(e);
		}
		log.info(regName + " bound in registry");
	}

	private static String getServerHost() {
		return EnvironmentManager.get(
				"/server/rmi/managerdelegator/name",
				"localhost");
	}

	private static int getServerObjPort() {
		return Integer.parseInt(EnvironmentManager.get(
				"/server/rmi/managerdelegator/rmiReceivePort",
				String.valueOf(WifeMain.RMI_RECV_PORT_SERVER)));
	}

	private static int getServerRegPort() {
		return Integer.parseInt(EnvironmentManager.get(
				"/server/rmi/managerdelegator/port",
				"1099"));
	}

	/**
	 * 引数のRMIインターフェイスを実装する、サーバーインスタンスを取得します
	 * 
	 * @param klass 取得するサーバーインスタンスが実装しているRMIインターフェイス
	 * @return 引数のインターフェイスを実装するサーバーインスタンス
	 */
	public static Remote lookupServer(Class klass) {
		String name = klass.getName();
		int regPort = getServerRegPort();
		String host = getServerHost();

		Exception exception = null;
		for (int i = 1; i <= Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				Registry hostRegistry = LocateRegistry.getRegistry(
						host,
						regPort);
				return hostRegistry.lookup(name);
			} catch (RemoteException e) {
				log.info(name + " retry rmi lookup. (" + i + "/"
						+ Globals.RMI_CONNECTION_RETRY_COUNT + ")");
				ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				exception = e;
				continue;
			} catch (NotBoundException e) {
				log.info(name + " retry rmi lookup. (" + i + "/"
						+ Globals.RMI_CONNECTION_RETRY_COUNT + ")");
				ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				exception = e;
				continue;
			}
		}
		throw new RemoteRuntimeException(exception);
	}
}
