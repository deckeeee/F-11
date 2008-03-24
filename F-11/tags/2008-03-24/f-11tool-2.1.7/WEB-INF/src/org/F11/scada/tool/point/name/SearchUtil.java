package org.F11.scada.tool.point.name;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.F11.scada.tool.ToolUtility;
import org.apache.struts.action.ActionForm;
import org.apache.struts.validator.DynaValidatorForm;

public abstract class SearchUtil {
	static void resetSearchField(HttpServletRequest request, ActionForm form) {
		HttpSession session = request.getSession();
		PointNameCondition condition = (PointNameCondition) session
				.getAttribute("pointNameCondition");
		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		actionForm.set("searchunit", condition.getSearchUnit());
		actionForm.set("searchname", condition.getSearchName());
		actionForm.set("searchunit_mark", condition.getSearchUnitMark());
		actionForm.set("searchattribute1", condition.getSearchAttribute1());
		actionForm.set("searchattribute2", condition.getSearchAttribute2());
		actionForm.set("searchattribute3", condition.getSearchAttribute3());
	}

	static PointNameBean getPointNameBean(DynaValidatorForm actionForm) {
		String point = (String) actionForm.get("point");
		String unit = (String) actionForm.get("unit");
		String name = (String) actionForm.get("name");
		String unit_mark = (String) actionForm.get("unit_mark");
		String attribute1 = (String) actionForm.get("attribute1");
		String attribute2 = (String) actionForm.get("attribute2");
		String attribute3 = (String) actionForm.get("attribute3");

		PointNameBean nameBean = new PointNameBean();
		nameBean.setPoint(Integer.parseInt(ToolUtility.htmlEscape(point)));
		nameBean.setUnit(ToolUtility.htmlEscape(unit));
		nameBean.setName(ToolUtility.htmlEscape(name));
		nameBean.setUnit_mark(ToolUtility.htmlEscape(unit_mark));
		nameBean.setAttribute1(ToolUtility.htmlEscape(attribute1));
		nameBean.setAttribute2(ToolUtility.htmlEscape(attribute2));
		nameBean.setAttribute3(ToolUtility.htmlEscape(attribute3));
		return nameBean;
	}

}
