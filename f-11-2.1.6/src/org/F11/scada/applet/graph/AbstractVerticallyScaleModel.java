package org.F11.scada.applet.graph;

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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.log4j.Logger;

/**
 * スケールデータモデルの基底クラスです。
 * SwingPropertyChangeSupport によるイベントデリゲーションを実装します。
 */
public abstract class AbstractVerticallyScaleModel implements VerticallyScaleModel, PropertyChangeListener {
	/** ロギングAPI */
	protected static Logger logger = Logger.getLogger(AbstractVerticallyScaleModel.class);
	/** プロパティイベント名 */
	private static final String PROPERTY_NAME = "SCALE_CHANGEED";
	/** プロパティチェンジサポート */
	private SwingPropertyChangeSupport property;
	/** 上位コンポーネント */
	protected JComponent comp;
	/** グラフプロパティモデル */
	protected GraphPropertyModel graphPropertyModel;
	/** シリーズ */
	protected int series;

	/** スケールメモリの横幅 */
	protected static final int scaleOneWidth = 10;
	/** スケール目盛りの文字列 */
	protected String[] scaleStrings;
	/** スケール目盛りの文字列の最大幅 */
	protected int maxStringWidth;
	/** スケール目盛りの文字列の最大高 */
	protected int maxStringHeight;
	/** スケール最大スパン変更の有無 */
	protected boolean isMaxEnable = true;
	/** スケール最小スパン変更の有無 */
	protected boolean isMinEnable = true;

	/**
	 * コンストラクタ
	 * @param comp 上位コンポーネント
	 * @param graphPropertyModel グラフプロパティモデル
	 * @param series シリーズ
	 */
	public AbstractVerticallyScaleModel(JComponent comp,
									   GraphPropertyModel graphPropertyModel,
									   int series) {
		this.comp = comp;
		this.graphPropertyModel = graphPropertyModel;
		this.graphPropertyModel.addPropertyChangeListener(this);
		this.graphPropertyModel.addPropertyChangeListener(GraphPropertyModel.GROUP_CHANGE_EVENT, this);
		this.series = series;
		logger.debug("initialize.");
	}

	/**
	 * コンポーネントの初期化初期を実装します。
	 */
	abstract protected void calcSize();

	/**
	 * PropertyChangeListener をリスナーリストに追加します。
	 * @param listener 追加する PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (property == null) {
			property = new SwingPropertyChangeSupport(this);
		}
		property.addPropertyChangeListener(PROPERTY_NAME, listener);
	}

	/**
	 * PropertyChangeListener をリスナーリストから削除します。
	 * @param listener 削除する PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (property == null) {
			return;
		}
		property.removePropertyChangeListener(PROPERTY_NAME, listener);
	}

	/**
	 * 登録されているリスナーに、バウンドプロパティの更新を通知します。
	 * 以前の値と新しい値が等しくて null でない場合、イベントはトリガされません。
	 * @param oldValue プロパティの古い値
	 * @param newValue プロパティの新しい値
	 */
	public void firePropertyChange(Object oldValue, Object newValue) {
		if (property == null) {
			return;
		}
		property.firePropertyChange(PROPERTY_NAME, oldValue, newValue);
	}

	/**
	 * 目盛りの幅を返します。
	 * @return 目盛りの幅
	 */
	public int getScaleOneWidth() {
		return scaleOneWidth;
	}

	/**
	 * 目盛りの高さを返します。
	 * @return 目盛りの高さ
	 */
	public int getScaleOneHeight() {
		return graphPropertyModel.getVerticalScaleHeight();
	}

	/**
	 * 目盛りの総数を返します。
	 * @return 目盛りの総数
	 */
	public int getScaleCount() {
		return graphPropertyModel.getVerticalScaleCount();
	}

	/**
	 * スケールの最小値を返します。
	 * @return スケールの最小値
	 */
	public double getScaleMin() {
		return graphPropertyModel.getVerticalMinimum(series);
	}

