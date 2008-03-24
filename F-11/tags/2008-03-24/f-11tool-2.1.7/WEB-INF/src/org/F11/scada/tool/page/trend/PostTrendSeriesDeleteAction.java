/*
 * Created on 2003/08/29
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.page.trend;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.F11.scada.tool.io.PageListBean;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.tool.page.parser.DOMPageDefine;
import org.F11.scada.tool.page.parser.trend.SeriesBean;
import org.F11.scada.tool.page.parser.trend.TrendGraphDefine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

/**
 * @author hori
 */
public class PostTrendSeriesDeleteAction extends Action {
	protected Log log = LogFactory.getLog(this.getClass());

	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

	    if (!PermissionCheck.check("page", request))
			return (mapping.getInputForward());

		String strId = request.getParameter("id");
		if (strId == null || strId.length() <= 0) {
			return (mapping.getInputForward());
		}
		int id = Integer.parseInt(strId);

		HttpSession session = request.getSession();
		PageListBean page = (PageListBean) session.getAttribute("page");

		ActionErrors errors = new ActionErrors();
		try {
			DOMPageDefine pageDefine = new DOMPageDefine(page.getPageXmlPath());
			TrendGraphDefine trendDefine = pageDefine.getTrendGraphDefine();
			SeriesBean series = trendDefine.getSeriesHead(id);
			request.setAttribute("name", series.getSeriesName());
		} catch (Exception e) {
			log.error("", e);
		    e.printStackTrace();
			errors.add(
				ActionErrors.GLOBAL_ERROR,
				new ActionError("error.page.notget"));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		actionForm.set("key", String.valueOf(id));

		return (mapping.findForward("continue"));
	}

}
