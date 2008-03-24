/*
 * Created on 2003/08/25
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.page.trend;

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
import org.apache.struts.validator.DynaValidatorForm;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * @author hori
 */
public class RemoveTrendSeriesAction extends Action {
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
		if (!PermissionCheck.check("page", request))
			return (mapping.getInputForward());

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		String key = (String) actionForm.get("key");
		if (key == null || key.length() <= 0) {
			return (mapping.getInputForward());
		}
		int id = Integer.parseInt(key);
		HttpSession session = request.getSession();
		PageListBean page = (PageListBean) session.getAttribute("page");

		try {
			S2Container container = SingletonS2ContainerFactory.getContainer();
			removeMainSystem(container, page, id);
			removeSubSystem(container, page, id);
		} catch (Exception e) {
			log.error("トレンドグラフ定義送信中にエラー発生", e);
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"error.page.notset"));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}

		return (mapping.findForward("continue"));
	}

	private void removeMainSystem(
			S2Container container,
			PageListBean page,
			int id) throws RemoteException {
		TrendService service = (TrendService) container
				.getComponent(TrendService.class);
		service.removeSeries(page, id);
	}

	private void removeSubSystem(
			S2Container container,
			PageListBean page,
			int id)
			throws MalformedURLException,
			RemoteException,
			NotBoundException {
		ServerConstruction construction = (ServerConstruction) container
				.getComponent(ServerConstruction.class);
		if (ServerConstructionUtil.isMainSystem(construction, log)) {
			List subSystems = construction.getSubSystems();
			for (Iterator i = subSystems.iterator(); i.hasNext();) {
				String subSystem = (String) i.next();
				TrendService service = (TrendService) Naming.lookup("//"
						+ subSystem + "/" + TrendService.class.getName());
				service.removeSeries(page, id);
			}
		}
	}
}
