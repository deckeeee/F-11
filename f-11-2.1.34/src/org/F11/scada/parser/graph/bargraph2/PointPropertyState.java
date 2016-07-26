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

import org.F11.scada.applet.graph.bargraph2.BarSeries;
import org.F11.scada.applet.graph.bargraph2.PointProperty;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

public class PointPropertyState implements State {
	private static Logger logger = Logger.getLogger(PointPropertyState.class);

	BarSeries series;
	PointProperty property;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public PointPropertyState(String tagName, Attributes atts, BarSeries series) {
		this.series = series;
		property = new PointProperty();
		property.setMinimums(Double.parseDouble(atts.getValue("minimums")));
		property.setMaximums(Double.parseDouble(atts.getValue("maximums")));
		String[] ret = split(atts.getValue("holder"));
		property.setProviderName(ret[0]);
		property.setHolderName(ret[1]);
		if (atts.getValue("nowvalue") != null) {
			ret = split(atts.getValue("nowvalue"));
			property.setNowValueProviderName(ret[0]);
			property.setNowValueHolderName(ret[1]);
		}
	}

	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("barprop")) {
			series.addPointProperty(property);
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	private boolean isNullstring(String s) {
		return (s == null || "".equals(s));
	}

	private String[] split(String value) {
		String[] ret = new String[2];
		logger.debug("split : " + value);
		ret[0] = value.substring(0, value.indexOf("_"));
		ret[1] = value.substring(value.indexOf("_") + 1);
		return ret;
	}
}
