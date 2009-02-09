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

package org.F11.scada.applet.ngraph;


import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.F11.scada.applet.ngraph.model.GraphModel;
import org.F11.scada.server.register.HolderString;

/**
 * トレンドグラフのプロパティ。
 * 
 * @author maekawa
 * 
 */
public class GraphProperties {
	private final PropertyChangeSupport changeSupport;
	private int horizontalForAllSpanMode = 112;
	private int horizontalForSelectSpanMode = 168;
	private int horizontalCount = 5;
	private long horizontalLineSpan = 18000000L;
	private String dateFormat = "%1$tm/%1$td";
	private String timeFormat = "%1$tH:%1$tM";
	private int verticalScale = 48;
	private int verticalCount = 10;
	private int verticalLine;
	private int scalePixcelSize = 5;
	private Insets insets = new Insets(50, 80, 60, 50);
	private Font font = new Font("Monospaced", Font.PLAIN, 18);
	private Color lineColor = Color.WHITE;
	private Color backGround = new Color(0, 0, 135);
	private Color verticalScaleColor = new Color(64, 95, 237);
	/** シリーズグループのリスト */
	private List<SeriesGroup> seriesGroups;
	private int groupNo;

	public GraphProperties() {
		changeSupport = new PropertyChangeSupport(this);
		seriesGroups = getTestGroup();
	}

	public void addPropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * 縦軸の総ピクセル verticalScale × verticalCount
	 * 
	 * @return 縦軸の総ピクセル verticalScale × verticalCount
	 */
	public int getVerticalLine() {
		if (verticalLine == 0) {
			verticalLine = verticalScale * verticalCount;
		}
		return verticalLine;
	}

	/**
	 * 縦1目盛のピクセル長
	 * 
	 * @return 縦1目盛のピクセル長
	 */
	public int getVerticalScale() {
		return verticalScale;
	}

	/**
	 * 縦1目盛のピクセル長
	 * 
	 * @param verticalScale 縦1目盛のピクセル長
	 */
	public void setVerticalScale(int verticalScale) {
		this.verticalScale = verticalScale;
	}

	/**
	 * 縦目盛の数(通常は10)
	 * 
	 * @return　縦目盛の数(通常は10)
	 */
	public int getVerticalCount() {
		return verticalCount;
	}

	/**
	 * 縦目盛の数(通常は10)
	 * 
	 * @param verticalCount 縦目盛の数(通常は10)
	 */
	public void setVerticalCount(int verticalCount) {
		this.verticalCount = verticalCount;
	}

	/**
	 * 横軸の総ピクセル
	 * 
	 * @param isAllSpanDisplayMode スパン全表示モードなら true を略表示モードなら false を指定
	 * 
	 * @return 横軸の総ピクセル
	 */
	public int getHorizontalLine(boolean isAllSpanDisplayMode) {
		return getHorizontalScale(isAllSpanDisplayMode) * horizontalCount;
	}

	/**
	 * 縦横スケール目盛自身のピクセル数
	 * 
	 * @return 縦横スケール目盛自身のピクセル数
	 */
	public int getScalePixcelSize() {
		return scalePixcelSize;
	}

	/**
	 * 縦横スケール目盛自身のピクセル数を設定
	 * 
	 * @param scalePixcelSize 縦横スケール目盛自身のピクセル数
	 */
	public void setScalePixcelSize(int scalePixcelSize) {
		this.scalePixcelSize = scalePixcelSize;
	}

	/**
	 * 横スケールの表示期間をミリ秒で返します。
	 * 
	 * @return 横スケールの表示期間をミリ秒で返します。
	 */
	public long getHorizontalLineSpan() {
		return horizontalLineSpan;
	}

	/**
	 * 横スケールの表示期間をミリ秒で設定します。
	 * 
	 * @param horizontalLineSpan 横スケールの表示期間をミリ秒で設定します。
	 */
	public void setHorizontalLineSpan(long horizontalLineSpan) {
		this.horizontalLineSpan = horizontalLineSpan;
	}

	/**
	 * 横軸1目盛の長さ(ピクセル)
	 * 
	 * @param isAllSpanDisplayMode
	 * 
	 * @return 横軸1目盛の長さ(ピクセル)
	 */
	public int getHorizontalScale(boolean isAllSpanDisplayMode) {
		return isAllSpanDisplayMode
			? horizontalForAllSpanMode
			: horizontalForSelectSpanMode;
	}

	/**
	 * スパン全表示の横幅の目盛ひとつ分ピクセル数
	 * 
	 * @param horizontalForAllSpanMode スパン全表示の横幅の目盛ひとつ分ピクセル数
	 */
	public void setHorizontalForAllSpanMode(int horizontalForAllSpanMode) {
		this.horizontalForAllSpanMode = horizontalForAllSpanMode;
	}

