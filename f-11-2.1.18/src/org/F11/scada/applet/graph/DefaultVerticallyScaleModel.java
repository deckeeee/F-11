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

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * デフォルトのスケールデータモデルを提供します。
 */
public class DefaultVerticallyScaleModel extends AbstractVerticallyScaleModel {
	/**
	 * コンストラクタ
	 * @param comp 上位コンポーネント
	 * @param graphPropertyModel グラフプロパティモデル
	 * @param series シリーズ
	 */
	public DefaultVerticallyScaleModel(JComponent comp,
									   GraphPropertyModel graphPropertyModel,
									   int series) {
		super(comp, graphPropertyModel, series);
	}

	protected void calcSize() {
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
			DataProvider provider =
				Manager.getInstance().getDataProvider(pName);
			DataHolder holder =
				provider.getDataHolder(
					graphPropertyModel.getDataHolderName(series));
		
			ConvertValue converter =
				(ConvertValue) holder.getParameter(WifeDataProvider.PARA_NAME_CONVERT);

			maxStringWidth = 0;
			for (int k = 0; k <= scaleCount; k++, j += scaleInc) {
				double ref = converter.convertInputValueUnlimited(j);
				scaleStrings[k] = converter.convertStringValueUnlimited(ref);
				maxStringWidth = Math.max(maxStringWidth, metrics.stringWidth("-" + scaleStrings[k]));
			}
		} else {
			maxStringWidth = 0;
			for (int k = 0; k <= scaleCount; k++, j += scaleInc) {
				scaleStrings[k] = "";
				maxStringWidth = Math.max(maxStringWidth, metrics.stringWidth("-" + scaleStrings[k]));
			}
		}
	}

}
