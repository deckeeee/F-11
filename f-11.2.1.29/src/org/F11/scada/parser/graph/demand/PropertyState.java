/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/graph/demand/PropertyState.java,v 1.3.6.1 2006/02/16 04:59:01 frdm Exp $
 * $Revision: 1.3.6.1 $
 * $Date: 2006/02/16 04:59:01 $
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
package org.F11.scada.parser.graph.demand;

import java.util.Stack;

import jp.gr.javacons.jim.DataReferencer;

import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/demandgraph/graphmodel/property 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PropertyState implements State {
	private static Logger logger;

	GraphModelState state;

	/**
	 * 状態オブジェクトを生成します。
	 */
	public PropertyState(String tagName, Attributes atts, GraphModelState state) {
		this.state = state;
		logger = Logger.getLogger(getClass().getName());
		
		String value = atts.getValue("value");
		if (value != null) {
			int p = value.indexOf('_');
			if (0 < p) {
				state.refList.add(
					new DataReferencer(
						value.substring(0, p),
						value.substring(p + 1)));
			}
		} else {
			throw new IllegalArgumentException("attribute \"value\" is null.");
		}
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
