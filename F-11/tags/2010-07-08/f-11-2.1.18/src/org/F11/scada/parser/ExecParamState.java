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

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/SYMBOL/destination/element 状態を表すクラスです。
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class ExecParamState implements State {
	private static Logger logger;

	/**
	 * Constructor for ElementState.
	 */
	public ExecParamState(String tagName, Attributes atts, ProgramExecuteButtonState state) {
		logger = Logger.getLogger(getClass());

		if (tagName.equals("execparam")) {
			logger.debug("Element:" + atts.getValue(0));
			
			state.execButton.addExecParam(atts.getValue("value"));
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/**
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
	}

	/**
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		stack.pop();
	}
}
