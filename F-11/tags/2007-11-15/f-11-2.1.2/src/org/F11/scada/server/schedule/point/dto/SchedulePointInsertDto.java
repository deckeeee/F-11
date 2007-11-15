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

public class SchedulePointInsertDto implements Serializable {
	private static final long serialVersionUID = 1589756798997487570L;
	public static final String TABLE = "schedule_point_table";
	public static final String NO_PERSISTENT_PROPS = "id";

	private Long id;
	private String provider;
	private String holder;
	private String pageId;
	private int groupNo;
	private String groupProvider;
	private String groupHolder;
	private boolean sort;
	private String separateProvider;
	private String separateHolder;

	public String getGroupProvider() {
		return groupProvider;
	}

	public void setGroupProvider(String groupProvider) {
		this.groupProvider = groupProvider;
	}

	public String getGroupHolder() {
		return groupHolder;
	}

	public void setGroupHolder(String groupHolder) {
		this.groupHolder = groupHolder;
	}

	public int getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(int groupNo) {
		this.groupNo = groupNo;
	}

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public boolean isSort() {
		return sort;
	}

	public void setSort(boolean sort) {
		this.sort = sort;
	}

	public String getSeparateHolder() {
		return separateHolder;
	}

	public void setSeparateHolder(String separateHolder) {
		this.separateHolder = separateHolder;
	}

	public String getSeparateProvider() {
		return separateProvider;
	}

	public void setSeparateProvider(String separateProvider) {
		this.separateProvider = separateProvider;
	}

}
