/*
 */
package org.F11.scada.tool.emailgroup.attribute;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
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
 * 属性別設定変更処理 属性別設定テーブルを更新し、該当する属性を持つアイテムを個別設定に更新します。
 * 
 * @author hori
 */
public class SetEmailAttributeAction extends Action {
	protected Log log = LogFactory.getLog(this.getClass());

	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!PermissionCheck.check("email_attribute_setting", request))
			return (mapping.getInputForward());

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		int attribute_id = ((Integer) actionForm.get("attribute_id"))
				.intValue();
		String[] assignList = (String[]) actionForm.get("assignList");
		StringBuffer addressBuffer = new StringBuffer();

		List groupIds = new ArrayList();
		for (int i = 0; i < assignList.length; i++) {
			String addr = ToolUtility.htmlEscape(assignList[i].trim());
			if (addr != null && 0 < addr.length()) {
				groupIds.add(addr);
			}
		}

		String param = (String) actionForm.get("address");
		if (param != null && 0 < param.length()) {
			if (0 < addressBuffer.length())
				addressBuffer.append(", ");
			addressBuffer.append(ToolUtility.htmlEscape(param.trim()));
		}
		String address = addressBuffer.toString();

		S2Container container = SingletonS2ContainerFactory.getContainer();
		setMainSystem(container, attribute_id, assignList, groupIds, address);
		setSubSystem(container, attribute_id, assignList, groupIds, address);

		return (mapping.findForward("continue"));
	}

	private void setMainSystem(
			S2Container container,
			int attribute_id,
			String[] assignList,
			List groupIds,
			String address) throws RemoteException {
		AttributeService service = (AttributeService) container
				.getComponent(AttributeService.class);
		service.insertAttribute(attribute_id, assignList, groupIds, address);
	}

	private void setSubSystem(
			S2Container container,
			int attribute_id,
			String[] assignList,
			List groupIds,
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
				AttributeService service = (AttributeService) Naming
						.lookup("//" + subSystem + "/"
								+ AttributeService.class.getName());
				service.insertAttribute(
						attribute_id,
						assignList,
						groupIds,
						address);
			}
		}
	}
}
