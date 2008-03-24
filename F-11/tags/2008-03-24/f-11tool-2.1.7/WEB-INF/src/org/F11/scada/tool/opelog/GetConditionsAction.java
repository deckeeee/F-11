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
package org.F11.scada.tool.opelog;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.login.PermissionCheck;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Administrator
 *
 * TODO この生成された型コメントのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
public class GetConditionsAction extends Action {
    public GetConditionsAction() {
        super();
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!PermissionCheck.check("opelog", request))
			return (mapping.getInputForward());

		DataConditionsForm dataCondForm = (DataConditionsForm) form;
		if (dataCondForm.getStYear() == 0) {
		    Calendar cal = Calendar.getInstance();
		    cal.add(Calendar.DAY_OF_MONTH, -1);
		    dataCondForm.setStYear(cal.get(Calendar.YEAR));
		    dataCondForm.setStMonth(cal.get(Calendar.MONTH) + 1);
		    dataCondForm.setStDay(cal.get(Calendar.DAY_OF_MONTH));
		    dataCondForm.setStEneble(true);
		}
		if (dataCondForm.getEtYear() == 0) {
		    Calendar cal = Calendar.getInstance();
		    dataCondForm.setEtYear(cal.get(Calendar.YEAR));
		    dataCondForm.setEtMonth(cal.get(Calendar.MONTH) + 1);
		    dataCondForm.setEtDay(cal.get(Calendar.DAY_OF_MONTH) + 1);
		    dataCondForm.setEtEneble(true);
		}

		return (mapping.findForward("continue"));
    }
}
