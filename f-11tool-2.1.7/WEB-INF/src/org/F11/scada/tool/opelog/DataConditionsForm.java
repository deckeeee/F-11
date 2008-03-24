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
	 * @return etDay ��߂��܂��B
	 */
	public int getEtDay() {
		return etDay;
	}

	/**
	 * @param etDay etDay ��ݒ�B
	 */
	public void setEtDay(int etDay) {
		this.etDay = etDay;
	}

	/**
	 * @return etEneble ��߂��܂��B
	 */
	public boolean isEtEneble() {
		return etEneble;
	}

	/**
	 * @param etEneble etEneble ��ݒ�B
	 */
	public void setEtEneble(boolean etEneble) {
		this.etEneble = etEneble;
	}

	/**
	 * @return etHour ��߂��܂��B
	 */
	public int getEtHour() {
		return etHour;
	}

	/**
	 * @param etHour etHour ��ݒ�B
	 */
	public void setEtHour(int etHour) {
		this.etHour = etHour;
	}

	/**
	 * @return etMinute ��߂��܂��B
	 */
	public int getEtMinute() {
		return etMinute;
	}

	/**
	 * @param etMinute etMinute ��ݒ�B
	 */
	public void setEtMinute(int etMinute) {
		this.etMinute = etMinute;
	}

	/**
	 * @return etMonth ��߂��܂��B
	 */
	public int getEtMonth() {
		return etMonth;
	}

	/**
	 * @param etMonth etMonth ��ݒ�B
	 */
	public void setEtMonth(int etMonth) {
		this.etMonth = etMonth;
	}

	/**
	 * @return etSecond ��߂��܂��B
	 */
	public int getEtSecond() {
		return etSecond;
	}

	/**
	 * @param etSecond etSecond ��ݒ�B
	 */
	public void setEtSecond(int etSecond) {
		this.etSecond = etSecond;
	}

	/**
	 * @return etYear ��߂��܂��B
	 */
	public int getEtYear() {
		return etYear;
	}

	/**
	 * @param etYear etYear ��ݒ�B
	 */
	public void setEtYear(int etYear) {
		this.etYear = etYear;
	}

	/**
	 * @return stDay ��߂��܂��B
	 */
	public int getStDay() {
		return stDay;
	}

	/**
	 * @param stDay stDay ��ݒ�B
	 */
	public void setStDay(int stDay) {
		this.stDay = stDay;
	}

	/**
	 * @return stEneble ��߂��܂��B
	 */
	public boolean isStEneble() {
		return stEneble;
	}

	/**
	 * @param stEneble stEneble ��ݒ�B
	 */
	public void setStEneble(boolean stEneble) {
		this.stEneble = stEneble;
	}

	/**
	 * @return stHour ��߂��܂��B
	 */
	public int getStHour() {
		return stHour;
	}

	/**
	 * @param stHour stHour ��ݒ�B
	 */
	public void setStHour(int stHour) {
		this.stHour = stHour;
	}

	/**
	 * @return stMinute ��߂��܂��B
	 */
	public int getStMinute() {
		return stMinute;
	}

	/**
	 * @param stMinute stMinute ��ݒ�B
	 */
	public void setStMinute(int stMinute) {
		this.stMinute = stMinute;
	}

	/**
	 * @return stMonth ��߂��܂��B
	 */
	public int getStMonth() {
		return stMonth;
	}

	/**
	 * @param stMonth stMonth ��ݒ�B
	 */
	public void setStMonth(int stMonth) {
		this.stMonth = stMonth;
	}

	/**
	 * @return stSecond ��߂��܂��B
	 */
	public int getStSecond() {
		return stSecond;
	}

	/**
	 * @param stSecond stSecond ��ݒ�B
	 */
	public void setStSecond(int stSecond) {
		this.stSecond = stSecond;
	}

	/**
	 * @return stYear ��߂��܂��B
	 */
	public int getStYear() {
		return stYear;
	}

	/**
	 * @param stYear stYear ��ݒ�B
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
