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

package org.F11.scada.tool.point.name;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;

import org.F11.scada.server.alarm.table.PointTableBean;
import org.F11.scada.server.frame.FramePageEditTimeSupport;
import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.io.PointItemStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.util.ConnectionUtil;
import org.F11.scada.util.RmiUtil;

public class NameServiceImpl implements NameService {
	public NameServiceImpl(int regport, int objport) {
		RmiUtil.registryServer(this, NameService.class, regport, objport);
	}

	public boolean addName(PointNameBean bean) throws RemoteException {
		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			PointItemStore store = new PointItemStore();
			PointNameBean stordNameBean = store.getPointName(util, bean
					.getPoint());

			if (bean.getPoint() == stordNameBean.getPoint()) {
				return false;
			}
			store.insertPointName(util, bean);
			PointTableBean pointBean = new PointTableBean(bean.getPoint(), bean
					.getUnit(), bean.getName(), bean.getUnit_mark());

			FramePageEditTimeSupport support = ToolUtility
					.getFramePageEditTimeSupport();
			support.setPageEditTime(pointBean, null);
			return true;
		} catch (SQLException e) {
			throw new RemoteException("SQLException", e);
		} catch (IOException e) {
			throw new RemoteException("IOException", e);
		} catch (NotBoundException e) {
			throw new RemoteException("NotBoundException", e);
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

	public void removeName(PointNameBean bean) throws RemoteException {
		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			PointItemStore store = new PointItemStore();
			store.removePointName(util, bean.getPoint());

			con.close();
			con = null;

			FramePageEditTimeSupport support = ToolUtility
					.getFramePageEditTimeSupport();
			support.setPageEditTime(new PointTableBean(
					bean.getPoint(),
					null,
					null,
					null), null);
		} catch (SQLException e) {
			throw new RemoteException("SQLException", e);
		} catch (IOException e) {
			throw new RemoteException("IOException", e);
		} catch (NotBoundException e) {
			throw new RemoteException("NotBoundException", e);
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

	public void setName(PointNameBean bean) throws RemoteException {
		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			PointItemStore store = new PointItemStore();

			PointNameBean oldBean = store.getPointName(util, bean.getPoint());

			store.updatePointName(util, bean);
			PointTableBean oldPointBean = new PointTableBean(
					oldBean.getPoint(),
					oldBean.getUnit(),
					oldBean.getName(),
					oldBean.getUnit_mark());

			PointTableBean pointBean = new PointTableBean(bean.getPoint(), bean
					.getUnit(), bean.getName(), bean.getUnit_mark());

			FramePageEditTimeSupport support = ToolUtility
					.getFramePageEditTimeSupport();
			support.setPageEditTime(pointBean, null);

			FramePageEditTimeSupport managerDelegator = ToolUtility
					.getManagerDelegator();
			managerDelegator.setPageEditTime(pointBean, oldPointBean);
		} catch (SQLException e) {
			throw new RemoteException("SQLException", e);
		} catch (IOException e) {
			throw new RemoteException("IOException", e);
		} catch (NotBoundException e) {
			throw new RemoteException("NotBoundException", e);
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
