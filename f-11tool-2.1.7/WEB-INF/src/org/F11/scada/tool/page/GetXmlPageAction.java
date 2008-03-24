/*
 * Created on 2003/08/25
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.F11.scada.tool.io.PageListBean;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.tool.page.parser.DOMPageDefine;
import org.F11.scada.tool.page.parser.bar.BarGraphDefine;
import org.F11.scada.tool.page.parser.trend.TrendGraphDefine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author hori
 */
public class GetXmlPageAction extends Action {
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

		HttpSession session = request.getSession();
		PageListBean page = (PageListBean) session.getAttribute("page");
		session.removeAttribute("actionName");
		session.removeAttribute("trendSeries");

		ActionErrors errors = new ActionErrors();

		DOMPageDefine pageDefine = null;
		try {
			pageDefine = new DOMPageDefine(page.getPageXmlPath());
		} catch (Exception e) {
			e.printStackTrace();
			errors.add(
				ActionErrors.GLOBAL_ERROR,
				new ActionError("error.page.notget"));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}
		TrendGraphDefine trendDefine = pageDefine.getTrendGraphDefine();
		BarGraphDefine barDefine = pageDefine.getBarGraphDefine();
		if (trendDefine != null) {
			request.setAttribute(
				"trendSeriesList",
				trendDefine.getSeriesNameList());
			log.debug("GetXmlPageAction OK! TREND : " + page.getPageXmlPath());
			return (mapping.findForward("trend"));
		} else if (barDefine != null) {
			request.setAttribute("barPointList", barDefine.getBarGroup());
			log.debug("GetXmlPageAction OK! BAR : " + page.getPageXmlPath());
			return (mapping.findForward("bar"));
		}

		errors.add(
			ActionErrors.GLOBAL_ERROR,
			new ActionError("error.page.notedit"));
		saveErrors(request, errors);

		return (mapping.getInputForward());
	}
}
