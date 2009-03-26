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

import java.awt.Color;
import java.io.Serializable;

public class UnitData implements Serializable {
	private static final long serialVersionUID = 1846914704721086167L;
	private Color unitColor;
	private String unitNo;
	private String unitName;
	private String unitMark;
	private Float min;
	private Float max;

	public UnitData() {
	}

	public UnitData(
			Color unitColor,
			String unitNo,
			String unitName,
			String unitMark,
			Float min,
			Float max) {
		this.unitColor = unitColor;
		this.unitNo = unitNo;
		this.unitName = unitName;
		this.unitMark = unitMark;
		this.min = min;
		this.max = max;
	}

	public UnitData(UnitData src) {
		unitColor = src.unitColor;
		unitNo = src.unitNo;
		unitName = src.unitName;
		unitMark = src.unitMark;
		min = src.min;
		max = src.max;
	}

	public Color getUnitColor() {
		return unitColor;
	}

	public void setUnitColor(Color unitColor) {
		this.unitColor = unitColor;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUnitMark() {
		return unitMark;
	}

	public void setUnitMark(String unitMark) {
		this.unitMark = unitMark;
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

	@Override
	public String toString() {
		return unitColor
			+ " "
			+ min
			+ " "
			+ max
			+ " "
			+ unitNo
			+ " "
			+ unitName
			+ " "
			+ unitMark;
	}
}
