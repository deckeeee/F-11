/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.tool.conf.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AlarmDefineStream {
	private static final Logger log = Logger.getLogger(AlarmDefineStream.class);
	private Document document;
	private boolean edited = false;

	public String getData(String key, String def) {
		int p = key.lastIndexOf('/');
		String attrname = key.substring(p + 1);
		StringTokenizer st = new StringTokenizer(key, "/");
		if (!"alarm".equals(st.nextToken()) || !st.hasMoreTokens()) {
			throw new IllegalArgumentException(key);
		}
		Element n = getChildElement(document.getDocumentElement(), st);
		String ret = n.getAttribute(attrname);
		if (ret == null || ret.length() <= 0)
			return def;
		return ret;
	}

	public void putData(String key, String value) {
		int p = key.lastIndexOf('/');
		String attrname = key.substring(p + 1);
		StringTokenizer st = new StringTokenizer(key, "/");
		if (!"alarm".equals(st.nextToken()) || !st.hasMoreTokens()) {
			throw new IllegalArgumentException(key);
		}
		Element n = getChildElement(document.getDocumentElement(), st);
		log.debug("put" + key + " " + value);
		n.setAttribute(attrname, value);
		edited = true;
	}

	private Element getChildElement(Element n, StringTokenizer st) {
		String name = st.nextToken();
		if (!st.hasMoreTokens()) {
			return n;
		}
		NodeList childs = n.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element el = (Element) childs.item(i);
				if (name.equals(el.getNodeName())) {
					return getChildElement(el, st);
				}
			}
		}
		Element el = n.getOwnerDocument().createElement(name);
		n.appendChild(el);
		return el;
	}

	public void load(String path) throws ParserConfigurationException,
			IOException, SAXException {
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbfactory.newDocumentBuilder();
		document = builder.parse(new File(path));
		log.debug("load[" + path + "]");
	}

	public void save(String path) throws IOException {
		if (!edited)
			return;
		log.debug("save[" + path + "]");
		// 保存処理
		Charset cs = Charset.forName("Windows-31J");
		PrintWriter pw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(path), cs)));
		pw.print("<?xml version=\"1.0\" encoding=\"");
		pw.print(cs.name());
		pw.println("\"?>");
		pw.println("<alarm>");
		pw.print("\t<news backGround=\"");
		pw.print(getData("/alarm/news/backGround", "WHITE").toUpperCase());
		pw.println("\">");
		pw.print("\t\t<font type=\"");
		pw.print(getData("/alarm/news/font/type", "Monospaced"));
		pw.print("\" style=\"");
		pw.print(getData("/alarm/news/font/style", "PLAIN").toUpperCase());
		pw.print("\" point=\"");
		pw.print(getData("/alarm/news/font/point", "18"));
		pw.println("\" />");
		pw.print("\t\t<linecount value=\"");
		pw.print(getData("/alarm/news/linecount/value", "5"));
		pw.println("\" />");
		pw.println("\t</news>");
		pw.print("\t<table backGround=\"");
		pw.print(getData("/alarm/table/backGround", "WHITE").toUpperCase());
		pw.print("\" headerBackGround=\"");
		pw.print(getData("/alarm/table/headerBackGround", "SILVER")
				.toUpperCase());
		pw.print("\" headerForeGround=\"");
		pw.print(getData("/alarm/table/headerForeGround", "BLACK")
				.toUpperCase());
		pw.print("\" defaultTabNo=\"");
		pw.print(getData("/alarm/table/defaultTabNo", "1"));
		pw.println("\">");
		pw.print("\t\t<font type=\"");
		pw.print(getData("/alarm/table/font/type", "Monospaced"));
		pw.print("\" style=\"");
		pw.print(getData("/alarm/table/font/style", "PLAIN").toUpperCase());
		pw.print("\" point=\"");
		pw.print(getData("/alarm/table/font/point", "14"));
		pw.println("\" />");
		pw.print("\t\t<linecount value=\"");
		pw.print(getData("/alarm/table/linecount/value", "20"));
		pw.println("\" />");
		pw.println("\t</table>");
		pw.print("\t<server message=\"");
		pw.print(getData("/alarm/server/message", "サーバーコネクションエラー"));
		pw.print("\" sound=\"");
		pw.print(getData("/alarm/server/sound", ""));
		pw.println("\" />");
		pw.print("\t<toolbar displayLogin=\"");
		pw.print(getData("/alarm/toolbar/displayLogin", "true"));
		pw.println("\" />");
		pw.print("\t<init initPage=\"");
		pw.print(getData("/alarm/init/initPage", ""));
		pw.println("\" />");
		pw.print("\t<title text=\"");
		pw.print(getData("/alarm/title/text", "F-11"));
		pw.print("\" image=\"");
		pw.print(getData("/alarm/title/image", ""));
		pw.println("\" />");
		pw.println("</alarm>");
		pw.close();
		edited = false;
	}
}
