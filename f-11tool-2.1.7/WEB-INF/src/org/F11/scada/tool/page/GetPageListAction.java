/*
 * Created on 2003/08/21
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.F11.scada.tool.io.PageListBean;
import org.F11.scada.tool.io.PageListDAO;
import org.F11.scada.tool.login.PermissionCheck;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author hori
 */
public class GetPageListAction extends Action {

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

		//データベースで一覧を定義しそれを持ってくる
		Collection col = null;
		PageListDAO dao = null;
		try {
			dao = new PageListDAO();
			col = dao.getPageList();
			if (col == Collections.EMPTY_LIST) {
				PageListBean pb = new PageListBean();
				pb.setPageName("TRND0001");
				pb.setPageXmlPath("TRND0001");
				col = new ArrayList();
				col.add(pb);
			}
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
/*
		List pageList = new ArrayList();
		pageList.add("TRND0001");
		request.setAttribute("pageList", pageList);
*/
		HttpSession session = request.getSession();
		session.removeAttribute("page");
		
		request.setAttribute("pageList", col);

		return (mapping.findForward("continue"));
	}

}
