/*
 * Created on 2003/10/09
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.alist.career;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.alist.RefConditionsForm;
import org.F11.scada.tool.io.AlarmListStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.util.ConnectionUtil;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author hori
 */
public class QueryListAction extends Action {

	/**
	 * 
	 */
	public QueryListAction() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (!PermissionCheck.check("career", request))
			return (mapping.getInputForward());

		long currNo = 0;
		try {
			// cはページの最初のレコード
			String curr = ToolUtility.htmlEscape(request.getParameter("c"));
			currNo = Long.parseLong(curr);
			if (currNo < 0) {
				currNo = 0;
			}
		} catch (Exception e) {
		}

		HttpSession session = request.getSession();
		RefConditionsForm refcond = (RefConditionsForm) session
				.getAttribute("refcond");

		Connection con = null;
		List careerList = null;
		Long allCount = new Long(0);
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			AlarmListStore store = new AlarmListStore();
			allCount = new Long(store.getAllCareerCount(util, refcond));
			careerList = store.getCareerList(util, refcond, currNo);
			con.close();
			con = null;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}

		request.setAttribute("careerList", careerList);
		request.setAttribute("allCount", allCount);
		if (0 < allCount.longValue()) {
			request.setAttribute("startPos", String.valueOf(currNo + 1));
			request.setAttribute("endPos", String.valueOf(currNo
					+ careerList.size()));
		}
		if (0 < currNo)
			request.setAttribute("forward", String.valueOf(currNo
					- refcond.getLimit()));
		if (currNo + careerList.size() < allCount.longValue())
			request.setAttribute("next", String.valueOf(currNo
					+ careerList.size()));

		return (mapping.findForward("continue"));
	}

}
