/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.tool.point.name;

import org.seasar.dao.pager.DefaultPagerCondition;

public class PointNameCondition extends DefaultPagerCondition {
	private static final long serialVersionUID = -2818900855547111786L;

	private String name;
	private String unit;
	private String unitMark;
	private String attribute1;
	private String attribute2;
	private String attribute3;

	public String getName() {
		return null != name && !"".equals(name) ? "%" + name + "%" : null;
	}

	public String getSearchName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return null != unit && !"".equals(unit) ? "%" + unit + "%" : null;
	}

	public String getSearchUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnitMark() {
		return null != unitMark && !"".equals(unitMark)
				? "%" + unitMark + "%"
				: null;
	}

	public String getSearchUnitMark() {
		return unitMark;
	}

	public void setUnitMark(String unitMark) {
		this.unitMark = unitMark;
	}

	public String getAttribute1() {
		return null != attribute1 && !"".equals(attribute1) ? "%" + attribute1
				+ "%" : null;
	}

	public String getSearchAttribute1() {
		return attribute1;
	}

	public void setAttribute1(String attribute1) {
		this.attribute1 = attribute1;
	}

	public String getAttribute2() {
		return null != attribute2 && !"".equals(attribute2) ? "%" + attribute2
				+ "%" : null;
	}

	public String getSearchAttribute2() {
		return attribute2;
	}

	public void setAttribute2(String attribute2) {
		this.attribute2 = attribute2;
	}

	public String getAttribute3() {
		return null != attribute3 && !"".equals(attribute3) ? "%" + attribute3
				+ "%" : null;
	}

	public String getSearchAttribute3() {
		return attribute3;
	}

	public void setAttribute3(String attribute3) {
		this.attribute3 = attribute3;
	}
}
