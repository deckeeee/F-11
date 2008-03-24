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

package org.F11.scada.tool.sound.individual;

import java.io.Serializable;

/**
 * 警報音個別設定に使用するDtoです。
 * @author maekawa
 */
public class IndividualDto implements Serializable {
	private static final long serialVersionUID = 7327364735207343047L;
	
	public static final String TABLE = "alarm_individual_setting_table";

	/** ID */
	private long alarmIndividualSettingId;
	/** ポイント */
	private int point;
	/** ポイント記号 */
	private String unit;
	/** ポイント名称 */
	private String name;
	/** ポイント属性 */
	private String attribute;
	/** 属性警報音タイプ */
	private int attributetype;
	/** 警報音タイプ */
	private int type;
	/** プロバイダ */
	private String provider;
	/** ホルダ */
	private String holder;

	public long getAlarmIndividualSettingId() {
		return alarmIndividualSettingId;
	}
	public void setAlarmIndividualSettingId(long alarmIndividualSettingId) {
		this.alarmIndividualSettingId = alarmIndividualSettingId;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public int getAttributetype() {
		return attributetype;
	}
	public void setAttributetype(int attributetype) {
		this.attributetype = attributetype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getHolder() {
		return holder;
	}
	public void setHolder(String holder) {
		this.holder = holder;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String toString() {
		return point + " " + unit + " " + name + " " + attribute + " " + attributetype + " " + type + " " + provider + " " + holder;
	}
}
