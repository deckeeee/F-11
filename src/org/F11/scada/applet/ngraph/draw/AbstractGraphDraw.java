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


import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;

import org.F11.scada.applet.ngraph.GraphProperties;
import org.F11.scada.applet.ngraph.LogData;
import org.F11.scada.applet.ngraph.SeriesProperties;


public abstract class AbstractGraphDraw implements GraphDraw {
	protected static final float DATE_STRING_RATE = 2.5F;
	protected final GraphProperties properties;

	public AbstractGraphDraw(GraphProperties properties) {
		this.properties = properties;
	}

	public void drawUnitMark(Graphics g, int top, int x, int drawSeriesIndex) {
		SeriesProperties seriesProperties =
			properties.getSeriesGroup().getSeriesProperties().get(
				drawSeriesIndex);
		String unitMark = getUnitMark(seriesProperties);
		FontMetrics metrics = g.getFontMetrics();
		if (seriesProperties.isVisible()) {
			g.drawString(unitMark, x
				- metrics.stringWidth(unitMark)
				- properties.getScalePixcelSize(), top - metrics.getHeight());
		}
	}

	private String getUnitMark(SeriesProperties seriesProperties) {
		return null == seriesProperties.getUnitMark() ? "[---]" : "["
			+ seriesProperties.getUnitMark()
			+ "]";
	}

	protected int getX(
			LogData value,
			long startDate,
			long lastDate,
			boolean isAllSpanDisplayMode) {
		Insets insets = properties.getInsets();
		return (int) Math.round((double) properties
			.getHorizontalLine(isAllSpanDisplayMode)
			/ (double) (lastDate - startDate)
			* (double) (value.getDate().getTime() - startDate)
			+ insets.left);
	}
}
