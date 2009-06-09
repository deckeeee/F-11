/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/parser/dialog/F11DialogHandler.java,v 1.1.6.1 2005/07/06 02:20:55 frdm Exp $
 * $Revision: 1.1.6.1 $
 * $Date: 2005/07/06 02:20:55 $
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
package org.F11.scada.applet.parser.dialog;

import java.awt.Window;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.F11.scada.parser.State;
import org.F11.scada.xwife.applet.PageChanger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ダイアログ定義を解析する、SAXハンドラークラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class F11DialogHandler extends DefaultHandler {
	Window window;
	Map dialogs;
	PageChanger changer;

	private Stack stack;

	/**
	 * Constructor for F11DialogHandler.
	 * 
	 * @param window ダイアログの親ウィンドゥの参照
	 */
	public F11DialogHandler(Window window) {
		super();
		this.window = window;
		stack = new Stack();
		dialogs = new HashMap();
	}

	public F11DialogHandler(Window window, PageChanger changer) {
		super();
		this.window = window;
		this.changer = changer;
		stack = new Stack();
		dialogs = new HashMap();
	}

	public void startElement(
			String uri,
			String name,
			String qualifiedName,
			Attributes attributes) throws SAXException {
		if (name.equals("dialogmap")) {
			stack.push(new DialogMapState(this));
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

	/**
	 * 定義ファイルより作成したダイアログの Map インスタンスを返します。
	 * 
	 * @return ダイアログオブジェクト
	 */
	public Map getDialogs() {
		return dialogs;
	}
}
