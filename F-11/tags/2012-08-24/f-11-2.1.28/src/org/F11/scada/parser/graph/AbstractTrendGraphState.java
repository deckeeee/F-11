/*
 * $Header$
 * $Revision$
 * $Date$
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
package org.F11.scada.parser.graph;

import java.awt.Color;
import java.util.Stack;

import org.F11.scada.applet.graph.GraphModel;
import org.F11.scada.applet.graph.GraphPropertyModel;
import org.F11.scada.applet.graph.GraphSeriesProperty;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.PageState;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.util.AttributesUtil;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/trendgraph 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
abstract public class AbstractTrendGraphState implements GraphState {
	protected static Logger logger;
	
	protected PageState pageState;
	
	protected Color foreground;
	protected Color background;
	protected String x;
	protected String y;
	protected String width;
	protected String height;
	protected GraphPropertyModel model;
//	protected GraphModel gmodel;
	protected String horizontalScaleFile;
	protected float strokeWidth;
	protected Object argv;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public AbstractTrendGraphState(
			String tagName,
			Attributes atts,
			PageState pageState,
			Object argv) {

		logger = Logger.getLogger(getClass().getName());

		this.pageState = pageState;
		this.argv = argv;
		foreground = ColorFactory.getColor(atts.getValue("foreground"));
		background = ColorFactory.getColor(atts.getValue("background"));
		x = atts.getValue("x");
		y = atts.getValue("y");
		width = atts.getValue("width");
		height = atts.getValue("height");
		horizontalScaleFile = AttributesUtil.getValue("horizontalScaleFile", atts);
		strokeWidth = getStrokeWidth(AttributesUtil.getValue("strokeWidth", atts));
	}
	
	private float getStrokeWidth(String atts) {
		return null == atts ? 1.0F : Float.parseFloat(atts);
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
//		this.gmodel = graphModel;
	}


	protected int getGroupNo(Object argv) {
		try {
			return Integer.parseInt((String) argv);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

}
