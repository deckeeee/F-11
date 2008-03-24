/*
 * Created on 2003/08/25
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

/**
 * @author hori
 */
public class InputNewTrendSeriesAction extends Action {
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
		SeriesBean series = null;
		try {
			DOMPageDefine pageDefine = new DOMPageDefine(page.getPageXmlPath());
			TrendGraphDefine trendDefine = pageDefine.getTrendGraphDefine();
			series = new SeriesBean();
			series.setHandlerName((String) trendDefine.getHandlerList().get(0));
			series.setId(id);
			series.setSeriesName("");
			series.setSeriesSize(0);
			series.setPropertyList(trendDefine.getColorList());
		} catch (Exception e) {
			log.error("", e);
			errors.add(
				ActionErrors.GLOBAL_ERROR,
				new ActionError("error.page.notget"));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}

		session = request.getSession();
		session.setAttribute("trendSeries", series);
		session.setAttribute("actionName", "addTrendSeries");

		return (mapping.findForward("continue"));
	}
}
