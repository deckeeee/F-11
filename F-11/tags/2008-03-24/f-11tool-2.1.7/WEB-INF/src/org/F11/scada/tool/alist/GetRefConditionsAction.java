/*
 * Created on 2003/10/10
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.alist;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
public class GetRefConditionsAction extends Action {

	/**
	 * 
	 */
	public GetRefConditionsAction() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
		if (!PermissionCheck.check("career", request))
			return (mapping.getInputForward());

		HttpSession session = request.getSession();
		RefConditionsForm refcond = (RefConditionsForm) session.getAttribute("refcond");
		RefConditionsForm refcondForm = (RefConditionsForm) form;
		refcondForm.setAll(refcond);

		Connection con = null;
		List selectList = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			AlarmListStore store = new AlarmListStore();
			selectList = store.getAttributeNameList(util);
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

		selectList.add(0, "");
		request.setAttribute("selectList", selectList);

		return (mapping.findForward("continue"));
	}

}
