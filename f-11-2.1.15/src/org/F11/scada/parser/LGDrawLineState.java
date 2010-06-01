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

import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.applet.symbol.LineGraphSymbol.LinePath;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * @author hori
 */
public class LGDrawLineState implements State {
	private static Logger logger;

	LinePath linePath;
	LineGraphSymbolState state;
	/**
	 * 
	 */
	public LGDrawLineState(String tagName, Attributes atts, LineGraphSymbolState state) {
		logger = Logger.getLogger(getClass());

		this.state = state;
		if (tagName.equals("drawline")) {
			SymbolProperty property = new SymbolProperty(atts);
			linePath = new LinePath(state.property, property);
			linePath.addLine(atts.getValue("start_x"), atts.getValue("start_y"));
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/* (non-Javadoc)
	 * @see org.F11.scada.parser.State#add(java.lang.String, org.xml.sax.Attributes, java.util.Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (tagName.equals("lineto")) {
			stack.push(new LGLineToState(tagName, atts, this));

		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/* (non-Javadoc)
	 * @see org.F11.scada.parser.State#end(java.lang.String, java.util.Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (tagName.equals("drawline")) {
			state.lineGraphSymbol.addLinePath(linePath);
			stack.pop();
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

}
