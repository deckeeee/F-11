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
package org.F11.scada.parser.graph.bargraph2;

import java.util.Stack;

import org.F11.scada.applet.graph.bargraph2.BarGraph2;
import org.F11.scada.applet.graph.bargraph2.BarSeries;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

public class BarSeriesState implements State {
	private static Logger logger = Logger.getLogger(BarSeriesState.class);

	BarGraph2 bargraph2;
	BarSeries series;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public BarSeriesState(String tagName, Attributes atts, BarGraph2 bargraph2) {
		this.bargraph2 = bargraph2;
		series = new BarSeries();
		series.setName(atts.getValue("name"));
		series.setUnit_mark(atts.getValue("unitmark"));
	}

	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("barprop")) {
			stack.push(new PointPropertyState(tagName, atts, series));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("barseries")) {
			bargraph2.addBarSeries(series);
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);

		}
	}

}
