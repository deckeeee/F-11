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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.applet.symbol.TextSymbol;

public class TextDateTimeSymbol extends TextSymbol {
	private static final long serialVersionUID = 2689800592438165532L;

	private Date date;

	/**
	 * @param property
	 */
	public TextDateTimeSymbol(SymbolProperty property) {
		super(property);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * ï∂éöóÒÇê›íËÇµÇ‹Ç∑ÅB
	 */
	protected void setFormatedString() {
		if (date == null) {
			this.setText("");
			setAlign();
			return;
		}
		String format = getProperty("value");
		if (0 <= format.indexOf("%H")) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			DecimalFormat df = new DecimalFormat("00");
			String hourno = df.format(cal.get(Calendar.HOUR_OF_DAY) + 1);
			format = format.replaceAll("%H", hourno);
		}
		SimpleDateFormat sf = new SimpleDateFormat(format);
		this.setText(sf.format(date));
		setAlign();
	}

}
