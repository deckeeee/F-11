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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * �x��ꗗ �����C���^�[�t�F�C�X�ł��B
 * 
 * @author hori <hoti@users.sourceforge.jp>
 */
public interface AlarmListFinder extends Remote {

	public FindAlarmTable getSummaryList(FindAlarmCondition cond, FindAlarmPosition fac, int order)
			throws SQLException, RemoteException;

	public FindAlarmTable getHistoryList(FindAlarmCondition cond, FindAlarmPosition fac, int order)
			throws SQLException, RemoteException;

	public FindAlarmTable getCareerList(FindAlarmCondition cond, FindAlarmPosition fac, int order)
			throws SQLException, RemoteException;

	public AttributeRecord[] getAttributeRecords() throws SQLException, RemoteException;

	public void setHistoryCheck(Integer point, String provider, String holder, Timestamp on_date)
			throws SQLException, RemoteException;

	public void setHistoryCheckAll() throws SQLException, RemoteException;

	List getPriorityTable() throws RemoteException;
}
