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

package org.F11.scada.tool.emailgroup.individual;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;

import org.F11.scada.tool.io.EmailgroupIndividualStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.util.ConnectionUtil;
import org.F11.scada.util.RmiUtil;

public class IndividualServiceImpl implements IndividualService {
	public IndividualServiceImpl(int regport, int objport) {
		RmiUtil.registryServer(this, IndividualService.class, regport, objport);
	}

	public void updateIndividual(
			String provider,
			String holder,
			String[] assignList,
			String address) throws RemoteException {
		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			con.setAutoCommit(false);
			StrategyUtility util = new StrategyUtility(con);

			EmailgroupIndividualStore store = new EmailgroupIndividualStore();
			store.deleteEmailgroupIndividual(util, provider, holder);
			for (int i = 0; i < assignList.length; i++) {
				int groupId = Integer.parseInt(assignList[i]);
				store.insertEmailgroupIndividual2(
						util,
						provider,
						holder,
						groupId);
			}
			if (store.updateEmailgroupIndividual(
					util,
					provider,
					holder,
					address) <= 0) {
				store.insertEmailgroupIndividual(
						util,
						provider,
						holder,
						address);
			}
			con.commit();
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RemoteException("メール送信設定中にエラーが発生しました : ", e);
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
