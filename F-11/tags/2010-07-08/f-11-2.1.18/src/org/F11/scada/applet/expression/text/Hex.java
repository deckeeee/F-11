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

public class Hex implements Format {
	private final boolean isUpperCase;
	private final int length;

	public Hex(String format) {
		if (format.startsWith("0X")) {
			isUpperCase = true;
			length = getLength(format);
		} else if (format.startsWith("0x")) {
			isUpperCase = false;
			length = getLength(format);
		} else {
			throw new IllegalArgumentException("format is not `hex' string.");
		}
	}

	private int getLength(String format) {
		return Integer.parseInt(format.substring(2));
	}

	public String format(double number) {
		return isUpperCase
			? format(Integer.toHexString((int) Math.round(number)).toUpperCase())
					: format(Integer.toHexString((int) Math.round(number)));
	}

	/**
	 * 4文字以下の文字列なら前に0をパディングします。
	 * @param numberStr パディング対象文字列
	 * @return 4文字以下の文字列なら前に0をパディングします。
	 */
	private String format(String numberStr) {
		if (numberStr.length() > length) {
			return numberStr.substring(numberStr.length() - length);
		} else {
			int numberLength = numberStr.length();
			StringBuffer b = new StringBuffer(numberStr);
			for (int i = 0; i < length - numberLength; i++) {
				b.insert(0, '0');
			}
			return b.toString();
		}
	}
}