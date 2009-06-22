/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/graph/GraphModelState.java,v 1.3.6.5 2006/02/16 04:59:01 frdm Exp $
 * $Revision: 1.3.6.5 $
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

import java.util.Stack;

import org.F11.scada.applet.graph.GraphModel;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.util.AttributesUtil;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/trendgraph/graphmodel 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class GraphModelState implements State, HandlerStateable {
	private static Logger logger;
	
	private GraphState graphState;
	private Class modelClass;
	private String[] handlerNames;
	private int maxMapSize = -1;

	/**
	 * 状態オブジェクトを生成します。
	 */
	public GraphModelState(String tagName, Attributes atts, GraphState graphState) {
		this.graphState = graphState;
		logger = Logger.getLogger(getClass().getName());
		
		String className = atts.getValue("class");
		if (className == null) {
			throw new IllegalArgumentException("attribute \"class\" is null.");
		}

		try {
			modelClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new NoClassDefFoundError(e.getMessage());
		}
		String maxMapSizeStr = AttributesUtil.getValue("maxMapSize", atts);
		if (maxMapSizeStr != null) {
			maxMapSize = Integer.parseInt(maxMapSizeStr);
		}
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("handler")) {
			stack.push(new HandlerState(tagName, atts, this));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (tagName.equals("graphmodel")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Pop : " + DisplayState.toString(tagName, stack));
			}
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	private void setGraphModel(GraphModel graphModel) {
		logger.info("graphModel" + graphModel);
		graphState.setGraphModel(graphModel);
	}

	/**
	 * ハンドラー名を設定します。
	 * @param handlerNames ハンドラー名の配列
	 * @see org.F11.scada.parser.graph.HandlerStateable#setHandlerName(String[])
	 */
	public void setHandlerName(String[] handlerNames) {
		this.handlerNames = handlerNames;
	}
}
