/*
 * Created on 2003/08/25
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.point.email;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.login.PermissionCheck;
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
public class GetEmailAction extends Action {
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

		String group_id = request.getParameter("group_id");
		String address = request.getParameter("address");
		group_id = ToolUtility.htmlEscape(group_id).trim();
		address = ToolUtility.htmlEscape(address).trim();
		if (group_id.length() <= 0 || address.length() <= 0)
			return (mapping.getInputForward());

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		actionForm.set("group_id", new Integer(group_id));
		actionForm.set("address", address);
		actionForm.set("old_group_id", new Integer(group_id));
		actionForm.set("old_address", address);

		log.debug("GetEmailAction OK!");
		return (mapping.findForward("continue"));
	}
}
