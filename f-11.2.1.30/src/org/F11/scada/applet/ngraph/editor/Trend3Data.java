/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.applet.ngraph.editor;

import static org.F11.scada.applet.ngraph.util.XmlAttributeUtil.getAttribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.F11.scada.applet.ngraph.HorizontalScaleButtonProperty;

/**
 * ページ定義のトレンドグラフプロパティー
 *
 * @author maekawa
 *
 */
public class Trend3Data implements Serializable {
	private static final long serialVersionUID = -7411129957549137491L;
	private Integer x;
	private Integer y;
	private Integer width;
	private Integer height;
	private Integer horizontalForAllSpanMode;
	private Integer horizontalForSelectSpanMode;
	private String dateFormat;
	private String timeFormat;
	private Integer verticalScale;
	private Integer verticalCount;
	private Integer compositionVerticalCount;
	private Integer scalePixcelSize;
	private String insets;
	private String font;
	private String lineColor;
	private String backGround;
	private String verticalScaleColor;
	private String pagefile;
	private List<SeriesData> seriesDatas = new ArrayList<SeriesData>();
	private String seriesColors;
	/** 横スケール変更ボタンのプロパティー */
	private List<HorizontalScaleButtonProperty> horizontalScaleButtonProperties;
	private Integer maxRecord;
	private Boolean isVisibleToolbar;
	private Boolean isVisibleSeries;
	private Boolean isVisibleStatus;
	private Boolean isVisibleScroolbar;
	private Boolean isVisibleReferenceLine;
	private Boolean isVisibleVerticalString;
	private Boolean isCompositionMode;
	private Boolean isAllSpanDisplayMode;
	private Integer verticalLineInterval;

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getHorizontalForAllSpanMode() {
		return horizontalForAllSpanMode;
	}

	public void setHorizontalForAllSpanMode(Integer horizontalForAllSpanMode) {
		this.horizontalForAllSpanMode = horizontalForAllSpanMode;
	}

	public Integer getHorizontalForSelectSpanMode() {
		return horizontalForSelectSpanMode;
	}

