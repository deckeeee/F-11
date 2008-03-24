/*
 * Created on 2003/08/22
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.group;

import org.F11.scada.tool.ToolUtility;

/**
 * @author hori
 */
public class UserAssignBean {
	private String userName = "";
	private String userAssign = "false";
	
	public void htmlEscape() {
		userAssign = ToolUtility.htmlEscape(userAssign);
		userName = ToolUtility.htmlEscape(userName);
	}
	
	/**
	 * @return
	 */
	public String getUserAssign() {
		return userAssign;
	}

	/**
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param string
	 */
	public void setUserAssign(String string) {
		userAssign = string;
	}

	/**
	 * @param string
	 */
	public void setUserName(String string) {
		userName = string;
	}

}
