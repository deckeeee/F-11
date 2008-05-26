/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/xwife/applet/ValueListHandlerProxy.java,v 1.5.2.1 2005/08/11 07:46:33 frdm Exp $
 * $Revision: 1.5.2.1 $
 * $Date: 2005/08/11 07:46:33 $
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
package org.F11.scada.xwife.applet;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.server.io.ValueListHandler;
import org.apache.log4j.Logger;

/**
 * AppletからValueListHandlerManagerにアクセスする代理クラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ValueListHandlerProxy
		extends UnicastRemoteObject
		implements ValueListHandler {
	private static final long serialVersionUID = -1130301718797965557L;
	/** ValueListHandlerManagerの参照です */
	private ValueListHandler handler;
	/** サーバーエラー例外 */
	private Exception serverError;
	
	private static final int MAX_RETRY = 30;
	
	private static Logger logger;

	/**
	 * 代理ハンドラーオブジェクトを生成します。
	 * 代理ハンドラーオブジェクトとは、アプレットを配信するホスト上で動作し、
	 * アプレットに成り代わって、データ収集サーバーをアクセスするオブジェクトです。
	 */
	public ValueListHandlerProxy(int recvPort) throws MalformedURLException, RemoteException {
		super(recvPort);
		logger = Logger.getLogger(getClass().getName());

		logger.info("ValueListHandlerProxy:" + WifeUtilities.createRmiValueListHandler());
		logger.info("ValueListHandlerManager:" + WifeUtilities.createRmiValueListHandlerManager());

		Naming.rebind(WifeUtilities.createRmiValueListHandler(), this);
		
		for (int i = 1; i <= MAX_RETRY; i++) {
			try {
				lookup();
				logger.info("ValueListHandlerProxy bound in registry");
				break;
			} catch (Exception e) {
				try {
					Thread.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				} catch (InterruptedException e2) {}
				logger.info("ValueListHandlerProxy RMI connect error. Retry RMI connect (" + i + ")");
				continue;
			}
		}
		
		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			throw ServerErrorUtil.createException(serverError);
		}
	}

	private void lookup() throws MalformedURLException, RemoteException, NotBoundException {
		handler = (ValueListHandler) Naming.lookup(WifeUtilities.createRmiValueListHandlerManager());
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#next(java.lang.String)
	 */
	public Object next(String name) throws RemoteException {
		if (serverError != null) {
			return null;
		}

		Object obj = null;
		try {
			obj = handler.next(name);
			serverError = null;
		} catch (RemoteException e) {
			try {
				lookup();
				obj = handler.next(name);
				serverError = null;
			} catch (Exception e1) {
				ServerErrorUtil.invokeServerError();
				throw ServerErrorUtil.createException(e1);
			}
		}
		return obj;
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#hasNext(java.lang.String)
	 */
	public boolean hasNext(String name) throws RemoteException {
		if (serverError != null) {
			return false;
		}

		boolean b = false;
		try {
			b = handler.hasNext(name);
			serverError = null;
		} catch (RemoteException e) {
			try {
				lookup();
				b = handler.hasNext(name);
				serverError = null;
			} catch (Exception e1) {
				ServerErrorUtil.invokeServerError();
				throw ServerErrorUtil.createException(e1);
			}
		}
		return b;
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#firstKey(java.lang.String)
	 */
	public Object firstKey(String name) throws RemoteException {
		if (serverError != null) {
			return null;
		}

		Object obj = null;
		try {
			obj = handler.firstKey(name);
			serverError = null;
		} catch (RemoteException e) {
			try {
				lookup();
				obj = handler.firstKey(name);
				serverError = null;
			} catch (Exception e1) {
				ServerErrorUtil.invokeServerError();
				throw ServerErrorUtil.createException(e1);
			}
		}
		return obj;
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#lastKey(java.lang.String)
	 */
	public Object lastKey(String name) throws RemoteException {
		if (serverError != null) {
			return null;
		}

		Object obj = null;
		try {
			obj = handler.lastKey(name);
			serverError = null;
		} catch (RemoteException e) {
			try {
				lookup();
				obj = handler.lastKey(name);
				serverError = null;
			} catch (Exception e1) {
				ServerErrorUtil.invokeServerError();
				throw ServerErrorUtil.createException(e1);
			}
		}
		return obj;
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#findRecord(java.lang.String, java.sql.Timestamp)
	 */
	public void findRecord(String name, Timestamp key) throws RemoteException {
		if (serverError != null) {
			return;
		}

		try {
			handler.findRecord(name, key);
		} catch (RemoteException e) {
			try {
				lookup();
				handler.findRecord(name, key);
			} catch (Exception e1) {
				ServerErrorUtil.invokeServerError();
				throw ServerErrorUtil.createException(e1);
			}
		}
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#getUpdateLoggingData(java.lang.String, java.sql.Timestamp)
	 */
	public Map getUpdateLoggingData(String name, Timestamp key)
			throws RemoteException {
		if (serverError != null) {
			return Collections.EMPTY_MAP;
		}

		Map map = null;
		try {
			map = handler.getUpdateLoggingData(name, key);
			serverError = null;
		} catch (RemoteException e) {
			try {
				lookup();
				map = handler.getUpdateLoggingData(name, key);
				serverError = null;
			} catch (Exception e1) {
				ServerErrorUtil.invokeServerError();
				throw ServerErrorUtil.createException(e1);
			}
		}
		return map;
	}
	
	/**
	 * 初期化用データのSortedMapを返します。
	 * @param name ハンドラ名
	 * @return 初期化用データのSortedMapを返します。
	 */
	public SortedMap getInitialData(String name) throws RemoteException {
		if (serverError != null) {
			return new TreeMap();
		}

		SortedMap map = null;
		try {
			map = handler.getInitialData(name);
			serverError = null;
		} catch (RemoteException e) {
			try {
				lookup();
				map = handler.getInitialData(name);
				serverError = null;
			} catch (Exception e1) {
				ServerErrorUtil.invokeServerError();
				throw ServerErrorUtil.createException(e1);
			}
		}
		return map;
	}

}
