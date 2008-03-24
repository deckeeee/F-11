/*
 * �쐬��: 2005/09/21 TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java -
 * �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
package org.F11.scada.tool.emailgroup.master;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.F11.scada.tool.io.EmailgroupMasterStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.util.ConnectionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

/**
 * @author hori TODO ���̐������ꂽ�^�R�����g�̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java -
 *         �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
public class NewEmailMasterAction extends Action {
	protected Log log = LogFactory.getLog(this.getClass());

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Connection con = null;
		Integer group_id = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);

			EmailgroupMasterStore store = new EmailgroupMasterStore();
			group_id = store.getNextGroupID(util);

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
		actionForm.set("group_id", group_id);
		return (mapping.findForward("continue"));
	}
}
