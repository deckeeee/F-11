/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/graph/demand/DemandGraphState.java,v 1.4.2.5 2006/06/02 02:18:04 frdm Exp $
 * $Revision: 1.4.2.5 $
 * $Date: 2006/06/02 02:18:04 $
 *
 * =============================================================================
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
package org.F11.scada.parser.graph.demand;

import java.awt.Color;
import java.util.Stack;

import javax.swing.JComponent;

import org.F11.scada.applet.graph.GraphModel;
import org.F11.scada.applet.graph.GraphPropertyModel;
import org.F11.scada.applet.graph.GraphSeriesProperty;
import org.F11.scada.applet.graph.demand.DemandGraph;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.PageState;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.parser.graph.GraphPropertyModelState;
import org.F11.scada.parser.graph.GraphState;
import org.F11.scada.util.AttributesUtil;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/demandgraph 状態を表すクラスです。
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DemandGraphState implements GraphState {
	private static Logger logger;

	PageState pageState;

	Color foreground;
	Color background;
	String x;
	String y;
	String width;
	String height;
	GraphPropertyModel model;
	GraphModel graphModel;
	private double expectYCount;
	private boolean alarmTimeMode;
	private Color stringColor;
	private boolean colorSetting;
	private int axisInterval;
	private double demandTime;
	private String graphBack;
	private String graphLine;
	private String graphBaseLine;
	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public DemandGraphState(String tagName, Attributes atts, PageState pageState) {

		logger = Logger.getLogger(getClass().getName());

		this.pageState = pageState;
		foreground = ColorFactory.getColor(atts.getValue("foreground"));
		background = ColorFactory.getColor(atts.getValue("background"));
		x = atts.getValue("x");
		y = atts.getValue("y");
		width = atts.getValue("width");
		height = atts.getValue("height");
		expectYCount = getExpectYCount(atts);
		alarmTimeMode =
			Boolean.valueOf(atts.getValue("alarmTimeMode")).booleanValue();
		stringColor = ColorFactory.getColor(atts.getValue("scaleStringColor"));
		colorSetting =
			Boolean.valueOf(atts.getValue("colorSetting")).booleanValue();
		axisInterval = getAxisInterval(atts);
		demandTime = getDemandTime(atts);
		graphBack = getGraphBack(atts);
		graphLine = getGraphLine(atts);
		graphBaseLine = getGraphBaseLine(atts);
	}

	private String getGraphBack(Attributes atts) {
		String s = atts.getValue("graphBack");
		return AttributesUtil.isSpaceOrNull(s) ? "navy" : s;
	}

	private String getGraphLine(Attributes atts) {
		String s = atts.getValue("graphLine");
		return AttributesUtil.isSpaceOrNull(s) ? "cornflowerblue" : s;
	}

	private String getGraphBaseLine(Attributes atts) {
		String s = atts.getValue("graphBaseLine");
		return AttributesUtil.isSpaceOrNull(s) ? "white" : s;
	}

	private int getAxisInterval(Attributes atts) {
		String countStr = atts.getValue("axisInterval");
		if (countStr != null) {
			return Integer.parseInt(countStr);
		}
		return 5;
	}

	private double getDemandTime(Attributes atts) {
		String countStr = atts.getValue("demandTime");
		if (countStr != null) {
			return Double.parseDouble(countStr);
		}
		return 30D;
	}

	private double getExpectYCount(Attributes atts) {
		String countStr = atts.getValue("expectYCount");
		if (countStr != null) {
			return Double.parseDouble(countStr);
		}
		return 2D;
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("graphproperty")) {
			stack.push(new GraphPropertyModelState(tagName, atts, this, false));
		} else if (tagName.equals("graphmodel")) {
			stack.push(new GraphModelState(tagName, atts, this));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (tagName.equals("demandgraph")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Pop : " + DisplayState.toString(tagName, stack));
			}

			DemandGraph graph =
				new DemandGraph(
					graphModel,
					model,
					alarmTimeMode,
					stringColor,
					colorSetting,
					axisInterval,
					demandTime,
					graphBack,
					graphLine,
					graphBaseLine);
			graph.setExpectYCount(expectYCount);
			JComponent graphPanel = graph;

			graphPanel.setLocation(Integer.parseInt(x), Integer.parseInt(y));
			graphPanel.setSize(
				Integer.parseInt(width),
				Integer.parseInt(height));

			if (foreground != null) {
				graphPanel.setForeground(foreground);
			}
			if (background != null) {
				graphPanel.setBackground(background);
			}
			pageState.addPageSymbol(graphPanel);

			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	/**
	 * 状態オブジェクトにグラフプロパティモデルを設定します。
	 *
	 * @param graphPropertyModel グラフプロパティモデル
	 */
	public void setGraphPropertyModel(GraphPropertyModel graphPropertyModel) {
		this.model = graphPropertyModel;
	}

	/**
	 * 状態オブジェクトのグラフプロパティモデルにシリーズプロパティを追加します。
	 *
	 * @param property シリーズプロパティ
	 */
	public void addSeriesProperty(GraphSeriesProperty property) {
		if (this.model != null) {
			this.model.addSeriesProperty(property);
		} else {
			throw new IllegalStateException("GraphSeriesProperty is null.");
		}
	}

	/**
	 * 状態オブジェクトにグラフモデルを設定します。
	 *
	 * @param graphModel グラフプロパティモデル
	 */
	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;
	}
}
