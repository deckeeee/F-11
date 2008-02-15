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

package org.F11.scada.server.communicater;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * セレクタ及びポートを管理する為のクラスです。
 * ポートへのリスナー追加、削除は必ずこのオブジェクトを介して行います。
 * シングルトン
 */
public final class PortChannelManager {
	private final static Logger log =
		Logger.getLogger(PortChannelManager.class);

	/** このオブジェクトのインスタンス */
	private final static PortChannelManager instance = new PortChannelManager();
	/** ポートのマップ */
	protected final static Map portChannels = new HashMap();
	/** セレクタ */
	protected static PortSelector portSelector;

	/**
	 * プライベートコンストラクタ
	 */
	private PortChannelManager() {
	}

	/**
	 * このオブジェクトのインスタンスを返します。
	 */
	public static PortChannelManager getInstance() {
		return instance;
	}

	/**
	 * ポートを取得し、リスナーを登録します。
	 * ポートが登録されていない場合は新たにポートを開き、セレクタに登録します。
	 * セレクタが無い場合は作成します。
	 * @param portKind ポート種別
	 * @param local ホストのアドレス
	 * @param listener 登録するリスナー
	 * @return ポートを管理するオブジェクト
	 */
	public synchronized PortChannel addPortListener(
		String portKind,
		InetSocketAddress target,
		InetSocketAddress local,
		RecvListener listener)
		throws IOException, InterruptedException {
		log.debug("addPortListener");
		PortChannel port = getPortChannel(portKind, target, local);
		port.addListener(listener);
		return port;
	}

	/**
	 * リスナーを削除します。
	 * ポートのリスナーが全て削除された場合に、ポートを閉じてセレクタから消去します。
	 * 全てのポートが消去された場合にセレクタを削除します。
	 * @param local ホストのアドレス
	 * @param listener 削除するリスナー
	 */
	public synchronized void removePortListener(
		InetSocketAddress local,
		RecvListener listener)
		throws InterruptedException {
		String key = getKey(local);
		log.debug("removePortListener :" + key);
		PortChannel port = (PortChannel) portChannels.get(key);
		if (port.removeListener(listener)) {
			portChannels.remove(key);
			port.closeReq();
			if (!portSelector.isActive()) {
				portSelector = null;
			}
		}
	}

	/*
	 * ポートを取得します。
	 * ポートが登録されていない場合は新たにポートを開き、セレクタに登録します。
	 * セレクタが無い場合は作成します。
	 */
	private PortChannel getPortChannel(
		String portKind,
		InetSocketAddress target,
		InetSocketAddress local)
		throws IOException, InterruptedException {
		if (portSelector == null) {
			portSelector = new PortSelector();
			log.debug("create PortSelector");
		}

		String key = getKey(local);
		PortChannel port = (PortChannel) portChannels.get(key);
		if (port == null) {
			key = getKey(target);
			port = (PortChannel) portChannels.get(key);
		}
		if (port == null) {
			if ("UDP".equals(portKind)) {
				key = getKey(local);
				port = new UdpPortChannel(local, portSelector);
				log.debug("create UdpPortChannel");
			} else if ("TCP".equals(portKind)) {
				key = getKey(target);
				port = new TcpPortChannel(portSelector, target);
				log.debug("create TcpPortChannel");
			} else {
				throw new IllegalArgumentException(
					"Non suport port kind. [" + portKind + "]");
			}
			portChannels.put(key, port);
		}
		log.debug("getPortChannel :" + key);
		return port;
	}

	/*
	 * ポートを管理する為のキーを作成します。
	 */
	private String getKey(InetSocketAddress local) {
		return local.getAddress().getHostAddress() + ":" + local.getPort();
	}
}
