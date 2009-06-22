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

import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.State;
import org.F11.scada.util.AttributesUtil;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * GraphViewタグ内のVerticallyScaleのプロパティーを生成します。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class VerticallyScaleState implements State {
	private static Logger logger;

	VerticallyScaleState(String tagName, Attributes atts, GraphViewState state) {
		logger = Logger.getLogger(getClass());
		addVerticallyScale(state, atts);
	}

	private void addVerticallyScale(GraphViewState state, Attributes atts) {
		int series = getSeries(atts);
		boolean isScaleButtonVisible = AttributesUtil.getBooleanValue("isScaleButtonVisible", atts);
		boolean isRight = getAlign(atts);
		int x = Integer.parseInt(AttributesUtil.getValue("x", atts));
		int y = Integer.parseInt(AttributesUtil.getValue("y", atts));
		int width = Integer.parseInt(AttributesUtil.getValue("width", atts));
		int height = Integer.parseInt(AttributesUtil.getValue("height", atts));
		Color foreground = getForeground(atts);
		Color background = getBackground(atts);
		boolean isTrend = AttributesUtil.getBooleanValue("isTrend", atts);
		state.addVerticallyScaleProperty(series, isScaleButtonVisible, isRight,
				x, y, width, height, foreground, background, isTrend);
	}

	private int getSeries(Attributes atts) {
		String seriesStr = AttributesUtil.getValue("series", atts);
		if (seriesStr == null) {
			throw new IllegalArgumentException("series is null.");
		}
		return Integer.parseInt(seriesStr);
	}

	private boolean getAlign(Attributes atts) {
		String align = AttributesUtil.getValue("align", atts);
		return !"left".equalsIgnoreCase(align);
	}

	private Color getForeground(Attributes atts) {
		Color color = ColorFactory.getColor(AttributesUtil.getValue("foreground", atts));
		return color == null ? Color.BLACK : color;
	}
	
	private Color getBackground(Attributes atts) {
		Color color = ColorFactory.getColor(AttributesUtil.getValue("background", atts));
		return color == null ? ColorFactory.getColor("gray") : color;
	}

	public void add(String tagName, Attributes atts, Stack stack) {
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("verticallyscale")) {
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}
}
