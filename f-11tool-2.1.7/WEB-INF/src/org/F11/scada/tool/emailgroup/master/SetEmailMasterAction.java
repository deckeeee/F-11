/*
 * Created on 2003/08/25 To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.emailgroup.master;

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
public class SetEmailMasterAction extends Action {
	protected Log log = LogFactory.getLog(this.getClass());

	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!PermissionCheck.check("email_group_master", request))
			return (mapping.getInputForward());

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		int group_id = ((Integer) actionForm.get("group_id")).intValue();
		String group_name = (String) actionForm.get("group_name");
		group_name = ToolUtility.htmlEscape(group_name).trim();
		log.debug(group_id + " " + group_name);
		String[] address = (String[]) actionForm.get("addrList");
		String addressNew = (String) actionForm.get("addressNew");
		S2Container container = SingletonS2ContainerFactory.getContainer();
		setMainSystem(container, group_id, group_name, address, addressNew);
		setSubSystem(container, group_id, group_name, address, addressNew);

		return (mapping.findForward("continue"));
	}

	private void setMainSystem(
			S2Container container,
			int group_id,
			String group_name,
			String[] address,
			String addressNew) throws RemoteException {
		MasterService service = (MasterService) container
				.getComponent(MasterService.class);
		service.insertGroupMaster(group_id, group_name, address, addressNew);
	}

	private void setSubSystem(
			S2Container container,
			int group_id,
			String group_name,
			String[] address,
			String addressNew)
			throws MalformedURLException,
			RemoteException,
			NotBoundException {
		ServerConstruction construction = (ServerConstruction) container
				.getComponent(ServerConstruction.class);
		if (ServerConstructionUtil.isMainSystem(construction, log)) {
			List subSystems = construction.getSubSystems();
			for (Iterator i = subSystems.iterator(); i.hasNext();) {
				String subSystem = (String) i.next();
				MasterService service = (MasterService) Naming.lookup("//"
						+ subSystem + "/" + MasterService.class.getName());
				service.insertGroupMaster(
						group_id,
						group_name,
						address,
						addressNew);
			}
		}

	}
}
