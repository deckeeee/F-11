/*
 * Created on 2003/08/25
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.point.email;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.io.PointEmailStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.util.ConnectionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

/**
 * @author hori
 */
public class RemoveEmailAction extends Action {
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
		if (!PermissionCheck.check("email", request))
			return (mapping.getInputForward());

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		int group_id = ((Integer) actionForm.get("group_id")).intValue();
		String address = (String) actionForm.get("address");

		PointEmailBean emailBean = new PointEmailBean();
		emailBean.setGroup_id(group_id);
		emailBean.setAddress(ToolUtility.htmlEscape(address));

		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			PointEmailStore store = new PointEmailStore();
			store.removePointEmail(util, emailBean);

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

		log.debug("RemoveEmailAction : " + emailBean);
		return (mapping.findForward("continue"));
	}

}
