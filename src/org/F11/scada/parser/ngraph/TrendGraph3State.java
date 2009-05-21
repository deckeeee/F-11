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
import org.F11.scada.applet.ngraph.HorizontalScaleButtonProperty;
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
	/** 日付表示フォーマット */
	private String dateFormat;
	/** 時間表示フォーマット */
	private String timeFormat;
	/** 縦目盛1つ分のピクセル数 */
	private int verticalScale;
	/** 縦目盛の数 */
	private int verticalCount;
	/** 合成モードの縦目盛の数 */
	private int compositionVerticalCount;
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
	/** 横スケールボタンプロパティーのリスト */
	List<HorizontalScaleButtonProperty> scaleButtonProperties;
	/** トレンドグラフ最大表示レコード */
	private int maxRecord;
	/** ツールバー表示の有無 */
	private boolean isVisibleToolbar;
	/** シリーズ表示の有無 */
	private boolean isVisibleSeries;
	/** ステータス表示の有無 */
	private boolean isVisibleStatus;
	/** スクロールバー表示の有無 */
	private boolean isVisibleScroolbar;
	/** 参照位置線表示の有無 */
	private boolean isVisibleReferenceLine;
	/** 縦スケール文字表示の有無 */
	private boolean isVisibleVerticalString;
	/** 現在の合成・分離表示モード */
	private boolean isCompositionMode;
	/** 現在のスパン表示モード */
	private boolean isAllSpanDisplayMode;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public TrendGraph3State(String tagName, Attributes atts, PageState pageState) {
		seriesGroups = new ArrayList<SeriesGroup>();
		this.pageState = pageState;
		x = getValue(atts, "x", "0");
		y = getValue(atts, "y", "0");
		width = getValue(atts, "width", "1028");
		height = getValue(atts, "height", "800");

		horizontalForAllSpanMode =
			Integer.parseInt(getValue(atts, "horizontalForAllSpanMode", "112"));
		horizontalForSelectSpanMode =
			Integer.parseInt(getValue(
				atts,
				"horizontalForSelectSpanMode",
				"168"));
		dateFormat = getValue(atts, "dateFormat", "MM/dd");
		timeFormat = getValue(atts, "timeFormat", "HH:mm");
		verticalScale = Integer.parseInt(getValue(atts, "verticalScale", "48"));
		verticalCount = Integer.parseInt(getValue(atts, "verticalCount", "10"));
		compositionVerticalCount =
			Integer.parseInt(getValue(atts, "compositionVerticalCount", "10"));
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
		maxRecord = Integer.parseInt(getValue(atts, "maxRecord", "5000"));
		isVisibleToolbar =
			Boolean.parseBoolean(getValue(atts, "visibleToolbar", "true"));
		isVisibleSeries =
			Boolean.parseBoolean(getValue(atts, "visibleSeries", "true"));
		isVisibleStatus =
			Boolean.parseBoolean(getValue(atts, "visibleStatus", "true"));
		isVisibleScroolbar =
			Boolean.parseBoolean(getValue(atts, "visibleScroolbar", "true"));
		isVisibleReferenceLine =
			Boolean
				.parseBoolean(getValue(atts, "visibleReferenceLine", "true"));
		isVisibleVerticalString =
			Boolean
				.parseBoolean(getValue(atts, "visibleVerticalString", "true"));
		isCompositionMode =
			Boolean.parseBoolean(getValue(atts, "compositionMode", "true"));
		isAllSpanDisplayMode =
			Boolean.parseBoolean(getValue(atts, "allSpanDisplayMode", "false"));
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
		} else if (tagName.equals("horizontalScaleButton")) {
			stack.push(new HorizontalScaleButtonState(tagName, atts, this));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("trendgraph3")) {
			GraphProperties p = getGraphProperties();
			p.setSeriesGroup(seriesGroups);
			p.setHorizontalScaleButtonProperty(scaleButtonProperties);
			GraphMainPanel mainPanel = new GraphMainPanel(p);
			if (x != null && y != null) {
				mainPanel.setLocation(getNumber(x), getNumber(y));
			}
			if (width != null && height != null) {
				mainPanel.setSize(getNumber(width), getNumber(height));
			}
			pageState.addPageSymbol(mainPanel);
			if (p.isVisibleToolbar()) {
				pageState.setToolBar(mainPanel.getToolBar());
			}
			stack.pop();
		}
	}

	private GraphProperties getGraphProperties() {
		GraphProperties p = new GraphProperties();
		p.setHorizontalForAllSpanMode(horizontalForAllSpanMode);
		p.setHorizontalForSelectSpanMode(horizontalForSelectSpanMode);
		p.setHorizontalCount(getHorizontalCount());
		p.setHorizontalLineSpan(getHorizontalSpan());
		p.setDateFormat(dateFormat);
		p.setTimeFormat(timeFormat);
		p.setVerticalScale(verticalScale);
		p.setVerticalCount(verticalCount);
		p.setCompositionVerticalCount(compositionVerticalCount);
		p.setScalePixcelSize(scalePixcelSize);
		p.setInsets(insets);
		p.setFont(font);
		p.setLineColor(lineColor);
		p.setBackGround(backGround);
		p.setVerticalScaleColor(verticalScaleColor);
		p.setPagefile(pagefile);
		p.setMaxRecord(maxRecord);
		p.setVisibleToolbar(isVisibleToolbar);
		p.setVisibleSeries(isVisibleSeries);
		p.setVisibleStatus(isVisibleStatus);
		p.setVisibleScroolbar(isVisibleScroolbar);
		p.setVisibleReferenceLine(isVisibleReferenceLine);
		p.setVisibleVerticalString(isVisibleVerticalString);
		p.setCompositionMode(isCompositionMode);
		p.setAllSpanDisplayMode(isAllSpanDisplayMode);
		return p;
	}

	private long getHorizontalSpan() {
		return scaleButtonProperties.get(0).getHorizontalLineSpan();
	}

	private int getHorizontalCount() {
		return scaleButtonProperties.get(0).getHorizontalCount();
	}

	private int getNumber(String string) {
		return Integer.parseInt(string);
	}

}