	public void setHorizontalForSelectSpanMode(
		Integer horizontalForSelectSpanMode) {
		this.horizontalForSelectSpanMode = horizontalForSelectSpanMode;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	public Integer getVerticalScale() {
		return verticalScale;
	}

	public void setVerticalScale(Integer verticalScale) {
		this.verticalScale = verticalScale;
	}

	public Integer getVerticalCount() {
		return verticalCount;
	}

	public void setVerticalCount(Integer verticalCount) {
		this.verticalCount = verticalCount;
	}

	public Integer getCompositionVerticalCount() {
		return compositionVerticalCount;
	}

	public void setCompositionVerticalCount(Integer compositionVerticalCount) {
		this.compositionVerticalCount = compositionVerticalCount;
	}

	public Integer getScalePixcelSize() {
		return scalePixcelSize;
	}

	public void setScalePixcelSize(Integer scalePixcelSize) {
		this.scalePixcelSize = scalePixcelSize;
	}

	public String getInsets() {
		return insets;
	}

	public void setInsets(String insets) {
		this.insets = insets;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getLineColor() {
		return lineColor;
	}

	public void setLineColor(String lineColor) {
		this.lineColor = lineColor;
	}

	public String getBackGround() {
		return backGround;
	}

	public void setBackGround(String backGround) {
		this.backGround = backGround;
	}

	public String getVerticalScaleColor() {
		return verticalScaleColor;
	}

	public void setVerticalScaleColor(String verticalScaleColor) {
		this.verticalScaleColor = verticalScaleColor;
	}

	public String getPagefile() {
		return pagefile;
	}

	public void setPagefile(String pagefile) {
		this.pagefile = pagefile;
	}

	public List<SeriesData> getSeriesDatas() {
		return seriesDatas;
	}

	public void setSeriesDatas(List<SeriesData> seriesDatas) {
		this.seriesDatas = seriesDatas;
	}

	public void addSeriesData(SeriesData seriesData) {
		this.seriesDatas.add(seriesData);
	}

	public String getSeriesColors() {
		return seriesColors;
	}

	public void setSeriesColors(String seriesColors) {
		this.seriesColors = seriesColors;
	}

	public List<HorizontalScaleButtonProperty> getHorizontalScaleButtonProperty() {
		return horizontalScaleButtonProperties;
	}

	public void setHorizontalScaleButtonProperty(
		List<HorizontalScaleButtonProperty> horizontalScaleButtonProperties) {
		this.horizontalScaleButtonProperties = horizontalScaleButtonProperties;
	}

	public void addHorizontalScaleButtonProperty(
		HorizontalScaleButtonProperty property) {
		if (null == horizontalScaleButtonProperties) {
			horizontalScaleButtonProperties =
				new ArrayList<HorizontalScaleButtonProperty>();
		}
		horizontalScaleButtonProperties.add(property);
	}

	public Integer getMaxRecord() {
		return maxRecord;
	}

	public void setMaxRecord(Integer maxRecord) {
		this.maxRecord = maxRecord;
	}

	public Boolean isVisibleToolbar() {
		return isVisibleToolbar;
	}

	public void setVisibleToolbar(Boolean isVisibleToolbar) {
		this.isVisibleToolbar = isVisibleToolbar;
	}

	public Boolean isVisibleSeries() {
		return isVisibleSeries;
	}

	public void setVisibleSeries(Boolean isVisibleSeries) {
		this.isVisibleSeries = isVisibleSeries;
	}

	public Boolean isVisibleStatus() {
		return isVisibleStatus;
	}

	public void setVisibleStatus(Boolean isVisibleStatus) {
		this.isVisibleStatus = isVisibleStatus;
	}

	public Boolean isVisibleScroolbar() {
		return isVisibleScroolbar;
	}

	public void setVisibleScroolbar(Boolean isVisibleScroolbar) {
		this.isVisibleScroolbar = isVisibleScroolbar;
	}

	public Boolean isVisibleReferenceLine() {
		return isVisibleReferenceLine;
	}

	public void setVisibleReferenceLine(Boolean isVisibleReferenceLine) {
		this.isVisibleReferenceLine = isVisibleReferenceLine;
	}

	public Boolean isVisibleVerticalString() {
		return isVisibleVerticalString;
	}

	public void setVisibleVerticalString(Boolean isVisibleVerticalString) {
		this.isVisibleVerticalString = isVisibleVerticalString;
	}

	public Boolean isCompositionMode() {
		return isCompositionMode;
	}

	public void setCompositionMode(Boolean isCompositionMode) {
		this.isCompositionMode = isCompositionMode;
	}

	public Boolean isAllSpanDisplayMode() {
		return isAllSpanDisplayMode;
	}

	public void setAllSpanDisplayMode(Boolean isAllSpanDisplayMode) {
		this.isAllSpanDisplayMode = isAllSpanDisplayMode;
	}

	public Integer getVerticalLineInterval() {
		return verticalLineInterval;
	}

	public void setVerticalLineInterval(Integer verticalLineInterval) {
		this.verticalLineInterval = verticalLineInterval;
	}

	public String getXmlString() {
		StringBuilder sb = new StringBuilder();
		sb.append("    <trendgraph3");
		sb.append(getAttribute("x", x));
		sb.append(getAttribute("y", y));
		sb.append(getAttribute("width", width));
		sb.append(getAttribute("height", height));
		sb.append(getAttribute("horizontalForAllSpanMode",
				horizontalForAllSpanMode));
		sb.append(getAttribute("horizontalForSelectSpanMode",
				horizontalForSelectSpanMode));
		sb.append(getAttribute("dateFormat", dateFormat));
		sb.append(getAttribute("timeFormat", timeFormat));
		sb.append(getAttribute("verticalScale", verticalScale));
		sb.append(getAttribute("verticalCount", verticalCount));
		sb.append(getAttribute("compositionVerticalCount",
				compositionVerticalCount));
		sb.append(getAttribute("scalePixcelSize", scalePixcelSize));
		sb.append(getAttribute("insets", insets));
		sb.append(getAttribute("font", font));
		sb.append(getAttribute("lineColor", lineColor));
		sb.append(getAttribute("backGround", backGround));
		sb.append(getAttribute("verticalScaleColor", verticalScaleColor));
		sb.append(getAttribute("pagefile", pagefile));
		sb.append(getAttribute("seriesColors", seriesColors));
		sb.append(getAttribute("maxRecord", maxRecord));
		sb.append(getAttribute("visibleToolbar", isVisibleToolbar));
		sb.append(getAttribute("visibleSeries", isVisibleSeries));
		sb.append(getAttribute("visibleStatus", isVisibleStatus));
		sb.append(getAttribute("visibleScroolbar", isVisibleScroolbar));
		sb.append(getAttribute("visibleReferenceLine", isVisibleReferenceLine));
		sb
				.append(getAttribute("visibleVerticalString",
						isVisibleVerticalString));
		sb.append(getAttribute("compositionMode", isCompositionMode));
		sb.append(getAttribute("allSpanDisplayMode", isAllSpanDisplayMode));
		sb.append(getAttribute("verticalLineInterval", verticalLineInterval));
		sb.append(">\n");
		sb.append("      <horizontalScaleButton>\n");
		for (HorizontalScaleButtonProperty hp : horizontalScaleButtonProperties) {
			sb.append(hp.getXmlString());
		}
		sb.append("      </horizontalScaleButton>\n");
		for (SeriesData sd : seriesDatas) {
			sb.append(sd.getXmlString());
		}
		sb.append("    </trendgraph3>\n");
		return sb.toString();
	}
}
