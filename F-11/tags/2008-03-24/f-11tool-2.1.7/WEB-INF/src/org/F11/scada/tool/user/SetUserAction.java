/*
 * Created on 2003/08/19
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.user;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.security.auth.Crypt;
import org.F11.scada.server.ServerConstruction;
import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.tool.util.ServerConstructionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * @author hori
 */
public class SetUserAction extends Action {
	protected Log log = LogFactory.getLog(this.getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!PermissionCheck.check("user", request))
			return (mapping.getInputForward());

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		String name = (String) actionForm.get("name");
		String pass1 = (String) actionForm.get("pass1");
		String pass2 = (String) actionForm.get("pass2");

		name = ToolUtility.htmlEscape(name);
		pass1 = Crypt.crypt(ToolUtility.htmlEscape(pass1).toCharArray());
		pass2 = Crypt.crypt(ToolUtility.htmlEscape(pass2).toCharArray());

		if (!pass1.equals(pass2)) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"error.password.twofields"));
			log.debug("error.password.twofields");
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}

		S2Container container = SingletonS2ContainerFactory.getContainer();
		updateMainSystem(name, pass1, container);
		updateSubSystem(name, pass1, container);

		return (mapping.findForward("continue"));
	}

	private void updateMainSystem(
			String name,
			String pass1,
			S2Container container) throws RemoteException {
		UserGroupService service = (UserGroupService) container
				.getComponent(UserGroupService.class);
		service.updateUserPassword(name, pass1);
	}

	private void updateSubSystem(
			String name,
			String pass1,
			S2Container container)
			throws NotBoundException,
			MalformedURLException,
			RemoteException {
		ServerConstruction construction = (ServerConstruction) container
				.getComponent(ServerConstruction.class);
		if (ServerConstructionUtil.isMainSystem(construction, log)) {
			List subSystems = construction.getSubSystems();
			for (Iterator i = subSystems.iterator(); i.hasNext();) {
				String subSystem = (String) i.next();
				UserGroupService service = (UserGroupService) Naming
						.lookup("//" + subSystem + "/"
								+ UserGroupService.class.getName());
				service.updateUserPassword(name, pass1);
			}
		}
	}

}
