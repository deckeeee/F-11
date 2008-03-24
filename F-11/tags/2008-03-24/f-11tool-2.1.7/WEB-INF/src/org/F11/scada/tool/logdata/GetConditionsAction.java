/*
 * 作成日: 2005/10/26
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
package org.F11.scada.tool.logdata;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.io.LoggingDataStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.login.PermissionCheck;
import org.F11.scada.util.ConnectionUtil;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Administrator
 *
 * TODO この生成された型コメントのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
public class GetConditionsAction extends Action {

    /**
     * 
     */
    public GetConditionsAction() {
        super();
    }

    /* (非 Javadoc)
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!PermissionCheck.check("logdata", request))
			return (mapping.getInputForward());

		DataConditionsForm dataCondForm = (DataConditionsForm) form;
		if (dataCondForm.getStYear() == 0) {
		    Calendar cal = Calendar.getInstance();
		    cal.add(Calendar.DAY_OF_MONTH, -1);
		    dataCondForm.setStYear(cal.get(Calendar.YEAR));
		    dataCondForm.setStMonth(cal.get(Calendar.MONTH) + 1);
		    dataCondForm.setStDay(cal.get(Calendar.DAY_OF_MONTH));
		    dataCondForm.setStEneble(true);
		}
		if (dataCondForm.getEtYear() == 0) {
		    Calendar cal = Calendar.getInstance();
		    dataCondForm.setEtYear(cal.get(Calendar.YEAR));
		    dataCondForm.setEtMonth(cal.get(Calendar.MONTH) + 1);
		    dataCondForm.setEtDay(cal.get(Calendar.DAY_OF_MONTH));
		    dataCondForm.setEtEneble(true);
		}
		if (dataCondForm.getHeadString() == null) {
		    dataCondForm.setHeadString("name");
		}

		Connection con = null;
		List tableList = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			LoggingDataStore store = new LoggingDataStore();
			tableList = store.getLoggtableNameList(util);
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

		request.setAttribute("tableList", tableList);

		return (mapping.findForward("continue"));
    }
}
