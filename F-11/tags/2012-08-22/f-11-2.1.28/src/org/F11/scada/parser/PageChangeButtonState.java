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

import org.F11.scada.applet.symbol.PageChangeButton;
import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * @author hori
 */
public class PageChangeButtonState implements State {
	private static Logger logger;

	/**
	 * 
	 */
	public PageChangeButtonState(String tagName, Attributes atts, SymbolContainerState state) {
		logger = Logger.getLogger(getClass());
		SymbolProperty prop = new SymbolProperty(atts);
		state.addPageSymbol(new PageChangeButton(prop, state.getPageChanger()));
	}

	public void add(String tagName, Attributes atts, Stack stack) {
		logger.info("tagName:" + tagName);
	}

	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("pagechangebutton")) {
			stack.pop();

		} else {
			logger.debug("tagName:" + tagName);
		}
	}

}
