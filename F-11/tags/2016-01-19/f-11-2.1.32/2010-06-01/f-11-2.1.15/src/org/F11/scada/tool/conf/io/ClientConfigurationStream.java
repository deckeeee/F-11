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
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.F11.scada.util.JavaVersion;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ClientConfigurationStream {
	private static final Logger log = Logger
			.getLogger(ClientConfigurationStream.class);
	private final Map entryMap = new TreeMap();
	private boolean edited = false;

	public String getString(String key, String def) {
		String ret = (String) entryMap.get(key);
		if (ret == null)
			return def;
		return ret;
	}

	public void putString(String key, String value) {
		entryMap.put(key, value);
		edited = true;
	}

	public void load(String path)
			throws ParserConfigurationException,
			IOException,
			SAXException {
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		JavaVersion version = new JavaVersion();
		if (0 < version.compareTo(new JavaVersion(1, 5, 0, 0))) {
			dbfactory
			.setFeature(
					"http://apache.org/xml/features/nonvalidating/load-external-dtd",
					false);
		} else {
			log.fatal("cur‚ÍJavaSE 5.0ˆÈã‚ª•K—v‚Å‚·B");
			System.exit(1);
		}
		DocumentBuilder builder = dbfactory.newDocumentBuilder();
		Document document = builder.parse(new File(path));
		Element root = document.getDocumentElement();
		NodeList childs = root.getElementsByTagName("entry");
		for (int i = 0; i < childs.getLength(); i++) {
			Element el = (Element) childs.item(i);
			String key = el.getAttribute("key");
			NodeList values = el.getChildNodes();
			if (null != values.item(0)) {
				entryMap.put(key, values.item(0).getNodeValue());
			}
		}
		log.debug("load[" + path + "]");
	}

	public void save(String path) throws IOException {
		if (!edited)
			return;
		log.debug("save[" + path + "]");
		// •Û‘¶ˆ—
		Charset cs = Charset.forName("Windows-31J");
		PrintWriter pw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(path), cs)));
		pw.print("<?xml version=\"1.0\" encoding=\"");
		pw.print(cs.name());
		pw.println("\"?>");
		pw.println("<properties>");
		for (Iterator it = entryMap.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			String value = (String) entryMap.get(key);
			pw.print("\t<entry key=\"");
			pw.print(key);
			pw.print("\">");
			pw.print(value);
			pw.println("</entry>");
		}
		pw.println("</properties>");
		pw.close();
		edited = false;
	}
}
