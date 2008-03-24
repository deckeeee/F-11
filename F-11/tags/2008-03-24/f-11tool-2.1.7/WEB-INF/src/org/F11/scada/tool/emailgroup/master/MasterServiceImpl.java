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

package org.F11.scada.tool.emailgroup.master;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;

import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.io.EmailgroupMasterStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.util.ConnectionUtil;
import org.F11.scada.util.RmiUtil;

public class MasterServiceImpl implements MasterService {
	public MasterServiceImpl(int regport, int objport) {
		RmiUtil.registryServer(this, MasterService.class, regport, objport);
	}

	public void insertGroupMaster(
			int group_id,
			String group_name,
			String[] address,
			String addressNew) throws RemoteException {

		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);
			EmailgroupMasterStore store = new EmailgroupMasterStore();

			store.removeEmailgroupMaster(util, group_id);
			store.insertEmailgroupMaster(util, group_id, 0, group_name);

			int kindNo = 1;
			for (int j = 0; j < address.length; j++) {
				if (address[j] == null)
					continue;
				String addr = ToolUtility.htmlEscape(address[j]).trim();
				if (addr.length() <= 0)
					continue;
				store.insertEmailgroupMaster(util, group_id, kindNo, addr);
				kindNo++;
			}
			if (addressNew != null && 0 < addressNew.trim().length()) {
				store.insertEmailgroupMaster(util, group_id, kindNo, addressNew
						.trim());
			}
		} catch (Exception e) {
			throw new RemoteException("グループマスタ更新時にエラー発生 : ", e);
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

	public boolean removeGroupMaster(int group_id) throws RemoteException {
		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);
			EmailgroupMasterStore store = new EmailgroupMasterStore();
			if (store.getGroupID(util, group_id)) {
				return false;
			}
			store.removeEmailgroupMaster(util, group_id);
			return true;
		} catch (Exception e) {
			throw new RemoteException("グループマスタ更新時にエラー発生 : ", e);
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
