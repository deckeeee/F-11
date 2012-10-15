/*
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
package org.F11.scada.parser.graph.bargraph2;

import java.awt.Font;
import java.util.Stack;

import org.F11.scada.applet.graph.bargraph2.BarGraph2;
import org.F11.scada.applet.graph.bargraph2.ModelSelectComboBox;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

public class ModelSelecterState implements State {
	private static Logger logger = Logger.getLogger(ModelSelecterState.class);

	private BarGraph2 bargraph2;
	private ModelSelectComboBox selector;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public ModelSelecterState(String tagName, Attributes atts,
			BarGraph2 bargraph2) {
		this.bargraph2 = bargraph2;
		selector = new ModelSelectComboBox();
		String x = atts.getValue("x");
		String y = atts.getValue("y");
		String width = atts.getValue("width");
		String height = atts.getValue("height");
		if (!isNullstring(x) && !isNullstring(y)) {
			selector.setLocation(Integer.parseInt(x), Integer.parseInt(y));
		}
		if (!isNullstring(width) && !isNullstring(height)) {
			selector.setSize(Integer.parseInt(width), Integer.parseInt(height));
		}

		String fgcolor = atts.getValue("foreground");
		if (isNullstring(fgcolor)) {
			fgcolor = "black";
		}
		selector.setForeground(ColorFactory.getColor(fgcolor));
		String bgcolor = atts.getValue("background");
		if (isNullstring(bgcolor)) {
			bgcolor = "white";
		}
		selector.setBackground(ColorFactory.getColor(bgcolor));

		String fontName = atts.getValue("font");
		if (isNullstring(fontName)) {
			fontName = "dialog";
		}
		String fontStyle = atts.getValue("font_style");
		int style = Font.PLAIN;
		if (isNullstring(fontStyle)) {
			style = Font.BOLD;
		} else if ("ITALIC".equalsIgnoreCase(fontStyle)) {
			style = Font.ITALIC;
		} else if ("PLAIN".equalsIgnoreCase(fontStyle)) {
			style = Font.PLAIN;
		} else {
			style = Font.BOLD;
		}
		String fontSize = atts.getValue("font_size");
		if (isNullstring(fontSize)) {
			fontSize = "12";
		}
		selector.setFont(new Font(fontName, style, Integer.parseInt(fontSize)));

	}

	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("bargraphmodel")) {
			stack.push(new BarGraphModelState(tagName, atts, selector));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("modelselector")) {
			bargraph2.setModelSelecter(selector);
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	private boolean isNullstring(String s) {
		return (s == null || "".equals(s));
	}

}
