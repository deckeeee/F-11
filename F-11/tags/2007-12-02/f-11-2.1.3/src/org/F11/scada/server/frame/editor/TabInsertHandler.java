/*
 * Created on 2003/09/16
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.server.frame.editor;

import org.F11.scada.WifeUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author hori
 */
public class TabInsertHandler extends DefaultHandler {

	private final String TAB_CHAR = "  ";

	private StringBuffer result;
	private int tabCount;

	/**
	 * 
	 */
	public TabInsertHandler() {
		super();
		result = new StringBuffer();
		tabCount = 0;
	}

	public String getResult() {
		return result.toString();
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes)
		throws SAXException {
		if (localName.equals("page_map")) {
			return;
		}
		tabCount++;
		insertTab();
		result.append("<").append(qName);
		for (int i = 0; i < attributes.getLength(); i++) {
			result.append(" ").append(attributes.getQName(i)).append("=\"");
			result.append(WifeUtilities.htmlEscape(attributes.getValue(i))).append("\"");
		}
		result.append(">\n");
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("page_map")) {
			return;
		}
		insertTab();
		result.append("</").append(qName).append(">\n");
		tabCount--;
	}

	private void insertTab() {
		for (int i = 0; i < tabCount; i++) {
			result.append(TAB_CHAR);
		}
	}

}
