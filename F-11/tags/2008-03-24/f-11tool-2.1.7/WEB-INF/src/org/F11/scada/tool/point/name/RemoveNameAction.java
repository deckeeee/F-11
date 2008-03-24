/*
 * Created on 2003/08/25
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.point.name;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.server.ServerConstruction;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.tool.util.ServerConstructionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * @author hori
 */
public class RemoveNameAction extends Action {
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
		if (!PermissionCheck.check("name", request)
				&& !PermissionCheck.check("ffugroupname", request))
			return (mapping.getInputForward());

		PointNameBean nameBean = SearchUtil
				.getPointNameBean((DynaValidatorForm) form);
		S2Container container = SingletonS2ContainerFactory.getContainer();
		removeMainSystem(nameBean, container);
		removeSubSystem(nameBean, container);
		SearchUtil.resetSearchField(request, form);
		return (mapping.findForward("continue"));
	}

	private void removeMainSystem(PointNameBean nameBean, S2Container container)
			throws RemoteException {
		NameService service = (NameService) container
				.getComponent(NameService.class);
		service.removeName(nameBean);
	}

	private void removeSubSystem(PointNameBean nameBean, S2Container container)
			throws NotBoundException,
			MalformedURLException,
			RemoteException {
		ServerConstruction construction = (ServerConstruction) container
				.getComponent(ServerConstruction.class);
		if (ServerConstructionUtil.isMainSystem(construction, log)) {
			List subSystems = construction.getSubSystems();
			for (Iterator i = subSystems.iterator(); i.hasNext();) {
				String subSystem = (String) i.next();
				NameService service = (NameService) Naming.lookup("//"
						+ subSystem + "/" + NameService.class.getName());
				service.removeName(nameBean);
			}
		}
	}
}
