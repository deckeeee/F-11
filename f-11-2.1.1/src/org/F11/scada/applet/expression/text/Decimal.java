/*
 * =============================================================================
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

package org.F11.scada.applet.expression.text;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Decimal implements Format {
	private final DecimalFormat format;
	private final int scale;

	public Decimal(String format) {
		this.format = new DecimalFormat(format);
		scale = getScale(format);
	}

	private int getScale(String format2) {
		int period = format2.indexOf(".");
		return period < 0 ? 0 : format2.substring(period + 1).length();
	}

	public String format(double number) {
		return format.format(getRoundHalfUped(number));
	}

	private double getRoundHalfUped(double number) {
		BigDecimal bd = new BigDecimal(String.valueOf(number));
		return bd.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
