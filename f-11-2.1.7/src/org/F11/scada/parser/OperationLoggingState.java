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
package org.F11.scada.parser;

import java.util.Stack;

import org.F11.scada.applet.operationlog.OperationLogging;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * @author hori
 */
public class OperationLoggingState implements State {
	private static Logger logger = Logger.getLogger(OperationLoggingState.class);

	/**
	 * 
	 */
	public OperationLoggingState(String tagName, Attributes atts, SymbolContainerState state) {
		logger = Logger.getLogger(getClass());
		OperationLogging operationLogging = new OperationLogging();

		String loc_x = atts.getValue("x");
		String loc_y = atts.getValue("y");
		if (loc_x != null && loc_y != null) {
		    operationLogging.setLocation(Integer.parseInt(loc_x), Integer.parseInt(loc_y));
		}
		loc_x = atts.getValue("width");
		loc_y = atts.getValue("height");
		if (loc_x != null && loc_y != null) {
		    operationLogging.setSize(Integer.parseInt(loc_x), Integer.parseInt(loc_y));
		}

		state.addPageSymbol(operationLogging);
	}

	public void add(String tagName, Attributes atts, Stack stack) {
	    if (logger.isDebugEnabled())
	        logger.debug("tagName:" + tagName);
	}

	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("operationlogging")) {
			stack.pop();

		} else {
			logger.debug("tagName:" + tagName);
		}
	}

}
