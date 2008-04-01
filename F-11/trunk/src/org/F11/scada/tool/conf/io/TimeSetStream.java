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

import static org.F11.scada.cat.util.CollectionUtil.$;
import static org.F11.scada.cat.util.CollectionUtil.map;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
	private final Logger log = Logger.getLogger(TimeSetStream.class);
	private boolean edited;

	private final Map<String, TimeSetTaskBean> timeSetTaskMap =
		new LinkedHashMap<String, TimeSetTaskBean>();
	private final Map<String, String> scheduleMap;
	private final Map<String, String> milliOffsetModeMap;

	public TimeSetStream() {
		scheduleMap = createScheduleMap();
		milliOffsetModeMap = createMilliOffsetModeMap();
	}

	private Map<String, String> createScheduleMap() {
		return map(
			$("MINUTE", "ï™ä‘äu"),
			$("TENMINUTE", "10ï™ä‘äu"),
			$("HOUR", "éûä‘äu"),
			$("MONTH", "ì˙ä‘äu"),
			$("MONTH", "åéä‘äu"),
			$("YEARLY", "îNä‘äu"),
			$("ï™ä‘äu", "MINUTE"),
			$("10ï™ä‘äu", "TENMINUTE"),
			$("éûä‘äu", "HOUR"),
			$("ì˙ä‘äu", "MONTH"),
			$("åéä‘äu", "MONTH"),
			$("îNä‘äu", "YEARLY"));
	}

	private Map<String, String> createMilliOffsetModeMap() {
		return map($("false", "í èÌ"), $("true", "É~ÉäïbÉÇÅ[Éh"), $("í èÌ", "false"), $(
			"É~ÉäïbÉÇÅ[Éh",
			"true"));
	}

	public String getValue(String name, String key, String def) {
		return timeSetTaskMap.get(name).containsKey(key) ? timeSetTaskMap.get(
			name).get(key) : def;
	}

	public List<TimeSetBean> getBeansList(String name) {
		return timeSetTaskMap.containsKey(name)
			? new ArrayList<TimeSetBean>(timeSetTaskMap.get(name).getTimeList())
			: new ArrayList<TimeSetBean>();
	}

	public void setValue(String name, String key, String value) {
		timeSetTaskMap.get(name).put(key, value);
		edited = true;
	}

	public void setBeansList(String name, List<TimeSetBean> list) {
		timeSetTaskMap.get(name).getTimeList().clear();
		timeSetTaskMap.get(name).getTimeList().addAll(list);
		edited = true;
	}

	public void setTimeSetTask(TimeSetTaskBean bean) {
		timeSetTaskMap.put(bean.get("name"), bean);
		edited = true;
	}

	public TimeSetTaskBean removeTimeSetTask(TimeSetTaskBean bean) {
		TimeSetTaskBean remove = timeSetTaskMap.remove(bean.get("name"));
		edited = true;
		return remove;
	}

	public List<TimeSetTaskBean> getTimeSetTask() {
		return new ArrayList<TimeSetTaskBean>(timeSetTaskMap.values());
	}

	public void load(String path)
			throws ParserConfigurationException,
			IOException,
			SAXException {
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbfactory.newDocumentBuilder();
		Document document = builder.parse(new File(path));
		Element root = document.getDocumentElement();
		NodeList childs = root.getElementsByTagName("f11:timesettask");
		for (int i = 0; i < childs.getLength(); i++) {
			Element el = (Element) childs.item(i);
			TimeSetTaskBean timeSetTaskBean = new TimeSetTaskBean();
			String name = el.getAttribute("name");
			timeSetTaskBean.put("name", name);
			timeSetTaskBean.put("schedule", getSchedule(el
				.getAttribute("schedule")));
			timeSetTaskBean.put("offset", el.getAttribute("offset"));
			timeSetTaskBean.put("milliOffsetMode", getMilliOffSetMode(el
				.getAttribute("milliOffsetMode")));

			NodeList props = el.getChildNodes();
			ArrayList<TimeSetBean> timeSetBean =
				new ArrayList<TimeSetBean>(props.getLength());
			for (int j = 0; j < props.getLength(); j++) {
				if (props.item(j).getNodeType() == Node.ELEMENT_NODE) {
					Element prop = (Element) props.item(j);
					TimeSetBean bean = new TimeSetBean();
					bean.setKind(prop.getNodeName().substring(4));
					bean.setProvider(prop.getAttribute("provider"));
					bean.setHolder(prop.getAttribute("holder"));
					timeSetBean.add(bean);
				}
			}
			timeSetTaskBean.setTimeList(timeSetBean);
			timeSetTaskMap.put(name, timeSetTaskBean);
		}
		log.debug("load[" + path + "]");
	}

	private String getSchedule(String key) {
		return scheduleMap.get(key);
	}

	private String getMilliOffSetMode(String key) {
		return milliOffsetModeMap.get(key);
	}

	public void save(String path) throws IOException {
		if (!edited)
			return;
		log.debug("save[" + path + "]");
		// ï€ë∂èàóù
		Charset cs = Charset.forName("Windows-31J");
		PrintWriter pw =
			new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path),
				cs)));
		pw.print("<?xml version=\"1.0\" encoding=\"");
		pw.print(cs.name());
		pw.println("\"?>");
		pw.println("<f11:timeset xmlns:f11=\"http://www.F-11.org/scada\">");
		for (TimeSetTaskBean timeSetTaskBean : timeSetTaskMap.values()) {
			pw.print("\t<f11:timesettask name=\"");
			pw.print(timeSetTaskBean.get("name"));
			pw.print("\" schedule=\"");
			pw.print(getSchedule(timeSetTaskBean.get("schedule")));
			pw.print("\" offset=\"");
			pw.print(timeSetTaskBean.get("offset"));
			pw.print("\" milliOffsetMode=\"");
			pw
				.print(getMilliOffSetMode(timeSetTaskBean
					.get("milliOffsetMode")));
			pw.println("\">");
			for (TimeSetBean bean : timeSetTaskBean.getTimeList()) {
				pw.print("\t\t<f11:");
				pw.print(bean.getKind());
				pw.print(" provider=\"");
				pw.print(bean.getProvider());
				pw.print("\" holder=\"");
				pw.print(bean.getHolder());
				pw.println("\"/>");
			}
			pw.println("\t</f11:timesettask>");
		}
		pw.println("</f11:timeset>");
		pw.close();
		edited = false;
	}

}
