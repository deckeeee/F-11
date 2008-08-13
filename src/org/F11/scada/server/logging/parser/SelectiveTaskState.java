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
 */
package org.F11.scada.server.logging.parser;

import static org.F11.scada.util.AttributesUtil.getTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.server.io.HandlerFactory;
import org.F11.scada.server.io.SelectiveValueListHandlerElement;
import org.F11.scada.util.AttributesUtil;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class SelectiveTaskState implements State, TaskStateble {
	private static Logger logger;

	String name;
	List dataHolders;

	SelectiveLoggingState state;
	private String factoryName;
	private String tables;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public SelectiveTaskState(
			String tagName,
			Attributes atts,
			SelectiveLoggingState state) {
		logger = Logger.getLogger(getClass().getName());
		name = atts.getValue("name");
		if (name == null) {
			throw new IllegalArgumentException("name is null");
		}
		factoryName = atts.getValue("factoryName");
		if (factoryName == null) {
			throw new IllegalArgumentException("factoryName is null");
		}

		dataHolders = new ArrayList();
		this.state = state;
		tables = AttributesUtil.getNonNullString(atts.getValue("tables"));
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
			// stack.push(new CsvoutTaskState(tagName, atts, this));
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
		if (tagName.equals("task")) {
			HandlerFactory factory =
				HandlerFactory.getHandlerFactory(factoryName);
			SelectiveValueListHandlerElement element =
				factory.createSelectviveHandler(name, getTables(tables));
			state.handlerManager.addValueListHandlerElement(name, element);
			stack.pop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.logging.parser.TaskStateble#add(java.lang.Object)
	 */
	public void add(Object obj) {
		dataHolders.add(obj);
	}
}
