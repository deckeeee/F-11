/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/logging/column/TaskState.java,v 1.3.4.1 2006/02/16 04:59:02 frdm Exp $
 * $Revision: 1.3.4.1 $
 * $Date: 2006/02/16 04:59:02 $
 * 
 * =============================================================================
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
package org.F11.scada.server.logging.column;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TaskState implements State {
	private static Logger logger;
	LoggingState state;
	Map columnMap;
	
	private String taskName;

	/**
	 * Constructor for TaskState.
	 */
	public TaskState(String tagName, Attributes atts, LoggingState state) {
		super();
		this.state = state;
		logger = Logger.getLogger(getClass().getName());

		if (atts.getValue("name") != null) {
			this.taskName = atts.getValue("name");
		} else {
			throw new IllegalArgumentException("atribute \"name\" is null.");
		}

		if (state.handler.manager.taskMap == null) {
			state.handler.manager.taskMap = new HashMap();
		}
	}
	
	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("column")) {
			stack.push(new ColumnState(tagName, atts, this));
		} else if (tagName.equals("csvout")) {
			stack.push(new DummyState(tagName, atts, this));
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		state.handler.manager.taskMap.put(taskName, columnMap);
		stack.pop();
	}

}
