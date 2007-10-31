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

package org.F11.scada.server.logging.parser;

import java.util.Stack;

import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.server.register.HolderString;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * xpath /logging/task/column 状態を表すクラスです。
 * @author Hideaki Maekawa <h@f-11.org>
 */
public class ColumnState implements State {
	private static Logger logger;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public ColumnState(String tagName, Attributes atts, TaskStateble taskState) {
		logger = Logger.getLogger(getClass().getName());

		String provider = atts.getValue("provider");
		String holder = atts.getValue("holder");
/*
		DataHolder dh = Manager.getInstance().findDataHolder(provider, holder);
		if (dh == null)
			throw new NullPointerException(
				"dataholder is null ["
					+ "provider:"
					+ provider
					+ " holder:"
					+ holder
					+ "]");
		taskState.dataHolders.add(dh);
*/
		HolderString hs = new HolderString();
		hs.setProvider(provider);
		hs.setHolder(holder);
		taskState.add(hs);
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
