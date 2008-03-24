/*
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.F11.scada.tool.autoprint;

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
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * @author hori
 */
public class SetAutoPrintAction extends Action {
	protected Log log = LogFactory.getLog(this.getClass());

	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!PermissionCheck.check("autoprint", request))
			return (mapping.getInputForward());

		AutoPrintForm actionForm = (AutoPrintForm) form;
		AutoPrintForm autoPrintForm = new AutoPrintForm();
		BeanUtils.copyProperties(autoPrintForm, actionForm);
		autoPrintForm.setName(ToolUtility.htmlEscape(actionForm.getName()));

		S2Container container = SingletonS2ContainerFactory.getContainer();
		setMainSystem(container, autoPrintForm);
		setSubSystem(container, autoPrintForm);

		log.debug("SetAutoPrintAction : " + autoPrintForm.getName());
		return (mapping.findForward("continue"));
	}

	private void setMainSystem(
			S2Container container,
			AutoPrintForm autoPrintForm) throws RemoteException {
		AutoPrintService service = (AutoPrintService) container
				.getComponent(AutoPrintService.class);
		service.updateAutoPrint(autoPrintForm);
	}

	private void setSubSystem(S2Container container, AutoPrintForm autoPrintForm)
			throws MalformedURLException,
			RemoteException,
			NotBoundException {
		ServerConstruction construction = (ServerConstruction) container
				.getComponent(ServerConstruction.class);
		if (ServerConstructionUtil.isMainSystem(construction, log)) {
			List subSystems = construction.getSubSystems();
			for (Iterator i = subSystems.iterator(); i.hasNext();) {
				String subSystem = (String) i.next();
				AutoPrintService service = (AutoPrintService) Naming
						.lookup("//" + subSystem + "/"
								+ AutoPrintService.class.getName());
				service.updateAutoPrint(autoPrintForm);
			}
		}
	}
}
