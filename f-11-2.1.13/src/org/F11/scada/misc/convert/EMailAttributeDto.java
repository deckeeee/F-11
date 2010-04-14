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

package org.F11.scada.misc.convert;

public class EMailAttributeDto {
	public static final String TABLE = "email_attribute_setting_table";

	private long emailAttributeSettingId;
	private int attributeId;
	private int emailGroupId;
	private String emailAddress;

	public long getEmailAttributeSettingId() {
		return emailAttributeSettingId;
	}
	public void setEmailAttributeSettingId(long emailAttributeSettingId) {
		this.emailAttributeSettingId = emailAttributeSettingId;
	}
	public int getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}
	public int getEmailGroupId() {
		return emailGroupId;
	}
	public void setEmailGroupId(int emailGroupId) {
		this.emailGroupId = emailGroupId;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String toString() {
		return emailAttributeSettingId + " " + attributeId + " " + emailGroupId + " " + emailAddress;
	}
}
