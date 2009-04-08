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

import org.F11.scada.applet.ngraph.SeriesProperties;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.State;
import org.F11.scada.server.register.HolderString;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

public class SeriesPropertyState implements State {
	private static Logger logger = Logger.getLogger(SeriesPropertyState.class);
	public SeriesPropertyState(
			String tagName,
			Attributes atts,
			SeriesState state) {
		state.seriesProperties.add(getSeriesProperties(atts));
	}

	private SeriesProperties getSeriesProperties(Attributes atts) {
		SeriesProperties sp = new SeriesProperties();
		sp.setIndex(Integer.parseInt(atts.getValue("index")));
		sp.setVisible(Boolean.valueOf(atts.getValue("visible")));
		sp.setColor(ColorFactory.getColor(atts.getValue("color")));
		sp.setUnit(atts.getValue("unit"));
		sp.setName(atts.getValue("name"));
		sp.setUnitMark(atts.getValue("unitMark"));
		sp.setVerticalFormat(atts.getValue("verticalFormat"));
		sp.setMax(Float.valueOf(atts.getValue("max")));
		sp.setMin(Float.valueOf(atts.getValue("min")));
		sp.setHolderString(new HolderString(atts.getValue("holder")));
		return sp;
	}

	public void add(String tagName, Attributes atts, Stack stack) {
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("series-property")) {
			stack.pop();
		}
	}

}
