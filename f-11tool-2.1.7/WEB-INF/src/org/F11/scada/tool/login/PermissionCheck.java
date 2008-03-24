/*
 * Created on 2003/08/20
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * @author hori
 */
public class PermissionCheck {
	public static boolean check(String key, HttpServletRequest request) {
		HttpSession session = request.getSession();
		UserPermission perm = (UserPermission) session.getAttribute("user");
		if (perm == null || !perm.hasPermission(key))
			return false;
		return true;
	}
	
	public static boolean checkLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("user") == null)
			return false;
		return true;
	}
}
