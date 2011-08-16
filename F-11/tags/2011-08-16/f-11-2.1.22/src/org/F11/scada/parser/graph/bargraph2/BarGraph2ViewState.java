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

import java.util.Stack;

import org.F11.scada.applet.graph.bargraph2.BarGraph2View;
import org.F11.scada.applet.graph.bargraph2.BarGraphModel;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

public class BarGraph2ViewState implements State {
	private static Logger logger = Logger.getLogger(BarGraph2ViewState.class);

	private BarGraphModel model;
	private BarGraph2View view;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public BarGraph2ViewState(String tagName, Attributes atts,
			BarGraphModel model) {
		this.model = model;
		view = new BarGraph2View(model);
		String x = atts.getValue("x");
		String y = atts.getValue("y");
		String width = atts.getValue("width");
		String height = atts.getValue("height");
		if (!isNullstring(x) && !isNullstring(y)) {
			view.setLocation(Integer.parseInt(x), Integer.parseInt(y));
		}
		if (!isNullstring(width) && !isNullstring(height)) {
			view.setSize(Integer.parseInt(width), Integer.parseInt(height));
		}
	}

	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("color")) {
			stack.push(new ColorState(tagName, atts, view));
		} else if (tagName.equals("focus")) {
			stack.push(new BarFocusState(tagName, atts, view,
					model.getBarCount()));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}
	public void end(String tagName, Stack stack) {
		if (tagName.equals("graphview")) {
			model.setBarGraph2View(view);
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	private boolean isNullstring(String s) {
		return (s == null || "".equals(s));
	}

}
