/*
 * Created on 2003/10/14
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.alist.summary;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
public class DownloadCSVAction extends Action {

	/**
	 * 
	 */
	public DownloadCSVAction() {
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

		Connection con = null;
		List summaryList = null;
		DataOutputStream out = null;
		OutputStreamWriter osw = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			AlarmListStore store = new AlarmListStore();
			summaryList = store.getAllSummaryList(util, refcond);
			con.close();
			con = null;

			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=\"summary.csv\"");

			out = new DataOutputStream(response.getOutputStream());
			osw = new OutputStreamWriter(out, "Shift_JIS");
			for (Iterator it = summaryList.iterator(); it.hasNext();) {
				SummaryRecordBean career = (SummaryRecordBean) it.next();
				StringBuffer sb = new StringBuffer();
				sb.append("\"").append(career.getAlarm_color()).append("\",");
				sb.append(career.getOndate()).append(",");
				sb.append(career.getOffdate()).append(",\"");
				sb.append(career.getUnit()).append("\",\"");
				sb.append(career.getP_name()).append("\",\"");
				sb.append(career.getAtt_name()).append("\",\"");
				sb.append(career.getMessage()).append("\"\r\n");
				osw.write(sb.toString());
			}
			osw.close();
			osw = null;
			out.close();
			out = null;

			return null;

		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					osw = null;
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					out = null;
				}
			}
		}
	}

}
