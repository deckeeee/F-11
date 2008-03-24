/*
 * 作成日: 2004/06/14
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package org.F11.scada.tool.page.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.Naming;
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

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.frame.editor.FrameEditHandler;
import org.F11.scada.tool.page.parser.bar.BarGraphDefine;
import org.F11.scada.tool.page.parser.trend.TrendGraphDefine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author hori
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public class DOMPageDefine {
    public static final String NAME_SPACE = "http://www.F-11.org/scada";
	protected Log log = LogFactory.getLog(this.getClass());

	private FrameEditHandler handler;
	private String pageXmlPath;
	private Document document;
	private Element element;
	private TrendGraphDefine trendDefine;
	private BarGraphDefine barDefine;

	public DOMPageDefine(String pageXmlPath)
		throws
			NotBoundException,
			ParserConfigurationException,
			IOException,
			SAXException {
		this.pageXmlPath = pageXmlPath;
		handler =
			(FrameEditHandler) Naming.lookup(
				WifeUtilities.createRmiFrameEditManager());
		String xml = handler.getPageXml(pageXmlPath);
		if (xml == null)
			return;
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		dbfactory.setNamespaceAware(true);
		DocumentBuilder builder = dbfactory.newDocumentBuilder();
		document = builder.parse(new InputSource(new StringReader(xml)));
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
			trendDefine = new TrendGraphDefine(document, element);
			return;
		}
		graphList = element.getElementsByTagNameNS(NAME_SPACE, "bargraph");
		if (0 < graphList.getLength()) {
			barDefine = new BarGraphDefine(document, element);
			return;
		}
		graphList = element.getElementsByTagNameNS(NAME_SPACE, "trendgraph2");
		if (0 < graphList.getLength()) {
			trendDefine = new TrendGraphDefine(document, element);
			return;
		}
		graphList = element.getElementsByTagNameNS(NAME_SPACE, "demandgraph");
		if (0 < graphList.getLength()) {
			barDefine = new BarGraphDefine(document, element);
			return;
		}
	}

	public TrendGraphDefine getTrendGraphDefine() {
		return trendDefine;
	}

	public BarGraphDefine getBarGraphDefine() {
		return barDefine;
	}

	public void send() throws TransformerException, RemoteException {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();

		DOMSource source = new DOMSource(document);
		StringWriter sw = new StringWriter();

		StreamResult result = new StreamResult(sw);
		transformer.transform(source, result);

		handler.setPageXml(pageXmlPath, sw.toString());
	}
}
