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

package org.F11.scada.applet.graph;

import java.awt.FontMetrics;

import javax.swing.JComponent;

/**
 * デジタルのスケールデータモデルを提供します。
 */
public class DigitalVerticallyScaleModel extends AbstractVerticallyScaleModel {
	/**
	 * コンストラクタ
	 * @param comp 上位コンポーネント
	 * @param graphPropertyModel グラフプロパティモデル
	 * @param series シリーズ
	 */
	public DigitalVerticallyScaleModel(JComponent comp,
									   GraphPropertyModel graphPropertyModel,
									   int series) {
		super(comp, graphPropertyModel, series);
	}

	protected void calcSize() {
		checkGraphPropertyModel();
		// スケールの文字列を生成
		int scaleCount = getScaleCount();
		scaleStrings = new String[scaleCount + 1];
		final FontMetrics metrics = comp.getFontMetrics(comp.getFont());
		maxStringHeight = metrics.getAscent();
		final double scaleMax = graphPropertyModel.getVerticalMaximum(series);
		final double scaleMin = graphPropertyModel.getVerticalMinimum(series);
		final double scaleInc = (scaleMax - scaleMin) / scaleCount;
		double j = scaleMin;

		String pName = graphPropertyModel.getDataProviderName(series);
		if (pName != null && !"".equals(pName)) {
			maxStringWidth = 0;
			scaleStrings[0] = " OFF";
			scaleStrings[1] = " O N";
			maxStringWidth = Math.max(metrics.stringWidth(scaleStrings[1]),
					metrics.stringWidth(scaleStrings[0]));
		} else {
			maxStringWidth = 0;
			for (int k = 0; k <= scaleCount; k++, j += scaleInc) {
				scaleStrings[k] = "";
				maxStringWidth = Math.max(maxStringWidth, metrics.stringWidth(scaleStrings[k]));
			}
		}
	}

	private void checkGraphPropertyModel() {
		if (getScaleCount() != 1) {
			throw new IllegalStateException("VerticalScaleCount not equal '1'");
		}
	}
}
