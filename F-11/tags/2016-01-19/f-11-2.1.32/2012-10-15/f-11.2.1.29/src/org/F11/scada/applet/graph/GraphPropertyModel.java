/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/GraphPropertyModel.java,v 1.13.2.5 2007/07/11 07:47:18 frdm Exp $
 * $Revision: 1.13.2.5 $
 * $Date: 2007/07/11 07:47:18 $
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

package org.F11.scada.applet.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.util.Collection;

public interface GraphPropertyModel {
    /** グループ変更イベントのプロパティー名 */
    String GROUP_CHANGE_EVENT =
        GraphPropertyModel.class.getName() + "_GROUP_CHANGE_EVENT";
    /** 横幅変更イベントのプロパティー名 */
    String X_SCALE_CHANGE_EVENT =
    	GraphPropertyModel.class.getName() + "_X_SCALE_CHANGE_EVENT";

	/**
	 * インスタンスをディープコピーします。
	 * @return GraphPropertyModel ディープコピーされたインスタンス。
	 */
	public GraphPropertyModel deepCopy();

	/**
	 * リスナーを追加します。
	 * @param l PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener l);

	/**
	 * リスナーを削除します。
	 * @param l PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener l);

	/**
	 * 縦スケールの目盛りの数を返します。
	 * @return 縦スケールの目盛りの数
	 */
	public int getVerticalScaleCount();

	/**
	 * 縦スケールの目盛り一つ分のピクセル長を返します。
	 * @return 縦スケールの目盛り一つ分のピクセル長
	 */
	public int getVerticalScaleHeight();

	/**
	 * 横スケールメモリの目盛りの数を返します。
	 * @return 横スケールメモリの目盛りの数
	 */
	public int getHorizontalScaleCount();

	/**
	 * 横スケールメモリの目盛り一つ分の時間（㍉秒）を返します。
	 * @return 横スケールメモリの目盛り一つ分の時間（㍉秒）
	 */
	public long getHorizontalScaleWidth();

	/**
	 * 横スケールメモリの目盛りの数を設定します。
	 * @param horizontalScaleCount 横スケールメモリの目盛りの数
	 */
	public void setHorizontalScaleCount(int horizontalScaleCount);

	/**
	 * 横スケールメモリの目盛り一つ分の時間（㍉秒）を設定します。
	 * @param horizontalScaleWidth 横スケールメモリの目盛り一つ分の時間（㍉秒）
	 */
	public void setHorizontalScaleWidth(long horizontalScaleWidth);

	/**
	 * グラフビュー以外のコンポーネントの余白部分のインセッツを返します。
	 * @return コンポーネントの余白部分のインセッツ
	 */
	public Insets getInsets();

	/**
	 * グラフビュー以外の余白部分のインセッツを返します。
	 * @return コンポーネントの余白部分のインセッツ
	 */
	public Insets getGraphiViewInsets();

	/**
	 * グラフ・凡例の Color オブジェクトの配列を返します。
	 * @return グラフ・凡例の Color オブジェクトの配列
	 */
	public Color[] getColors();

	/**
	 * このプロパティで設定されている、グループのサイズを返します。
	 * @return グループのサイズ
	 */
	public int getGroupSize();

	/**
	 * 現在設定されているグループを返します。
	 * @return 現在設定されているグループ
	 */
	public int getGroup();

	/**
	 * 現在設定されているグループ名を返します。
	 * @return 現在設定されているグループ名
	 */
	public String getGroupName();

	/**
	 * 選択グループを設定します
	 * @param group グループ
	 */
	public void setGroup(int group);

	/**
	 * 次のグループに移動します。
	 */
	void nextGroup();
	
	/**
	 * 前のグループに移動します。
	 */
	void prevGroup();

	/**
	 * 現在位置づけられているグループの、シリーズサイズを返します。
	 * @return 現在位置づけられているグループの、シリーズサイズ
	 */
	public int getSeriesSize();

	/**
	 * 現在位置づけられているグループの、縦スケールの最小値を返します。
	 * @param series シリーズ
	 * @return 縦スケールの最小値
	 */
	public double getVerticalMinimum(int series);

	/**
	 * 現在位置づけられているグループの、縦スケールの最大値を返します。
	 * @param series シリーズ
	 * @return 縦スケールの最大値
	 */
	public double getVerticalMaximum(int series);

	/**
	 * 現在位置づけられているグループの、縦スケールの最小値を設定します。
	 * @param series シリーズ
	 * @param verticalMinimum 縦スケールの最小値
	 */
	public void setVerticalMinimum(int series, double verticalMinimum);

	/**
	 * 現在位置づけられているグループの、縦スケールの最大値を設定します。
	 * @param series シリーズ
	 * @param verticalMaximum 縦スケールの最大値
	 */
	public void setVerticalMaximum(int series, double verticalMaximum);

	/**
	 * 現在位置づけられているグループの、データプロバイダ名を返します。
	 * @param series シリーズ
	 * @return データプロバイダ名
	 */
	public String getDataProviderName(int series);

	/**
	 * 現在位置づけられているグループの、データホルダ名を返します。
	 * @param series シリーズ
	 * @return データホルダ名
	 */
	public String getDataHolderName(int series);

