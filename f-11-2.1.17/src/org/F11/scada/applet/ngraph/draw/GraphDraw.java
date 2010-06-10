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

package org.F11.scada.applet.ngraph.draw;


import java.awt.Graphics;

import org.F11.scada.applet.ngraph.LogData;


/**
 * トレンドグラフのドローエンジンです。
 * 
 * @author maekawa
 * 
 */
public interface GraphDraw {
	/**
	 * シリーズデータを描画します
	 * 
	 * @param g グラフィックコンテキスト
	 * @param currentIndex TODO
	 * @param displayDatas TODO
	 * @param isAllSpanDisplayMode TODO
	 */
	void drawSeries(
			Graphics g,
			int currentIndex,
			LogData[] displayDatas,
			boolean isAllSpanDisplayMode);

	/**
	 * 単位記号を描画します
	 * 
	 * @param g グラフィックコンテキスト
	 * @param top 上余白
	 * @param x x座標
	 * @param drawSeriesIndex 単位描画するシリーズインデックス
	 */
	void drawUnitMark(Graphics g, int top, int x, int drawSeriesIndex);

	/**
	 * 縦スケールの目盛文字列を描画します
	 * 
	 * @param g グラフィックコンテキスト
	 * @param top 上余白
	 * @param x x座標
	 * @param y y座標
	 * @param i 目盛数のインデックス
	 * @param drawSeriesIndex 単位描画するシリーズインデックス
	 */
	void drawVerticalString(
			Graphics g,
			int top,
			int x,
			int y,
			int i,
			int drawSeriesIndex);
}
