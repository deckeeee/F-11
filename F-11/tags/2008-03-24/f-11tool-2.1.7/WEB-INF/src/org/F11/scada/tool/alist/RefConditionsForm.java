/*
 * Created on 2003/10/09
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.alist;

import java.sql.Timestamp;
import java.util.Calendar;

import org.F11.scada.tool.ToolUtility;
import org.apache.struts.validator.ValidatorForm;

/**
 * @author hori
 */
public class RefConditionsForm extends ValidatorForm {
	private boolean stEneble;
	private int stYear;
	private int stMonth;
	private int stDay;
	private int stHour;
	private int stMinute;
	private int stSecond;
	private boolean etEneble;
	private int etYear;
	private int etMonth;
	private int etDay;
	private int etHour;
	private int etMinute;
	private int etSecond;
	private String findString;
	private String msgString;
	private String bitval;
	private String histrycheck;

	private long limit;

	/**
	 * 
	 */
	public RefConditionsForm() {
		super();
		stEneble = false;
		etEneble = false;
		findString = "";
		msgString = "";
		bitval = "all";
		histrycheck = "all";
		limit = 50;
	}

	public void setAll(RefConditionsForm src) {
		stEneble = src.stEneble;
		stYear = src.stYear;
		stMonth = src.stMonth;
		stDay = src.stDay;
		stHour = src.stHour;
		stMinute = src.stMinute;
		stSecond = src.stSecond;
		etEneble = src.etEneble;
		etYear = src.etYear;
		etMonth = src.etMonth;
		etDay = src.etDay;
		etHour = src.etHour;
		etMinute = src.etMinute;
		etSecond = src.etSecond;
		findString = src.findString;
		msgString = src.msgString;
		bitval = src.bitval;
		histrycheck = src.histrycheck;
		limit = src.limit;
	}

	public void htmlEscape() {
		findString = ToolUtility.htmlEscape(findString);
		msgString = ToolUtility.htmlEscape(msgString);
		bitval = ToolUtility.htmlEscape(bitval);
		histrycheck = ToolUtility.htmlEscape(histrycheck);
	}

	public Timestamp getStTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(stYear, stMonth - 1, stDay, stHour, stMinute, stSecond);
		return new Timestamp(cal.getTimeInMillis());
	}

	public Timestamp getEtTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(etYear, etMonth - 1, etDay, etHour, etMinute, etSecond);
		return new Timestamp(cal.getTimeInMillis());
	}

	public void setStTime(Timestamp tm) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(tm.getTime());
		stYear = cal.get(Calendar.YEAR);
		stMonth = cal.get(Calendar.MONTH) + 1;
		stDay = cal.get(Calendar.DAY_OF_MONTH);
		stHour = cal.get(Calendar.HOUR_OF_DAY);
		stMinute = cal.get(Calendar.MINUTE);
		stSecond = cal.get(Calendar.SECOND);
	}

	public void setEtTime(Timestamp tm) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(tm.getTime());
		etYear = cal.get(Calendar.YEAR);
		etMonth = cal.get(Calendar.MONTH) + 1;
		etDay = cal.get(Calendar.DAY_OF_MONTH);
		etHour = cal.get(Calendar.HOUR_OF_DAY);
		etMinute = cal.get(Calendar.MINUTE);
		etSecond = cal.get(Calendar.SECOND);
	}

	/**
	 * @return
	 */
	public String getBitval() {
		return bitval;
	}

	/**
	 * @return
	 */
	public int getEtDay() {
		return etDay;
	}

	/**
	 * @return
	 */
	public boolean getEtEneble() {
		return etEneble;
	}

	/**
	 * @return
	 */
	public int getEtHour() {
		return etHour;
	}

	/**
	 * @return
	 */
	public int getEtMinute() {
		return etMinute;
	}

	/**
	 * @return
	 */
	public int getEtMonth() {
		return etMonth;
	}

	/**
	 * @return
	 */
	public int getEtSecond() {
		return etSecond;
	}

	/**
	 * @return
	 */
	public int getEtYear() {
		return etYear;
	}

	/**
	 * @return
	 */
	public String getFindString() {
		return findString;
	}

	/**
	 * @return
	 */
	public String getMsgString() {
		return msgString;
	}

	/**
	 * @return
	 */
	public int getStDay() {
		return stDay;
	}

	/**
	 * @return
	 */
	public boolean getStEneble() {
		return stEneble;
	}

	/**
	 * @return
	 */
	public int getStHour() {
		return stHour;
	}

	/**
	 * @return
	 */
	public int getStMinute() {
		return stMinute;
	}

	/**
	 * @return
	 */
	public int getStMonth() {
		return stMonth;
	}

	/**
	 * @return
	 */
	public int getStSecond() {
		return stSecond;
	}

	/**
	 * @return
	 */
	public int getStYear() {
		return stYear;
	}

	/**
	 * @param string
	 */
	public void setBitval(String string) {
		bitval = string;
	}

	/**
	 * @param i
	 */
	public void setEtDay(int i) {
		etDay = i;
	}

	/**
	 * @param b
	 */
	public void setEtEneble(boolean b) {
		etEneble = b;
	}

	/**
	 * @param i
	 */
	public void setEtHour(int i) {
		etHour = i;
	}

	/**
	 * @param i
	 */
	public void setEtMinute(int i) {
		etMinute = i;
	}

	/**
	 * @param i
	 */
	public void setEtMonth(int i) {
		etMonth = i;
	}

	/**
	 * @param i
	 */
	public void setEtSecond(int i) {
		etSecond = i;
	}

	/**
	 * @param i
	 */
	public void setEtYear(int i) {
		etYear = i;
	}

	/**
	 * @param string
	 */
	public void setFindString(String string) {
		findString = string;
	}

	/**
	 * @param string
	 */
	public void setMsgString(String string) {
		msgString = string;
	}

	/**
	 * @param i
	 */
	public void setStDay(int i) {
		stDay = i;
	}

	/**
	 * @param b
	 */
	public void setStEneble(boolean b) {
		stEneble = b;
	}

	/**
	 * @param i
	 */
	public void setStHour(int i) {
		stHour = i;
	}

	/**
	 * @param i
	 */
	public void setStMinute(int i) {
		stMinute = i;
	}

	/**
	 * @param i
	 */
	public void setStMonth(int i) {
		stMonth = i;
	}

	/**
	 * @param i
	 */
	public void setStSecond(int i) {
		stSecond = i;
	}

	/**
	 * @param i
	 */
	public void setStYear(int i) {
		stYear = i;
	}

	/**
	 * @return
	 */
	public long getLimit() {
		return limit;
	}

	/**
	 * @param l
	 */
	public void setLimit(long l) {
		limit = l;
	}

	/**
	 * @return
	 */
	public String getHistrycheck() {
		return histrycheck;
	}

	/**
	 * @param string
	 */
	public void setHistrycheck(String string) {
		histrycheck = string;
	}

}
