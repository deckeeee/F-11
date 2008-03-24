/*
 * =============================================================================
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
 *
 */

package org.F11.scada.tool.sound.individual;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.login.PermissionCheck;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class IndividualAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if (!PermissionCheck.check("sound_individual_setting", request))
			return (mapping.getInputForward());

		S2Container container = SingletonS2ContainerFactory.getContainer();
		IndividualService individualService =
			(IndividualService) container.getComponent(IndividualService.class);
		String provider = getProvider(request);
		String holder = getHolder(request);
		IndividualDto dto = individualService.getIndividual(provider, holder);
		BeanUtils.copyProperties(form, dto);
		
		return (mapping.findForward("continue"));
	}

	private String getProvider(HttpServletRequest request) {
		String provider = request.getParameter("provider");
		return ToolUtility.htmlEscape(provider.trim());
	}

	private String getHolder(HttpServletRequest request) {
		String holder = request.getParameter("holder");
		return ToolUtility.htmlEscape(holder.trim());
	}
}
