/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.applet.graph.bargraph2;

import java.rmi.RemoteException;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.F11.scada.Service;

public interface BarGraphModel extends Service {
	/**
	 * バーグラフコンポーネントを返します。
	 * @return
	 */
	public JComponent getJComponent();
	/**
	 * 表示ポイントを変更します。
	 * @param series
	 */
	public void changePoint(BarSeries series) throws RemoteException;
	/**
	 * 表示データの参照位置を変更します。
	 */
	public void changePeriod(int offset);
	/**
	 * 横スケール１単位中のバーの本数を返します。
	 * @return
	 */
	public int getBarCount();
	/**
	 * 横スケールの数を返します。
	 * @return
	 */
	public int getBlockCount();
	/**
	 * 表示データのマトリックスを返します。
	 * @return
	 */
	public BarData[][] getDataMatrix();

	/**
	 * 自身のインデックスを設定します。<br>
	 * シリーズ内のポイントを取得する時のインデックスとして使用します。
	 * @param barIndex
	 */
	public void setBarGraphModelIndex(int barIndex);
	/**
	 * toString()の返り値を設定します。
	 * @param text
	 */
	public void setText(String text);
	/**
	 * コンポーネントのバックグラウンドイメージを設定します。
	 * @param icon
	 */
	public void setIcon(Icon icon);
	/**
	 * 参照するテーブル名を設定します。
	 * @param handlerNames
	 */
	public void setHandlerNames(String[] handlerNames);
	/**
	 * 横スケール１単位中のバーの本数を設定します。
	 * @param count
	 */
	public void setBarCount(int count);
	/**
	 * データの参照日を操作するクラスを設定します。
	 * @param refDate
	 */
	public void setReferenceDate(ReferenceDate refDate);
	/**
	 * バーを描画するコンポーネントを設定します。
	 * @param view
	 */
	public void setBarGraph2View(BarGraph2View view);
	/**
	 * 縦スケールを管理するクラスを追加します。
	 * @param symbol
	 */
	public void setVerticallyScaleModel(VerticallyScaleModel vertical);
	/**
	 * コンポーネントに単位表示用テキストシンボルを追加します。
	 * @param symbol
	 */
	public void addUnitsSymbol(TextUnitsSymbol symbol);
}
