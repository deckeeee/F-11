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
package org.F11.scada.parser.ngraph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.F11.scada.applet.ngraph.GraphMainPanel;
import org.F11.scada.applet.ngraph.GraphProperties;
import org.F11.scada.applet.ngraph.SeriesGroup;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.PageState;
import org.F11.scada.parser.State;
import org.F11.scada.util.AttributesUtil;
import org.F11.scada.util.FontUtil;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/trendgraph 状態を表すクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TrendGraph3State implements State {
	protected static Logger logger = Logger.getLogger(TrendGraph3State.class);
	private PageState pageState;
	private String x;
	private String y;
	private String width;
	private String height;
	/** ｽﾊﾟﾝ全表示の横幅ピクセル数 */
	private int horizontalForAllSpanMode;
	/** ｽﾊﾟﾝ略表示の横幅ピクセル数 */
	private int horizontalForSelectSpanMode;
	/** 横目盛の数 */
	private int horizontalCount;
	/** 横目盛の時間スケール幅 */
	private long horizontalLineSpan;
	/** 日付表示フォーマット */
	private String dateFormat;
	/** 時間表示フォーマット */
	private String timeFormat;
	/** 縦目盛1つ分のピクセル数 */
	private int verticalScale;
	/** 縦目盛の数 */
	private int verticalCount;
	/** 目盛線のピクセル数 */
	private int scalePixcelSize;
	/** グラフエリアの余白 */
	private Insets insets;
	/** 使用フォント */
	private Font font;
	/** 線の色 */
	private Color lineColor;
	/** 背景色 */
	private Color backGround;
	/** グラフエリアのグリッド色 */
	private Color verticalScaleColor;
	/** シリーズグループのリスト */
	List<SeriesGroup> seriesGroups;
	/** ページファイル名 */
	private String pagefile;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public TrendGraph3State(String tagName, Attributes atts, PageState pageState) {
		seriesGroups = new ArrayList<SeriesGroup>();
		this.pageState = pageState;
		x = atts.getValue("x");
		y = atts.getValue("y");
		width = atts.getValue("width");
		height = atts.getValue("height");

		horizontalForAllSpanMode =
			Integer.parseInt(getValue(atts, "horizontalForAllSpanMode", "112"));
		horizontalForSelectSpanMode =
			Integer.parseInt(getValue(
				atts,
				"horizontalForSelectSpanMode",
				"168"));
		horizontalCount =
			Integer.parseInt(getValue(atts, "horizontalCount", "5"));
		horizontalLineSpan =
			Long.parseLong(getValue(atts, "horizontalLineSpan", "18000000"));
		dateFormat = getValue(atts, "dateFormat", "%1$tm/%1$td");
		timeFormat = getValue(atts, "timeFormat", "%1$tH:%1$tM");
		verticalScale = Integer.parseInt(getValue(atts, "verticalScale", "48"));
		verticalCount = Integer.parseInt(getValue(atts, "verticalCount", "10"));
		scalePixcelSize =
			Integer.parseInt(getValue(atts, "scalePixcelSize", "5"));
		insets = AttributesUtil.getInsets(atts.getValue("insets"));
		font = FontUtil.getFont(atts.getValue("font"));

		lineColor = ColorFactory.getColor(getValue(atts, "lineColor", "white"));
		backGround =
			ColorFactory.getColor(getValue(atts, "backGround", "navy"));
		verticalScaleColor =
			ColorFactory.getColor(getValue(
				atts,
				"verticalScaleColor",
				"cornflowerblue"));
		pagefile = atts.getValue("pagefile");
	}

	private String getValue(Attributes atts, String name, String def) {
		String value = atts.getValue(name);
		return value != null ? value : def;
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (tagName.equals("series")) {
			stack.push(new SeriesState(tagName, atts, this));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("trendgraph3")) {
			GraphProperties p = getGraphProperties();
			p.setSeriesGroup(seriesGroups);
			GraphMainPanel mainPanel = new GraphMainPanel(p);
			if (x != null && y != null) {
				mainPanel.setLocation(getNumber(x), getNumber(y));
			}
			if (width != null && height != null) {
				mainPanel.setSize(getNumber(width), getNumber(height));
			}
			pageState.addPageSymbol(mainPanel);
			pageState.setToolBar(mainPanel.getToolBar());
			stack.pop();
		}
	}

	private GraphProperties getGraphProperties() {
		GraphProperties p = new GraphProperties();
		p.setHorizontalForAllSpanMode(horizontalForAllSpanMode);
		p.setHorizontalForSelectSpanMode(horizontalForSelectSpanMode);
		p.setHorizontalCount(horizontalCount);
		p.setHorizontalLineSpan(horizontalLineSpan);
		p.setDateFormat(dateFormat);
		p.setTimeFormat(timeFormat);
		p.setVerticalScale(verticalScale);
		p.setVerticalCount(verticalCount);
		p.setScalePixcelSize(scalePixcelSize);
		p.setInsets(insets);
		p.setFont(font);
		p.setLineColor(lineColor);
		p.setBackGround(backGround);
		p.setVerticalScaleColor(verticalScaleColor);
		p.setPagefile(pagefile);
		return p;
	}

	private int getNumber(String string) {
		return Integer.parseInt(string);
	}

}
