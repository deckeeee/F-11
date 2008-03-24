/*
 * Created on 2003/08/20
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.group;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class SetGroupAction extends Action {
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
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"error.group.nocheckuser"));
		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}

		S2Container container = SingletonS2ContainerFactory.getContainer();
		setMainSystem(container, name, select);
		setSubSystem(container, name, select);

		log.debug("SetGroupAction : " + name);
		return (mapping.findForward("continue"));
	}

	private void setMainSystem(
			S2Container container,
			String name,
			String[] select) throws RemoteException {
		GroupService service = (GroupService) container
				.getComponent(GroupService.class);
		service.setUserAssign(name, select);
	}

	private void setSubSystem(
			S2Container container,
			String name,
			String[] select)
			throws MalformedURLException,
			RemoteException,
			NotBoundException {
		ServerConstruction construction = (ServerConstruction) container
				.getComponent(ServerConstruction.class);
		if (ServerConstructionUtil.isMainSystem(construction, log)) {
			List subSystems = construction.getSubSystems();
			for (Iterator i = subSystems.iterator(); i.hasNext();) {
				String subSystem = (String) i.next();
				GroupService service = (GroupService) Naming.lookup("//"
						+ subSystem + "/" + GroupService.class.getName());
				service.setUserAssign(name, select);
			}
		}
	}

}
