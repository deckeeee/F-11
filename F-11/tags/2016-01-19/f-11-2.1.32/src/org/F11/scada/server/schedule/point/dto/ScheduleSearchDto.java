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

import org.seasar.dao.pager.DefaultPagerCondition;

public class ScheduleSearchDto extends DefaultPagerCondition {
	private static final long serialVersionUID = -7049982266056269502L;
	private String unit;
	private String name;
	private String groupName;
	private String pageId;
	private Integer groupNo;

	public String getGroupName() {
		return null != groupName ? "%" + groupName + "%" : null;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getName() {
		return null != name ? "%" + name + "%" : null;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return null != unit ? "%" + unit + "%" : null;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public Integer getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(Integer groupNo) {
		this.groupNo = groupNo;
	}

	public String toString() {
		return "count=" + getCount() + ", limit=" + getLimit() + ", offset="
				+ getOffset() + ", unit=" + unit + ", name=" + name
				+ ", groupName=" + groupName + ", pageId=" + pageId
				+ ", groupNo=" + groupNo;
	}
}
