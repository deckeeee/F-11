/*
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

package org.F11.scada.tool.autoprint;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.edit.ServerEditHandler;
import org.F11.scada.tool.io.AutoPrintStore;
import org.F11.scada.tool.io.AutoPrintStoreFactory;
import org.F11.scada.util.ConnectionUtil;
import org.F11.scada.util.RmiUtil;

public class AutoPrintServiceImpl implements AutoPrintService {
	public AutoPrintServiceImpl(int regport, int objport) {
		RmiUtil.registryServer(this, AutoPrintService.class, regport, objport);
	}

	public void updateAutoPrint(AutoPrintForm form) throws RemoteException {
		Connection con = null;
		try {
			ServerEditHandler handler = (ServerEditHandler) Naming
					.lookup(WifeUtilities.createRmiServerEditManager());
			con = ConnectionUtil.getConnection();
			AutoPrintStoreFactory factory = new AutoPrintStoreFactory(con);
			AutoPrintStore store = factory.getAutoPrintStore(handler
					.getServerName());
			store.updateAutoPrint(form);
			handler.editAutoPrint();
		} catch (MalformedURLException e) {
			throw new RemoteException("MalformedURLException", e);
		} catch (NotBoundException e) {
			throw new RemoteException("NotBoundException", e);
		} catch (SQLException e) {
			throw new RemoteException("SQLException", e);
		} catch (IOException e) {
			throw new RemoteException("IOException", e);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
	}
}
