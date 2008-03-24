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
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

/**
 * @author hori
 */
public class SetEmailAction extends Action {
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
		int old_group_id =
			((Integer) actionForm.get("old_group_id")).intValue();
		String old_address = (String) actionForm.get("old_address");

		PointEmailBean emailBean = new PointEmailBean();
		emailBean.setGroup_id(group_id);
		emailBean.setAddress(ToolUtility.htmlEscape(address));
		PointEmailBean old_emailBean = new PointEmailBean();
		old_emailBean.setGroup_id(old_group_id);
		old_emailBean.setAddress(ToolUtility.htmlEscape(old_address));
		
		ActionErrors errors = new ActionErrors();

		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			PointEmailStore store = new PointEmailStore();
			int count = store.getCountPointEmail(util, emailBean);

			if (count > 0) {
				errors.add(
					ActionErrors.GLOBAL_ERROR,
					new ActionError("error.point.email.duplicate"));
				log.debug("error.point.email.duplicate");
			}
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				return (mapping.getInputForward());
			}
			store.updatePointEmail(util, emailBean, old_emailBean);

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

		log.debug("SetEmailAction : " + emailBean);
		return (mapping.findForward("continue"));
	}

}
