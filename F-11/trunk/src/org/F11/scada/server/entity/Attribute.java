/*
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

package org.F11.scada.server.entity;

import java.io.Serializable;

/**
 * attribute_tableのエンティティクラス
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class Attribute implements Serializable {
	private static final long serialVersionUID = 1298525568741215785L;
	public static final String TABLE = "attribute_table";

	private Integer attribute;
	private String name;
	private String onAlarmColor;
	private String offAlarmColor;
	private String onSummaryColor;
	private String offSummaryColor;
	private String onPrinterColor;
	private String offPrinterColor;
	private Integer soundType;
	private Boolean checkType;
	private Integer summaryMode;
	private Integer historyMode;
	private Integer careerMode;
	private Integer newInfoMode;
	private Boolean printerMode;
	private Integer chateringTimer;
	private Boolean messageMode;

	public Integer getAttribute() {
		return attribute;
	}

	public void setAttribute(Integer attribute) {
		this.attribute = attribute;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOnAlarmColor() {
		return onAlarmColor;
	}

	public void setOnAlarmColor(String onAlarmColor) {
		this.onAlarmColor = onAlarmColor;
	}

	public String getOffAlarmColor() {
		return offAlarmColor;
	}

	public void setOffAlarmColor(String offAlarmColor) {
		this.offAlarmColor = offAlarmColor;
	}

	public String getOnSummaryColor() {
		return onSummaryColor;
	}

	public void setOnSummaryColor(String onSummaryColor) {
		this.onSummaryColor = onSummaryColor;
	}

	public String getOffSummaryColor() {
		return offSummaryColor;
	}

	public void setOffSummaryColor(String offSummaryColor) {
		this.offSummaryColor = offSummaryColor;
	}

	public String getOnPrinterColor() {
		return onPrinterColor;
	}

	public void setOnPrinterColor(String onPrinterColor) {
		this.onPrinterColor = onPrinterColor;
	}

	public String getOffPrinterColor() {
		return offPrinterColor;
	}

	public void setOffPrinterColor(String offPrinterColor) {
		this.offPrinterColor = offPrinterColor;
	}

	public Integer getSoundType() {
		return soundType;
	}

	public void setSoundType(Integer soundType) {
		this.soundType = soundType;
	}

	public Boolean getCheckType() {
		return checkType;
	}

	public void setCheckType(Boolean checkType) {
		this.checkType = checkType;
	}

	public Integer getSummaryMode() {
		return summaryMode;
	}

	public void setSummaryMode(Integer summaryMode) {
		this.summaryMode = summaryMode;
	}

	public Integer getHistoryMode() {
		return historyMode;
	}

	public void setHistoryMode(Integer historyMode) {
		this.historyMode = historyMode;
	}

	public Integer getCareerMode() {
		return careerMode;
	}

	public void setCareerMode(Integer careerMode) {
		this.careerMode = careerMode;
	}

	public Integer getNewInfoMode() {
		return newInfoMode;
	}

	public void setNewInfoMode(Integer newInfoMode) {
		this.newInfoMode = newInfoMode;
	}

	public Boolean getPrinterMode() {
		return printerMode;
	}

	public void setPrinterMode(Boolean printerMode) {
		this.printerMode = printerMode;
	}

	public Integer getChateringTimer() {
		return chateringTimer;
	}

	public void setChateringTimer(Integer chateringTimer) {
		this.chateringTimer = chateringTimer;
	}

	public Boolean getMessageMode() {
		return messageMode;
	}

	public void setMessageMode(Boolean messageMode) {
		this.messageMode = messageMode;
	}

	@Override
	public String toString() {
		return attribute
			+ " "
			+ name
			+ " "
			+ onAlarmColor
			+ " "
			+ offAlarmColor
			+ " "
			+ onSummaryColor
			+ " "
			+ offSummaryColor
			+ " "
			+ onPrinterColor
			+ " "
			+ offPrinterColor
			+ " "
			+ soundType
			+ " "
			+ checkType
			+ " "
			+ summaryMode
			+ " "
			+ historyMode
			+ " "
			+ careerMode
			+ " "
			+ newInfoMode
			+ " "
			+ printerMode
			+ " "
			+ chateringTimer
			+ " "
			+ messageMode;
	}
}
