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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TimeSetStream {
	private static final Logger log = Logger.getLogger(TimeSetStream.class);
	private final Map dataMap = new HashMap();
	private final List beansList = new ArrayList();
	private boolean edited = false;

	public String getValue(String key, String def) {
		String ret = (String) dataMap.get(key);
		if (ret == null)
			return def;
		return ret;
	}
	public List getBeansList() {
		return new ArrayList(beansList);
	}

	public void setValue(String key, String value) {
		dataMap.put(key, value);
		edited = true;
	}
	public void setBeansList(List list) {
		this.beansList.clear();
		this.beansList.addAll(list);
		edited = true;
	}

	public void load(String path) throws ParserConfigurationException,
			IOException, SAXException {
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbfactory.newDocumentBuilder();
		Document document = builder.parse(new File(path));
		Element root = document.getDocumentElement();
		NodeList childs = root.getElementsByTagName("f11:timesettask");
		for (int i = 0; i < childs.getLength(); i++) {
			Element el = (Element) childs.item(i);
			dataMap.put("schedule", el.getAttribute("schedule"));
			dataMap.put("offset", el.getAttribute("offset"));
			dataMap.put("milliOffsetMode", el.getAttribute("milliOffsetMode"));
			NodeList props = el.getChildNodes();
			for (int j = 0; j < props.getLength(); j++) {
				if (props.item(j).getNodeType() == Node.ELEMENT_NODE) {
					Element prop = (Element) props.item(j);
					TimeSetBean bean = new TimeSetBean();
					bean.setKind(prop.getNodeName().substring(4));
					bean.setProvider(prop.getAttribute("provider"));
					bean.setHolder(prop.getAttribute("holder"));
					beansList.add(bean);
				}
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
		pw.println("<f11:timeset xmlns:f11=\"http://www.F-11.org/scada\">");
		pw.print("\t<f11:timesettask schedule=\"");
		pw.print((String) dataMap.get("schedule"));
		pw.print("\" offset=\"");
		pw.print((String) dataMap.get("offset"));
		pw.print("\" milliOffsetMode=\"");
		pw.print((String) dataMap.get("milliOffsetMode"));
		pw.println("\">");
		for (Iterator it = beansList.iterator(); it.hasNext();) {
			TimeSetBean bean = (TimeSetBean) it.next();
			pw.print("\t\t<f11:");
			pw.print(bean.getKind());
			pw.print(" provider=\"");
			pw.print(bean.getProvider());
			pw.print("\" holder=\"");
			pw.print(bean.getHolder());
			pw.println("\"/>");
		}
		pw.println("\t</f11:timesettask>");
		pw.println("</f11:timeset>");
		pw.close();
		edited = false;
	}

}
