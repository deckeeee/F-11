/*
 * Created on 2003/10/09
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.alist.summary;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author hori
 */
public class SummaryRecordBean {
	private static final Timestamp epoch = new Timestamp(0);
	private static final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private String alarm_color;
	private Timestamp ondate;
	private Timestamp offdate;
	private String unit;
	private String p_name;
	private String att_name;
	private String message;

	/**
	 * 
	 */
	public SummaryRecordBean(
		String alarm_color,
		Timestamp ondate,
		Timestamp offdate,
		String unit,
		String p_name,
		String att_name,
		String message) {
		this.alarm_color = alarm_color;
		this.ondate = ondate;
		this.offdate = offdate;
		this.unit = unit;
		this.p_name = p_name;
		this.att_name = att_name;
		this.message = message;
	}
	public SummaryRecordBean() {
		this.ondate = epoch;
		this.offdate = epoch;
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

	/**
	 * @return
	 */
	public String getOffdate() {
		if (epoch.equals(offdate))
			return "";
		return df.format(offdate);
	}
	public Timestamp getOffdateTime() {
		return offdate;
	}

	/**
	 * @return
	 */
	public String getOndate() {
		if (epoch.equals(ondate))
			return "";
		return df.format(ondate);
	}
	public Timestamp getOndateTime() {
		return ondate;
	}

	/**
	 * @param timestamp
	 */
	public void setOffdate(Timestamp timestamp) {
		if (timestamp == null) {
			offdate = epoch;
		} else {
			offdate = timestamp;
		}
	}

	/**
	 * @param timestamp
	 */
	public void setOndate(Timestamp timestamp) {
		if (timestamp == null) {
			ondate = epoch;
		} else {
			ondate = timestamp;
		}
	}

}
