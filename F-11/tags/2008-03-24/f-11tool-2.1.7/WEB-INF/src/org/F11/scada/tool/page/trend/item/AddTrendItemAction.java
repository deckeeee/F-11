/*
 * Created on 2003/08/25
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.page.trend.item;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.tool.page.parser.trend.SeriesBean;
import org.F11.scada.tool.page.trend.PropertyForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author hori
 */
public class AddTrendItemAction extends Action {
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

		PropertyForm actionForm = (PropertyForm) form;
		log.debug("select:" + actionForm.getPoint());

		HttpSession session = request.getSession();
		SeriesBean series = (SeriesBean) session.getAttribute("trendSeries");
		List propertyList = series.getPropertyList();

		int pos = actionForm.getPos();
		if (0 <= pos
			&& pos < propertyList.size()
			&& series.getSeriesSize() < propertyList.size()) {
			propertyList.add(pos, actionForm);
			for (int i = pos; i < propertyList.size() - 1; i++) {
				PropertyForm fromBean = (PropertyForm) propertyList.get(i + 1);
				PropertyForm toBean = (PropertyForm) propertyList.get(i);
				toBean.setColor(fromBean.getColor());
			}
			propertyList.remove(propertyList.size() - 1);
			series.setSeriesSize(series.getSeriesSize() + 1);
		}
		return (mapping.findForward("continue"));
	}
}