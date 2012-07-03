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

import java.awt.Color;
import java.io.Serializable;

import org.F11.scada.applet.symbol.ColorFactory;

/**
 * ページ定義のシリーズプロパティー
 *
 * @author maekawa
 *
 */
public class SeriesPropertyData implements Serializable {
	private static final long serialVersionUID = 888380556791138788L;
	private Integer index;
	private Boolean visible;
	private String color;
	private Color colorObject;
	private String unit;
	private String name;
	private String mark;
	private Float min;
	private Float max;
	private String verticalFormat;
	private String holder;
	private String convert;
	private Integer point;

	public SeriesPropertyData() {
	}

	public SeriesPropertyData(SeriesPropertyData src) {
		index = src.index;
		visible = src.visible;
		color = src.color;
		colorObject = src.colorObject;
		unit = src.unit;
		name = src.name;
		mark = src.mark;
		min = src.min;
		max = src.max;
		verticalFormat = src.verticalFormat;
		holder = src.holder;
		convert = src.convert;
		point = src.point;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Boolean isVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
		colorObject = ColorFactory.getColor(color);
	}

	public Color getColorObject() {
		return colorObject;
	}

	public void setColorObject(Color colorObject) {
		this.colorObject = colorObject;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public Float getMin() {
		return min;
	}

	public void setMin(Float min) {
		this.min = min;
	}

	public Float getMax() {
		return max;
	}

	public void setMax(Float max) {
		this.max = max;
	}

	public String getVerticalFormat() {
		return verticalFormat;
	}

	public void setVerticalFormat(String verticalFormat) {
		this.verticalFormat = verticalFormat;
	}

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public String getConvert() {
		return convert;
	}

	public void setConvert(String convert) {
		this.convert = convert;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public String getXmlString() {
		StringBuilder sb = new StringBuilder();
		sb.append("        <series-property");
		sb.append(getAttribute("index", index));
		sb.append(getAttribute("visible", visible));
		sb.append(getAttribute("color", color));
		sb.append(getAttribute("unit", getDollarUnit(point)));
		sb.append(getAttribute("name", getDollarName(point)));
		sb.append(getAttribute("mark", getDollarMark(point)));
		sb.append(getAttribute("min", min));
		sb.append(getAttribute("max", max));
		sb.append(getAttribute("verticalFormat", verticalFormat));
		sb.append(getAttribute("holder", holder));
		sb.append(getAttribute("point", point));
		sb.append(getAttribute("convert", convert)).append("/>\n");
		return sb.toString();
	}

	private String getDollarUnit(Integer point) {
		return "$(" + point + "_unit)";
	}

	private String getDollarName(Integer point) {
		return "$(" + point + "_name)";
	}

	private String getDollarMark(Integer point) {
		return "$(" + point + "_unit_mark)";
	}
}
