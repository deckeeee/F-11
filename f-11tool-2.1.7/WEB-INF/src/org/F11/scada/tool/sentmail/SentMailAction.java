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

package org.F11.scada.tool.sentmail;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.login.PermissionCheck;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.seasar.dao.pager.PagerSupport;
import org.seasar.dao.pager.PagerViewHelper;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class SentMailAction extends Action {
	/** ページャサポートクラス */
	private PagerSupport pager =
		new PagerSupport(50, SentMailCondition.class, "sentmailCondition");

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if (!PermissionCheck.check("sentmail", request))
			return (mapping.getInputForward());

		S2Container container = SingletonS2ContainerFactory.getContainer();
		SentMailService sentMailService =
			(SentMailService) container.getComponent(SentMailService.class);

		//パラメータoffsetを元にページャのoffset位置を更新
		pager.updateOffset(request);
		//検索
		SentMailCondition dto = (SentMailCondition) pager.getPagerCondition(request);
		List data = sentMailService.findAllBySentMailCondition(dto);

		request.setAttribute("sentmaillist", data);
		//ページャーヘルパークラス
		PagerViewHelper helper = new PagerViewHelper(dto);
		request.setAttribute("viewHelper", helper);

		return (mapping.findForward("continue"));
	}
}
