/*
 * 作成日: 2004/06/14
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package org.F11.scada.tool.page.parser.trend;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.F11.scada.tool.io.PointItemStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.page.parser.DOMPageDefine;
import org.F11.scada.tool.page.trend.PropertyForm;
import org.F11.scada.tool.point.name.PointNameBean;
import org.F11.scada.util.ConnectionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author hori
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public class TrendGraphDefine {
	protected Log log = LogFactory.getLog(this.getClass());

	private final Document document;
	private final Element element;

	public TrendGraphDefine(Document document, Element element) {
		this.document = document;
		this.element = element;
	}

	public List getHandlerList() {
		List ret = new ArrayList();
		NodeList modelList = element.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "graphmodel");
		Element modelElement = (Element) modelList.item(0);
		NodeList handlerList = modelElement.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "handler");
		Element handlerElement = (Element) handlerList.item(0);
		NodeList nameList = handlerElement.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "name");
		for (int i = 0; i < nameList.getLength(); i++) {
			Element nameElement = (Element) nameList.item(i);
			ret.add(nameElement.getAttribute("value"));
		}
		return ret;
	}

	public List getSeriesNameList() {
		List ret = new ArrayList();
		NodeList seriesList = element.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "series");
		for (int i = 0; i < seriesList.getLength(); i++) {
			Element seriesElement = (Element) seriesList.item(i);
			SeriesBean bean = new SeriesBean();
			bean.setId(i);
			bean.setSeriesName(seriesElement.getAttribute("name"));
			ret.add(bean);
		}
		return ret;
	}

	public List getColorList() {
		List ret = new ArrayList();
		NodeList colorList = element.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "color");
		Element colorElement = (Element) colorList.item(0);
		NodeList nameList = colorElement.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "name");
		for (int i = 0; i < nameList.getLength(); i++) {
			Element nameElement = (Element) nameList.item(i);
			PropertyForm bean = new PropertyForm();
			bean.setColor(nameElement.getAttribute("value"));
			bean.setPos(i);
			ret.add(bean);
		}
		return ret;
	}

	public SeriesBean getSeriesHead(int seriesID)
		throws SQLException, IOException {
		SeriesBean series = new SeriesBean();
		series.setId(seriesID);
		NodeList seriesList = element.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "series");
		if (0 <= seriesID && seriesID < seriesList.getLength()) {
			Element seriesElement = (Element) seriesList.item(seriesID);
			series.setSeriesName(seriesElement.getAttribute("name"));
			series.setSeriesSize(
				Integer.parseInt(seriesElement.getAttribute("size")));
			series.setPropertyList(getPropertyList(seriesID));
		}
		return series;
	}

	private List getPropertyList(int seriesID)
		throws SQLException, IOException {
		List ret = getColorList();
		setProperty(seriesID, ret);
		setHolder(seriesID, ret);
		setPointName(ret);
		return ret;
	}

	private void setProperty(int seriesID, List properties) {
		NodeList seriesList = element.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "series");
		if (0 <= seriesID && seriesID < seriesList.getLength()) {
			Element seriesElement = (Element) seriesList.item(seriesID);
			NodeList propertyList =
				seriesElement.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "property");

			for (int i = 0; i < propertyList.getLength(); i++) {
				Element propertyElement = (Element) propertyList.item(i);
				String key = propertyElement.getAttribute("name");
				NodeList valueList =
					propertyElement.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "element");
				if (valueList.getLength() <= 0) {
					valueList =
						propertyElement.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "name");
				}
				for (int j = 0;
					j < valueList.getLength() && j < properties.size();
					j++) {
					Element valueElement = (Element) valueList.item(j);
					PropertyForm bean = (PropertyForm) properties.get(j);
					if ("minimums".equals(key)) {
						String value = valueElement.getAttribute("value");
						bean.setMinimums(Double.parseDouble(value));
					} else if ("maximums".equals(key)) {
						String value = valueElement.getAttribute("value");
						bean.setMaximums(Double.parseDouble(value));
					} else if ("inputminimums".equals(key)) {
						String value = valueElement.getAttribute("value");
						bean.setInputminimums(Double.parseDouble(value));
					} else if ("inputmaximums".equals(key)) {
						String value = valueElement.getAttribute("value");
						bean.setInputmaximums(Double.parseDouble(value));
					} else if ("pointname".equals(key)) {
						String value = valueElement.getAttribute("value");
						int p1 = value.indexOf("$(");
						int p2 = value.indexOf('_', p1);
						if (0 <= p1 && p1 < p2) {
							bean.setPoint(
								Integer.parseInt(value.substring(p1 + 2, p2)));
						}
					} else if ("unitmark".equals(key)) {
					}
				}
			}
		}
	}
	
	private void setHolder(int seriesID, List properties) {
		NodeList seriesList = element.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "series");
		if (0 <= seriesID && seriesID < seriesList.getLength()) {
			Element seriesElement = (Element) seriesList.item(seriesID);
			NodeList propertyList =
				seriesElement.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "holder");

			for (int i = 0; i < propertyList.getLength(); i++) {
				Element propertyElement = (Element) propertyList.item(i);
				String key = propertyElement.getAttribute("name");
				NodeList valueList =
					propertyElement.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "name");
				for (int j = 0;
					j < valueList.getLength() && j < properties.size();
					j++) {
					Element valueElement = (Element) valueList.item(j);
					PropertyForm bean = (PropertyForm) properties.get(j);
					if ("holder".equals(key)) {
						bean.setHolder(valueElement.getAttribute("value"));
					} else {
					    throw new IllegalStateException();
					}
				}
			}
		}
	}

	private void setPointName(List properties)
		throws SQLException, IOException {

		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);
			PointItemStore store = new PointItemStore();

			for (int i = 0; i < properties.size(); i++) {
				PropertyForm bean = (PropertyForm) properties.get(i);
				PointNameBean nameBean =
					store.getPointName(util, bean.getPoint());
				bean.setUnit(nameBean.getUnit());
				bean.setName(nameBean.getName());
				bean.setUnit_mark(nameBean.getUnit_mark());
			}

			con.close();
			con = null;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
	}

	public void replaceSeries(SeriesBean series) {
		NodeList graphPropertyList =
			element.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "graphproperty");
		Element graphPropertyElement = (Element) graphPropertyList.item(0);
		NodeList seriesList =
			graphPropertyElement.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "series");
		Element newElement = createSeriesElement(series);
		if (0 <= series.getId() && series.getId() < seriesList.getLength()) {
			Node seriesElement = seriesList.item(series.getId());
			graphPropertyElement.replaceChild(newElement, seriesElement);
		}
	}

	public void addSeries(SeriesBean series) {
		NodeList graphPropertyList =
			element.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "graphproperty");
		Element graphPropertyElement = (Element) graphPropertyList.item(0);
		NodeList seriesList =
			graphPropertyElement.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "series");
		Element newElement = createSeriesElement(series);
		if (0 <= series.getId() && series.getId() < seriesList.getLength()) {
			Node seriesElement = seriesList.item(series.getId());
			graphPropertyElement.insertBefore(newElement, seriesElement);
		} else {
			graphPropertyElement.appendChild(newElement);
		}
	}

	private Element createSeriesElement(SeriesBean series) {
		Element seriesElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:series");

		seriesElement.setAttribute("name", series.getSeriesName());
		String value = String.valueOf(series.getSeriesSize());
		seriesElement.setAttribute("size", value);

		Element propertyElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:property");
		propertyElement.setAttribute("name", "minimums");
		Iterator it = series.getPropertyList().iterator();
		for (int j = 0; j < series.getSeriesSize() && it.hasNext(); j++) {
			PropertyForm bean = (PropertyForm) it.next();
			Element newElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:element");
			value = String.valueOf(bean.getMinimums());
			newElement.setAttribute("value", value);
			propertyElement.appendChild(newElement);
		}
		seriesElement.appendChild(propertyElement);

		propertyElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:property");
		propertyElement.setAttribute("name", "maximums");
		it = series.getPropertyList().iterator();
		for (int j = 0; j < series.getSeriesSize() && it.hasNext(); j++) {
			PropertyForm bean = (PropertyForm) it.next();
			Element newElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:element");
			value = String.valueOf(bean.getMaximums());
			newElement.setAttribute("value", value);
			propertyElement.appendChild(newElement);
		}
		seriesElement.appendChild(propertyElement);

		propertyElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:property");
		propertyElement.setAttribute("name", "inputminimums");
		it = series.getPropertyList().iterator();
		for (int j = 0; j < series.getSeriesSize() && it.hasNext(); j++) {
			PropertyForm bean = (PropertyForm) it.next();
			Element newElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:element");
			value = String.valueOf(bean.getInputminimums());
			newElement.setAttribute("value", value);
			propertyElement.appendChild(newElement);
		}
		seriesElement.appendChild(propertyElement);

		propertyElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:property");
		propertyElement.setAttribute("name", "inputmaximums");
		it = series.getPropertyList().iterator();
		for (int j = 0; j < series.getSeriesSize() && it.hasNext(); j++) {
			PropertyForm bean = (PropertyForm) it.next();
			Element newElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:element");
			value = String.valueOf(bean.getInputmaximums());
			newElement.setAttribute("value", value);
			propertyElement.appendChild(newElement);
		}
		seriesElement.appendChild(propertyElement);

		propertyElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:holder");
		propertyElement.setAttribute("name", "holder");
		it = series.getPropertyList().iterator();
		for (int j = 0; j < series.getSeriesSize() && it.hasNext(); j++) {
			PropertyForm bean = (PropertyForm) it.next();
			Element newElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:name");
			newElement.setAttribute("value", bean.getHolder());
			propertyElement.appendChild(newElement);
		}
		seriesElement.appendChild(propertyElement);

		propertyElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:property");
		propertyElement.setAttribute("name", "pointname");
		it = series.getPropertyList().iterator();
		for (int j = 0; j < series.getSeriesSize() && it.hasNext(); j++) {
			PropertyForm bean = (PropertyForm) it.next();
			Element newElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:name");
			value = "$(" + bean.getPoint() + "_unit)";
			value += " $(" + bean.getPoint() + "_name)";
			newElement.setAttribute("value", value);
			propertyElement.appendChild(newElement);
		}
		seriesElement.appendChild(propertyElement);

		propertyElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:property");
		propertyElement.setAttribute("name", "unitmark");
		it = series.getPropertyList().iterator();
		for (int j = 0; j < series.getSeriesSize() && it.hasNext(); j++) {
			PropertyForm bean = (PropertyForm) it.next();
			Element newElement = document.createElementNS(DOMPageDefine.NAME_SPACE, "f11:name");
			value = "$(" + bean.getPoint() + "_unit_mark)";
			newElement.setAttribute("value", value);
			propertyElement.appendChild(newElement);
		}
		seriesElement.appendChild(propertyElement);

		return seriesElement;
	}

	public void removeSeries(int id) {
		NodeList graphPropertyList =
			element.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "graphproperty");
		Element graphPropertyElement = (Element) graphPropertyList.item(0);
		NodeList seriesList =
			graphPropertyElement.getElementsByTagNameNS(DOMPageDefine.NAME_SPACE, "series");
		if (0 <= id && id < seriesList.getLength()) {
			Node seriesElement = seriesList.item(id);
			graphPropertyElement.removeChild(seriesElement);
		}
	}
}
