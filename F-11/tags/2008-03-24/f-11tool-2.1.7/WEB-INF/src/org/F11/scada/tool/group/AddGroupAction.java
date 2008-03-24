/*
 * Created on 2003/08/20
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.group;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class AddGroupAction extends Action {
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
		if (!PermissionCheck.check("group", request))
			return (mapping.getInputForward());

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		String name = (String) actionForm.get("name");
		String[] select = (String[]) actionForm.get("select");

		name = ToolUtility.htmlEscape(name);
		for (int i = 0; i < select.length; i++)
			select[i] = ToolUtility.htmlEscape(select[i]);

		ActionErrors errors = new ActionErrors();
		if (select.length <= 0) {
			log.debug("チェックなし");
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.group.nocheckuser"));
		}

		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			UserGroupStore store = new UserGroupStore();

			if (store.isExistGroup(util, name)) {
				log.debug("重複レコード");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.group.duplicate"));
			}
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				return (mapping.getInputForward());
			}

			store.setUserAssign(util, name, select);

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

		log.debug("SetGroupAction : " + name);

		return (mapping.findForward("continue"));
	}

}
