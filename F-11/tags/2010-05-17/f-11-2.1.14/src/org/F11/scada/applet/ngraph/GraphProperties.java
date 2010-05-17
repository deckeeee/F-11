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

/**
 * トレンドグラフのプロパティ。
 * 
 * @author maekawa
 * 
 */
public class GraphProperties {
	/** このプロパティーのリスナー */
	private final PropertyChangeSupport changeSupport;
	/** ｽｹｰﾙ全表示の横幅ピクセル数 */
	private int horizontalForAllSpanMode;
	/** ｽｹｰﾙ略表示の横幅ピクセル数 */
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
	/** 縦目盛のピクセル数 */
	private int verticalLine;
	/** 目盛線のピクセル数 */
	private int scalePixcelSize;
	/** 合成モード縦目盛の数 */
	private int compositionVerticalCount;
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
	private List<SeriesGroup> seriesGroups;
	/** カレントグループNo. */
	private int groupNo;
	/** ページ定義ファイル名 */
	private String pagefile;
	/** 横スケール変更ボタンのプロパティー */
	private List<HorizontalScaleButtonProperty> horizontalScaleButtonProperty;
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
	private boolean isCompositionMode = true;
	/** 現在のスパン表示モード */
	private boolean isAllSpanDisplayMode;

	public GraphProperties() {
		changeSupport = new PropertyChangeSupport(this);
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

	public void removePropertyChangeListeners() {
		PropertyChangeListener[] l = changeSupport.getPropertyChangeListeners();
		for (PropertyChangeListener listener : l) {
			changeSupport.removePropertyChangeListener(listener);
		}
	}

	/**
	 * 縦軸の総ピクセル verticalScale × compositionVerticalCount
	 * 
	 * @return 縦軸の総ピクセル verticalScale × compositionVerticalCount
	 */
	public int getVerticalLine() {
		if (verticalLine == 0) {
			verticalLine = verticalScale * compositionVerticalCount;
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
	 * 合成表示モードの縦目盛の数
	 * 
	 * @return 合成表示モードの縦目盛の数
	 */
	public int getCompositionVerticalCount() {
		return compositionVerticalCount;
	}

	/**
	 * 合成表示モードの縦目盛の数を設定します
	 * 
	 * @param compositionVerticalCount 合成表示モードの縦目盛の数
	 */
	public void setCompositionVerticalCount(int compositionVerticalCount) {
		this.compositionVerticalCount = compositionVerticalCount;
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
	public void setSeriesGroup(List<SeriesGroup> s) {
		seriesGroups = new ArrayList<SeriesGroup>(s);
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

	/**
	 * グラフ定義ファイル名を返します
	 * 
	 * @return グラフ定義ファイル名を返します
	 */
	public String getPagefile() {
		return pagefile;
	}

	/**
	 * グラフ定義ファイル名を設定します
	 * 
	 * @param pagefile グラフ定義ファイル名を設定します
	 */
	public void setPagefile(String pagefile) {
		this.pagefile = pagefile;
	}

	/**
	 * ｽｹｰﾙ全表示の横幅ピクセル数を返します
	 * 
	 * @return ｽｹｰﾙ全表示の横幅ピクセル数を返します
	 */
	public int getHorizontalForAllSpanMode() {
		return horizontalForAllSpanMode;
	}

	/**
	 * ｽｹｰﾙ略表示の横幅ピクセル数を返します
	 * 
	 * @return ｽｹｰﾙ略表示の横幅ピクセル数を返します
	 */
	public int getHorizontalForSelectSpanMode() {
		return horizontalForSelectSpanMode;
	}

	/**
	 * 横スケール変更ボタンのプロパティーを返します
	 * 
	 * @return 横スケール変更ボタンのプロパティーを返します
	 */
	public List<HorizontalScaleButtonProperty> getHorizontalScaleButtonProperty() {
		return horizontalScaleButtonProperty;
	}

	/**
	 * 横スケール変更ボタンのプロパティーを設定します
	 * 
	 * @param horizontalScaleButtonProperty 横スケール変更ボタンのプロパティー
	 */
	public void setHorizontalScaleButtonProperty(
			List<HorizontalScaleButtonProperty> horizontalScaleButtonProperty) {
		this.horizontalScaleButtonProperty = horizontalScaleButtonProperty;
	}

	/**
	 * トレンドグラフ最大表示レコードを返します
	 * 
	 * @return トレンドグラフ最大表示レコードを返します
	 */
	public int getMaxRecord() {
		return maxRecord;
	}

	/**
	 * トレンドグラフ最大表示レコードを設定します
	 * 
	 * @param maxRecord トレンドグラフ最大表示レコード
	 */
	public void setMaxRecord(int maxRecord) {
		this.maxRecord = maxRecord;
	}

	/**
	 * ツールバー表示の有無を返します
	 * 
	 * @return ツールバー表示の有無を返します
	 */
	public boolean isVisibleToolbar() {
		return isVisibleToolbar;
	}

	/**
	 * ツールバー表示の有無を設定します
	 * 
	 * @param isVisibleToolbar ツールバー表示の有無
	 */
	public void setVisibleToolbar(boolean isVisibleToolbar) {
		this.isVisibleToolbar = isVisibleToolbar;
	}

	/**
	 * シリーズ表示の有無を返します
	 * 
	 * @return シリーズ表示の有無を返します
	 */
	public boolean isVisibleSeries() {
		return isVisibleSeries;
	}

	/**
	 * シリーズ表示の有無を設定します
	 * 
	 * @param isVisibleSeries シリーズ表示の有無
	 */
	public void setVisibleSeries(boolean isVisibleSeries) {
		this.isVisibleSeries = isVisibleSeries;
	}

	/**
	 * ステータス表示の有無を返します
	 * 
	 * @return ステータス表示の有無
	 */
	public boolean isVisibleStatus() {
		return isVisibleStatus;
	}

	/**
	 * ステータス表示の有無を設定します
	 * 
	 * @param isVisibleStatus ステータス表示の有無
	 */
	public void setVisibleStatus(boolean isVisibleStatus) {
		this.isVisibleStatus = isVisibleStatus;
	}

	/**
	 * スクロールバー表示の有無を返します
	 * 
	 * @return スクロールバー表示の有無
	 */
	public boolean isVisibleScroolbar() {
		return isVisibleScroolbar;
	}

	/**
	 * スクロールバー表示の有無を設定します
	 * 
	 * @param isVisibleScroolbar スクロールバー表示の有無
	 */
	public void setVisibleScroolbar(boolean isVisibleScroolbar) {
		this.isVisibleScroolbar = isVisibleScroolbar;
	}

	/**
	 * 参照位置線表示の有無を返します
	 * 
	 * @return 参照位置線表示の有無を返します
	 */
	public boolean isVisibleReferenceLine() {
		return isVisibleReferenceLine;
	}

	/**
	 * 参照位置線表示の有無を設定します
	 * 
	 * @param isVisibleReferenceLine 参照位置線表示の有無
	 */
	public void setVisibleReferenceLine(boolean isVisibleReferenceLine) {
		this.isVisibleReferenceLine = isVisibleReferenceLine;
	}

	/**
	 * 縦スケール文字表示の有無を返します
	 * 
	 * @return 縦スケール文字表示の有無を返します
	 */
	public boolean isVisibleVerticalString() {
		return isVisibleVerticalString;
	}

	/**
	 * 縦スケール文字表示の有無を設定します
	 * 
	 * @param isVisibleVerticalString 縦スケール文字表示の有無
	 */
	public void setVisibleVerticalString(boolean isVisibleVerticalString) {
		this.isVisibleVerticalString = isVisibleVerticalString;
	}

	/**
	 * 現在の合成・分離表示モードを返します
	 * 
	 * @return 現在の合成・分離表示モードを返します
	 */
	public boolean isCompositionMode() {
		return isCompositionMode;
	}

	/**
	 * 現在の合成・分離表示モードを設定します
	 * 
	 * @param isCompositionMode 現在の合成・分離表示モード
	 */
	public void setCompositionMode(boolean isCompositionMode) {
		this.isCompositionMode = isCompositionMode;
	}

	/**
	 * 現在のスパン表示モードを返します
	 * 
	 * @return 現在のスパン表示モードを返します
	 */
	public boolean isAllSpanDisplayMode() {
		return isAllSpanDisplayMode;
	}

	/**
	 * 現在のスパン表示モードを設定します
	 * 
	 * @param isAllSpanDisplayMode 現在のスパン表示モード
	 */
	public void setAllSpanDisplayMode(boolean isAllSpanDisplayMode) {
		this.isAllSpanDisplayMode = isAllSpanDisplayMode;
	}

}
