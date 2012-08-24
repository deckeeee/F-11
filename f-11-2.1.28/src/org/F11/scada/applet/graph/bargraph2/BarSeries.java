/*
 * $Header:
 * /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/GraphPropertyModel.java,v
 * 1.13.2.5 2007/07/11 07:47:18 frdm Exp $ $Revision: 1.13.2.5 $ $Date:
 * 2007/07/11 07:47:18 $
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.applet.graph.bargraph2;

import java.util.ArrayList;
import java.util.List;

public class BarSeries {
	private String name;
	private String unit_mark;
	private List<PointProperty> propertyList = new ArrayList<PointProperty>();

	public PointProperty getPointProperty(int index) {
		return propertyList.get(index);
	}
	public void addPointProperty(PointProperty pointProperty) {
		this.propertyList.add(pointProperty);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit_mark() {
		return unit_mark;
	}
	public void setUnit_mark(String unit_mark) {
		this.unit_mark = unit_mark;
	}

}
