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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.text.DecimalFormat;

public class BarFocus {
	private final int barCount;
	private final TextDateTimeSymbol[] dateArray;
	private final TextValueSymbol[] valuesArray;
	private Insets insets;
	private Color color;
	private BasicStroke basicStroke;

	public BarFocus(int barCount) {
		this.barCount = barCount;
		this.dateArray = new TextDateTimeSymbol[barCount];
		this.valuesArray = new TextValueSymbol[barCount];
	}

	public void setFocusData(BarData[] barArray, String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
		for (int i = 0; i < barCount; i++) {
			BarData data = barArray[i];
			if (data != null) {
				dateArray[i].setDate(data.getDate());
				valuesArray[i].setTextValue(df.format(data.getValue(0)));
			} else {
				dateArray[i].setDate(null);
				valuesArray[i].setTextValue("");
			}
			dateArray[i].updateProperty();
			valuesArray[i].updateProperty();
		}
	}

	public void draw(Graphics2D g2d, Rectangle rect) {
		g2d.setStroke(basicStroke);
		g2d.setColor(color);
		rect.x -= insets.left;
		rect.y -= insets.top;
		rect.width += insets.left + insets.right;
		rect.height += insets.top + insets.bottom;
		g2d.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
	}

	public Insets getInsets() {
		Insets ret = new Insets(insets.top, insets.left, insets.bottom,
				insets.right);
		if (ret.left < 0)
			ret.left = 0;
		if (ret.top < 0)
			ret.top = 0;
		if (ret.right < 0)
			ret.right = 0;
		if (ret.bottom < 0)
			ret.bottom = 0;
		return ret;
	}

	public void setOversize(int top, int left, int bottom, int right) {
		this.insets = new Insets(top, left, bottom, right);
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setDash(float dash_line, float dash_gap) {
		float[] dash = {dash_line, dash_gap};
		basicStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
	}

	public void addTextDateTimeSymbol(TextDateTimeSymbol symbol) {
		for (int i = 0; i < barCount; i++) {
			if (dateArray[i] == null) {
				dateArray[i] = symbol;
				break;
			}
		}
	}

	public void addTextValueSymbol(TextValueSymbol symbol) {
		for (int i = 0; i < barCount; i++) {
			if (valuesArray[i] == null) {
				valuesArray[i] = symbol;
				break;
			}
		}
	}

	public TextDateTimeSymbol[] getTextDateTimeSymbolArray() {
		return dateArray;
	}

	public TextValueSymbol[] getTextSymbolArray() {
		return valuesArray;
	}
}
