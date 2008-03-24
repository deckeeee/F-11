/*
 * Created on 2003/10/09
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.alist.career;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author hori
 */
public class CareerRecordBean {

	private String alarm_color;
	private Timestamp entrydate;
	private String unit;
	private String p_name;
	private String att_name;
	private String message;

	/**
	 * 
	 */
	public CareerRecordBean(
		String alarm_color,
		Timestamp entrydate,
		String unit,
		String p_name,
		String att_name,
		String message) {
		this.alarm_color = alarm_color;
		this.entrydate = entrydate;
		this.unit = unit;
		this.p_name = p_name;
		this.att_name = att_name;
		this.message = message;
	}
	public CareerRecordBean() {
	}

	/**
	 * @return
	 */
	public String getAlarm_color() {
		return alarm_color;
	}

	/**
	 * @return
	 */
	public String getAtt_name() {
		return att_name;
	}

	/**
	 * @return
	 */
	public String getEntrydate() {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return df.format(entrydate);
	}
	public Timestamp getEntrydateTime() {
		return entrydate;
	}

	/**
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return
	 */
	public String getP_name() {
		return p_name;
	}

	/**
	 * @return
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param string
	 */
	public void setAlarm_color(String string) {
		alarm_color = string;
	}

	/**
	 * @param string
	 */
	public void setAtt_name(String string) {
		att_name = string;
	}

	/**
	 * @param string
	 */
	public void setEntrydate(Timestamp time) {
		entrydate = time;
	}

	/**
	 * @param string
	 */
	public void setMessage(String string) {
		message = string;
	}

	/**
	 * @param string
	 */
	public void setP_name(String string) {
		p_name = string;
	}

	/**
	 * @param string
	 */
	public void setUnit(String string) {
		unit = string;
	}

}
