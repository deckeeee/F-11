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

import java.text.DecimalFormat;

import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.applet.symbol.TextSymbol;

public class TextVerticalSymbol extends TextSymbol {
	private static final long serialVersionUID = -6862126547881510595L;

	private DecimalFormat df;
	private double min;
	private double max;

	/**
	 * @param property
	 */
	public TextVerticalSymbol(SymbolProperty property) {
		super(property);
	}

	public void setPattern(String pattern, double min, double max) {
		this.df = new DecimalFormat(pattern);
		this.min = min;
		this.max = max;
	}

	/**
	 * ï∂éöóÒÇê›íËÇµÇ‹Ç∑ÅB
	 */
	protected void setFormatedString() {
		double k = Double.parseDouble(getProperty("value"));
		double level = (max - min) * k + min;
		this.setText(df.format(level));
		setAlign();
	}

}
