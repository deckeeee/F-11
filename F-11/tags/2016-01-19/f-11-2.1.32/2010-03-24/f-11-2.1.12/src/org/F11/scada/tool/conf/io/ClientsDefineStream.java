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
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ClientsDefineStream {
	private static final Logger log = Logger
			.getLogger(ClientsDefineStream.class);
	private final List beansList = new ArrayList();
	private boolean edited = false;

	public List getBeansList() {
		return new ArrayList(beansList);
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
		NodeList childs = root.getElementsByTagName("client");
		for (int i = 0; i < childs.getLength(); i++) {
			Element el = (Element) childs.item(i);
			ClientDefineBean bean = new ClientDefineBean();
			bean.setIpAddress(el.getAttribute("ipaddress"));
			beansList.add(bean);
			NodeList props = el.getChildNodes();
			for (int j = 0; j < props.getLength(); j++) {
				if ("defaultuser".equals(props.item(j).getNodeName())) {
					Element prop = (Element) props.item(j);
					bean.setDefaultUserName(prop.getAttribute("name"));
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
		pw.println("<client_map>");
		for (Iterator it = beansList.iterator(); it.hasNext();) {
			ClientDefineBean bean = (ClientDefineBean) it.next();
			pw.print("\t<client ipaddress=\"");
			pw.print(bean.getIpAddress());
			pw.println("\">");

			pw.print("\t\t<defaultuser name=\"");
			pw.print(bean.getDefaultUserName());
			pw.println("\"/>");

			pw.println("\t</client>");
		}
		pw.println("</client_map>");
		pw.close();
		edited = false;
	}

}
