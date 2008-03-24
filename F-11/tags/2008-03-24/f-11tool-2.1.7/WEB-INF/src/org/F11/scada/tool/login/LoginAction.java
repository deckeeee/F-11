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
 *
 */

package org.F11.scada.tool.login;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.F11.scada.WifeUtilities;
import org.F11.scada.security.auth.Crypt;
import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.io.UserGroupStore;
import org.F11.scada.util.ConnectionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

/**
 * @author hori
 */
public class LoginAction extends Action {
	protected Log log = LogFactory.getLog(this.getClass());

	public LoginAction() throws ClassNotFoundException {
		super();
		Class.forName(WifeUtilities.getJdbcDriver());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		String name = (String) actionForm.get("name");
		String pass = (String) actionForm.get("pass");

		name = ToolUtility.htmlEscape(name);
		pass = Crypt.crypt(ToolUtility.htmlEscape(pass).toCharArray());

		ActionErrors errors = new ActionErrors();
		//UserGroupStore userStore = new UserGroupStore();
		UserPermission permiss = null;

		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			UserGroupStore store = new UserGroupStore();
			if (!store.checkUserPassword(util, name, pass)) {
				log.debug("error.password.mismatch");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.password.mismatch"));
			}

			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				return (mapping.getInputForward());
			}

			permiss = store.getUserPermission(util, name);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}

		// Save our logged-in user in the session
		HttpSession session = request.getSession();
		session.setAttribute("user", permiss);

		if (log.isDebugEnabled()) {
			log.debug("LogonAction: User '" + name + "' logged on in session " + session.getId());
		}
		return (mapping.findForward("continue"));
	}

}
