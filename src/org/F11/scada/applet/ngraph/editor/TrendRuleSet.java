/*
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

package org.F11.scada.applet.ngraph.editor;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

/**
 * ページ定義からデータホルダを抽出するルールセットです。
 *
 * @author maekawa
 */
public class TrendRuleSet extends RuleSetBase {
	private static final String TREND3_DATA =
		"org.F11.scada.applet.ngraph.editor.Trend3Data";
	private static final String SERIES_DATA =
		"org.F11.scada.applet.ngraph.editor.SeriesData";
	private static final String SERIES_PROPERTY_DATA =
		"org.F11.scada.applet.ngraph.editor.SeriesPropertyData";
	private static final String HORIZONTAL_SCALE_BUTTON =
		"org.F11.scada.applet.ngraph.HorizontalScaleButtonProperty";
	private static final String[] PAGE_ATTRIBUTE =
		{ "width", "height", "name", "value", "cache" };
	private static final String[] TREND3_ATTRIBUTE =
		{
			"x",
			"y",
			"width",
			"height",
			"horizontalForAllSpanMode",
			"horizontalForSelectSpanMode",
			"dateFormat",
			"timeFormat",
			"verticalScale",
			"verticalCount",
			"compositionVerticalCount",
			"scalePixcelSize",
			"insets",
			"font",
			"lineColor",
			"backGround",
			"verticalScaleColor",
			"pagefile",
			"seriesColors",
			"maxRecord",
			"visibleToolbar",
			"visibleSeries",
			"visibleStatus",
			"visibleScroolbar",
			"visibleReferenceLine",
			"visibleVerticalString",
			"compositionMode",
			"allSpanDisplayMode",
			"verticalLineInterval"};
	private static final String[] SERIES_ATTRIBUTE = { "groupNo", "groupName" };
	private static final String[] SERIES_PROPERTIES =
		{
			"index",
			"visible",
			"color",
			"unit",
			"name",
			"unitMark",
			"verticalFormat",
			"max",
			"min",
			"holder",
			"convert" };
	private static final String[] HORIZONTAL_SCALE_BUTTON_PROPERTY =
		{
			"buttonText",
			"labelText",
			"horizontalCount",
			"horizontalAllSpanMode",
			"horizontalSelectSpanMode",
			"horizontalLineSpan",
			"recordeSpan",
			"logName", };
	private static final String PAGE_CHENGE_BUTTON =
		"org.F11.scada.applet.ngraph.editor.PageChangeButton";
	private static final String[] PAGE_CHENGE_BUTTON_ATTRIBUTE =
		{
			"changeto",
			"value",
			"x",
			"y",
			"tooltiptext",
			"opaque",
			"foreground",
			"background",
			"font",
			"font_style",
			"font_size",
			"width",
			"height", };

	public void addRuleInstances(Digester digester) {
		digester.addSetProperties("*/page", PAGE_ATTRIBUTE, PAGE_ATTRIBUTE);
		digester.addObjectCreate("*/trendgraph3", TREND3_DATA);
		digester.addSetNext("*/trendgraph3", "setTrend3Data", TREND3_DATA);
		digester.addSetProperties(
			"*/trendgraph3",
			TREND3_ATTRIBUTE,
			TREND3_ATTRIBUTE);
		digester.addObjectCreate("*/trendgraph3/series", SERIES_DATA);
		digester.addSetNext(
			"*/trendgraph3/series",
			"addSeriesData",
			SERIES_DATA);
		digester.addSetProperties(
			"*/trendgraph3/series",
			SERIES_ATTRIBUTE,
			SERIES_ATTRIBUTE);
		digester.addObjectCreate(
			"*/trendgraph3/series/series-property",
			SERIES_PROPERTY_DATA);
		digester.addSetNext(
			"*/trendgraph3/series/series-property",
			"addSeriesProperty",
			SERIES_PROPERTY_DATA);
		digester.addSetProperties(
			"*/trendgraph3/series/series-property",
			SERIES_PROPERTIES,
			SERIES_PROPERTIES);

		digester
			.addObjectCreate(
				"*/trendgraph3/horizontalScaleButton/horizontalScaleButton-property",
				HORIZONTAL_SCALE_BUTTON);
		digester
			.addSetNext(
				"*/trendgraph3/horizontalScaleButton/horizontalScaleButton-property",
				"addHorizontalScaleButtonProperty",
				HORIZONTAL_SCALE_BUTTON);
		digester
			.addSetProperties(
				"*/trendgraph3/horizontalScaleButton/horizontalScaleButton-property",
				HORIZONTAL_SCALE_BUTTON_PROPERTY,
				HORIZONTAL_SCALE_BUTTON_PROPERTY);

		digester.addObjectCreate("*/pagechangebutton", PAGE_CHENGE_BUTTON);
		digester.addSetNext(
			"*/pagechangebutton",
			"setPageChangeButton",
			PAGE_CHENGE_BUTTON);
		digester.addSetProperties(
			"*/pagechangebutton",
			PAGE_CHENGE_BUTTON_ATTRIBUTE,
			PAGE_CHENGE_BUTTON_ATTRIBUTE);
	}
}