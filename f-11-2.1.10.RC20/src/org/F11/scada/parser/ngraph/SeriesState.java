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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.F11.scada.applet.ngraph.SeriesGroup;
import org.F11.scada.applet.ngraph.SeriesProperties;
import org.F11.scada.parser.State;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

public class SeriesState implements State {
	private static Logger logger = Logger.getLogger(SeriesState.class);
	final TrendGraph3State state;
	String groupName;
	List<SeriesProperties> seriesProperties;

	public SeriesState(String tagName, Attributes atts, TrendGraph3State state) {
		this.state = state;
		seriesProperties = new ArrayList<SeriesProperties>();
		groupName = atts.getValue("groupName");
	}

	public void add(String tagName, Attributes atts, Stack stack) {
		if (tagName.equals("series-property")) {
			stack.push(new SeriesPropertyState(tagName, atts, this));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	public void end(String tagName, Stack stack) {
		SeriesGroup sg = new SeriesGroup(groupName, seriesProperties);
		state.seriesGroups.add(sg);
		if (tagName.equals("series")) {
			stack.pop();
		}
	}
}
