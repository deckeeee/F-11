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

import static java.lang.Math.*;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.text.DecimalFormat;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.applet.ngraph.GraphProperties;
import org.F11.scada.applet.ngraph.LogData;
import org.F11.scada.applet.ngraph.SeriesProperties;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.commons.collections.primitives.DoubleIterator;
import org.apache.commons.collections.primitives.DoubleList;

/**
 * 分離表示のグラフ描画クラス
 *
 * @author maekawa
 *
 */
public class SegmentasionGraphDraw extends AbstractGraphDraw {
	public SegmentasionGraphDraw(GraphProperties properties) {
		super(properties);
	}

	public void drawSeries(Graphics g,
			int currentIndex,
			LogData[] displayDatas,
			boolean isAllSpanDisplayMode) {
		Insets insets = properties.getInsets();
		long lastDate = displayDatas[currentIndex].getDate().getTime();
		long startDate = lastDate - properties.getHorizontalLineSpan();
		for (int i = currentIndex; isDrawing(displayDatas, startDate, i); i++) {
			LogData logData1 = displayDatas[i + 1];
			LogData logData2 = displayDatas[i];
			int x1 = getX(logData1, startDate, lastDate, isAllSpanDisplayMode);
			int x2 = getX(logData2, startDate, lastDate, isAllSpanDisplayMode);
			DoubleList values1 = logData1.getValues();
			DoubleList values2 = logData2.getValues();
			int seriesIndex = 0;
			for (DoubleIterator i1 = values1.iterator(), i2 =
				values2.iterator(); i1.hasNext() && i2.hasNext(); seriesIndex++) {
				int y1 = getY(i1.next(), seriesIndex);
				int y2 = getY(i2.next(), seriesIndex);
				SeriesProperties sp =
					properties
						.getSeriesGroup()
						.getSeriesProperties()
						.get(seriesIndex);
				if (sp.isVisible()) {
					g.setColor(sp.getColor());
					g.drawLine(x1, y1 + insets.top, x2, y2 + insets.top);
				}
			}
		}
	}

	private boolean isDrawing(LogData[] displayDatas, long startDate, int i) {
		return i < displayDatas.length - 1
			&& startDate <= displayDatas[i + 1].getDate().getTime();
	}

	private int getY(double value, int seriesIndex) {
		SeriesProperties sp =
			properties.getSeriesGroup().getSeriesProperties().get(seriesIndex);
		DataHolder holder =
			Manager.getInstance().findDataHolder(
				sp.getHolderString().getHolderId());
		float max = 0;
		float min = 0;
		if (isDigital(holder)) {
			max = 1.1f;
			min = -0.1f;
		} else {
			max = sp.getMax();
			min = sp.getMin();
		}
		float os = properties.getVerticalLine() / getHalfVerticalCount();
		float r = os / (max - min);
		float dispMax = os * seriesIndex;
		float dispMin = dispMax + os;
		int round = (int) round((max - value) * r + dispMax);
		return (round <= dispMin && round >= dispMax)
			? round
			: (round > dispMin) ? round(dispMin) : round(dispMax);
	}

	private float getHalfVerticalCount() {
		return properties.getVerticalCount() / 2F;
	}

	public void drawVerticalString(Graphics g,
			int top,
			int x,
			int y,
			int i,
			int drawSeriesIndex) {
		if (properties.isVisibleVerticalString()) {
			if (isDrawDateString(i, drawSeriesIndex)) {
				SeriesProperties p =
					properties
						.getSeriesGroup()
						.getSeriesProperties()
						.get(drawSeriesIndex);
				float max = p.getMax();
				DecimalFormat f = new DecimalFormat(p.getVerticalFormat());
				float number =
					max
						- (i - drawSeriesIndex * 2)
						* ((max - p.getMin()) / properties.getVerticalCount() * getHalfVerticalCount());
				DataHolder holder =
					Manager.getInstance().findDataHolder(
						p.getHolderString().getHolderId());
				if (isDigital(holder)) {
					digitalDraw(g, top, x, y, p, number);
				} else {
					analogDraw(g, top, x, y, p, number, holder);
				}
			}
		}
	}

	private void digitalDraw(Graphics g,
			int top,
			int x,
			int y,
			SeriesProperties p,
			float number) {
		String dateStr = "";
		if (number == 0F) {
			dateStr = "OFF";
		} else if (number == 1F) {
			dateStr = "ON ";
		}
		FontMetrics metrics = g.getFontMetrics();
		if (p.isVisible()) {
			g.setColor(p.getColor());
			g.drawString(
				dateStr,
				x
					- metrics.stringWidth(dateStr)
					- properties.getScalePixcelSize(),
				Math.round(top + y + metrics.getAscent() / DATE_STRING_RATE));
		}
	}

	private void analogDraw(Graphics g,
			int top,
			int x,
			int y,
			SeriesProperties p,
			float number,
			DataHolder holder) {
		ConvertValue converter =
			(ConvertValue) holder
				.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
		double ref = converter.convertInputValueUnlimited(number);
		String dateStr = converter.convertStringValueUnlimited(ref);
		FontMetrics metrics = g.getFontMetrics();
		if (p.isVisible()) {
			g.setColor(p.getColor());
			g.drawString(
				dateStr,
				x
					- metrics.stringWidth(dateStr)
					- properties.getScalePixcelSize(),
				Math.round(top + y + metrics.getAscent() / DATE_STRING_RATE));
		}
	}

	private boolean isDrawDateString(int i, int drawSeriesIndex) {
		int ssi = drawSeriesIndex * 2;
		return i >= ssi && i <= ssi + 2;
	}
}
