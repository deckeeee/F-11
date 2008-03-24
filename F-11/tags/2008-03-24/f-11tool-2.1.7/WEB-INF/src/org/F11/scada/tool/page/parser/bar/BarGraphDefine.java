/*
 * 作成日: 2004/06/14
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package org.F11.scada.tool.page.parser.bar;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.F11.scada.tool.io.PointItemStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.page.bar.BarPointForm;
import org.F11.scada.tool.page.parser.DOMPageDefine;
import org.F11.scada.tool.point.name.PointNameBean;
import org.F11.scada.util.ConnectionUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author hori
 * 
 * この生成されたコメントの挿入されるテンプレートを変更するため ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public class BarGraphDefine {
	private final Document document;
	private final Element element;

	public BarGraphDefine(Document document, Element element) {
		this.document = document;
		this.element = element;
	}

	public void setBarPoint(BarPointForm form) {
		NodeList seriesList = element.getElementsByTagNameNS(
				DOMPageDefine.NAME_SPACE,
				"series");
		for (int i = 0; i < seriesList.getLength(); i++) {
			Element seriesElement = (Element) seriesList.item(i);
			String value = seriesElement.getAttribute("name");
			int p1 = value.indexOf("$(");
			int p2 = value.indexOf('_', p1);
			if (0 <= p1 && p1 < p2) {
				int refPoint = Integer.parseInt(value.substring(p1 + 2, p2));
				if (refPoint == form.getPoint()) {
					NodeList propertyList = seriesElement
							.getElementsByTagNameNS(
									DOMPageDefine.NAME_SPACE,
									"property");

					for (int j = 0; j < propertyList.getLength(); j++) {
						Element propertyElement = (Element) propertyList
								.item(j);
						String key = propertyElement.getAttribute("name");
						if ("minimums".equals(key)) {
							NodeList valueList = propertyElement
									.getElementsByTagNameNS(
											DOMPageDefine.NAME_SPACE,
											"element");
							Element valueElement = (Element) valueList.item(0);
							valueElement.setAttribute("value", String
									.valueOf(form.getMinimums()));
						} else if ("maximums".equals(key)) {
							NodeList valueList = propertyElement
									.getElementsByTagNameNS(
											DOMPageDefine.NAME_SPACE,
											"element");
							Element valueElement = (Element) valueList.item(0);
							valueElement.setAttribute("value", String
									.valueOf(form.getMaximums()));
						}
					}
					break;
				}
			}
		}
	}

	public BarPointForm getBarPoint(int point) throws SQLException, IOException {
		BarPointForm bean = new BarPointForm();
		NodeList seriesList = element.getElementsByTagNameNS(
				DOMPageDefine.NAME_SPACE,
				"series");
		for (int i = 0; i < seriesList.getLength(); i++) {
			Element seriesElement = (Element) seriesList.item(i);
			String value = seriesElement.getAttribute("name");
			int p1 = value.indexOf("$(");
			int p2 = value.indexOf('_', p1);
			if (0 <= p1 && p1 < p2) {
				int refPoint = Integer.parseInt(value.substring(p1 + 2, p2));
				if (refPoint == point) {
					bean.setPoint(point);
					NodeList propertyList = seriesElement
							.getElementsByTagNameNS(
									DOMPageDefine.NAME_SPACE,
									"property");

					for (int j = 0; j < propertyList.getLength(); j++) {
						Element propertyElement = (Element) propertyList
								.item(j);
						String key = propertyElement.getAttribute("name");
						if ("minimums".equals(key)) {
							NodeList valueList = propertyElement
									.getElementsByTagNameNS(
											DOMPageDefine.NAME_SPACE,
											"element");
							Element valueElement = (Element) valueList.item(0);
							value = valueElement.getAttribute("value");
							bean.setMinimums(Long.parseLong(value));
						} else if ("maximums".equals(key)) {
							NodeList valueList = propertyElement
									.getElementsByTagNameNS(
											DOMPageDefine.NAME_SPACE,
											"element");
							Element valueElement = (Element) valueList.item(0);
							value = valueElement.getAttribute("value");
							bean.setMaximums(Long.parseLong(value));
						}
					}

					Connection con = null;
					try {
						con = ConnectionUtil.getConnection();
						StrategyUtility util = new StrategyUtility(con);
						PointItemStore store = new PointItemStore();

						PointNameBean nameBean = store.getPointName(util, bean
								.getPoint());
						bean.setUnit(nameBean.getUnit());
						bean.setName(nameBean.getName());
						bean.setUnit_mark(nameBean.getUnit_mark());

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
					break;
				}
			}
		}
		return bean;
	}

	public List getBarGroup() throws SQLException, IOException {
		List ret = getPropertyList();
		setPointName(ret);
		return ret;
	}

	private List getPropertyList() {
		List ret = new ArrayList();
		NodeList seriesList = element.getElementsByTagNameNS(
				DOMPageDefine.NAME_SPACE,
				"series");
		for (int i = 0; i < seriesList.getLength(); i++) {
			BarPointForm bean = new BarPointForm();
			Element seriesElement = (Element) seriesList.item(i);
			String value = seriesElement.getAttribute("name");
			int p1 = value.indexOf("$(");
			int p2 = value.indexOf('_', p1);
			if (0 <= p1 && p1 < p2) {
				bean.setPoint(Integer.parseInt(value.substring(p1 + 2, p2)));
			}
			NodeList propertyList = seriesElement.getElementsByTagNameNS(
					DOMPageDefine.NAME_SPACE,
					"property");

			for (int j = 0; j < propertyList.getLength(); j++) {
				Element propertyElement = (Element) propertyList.item(j);
				String key = propertyElement.getAttribute("name");
				if ("minimums".equals(key)) {
					NodeList valueList = propertyElement
							.getElementsByTagNameNS(
									DOMPageDefine.NAME_SPACE,
									"element");
					Element valueElement = (Element) valueList.item(0);
					value = valueElement.getAttribute("value");
					bean.setMinimums(Long.parseLong(value));
				} else if ("maximums".equals(key)) {
					NodeList valueList = propertyElement
							.getElementsByTagNameNS(
									DOMPageDefine.NAME_SPACE,
									"element");
					Element valueElement = (Element) valueList.item(0);
					value = valueElement.getAttribute("value");
					bean.setMaximums(Long.parseLong(value));
				}
			}
			ret.add(bean);
		}
		return ret;
	}

	private void setPointName(List properties) throws SQLException, IOException {

		Connection con = null;
		try {
			con = ConnectionUtil.getConnection();
			StrategyUtility util = new StrategyUtility(con);
			PointItemStore store = new PointItemStore();

			for (int i = 0; i < properties.size(); i++) {
				BarPointForm bean = (BarPointForm) properties.get(i);
				PointNameBean nameBean = store.getPointName(util, bean
						.getPoint());
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

}
