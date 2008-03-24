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

package org.F11.scada.tool.autoprint;

import java.rmi.Naming;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.edit.ServerEditHandler;
import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.io.AutoPrintStore;
import org.F11.scada.tool.io.AutoPrintStoreFactory;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.util.ConnectionUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author hori
 */
public class GetAutoPrintAction extends Action {
	protected Log log = LogFactory.getLog(this.getClass());

	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
		if (!PermissionCheck.check("autoprint", request))
			return (mapping.getInputForward());

		String name = request.getParameter("name");
		name = ToolUtility.htmlEscape(name).trim();
		if (name.length() <= 0)
			return (mapping.getInputForward());

		Connection con = null;
		AutoPrintForm autoPrintForm = null;
		try {
			con = ConnectionUtil.getConnection();
			ServerEditHandler handler =
				(ServerEditHandler) Naming.lookup(
					WifeUtilities.createRmiServerEditManager());
			AutoPrintStoreFactory factory = new AutoPrintStoreFactory(con);
			AutoPrintStore store =
				factory.getAutoPrintStore(handler.getServerName());
			autoPrintForm = store.getAutoPrint(name);

			con.close();
			con = null;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}

		AutoPrintForm actionForm = (AutoPrintForm) form;
		BeanUtils.copyProperties(actionForm, autoPrintForm);

		return (mapping.findForward("continue"));
	}
}
