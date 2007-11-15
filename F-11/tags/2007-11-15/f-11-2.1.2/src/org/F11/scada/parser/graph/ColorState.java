/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/graph/ColorState.java,v 1.3.6.1 2006/02/16 04:59:01 frdm Exp $
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
package org.F11.scada.parser.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/trendgraph/graphproperty/color 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ColorState implements State {
	private static Logger logger;
	
	GraphPropertyModelState state;

	List colorList;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public ColorState(String tagName, Attributes atts, GraphPropertyModelState state) {
		logger = Logger.getLogger(getClass().getName());
		
		this.state = state;
		colorList = new ArrayList();
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("name")) {
			stack.push(new ColorNameState(tagName, atts, this));
		} else {
			logger.info("tagName : " + tagName);
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {	
		if (tagName.equals("color")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Pop : " + DisplayState.toString(tagName, stack));
			}
			state.colors = (Color[]) colorList.toArray(new Color[0]);
			stack.pop();
		} else {
			logger.info("tagName : " + tagName);
		}
	}

}
