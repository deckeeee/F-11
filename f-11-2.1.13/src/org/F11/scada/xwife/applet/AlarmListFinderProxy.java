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
 */
package org.F11.scada.xwife.applet;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.server.alarm.table.AlarmListFinder;
import org.F11.scada.server.alarm.table.AttributeRecord;
import org.F11.scada.server.alarm.table.FindAlarmCondition;
import org.F11.scada.server.alarm.table.FindAlarmPosition;
import org.F11.scada.server.alarm.table.FindAlarmTable;
import org.apache.log4j.Logger;

/**
 * 警報一覧 検索インターフェイスのプロキシです。
 * 
 * @author hori <hoti@users.sourceforge.jp>
 */
public class AlarmListFinderProxy extends UnicastRemoteObject implements AlarmListFinder {
	private static final long serialVersionUID = 6259183668199393182L;
	/** FrameDefineManagerの参照です */
	private AlarmListFinder handler;
	/** サーバーエラー例外 */
	private Exception serverError;

	private static final int MAX_RETRY = 30;
	private static final long MAX_RETRY_TIME = 30000L;

	private final Logger logger = Logger.getLogger(AlarmListFinderProxy.class);

	/**
	 * @throws java.rmi.RemoteException
	 */
	public AlarmListFinderProxy(int recvPort) throws RemoteException, MalformedURLException {
		super(recvPort);

		Naming.rebind(WifeUtilities.createRmiAlarmListFinderHandler(), this);

		for (int i = 1; i <= MAX_RETRY; i++) {
			try {
				lookup();
				serverError = null;
				logger.debug("AlarmListFinderProxy bound in registry");
				break;
			} catch (Exception e) {
				serverError = e;
				try {
					Thread.sleep(MAX_RETRY_TIME);
				} catch (InterruptedException e2) {
				}
				logger
						.info("AlarmListFinderProxy RMI connect error. Retry RMI connect (" + i
								+ ")");
				continue;
			}
		}

		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			throw ServerErrorUtil.createException(serverError);
		}
	}

	private void lookup() throws MalformedURLException, RemoteException, NotBoundException {
		handler = (AlarmListFinder) Naming.lookup(WifeUtilities.createRmiAlarmListFinderManager());
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see org.F11.scada.server.alarm.table.AlarmListFinder#getSummaryList(org.F11.scada.server.alarm.table.FindAlarmCondition,
	 *      org.F11.scada.server.alarm.table.FindAlarmPosition)
	 */
	public FindAlarmTable getSummaryList(FindAlarmCondition cond, FindAlarmPosition fac, int order)
			throws RemoteException {
		FindAlarmTable table = new FindAlarmTable();

		if (serverError != null) {
			return table;
		}

		for (int i = 1; i <= Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				table = handler.getSummaryList(cond, fac, order);
				serverError = null;
				break;
			} catch (RemoteException e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				logger.info("getSummaryList retry rmi lookup. (" + i + "/"
						+ Globals.RMI_METHOD_RETRY_COUNT + ")");
				serverError = e;
				continue;
			} catch (SQLException e) {
				logger.error("サーバーコネクションエラー発生", e);
				break;
			}
		}

		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			serverError.printStackTrace();
		}

		return table;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see org.F11.scada.server.alarm.table.AlarmListFinder#getHistoryList(org.F11.scada.server.alarm.table.FindAlarmCondition,
	 *      org.F11.scada.server.alarm.table.FindAlarmPosition)
	 */
	public FindAlarmTable getHistoryList(FindAlarmCondition cond, FindAlarmPosition fac, int order)
			throws RemoteException {
		FindAlarmTable table = new FindAlarmTable();

		if (serverError != null) {
			return table;
		}

		for (int i = 1; i <= Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				table = handler.getHistoryList(cond, fac, order);
				serverError = null;
				break;
			} catch (RemoteException e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				logger.info("getHistoryList retry rmi lookup. (" + i + "/"
						+ Globals.RMI_METHOD_RETRY_COUNT + ")");
				serverError = e;
				continue;
			} catch (SQLException e) {
				logger.error("サーバーコネクションエラー発生", e);
				break;
			}
		}

		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			serverError.printStackTrace();
		}

		return table;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see org.F11.scada.server.alarm.table.AlarmListFinder#getCareerList(org.F11.scada.server.alarm.table.FindAlarmCondition,
	 *      org.F11.scada.server.alarm.table.FindAlarmPosition)
	 */
	public FindAlarmTable getCareerList(FindAlarmCondition cond, FindAlarmPosition fac, int order)
			throws RemoteException {
		FindAlarmTable table = new FindAlarmTable();

		if (serverError != null) {
			return table;
		}

		for (int i = 1; i <= Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				table = handler.getCareerList(cond, fac, order);
				serverError = null;
				break;
			} catch (RemoteException e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				logger.info("getCareerList retry rmi lookup. (" + i + "/"
						+ Globals.RMI_METHOD_RETRY_COUNT + ")");
				serverError = e;
				continue;
			} catch (SQLException e) {
				logger.error("サーバーコネクションエラー発生", e);
				break;
			}
		}

		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			serverError.printStackTrace();
		}

		return table;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see org.F11.scada.server.alarm.table.AlarmListFinder#getAttributeRecords()
	 */
	public AttributeRecord[] getAttributeRecords() throws SQLException, RemoteException {
		AttributeRecord[] table = new AttributeRecord[0];

		if (serverError != null) {
			return table;
		}

		for (int i = 1; i <= Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				table = handler.getAttributeRecords();
				serverError = null;
				break;
			} catch (RemoteException e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				logger.info("getCareerList retry rmi lookup. (" + i + "/"
						+ Globals.RMI_METHOD_RETRY_COUNT + ")");
				serverError = e;
				continue;
			} catch (SQLException e) {
				logger.error("サーバーコネクションエラー発生", e);
				break;
			}
		}

		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			serverError.printStackTrace();
		}

		return table;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see org.F11.scada.server.alarm.table.AlarmListFinder#setHistoryCheck(int,
	 *      java.lang.String, java.lang.String, java.sql.Timestamp)
	 */
	public void setHistoryCheck(Integer point, String provider, String holder, Timestamp on_date)
			throws SQLException, RemoteException {
		try {
			handler.setHistoryCheck(point, provider, holder, on_date);
		} catch (Exception e) {
			logger.error("サーバーコネクションエラー発生", e);
		}
	}

	public void setHistoryCheckAll() throws SQLException, RemoteException {
		try {
			handler.setHistoryCheckAll();
		} catch (Exception e) {
			logger.error("サーバーコネクションエラー発生", e);
		}
	}

	public List getPriorityTable() throws RemoteException {
		return handler.getPriorityTable();
	}
}
