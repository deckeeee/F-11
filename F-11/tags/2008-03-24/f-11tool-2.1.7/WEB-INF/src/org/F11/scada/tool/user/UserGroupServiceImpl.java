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

package org.F11.scada.tool.user;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;

import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.io.UserGroupStore;
import org.F11.scada.util.ConnectionUtil;
import org.F11.scada.util.RmiUtil;

public class UserGroupServiceImpl implements UserGroupService {
	public UserGroupServiceImpl(int regport, int objport) {
		RmiUtil.registryServer(this, UserGroupService.class, regport, objport);
	}

	public boolean insertUser(String user, String pass) throws RemoteException {
		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			UserGroupStore store = new UserGroupStore();
			if (store.isExistUser(util, user)) {
				return false;
			}

			store.insertUser(util, user, pass);
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
		return true;
	}

	public void removeUser(String user) throws RemoteException {
		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			UserGroupStore store = new UserGroupStore();
			store.removeUser(util, user);
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

	public void updateUserPassword(String user, String pass)
			throws RemoteException {
		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			UserGroupStore store = new UserGroupStore();

			store.updateUserPassword(util, user, pass);
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