	/**
	 * スパン略表示の横幅の目盛ひとつ分ピクセル数
	 * 
	 * @param horizontalForSelectSpanMode スパン略表示の横幅の目盛ひとつ分ピクセル数
	 */
	public void setHorizontalForSelectSpanMode(int horizontalForSelectSpanMode) {
		this.horizontalForSelectSpanMode = horizontalForSelectSpanMode;
	}

	/**
	 * 横軸の目盛数
	 * 
	 * @return 横軸の目盛数
	 */
	public int getHorizontalCount() {
		return horizontalCount;
	}

	/**
	 * 横軸の目盛数
	 * 
	 * @param horizontalCount 横軸の目盛数
	 */
	public void setHorizontalCount(int horizontalCount) {
		this.horizontalCount = horizontalCount;
	}

	/**
	 * 上段の横スケール単位のフォーマット文字列 フォーマットは{@link java.util.Formatter}を利用しています。
	 * 
	 * @return 上段の横スケール単位のフォーマット文字列
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * 上段の横スケール単位のフォーマット文字列
	 * 
	 * @param dateFormat 上段の横スケール単位のフォーマット文字列
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * 下段の横スケール単位のフォーマット文字列 フォーマットは{@link java.util.Formatter}を利用しています。
	 * 
	 * @return 下段の横スケール単位のフォーマット文字列
	 */
	public String getTimeFormat() {
		return timeFormat;
	}

	/**
	 * 下段の横スケール単位のフォーマット文字列
	 * 
	 * @param timeFormat 下段の横スケール単位のフォーマット文字列
	 */
	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	/**
	 * グラフ周りの余白
	 * 
	 * @return グラフ周りの余白
	 */
	public Insets getInsets() {
		return insets;
	}

	/**
	 * グラフ周りの余白
	 * 
	 * @param insets グラフ周りの余白
	 */
	public void setInsets(Insets insets) {
		this.insets = insets;
	}

	/**
	 * グラフに使用するフォント
	 * 
	 * @return グラフに使用するフォント
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * グラフに使用するフォント
	 * 
	 * @param font グラフに使用するフォント
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * 元になる縦横の線の色。横軸の文字列の色にも使用する。
	 * 
	 * @return 元になる縦横の線の色。横軸の文字列の色にも使用する。
	 */
	public Color getLineColor() {
		return lineColor;
	}

	/**
	 * 元になる縦横の線の色。横軸の文字列の色にも使用する。
	 * 
	 * @param lineColor 元になる縦横の線の色。横軸の文字列の色にも使用する。
	 */
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	/**
	 * グラフの背景色
	 * 
	 * @return グラフの背景色
	 */
	public Color getBackGround() {
		return backGround;
	}

	/**
	 * グラフの背景色
	 * 
	 * @param backGround グラフの背景色
	 */
	public void setBackGround(Color backGround) {
		this.backGround = backGround;
	}

	/**
	 * 目盛り線(点線)の色
	 * 
	 * @return 目盛り線(点線)の色
	 */
	public Color getVerticalScaleColor() {
		return verticalScaleColor;
	}

	/**
	 * 目盛り線(点線)の色
	 * 
	 * @param verticalScaleColor 目盛り線(点線)の色
	 */
	public void setVerticalScaleColor(Color verticalScaleColor) {
		this.verticalScaleColor = verticalScaleColor;
	}

	/**
	 * シリーズをグループに追加します。
	 * 
	 * @param s シリーズをグループに追加します。
	 */
	public void addSeriesGroup(SeriesGroup s) {
		if (null == seriesGroups) {
			seriesGroups = new ArrayList<SeriesGroup>();
		}
		seriesGroups.add(s);
	}

	/**
	 * シリーズをグループから削除します。
	 * 
	 * @param groupNo グループNo.
	 */
	public void removeSeriesGroup(int groupNo) {
		if (null != seriesGroups) {
			seriesGroups.remove(groupNo);
		}
	}

	/**
	 * カレントグループNo.のシリーズグループを返します。
	 * 
	 * @return カレントグループNo.のシリーズグループを返します。
	 */
	public SeriesGroup getSeriesGroup() {
		return seriesGroups.get(groupNo);
	}

	/**
	 * シリーズグループのリストを返します。
	 * 
	 * @return シリーズグループリストを返します。
	 */
	public List<SeriesGroup> getSeriesGroups() {
		return Collections.unmodifiableList(seriesGroups);
	}

