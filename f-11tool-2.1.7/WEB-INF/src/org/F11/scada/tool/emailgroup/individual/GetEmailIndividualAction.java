/*
 * Created on 2003/08/25 To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.emailgroup.individual;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.ToolUtility;
import org.F11.scada.tool.io.EmailgroupIndividualStore;
import org.F11.scada.tool.io.EmailgroupMasterStore;
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
public class GetEmailIndividualAction extends Action {
	protected Log log = LogFactory.getLog(this.getClass());

	/*
	 * (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (!PermissionCheck.check("email_individual_setting", request))
			return (mapping.getInputForward());

		String provider = request.getParameter("provider");
		provider = ToolUtility.htmlEscape(provider.trim());
		String holder = request.getParameter("holder");
		holder = ToolUtility.htmlEscape(holder.trim());

		Connection con = null;
		List masterList = null;
		EmailIndividualBean bean = null;
		List assignList = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			EmailgroupMasterStore masterStore = new EmailgroupMasterStore();
			masterList = masterStore.getAllEmailgroupMaster(util);

			EmailgroupIndividualStore store = new EmailgroupIndividualStore();
			bean = store.getEmailgroupIndividual(util, provider, holder);
			
			assignList = store.getGroupIndividual(util, provider, holder);

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
		actionForm.set("masterList", masterList);

		actionForm.set("provider", bean.getProvider());
		actionForm.set("holder", bean.getHolder());
		actionForm.set("p_unit", bean.getP_unit());
		actionForm.set("p_name", bean.getP_name());
		actionForm.set("p_attrname", bean.getP_attrname());
		StringBuffer address = new StringBuffer();
		StringTokenizer st = new StringTokenizer(bean.getEmail_address(), ",");
		while (st.hasMoreTokens()) {
			String mailaddress = st.nextToken().trim();
			if (0 < address.length())
				address.append(", ");
			address.append(mailaddress);
		}
		String[] assignArray = new String[masterList.size()]; // masterList
		// ‚ÌƒTƒCƒY‚ð€”õ
		int i = 0;
		for (Iterator it = assignList.iterator(); it.hasNext(); i++) {
			assignArray[i] = (String) it.next();
		}
		actionForm.set("assignList", assignArray);
		actionForm.set("address", address.toString());

		log.debug("GetEmailIndividualAction OK!");
		return (mapping.findForward("continue"));
	}
}
