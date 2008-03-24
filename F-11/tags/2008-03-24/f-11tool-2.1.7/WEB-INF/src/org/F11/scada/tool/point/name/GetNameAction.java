/*
 * Created on 2003/08/25
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.point.name;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.io.PointItemStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.util.ConnectionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

/**
 * @author hori
 */
public class GetNameAction extends Action {
	protected Log log = LogFactory.getLog(this.getClass());

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
		if (!PermissionCheck.check("name", request)
				&& !PermissionCheck.check("ffugroupname", request))
			return (mapping.getInputForward());

		String point = request.getParameter("point");
		point = ToolUtility.htmlEscape(point).trim();
		if (point.length() <= 0)
			return (mapping.getInputForward());

		Connection con = null;
		PointNameBean nameBean = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			PointItemStore store = new PointItemStore();
			nameBean = store.getPointName(util, Integer.parseInt(point));

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

		DynaValidatorForm actionForm = (DynaValidatorForm) form;
		actionForm.set("point", String.valueOf(nameBean.getPoint()));
		actionForm.set("unit", nameBean.getUnit());
		actionForm.set("name", nameBean.getName());
		actionForm.set("unit_mark", nameBean.getUnit_mark());
		actionForm.set("attribute1", nameBean.getAttribute1());
		actionForm.set("attribute2", nameBean.getAttribute2());
		actionForm.set("attribute3", nameBean.getAttribute3());

		log.debug("GetNameAction OK!");
		return (mapping.findForward("continue"));
	}
}
