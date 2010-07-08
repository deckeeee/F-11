/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.applet.ngraph.editor;

import static org.F11.scada.applet.ngraph.util.XmlAttributeUtil.getAttribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ページ定義のシリーズ定義プロパティー
 * 
 * @author maekawa
 *
 */
public class SeriesData implements Serializable {
	private static final long serialVersionUID = 4704897104528037563L;
	private Integer groupNo;
	private String groupName;
	private List<SeriesPropertyData> seriesProperties;

	public SeriesData() {
	}

	public SeriesData(
			Integer groupNo,
			String groupName,
			List<SeriesPropertyData> seriesProperties) {
		this.groupNo = groupNo;
		this.groupName = groupName;
		this.seriesProperties = seriesProperties;
	}

	public Integer getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(Integer groupNo) {
		this.groupNo = groupNo;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<SeriesPropertyData> getSeriesProperties() {
		return seriesProperties;
	}

	public void setSeriesProperties(List<SeriesPropertyData> seriesProperties) {
		this.seriesProperties = seriesProperties;
	}

	public void addSeriesProperty(SeriesPropertyData seriesPropertyData) {
		if (null == this.seriesProperties) {
			this.seriesProperties = new ArrayList<SeriesPropertyData>();
		}
		this.seriesProperties.add(seriesPropertyData);
	}

	public String getXmlString() {
		StringBuilder sb = new StringBuilder();
		sb.append("      <series");
		sb.append(getAttribute("groupNo", groupNo));
		sb.append(getAttribute("groupName", groupName)).append(">\n");
		for (SeriesPropertyData sd : seriesProperties) {
			sb.append(sd.getXmlString());
		}
		sb.append("      </series>\n");
		return sb.toString();
	}
}
