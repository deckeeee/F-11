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
 * F11�̒�`�t�@�C������͂���ASAX�p�[�T�[�n���h���[�N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class F11Handler extends DefaultHandler {
	private PagemapState pageMapState;
	private Stack stack = new Stack();

	private Authenticationable authenticationable;
	private PageChanger changer;
	/** �y�[�W�W�����v�{�^���̃p�����[�^ */
	private Object argv;

	/**
	 * SAX�p�[�T�[�n���h���[�I�u�W�F�N�g�𐶐����܂��B
	 * 
	 * @param authenticationable ���[�U�[�F�؃I�u�W�F�N�g
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
	 * SAX�p�[�T�[�n���h���[�I�u�W�F�N�g�𐶐����܂��B
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
	 * �x�[�X�y�[�W�I�u�W�F�N�g��Ԃ��܂��B
	 * 
	 * @param key �y�[�WID
	 * @return �x�[�X�y�[�W�I�u�W�F�N�g
	 */
	public Map getItemMap() {
		return pageMapState.getItemMap();
	}
}
