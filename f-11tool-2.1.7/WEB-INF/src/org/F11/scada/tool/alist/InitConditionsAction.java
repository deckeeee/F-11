/*
 * Created on 2003/10/09
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.alist;

import java.sql.Timestamp;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.tool.login.PermissionCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author hori
 */
public class InitConditionsAction extends Action {
	private final Log log = LogFactory.getLog(InitConditionsAction.class);
	private final ClientConfiguration configuration = new ClientConfiguration();

	public InitConditionsAction() {
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

		// Save our logged-in user in the session
		HttpSession session = request.getSession();
		session.removeAttribute("refcond");

		RefConditionsForm refcond = new RefConditionsForm();
		refcond.setStEneble(true);
		refcond.setEtEneble(true);
		long now = System.currentTimeMillis();
		refcond.setStTime(getStartTime(now));
		refcond.setEtTime(new Timestamp(now));
		session.setAttribute("refcond", refcond);

		return (mapping.findForward("continue"));
	}

	private Timestamp getStartTime(long now) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(now);
		calendar.add(Calendar.DAY_OF_MONTH, configuration.getInt(
				"xwife.applet.Applet.alarm.table.search",
				1)
				* -1);
		return new Timestamp(calendar.getTimeInMillis());
	}

}
