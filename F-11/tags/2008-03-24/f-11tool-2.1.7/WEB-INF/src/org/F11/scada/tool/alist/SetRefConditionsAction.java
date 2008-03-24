/*
 * Created on 2003/10/10
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.alist;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.F11.scada.tool.login.PermissionCheck;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author hori
 */
public class SetRefConditionsAction extends Action {

	/**
	 * 
	 */
	public SetRefConditionsAction() {
		super();
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
		if (!PermissionCheck.check("career", request))
			return (mapping.getInputForward());

		HttpSession session = request.getSession();
		RefConditionsForm refcond = (RefConditionsForm) session.getAttribute("refcond");
		RefConditionsForm refcondForm = (RefConditionsForm) form;
		refcondForm.htmlEscape();
		refcond.setAll(refcondForm);

		return (mapping.findForward("continue"));
	}

}