	/**
	 * 現在位置づけられているグループの、参照値を返します。
	 * @param series シリーズ
	 * @param fold 折り返し位置
	 * @return 参照値
	 */
	public double getReferenceValue(int series, int fold);

	/**
	 * 現在位置づけられているグループの、参照値を返します。
	 * @param series シリーズ
	 * @return 参照値
	 */
	public double getReferenceValue(int series);

	/**
	 * 現在位置づけられているグループの、参照値を設定します。
	 * @param series シリーズ
	 * @param fold 折り返し位置
	 * @param referenceValue 参照値
	 */
	public void setReferenceValue(int series, int fold, double referenceValue);

	/**
	 * 現在位置づけられているグループの、参照値を設定します。
	 * @param series シリーズ
	 * @param referenceValue 参照値
	 */
	public void setReferenceValue(int series, double referenceValue);

	/**
	 * 現在位置づけられているグループの、参照時刻を設定します。
	 * @param series シリーズ
	 * @param fold 折り返し位置
	 * @param referenceTime 参照時刻
	 */
	public void setReferenceTime(int series, int fold, Timestamp referenceTime);

	/**
	 * 現在位置づけられているグループの、参照時刻を設定します。
	 * @param series シリーズ
	 * @param referenceTime 参照時刻
	 */
	public void setReferenceTime(int series, Timestamp referenceTime);

	/**
	 * 現在位置づけられているグループの、参照時刻を返します。
	 * @param series シリーズ
	 * @param fold 折り返し位置
	 * @return 参照時刻
	 */
	public Timestamp getReferenceTime(int series, int fold);

	/**
	 * 現在位置づけられているグループの、参照時刻を返します。
	 * @param series シリーズ
	 * @return 参照時刻
	 */
	public Timestamp getReferenceTime(int series);

	/**
	 * 現在位置づけられているグループの、列インデックスを返します。
	 * @return 列インデックスの配列
	 */
	public int[] getGroupColumnIndex();
	
	/**
	 * シリーズプロパティ(グループの設定)を追加します。
	 * @param property シリーズプロパティ
	 */
	public void addSeriesProperty(GraphSeriesProperty property);
	
	/**
	 * 現在表示しているハンドラー名のインデックスを設定します。
	 * @param name ハンドラー名
	 */
	public void setListHandlerIndex(int index);
	
	/**
	 * 現在インデックスで指定されているハンドラー名を返します。
	 * @return 現在インデックスで指定されているハンドラー名
	 */
	public String getListHandlerName();
	
	/**
	 * 折り返し回数を返します。
	 * @return 折り返し回数
	 */
	public int getFoldCount();

	/**
	 * 現在位置づけられているグループの、ポイント名称を返します。
	 * @param series シリーズ
	 * @return 現在位置づけられているグループの、列インデックスを返します。
	 */
	public String getPointName(int series);

	/**
	 * 現在位置づけられているグループの、単位記号を返します。
	 * @param series シリーズ
	 * @return 現在位置づけられているグループの、単位記号を返します。
	 */
	public String getPointMark(int series);
	
	/**
	 * 現在位置づけられているグループの、現在値表示シンボルを返します。
	 * @param series シリーズ
	 * @return 現在位置づけられているグループの、現在値表示シンボルを返します。
	 */
	public ExplanatoryNotesText getSymbol(int series);
	
	/**
	 * 現在クリックされている参照日時を返します
	 * @return 現在クリックされている参照日時を返します
	 */
	public Timestamp getReferenceTime();
	
	/**
	 * 現在クリックされている参照日時を設定します
	 * @param time 現在クリックされている参照日時を設定します
	 */
	public void setReferenceTime(Timestamp time);
	
	/**
	 * 表示されるグラフの横ピクセル数を返します。
	 * @return 表示されるグラフの横ピクセル数を返します。
	 */
	public int getHorizontalPixcelWidth();
	
	/**
	 * X軸スケールの目盛り幅を返します。
	 * @return X軸スケールの目盛り幅を返します。
	 */
	public int getScaleOneHeightPixel();
	
	/**
	 * 参照表示の文字色を返します
	 * @return 参照表示の文字色を返します
	 */
	public Color getExplanatoryColor();

	/**
	 * 参照表示のフォントを返します
	 * @return 参照表示のフォントを返します
	 */
	public Font getExplanatoryFont();

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener);

    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener);


	/**
	 * 現在設定されているグループ名を返します。
	 * @return 現在設定されているグループ名
	 */
	public Collection getGroupNames();
	
	/**
	 * 1列目のフォーマット文字列を返します
	 * @return 1列目のフォーマット文字列を返します
	 */
	public String getFirstFormat();

	/**
	 * 1列目のフォーマット文字列を設定します
	 * @param format 1列目のフォーマット文字列を設定します
	 */
	public void setFirstFormat(String format);
	
	/**
	 * 2列目のフォーマット文字列を返します
	 * @return 2列目のフォーマット文字列を返します
	 */
	public String getSecondFormat();
	
	/**
	 * 2列目のフォーマット文字列を設定します
	 * @param format 2列目のフォーマット文字列を設定します
	 */
	public void setSecondFormat(String format);
	
	/**
	 * 縦スケールのプロパティーを返します。
	 * @return 縦スケールのプロパティーを返します
	 */
	public VerticallyScaleProperty getVerticallyScaleProperty();
}
