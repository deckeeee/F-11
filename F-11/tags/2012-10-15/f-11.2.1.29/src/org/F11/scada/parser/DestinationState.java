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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.F11.scada.applet.symbol.Editable;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/SYMBOL/destination 状態を表すクラスです。
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class DestinationState implements State {
	private static Logger logger;
	private Editable symbolEditable;

	private boolean notAddElement = true;

	public DestinationState(String tagName, Attributes atts, Editable symbolEditable) {
		logger = Logger.getLogger(getClass());
		this.symbolEditable = symbolEditable;
		if (tagName.equals("destination")) {
			logger.debug("Distination : " + atts.getValue(0));

			Map params = new HashMap();
			for (int i = 0; i < atts.getLength(); i++)
				params.put(atts.getQName(i), atts.getValue(i));
			symbolEditable.addDestination(params);
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/**
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (ElementState.isSupport(tagName)) {
			stack.push(new ElementState(tagName, atts, symbolEditable));
			notAddElement = false;
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/**
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		stack.pop();
		logger.debug("destination Stack Poped.");
		if (notAddElement) {
			throw new IllegalArgumentException(tagName + " : Not Add Element.");
		}
	}
}
