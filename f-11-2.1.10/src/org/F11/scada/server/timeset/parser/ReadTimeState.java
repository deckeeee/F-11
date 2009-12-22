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
package org.F11.scada.server.timeset.parser;

import java.util.Stack;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * @author hori
 */
public class ReadTimeState implements State {
	private static Logger logger;
	/**
	 * 
	 */
	public ReadTimeState(String tagName, Attributes atts, TimeSetTaskState state) {
		logger = Logger.getLogger(getClass().getName());
		state.setProviderName(atts.getValue("provider"));
		state.setHolderName(atts.getValue("holder"));
		DataHolder dh = Manager.getInstance().findDataHolder(state.providerName, state.holderName);
		if (dh == null)
			throw new NullPointerException(
				"dataholder is null ["
					+ "provider:"
					+ state.providerName
					+ " holder:"
					+ state.holderName
					+ "]");
	}

	/* (non-Javadoc)
	 * @see org.F11.scada.parser.State#add(java.lang.String, org.xml.sax.Attributes, java.util.Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
	}

	/* (non-Javadoc)
	 * @see org.F11.scada.parser.State#end(java.lang.String, java.util.Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		stack.pop();
	}

}
