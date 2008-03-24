/*
 * 作成日: 2005/09/21 TODO この生成されたファイルのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 * コード・スタイル - コード・テンプレート
 */
package org.F11.scada.tool.emailgroup.individual;

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
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * @author hori
 */
public class SetEmailIndividualAction extends Action {
	protected Log log = LogFactory.getLog(this.getClass());

	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!PermissionCheck.check("email_individual_setting", request))
			return (mapping.getInputForward());

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		String provider = (String) actionForm.get("provider");
		String holder = (String) actionForm.get("holder");
		String[] assignList = (String[]) actionForm.get("assignList");
		String param = (String) actionForm.get("address");
		String address = ToolUtility.htmlEscape(param.trim());

		S2Container container = SingletonS2ContainerFactory.getContainer();
		setMainSystem(container, provider, holder, assignList, address);
		setSubSystem(container, provider, holder, assignList, address);

		return (mapping.findForward("continue"));
	}

	private void setMainSystem(
			S2Container container,
			String provider,
			String holder,
			String[] assignList,
			String address) throws RemoteException {
		IndividualService service = (IndividualService) container
				.getComponent(IndividualService.class);
		service.updateIndividual(provider, holder, assignList, address);
	}

	private void setSubSystem(
			S2Container container,
			String provider,
			String holder,
			String[] assignList,
			String address)
			throws MalformedURLException,
			RemoteException,
			NotBoundException {
		ServerConstruction construction = (ServerConstruction) container
				.getComponent(ServerConstruction.class);
		if (ServerConstructionUtil.isMainSystem(construction, log)) {
			List subSystems = construction.getSubSystems();
			for (Iterator i = subSystems.iterator(); i.hasNext();) {
				String subSystem = (String) i.next();
				IndividualService service = (IndividualService) Naming
						.lookup("//" + subSystem + "/"
								+ IndividualService.class.getName());
				service.updateIndividual(provider, holder, assignList, address);
			}
		}
	}
}
