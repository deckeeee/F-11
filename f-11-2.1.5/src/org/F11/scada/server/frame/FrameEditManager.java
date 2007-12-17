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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.frame.editor.FrameEditHandler;
import org.F11.scada.server.frame.editor.TabInsertHandler;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author hori
 */
public class FrameEditManager
		extends UnicastRemoteObject
		implements FrameEditHandler {

	private static final long serialVersionUID = 4223511559618331389L;
	private final String DEF_XML_HED =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<f11:page_map xmlns:f11=\"http://www.F-11.org/scada\">\n";
	private final String DEF_XML_FUT = "</f11:page_map>\n";

	private FrameDefineManager defineManager;
	
	private final LoggingTaskManager taskManager;
	
	private static Logger logger = Logger.getLogger(FrameEditManager.class);

	/**
	 * コンストラクタ
	 * @param port
	 * @throws java.rmi.RemoteException
	 */
	public FrameEditManager(int port, FrameDefineManager defineManager, Map taskMap)
			throws RemoteException, MalformedURLException {

		super(port);
		Naming.rebind(WifeUtilities.createRmiFrameEditManager(), this);
		this.defineManager = defineManager;
		taskManager = new LoggingTaskManager(taskMap);

		logger.info("FrameEditManager constracted.");
	}

	/**
	 * nameで指定されたページ定義をXMLで返します。
	 * @param name ページ名
	 * @return String ページ定義のXML表現。ページ名無しの場合null
	 */
	public String getPageXml(String name) throws RemoteException {
		return defineManager.getPageString(name);
	}

	/**
	 * nameで指定したページ定義を設定します。
	 * @param name ページ名
	 * @param xml ページ定義
	 */
	public void setPageXml(String name, String xml) {
		defineManager.setPageString(name, xml);
		saveAllPages();

	}

	/**
	 * loggingNameで指定したロギングファイルに保存される項目の属性リストを返します。
	 * @param loggingName ロギングファイル名
	 * @return 項目の属性リスト
	 */
	public List getLoggingHolders(String loggingName) {
		return taskManager.getLoggingHolders(loggingName);
	}

	private void saveAllPages() {
		Writer out = null;
		try {
			XMLReader parser =
				XMLReaderFactory.createXMLReader(EnvironmentManager.get("/org.xml.sax.driver", ""));
			File editFile = new File("resources/XWifeAppletDefine.xml");
			out =
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(editFile), "UTF-8"));
			out.write(DEF_XML_HED);
			Set names = new TreeSet(defineManager.getPageNameSet());
			for (Iterator it = names.iterator(); it.hasNext();) {
				out.write("\n");
				String xml = defineManager.getPageString((String) it.next());
				// パース
				TabInsertHandler tabInsert = new TabInsertHandler();
				parser.setContentHandler(tabInsert);
				StringReader sr = null;
				try {
					sr = new StringReader(xml);
					InputSource is = new InputSource(sr);
					parser.parse(is);
					out.write(tabInsert.getResult());
				} finally {
					if (sr != null) {
						sr.close();
					}
				}
			}
			String statusBar = defineManager.getStatusbarString();
			if (statusBar != null) {
				out.write("\n");
				// パース
				TabInsertHandler tabInsert = new TabInsertHandler();
				parser.setContentHandler(tabInsert);
				StringReader sr = null;
				try {
					sr = new StringReader(statusBar);
					InputSource is = new InputSource(sr);
					parser.parse(is);
					out.write(tabInsert.getResult());
				} finally {
					if (sr != null) {
						sr.close();
					}
				}
			}
			out.write("\n");
			out.write(DEF_XML_FUT);
			out.close();
			
			Process excel =
				Runtime.getRuntime().exec(
					new String[] {
						"jar",
						"-uf",
						"lib/F-11.jar",
						"resources/XWifeAppletDefine.xml" });
			excel.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}
