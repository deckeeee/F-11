/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/logging/column/ColumnState.java,v 1.2.6.1 2006/02/16 04:59:02 frdm Exp $
 * $Revision: 1.2.6.1 $
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
import java.util.Stack;

import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ColumnState implements State {
	private static Logger logger;
	TaskState state;

	/**
	 * Constructor for ColumnState.
	 */
	public ColumnState(String tagName, Attributes atts, TaskState state) {
		super();
		this.state = state;
		logger = Logger.getLogger(getClass().getName());
		
		if (atts.getValue("index") == null) {
			throw new IllegalArgumentException("index is null.");
		}
		Integer value = Integer.valueOf(atts.getValue("index"));
	
		if (atts.getValue("provider") == null) {
			throw new IllegalArgumentException("provider is null.");
		}
		String provider = atts.getValue("provider");
		
		if (atts.getValue("holder") == null) {
			throw new IllegalArgumentException("holder is null.");
		}
		String holder = atts.getValue("holder");

		if (state.columnMap == null) {
			state.columnMap = new HashMap();
		}
		state.columnMap.put(provider + "_" + holder, value);
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		stack.pop();
	}

}
