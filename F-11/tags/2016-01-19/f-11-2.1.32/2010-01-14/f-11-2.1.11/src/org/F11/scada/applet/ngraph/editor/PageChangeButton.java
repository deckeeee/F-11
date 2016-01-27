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

public class PageChangeButton {
	private String changeto;
	private String value;
	private Integer x;
	private Integer y;
	private String tooltiptext;
	private Boolean opaque;
	private String foreground;
	private String background;
	private String font;
	private String font_style;
	private String font_size;
	private Integer width;
	private Integer height;

	public String getChangeto() {
		return changeto;
	}

	public void setChangeto(String changeto) {
		this.changeto = changeto;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public String getTooltiptext() {
		return tooltiptext;
	}

	public void setTooltiptext(String tooltiptext) {
		this.tooltiptext = tooltiptext;
	}

	public Boolean getOpaque() {
		return opaque;
	}

	public void setOpaque(Boolean opaque) {
		this.opaque = opaque;
	}

	public String getForeground() {
		return foreground;
	}

	public void setForeground(String foreground) {
		this.foreground = foreground;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getFont_style() {
		return font_style;
	}

	public void setFont_style(String font_style) {
		this.font_style = font_style;
	}

	public String getFont_size() {
		return font_size;
	}

	public void setFont_size(String font_size) {
		this.font_size = font_size;
	}

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

	public String getXmlString() {
		StringBuilder sb = new StringBuilder();
		sb.append("    <pagechangebutton");
		sb.append(getAttribute("x", x));
		sb.append(getAttribute("y", y));
		sb.append(getAttribute("width", width));
		sb.append(getAttribute("height", height));
		sb.append(getAttribute("changeto", changeto));
		sb.append(getAttribute("value", value));
		sb.append(getAttribute("tooltiptext", tooltiptext));
		sb.append(getAttribute("opaque", opaque));
		sb.append(getAttribute("foreground", foreground));
		sb.append(getAttribute("background", background));
		sb.append(getAttribute("font", font));
		sb.append(getAttribute("font_style", font_style));
		sb.append(getAttribute("font_size", font_size));
		sb.append(" />\n");
		return sb.toString();
	}
}