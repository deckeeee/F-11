/*
 * Created on 2003/08/19
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.user;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.security.auth.Crypt;
import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.io.UserGroupStore;
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
public class SetPasswordAction extends Action {
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
		if (!PermissionCheck.checkLogin(request))
			return (mapping.getInputForward());

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		String name = (String) actionForm.get("name");
		String passOld = (String) actionForm.get("passOld");
		String pass1 = (String) actionForm.get("pass1");
		String pass2 = (String) actionForm.get("pass2");

		name = ToolUtility.htmlEscape(name);
		passOld = Crypt.crypt(ToolUtility.htmlEscape(passOld).toCharArray());
		pass1 = Crypt.crypt(ToolUtility.htmlEscape(pass1).toCharArray());
		pass2 = Crypt.crypt(ToolUtility.htmlEscape(pass2).toCharArray());

		ActionErrors errors = new ActionErrors();

		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			UserGroupStore store = new UserGroupStore();
			if (!store.checkUserPassword(util, name, passOld)) {
				log.debug("error.password.mismatch");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.password.mismatch"));
			}
			if (!pass1.equals(pass2)) {
				log.debug("error.password.twofields");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.password.twofields"));
			}

			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				return (mapping.getInputForward());
			}

			store.updateUserPassword(util, name, pass1);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}

		return (mapping.findForward("continue"));
	}

}
