/*
 * $Header:
 * /cvsroot/f-11/F-11/src/org/F11/scada/parser/graph/bargraph/BarGraph2State
 * .java,v 1.5.2.2 2007/03/13 05:54:20 frdm Exp $ $Revision: 1.5.2.2 $ $Date:
 * 2007/03/13 05:54:20 $
 * =============================================================================
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import org.F11.scada.applet.symbol.TextSymbol;
import org.apache.log4j.Logger;

/**
 * バーとフォーカスの描画クラスです。
 */
public class BarGraph2View extends JComponent
		implements
			VerticallyScaleChangeListener {
	private static final long serialVersionUID = -6731876207573008310L;
	private static Logger logger = Logger.getLogger(BarGraph2View.class);
	private final BarData[] nullDatas;

	private BarGraphModel model;
	private Color[] colors;
	private BarFocus focus;
	private Rectangle innerRect;
	private int clickIndex = 0;
	private double min = 0.0;
	private double max = 1.0;
	private String pattern;
	private BarData[][] dataMatrix;

	public BarGraph2View(BarGraphModel model) {
		this.model = model;
		colors = new Color[model.getBarCount()];
		nullDatas = new BarData[model.getBarCount()];
		this.addMouseListener(new FillGraphMouseListener(this));
	}

	public void changeScale(double min, double max) {
		this.min = min;
		this.max = max;
		repaint();
	}

	public void changePattern(String pattern) {
		this.pattern = pattern;
	}

	public void changeData(BarData[][] dataMatrix) {
		this.dataMatrix = dataMatrix;
		fireSetFocus(clickIndex);
	}

	private void fireSetFocus(int clickIndex) {
		this.clickIndex = clickIndex;
		if (dataMatrix != null)
			focus.setFocusData(dataMatrix[clickIndex], pattern);
		else
			focus.setFocusData(nullDatas, pattern);
		repaint();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (dataMatrix == null)
			return;

		Graphics2D g2d = (Graphics2D) g.create();

		// ロギングデータを描画
		drawSeries(g2d);
		// 参照値の破線を描画
		Rectangle blockRect = new Rectangle(innerRect);
		blockRect.width = innerRect.width / model.getBlockCount();
		blockRect.x = innerRect.x + innerRect.width * clickIndex
				/ model.getBlockCount();
		focus.draw(g2d, blockRect);

		g2d.dispose();
	}

	private void drawSeries(Graphics2D g2d) {
		int block_width = innerRect.width / model.getBlockCount();
		int bar_width = block_width / (model.getBarCount() + 1);
		int block_gap = block_width - (bar_width * model.getBarCount());
		Rectangle bar_rect = new Rectangle();
		bar_rect.width = bar_width - 1; // 間1ピクセル
		for (int block = 0; block < model.getBlockCount(); block++) {
			bar_rect.x = innerRect.x + (block_gap / 2)
					+ (block * innerRect.width / model.getBlockCount());
			for (int bar = 0; bar < model.getBarCount(); bar++) {
				BarData barData = dataMatrix[block][bar];
				if (barData != null) {
					double rate = (barData.getValue(0) - min) / (max - min);
					if (rate < 0.0)
						rate = 0.0;
					if (1.0 < rate)
						rate = 1.0;
					bar_rect.height = (int) Math.floor(innerRect.height * rate
							+ 0.5);
					bar_rect.y = innerRect.y + innerRect.height
							- bar_rect.height;
					g2d.setColor(colors[bar]);
					g2d.fill(bar_rect);
				}
				bar_rect.x += bar_width;
			}
		}
	}

	public void addColor(Color color) {
		for (int i = 0; i < colors.length; i++) {
			if (colors[i] == null) {
				colors[i] = color;
				break;
			}
		}
	}

	/**
	 * フォーカスクラスを設定します。自身のサイズより大きい部分がある場合、自身を拡大します。
	 * @param focus
	 */
	public void setBarFocus(BarFocus focus) {
		this.focus = focus;
		// BarFocusの管理シンボルを親に追加
		for (TextDateTimeSymbol symbol : focus.getTextDateTimeSymbolArray()) {
			model.getJComponent().add(symbol);
		}
		for (TextSymbol symbol : focus.getTextSymbolArray()) {
			model.getJComponent().add(symbol);
		}
		// BarFocusの拡大分に合わせる
		innerRect = new Rectangle(this.getSize());
		Insets insets = focus.getInsets();
		Point pos = this.getLocation();
		pos.translate(-insets.left, -insets.top);
		this.setLocation(pos);
		Dimension dim = this.getSize();
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom;
		this.setSize(dim);
		innerRect.translate(insets.left, insets.top);
	}

	/**
	 * グラフ描画エリアのマウスリスナーアダプタークラスです。<br>
	 * マウスリリース位置の値をfocusへ通知し、再描画します。
	 */
	private static class FillGraphMouseListener extends MouseAdapter {
		private BarGraph2View view;

		FillGraphMouseListener(BarGraph2View view) {
			this.view = view;
		}

		public void mouseReleased(MouseEvent e) {
			int index = 0;
			int x = e.getPoint().x - view.innerRect.x;
			if (x <= 0) {
				index = 0;
			} else if (view.innerRect.width <= x) {
				index = view.model.getBlockCount() - 1;
			} else {
				index = x * view.model.getBlockCount() / view.innerRect.width;
			}
			logger.debug("mouseReleased index=" + index);
			view.fireSetFocus(index % view.model.getBlockCount());
		}
	}

}
