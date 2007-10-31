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

package org.F11.scada.server.deploy;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.WifeUtilities;
import org.F11.scada.parser.AppletFrameDefine;
import org.F11.scada.server.frame.PageDefine;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * ページ定義XMLとPageDefineクラスに関するユーティリティークラスです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageDefineUtil {
	/**
	 * ストリームで渡されたXMLをパースして、ページ名称とページ定義オブジェクトのマップを生成して返します
	 * @param in XMLのストリーム
	 * @return ページ名称とページ定義オブジェクトのマップ
	 * @throws IOException 入出力エラー発生時
	 */
	public static Map parse(InputStream in) throws IOException, SAXException {
		PageHandler handler = new PageHandler();
		// パース
		XMLReader parser =
			XMLReaderFactory.createXMLReader(EnvironmentManager.get("/org.xml.sax.driver", ""));
		parser.setContentHandler(handler);
		InputSource is = new InputSource(in);
		parser.parse(is);
		return handler.getPageMap();
	}


	/**
	 * ページ定義XMLをパースして、ページ名称とページ定義オブジェクトのマップを生成する、SAXハンドラクラスです
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	static class PageHandler extends DefaultHandler {
		/** ページ名 */
		private String pageName;
		/** ページ定義XML文字列 */
		private StringBuffer page;
		/** ページ名称とページ定義オブジェクトのマップです */
		private Map pages;

		/**
		 * コンストラクタ
		 */
		public PageHandler() {
			super();
			pages = new HashMap();
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {

			if (localName.equals("page_map")) {
				return;
			} else if (localName.equals("page")) {
				pageName = attributes.getValue("name");
				page = new StringBuffer();
				page.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				page.append("<f11:page_map xmlns:f11=\"http://www.F-11.org/scada\">");
			} else if (localName.equals("statusbar")) {
				pageName = AppletFrameDefine.ITEM_KEY_STATUSBAR;
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
			if (localName.equals("page") || localName.equals("statusbar")) {
				page.append("</f11:page_map>");
				
				pages.put(pageName, new PageDefine(System.currentTimeMillis(), page.toString()));
			
				pageName = null;
				page = null;
			}
		}
		
		/**
		 * パース結果より生成されたページ名称とページ定義オブジェクトのマップを返します
		 * @return ページ名称とページ定義オブジェクトのマップを返します
		 */
		public Map getPageMap() {
			return new HashMap(pages);
		}
	}
}
