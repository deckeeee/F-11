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

import org.F11.scada.applet.graph.bargraph2.BarGraphModel;
import org.F11.scada.applet.graph.bargraph2.VerticallyScaleModel;
import org.F11.scada.applet.graph.bargraph2.VerticallyScaleModelImpl;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.util.AttributesUtil;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

public class VerticallyScaleModelState implements State {
	private static Logger logger = Logger
		.getLogger(VerticallyScaleModelState.class);

	private BarGraphModel model;
	private VerticallyScaleModel vartical;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public VerticallyScaleModelState(String tagName,
			Attributes atts,
			BarGraphModel model) {
		this.model = model;
		vartical = new VerticallyScaleModelImpl();
		vartical.setMin(AttributesUtil.getDoubleValue(atts.getValue("min"), 0));
		vartical.setMax(AttributesUtil.getDoubleValue(
			atts.getValue("max"),
			Long.MAX_VALUE));
		vartical.setLimiton(AttributesUtil.getBooleanValue("limiton", atts));
	}

	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("textsymbol")) {
			stack.push(new TextVerticalSymbolState(tagName, atts, vartical));
		} else if (tagName.equals("scalechangebutton")) {
			stack
				.push(new ScaleChangeButtonSymbolState(tagName, atts, vartical));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("verticalscale")) {
			model.setVerticallyScaleModel(vartical);
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

}
