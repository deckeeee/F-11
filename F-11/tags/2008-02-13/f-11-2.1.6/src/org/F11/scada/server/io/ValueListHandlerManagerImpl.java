/*
 * $Header$
 * $Revision$
 * $Date$
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
import java.util.Map;
import java.util.SortedMap;

import org.F11.scada.WifeUtilities;
import org.apache.log4j.Logger;

/**
 * ロギングハンドラーの管理クラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ValueListHandlerManagerImpl
		extends UnicastRemoteObject
		implements ValueListHandlerManager {
			
	private static final long serialVersionUID = -2516839121099400245L;
	/** ハンドラ名とハンドラオブジェクトのマップです */
	private Map handlerMap;
	/** ロギングAPI */
	private static Logger logger;

	/**
	 * Constructor for ValueListHandlerManager.
	 * @throws RemoteException
	 */
	public ValueListHandlerManagerImpl(int recvPort) throws RemoteException, MalformedURLException {
		super(recvPort);
		logger = Logger.getLogger(getClass().getName());

		logger.info("ValueListHandlerManager:" + WifeUtilities.createRmiValueListHandlerManager());
		Naming.rebind(WifeUtilities.createRmiValueListHandlerManager(), this);
		logger.info("ValueListHandlerManager bound in registry");
	}
	
	/**
	 * ハンドラエレメントをマネージャーに追加します。
	 * @param name ハンドラ名
	 * @param handler ハンドラオブジェクト
	 */
	public synchronized void addValueListHandlerElement(String name, ValueListHandlerElement handler) {
		if (handlerMap == null) {
			handlerMap = new HashMap();
		}
		handlerMap.put(name, handler);
	}

	/**
	 * ハンドラエレメントをマネージャーから削除します。
	 * @param name ハンドラ名
	 */	
	public synchronized void removeValueListHandlerElement(String name) {
		if (handlerMap == null) {
			return;
		}
		handlerMap.remove(name);
	}
	
	private ValueListHandlerElement getValueListHandlerElement(String name) {
		if (handlerMap == null) {
			throw new IllegalStateException("A ValueListHandlerManager doesn't hold a ValueListHandlerElement.");
		}

		return (ValueListHandlerElement) handlerMap.get(name);
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#next(java.lang.String)
	 */
	public Object next(String name) {
		return getValueListHandlerElement(name).next();
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#hasNext(java.lang.String)
	 */
	public boolean hasNext(String name) {
		return getValueListHandlerElement(name).hasNext();
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#firstKey(java.lang.String)
	 */
	public Object firstKey(String name) {
		return getValueListHandlerElement(name).firstKey();
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#lastKey(java.lang.String)
	 */
	public Object lastKey(String name) {
		return getValueListHandlerElement(name).lastKey();
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#findRecord(java.lang.String, java.sql.Timestamp)
	 */
	public void findRecord(String name, Timestamp key) {
		getValueListHandlerElement(name).findRecord(key);
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#getUpdateLoggingData(java.lang.String, java.sql.Timestamp)
	 */
	public Map getUpdateLoggingData(String name, Timestamp key) {
		return getValueListHandlerElement(name).getUpdateLoggingData(key);
	}

	/**
	 * 初期化用データのSortedMapを返します。
	 * @return 初期化用データのSortedMapを返します。
	 */
	public SortedMap getInitialData(String name) {
		return getValueListHandlerElement(name).getInitialData();
	}
}
