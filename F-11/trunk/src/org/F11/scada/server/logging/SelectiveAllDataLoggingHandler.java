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
package org.F11.scada.server.logging;

import java.util.Stack;

import org.F11.scada.parser.State;
import org.F11.scada.server.io.SelectiveAllDataValueListHandlerManager;
import org.F11.scada.server.logging.parser.SelectiveAllDataLoggingState;
import org.F11.scada.util.RmiUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class SelectiveAllDataLoggingHandler extends AbstractLoggingHandler {
	private SelectiveAllDataValueListHandlerManager handlerManager;

	/**
	 * SAXハンドラクラスを生成します。
	 */
	public SelectiveAllDataLoggingHandler(
			SelectiveAllDataValueListHandlerManager handlerManager) {
		super();
		stack = new Stack();
		this.handlerManager = handlerManager;
		RmiUtil.registryServer(this, getClass());
	}

	public void startElement(
			String uri,
			String name,
			String qualifiedName,
			Attributes attributes) throws SAXException {
		if (name.equals("logging")) {
			stack.push(new SelectiveAllDataLoggingState(handlerManager, this));
		} else {
			State state = (State) stack.peek();
			state.add(name, attributes, stack);
		}
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		State state = (State) stack.peek();
		state.end(localName, stack);
	}
}
