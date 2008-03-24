package org.F11.scada.tool.misc;

import org.F11.scada.tool.page.parser.DOMPageDefine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ElementConverter {
	protected Log log = LogFactory.getLog(this.getClass());

	private final Document document;
	private final Element element;

	public ElementConverter(Document document, Element element) {
		this.document = document;
		this.element = element;
	}

	public void convertHolderElement() {
		NodeList seriesList = element.getElementsByTagNameNS(
				DOMPageDefine.NAME_SPACE,
				"series");
		for (int seriesID = 0; seriesID < seriesList.getLength(); seriesID++) {
			Element seriesElement = (Element) seriesList.item(seriesID);
			NodeList propertyList = seriesElement.getElementsByTagNameNS(
					DOMPageDefine.NAME_SPACE,
					"holder");

			for (int i = 0; i < propertyList.getLength(); i++) {
				Element propertyElement = (Element) propertyList.item(i);
				String key = propertyElement.getAttribute("name");
				NodeList valueList = propertyElement.getElementsByTagNameNS(
						DOMPageDefine.NAME_SPACE,
						"name");
				String[] proviers = new String[valueList.getLength()];
				String[] holders = new String[valueList.getLength()];
				for (int j = 0; j < valueList.getLength(); j++) {
					Element valueElement = (Element) valueList.item(j);
					if ("holder".equals(key)) {
						String attribute = valueElement.getAttribute("value");
						int idx = attribute.indexOf("_");
						String provider = attribute.substring(0, idx);
						String holder = attribute.substring(idx + 1);
						proviers[j] = provider;
						holders[j] = holder;
					} else {
						throw new IllegalStateException();
					}
				}
				convertHolderElement(seriesElement, proviers, holders);
				seriesElement.removeChild(propertyElement);
			}
		}
	}

	private void convertHolderElement(
			Element seriesElement,
			String[] providers,
			String[] holders) {
		Element propertyElement = document.createElementNS(
				DOMPageDefine.NAME_SPACE,
				"f11:property");
		propertyElement.setAttribute("name", "provider");
		for (int i = 0; i < providers.length; i++) {
			Element newElement = document.createElementNS(
					DOMPageDefine.NAME_SPACE,
					"f11:name");
			newElement.setAttribute("value", providers[i]);
			propertyElement.appendChild(newElement);
		}
		seriesElement.appendChild(propertyElement);

		propertyElement = document.createElementNS(
				DOMPageDefine.NAME_SPACE,
				"f11:property");
		propertyElement.setAttribute("name", "holder");
		for (int i = 0; i < holders.length; i++) {
			Element newElement = document.createElementNS(
					DOMPageDefine.NAME_SPACE,
					"f11:name");
			newElement.setAttribute("value", holders[i]);
			propertyElement.appendChild(newElement);
		}
		seriesElement.appendChild(propertyElement);
	}

	public void convertHolderElement2() {
		NodeList seriesList = element.getElementsByTagNameNS(
				DOMPageDefine.NAME_SPACE,
				"series");
		for (int seriesID = 0; seriesID < seriesList.getLength(); seriesID++) {
			Element seriesElement = (Element) seriesList.item(seriesID);
			NodeList propertyList = seriesElement.getElementsByTagNameNS(
					DOMPageDefine.NAME_SPACE,
					"property");

			for (int i = 0; i < propertyList.getLength(); i++) {
				Element propertyElement = (Element) propertyList.item(i);
				String key = propertyElement.getAttribute("name");
				NodeList valueList = propertyElement.getElementsByTagNameNS(
						DOMPageDefine.NAME_SPACE,
						"name");
				String[] providers = new String[valueList.getLength()];
				String[] holders = new String[valueList.getLength()];
				if ("provider".equals(key)) {
					for (int j = 0; j < valueList.getLength(); j++) {
						Element valueElement = (Element) valueList.item(j);
						String attribute = valueElement.getAttribute("value");
						providers[j] = attribute;
					}
				} else if ("holder".equals(key)) {
					for (int j = 0; j < valueList.getLength(); j++) {
						Element valueElement = (Element) valueList.item(j);
						String attribute = valueElement.getAttribute("value");
						holders[j] = attribute;
					}
				}
				if (providers.length == holders.length) {
					convertHolderElement2(seriesElement, providers, holders);
					seriesElement.removeChild(propertyElement);
				}
			}
		}
	}

	private void convertHolderElement2(
			Element seriesElement,
			String[] providers,
			String[] holders) {
		/*
		 * System.out.println(Arrays.asList(providers));
		 * System.out.println(Arrays.asList(holders));
		 */
		Element propertyElement = document.createElementNS(
				DOMPageDefine.NAME_SPACE,
				"f11:holder");
		propertyElement.setAttribute("name", "holder");
		for (int i = 0; i < providers.length; i++) {
			Element newElement = document.createElementNS(
					DOMPageDefine.NAME_SPACE,
					"f11:name");
			newElement.setAttribute("value", providers[i] + "_" + holders[i]);
			propertyElement.appendChild(newElement);
		}
		seriesElement.appendChild(propertyElement);
	}
}
