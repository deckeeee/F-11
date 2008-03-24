/*
 * Created on 2003/08/21
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.group;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.io.UserGroupStore;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.util.ConnectionUtil;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

/**
 * @author hori
 */
public class GetGroupAction extends Action {

	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
		if (!PermissionCheck.check("group", request))
			return (mapping.getInputForward());

		String name = (String) request.getParameter("name");
		name = ToolUtility.htmlEscape(name);

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		actionForm.set("name", name);

		Connection con = null;
		String[] users = null;
		Collection assignList = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			UserGroupStore store = new UserGroupStore();

			List assUList = store.getAssignUsers(util, name);
			assignList = new TreeSet();
			assignList.addAll(assUList);

			List userList = store.getAllUser(util);
			users = new String[userList.size()];
			int i = 0;
			for (Iterator it = userList.iterator(); it.hasNext(); i++) {
				users[i] = (String) it.next();
			}
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
		actionForm.set("users", users);
		request.setAttribute("assignList", assignList);

		return (mapping.findForward("continue"));
	}

}
