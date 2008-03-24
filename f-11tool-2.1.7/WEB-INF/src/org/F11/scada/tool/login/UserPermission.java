/*
 * Created on 2003/08/18
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.login;

import java.util.HashSet;
import java.util.Set;

/**
 * @author hori
 */
public class UserPermission {
	private String name;
	private Set permission = new HashSet();

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	public void addPermission(String key) {
		permission.add(key);
	}

	public boolean hasPermission(String key) {
		if (permission.contains("admin"))
			return true;
		return permission.contains(key);
	}
}
