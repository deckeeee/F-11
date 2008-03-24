package org.F11.scada.tool.point.name;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.login.PermissionCheck;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import org.seasar.dao.pager.PagerSupport;
import org.seasar.dao.pager.PagerViewHelper;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * @author hori
 */
public class GetNameListAction extends Action {
	/** ページャサポートクラス */
	private PagerSupport pager = new PagerSupport(
			50,
			PointNameCondition.class,
			"pointNameCondition");

	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (isCheck(request)) {
			return (mapping.getInputForward());
		}

		setRequest(request, form);

		return (mapping.findForward("continue"));
	}

	private boolean isCheck(HttpServletRequest request) {
		return !PermissionCheck.check("name", request)
				&& !PermissionCheck.check("ffugroupname", request);
	}

	private void setRequest(HttpServletRequest request, ActionForm form) {
		S2Container container = SingletonS2ContainerFactory.getContainer();
		PointNameService service = (PointNameService) container
				.getComponent(PointNameService.class);
		// パラメータoffsetを元にページャのoffset位置を更新
		pager.updateOffset(request);
		PointNameCondition condition = (PointNameCondition) pager
				.getPagerCondition(request);

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		String unit = (String) actionForm.get("searchunit");
		condition.setUnit(unit);
		String name = (String) actionForm.get("searchname");
		condition.setName(name);
		String unitMark = (String) actionForm.get("searchunit_mark");
		condition.setUnitMark(unitMark);
		System.out.println(actionForm);
		String attribute1 = (String) actionForm.get("searchattribute1");
		condition.setAttribute1(attribute1);
		String attribute2 = (String) actionForm.get("searchattribute2");
		condition.setAttribute2(attribute2);
		String attribute3 = (String) actionForm.get("searchattribute3");
		condition.setAttribute3(attribute3);

		List pointList = service.findAllByPointNameCondition(condition);
		// ページャーヘルパークラス
		PagerViewHelper helper = new PagerViewHelper(condition);
		request.setAttribute("pointList", pointList);
		request.setAttribute("viewHelper", helper);
	}

}
