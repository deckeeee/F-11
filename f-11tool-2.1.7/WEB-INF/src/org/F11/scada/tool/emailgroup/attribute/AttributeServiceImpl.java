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

package org.F11.scada.tool.emailgroup.attribute;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.F11.scada.tool.io.EmailgroupAttributeStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.util.ConnectionUtil;
import org.F11.scada.util.RmiUtil;

public class AttributeServiceImpl implements AttributeService {
	public AttributeServiceImpl(int regport, int objport) {
		RmiUtil.registryServer(this, AttributeService.class, regport, objport);
	}

	public void insertAttribute(
			int attribute_id,
			String[] assignList,
			List groupIds,
			String address) throws RemoteException {
		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			con.setAutoCommit(false);
			StrategyUtility util = new StrategyUtility(con);

			EmailgroupAttributeStore store = new EmailgroupAttributeStore();
			store.deleteEmailgroupAttribute(util, attribute_id);
			for (int i = 0, size = groupIds.size(); i < size; i++) {
				int groupId = Integer.parseInt((String) groupIds.get(i));
				store.insertEmailgroupAttribute2(util, attribute_id, groupId);
				store.updateEmailgroupIndividual2(util, attribute_id, groupId);
			}
			store.updateEmailgroupIndividual(util, attribute_id, address);

			if (store.updateEmailgroupAttribute(util, attribute_id, address) <= 0) {
				store.insertEmailgroupAttribute(util, attribute_id, address);
			}

			con.commit();
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RemoteException("", e);
		} finally {
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
