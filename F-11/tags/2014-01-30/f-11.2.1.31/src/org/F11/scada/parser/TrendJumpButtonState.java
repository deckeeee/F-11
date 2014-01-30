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
import org.F11.scada.applet.symbol.TrendJumpButton;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * @author hori
 */
public class TrendJumpButtonState implements State {
	private final Logger logger = Logger.getLogger(TrendJumpButtonState.class);

	/**
	 * 
	 */
	public TrendJumpButtonState(
			String tagName,
			Attributes atts,
			SymbolContainerState state) {
		SymbolProperty prop = new SymbolProperty(atts);
		TrendJumpButton trendJumpButton = new TrendJumpButton(prop, state.getPageChanger());
		trendJumpButton.updateProperty();
		state.addPageSymbol(trendJumpButton);
	}

	public void add(String tagName, Attributes atts, Stack stack) {
		logger.info("tagName:" + tagName);
	}

	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("trendjumpbutton")) {
			stack.pop();

		} else {
			logger.debug("tagName:" + tagName);
		}
	}

}
