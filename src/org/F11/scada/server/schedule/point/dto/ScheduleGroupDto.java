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

package org.F11.scada.server.schedule.point.dto;

import java.io.Serializable;

public class ScheduleGroupDto implements Serializable {
	private static final long serialVersionUID = 2310689930470126125L;
	private static final String[] TITLE = { "id", "No.", "グループ", "プロバイダ", "ホルダ", "ページID" };
	public static final String TABLE = "schedule_group_table";

	private Long id;
	private Integer groupNo;
	private String groupName;
	private String provider;
	private String holder;
	private String pageId;

	public static String[] getGroupTitles() {
		String[] rt = new String[TITLE.length];
		System.arraycopy(TITLE, 0, rt, 0, TITLE.length);
		return rt;
	}

	public Integer getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(Integer groupNo) {
		this.groupNo = groupNo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String toString() {
		return "id=" + id + ", groupNo=" + groupNo + ", groupName=" + groupName
				+ ", provider=" + provider + ", holder=" + holder + ", pageId=" + pageId;
	}
}
