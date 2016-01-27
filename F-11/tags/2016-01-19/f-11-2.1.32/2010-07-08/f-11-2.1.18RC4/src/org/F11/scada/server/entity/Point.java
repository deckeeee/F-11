/*
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

package org.F11.scada.server.entity;

import java.io.Serializable;

/**
 * point_tableのエンティティ
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class Point implements Serializable {
    private static final long serialVersionUID = -8881807660923919005L;

    public static final String TABLE = "point_table";
    public static final String unitMark_COLUMN = "unit_mark";

    private Integer point;
    private String unit;
    private String name;
    private String unitMark;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getPoint() {
        return point;
    }
    public void setPoint(Integer point) {
        this.point = point;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getUnitMark() {
        return unitMark;
    }
    public void setUnitMark(String unitMark) {
        this.unitMark = unitMark;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return
        	point + " " +
        	unit  + " " +
        	name + " " +
        	unitMark;
    }
}
