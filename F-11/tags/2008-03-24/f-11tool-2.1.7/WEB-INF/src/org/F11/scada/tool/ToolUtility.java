/*
 * Created on 2003/10/15
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.frame.FramePageEditTimeSupport;
import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

/**
 * @author hori
 */
public class ToolUtility implements ViewTool {

	protected HttpServletRequest request;
	protected HttpSession session;
	/**
	 * 
	 */
	public ToolUtility() {
		super();
	}

	public void init(Object obj) {
		ViewContext context = (ViewContext)obj;
		this.request = context.getRequest();
		this.session = request.getSession(false);
	}

	/**
	 * HTML�̐��䕶�����G�X�P�[�v���܂��B
	 * @param string �ϊ���������
	 * @return �ϊ����ʕ�����
	 */
	public static String htmlEscape(String string) {
		String ret = string;
		if (string != null) {
			ret = ret.replaceAll("&", "&amp;");
			ret = ret.replaceAll("<", "&lt;");
			ret = ret.replaceAll(">", "&gt;");
			ret = ret.replaceAll("\"", "&quot;");
			ret = ret.replaceAll("'", "&#39;");
		}
		return ret;
	}

	/**
	 * FramePageEditTimeSupport �̃����[�g�Q�Ƃ�Ԃ��܂��B
	 * 
	 * @return FramePageEditTimeSupport �̃����[�g�Q��
	 */
	public static FramePageEditTimeSupport getFramePageEditTimeSupport() throws MalformedURLException, RemoteException, NotBoundException {
		return (FramePageEditTimeSupport) Naming.lookup(
			WifeUtilities.createRmiFrameDefineManager());
	}


	/**
	 * ManagerDelegator �� FramePageEditTimeSupport �̃����[�g�Q�Ƃ�Ԃ��܂��B
	 * 
	 * @return FramePageEditTimeSupport �̃����[�g�Q��
	 */
	public static FramePageEditTimeSupport getManagerDelegator() throws MalformedURLException, RemoteException, NotBoundException {
		return (FramePageEditTimeSupport) Naming.lookup(
			WifeUtilities.createRmiManagerDelegator());
	}
	
	public static String format(Date date) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return fmt.format(date);
	}
}
