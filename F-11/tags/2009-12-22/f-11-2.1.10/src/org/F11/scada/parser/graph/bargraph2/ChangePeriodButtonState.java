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

import org.F11.scada.applet.graph.bargraph2.ChangePeriodButtonSymbol;
import org.F11.scada.applet.graph.bargraph2.ReferenceDate;
import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * @author hori
 */
public class ChangePeriodButtonState implements State {
	private static Logger logger = Logger.getLogger(ChangePeriodButtonState.class);

	ReferenceDate refDate;
	ChangePeriodButtonSymbol button;

	/**
	 * 
	 */
	public ChangePeriodButtonState(String tagName, Attributes atts,
			ReferenceDate refDate) {
		this.refDate = refDate;
		SymbolProperty prop = new SymbolProperty(atts);
		button = new ChangePeriodButtonSymbol(prop);
	}

	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
	}

	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("changeperiodbutton")) {
			refDate.addChangePeriodButtonSymbol(button);
			stack.pop();
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

}