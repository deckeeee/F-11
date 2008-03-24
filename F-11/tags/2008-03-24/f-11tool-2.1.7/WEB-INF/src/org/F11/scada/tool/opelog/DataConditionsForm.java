/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.F11.scada.tool.opelog;

import org.apache.struts.validator.ValidatorForm;

public class DataConditionsForm extends ValidatorForm {
	private static final long serialVersionUID = 3088234355108067557L;
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
	private String opeUser;
	private String opeIp;
	private String opeName;
	private String opeMessage;

	/**
	 * @return etDay を戻します。
	 */
	public int getEtDay() {
		return etDay;
	}

	/**
	 * @param etDay etDay を設定。
	 */
	public void setEtDay(int etDay) {
		this.etDay = etDay;
	}

	/**
	 * @return etEneble を戻します。
	 */
	public boolean isEtEneble() {
		return etEneble;
	}

	/**
	 * @param etEneble etEneble を設定。
	 */
	public void setEtEneble(boolean etEneble) {
		this.etEneble = etEneble;
	}

	/**
	 * @return etHour を戻します。
	 */
	public int getEtHour() {
		return etHour;
	}

	/**
	 * @param etHour etHour を設定。
	 */
	public void setEtHour(int etHour) {
		this.etHour = etHour;
	}

	/**
	 * @return etMinute を戻します。
	 */
	public int getEtMinute() {
		return etMinute;
	}

	/**
	 * @param etMinute etMinute を設定。
	 */
	public void setEtMinute(int etMinute) {
		this.etMinute = etMinute;
	}

	/**
	 * @return etMonth を戻します。
	 */
	public int getEtMonth() {
		return etMonth;
	}

	/**
	 * @param etMonth etMonth を設定。
	 */
	public void setEtMonth(int etMonth) {
		this.etMonth = etMonth;
	}

	/**
	 * @return etSecond を戻します。
	 */
	public int getEtSecond() {
		return etSecond;
	}

	/**
	 * @param etSecond etSecond を設定。
	 */
	public void setEtSecond(int etSecond) {
		this.etSecond = etSecond;
	}

	/**
	 * @return etYear を戻します。
	 */
	public int getEtYear() {
		return etYear;
	}

	/**
	 * @param etYear etYear を設定。
	 */
	public void setEtYear(int etYear) {
		this.etYear = etYear;
	}

	/**
	 * @return stDay を戻します。
	 */
	public int getStDay() {
		return stDay;
	}

	/**
	 * @param stDay stDay を設定。
	 */
	public void setStDay(int stDay) {
		this.stDay = stDay;
	}

	/**
	 * @return stEneble を戻します。
	 */
	public boolean isStEneble() {
		return stEneble;
	}

	/**
	 * @param stEneble stEneble を設定。
	 */
	public void setStEneble(boolean stEneble) {
		this.stEneble = stEneble;
	}

	/**
	 * @return stHour を戻します。
	 */
	public int getStHour() {
		return stHour;
	}

	/**
	 * @param stHour stHour を設定。
	 */
	public void setStHour(int stHour) {
		this.stHour = stHour;
	}

	/**
	 * @return stMinute を戻します。
	 */
	public int getStMinute() {
		return stMinute;
	}

	/**
	 * @param stMinute stMinute を設定。
	 */
	public void setStMinute(int stMinute) {
		this.stMinute = stMinute;
	}

	/**
	 * @return stMonth を戻します。
	 */
	public int getStMonth() {
		return stMonth;
	}

	/**
	 * @param stMonth stMonth を設定。
	 */
	public void setStMonth(int stMonth) {
		this.stMonth = stMonth;
	}

	/**
	 * @return stSecond を戻します。
	 */
	public int getStSecond() {
		return stSecond;
	}

	/**
	 * @param stSecond stSecond を設定。
	 */
	public void setStSecond(int stSecond) {
		this.stSecond = stSecond;
	}

	/**
	 * @return stYear を戻します。
	 */
	public int getStYear() {
		return stYear;
	}

	/**
	 * @param stYear stYear を設定。
	 */
	public void setStYear(int stYear) {
		this.stYear = stYear;
	}

	public String getOpeIp() {
		return opeIp;
	}

	public void setOpeIp(String opeIp) {
		this.opeIp = opeIp;
	}

	public String getOpeMessage() {
		return opeMessage;
	}

	public void setOpeMessage(String opeMessage) {
		this.opeMessage = opeMessage;
	}

	public String getOpeName() {
		return opeName;
	}

	public void setOpeName(String opeName) {
		this.opeName = opeName;
	}

	public String getOpeUser() {
		return opeUser;
	}

	public void setOpeUser(String opeUser) {
		this.opeUser = opeUser;
	}
}
