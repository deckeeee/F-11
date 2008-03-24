/*
 * Created on 2003/08/29
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.point.email;

/**
 * @author hori
 */
public class PointEmailBean {
	private int group_id;
	private String address;

	/**
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return
	 */
	public int getGroup_id() {
		return group_id;
	}

	/**
	 * @param string
	 */
	public void setAddress(String string) {
		address = string;
	}

	/**
	 * @param i
	 */
	public void setGroup_id(int i) {
		group_id = i;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("PointEmailBean group_id[").append(group_id);
		sb.append("] address[").append(address).append("]");
		return sb.toString();
	}
}
