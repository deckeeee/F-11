/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.server.io;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

import org.F11.scada.server.register.HolderString;
import org.apache.commons.collections.primitives.DoubleList;

public interface BarGraph2ValueListHandler extends Remote {
	public Date getFirstDateTime(String name, List<HolderString> dataHolders)
			throws RemoteException, SQLException;
	public Date getLastDateTime(String name, List<HolderString> dataHolders)
			throws RemoteException, SQLException;
	public SortedMap<Timestamp, DoubleList> getLoggingData(String name,
			List<HolderString> dataHolders, Date first, Date last)
			throws RemoteException, SQLException;
}
