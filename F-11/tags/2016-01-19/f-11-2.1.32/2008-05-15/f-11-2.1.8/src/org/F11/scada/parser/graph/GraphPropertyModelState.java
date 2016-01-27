/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/graph/GraphPropertyModelState.java,v 1.6.2.8 2007/03/13 05:55:06 frdm Exp $
 * $Revision: 1.6.2.8 $
 * $Date: 2007/03/13 05:55:06 $
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
import java.awt.Font;
import java.awt.Insets;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.F11.scada.applet.graph.DefaultGraphPropertyModel;
import org.F11.scada.applet.graph.GraphSeriesProperty;
import org.F11.scada.applet.graph.VerticallyScaleProperty;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/trendgraph/graphproperty 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class GraphPropertyModelState implements State, HandlerStateable {
	private static Logger logger;

	GraphState graphState;

	Insets insets;
	Insets graphView;
	Color[] colors;
	List series;
	String[] handlerNames;

	private int verticalScaleCount;
	private int verticalScaleHeight;
	private int horizontalScaleCount;
	private long horizontalScaleWidth;
	int foldCount;
	private int horizontalPixcelWidth;
	private int scaleOneHeightPixel;
	Color explanatoryColor;
	Font explanatoryFont;
	private String firstFormat;
	private String secondFormat;
	VerticallyScaleProperty verticallyScaleProperty;
	boolean isNotDemand;

	public GraphPropertyModelState(
			String tagName,
			Attributes atts,
			GraphState state,
			boolean isNotDemand) {

		logger = Logger.getLogger(getClass().getName());
		this.isNotDemand = isNotDemand;
		this.graphState = state;

		if (isCreateModel(atts)) {
			this.verticalScaleCount = Integer.parseInt(atts.getValue("verticalScaleCount"));
			this.verticalScaleHeight = Integer.parseInt(atts.getValue("verticalScaleHeight"));
			this.horizontalScaleCount = Integer.parseInt(atts.getValue("horizontalScaleCount"));
			this.horizontalScaleWidth = Long.parseLong(atts.getValue("horizontalScaleWidth"));
			this.horizontalPixcelWidth = Integer.parseInt(atts.getValue("horizontalPixcelWidth"));
			this.scaleOneHeightPixel = Integer.parseInt(atts.getValue("scaleOneHeightPixel"));
		} else {
			logger.info("Use Default.");
			this.verticalScaleCount = 10;
			this.verticalScaleHeight = 45;
			this.horizontalScaleCount = 4;
			this.horizontalScaleWidth = 3600000;
			this.horizontalPixcelWidth = 560;
			this.scaleOneHeightPixel = 5;
		}
		if (atts.getValue("foldcount") != null)
			this.foldCount = Integer.parseInt(atts.getValue("foldcount"));
		else
			this.foldCount = 0;
		String firstFormatStr = atts.getValue("firstFormat");
		logger.debug(firstFormatStr);
		if (firstFormatStr != null) {
		    this.firstFormat = firstFormatStr;
		} else {
		    this.firstFormat = "MM/dd";
		}
		String secondFormatStr = atts.getValue("secondFormat");
		logger.debug(secondFormatStr);
		if (secondFormatStr != null) {
		    this.secondFormat = secondFormatStr;
		} else {
		    this.secondFormat = "HH:mm";
		}
		logger.debug(firstFormat);
		logger.debug(secondFormat);
	}

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public GraphPropertyModelState(
			String tagName,
			Attributes atts,
			GraphState state) {
		this(tagName, atts, state, true);
	}

	private boolean isCreateModel(Attributes atts) {
		return (
			atts.getValue("verticalScaleCount") != null
				&& atts.getValue("verticalScaleHeight") != null
				&& atts.getValue("horizontalScaleCount") != null
				&& atts.getValue("horizontalScaleWidth") != null
				&& atts.getValue("horizontalPixcelWidth") != null
				&& atts.getValue("scaleOneHeightPixel") != null)
			? true
			: false;
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("insets") || tagName.equals("graphview")) {
			stack.push(new InsetsState(tagName, atts, this));
		} else if (tagName.equals("color")) {
			stack.push(new ColorState(tagName, atts, this));
		} else if (tagName.equals("series")) {
			stack.push(new SeriesState(tagName, atts, this));
		} else if (tagName.equals("handler")) {
			stack.push(new HandlerState(tagName, atts, this));
		} else if (tagName.equals("explanatory")) {
			stack.push(new ExplanatoryState(tagName, atts, this));
		} else if (tagName.equals("vertically")) {
			stack.push(new VerticallyScalePropertyState(tagName, atts, this));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (tagName.equals("graphproperty")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Pop : " + DisplayState.toString(tagName, stack));
			}
			if  (verticallyScaleProperty == null) {
				verticallyScaleProperty = new VerticallyScaleProperty(Color.GRAY, Color.BLACK, Color.WHITE);
			}
			graphState.setGraphPropertyModel(
				new DefaultGraphPropertyModel(
					verticalScaleCount,
					verticalScaleHeight,
					horizontalScaleCount,
					horizontalScaleWidth,
					insets,
					graphView,
					colors,
					handlerNames,
					foldCount,
					horizontalPixcelWidth,
					scaleOneHeightPixel,
					explanatoryColor,
					explanatoryFont,
					firstFormat,
					secondFormat,
					verticallyScaleProperty));
			for (Iterator it = series.iterator(); it.hasNext();) {
				GraphSeriesProperty element = (GraphSeriesProperty) it.next();
				graphState.addSeriesProperty(element);
			}
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}
	
	/*
	 * @see org.F11.scada.parser.graph.HandlerStateable#setHandlerName(String[])
	 */
	public void setHandlerName(String[] handlerNames) {
		this.handlerNames = handlerNames;
	}

}