	/**
	 * スケールの最大値を返します。
	 * @return スケールの最大値
	 */
	public double getScaleMax() {
		return graphPropertyModel.getVerticalMaximum(series);
	}

	/**
	 * スケールの最小値を設定します。
	 * @param min スケールの最小値
	 */
	public void setScaleMin(double min) {
		graphPropertyModel.setVerticalMinimum(series, min);
		calcSize();
	}

	/**
	 * スケールの最大値を設定します。
	 * @param max スケールの最大値
	 */
	public void setScaleMax(double max) {
		graphPropertyModel.setVerticalMaximum(series, max);
		calcSize();
	}

	/**
	 * スケール目盛り文字列の配列を返します。
	 * @return スケール目盛り文字列の配列
	 */
	public String[] getScaleStrings() {
		if (scaleStrings == null) {
			calcSize();
		}
		return scaleStrings;
	}

	/**
	 * スケール目盛り文字列の最大幅を返します。
	 * @param isLeft 左側スケールかどうか
	 * @return スケール目盛り文字列の最大幅
	 */
	public int getScaleStringMaxWidth(boolean isLeft) {
		if (maxStringWidth == 0) {
			calcSize();
		}
		FontMetrics metrics = comp.getFontMetrics(comp.getFont());
		return isLeft ? maxStringWidth + metrics.stringWidth("  ") : maxStringWidth;
	}

	/**
	 * スケール目盛り文字列の最大高を返します。
	 * @return スケール目盛り文字列の最大高
	 */
	public int getScaleStringMaxHeight() {
		if (maxStringHeight == 0) {
			calcSize();
		}
		return maxStringHeight;
	}

	/**
	 * スケール目盛り文字列の色を返します。
	 * @return スケール目盛り文字列の色
	 */
	public Color getScaleStringColor() {
		return graphPropertyModel.getColors()[series];
	}

	/**
	 * 描画エリアのインセッツを返します
	 * @return 描画エリアのインセッツ
	 */
	public Insets getScaleInsets() {
		return graphPropertyModel.getInsets();
	}

	/**
	 * スケール最大スパン変更の有無
	 * @return スケール最大スパン変更ができる場合は true、そうでない場合は false
	 */
	public boolean isMaxEnable() {
		return isMaxEnable;
	}

	/**
	 * スケール最小スパン変更の有無
	 * @return スケール最小スパン変更ができる場合は true、そうでない場合は false
	 */
	public boolean isMinEnable() {
		return isMinEnable;
	}

	/**
	 * スケール最大スパン変更の有無を設定します。
	 * @param isMaxEnable 変更可能にする場合は true、変更不可にする場合は false
	 */
	public void setMaxEnable(boolean isMaxEnable) {
		this.isMaxEnable = isMaxEnable;
	}

	/**
	 * スケール最小スパン変更の有無を設定します。
	 * @param isMinEnable 変更可能にする場合は true、変更不可にする場合は false
	 */
	public void setMinEnable(boolean isMinEnable) {
		this.isMinEnable = isMinEnable;
	}

	/**
	 * バウンドプロパティの変更時に呼び出されます。
	 * @param evt イベントソースおよび変更したプロパティを
	 * 記述する PropertyChangeEvent オブジェクト
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		calcSize();
		firePropertyChange(evt.getOldValue(), evt.getNewValue());
	}

	/**
	 * スケールに対応するシリーズを返します。
	 * 対応するシリーズが存在しない場合は-1を返します。
	 * 
	 * @return スケールに対応するシリーズを返します。対応するシリーズが存在しない場合は-1を返します。
	 */
	public int getSeries() {
		int max = graphPropertyModel.getSeriesSize();
		return (max <= series || 0 > series) ? -1 : series;
	}
	
	/**
	 * データプロバイダ名を返します
	 * @return データプロバイダ名を返します
	 */
	public String getDataProviderName() {
		return graphPropertyModel.getDataProviderName(series);
	}
	
	/**
	 * データホルダ名を返します
	 * @return データホルダ名を返します
	 */
	public String getDataHolderName() {
		return graphPropertyModel.getDataHolderName(series);
	}
}
