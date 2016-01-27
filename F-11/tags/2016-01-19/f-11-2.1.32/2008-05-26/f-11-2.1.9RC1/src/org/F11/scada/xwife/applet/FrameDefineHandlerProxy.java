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
package org.F11.scada.xwife.applet;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.parser.tree.TreeDefine;
import org.F11.scada.server.frame.FrameDefineHandler;
import org.F11.scada.server.frame.PageDefine;
import org.F11.scada.util.ThreadUtil;
import org.apache.log4j.Logger;

/**
 * AppletからFrameDefineManagerにアクセスする代理クラスです。
 * 
 * @author hori
 */
public class FrameDefineHandlerProxy extends UnicastRemoteObject implements
		FrameDefineHandler {
	private static final long serialVersionUID = -1850337761534697894L;
	/** FrameDefineManagerの参照です */
	private FrameDefineHandler handler;
	/** サーバーエラー例外 */
	private Exception serverError;

	private static final int MAX_RETRY = 30;
	private static final long MAX_RETRY_TIME = 30000L;

	private static Logger logger;

	/**
	 * @throws java.rmi.RemoteException
	 */
	public FrameDefineHandlerProxy(int recvPort) throws RemoteException,
			MalformedURLException {
		super(recvPort);
		logger = Logger.getLogger(getClass().getName());

		Naming.rebind(WifeUtilities.createRmiFrameDefineHandler(), this);

		for (int i = 1; i <= MAX_RETRY; i++) {
			try {
				lookup();
				serverError = null;
				logger.debug("FrameDefineHandlerProxy bound in registry");
				break;
			} catch (Exception e) {
				serverError = e;
				try {
					Thread.sleep(MAX_RETRY_TIME);
				} catch (InterruptedException e2) {
				}
				logger
						.info("FrameDefineHandlerProxy RMI connect error. Retry RMI connect ("
								+ i + ")");
				continue;
			}
		}

		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			throw ServerErrorUtil.createException(serverError);
		}
	}

	private void lookup()
			throws MalformedURLException,
			RemoteException,
			NotBoundException {
		handler = (FrameDefineHandler) Naming.lookup(WifeUtilities
				.createRmiFrameDefineManager());
	}

	/*
	 * (Javadoc なし)
	 * 
	 * @see org.F11.scada.server.frame.FrameDefineHandler#getPage(java.lang.String,
	 *      long, java.net.InetAddress)
	 */
	public PageDefine getPage(String name, long key, Session session)
			throws RemoteException {
		if (serverError != null) {
			return null;
		}

		PageDefine define = null;

		for (int i = 1; i <= Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				define = handler.getPage(name, key, session);
				serverError = null;
				break;
			} catch (RemoteException e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				logger.info("getPage retry rmi lookup. (" + i + "/"
						+ Globals.RMI_METHOD_RETRY_COUNT + ")");
				e.printStackTrace();
				serverError = e;
				continue;
			}
		}

		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			serverError.printStackTrace();
		}

		return define;
	}

	/**
	 * keyで指定された時刻以降にステータスバー定義が変更されていれば、XMLで定義を返します。
	 * 
	 * @param key 更新時刻
	 * @return String ステータスバー定義のXML表現。変更無しの場合null
	 */
	public PageDefine getStatusbar(long key) throws RemoteException {
		return handler.getStatusbar(key);
	}

	/**
	 * ユーザー毎のメニューツリーを返します。 指定ユーザーにメニュー定義が無ければ、デフォルトのメニューツリーを返します。
	 * 
	 * @param user ユーザー名
	 * @return メニューツリーの定義
	 * @throws RemoteException
	 */
	public TreeDefine getMenuTreeRoot(String user) throws RemoteException {
		return handler.getMenuTreeRoot(user);
	}

	public List getCachePages() {
		List pages = Collections.EMPTY_LIST;
		for (int i = 1; i <= Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				pages = handler.getCachePages();
			} catch (RemoteException e) {
				try {
					lookup();
				} catch (Exception e1) {
					logger.info("getCachePages retry rmi lookup. (" + i + "/"
							+ Globals.RMI_METHOD_RETRY_COUNT + ")", e1);
					ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
					continue;
				}
				logger.info("getCachePages retry rmi lookup. (" + i + "/"
						+ Globals.RMI_METHOD_RETRY_COUNT + ")", e);
				ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				continue;
			}
		}
		return pages;
	}
}
