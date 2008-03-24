/*
 * Created on 2003/08/28
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.maintenance;

/**
 * @author hori
 */
public class MainteItemBean {
	private String key;
	private String name;
	private boolean assign;

	public MainteItemBean(String key, String name, boolean assign) {
		this.key = key;
		this.name = name;
		this.assign = assign;
	}

	public MainteItemBean(String key, String name) {
		this(key, name, false);
	}

	/**
	 * @return
	 */
	public boolean getAssign() {
		return assign;
	}

	/**
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param b
	 */
	public void setAssign(boolean b) {
		assign = b;
	}

}
