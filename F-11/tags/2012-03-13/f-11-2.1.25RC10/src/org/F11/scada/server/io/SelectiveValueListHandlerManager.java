/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/Attic/SelectiveValueListHandlerManager.java,v 1.1.2.5 2006/05/18 06:52:57 frdm Exp $
 * $Revision: 1.1.2.5 $
 * $Date: 2006/05/18 06:52:57 $
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

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.register.HolderString;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

/**
 * ロギングハンドラーの管理クラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class SelectiveValueListHandlerManager extends UnicastRemoteObject
		implements SelectiveValueListHandler {

	private static final long serialVersionUID = -3474934483417202536L;
	/** ハンドラ名とハンドラオブジェクトのマップです */
	private Map handlerMap;
	/** ロギングAPI */
	private static Logger logger;

	/**
	 * Constructor for SelectiveValueListHandlerManager.
	 * 
	 * @throws RemoteException
	 */
	public SelectiveValueListHandlerManager(int recvPort)
			throws RemoteException, MalformedURLException {
		super(recvPort);
		logger = Logger.getLogger(getClass().getName());

		logger.info("SelectiveValueListHandlerManager:"
			+ WifeUtilities.createRmiSelectiveValueListHandlerManager());
		Naming.rebind(
			WifeUtilities.createRmiSelectiveValueListHandlerManager(),
			this);
		logger.info("SelectiveValueListHandlerManager bound in registry");
	}

	/**
	 * ハンドラエレメントをマネージャーに追加します。
	 * 
	 * @param name ハンドラ名
	 * @param handler ハンドラオブジェクト
	 */
	public synchronized void addValueListHandlerElement(
			String name,
			SelectiveValueListHandlerElement handler) {
		if (null == handler) {
			throw new IllegalArgumentException("handler is null");
		}
		if (handlerMap == null) {
			handlerMap = new HashMap();
		}
		handlerMap.put(name, handler);
	}

	/**
	 * ハンドラエレメントをマネージャーから削除します。
	 * 
	 * @param name ハンドラ名
	 */
	public synchronized void removeValueListHandlerElement(String name) {
		if (handlerMap == null) {
			return;
		}
		handlerMap.remove(name);
	}

	private SelectiveValueListHandlerElement getValueListHandlerElement(
			String name) {
		if (handlerMap == null) {
			throw new IllegalStateException(
				"A SelectiveValueListHandlerManager doesn't hold a SelectiveValueListHandlerElement.");
		}

		if (handlerMap.containsKey(name)) {
			return (SelectiveValueListHandlerElement) handlerMap.get(name);
		} else {
			throw new IllegalStateException(
				"A SelectiveValueListHandlerManager doesn't hold a SelectiveValueListHandlerElement."
					+ name);
		}
	}

	public SortedMap<Timestamp, DoubleList> getInitialData(
			String name,
			List<HolderString> holderStrings) {
		return getValueListHandlerElement(name).getInitialData(holderStrings);
	}

	public SortedMap<Timestamp, DoubleList> getInitialData(
			String name,
			List<HolderString> holderStrings,
			int limit) {
		return getValueListHandlerElement(name).getInitialData(
			holderStrings,
			limit);
	}

	public Map<Timestamp, DoubleList> getUpdateLoggingData(
			String name,
			Timestamp key,
			List<HolderString> holderStrings) {
		return getValueListHandlerElement(name).getUpdateLoggingData(
			key,
			holderStrings);
	}
}
