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
 */

package org.F11.scada.parser.graph;

import java.awt.Color;
import java.util.Stack;

import org.F11.scada.applet.graph.VerticallyScaleProperty;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.State;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class VerticallyScalePropertyState implements State {
	private static Logger logger;

	private Color backGroundColor;
	private Color foregroundColor1;
	private Color foregroundColor2;
	
	VerticallyScalePropertyState(String tagName, Attributes atts, GraphPropertyModelState graphPropertyModelState) {
		logger = Logger.getLogger(getClass());
		
		backGroundColor = ColorFactory.getColor(getColor(atts, "backGroundColor", "gray"));
		foregroundColor1 = ColorFactory.getColor(getColor(atts, "foregroundColor1", "black"));
		foregroundColor2 = ColorFactory.getColor(getColor(atts, "foregroundColor2", "white"));
		graphPropertyModelState.verticallyScaleProperty = new VerticallyScaleProperty(
				backGroundColor, foregroundColor1, foregroundColor2);
	}
	
	private String getColor(Attributes atts, String key, String color) {
		String att = atts.getValue(key);
		return isNullOrNullString(att) ? color : att;
	}
	
	private boolean isNullOrNullString(String s) {
		return (s == null || "".equals(s));
	}

	public void add(String tagName, Attributes atts, Stack stack) {
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("vertically")) {
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}
}
