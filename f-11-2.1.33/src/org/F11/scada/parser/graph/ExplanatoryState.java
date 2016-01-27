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
import java.awt.Font;
import java.util.Stack;

import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.State;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ExplanatoryState implements State {
	private static Logger logger;

	GraphPropertyModelState graphPropertyModelState;
	Color foreground;
	Font font;
	
	ExplanatoryState(String tagName, Attributes atts, GraphPropertyModelState graphPropertyModelState) {
		logger = Logger.getLogger(getClass());
		this.graphPropertyModelState = graphPropertyModelState;
		
		String foregroundStr = atts.getValue("foreground");
		if (isNullOrNullString(foregroundStr)) {
			foregroundStr = "black";
		}
		foreground = ColorFactory.getColor(foregroundStr);

		String fontName = atts.getValue("font");
		if (isNullOrNullString(fontName)) {
			fontName = "dialog";
		}
		String fontStyle = atts.getValue("font_style");
		int style = Font.PLAIN;
		if (isNullOrNullString(fontStyle)) {
			style = Font.BOLD;
		} else if ("ITALIC".equalsIgnoreCase(fontStyle)) {
			style = Font.ITALIC;
		} else if ("PLAIN".equalsIgnoreCase(fontStyle)){
			style = Font.PLAIN;
		} else {
			style = Font.BOLD;
		}
		String fontSize = atts.getValue("font_size");
		if (isNullOrNullString(fontSize)) {
			fontSize = "12";
		}
		font = new Font(fontName, style, Integer.parseInt(fontSize));
	}
	
	private boolean isNullOrNullString(String s) {
		return (s == null || "".equals(s));
	}

	public void add(String tagName, Attributes atts, Stack stack) {
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("explanatory")) {
			graphPropertyModelState.explanatoryColor = foreground;
			graphPropertyModelState.explanatoryFont = font;
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}
}
