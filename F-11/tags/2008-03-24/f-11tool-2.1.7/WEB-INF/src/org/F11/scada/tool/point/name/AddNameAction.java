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
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * ポイント情報を追加するアクションクラスです。
 * 
 * @author hori
 */
public class AddNameAction extends Action {
	/** Logging API */
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
				&& !PermissionCheck.check("ffugroupname", request)) {
			return (mapping.getInputForward());
		}

		PointNameBean nameBean = SearchUtil
				.getPointNameBean((DynaValidatorForm) form);

		ActionErrors errors = new ActionErrors();

		S2Container container = SingletonS2ContainerFactory.getContainer();
		NameService service = (NameService) container
				.getComponent(NameService.class);
		if (!service.addName(nameBean)) {
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"error.point.name.idduplicate"));
			log.debug("error.point.name.idduplicate");
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		addSubSystem(nameBean, container);

		SearchUtil.resetSearchField(request, form);
		return (mapping.findForward("continue"));
	}

	private void addSubSystem(PointNameBean nameBean, S2Container container)
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
				service.addName(nameBean);
			}
		}
	}
}
