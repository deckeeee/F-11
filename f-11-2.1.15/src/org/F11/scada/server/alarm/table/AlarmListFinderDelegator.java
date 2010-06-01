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
package org.F11.scada.server.alarm.table;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.alarm.table.postgresql.PostgreSQLAlarmListFinder;
import org.apache.log4j.Logger;

/**
 * 警報一覧 検索インターフェイスのリモートデリゲータです。
 * 
 * @author hori <hoti@users.sourceforge.jp>
 */
public class AlarmListFinderDelegator extends UnicastRemoteObject implements AlarmListFinder {
	private static final long serialVersionUID = 5221373913865018229L;
	/** ロギングAPI */
	private static Logger logger;
	/** 対象オブジェクト */
	private AlarmListFinder alarmListFinder;

	/**
	 * @throws java.rmi.RemoteException
	 */
	public AlarmListFinderDelegator(int recvPort) throws RemoteException, MalformedURLException,
			IOException {
		super(recvPort);
		logger = Logger.getLogger(getClass().getName());

		logger.info("AlarmListFinderManager:" + WifeUtilities.createRmiAlarmListFinderManager());
		Naming.rebind(WifeUtilities.createRmiAlarmListFinderManager(), this);
		logger.info("AlarmListFinderManager bound in registry");

		alarmListFinder = new PostgreSQLAlarmListFinder();
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see org.F11.scada.server.alarm.table.AlarmListFinder#getSummaryList(org.F11.scada.server.alarm.table.FindAlarmCondition,
	 *      org.F11.scada.server.alarm.table.FindAlarmPosition)
	 */
	public FindAlarmTable getSummaryList(FindAlarmCondition cond, FindAlarmPosition fac, int order)
			throws SQLException, RemoteException {
		return alarmListFinder.getSummaryList(cond, fac, order);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see org.F11.scada.server.alarm.table.AlarmListFinder#getHistoryList(org.F11.scada.server.alarm.table.FindAlarmCondition,
	 *      org.F11.scada.server.alarm.table.FindAlarmPosition)
	 */
	public FindAlarmTable getHistoryList(FindAlarmCondition cond, FindAlarmPosition fac, int order)
			throws SQLException, RemoteException {
		return alarmListFinder.getHistoryList(cond, fac, order);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see org.F11.scada.server.alarm.table.AlarmListFinder#getCareerList(org.F11.scada.server.alarm.table.FindAlarmCondition,
	 *      org.F11.scada.server.alarm.table.FindAlarmPosition)
	 */
	public FindAlarmTable getCareerList(FindAlarmCondition cond, FindAlarmPosition fac, int order)
			throws SQLException, RemoteException {
		return alarmListFinder.getCareerList(cond, fac, order);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see org.F11.scada.server.alarm.table.AlarmListFinder#getAttributeRecords()
	 */
	public AttributeRecord[] getAttributeRecords() throws SQLException, RemoteException {
		return alarmListFinder.getAttributeRecords();
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see org.F11.scada.server.alarm.table.AlarmListFinder#setHistoryCheck(int,
	 *      java.lang.String, java.lang.String, java.sql.Timestamp)
	 */
	public void setHistoryCheck(Integer point, String provider, String holder, Timestamp on_date)
			throws SQLException, RemoteException {
		alarmListFinder.setHistoryCheck(point, provider, holder, on_date);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see org.F11.scada.server.alarm.table.AlarmListFinder#setHistoryCheckAll()
	 */
	public void setHistoryCheckAll() throws SQLException, RemoteException {
		alarmListFinder.setHistoryCheckAll();
	}

	public List getPriorityTable() throws RemoteException {
		return alarmListFinder.getPriorityTable();
	}
}
