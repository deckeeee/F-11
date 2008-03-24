/*
 * Created on 2003/08/25
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.page.bar;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.F11.scada.server.ServerConstruction;
import org.F11.scada.tool.io.PageListBean;
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
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * @author hori
 */
public class SetBarPointAction extends Action {
	protected Log log = LogFactory.getLog(this.getClass());

	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!PermissionCheck.check("page", request))
			return (mapping.getInputForward());

		BarPointForm actionForm = (BarPointForm) form;
		HttpSession session = request.getSession();
		PageListBean page = (PageListBean) session.getAttribute("page");

		try {
			S2Container container = SingletonS2ContainerFactory.getContainer();
			setMainSystem(container, page, actionForm);
			setSubSystem(container, page, actionForm);
		} catch (Exception e) {
			log.error("ページ変更中にエラーが発生しました : ", e);
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"error.page.notset"));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}

		return (mapping.findForward("continue"));
	}

	private void setMainSystem(
			S2Container container,
			PageListBean page,
			BarPointForm actionForm) throws RemoteException {
		BarService service = (BarService) container
				.getComponent(BarService.class);
		service.setBar(page, actionForm);
	}

	private void setSubSystem(
			S2Container container,
			PageListBean page,
			BarPointForm actionForm)
			throws MalformedURLException,
			RemoteException,
			NotBoundException {
		ServerConstruction construction = (ServerConstruction) container
				.getComponent(ServerConstruction.class);
		if (ServerConstructionUtil.isMainSystem(construction, log)) {
			List subSystems = construction.getSubSystems();
			for (Iterator i = subSystems.iterator(); i.hasNext();) {
				String subSystem = (String) i.next();
				BarService service = (BarService) Naming.lookup("//"
						+ subSystem + "/" + BarService.class.getName());
				service.setBar(page, actionForm);
			}
		}
	}
}
