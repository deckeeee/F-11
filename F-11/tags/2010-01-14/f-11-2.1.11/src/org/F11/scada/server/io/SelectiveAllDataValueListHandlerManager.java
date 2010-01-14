/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/Attic/SelectiveAllDataValueListHandlerManager.java,v 1.1.2.3 2006/02/09 01:09:17 frdm Exp $
 * $Revision: 1.1.2.3 $
 * $Date: 2006/02/09 01:09:17 $
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
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

/**
 * ロギングハンドラーの管理クラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class SelectiveAllDataValueListHandlerManager
		extends UnicastRemoteObject
		implements SelectiveAllDataValueListHandler {
			
	private static final long serialVersionUID = -478396495273190658L;
	/** ハンドラ名とハンドラオブジェクトのマップです */
	private Map handlerMap;
	/** ロギングAPI */
	private static Logger logger;

	/**
	 * Constructor for SelectiveValueListHandlerManager.
	 * @throws RemoteException
	 */
	public SelectiveAllDataValueListHandlerManager(int recvPort) throws RemoteException, MalformedURLException {
		super(recvPort);
		logger = Logger.getLogger(getClass().getName());

		logger.info("SelectiveAllDataValueListHandlerManager:" + WifeUtilities.createRmiSelectiveAllDataValueListHandlerManager());
		Naming.rebind(WifeUtilities.createRmiSelectiveAllDataValueListHandlerManager(), this);
		logger.info("SelectiveAllDataValueListHandlerManager bound in registry");
	}
	
	/**
	 * ハンドラエレメントをマネージャーに追加します。
	 * @param name ハンドラ名
	 * @param handler ハンドラオブジェクト
	 */
	public synchronized void addValueListHandlerElement(String name, SelectiveAllDataValueListHandlerElement handler) {
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
	
	private SelectiveAllDataValueListHandlerElement getValueListHandlerElement(String name) {
		if (handlerMap == null) {
			throw new IllegalStateException("A SelectiveAllDataValueListHandlerManager doesn't hold a SelectiveAllDataValueListHandlerElement.");
		}

		if (handlerMap.containsKey(name)) {
		    return (SelectiveAllDataValueListHandlerElement) handlerMap.get(name);
		} else {
		    throw new IllegalStateException("A SelectiveAllDataValueListHandlerManager doesn't hold a SelectiveAllDataValueListHandlerElement." + name);
		}
	}
	
    public SortedMap<Timestamp, DoubleList> getInitialData(String name, List holderStrings) {
		return getValueListHandlerElement(name).getInitialData(holderStrings);
    }
	
    public SortedMap<Timestamp, DoubleList> getInitialData(String name, List holderStrings, int limit) {
		return getValueListHandlerElement(name).getInitialData(holderStrings, limit);
    }

    public Map<Timestamp, DoubleList> getUpdateLoggingData(String name, Timestamp key,
            List holderStrings) {
		return getValueListHandlerElement(name).getUpdateLoggingData(key, holderStrings);
    }
    
    public Timestamp firstTime(String name, List holderStrings) throws RemoteException {
        return getValueListHandlerElement(name).firstTime(holderStrings);
    }
    
    public Timestamp lastTime(String name, List holderStrings) throws RemoteException {
        return getValueListHandlerElement(name).lastTime(holderStrings);
    }
    
    public SortedMap getLoggingData(String name, List holderStrings, Timestamp start, int limit) {
        return getValueListHandlerElement(name).getLoggingData(holderStrings, start, limit);
    }
}
