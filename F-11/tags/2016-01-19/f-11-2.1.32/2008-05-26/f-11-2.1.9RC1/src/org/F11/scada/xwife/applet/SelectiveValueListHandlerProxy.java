/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/xwife/applet/Attic/SelectiveValueListHandlerProxy.java,v 1.1.2.4 2007/06/28 04:44:07 frdm Exp $
 * $Revision: 1.1.2.4 $
 * $Date: 2007/06/28 04:44:07 $
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
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.server.io.SelectiveValueListHandler;
import org.F11.scada.util.ThreadUtil;
import org.apache.log4j.Logger;

/**
 * AppletからValueListHandlerManagerにアクセスする代理クラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class SelectiveValueListHandlerProxy
		extends UnicastRemoteObject
		implements SelectiveValueListHandler {
	private static final long serialVersionUID = -6996777317741409508L;
	/** ValueListHandlerManagerの参照です */
	private SelectiveValueListHandler handler;
	/** サーバーエラー例外 */
	private Exception serverError;
	
	private static final int MAX_RETRY = 30;
	
	private static Logger logger;

	/**
	 * 代理ハンドラーオブジェクトを生成します。
	 * 代理ハンドラーオブジェクトとは、アプレットを配信するホスト上で動作し、
	 * アプレットに成り代わって、データ収集サーバーをアクセスするオブジェクトです。
	 */
	public SelectiveValueListHandlerProxy(int recvPort) throws MalformedURLException, RemoteException {
		super(recvPort);
		logger = Logger.getLogger(getClass().getName());

		logger.info("SelectiveValueListHandlerProxy:" + WifeUtilities.createRmiSelectiveValueListHandler());
		logger.info("SelectiveValueListHandlerManager:" + WifeUtilities.createRmiSelectiveValueListHandlerManager());

		Naming.rebind(WifeUtilities.createRmiSelectiveValueListHandler(), this);
		
		for (int i = 1; i <= MAX_RETRY; i++) {
			try {
				lookup();
				logger.info("SelectiveValueListHandlerProxy bound in registry");
				break;
			} catch (Exception e) {
				try {
					Thread.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				} catch (InterruptedException e2) {}
				logger.info("SelectiveValueListHandlerProxy RMI connect error. Retry RMI connect (" + i + ")");
				continue;
			}
		}
		
		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			throw ServerErrorUtil.createException(serverError);
		}
	}

	private void lookup() throws MalformedURLException, RemoteException, NotBoundException {
		handler = (SelectiveValueListHandler) Naming.lookup(WifeUtilities.createRmiSelectiveValueListHandlerManager());
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandler#getUpdateLoggingData(java.lang.String, java.sql.Timestamp)
	 */
	public Map getUpdateLoggingData(String name, Timestamp key, List holderStrings)
			throws RemoteException {

	    logger.debug(holderStrings);

	    if (serverError != null) {
			return Collections.EMPTY_MAP;
		}

	    Exception exception = null;
		for (int i = 0; i < Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				return handler.getUpdateLoggingData(name, key, holderStrings);
			} catch (RemoteException e) {
				ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				logger.info("SelectiveValueListHandlerProxy RMI connect error. Retry RMI connect (" + i + ")", e);
				try {
					lookup();
				} catch (Exception ex) {
					exception = ex;
				}
				continue;
			}
		}
		throw new RemoteException("getUpdateLoggingDatadeでエラーが発生", exception);
/*
		Map map = null;
		try {
			map = handler.getUpdateLoggingData(name, key, holderStrings);
			serverError = null;
		} catch (RemoteException e) {
			try {
				lookup();
				map = handler.getUpdateLoggingData(name, key, holderStrings);
				serverError = null;
			} catch (Exception e1) {
				ServerErrorUtil.invokeServerError();
				throw ServerErrorUtil.createException(e1);
			}
		}
		return map;
*/
	}
	
	/**
	 * 初期化用データのSortedMapを返します。
	 * @param name ハンドラ名
	 * @return 初期化用データのSortedMapを返します。
	 */
	public SortedMap getInitialData(String name, List holderStrings) throws RemoteException {

	    logger.debug(holderStrings);

	    if (serverError != null) {
			return new TreeMap();
		}

		SortedMap map = null;
		try {
			map = handler.getInitialData(name, holderStrings);
			serverError = null;
		} catch (RemoteException e) {
			try {
				lookup();
				map = handler.getInitialData(name, holderStrings);
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
	public SortedMap getInitialData(String name, List holderStrings, int limit) throws RemoteException {

	    logger.debug(holderStrings);

	    if (serverError != null) {
			return new TreeMap();
		}

		SortedMap map = null;
		try {
			map = handler.getInitialData(name, holderStrings, limit);
			serverError = null;
		} catch (RemoteException e) {
			try {
				lookup();
				map = handler.getInitialData(name, holderStrings);
				serverError = null;
			} catch (Exception e1) {
				ServerErrorUtil.invokeServerError();
				throw ServerErrorUtil.createException(e1);
			}
		}
		return map;
	}

}
