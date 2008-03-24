package org.F11.scada.tool.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class GraphDefineConverter {
    public static final String NAME_SPACE = "http://www.F-11.org/scada";

	private Document document;
	private Element element;
	private ElementConverter trendDefine;

	public GraphDefineConverter(String pageXmlPath)
		throws
			NotBoundException,
			ParserConfigurationException,
			IOException,
			SAXException {
		if (pageXmlPath == null)
			return;
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		dbfactory.setNamespaceAware(true);
		DocumentBuilder builder = dbfactory.newDocumentBuilder();
		document = builder.parse(new FileInputStream(pageXmlPath));
		Element root = document.getDocumentElement();

		// page要素のリストを取得
		NodeList pageList = root.getElementsByTagNameNS(NAME_SPACE, "page");
		if (pageList.getLength() <= 0) {
			return;
		}
		// 先頭page要素
		element = (Element) pageList.item(0);
		NodeList graphList = element.getElementsByTagNameNS(NAME_SPACE, "trendgraph");
		if (0 < graphList.getLength()) {
			trendDefine = new ElementConverter(document, element);
			return;
		}
		graphList = element.getElementsByTagNameNS(NAME_SPACE, "bargraph");
		if (0 < graphList.getLength()) {
			trendDefine = new ElementConverter(document, element);
			return;
		}
		graphList = element.getElementsByTagNameNS(NAME_SPACE, "trendgraph2");
		if (0 < graphList.getLength()) {
			trendDefine = new ElementConverter(document, element);
			return;
		}
	}

	public static void convert(String input, String output) {
		try {
			GraphDefineConverter define = new GraphDefineConverter(input);
			define.getTrendGraphDefine().convertHolderElement();
			define.send(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void convert2(String input, String output) {
		try {
			GraphDefineConverter define = new GraphDefineConverter(input);
			define.getTrendGraphDefine().convertHolderElement2();
			define.send(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ElementConverter getTrendGraphDefine() {
		return trendDefine;
	}

	public void send(String out) throws TransformerException, RemoteException {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();

		DOMSource source = new DOMSource(document);
		StringWriter sw = new StringWriter();

		StreamResult result = new StreamResult(sw);
		transformer.transform(source, result);

		savePage(out, sw.toString());
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
