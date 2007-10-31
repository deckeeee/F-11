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

public class EMailIndividualDto {
	public static final String TABLE = "email_individual_setting_table";

	private long emailIndividualSettingId;
	private String provider;
	private String holder;
	private int emailGroupId;
	private String emailAddress;

	public long getEmailIndividualSettingId() {
		return emailIndividualSettingId;
	}
	public void setEmailIndividualSettingId(long emailIndividualSettingId) {
		this.emailIndividualSettingId = emailIndividualSettingId;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
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
	public int getEmailGroupId() {
		return emailGroupId;
	}
	public void setEmailGroupId(int emailGroupId) {
		this.emailGroupId = emailGroupId;
	}
	public String toString() {
		return emailIndividualSettingId + " " + provider + " " + holder + " " + emailGroupId + " " + emailAddress;
	}
}
