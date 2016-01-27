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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.applet.graph.ExplanatoryNotesText;
import org.F11.scada.applet.graph.TrendGraphView;
import org.F11.scada.applet.graph.VerticallyScale;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.PageState;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.util.AttributesUtil;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/trendgraph 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class GraphViewState extends AbstractTrendGraphState {
	/** 縦グリッド点線の有無 */
	private boolean isViewVerticalScale = true;
	/** グラフオブジェクトのマウスイベント有無 */
	private boolean isMouseClickEnable;
	/** X軸日時文字列の表示有無 */
	private boolean isDrawString = true;
	/** 文字列の色 */
	private Color stringColor;
	/** スクロールバー表示の有無 */
	private boolean isScrollBarVisible;
	/** 縦スケールプロパティーのリスト */
	private List verticalScales;
	/** プールする最大レコード */
	private int maxMapSize;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public GraphViewState(
			String tagName,
			Attributes atts,
			PageState pageState) {
		super(tagName, atts, pageState, null);

		String isViewVerticalScaleStr = AttributesUtil.getValue("isViewVerticalScale", atts);
		if (isViewVerticalScaleStr != null) {
			isViewVerticalScale = Boolean.valueOf(isViewVerticalScaleStr).booleanValue();
		}

		String isMouseClickEnableStr = AttributesUtil.getValue("isMouseClickEnable", atts);
		if (isMouseClickEnableStr != null) {
			isMouseClickEnable = Boolean.valueOf(isMouseClickEnableStr).booleanValue();
		}

		String isDrawStringStr = AttributesUtil.getValue("isDrawString", atts);
		if (isDrawStringStr != null) {
			isDrawString = Boolean.valueOf(isDrawStringStr).booleanValue();
		}
		stringColor = ColorFactory.getColor(AttributesUtil.getValue("stringColor", atts));

		String isScrollBarVisibleStr = AttributesUtil.getValue("isScrollBarVisible", atts);
		if (isScrollBarVisibleStr != null) {
			isScrollBarVisible = Boolean.valueOf(isScrollBarVisibleStr).booleanValue();
		}
		verticalScales = new ArrayList();
		
		String maxMapSizeStr = AttributesUtil.getValue("maxMapSize", atts);
		if (maxMapSizeStr != null) {
			maxMapSize = Integer.parseInt(maxMapSizeStr);
		} else {
			maxMapSize = Integer.parseInt(EnvironmentManager.get(
					"/server/logging/maxrecord", "4096"));
		}
	}
	
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("verticallyscale")) {
			stack.push(new VerticallyScaleState(tagName, atts, this));
		} else {
			logger.debug("tagName : " + tagName);
		}
		super.add(tagName, atts, stack);
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (tagName.equals("graphview")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Pop : " + DisplayState.toString(tagName, stack));
			}
	
			try {
				TrendGraphView view = new TrendGraphView(model,
						isViewVerticalScale, isMouseClickEnable, isDrawString, maxMapSize);
				view.setStrokeWidth(strokeWidth);
				view.setScrollBarVisible(isScrollBarVisible);
				if (foreground != null) {
					view.setForeground(foreground);
				}
				if (background != null) {
					view.setBackground(background);
				}
				if (stringColor != null) {
					view.setStringColor(stringColor);
				}
		
				if (x != null && y != null) {
					view.setLocation(
						Integer.parseInt(x),
						Integer.parseInt(y));
				}
				if (width != null && height != null) {
					view.setSize(
						Integer.parseInt(width),
						Integer.parseInt(height));
				}
		
				pageState.addPageSymbol(view);
				addVerticallyScale();
				disConnectExplanatoryNotesText();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}
	
	private void disConnectExplanatoryNotesText() {
		for (int i = 0; i < model.getSeriesSize(); i++) {
			ExplanatoryNotesText symbol = model.getSymbol(i);
			if (null != symbol) {
				symbol.disConnect();
			}
		}
	}

	private void addVerticallyScale() {
		for (Iterator i = verticalScales.iterator(); i.hasNext();) {
			VerticallyScaleProperty p = (VerticallyScaleProperty) i.next();
			addVerticallyScale(p);
		}
	}

	private void addVerticallyScale(VerticallyScaleProperty p) {
		pageState.addPageSymbol(getVerticallyScale(p));
	}

	private VerticallyScale getVerticallyScale(VerticallyScaleProperty p) {
		VerticallyScale scale = null;
		if (p.isRight()) {
			scale = VerticallyScale.createRightStringScale(model, p.getSeries(), false, p.isTrend(), null);
		} else {
			scale = VerticallyScale.createLeftStringScale(model, p.getSeries(), false, p.isTrend(), null);
		}
		setProperty(scale, p);
		return scale;
	}

	private void setProperty(VerticallyScale scale, VerticallyScaleProperty p) {
		scale.setScaleButtonVisible(p.isScaleButtonVisible());
		scale.setForeground(p.getForeground());
		scale.setBackground(p.getBackground());
		scale.setBounds(p.getX(), p.getY(), p.getWidth(), p.getHeight());
	}

	void addVerticallyScaleProperty(int series, boolean isScaleButtonVisible,
			boolean isRight, int x, int y, int width, int height,
			Color foreground, Color background, boolean isTrend) {
		verticalScales.add(new VerticallyScaleProperty(series, isScaleButtonVisible,
				isRight, x, y, width, height, foreground, background, isTrend));
	}
	
	static class VerticallyScaleProperty {
		private final int series;
		private final boolean isScaleButtonVisible;
		private final boolean isRight;
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		private final Color foreground;
		private final Color background;
		private final boolean isTrend;
		
		VerticallyScaleProperty(int series, boolean isScaleButtonVisible, boolean isRight,
				int x, int y, int width, int height, Color foreground, Color background, boolean isTrend) {
			this.series = series;
			this.isScaleButtonVisible = isScaleButtonVisible;
			this.isRight = isRight;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.foreground = foreground;
			this.background = background;
			this.isTrend = isTrend;
		}

		public Color getBackground() {
			return background;
		}

		public Color getForeground() {
			return foreground;
		}

		public int getHeight() {
			return height;
		}

		public boolean isRight() {
			return isRight;
		}

		public boolean isScaleButtonVisible() {
			return isScaleButtonVisible;
		}

		public int getSeries() {
			return series;
		}

		public int getWidth() {
			return width;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public boolean isTrend() {
			return isTrend;
		}
		
	}
}
