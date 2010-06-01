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

import org.F11.scada.util.JavaVersion;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RemoveDefineStream {
	private static final Logger log = Logger
			.getLogger(RemoveDefineStream.class);
	private final List countRemove = new ArrayList();
	private final List secondRemove = new ArrayList();
	private boolean edited = false;

	public List getCountRemove() {
		return new ArrayList(countRemove);
	}

	public void setCountRemove(List list) {
		countRemove.clear();
		countRemove.addAll(list);
		edited = true;
	}

	public List getSecondRemove() {
		return new ArrayList(secondRemove);
	}

	public void setSecondRemove(List list) {
		secondRemove.clear();
		secondRemove.addAll(list);
		edited = true;
	}

	public void load(String path)
			throws ParserConfigurationException,
			IOException,
			SAXException {
		List list = countRemove;
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		JavaVersion version = new JavaVersion();
		if (0 < version.compareTo(new JavaVersion(1, 5, 0, 0))) {
			dbfactory
					.setFeature(
							"http://apache.org/xml/features/nonvalidating/load-external-dtd",
							false);
		} else {
			log.fatal("curはJavaSE 5.0以上が必要です。");
			System.exit(1);
		}
		DocumentBuilder builder = dbfactory.newDocumentBuilder();
		Document document = builder.parse(new File(path));
		Element root = document.getDocumentElement();
		NodeList c0 = root.getChildNodes();
		for (int i0 = 0; i0 < c0.getLength(); i0++) {
			if (!"component".equals(c0.item(i0).getNodeName())
					|| c0.item(i0).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element e1 = (Element) c0.item(i0);
			NodeList c1 = e1.getChildNodes();
			for (int i1 = 0; i1 < c1.getLength(); i1++) {
				if (c1.item(i1).getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element e2 = (Element) c1.item(i1);
				if ("property".equals(e2.getNodeName())
						&& "dao".equals(e2.getAttribute("name"))) {
					NodeList c2 = e2.getChildNodes();
					if ("countRemoveDao".equals(c2.item(0).getNodeValue()))
						list = countRemove;
					else if ("secondRemoveDao"
							.equals(c2.item(0).getNodeValue()))
						list = secondRemove;
				} else if ("initMethod".equals(e2.getNodeName())) {
					RemoveDefineBean bean = new RemoveDefineBean();
					NodeList c2 = e2.getElementsByTagName("property");
					for (int i2 = 0; i2 < c2.getLength(); i2++) {
						Element e3 = (Element) c2.item(i2);
						NodeList c3 = e3.getChildNodes();
						if ("tableName".equals(e3.getAttribute("name"))) {
							String value = c3.item(0).getNodeValue();
							bean.setTableName(value.substring(
									1,
									value.length() - 1));
						} else if ("dateFieldName".equals(e3
								.getAttribute("name"))) {
							String value = c3.item(0).getNodeValue();
							bean.setDateFieldName(value.substring(1, value
									.length() - 1));
						} else if ("removeValue"
								.equals(e3.getAttribute("name"))) {
							bean.setRemoveValue(c3.item(0).getNodeValue());
						}
					}
					c2 = e2.getElementsByTagName("component");
					for (int i2 = 0; i2 < c2.getLength(); i2++) {
						Element e3 = (Element) c2.item(i2);
						if ("org.F11.scada.scheduling.DailyIterator".equals(e3
								.getAttribute("class"))) {
							bean.setDaily(true);
							NodeList c3 = e3.getElementsByTagName("arg");
							Element e4 = (Element) c3.item(0);
							NodeList c4 = e4.getChildNodes();
							for (int i4 = 0; i4 < c4.getLength(); i4++) {
								if ("#text".equals(c4.item(i4).getNodeName())) {
									bean.setExecuteHour(c4.item(i4)
											.getNodeValue());
								}
							}
							e4 = (Element) c3.item(1);
							c4 = e4.getChildNodes();
							for (int i4 = 0; i4 < c4.getLength(); i4++) {
								if ("#text".equals(c4.item(i4).getNodeName())) {
									bean.setExecuteMinute(c4.item(i4)
											.getNodeValue());
								}
							}
							e4 = (Element) c3.item(2);
							c4 = e4.getChildNodes();
							for (int i4 = 0; i4 < c4.getLength(); i4++) {
								if ("#text".equals(c4.item(i4).getNodeName())) {
									bean.setExecuteSecond(c4.item(i4)
											.getNodeValue());
								}
							}
						} else if ("org.F11.scada.scheduling.MonthlyIterator"
								.equals(e3.getAttribute("class"))) {
							bean.setDaily(false);
							NodeList c3 = e3.getElementsByTagName("arg");
							Element e4 = (Element) c3.item(0);
							NodeList c4 = e4.getChildNodes();
							for (int i4 = 0; i4 < c4.getLength(); i4++) {
								if ("#text".equals(c4.item(i4).getNodeName())) {
									bean.setExecuteDay(c4.item(i4)
											.getNodeValue());
								}
							}
							e4 = (Element) c3.item(1);
							c4 = e4.getChildNodes();
							for (int i4 = 0; i4 < c4.getLength(); i4++) {
								if ("#text".equals(c4.item(i4).getNodeName())) {
									bean.setExecuteHour(c4.item(i4)
											.getNodeValue());
								}
							}
							e4 = (Element) c3.item(2);
							c4 = e4.getChildNodes();
							for (int i4 = 0; i4 < c4.getLength(); i4++) {
								if ("#text".equals(c4.item(i4).getNodeName())) {
									bean.setExecuteMinute(c4.item(i4)
											.getNodeValue());
								}
							}
							e4 = (Element) c3.item(3);
							c4 = e4.getChildNodes();
							for (int i4 = 0; i4 < c4.getLength(); i4++) {
								if ("#text".equals(c4.item(i4).getNodeName())) {
									bean.setExecuteSecond(c4.item(i4)
											.getNodeValue());
								}
							}
						}
					}
					list.add(bean);
				}
			}
		}
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
		pw
				.println("<!DOCTYPE components PUBLIC \"-//SEASAR2.1//DTD S2Container//EN\"");
		pw.println("\t\"http://www.seasar.org/dtd/components21.dtd\">");
		pw.println("<components namespace=\"org_F11_scada_server_remove\">");
		pw
				.println("<include path=\"org/F11/scada/server/remove/RemoveDao.dicon\"/>");

		if (0 < countRemove.size()) {
			pw
					.println("\t<component class=\"org.F11.scada.server.remove.impl.RemoveServiceImpl\">");
			// 削除コンポーネントを指定 秒数:secondRemoveDao 件数:countRemoveDao
			pw.println("\t\t<property name=\"dao\">countRemoveDao</property>");
			for (Iterator it = countRemove.iterator(); it.hasNext();) {
				RemoveDefineBean bean = (RemoveDefineBean) it.next();
				outRemoveDefineBean(pw, bean);
			}
			pw.println("\t</component>");
		}

		if (0 < secondRemove.size()) {
			pw
					.println("\t<component class=\"org.F11.scada.server.remove.impl.RemoveServiceImpl\">");
			// 削除コンポーネントを指定 秒数:secondRemoveDao 件数:countRemoveDao
			pw.println("\t\t<property name=\"dao\">secondRemoveDao</property>");
			for (Iterator it = secondRemove.iterator(); it.hasNext();) {
				RemoveDefineBean bean = (RemoveDefineBean) it.next();
				outRemoveDefineBean(pw, bean);
			}
			pw.println("\t</component>");
		}
		pw.println("</components>");
		pw.close();
		edited = false;
	}

	private void outRemoveDefineBean(PrintWriter pw, RemoveDefineBean bean) {
		pw.println("\t\t<initMethod name=\"addSchedule\" >");
		pw.println("\t\t\t<arg>");
		pw
				.println("\t\t\t\t<component class=\"org.F11.scada.server.remove.RemoveDto\" >");
		pw.print("\t\t\t\t\t<property name=\"tableName\">\"");
		pw.print(bean.getTableName());
		pw.println("\"</property>");
		pw.print("\t\t\t\t\t<property name=\"dateFieldName\">\"");
		pw.print(bean.getDateFieldName());
		pw.println("\"</property>");
		pw.print("\t\t\t\t\t<property name=\"removeValue\">");
		pw.print(bean.getRemoveValue());
		pw.println("</property>");
		pw.println("\t\t\t\t</component>");
		pw.println("\t\t\t</arg>");
		pw.println("\t\t\t<arg>");
		if (bean.isDaily()) {
			pw
					.println("\t\t\t\t<component class=\"org.F11.scada.scheduling.DailyIterator\">");
		} else {
			pw
					.println("\t\t\t\t<component class=\"org.F11.scada.scheduling.MonthlyIterator\">");
			pw.print("\t\t\t\t\t<arg>");
			pw.print(bean.getExecuteDay());
			pw.println("</arg>");
		}
		pw.print("\t\t\t\t\t<arg>");
		pw.print(bean.getExecuteHour());
		pw.println("</arg>");
		pw.print("\t\t\t\t\t<arg>");
		pw.print(bean.getExecuteMinute());
		pw.println("</arg>");
		pw.print("\t\t\t\t\t<arg>");
		pw.print(bean.getExecuteSecond());
		pw.println("</arg>");
		pw.println("\t\t\t\t</component>");
		pw.println("\t\t\t</arg>");
		pw.println("\t\t</initMethod>");
	}

}
