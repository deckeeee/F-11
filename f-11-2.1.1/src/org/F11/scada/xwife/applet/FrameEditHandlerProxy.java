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

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.server.frame.editor.FrameEditHandler;
import org.apache.log4j.Logger;

/**
 * Applet����FrameDefineManager�ɃA�N�Z�X����㗝�N���X�ł��B
 * @author hori
 */
public class FrameEditHandlerProxy extends UnicastRemoteObject implements FrameEditHandler {
	private static final long serialVersionUID = -1127091288737627298L;
	/** FrameDefineManager�̎Q�Ƃł� */
	private FrameEditHandler handler;
	/** �T�[�o�[�G���[��O */
	private Exception serverError;

	private static final int MAX_RETRY = 30;

	private static Logger logger;

	/**
	 * @throws java.rmi.RemoteException
	 */
	public FrameEditHandlerProxy(int recvPort) throws RemoteException, MalformedURLException {
		super(recvPort);
		logger = Logger.getLogger(getClass().getName());

		Naming.rebind(WifeUtilities.createRmiFrameEditHandler(), this);

		int maxRetry = Integer.parseInt(EnvironmentManager.get(
				"/server/rmi/collectorserver/retry/count",
				"-1"));

		if (maxRetry < 0) {
			for (int i = 1;; i++) {
				try {
					lookup();
					serverError = null;
					logger.debug("FrameEditHandlerProxy bound in registry");
					break;
				} catch (Exception e) {
					serverError = e;
					try {
						Thread.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
					} catch (InterruptedException e1) {}
					logger.info(
						"FrameEditHandlerProxy RMI connect error. Retry RMI connect (" + i + ")");
					continue;
				}
			}
		} else {
			for (int i = 1; i <= MAX_RETRY; i++) {
				try {
					lookup();
					serverError = null;
					logger.debug("FrameEditHandlerProxy bound in registry");
					break;
				} catch (Exception e) {
					serverError = e;
					try {
						Thread.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
					} catch (InterruptedException e1) {}
					logger.info(
						"FrameEditHandlerProxy RMI connect error. Retry RMI connect (" + i + ")");
					continue;
				}
			}
		}
		
		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			throw ServerErrorUtil.createException(serverError);
		}
	}

	private void lookup() throws MalformedURLException, RemoteException, NotBoundException {
		handler =
			(FrameEditHandler) Naming.lookup(WifeUtilities.createRmiFrameEditManager());
	}

	/**
	 * name�Ŏw�肳�ꂽ�y�[�W��`��XML�ŕԂ��܂��B
	 * @param name �y�[�W��
	 * @return String �y�[�W��`��XML�\���B�y�[�W�������̏ꍇnull
	 */
	public String getPageXml(String name) throws RemoteException {
		if (serverError != null) {
			return "";
		}

		String page = null;
		for (int i = 1; i <= Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				page = handler.getPageXml(name);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				continue;
			}
		}
		
		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			serverError.printStackTrace();
		}
		return page;
	}

	/**
	 * name�Ŏw�肵���y�[�W��`��ݒ肵�܂��B
	 * @param name �y�[�W��
	 * @param xml �y�[�W��`
	 */
	public void setPageXml(String name, String xml) throws RemoteException {
		if (serverError != null) {
			return;
		}

		for (int i = 1; i <= Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				handler.setPageXml(name, xml);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				continue;
			}
		}
		
		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			serverError.printStackTrace();
		}
	}

	/**
	 * loggingName�Ŏw�肵�����M���O�t�@�C���ɕۑ�����鍀�ڂ̑������X�g��Ԃ��܂��B
	 * @param loggingName ���M���O�t�@�C����
	 * @return ���ڂ̑������X�g
	 */
	public List getLoggingHolders(String loggingName) throws RemoteException {
		if (serverError != null) {
			logger.info(serverError);
			return Collections.EMPTY_LIST;
		}

		List list = null;
		for (int i = 1; i <= Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				list = handler.getLoggingHolders(loggingName);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				continue;
			}
		}
		
		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			serverError.printStackTrace();
		}
		return list;
	}

}
