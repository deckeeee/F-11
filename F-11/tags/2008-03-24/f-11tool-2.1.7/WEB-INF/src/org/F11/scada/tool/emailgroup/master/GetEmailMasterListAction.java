/*
 * Created on 2003/08/22 To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.emailgroup.master;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.io.EmailgroupMasterStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.util.ConnectionUtil;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author hori
 */
public class GetEmailMasterListAction extends Action {

	/*
	 * (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (!PermissionCheck.check("email_group_master", request))
			return (mapping.getInputForward());

		Connection con = null;
		List emailgroupMasterList = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			EmailgroupMasterStore store = new EmailgroupMasterStore();
			emailgroupMasterList = store.getAllEmailgroupMaster(util);

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
		request.setAttribute("emailgroupMasterList", emailgroupMasterList);

		return (mapping.findForward("continue"));
	}

}
