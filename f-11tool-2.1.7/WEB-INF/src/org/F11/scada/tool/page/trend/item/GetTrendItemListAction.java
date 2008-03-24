/*
 * Created on 2003/08/25
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.page.trend.item;

import java.rmi.Naming;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.frame.editor.FrameEditHandler;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.tool.page.parser.trend.SeriesBean;
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
public class GetTrendItemListAction extends Action {
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

		String strPos = request.getParameter("pos");
		if (strPos == null || strPos.length() <= 0) {
			return (mapping.getInputForward());
		}
		int pos = Integer.parseInt(strPos);

		HttpSession session = request.getSession();
		SeriesBean series = (SeriesBean) session.getAttribute("trendSeries");

		ActionErrors errors = new ActionErrors();
		try {
			FrameEditHandler handler =
				(FrameEditHandler) Naming.lookup(
					WifeUtilities.createRmiFrameEditManager());
			List itemList = handler.getLoggingHolders(series.getHandlerName());

			request.setAttribute("loggItemList", itemList);
			request.setAttribute("pos", new Integer(pos));
		} catch (Exception e) {
			errors.add(
				ActionErrors.GLOBAL_ERROR,
				new ActionError("error.page.item.notget"));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}

		return (mapping.findForward("continue"));
	}
}
