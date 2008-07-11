/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/graph/bargraph/BarGraphState.java,v 1.5.2.2 2007/03/13 05:54:20 frdm Exp $
 * $Revision: 1.5.2.2 $
 * $Date: 2007/03/13 05:54:20 $
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
package org.F11.scada.parser.graph.bargraph;

import java.awt.Color;
import java.util.Stack;

import javax.swing.JComponent;

import org.F11.scada.applet.graph.GraphModel;
import org.F11.scada.applet.graph.GraphPropertyModel;
import org.F11.scada.applet.graph.GraphSeriesProperty;
import org.F11.scada.applet.graph.bargraph.BarGraph;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.PageState;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.parser.graph.GraphModelState;
import org.F11.scada.parser.graph.GraphPropertyModelState;
import org.F11.scada.parser.graph.GraphState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/bargraph 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class BarGraphState implements GraphState {
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
	String barstep;
	int axismode;
	private boolean isYear;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public BarGraphState(
		String tagName,
		Attributes atts,
		PageState pageState) {

		logger = Logger.getLogger(getClass().getName());

		this.pageState = pageState;
		foreground = ColorFactory.getColor(atts.getValue("foreground"));
		background = ColorFactory.getColor(atts.getValue("background"));
		x = atts.getValue("x");
		y = atts.getValue("y");
		width = atts.getValue("width");
		height = atts.getValue("height");
		barstep = atts.getValue("barstep");
		axismode = 0;
		if (atts.getValue("axismode") != null) {
			axismode = Integer.parseInt(atts.getValue("axismode"));
		}
		isYear = Boolean.valueOf(atts.getValue("isYear")).booleanValue();
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("graphproperty")) {
			stack.push(new GraphPropertyModelState(tagName, atts, this));
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
		if (tagName.equals("bargraph")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Pop : " + DisplayState.toString(tagName, stack));
			}

			try {
				//				GraphModel graphModel = new DefaultGraphModel(new String[]{"log_table_MINUTE", "log_table_HOUR"});

				BarGraph graph =
					new BarGraph(graphModel, model, barstep, axismode, isYear);
				JComponent graphPanel = graph.getMainPanel();
				if (foreground != null) {
					graphPanel.setForeground(foreground);
				}
				if (background != null) {
					graphPanel.setBackground(background);
				}
				pageState.setToolBar(graph.getToolBar());

				if (x != null && y != null) {
					graphPanel.setLocation(
						Integer.parseInt(x),
						Integer.parseInt(y));
				}
				if (width != null && height != null) {
					graphPanel.setSize(
						Integer.parseInt(width),
						Integer.parseInt(height));
				}

				pageState.addPageSymbol(graphPanel);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	/**
	 * 状態オブジェクトにグラフプロパティモデルを設定します。
	 * @param graphPropertyModel グラフプロパティモデル
	 */
	public void setGraphPropertyModel(GraphPropertyModel graphPropertyModel) {
		this.model = graphPropertyModel;
	}

	/**
	 * 状態オブジェクトのグラフプロパティモデルにシリーズプロパティを追加します。
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
	 * @param graphModel グラフプロパティモデル
	 */
	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;
	}
}
