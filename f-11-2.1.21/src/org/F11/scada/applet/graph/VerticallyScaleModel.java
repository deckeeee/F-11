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
import java.awt.Insets;
import java.beans.PropertyChangeListener;

/**
 * スケール表示のデータモデル・インターフェイスです。
 */
public interface VerticallyScaleModel {

	/**
	 * PropertyChangeListener をリスナーリストに追加します。
	 * @param listener 追加する PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * PropertyChangeListener をリスナーリストから削除します。
	 * @param listener 削除する PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * 目盛りの幅を返します。
	 * @return 目盛りの幅
	 */
	public int getScaleOneWidth();

	/**
	 * 目盛りの高さを返します。
	 * @return 目盛りの高さ
	 */
	public int getScaleOneHeight();

	/**
	 * 目盛りの総数を返します。
	 * @return 目盛りの総数
	 */
	public int getScaleCount();

	/**
	 * スケールの最小値を返します。
	 * @return スケールの最小値
	 */
	public double getScaleMin();

	/**
	 * スケールの最大値を返します。
	 * @return スケールの最大値
	 */
	public double getScaleMax();

	/**
	 * スケールの最小値を設定します。
	 * @param スケールの最小値
	 */
	// TODO 引数はStringに変更？
	public void setScaleMin(double min);

	/**
	 * スケールの最大値を設定します。
	 * @param スケールの最大値
	 */
	// TODO 引数はStringに変更？
	public void setScaleMax(double max);

	/**
	 * スケール目盛り文字列の配列を返します。
	 * @return スケール目盛り文字列の配列
	 */
	public String[] getScaleStrings();

	/**
	 * スケール目盛り文字列の最大幅を返します。
	 * @param isLeft 左側スケールかどうか
	 * @return スケール目盛り文字列の最大幅
	 */
	public int getScaleStringMaxWidth(boolean isLeft);

	/**
	 * スケール目盛り文字列の最大高を返します。
	 * @return スケール目盛り文字列の最大高
	 */
	public int getScaleStringMaxHeight();

	/**
	 * スケール目盛り文字列の色を返します。
	 * @return スケール目盛り文字列の色
	 */
	public Color getScaleStringColor();

	/**
	 * 描画エリア内のインセッツを返します
	 */
	public Insets getScaleInsets();

	/**
	 * スケール最大スパン変更の有無
	 * @return スケール最大スパン変更ができる場合は true そうでない場合は false
	 */
	public boolean isMaxEnable();

	/**
	 * スケール最小スパン変更の有無
	 * @return スケール最小スパン変更ができる場合は true そうでない場合は false
	 */
	public boolean isMinEnable();

	/**
	 * スケール最大スパン変更の有無を設定します。
	 * @param enable 変更可能にする場合は true、変更不可にする場合は false
	 */
	public void setMaxEnable(boolean enable);

	/**
	 * スケール最小スパン変更の有無を設定します。
	 * @param enable 変更可能にする場合は true、変更不可にする場合は false
	 */
	public void setMinEnable(boolean enable);

	/**
	 * スケールに対応するシリーズを返します。
	 * 対応するシリーズが存在しない場合は-1を返します。
	 * 
	 * @return スケールに対応するシリーズを返します。対応するシリーズが存在しない場合は-1を返します。
	 */
	public int getSeries();
	
	/**
	 * データプロバイダ名を返します
	 * @return データプロバイダ名を返します
	 */
	public String getDataProviderName();
	
	/**
	 * データホルダ名を返します
	 * @return データホルダ名を返します
	 */
	public String getDataHolderName();
}
