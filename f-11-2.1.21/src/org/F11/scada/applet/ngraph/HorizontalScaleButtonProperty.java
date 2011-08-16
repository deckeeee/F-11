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

package org.F11.scada.applet.ngraph;

import static org.F11.scada.applet.ngraph.util.XmlAttributeUtil.getAttribute;

public class HorizontalScaleButtonProperty {
	private String buttonText;
	private String labelText;
	private Integer horizontalCount;
	private Integer horizontalAllSpanMode;
	private Integer horizontalSelectSpanMode;
	private Long horizontalLineSpan;
	private Long recordeSpan;
	private String logName;

	public String getButtonText() {
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public Integer getHorizontalCount() {
		return horizontalCount;
	}

	public void setHorizontalCount(Integer horizontalCount) {
		this.horizontalCount = horizontalCount;
	}

	public Integer getHorizontalAllSpanMode() {
		return horizontalAllSpanMode;
	}

	public void setHorizontalAllSpanMode(Integer horizontalAllSpanMode) {
		this.horizontalAllSpanMode = horizontalAllSpanMode;
	}

	public Integer getHorizontalSelectSpanMode() {
		return horizontalSelectSpanMode;
	}

	public void setHorizontalSelectSpanMode(Integer horizontalSelectSpanMode) {
		this.horizontalSelectSpanMode = horizontalSelectSpanMode;
	}

	public Long getHorizontalLineSpan() {
		return horizontalLineSpan;
	}

	public void setHorizontalLineSpan(Long horizontalLineSpan) {
		this.horizontalLineSpan = horizontalLineSpan;
	}

	public Long getRecordeSpan() {
		return recordeSpan;
	}

	public void setRecordeSpan(Long recordeSpan) {
		this.recordeSpan = recordeSpan;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public String getXmlString() {
		StringBuilder sb = new StringBuilder();
		sb.append("        <horizontalScaleButton-property");
		sb.append(getAttribute("buttonText", buttonText));
		sb.append(getAttribute("labelText", labelText));
		sb.append(getAttribute("horizontalCount", horizontalCount));
		sb.append(getAttribute("horizontalAllSpanMode", horizontalAllSpanMode));
		sb.append(getAttribute(
			"horizontalSelectSpanMode",
			horizontalSelectSpanMode));
		sb.append(getAttribute("horizontalLineSpan", horizontalLineSpan));
		sb.append(getAttribute("recordeSpan", recordeSpan));
		sb.append(getAttribute("logName", logName)).append("/>\n");
		return sb.toString();
	}
}
