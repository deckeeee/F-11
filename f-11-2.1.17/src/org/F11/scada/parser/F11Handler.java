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
package org.F11.scada.parser;

import java.util.Map;
import java.util.Stack;

import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.xwife.applet.PageChanger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * F11の定義ファイルを解析する、SAXパーサーハンドラークラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class F11Handler extends DefaultHandler {
	private PagemapState pageMapState;
	private Stack stack = new Stack();

	private Authenticationable authenticationable;
	private PageChanger changer;
	/** ページジャンプボタンのパラメータ */
	private Object argv;

	/**
	 * SAXパーサーハンドラーオブジェクトを生成します。
	 * 
	 * @param authenticationable ユーザー認証オブジェクト
	 */
	public F11Handler(
			Authenticationable authenticationable,
			PageChanger changer,
			Object argv) {
		this.authenticationable = authenticationable;
		this.changer = changer;
		this.argv = argv;
		pageMapState = new PagemapState(this.authenticationable, this.changer, this.argv);
	}

	/**
	 * SAXパーサーハンドラーオブジェクトを生成します。
	 */
	public F11Handler() {
		this(null, null, null);
	}

	public void startDocument() throws SAXException {
		stack.push(pageMapState);
	}

	public void startElement(
			String uri,
			String name,
			String qualifiedName,
			Attributes attributes) throws SAXException {
		if (name.equals("page_map")) {
			return;
		}
		State state = (State) stack.peek();
		state.add(name, attributes, stack);
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("page_map")) {
			return;
		}
		State state = (State) stack.peek();
		state.end(localName, stack);
	}

	/**
	 * ベースページオブジェクトを返します。
	 * 
	 * @param key ページID
	 * @return ベースページオブジェクト
	 */
	public Map getItemMap() {
		return pageMapState.getItemMap();
	}
}
