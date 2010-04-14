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
import java.sql.Timestamp;

public class SchedulePointRowDto implements Serializable {
	private static final long serialVersionUID = 2125063501419758561L;
	public static final String TABLE = "schedule_point_table";
	private static final String[] TITLE = { "id", "No.", "グループ名", "記号", "機器名称",
			"個別", "グループプロバイダ", "グループホルダ", "個別プロバイダ", "個別ホルダ", "ページID" };

	private Long id;
	private Integer groupNo;
	private String groupName;
	private String unit;
	private String name;
	private Boolean sort;
	private String groupNoProvider;
	private String groupNoHolder;
	private String separateProvider;
	private String separateHolder;
	private String pageId;
	private String user;
	private String ipAddress;
	private Timestamp timestamp;

	public static int getColumn() {
		return 10;
	}

	public static String[] getTitles() {
		String[] titles = new String[TITLE.length];
		System.arraycopy(TITLE, 0, titles, 0, TITLE.length);
		return titles;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(Integer groupNo) {
		this.groupNo = groupNo;
	}

	public String getGroupNoProvider() {
		return groupNoProvider;
	}

	public void setGroupNoProvider(String groupNoProvider) {
		this.groupNoProvider = groupNoProvider;
	}

	public String getGroupNoHolder() {
		return groupNoHolder;
	}

	public void setGroupNoHolder(String groupNoholder) {
		this.groupNoHolder = groupNoholder;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String mark) {
		this.unit = mark;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getSort() {
		return sort;
	}

	public void setSort(Boolean sort) {
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

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String toString() {
		return "id=" + id + ", groupNo=" + groupNo + ", groupName=" + groupName
				+ ", unit=" + unit + ", name=" + name + ", sort=" + sort
				+ ", groupNoProvider=" + groupNoProvider + ", groupNoholder="
				+ groupNoHolder + ", separateProvider=" + separateProvider
				+ ", separateHolder=" + separateHolder + ", pageId=" + pageId;
	}
}
