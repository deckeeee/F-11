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
 * �x�񉹌ʐݒ�Ɏg�p����Dto�ł��B
 * @author maekawa
 */
public class IndividualDto implements Serializable {
	private static final long serialVersionUID = 7327364735207343047L;
	
	public static final String TABLE = "alarm_individual_setting_table";

	/** ID */
	private long alarmIndividualSettingId;
	/** �|�C���g */
	private int point;
	/** �|�C���g�L�� */
	private String unit;
	/** �|�C���g���� */
	private String name;
	/** �|�C���g���� */
	private String attribute;
	/** �����x�񉹃^�C�v */
	private int attributetype;
	/** �x�񉹃^�C�v */
	private int type;
	/** �v���o�C�_ */
	private String provider;
	/** �z���_ */
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
