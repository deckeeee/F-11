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

import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.applet.symbol.TextSymbol;

public class TextUnitsSymbol extends TextSymbol {
	private static final long serialVersionUID = 5021970322668097628L;

	private String units;

	/**
	 * @param property
	 */
	public TextUnitsSymbol(SymbolProperty property) {
		super(property);
	}

	public void setUnits(String units) {
		this.units = units;
	}

	/**
	 * �������ݒ肵�܂��B
	 */
	protected void setFormatedString() {
		String format = getProperty("value");
		this.setText(format.replaceAll("%s", units));
		setAlign();
	}

}
