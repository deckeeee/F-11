/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/DefaultGraphPropertyModel.java,v 1.16.2.7 2007/07/11 07:47:18 frdm Exp $
 * $Revision: 1.16.2.7 $
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * デフォルトの GraphPropertyModel 実装クラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DefaultGraphPropertyModel extends AbstractGraphPropertyModel {
	/** 縦スケールの目盛りの数 */
	private int verticalScaleCount;
	/** 縦スケールの目盛り一つ分のピクセル長 */
	private int verticalScaleHeight;
	/** 横スケールメモリの目盛りの数 */
	private int horizontalScaleCount;
	/** 横スケールメモリの目盛り一つ分の時間（㍉秒） */
	private long horizontalScaleWidth;
	/** グラフビュー以外コンポーネントの余白部分のインセッツ */
	private Insets insets;
	/** グラフビューコンポーネントの余白部分のインセッツ */
	private Insets graphiViewInsets;
	/** グラフ・凡例の Color オブジェクトの配列 */
	private Color[] colors;
	/** 現在位置づけられているグループ */
	private int group;
	/** 現在位置づけられているハンドラー名インデックス（使用するハンドラー名インデックス） */
	private int handlerIndex;
	/** 設定されているハンドラー名 */
	private String[] handlerName;
	/** 折り返し回数 */
	private int foldCount;
	/** 現在クリックされているの参照日時 */
	private Timestamp referenceTime;
	/** 表示されるグラフの横ピクセル数 */
	private int horizontalPixcelWidth;
	/** X軸スケールの目盛り幅 */
	private int scaleOneHeightPixel;
	/** 参照表示の文字色 */
	private Color explanatoryColor;
	/** 参照表示のフォント */
	private Font explanatoryFont;
	/**  1段目の日時フォーマット文字列 */
	private String firstFormat;
	/**  2段目の日時フォーマット文字列 */
	private String secondFormat;
	/** 縦スケールのプロパティー */
	private VerticallyScaleProperty verticallyScaleProperty;

	/**
	 * 全グループのシリーズプロパティ・リスト
	 * このリストには、GraphSeriesProperty が格納されています。
	 */
	private List seriesPropertyList;

	/**
	 * コンストラクタ
	 */
	public DefaultGraphPropertyModel(
			int verticalScaleCount,
			int verticalScaleHeight,
			int horizontalScaleCount,
			long horizontalScaleWidth,
			Insets insets,
			Insets graphiViewInsets,
			Color[] colors,
			String[] handlerName,
			int foldCount,
			int horizontalPixcelWidth,
			int scaleOneHeightPixel,
			Color explanatoryColor,
			Font explanatoryFont,
			String firstFormat,
			String secondFormat,
			VerticallyScaleProperty verticallyScaleProperty
			) {

		super();
		
		this.verticalScaleCount = verticalScaleCount;
		this.verticalScaleHeight = verticalScaleHeight;
		this.horizontalScaleCount = horizontalScaleCount;
		this.horizontalScaleWidth = horizontalScaleWidth;
		this.insets = insets;
		this.graphiViewInsets = graphiViewInsets;
		this.colors = colors;
		this.handlerName = handlerName;
		this.foldCount = foldCount;
		this.horizontalPixcelWidth = horizontalPixcelWidth;
		this.scaleOneHeightPixel = scaleOneHeightPixel;
		if (explanatoryColor == null) {
			this.explanatoryColor = Color.BLACK;
		} else {
			this.explanatoryColor = explanatoryColor;
		}
		if (explanatoryFont == null) {
			this.explanatoryFont = new Font("dialog", Font.BOLD, 14);
		} else {
			this.explanatoryFont = explanatoryFont;
		}
		this.firstFormat = firstFormat;
		this.secondFormat = secondFormat;
		this.verticallyScaleProperty = verticallyScaleProperty; 
		
		seriesPropertyList = new ArrayList();
	}

	/**
	 * インスタンスをディープコピーします。
	 * @return GraphPropertyModel ディープコピーされたインスタンス。
	 */
	public GraphPropertyModel deepCopy() {
		return new DefaultGraphPropertyModel(this);
	}

	/**
	 * コピーコンストラクタ
	 * @param src ソースオブジェクト
	 */
	private DefaultGraphPropertyModel(DefaultGraphPropertyModel src) {
		super();
		group = src.group;
		handlerIndex = src.handlerIndex;
		verticalScaleCount = src.verticalScaleCount;
		verticalScaleHeight = src.verticalScaleHeight;
		horizontalScaleCount = src.horizontalScaleCount;
		horizontalScaleWidth = src.horizontalScaleWidth;
		insets =
			new Insets(
				src.insets.top,
				src.insets.left,
				src.insets.bottom,
				src.insets.right);
		graphiViewInsets =
			new Insets(
				src.graphiViewInsets.top,
				src.graphiViewInsets.left,
				src.graphiViewInsets.bottom,
				src.graphiViewInsets.right);
		colors = new Color[src.colors.length];
		System.arraycopy(src.colors, 0, colors, 0, colors.length);
		handlerName = new String[src.handlerName.length];
		System.arraycopy(src.handlerName, 0, handlerName, 0, handlerName.length);

		seriesPropertyList = new ArrayList(src.seriesPropertyList.size());
		for (Iterator i = src.seriesPropertyList.iterator(); i.hasNext();) {
			GraphSeriesProperty item = (GraphSeriesProperty) i.next();
			seriesPropertyList.add(new GraphSeriesProperty(item));
		}
		foldCount = src.foldCount;
		referenceTime = src.referenceTime;
		horizontalPixcelWidth = src.horizontalPixcelWidth;
		scaleOneHeightPixel = src.scaleOneHeightPixel;
		explanatoryColor = src.explanatoryColor;
		explanatoryFont = src.explanatoryFont;
		firstFormat = src.firstFormat;
		secondFormat = src.secondFormat;
		verticallyScaleProperty = src.verticallyScaleProperty;
	}

	/**
	 * 縦スケールの目盛りの数を返します。
	 * @return 縦スケールの目盛りの数
	 */
	public int getVerticalScaleCount() {
		return verticalScaleCount;
	}

	/**
	 * 縦スケールの目盛り一つ分のピクセル長を返します。
	 * @return 縦スケールの目盛り一つ分のピクセル長
	 */
	public int getVerticalScaleHeight() {
		return verticalScaleHeight;
	}

	/**
	 * 横スケールメモリの目盛りの数を返します。
	 * @return 横スケールメモリの目盛りの数
	 */
	public int getHorizontalScaleCount() {
		return horizontalScaleCount;
	}

	/**
	 * 横スケールメモリの目盛り一つ分の時間（㍉秒）を返します。
	 * @return 横スケールメモリの目盛り一つ分の時間（㍉秒）
	 */
	public long getHorizontalScaleWidth() {
		return horizontalScaleWidth;
	}

	/**
	 * 横スケールメモリの目盛りの数を設定します。
	 * @param horizontalScaleCount 横スケールメモリの目盛りの数
	 */
	public void setHorizontalScaleCount(int horizontalScaleCount) {
		Object old = deepCopy();
		this.horizontalScaleCount = horizontalScaleCount;
		firePropertyChange(X_SCALE_CHANGE_EVENT, old, this);
	}

	/**
	 * 横スケールメモリの目盛り一つ分の時間（㍉秒）を設定します。
	 * @param horizontalScaleWidth 横スケールメモリの目盛り一つ分の時間（㍉秒）
	 */
	public void setHorizontalScaleWidth(long horizontalScaleWidth) {
		Object old = deepCopy();
		this.horizontalScaleWidth = horizontalScaleWidth;
		firePropertyChange(X_SCALE_CHANGE_EVENT, old, this);
	}

	/**
	 * コンポーネントの余白部分のインセッツを返します。
	 * @return コンポーネントの余白部分のインセッツ
	 */
	public Insets getInsets() {
		return insets;
	}

	/**
	 * グラフビュー以外の余白部分のインセッツを返します。
	 * @return コンポーネントの余白部分のインセッツ
	 */
	public Insets getGraphiViewInsets() {
		return graphiViewInsets;
	}

	/**
	 * グラフ・凡例の Color オブジェクトの配列を返します。
	 * @return グラフ・凡例の Color オブジェクトの配列
	 */
	public Color[] getColors() {
		return colors;
	}

	/**
	 * このプロパティで設定されている、グループのサイズを返します。
	 * @return グループのサイズ
	 */
	public int getGroupSize() {
		return seriesPropertyList.size();
	}

	/**
	 * 現在設定されているグループを返します。
	 * @return 現在設定されているグループ
	 */
	public int getGroup() {
		return group;
	}
	
	/**
	 * 現在設定されているグループ名を返します。
	 * @return 現在設定されているグループ名
	 */
	public String getGroupName() {
		return getGraphSeriesPropertyModel(getGroup()).getSeriesName();
	}

	/**
	 * 選択グループを設定します
	 * @param group グループ
	 */
	public void setGroup(int group) {
		if (group < 0 || group >= getGroupSize()) {
			throw new IllegalArgumentException("group : " + group);
		}
		logger.debug("group : " + group);
		Object old = deepCopy();
		this.group = group;
		firePropertyChange(GROUP_CHANGE_EVENT, old, this);
	}

	public void nextGroup() {
		if (group < (getGroupSize() - 1)) {
			Object old = deepCopy();
			group++;
			firePropertyChange(GROUP_CHANGE_EVENT, old, this);
		}
	}
	
	public void prevGroup() {
		if (0 < group) {
			Object old = deepCopy();
			group--;
			firePropertyChange(GROUP_CHANGE_EVENT, old, this);
		}
	}

	private GraphSeriesProperty getGraphSeriesPropertyModel(int group) {
		if (group < 0 || group >= getGroupSize()) {
			throw new IllegalArgumentException("group : " + group);
		}
		return (GraphSeriesProperty) seriesPropertyList.get(group);
	}

	/**
	 * 設定されているシリーズのサイズを返します。
	 * @return 設定されているシリーズのサイズ（グループ内のシリーズ数）
	 */
	public int getSeriesSize() {
		return getGraphSeriesPropertyModel(group).getSeriesSize();
	}

	/**
	 * 縦スケールの最小値を返します。
	 * @param series シリーズ
	 * @return 縦スケールの最小値
	 */
	public double getVerticalMinimum(int series) {
		return getGraphSeriesPropertyModel(group).getVerticalMinimum(series);
	}

	/**
	 * 縦スケールの最大値を返します。
	 * @param series シリーズ
	 * @return 縦スケールの最大値
	 */
	public double getVerticalMaximum(int series) {
		return getGraphSeriesPropertyModel(group).getVerticalMaximum(series);
	}

	/**
	 * 縦スケールの最小値を設定します。
	 * @param series シリーズ
	 * @param verticalMinimum 縦スケールの最小値
	 */
	public void setVerticalMinimum(int series, double verticalMinimum) {
		Object old = deepCopy();
		getGraphSeriesPropertyModel(group).setVerticalMinimum(
			series,
			verticalMinimum);
		firePropertyChange(old, this);
	}

	/**
	 * 縦スケールの最大値を設定します。
	 * @param series シリーズ
	 * @param verticalMaximum 縦スケールの最大値
	 */
	public void setVerticalMaximum(int series, double verticalMaximum) {
		Object old = deepCopy();
		getGraphSeriesPropertyModel(group).setVerticalMaximum(
			series,
			verticalMaximum);
		firePropertyChange(old, this);
	}

	/**
	 * データプロバイダ名を返します。
	 * @param series シリーズ
	 * @return データプロバイダ名
	 */
	public String getDataProviderName(int series) {
		return getGraphSeriesPropertyModel(group).getDataProviderName(series);
	}

	/**
	 * データホルダ名を返します。
	 * @param series シリーズ
	 * @return データホルダ名
	 */
	public String getDataHolderName(int series) {
		return getGraphSeriesPropertyModel(group).getDataHolderName(series);
	}

	/**
	 * 現在位置づけられているグループの、参照値を返します。
	 * @param series シリーズ
	 * @param fold 折り返し位置
	 * @return 参照値
	 */
	public double getReferenceValue(int series, int fold) {
		return getGraphSeriesPropertyModel(group).getReferenceValue(series, fold);
	}

	/**
	 * 現在位置づけられているグループの、参照値を返します。
	 * @param series シリーズ
	 * @return 参照値
	 */
	public double getReferenceValue(int series) {
		return getReferenceValue(series, 0);
	}

	/**
	 * 現在位置づけられているグループの、参照値を設定します。
	 * @param series シリーズ
	 * @param fold 折り返し位置
	 * @param referenceValue 参照値
	 */
	public void setReferenceValue(int series, int fold, double referenceValue) {
		Object old = deepCopy();
		getGraphSeriesPropertyModel(group).setReferenceValue(
			series,
			fold,
			referenceValue);
		firePropertyChange(old, this);
	}

	/**
	 * 現在位置づけられているグループの、参照値を設定します。
	 * @param series シリーズ
	 * @param referenceValue 参照値
	 */
	public void setReferenceValue(int series, double referenceValue) {
		setReferenceValue(series, 0, referenceValue);
	}
	
	/**
	 * 現在位置づけられているグループの、参照時刻を設定します。
	 * @param series シリーズ
	 * @param fold 折り返し位置
	 * @param referenceTime 参照時刻
	 */
	public void setReferenceTime(int series, int fold, Timestamp referenceTime) {
		Object old = deepCopy();
		getGraphSeriesPropertyModel(group).setReferenceTime(
			series,
			fold,
			referenceTime);
		firePropertyChange(old, this);
	}
	
	/**
	 * 現在位置づけられているグループの、参照時刻を設定します。
	 * @param series シリーズ
	 * @param referenceTime 参照時刻
	 */
	public void setReferenceTime(int series, Timestamp referenceTime) {
		setReferenceTime(series, 0, referenceTime);
	}

	/**
	 * 現在位置づけられているグループの、参照時刻を返します。
	 * @param series シリーズ
	 * @param fold 折り返し位置
	 * @return 参照時刻
	 */
	public Timestamp getReferenceTime(int series, int fold) {
		return getGraphSeriesPropertyModel(getGroup()).getReferenceTime(series, fold);
	}

	/**
	 * 現在位置づけられているグループの、参照時刻を返します。
	 * @param series シリーズ
	 * @return 参照時刻
	 */
	public Timestamp getReferenceTime(int series) {
		return getReferenceTime(series, 0);
	}

	/**
	 * 現在位置づけられているグループの、列インデックスを返します。
	 * @return 列インデックスの配列
	 */
	public int[] getGroupColumnIndex() {
		return new int[0];
	}

	/**
	 * シリーズプロパティ(グループの設定)を追加します。
	 * @param property シリーズプロパティ
	 */
	public void addSeriesProperty(GraphSeriesProperty property) {
		Object old = deepCopy();
		seriesPropertyList.add(property);
		firePropertyChange(old, this);
	}
	
	/**
	 * 現在表示しているハンドラー名のインデックスを設定します。
	 * @param name ハンドラー名
	 */
	public void setListHandlerIndex(int index) {
		if (index < 0 || index >= handlerName.length) {
			throw new IllegalArgumentException("index : " + index);
		}
		Object old = deepCopy();
		handlerIndex = index;
		firePropertyChange(X_SCALE_CHANGE_EVENT, old, this);
	}
	
	/**
	 * 現在インデックスで指定されているハンドラー名を返します。
	 * @return 現在インデックスで指定されているハンドラー名
	 */
	public String getListHandlerName() {
		return handlerName[handlerIndex];
	}
	
	/**
	 * 折り返し回数を返します。
	 * @return 折り返し回数
	 */
	public int getFoldCount() {
		return foldCount;
	}

	/**
	 * 現在位置づけられているグループの、ポイント名称を返します。
	 * @param series シリーズ
	 * @return 現在位置づけられているグループの、列インデックスを返します。
	 */
	public String getPointName(int series) {
		return getGraphSeriesPropertyModel(getGroup()).getPointName(series);
	}


	/**
	 * 現在位置づけられているグループの、単位記号を返します。
	 * @param series シリーズ
	 * @return 現在位置づけられているグループの、単位記号を返します。
	 */
	public String getPointMark(int series) {
		return getGraphSeriesPropertyModel(getGroup()).getPointMark(series);
	}

	/**
	 * 現在位置づけられているグループの、現在値表示シンボルを返します。
	 * @param series シリーズ
	 * @return 現在位置づけられているグループの、現在値表示シンボルを返します。
	 */
	public ExplanatoryNotesText getSymbol(int series) {
		return getGraphSeriesPropertyModel(getGroup()).getSymbol(series);
	}
	
	/**
	 * 現在クリックされている参照日時を返します
	 * @return 現在クリックされている参照日時を返します
	 */
	public Timestamp getReferenceTime() {
		return referenceTime;
	}
	
	/**
	 * 現在クリックされている参照日時を設定します
	 * @param time 現在クリックされている参照日時を設定します
	 */
	public void setReferenceTime(Timestamp time) {
		Timestamp old = null;
		if (referenceTime != null) {
			old = (Timestamp) referenceTime.clone();
		}
		referenceTime = time;
		firePropertyChange(old, time);
	}

	/**
	 * 表示されるグラフの横ピクセル数を返します。
	 * @return 表示されるグラフの横ピクセル数を返します。
	 */
	public int getHorizontalPixcelWidth() {
		return horizontalPixcelWidth;
	}
	
	/**
	 * X軸スケールの目盛り幅を返します。
	 * @return X軸スケールの目盛り幅を返します。
	 */
	public int getScaleOneHeightPixel() {
		return scaleOneHeightPixel;
	}

	/**
	 * 参照表示の文字色を返します
	 * @return 参照表示の文字色を返します
	 */
	public Color getExplanatoryColor() {
		return explanatoryColor;
	}

	/**
	 * 参照表示のフォントを返します
	 * @return 参照表示のフォントを返します
	 */
	public Font getExplanatoryFont() {
		return explanatoryFont;
	}

	
    public Collection getGroupNames() {
        ArrayList groupNames = new ArrayList(seriesPropertyList.size());
        int index = 0;
        for (Iterator i = seriesPropertyList.iterator(); i.hasNext(); index++) {
            GraphSeriesProperty gp = (GraphSeriesProperty) i.next();
            groupNames.add(String.format("%03d : %s", index, gp.getSeriesName()));
        }
        return groupNames;
    }

    public String getFirstFormat() {
        return firstFormat;
    }
    public String getSecondFormat() {
        return secondFormat;
    }
    public void setFirstFormat(String format) {
        if (format == null) {
            format = "MM/dd";
        }
		Object old = deepCopy();
        firstFormat = format;
		firePropertyChange(X_SCALE_CHANGE_EVENT, old, this);
    }
    public void setSecondFormat(String format) {
        if (format == null) {
            format = "HH:mm";
        }
		Object old = deepCopy();
        secondFormat = format;
		firePropertyChange(X_SCALE_CHANGE_EVENT, old, this);
    }

	public VerticallyScaleProperty getVerticallyScaleProperty() {
		return verticallyScaleProperty;
	}

    /**
	 * このオブジェクトの文字列表現を返します。
	 * 内部プロパティを文字列表現して返します。尚、このメソッドは開発段階で使用すべきであり、
	 * テスト目的以外で使用しないでください。将来、返される内容が変更されることがあります。
	 * @return オブジェクトの文字列表現
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("verticalScaleCount:" + verticalScaleCount);
		buffer.append(",verticalScaleHeight:" + verticalScaleHeight);
		buffer.append(",horizontalScaleCount:" + horizontalScaleCount);
		buffer.append(",horizontalScaleWidth:" + horizontalScaleWidth);
		buffer.append(",insets:" + insets);
		for (int i = 0; i < colors.length; i++) {
			buffer.append(",colors:" + colors[i]);
		}
		List handler = Arrays.asList(handlerName);
		buffer.append("handlerName:" + handler);
		buffer.append(",group:" + group);
		buffer.append("seriesPropertyList:" + seriesPropertyList);
		buffer.append(",foldCount:" + foldCount);
		buffer.append(",referenceTime:").append(referenceTime)
		.append(",horizontalPixcelWidth:").append(horizontalPixcelWidth)
		.append(",scaleOneHeightPixel:").append(scaleOneHeightPixel)
		.append(",firstFormat:").append(firstFormat)
		.append(",secondFormat:").append(secondFormat)
		.append("verticalScaleCount:").append(verticallyScaleProperty);
		return buffer.toString();
	}

	/**
	 * 別のオブジェクトが、このオブジェクトと同じか調べます。
	 * 結果は引数が null でなく、このオブジェクトと同じ各プロパティー（フィールド）の値を持つ
	 * オブジェクトである場合に true を返します。
	 * @param obj この DefaultGraphPropertyModel と等しいかどうかが判定されるオブジェクト
	 * @return オブジェクトが同じである場合は true、そうでない場合は false
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DefaultGraphPropertyModel)) {
			return false;
		}
		DefaultGraphPropertyModel pm = (DefaultGraphPropertyModel) obj;
		return pm.group == this.group
			&& pm.handlerIndex == this.handlerIndex
			&& pm.verticalScaleCount == this.verticalScaleCount
			&& pm.verticalScaleHeight == this.verticalScaleHeight
			&& pm.horizontalScaleCount == this.horizontalScaleCount
			&& pm.horizontalScaleWidth == this.horizontalScaleWidth
			&& pm.insets.equals(this.insets)
			&& pm.graphiViewInsets.equals(this.graphiViewInsets)
			&& Arrays.equals(pm.colors, this.colors)
			&& Arrays.equals(pm.handlerName, this.handlerName)
			&& pm.seriesPropertyList.equals(this.seriesPropertyList)
			&& pm.foldCount == this.foldCount
			&& ((pm.referenceTime == null && this.referenceTime == null) || pm.referenceTime.equals(this.referenceTime))
			&& pm.horizontalPixcelWidth == this.horizontalPixcelWidth
			&& pm.scaleOneHeightPixel == this.scaleOneHeightPixel
			&& ((pm.explanatoryColor == null && this.explanatoryColor == null) || pm.explanatoryColor.equals(this.explanatoryColor))
			&& ((pm.explanatoryFont == null && this.explanatoryFont == null) || pm.explanatoryFont.equals(this.explanatoryFont))
			&& pm.firstFormat.equals(this.firstFormat)
			&& pm.secondFormat.equals(this.secondFormat)
			&& pm.verticallyScaleProperty.equals(verticallyScaleProperty);
	}

	/**
	 * この DefaultGraphPropertyModel のハッシュコードを計算します。
	 * @return この DefaultGraphPropertyModel のハッシュコード値
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + group;
		result = 37 * result + handlerIndex;
		result = 37 * result + verticalScaleCount;
		result = 37 * result + verticalScaleHeight;
		result = 37 * result + horizontalScaleCount;
		result =
			37 * result
				+ (int) (horizontalScaleWidth ^ (horizontalScaleWidth >>> 32));
		result = 37 * result + insets.hashCode();
		result = 37 * result + graphiViewInsets.hashCode();
		for (int i = 0; i < colors.length; i++) {
			result = 37 * result + colors[i].hashCode();
		}
		for (int i = 0; i < handlerName.length; i++) {
			result = 37 * result + handlerName[i].hashCode();
		}
		result = 37 * result + seriesPropertyList.hashCode();
		result = 37 * result + foldCount;
		if (referenceTime != null) {
			result = 37 * result + referenceTime.hashCode();
		}
		result = 37 * result + horizontalPixcelWidth;
		result = 37 * result + scaleOneHeightPixel;
		if (explanatoryColor != null)
		    result = 37 * result + explanatoryColor.hashCode();
		if (explanatoryFont != null)
		    result = 37 * result + explanatoryFont.hashCode();
		result = 37 * result + firstFormat.hashCode();
		result = 37 * result + secondFormat.hashCode();
		result = 37 * result + verticallyScaleProperty.hashCode();
		return result;
	}
}
