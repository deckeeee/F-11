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

package org.F11.scada.parser.ngraph;

import java.util.Stack;

import org.F11.scada.applet.ngraph.HorizontalScaleButtonProperty;
import org.F11.scada.parser.State;
import org.xml.sax.Attributes;

public class ScaleButtonPropertiesState implements State {

	public ScaleButtonPropertiesState(
			String tagName,
			Attributes atts,
			HorizontalScaleButtonState horizontalScaleButtonState) {
		HorizontalScaleButtonProperty property =
			new HorizontalScaleButtonProperty();
		property.setButtonText(atts.getValue("buttonText"));
		property.setLabelText(atts.getValue("labelText"));
		property
			.setHorizontalCount(getInteger(atts.getValue("horizontalCount")));
		property.setHorizontalAllSpanMode(getInteger(atts
			.getValue("horizontalAllSpanMode")));
		property.setHorizontalSelectSpanMode(getInteger(atts
			.getValue("horizontalSelectSpanMode")));
		property.setHorizontalLineSpan(getLong(atts
			.getValue("horizontalLineSpan")));
		property.setRecordeSpan(getLong(atts.getValue("recordeSpan")));
		property.setLogName(atts.getValue("logName"));
		horizontalScaleButtonState.scaleButtonProperties.add(property);
	}

	private Integer getInteger(String s) {
		try {
			return Integer.valueOf(s);
		} catch (Exception e) {
			return null;
		}
	}
	
	private Long getLong(String s) {
		try {
			return Long.valueOf(s);
		} catch (Exception e) {
			return null;
		}
	}

	public void add(String tagName, Attributes atts, Stack stack) {
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("horizontalScaleButton-property")) {
			stack.pop();
		}
	}

}
