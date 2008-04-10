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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.deploy.PageDefineUtil;
import org.F11.scada.server.frame.editor.FrameEditHandler;
import org.F11.scada.server.register.HolderString;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * ページ定義XMLファイル名より、ページ定義文字列を返す FrameEditHandler クラスです
 * 旧ページ定義XMLファイル(/resources/XWifeAppletDefine.xml)を使用せずに、
 * pagedefine ディレクトリ以下にページ定義XMLファイルを保存する、
 * 新ページ定義を使用する場合に、このクラスを FrameEditHandler として使用します。
 * 
 * <i>出力するXML定義ファイルは、Windows-31Jエンコーディングを使用します</i>
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class XmlFrameEditManager
		extends UnicastRemoteObject
		implements FrameEditHandler {

	private static final long serialVersionUID = -6064343543109351074L;

	/** ロギングタスクマネージャーの参照です */
	private final LoggingTaskManager taskManager;
	
	/** Logging API */
	private static Logger logger = Logger.getLogger(XmlFrameEditManager.class);

	/**
	 * コンストラクタ
	 * このマネージャーを初期化します
	 * @param port RMIオブジェクト転送ポート番号
	 * @param taskMap ロギングタスクのマップ
	 * @throws java.rmi.RemoteException RMIレジストリ登録でエラー
	 */
	public XmlFrameEditManager(int port, Map taskMap)
		throws RemoteException, MalformedURLException {
		super(port);
		Naming.rebind(WifeUtilities.createRmiFrameEditManager(), this);
		taskManager = new LoggingTaskManager(taskMap);
		
		logger.info("XmlFrameEditManager constracted.");
	}

	/**
	 * nameで指定されたページ定義をXMLで返します。
	 * @param name ページ定義XMLファイル名
	 * @return String ページ定義のXML表現。ページ名無しの場合null
	 */
	public String getPageXml(String name) throws RemoteException {
		logger.debug("getPage : " + name);

		InputStream stream = null;
		String pageXml = null;
		try {
			// パース
			stream = new BufferedInputStream(new FileInputStream(name));
			Map map = PageDefineUtil.parse(stream);
			if (logger.isDebugEnabled()) {
				logger.debug(map);
			}
			stream.close();

			if (map.size() != 1) {
				logger.info("Edit File is multi page define.");
			}		
			Iterator it = map.values().iterator();
			PageDefine page = (PageDefine) it.next();
			pageXml = page.getSrcXml();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return pageXml;
	}

	/**
	 * nameで指定したページ定義を設定します。
	 * @param name ページ名
	 * @param xml ページ定義
	 */
	public void setPageXml(String name, String xml) {
		logger.debug("setPage : " + name);

		savePage(name, xml);
	}

	/**
	 * loggingNameで指定したロギングファイルに保存される項目の属性リストを返します。
	 * @param loggingName ロギングファイル名
	 * @return 項目の属性リスト
	 */
	public List getLoggingHolders(String loggingName) {
		return taskManager.getLoggingHolders(loggingName);
	}

	public List<HolderString> getHolders(
			String loggingName) throws RemoteException {
		return taskManager.getHolders(loggingName);
	}

	/**
	 * ファイルを更新します。
	 * @param file 更新するページ定義XMLファイル
	 * @param xml ファイルの内容
	 */
	private void savePage(String file, String xml) {
		StringReader sr = null;
		Writer out = null;
		try {
			sr = new StringReader(xml);
			InputSource is = new InputSource(sr);

			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);

			out =
				new OutputStreamWriter(
					new FileOutputStream(new File(file)), "UTF-8");

			OutputFormat format = new OutputFormat(doc, "UTF-8", true);
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(doc);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (sr != null) {
				sr.close();
			}
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
