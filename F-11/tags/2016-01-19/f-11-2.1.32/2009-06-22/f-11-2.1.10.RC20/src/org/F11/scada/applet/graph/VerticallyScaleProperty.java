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

package org.F11.scada.applet.graph;

import java.awt.Color;

public class VerticallyScaleProperty {
	private final Color backGroundColor;
	private final Color foregroundColor1;
	private final Color foregroundColor2;

	public VerticallyScaleProperty(Color backGroundColor, Color foregroundColor1, Color foregroundColor2) {
		this.backGroundColor = backGroundColor;
		this.foregroundColor1 = foregroundColor1;
		this.foregroundColor2 = foregroundColor2;
	}

	public Color getBackGroundColor() {
		return backGroundColor;
	}

	public Color getForegroundColor1() {
		return foregroundColor1;
	}

	public Color getForegroundColor2() {
		return foregroundColor2;
	}
	
	public String toString() {
		return "backGroundColor:" + backGroundColor
			+ " ,foregroundColor1:" + foregroundColor1
			+ " ,foregroundColor2:" + foregroundColor2;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof VerticallyScaleProperty)) {
			return false;
		}
		VerticallyScaleProperty vp = (VerticallyScaleProperty) obj;
		
		return vp.backGroundColor.equals(backGroundColor)
			&& vp.foregroundColor1.equals(foregroundColor1)
			&& vp.foregroundColor2.equals(foregroundColor2);
	}
	
	public int hashCode() {
		int result = 17;
		result = 37 * result + backGroundColor.hashCode();
		result = 37 * result + foregroundColor1.hashCode();
		result = 37 * result + foregroundColor2.hashCode();
		return result;
	}
}
