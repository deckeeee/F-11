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
package org.F11.scada.server.frame;

import org.F11.scada.WifeUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * �y�[�W��`��XML��ǂݍ��ރn���h���ł��B
 * �p�[�X���ʂ�FrameDefineManager�ɓo�^���܂��B
 * @author hori
 */
public class F11PageHandler extends DefaultHandler {
	/** �y�[�W��`�}�l�[�W���[�̎Q�� */
	private FrameDefine parent;
	/** �y�[�W�� */
	private String pageName;
	/** �y�[�W��`XML������ */
	private StringBuffer page;

	/**
	 * �R���X�g���N�^
	 * @param parent �y�[�W��`�}�l�[�W���[
	 */
	public F11PageHandler(FrameDefine parent) {
		super();
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes)
		throws SAXException {
		if (localName.equals("page_map")) {
			return;
		} else if (localName.equals("page") || localName.equals("statusbar")) {
			pageName = attributes.getValue("name");
			page = new StringBuffer();
			page.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			page.append("<f11:page_map xmlns:f11=\"http://www.F-11.org/scada\">");
		}
		page.append("<").append(qName);
		for (int i = 0; i < attributes.getLength(); i++) {
			page.append(" ").append(attributes.getQName(i)).append("=\"");
			page.append(WifeUtilities.htmlEscape(attributes.getValue(i))).append("\"");
		}
		page.append(">");
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("page_map")) {
			return;
		}
		page.append("</").append(qName).append(">");
		if (localName.equals("page")) {
			page.append("</f11:page_map>");
			
			parent.setPageString(pageName, page.toString());
			
			pageName = null;
			page = null;
		} else if (localName.equals("statusbar")) {
			page.append("</f11:page_map>");
			
			parent.setStatusbar(page.toString());
			
			pageName = null;
			page = null;
		}
	}
}
