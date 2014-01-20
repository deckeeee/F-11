/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/logging/column/XMLHandler.java,v 1.2.6.1 2005/07/06 02:20:59 frdm Exp $
 * $Revision: 1.2.6.1 $
 * $Date: 2005/07/06 02:20:59 $
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

import java.util.Stack;

import org.F11.scada.parser.State;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ロギング定義ファイル(XML)をパースし、列管理マップを生成するハンドラークラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class XMLHandler extends DefaultHandler {
	private Stack stack;

	ColumnManager manager;

	/**
	 * Constructor for XMLHandler.
	 */
	public XMLHandler(ColumnManager manager) {
		super();
		this.manager = manager;
		stack = new Stack();
	}
	
	public void startElement(
			String uri,
			String name,
			String qualifiedName,
			Attributes attributes)
			throws SAXException {
		if (name.equals("logging")) {
			stack.push(new LoggingState(this));
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
