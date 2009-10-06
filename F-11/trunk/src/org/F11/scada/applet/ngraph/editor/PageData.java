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

/**
 * 定義ファイルのページプロパティー
 * 
 * @author maekawa
 * 
 */
public class PageData implements Serializable {
	private static final long serialVersionUID = -4675764103764679678L;
	private Integer width;
	private Integer height;
	private String name;
	private String value;
	private Trend3Data trend3Data;
	private PageChangeButton pageChangeButton;

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Trend3Data getTrend3Data() {
		return trend3Data;
	}

	public void setTrend3Data(Trend3Data trend3Data) {
		this.trend3Data = trend3Data;
	}

	public PageChangeButton getPageChangeButton() {
		return pageChangeButton;
	}

	public void setPageChangeButton(PageChangeButton pageChangeButton) {
		this.pageChangeButton = pageChangeButton;
	}

	public String getXmlString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<page_map>\n");
		sb.append("  <page").append(getAttributes()).append(">\n");
		if (null != pageChangeButton) {
			sb.append(pageChangeButton.getXmlString());
		}
		sb.append(trend3Data.getXmlString());
		sb.append("  </page>\n");
		sb.append("</page_map>\n");
		return sb.toString();
	}

	private String getAttributes() {
		StringBuilder sb = new StringBuilder();
		sb.append(getAttribute("width", width));
		sb.append(getAttribute("height", height));
		sb.append(getAttribute("name", name));
		sb.append(getAttribute("value", value));
		return sb.toString();
	}
	
	public boolean isWriteOk() {
		boolean ret = true;
		for (SeriesData sd : trend3Data.getSeriesDatas()) {
			if (0 >= sd.getSeriesProperties().size()) {
				 ret = false;
			}
		}
		return ret;
	}
}
