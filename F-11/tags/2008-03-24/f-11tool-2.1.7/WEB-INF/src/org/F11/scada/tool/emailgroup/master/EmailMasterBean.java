/*
 * Created on 2003/08/29 To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.emailgroup.master;

/**
 * @author hori
 */
public class EmailMasterBean {
	private Integer group_id;
	private String group_name;
	private String address;

	/**
	 * @return
	 */
	public String getAddress() {
		if (address == null)
			address = "";
		return address;
	}

	/**
	 * @return
	 */
	public Integer getGroup_id() {
		if (group_id == null)
			group_id = new Integer(0);
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
	public void setGroup_id(Integer i) {
		group_id = i;
	}

	public String getGroup_name() {
		if (group_name == null)
			group_name = "";
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("EmailMasterBean group_id[").append(group_id);
		sb.append("] address[").append(address).append("]");
		return sb.toString();
	}
}
