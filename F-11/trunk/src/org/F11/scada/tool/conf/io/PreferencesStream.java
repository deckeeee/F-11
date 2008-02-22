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
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PreferencesStream {
	private static final Logger log = Logger.getLogger(PreferencesStream.class);
	private final Map envMap = new HashMap();
	private boolean edited = false;

	public String getEnv(String key, String def) {
		String ret = (String) envMap.get(key);
		if (ret == null)
			return def;
		return ret;
	}

	public void putEnv(String key, String value) {
		envMap.put(key, value);
		edited = true;
	}

	public void load(String path)
			throws ParserConfigurationException,
			IOException,
			SAXException {
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbfactory.newDocumentBuilder();
		Document document = builder.parse(new File(path));
		Element root = document.getDocumentElement();
		NodeList childs = root.getElementsByTagName("property");
		for (int i = 0; i < childs.getLength(); i++) {
			Element el = (Element) childs.item(i);
			String key = el.getAttribute("key");
			envMap.put(key, el.getAttribute("value"));
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
		pw.println("<environment>");
		pw.println(getXmlProp("/server/rmi/managerdelegator/name", ""));
		pw.println(getXmlProp("/server/rmi/managerdelegator/port", String
				.valueOf(Registry.REGISTRY_PORT)));
		pw.println(getXmlProp(
				"/server/rmi/managerdelegator/rmiReceivePort",
				"50001"));
		pw.println(getXmlProp("/server/rmi/collectorserver/name", ""));
		pw.println(getXmlProp("/server/rmi/collectorserver/port", String
				.valueOf(Registry.REGISTRY_PORT)));
		pw.println(getXmlProp(
				"/server/rmi/collectorserver/rmiReceivePort",
				"50000"));
		pw.println(getXmlProp("/server/rmi/collectorserver/retry/count", "-1"));
		pw.println(getXmlProp(
				"/server/policy/policyMap",
				"org.F11.scada.security.postgreSQL.PostgreSQLPolicyMap"));
		pw.println(getXmlProp(
				"/server/policy/authentication",
				"org.F11.scada.security.postgreSQL.PostgreSQLAuthentication"));
		pw.println(getXmlProp("/server/jdbc/servername", "localhost"));
		pw.println(getXmlProp("/server/jdbc/dbmsname", "postgresql"));
		pw.println(getXmlProp("/server/jdbc/driver", "org.postgresql.Driver"));
		pw.println(getXmlProp("/server/jdbc/option", ""));
		pw.println(getXmlProp("/server/jdbc/dbname", ""));
		pw.println(getXmlProp("/server/jdbc/username", ""));
		pw.println(getXmlProp("/server/jdbc/password", ""));
		pw.println(getXmlProp("/org.xml.sax.driver", ""));
		pw.println(getXmlProp("/server/mail/smtp/servername", ""));
		pw.println(getXmlProp("/server/mail/smtp/disableHolder", ""));
		pw.println(getXmlProp("/server/mail/message/from", ""));
		pw.println(getXmlProp("/server/mail/message/subject", ""));
		pw.println(getXmlProp("/server/mail/message/address/field", "TO"));
		pw.println(getXmlProp("/server/mail/message/datemode", "false"));
		pw.println(getXmlProp("/server/alarm/sentmail", "false"));
		pw.println(getXmlProp("/server/mail/message/retry", "5"));
		pw.println(getXmlProp("/server/mail/message/wait", "1000"));
		pw.println(getXmlProp("/server/alarm/print/printservice", ""));
		pw.println(getXmlProp("/server/alarm/print/size", "ISO_A4"));
		pw.println(getXmlProp("/server/alarm/print/orientation", "PORTRAIT"));
		pw
				.println(getXmlProp(
						"/server/alarm/print/font",
						"Monospaced,PLAIN,10"));
		pw.println(getXmlProp("/server/alarm/print/pagelines", "10"));
		pw.println(getXmlProp("/server/alarm/print/className", ""));
		pw.println(getXmlProp("/server/device", ""));
		pw.println(getXmlProp("/server/FrameEditHandler", ""));
		pw.println(getXmlProp(
				"/server/autoprint",
				"org.F11.scada.xwife.server.AutoPrintPanel"));
		pw.println(getXmlProp("/server/title", "F-11 Server"));
		pw.println(getXmlProp("/server/startup/wait", "0"));
		pw.println(getXmlProp("/server/logging/maxrecord", "4096"));
		pw.println(getXmlProp("/server/alarm/maxrow", "5000"));
		pw.println(getXmlProp("/server/operationlog/prefix", "false"));
		pw.println(getXmlProp("/server/schedulepoint", "false"));
		pw.println(getXmlProp("/server/graphcache", "true"));
		pw.println(getXmlProp("/server/deploy/period", "69896"));
		pw.println(getXmlProp("/server/logging/noRevision", "false"));
		pw.println(getXmlProp("/server/operationlog/impl/OperationLoggingUtilImpl", ""));
		pw.println(getXmlProp("/server/operationlog/impl/OperationLoggingUtilImpl/scheduleCount", "false"));
		pw.println(getXmlProp("/server/logging/report/outputMode", "false"));
		pw.println(getXmlProp("/server/systemtime/testMode", "false"));
		pw.println(getXmlProp("/server/communicateWaitTime", "100"));
		pw.println(getXmlProp("/server/mail/smtp/sender", "alarmMail"));
		pw.println(getXmlProp("/server/mail/smtp/port", "25"));
		pw.println(getXmlProp("/server/mail/smtp/user", ""));
		pw.println(getXmlProp("/server/mail/smtp/password", ""));
		pw.println(getXmlProp("/server/formula/isUseFormula", "false"));
		pw.println("</environment>");
		pw.close();
		edited = false;
	}

	private String getXmlProp(String key, String def) {
		StringBuffer sb = new StringBuffer();
		sb.append("\t<property key=\"").append(key).append("\" value=\"");
		sb.append(HtmlUtility.htmlEscape(getEnv(key, def))).append("\" />");
		return sb.toString();
	}
}
