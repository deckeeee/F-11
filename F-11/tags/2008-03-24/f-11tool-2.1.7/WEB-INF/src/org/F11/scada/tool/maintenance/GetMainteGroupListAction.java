/*
 * Created on 2003/08/26
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.maintenance;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.io.UserGroupStore;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.util.ConnectionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author hori
 */
public class GetMainteGroupListAction extends Action {
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
		if (!PermissionCheck.check("maintenance", request))
			return (mapping.getInputForward());

		Connection con = null;
		Collection groupList = null;
		Collection userList = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			UserGroupStore store = new UserGroupStore();
			groupList = store.getAllGroups(util);
			userList = store.getAllUser(util);
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
		request.setAttribute("groupList", groupList);
		request.setAttribute("userList", userList);

		return (mapping.findForward("continue"));
	}

}