	/**
	 * カレントグループNo.を返します。
	 * 
	 * @return カレントグループNo.を返します。
	 */
	public int getGroupNo() {
		return groupNo;
	}

	/**
	 * カレントグループNo.を設定します。
	 * 
	 * @param groupNo グループNo.
	 * @return グループNo.を変更できた場合は true をそうでない場合は false を返します。
	 */
	public boolean setGroupNo(int groupNo) {
		boolean validGroupNo = isValidGroupNo(groupNo);
		if (validGroupNo) {
			int old = this.groupNo;
			this.groupNo = groupNo;
			changeSupport.firePropertyChange(
				GraphModel.GROUP_CHANGE,
				old,
				this.groupNo);
		}
		return validGroupNo;
	}

	private boolean isValidGroupNo(int groupNo) {
		return 0 <= groupNo && seriesGroups.size() - 1 >= groupNo;
	}

	public SeriesGroup getTestData() {
		List<SeriesProperties> serieses = new ArrayList<SeriesProperties>();
		serieses.add(getProperty(
			true,
			Color.yellow,
			"",
			"施設棟　受電　電流",
			null,
			null,
			"A",
			"%3.0f",
			0,
			100,
			0,
			"P1_D_500_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.magenta,
			"",
			"施設棟　受電　電圧",
			null,
			null,
			"V",
			"%04.0f",
			0,
			90,
			1,
			"P1_D_501_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.cyan,
			"",
			"施設棟　受電　電力",
			null,
			null,
			"kW",
			"%3.1f",
			0,
			90,
			2,
			"P1_D_502_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.red,
			"",
			"施設棟　受電　無効電力",
			null,
			null,
			"kVar",
			"%3.2f",
			0,
			100,
			3,
			"P1_D_503_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.green,
			"",
			"施設棟　受電　力率",
			null,
			null,
			null,
			"%03.1f",
			0,
			100,
			4,
			"P1_D_504_BcdSingle"));
		serieses.add(getProperty(
			false,
			Color.white,
			"UNIT-06",
			"名前06",
			null,
			null,
			null,
			"%04.1f",
			0,
			100,
			5,
			"P1_D_506_BcdSingle"));
		return new SeriesGroup("施設棟　電源", serieses);
	}

	private SeriesProperties getProperty(
			boolean visible,
			Color color,
			String unit,
			String name,
			Float refValue,
			Float nowValue,
			String unitMark,
			String verticalFormat,
			float min,
			float max,
			int index,
			String holderString) {
		SeriesProperties p = new SeriesProperties();
		p.setVisible(visible);
		p.setColor(color);
		p.setUnit(unit);
		p.setName(name);
		p.setReferenceValue(refValue);
		p.setNowValue(nowValue);
		p.setUnitMark(unitMark);
		p.setVerticalFormat(verticalFormat);
		p.setMin(min);
		p.setMax(max);
		p.setIndex(index);
		p.setHolderString(getHolderString(holderString));
		return p;
	}

	private HolderString getHolderString(String holderString) {
		return null == holderString ? null : new HolderString(holderString);
	}

	private SeriesGroup getTestData2() {
		List<SeriesProperties> serieses = new ArrayList<SeriesProperties>();
		serieses.add(getProperty(
			true,
			Color.yellow,
			"",
			"東棟　受電　電流",
			null,
			null,
			"kA",
			"%3.0f",
			0,
			2000,
			0,
			"P1_D_3300_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.magenta,
			"",
			"東棟　受電　電圧",
			null,
			null,
			"V",
			"%04.0f",
			0,
			2000,
			1,
			"P1_D_3301_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.cyan,
			"",
			"東棟　受電　電力",
			null,
			null,
			"kW",
			"%3.1f",
			0,
			5000,
			2,
			"P1_D_3307_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.red,
			"",
			"東棟　受電　無効電力",
			null,
			null,
			"kVar",
			"%3.2f",
			0,
			5000,
			3,
			"P1_D_3310_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.green,
			"",
			"東棟　受電　力率",
			null,
			null,
			null,
			"%03.1f",
			0,
			60,
			4,
			"P1_D_3316_BcdSingle"));
		serieses.add(getProperty(
			false,
			Color.white,
			"UNIT-06",
			"名前06",
			null,
			null,
			null,
			"%04.1f",
			0,
			100,
			5,
			"P1_D_3316_BcdSingle"));
		return new SeriesGroup("東棟　電源", serieses);
	}

	private List<SeriesGroup> getTestGroup() {
		ArrayList<SeriesGroup> l = new ArrayList<SeriesGroup>();
		l.add(getTestData());
		l.add(getTestData2());
		return l;
	}
}
