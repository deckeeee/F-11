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

package org.F11.scada.util;

import java.awt.Component;
import java.awt.Font;

public abstract class FontUtil {
	/**
	 * コンポーネントにフォントを設定します。
	 * 
	 * @param name フォント名
	 * @param style フォントスタイル
	 * @param size フォントサイズ
	 * @param comp コンポーネント
	 */
	public static void setFont(
			String name,
			String style,
			String size,
			Component comp) {
		Font font = comp.getFont();
		comp.setFont(new Font(
			getName(font, name),
			getStyle(font, style),
			getSize(font, size)));
	}

	/**
	 * コンポーネントにフォントを設定します。
	 * 
	 * @param name フォント名
	 * @param style フォントスタイル
	 * @param size フォントサイズ
	 * @param comp コンポーネント
	 */
	public static void setFont(
			String name,
			String style,
			int size,
			Component comp) {
		Font font = comp.getFont();
		comp
			.setFont(new Font(getName(font, name), getStyle(font, style), size));
	}

	private static String getName(Font font, String name) {
		return (null == name) ? font.getFontName() : name;
	}

	private static int getStyle(Font font, String style) {
		int s = Font.PLAIN;
		if (null == style) {
			s = Font.PLAIN;
		} else if ("BOLD".equals(style.toUpperCase())) {
			s = Font.BOLD;
		} else if ("ITALIC".equals(style.toUpperCase())) {
			s = Font.ITALIC;
		}
		return s;
	}

	private static int getSize(Font font, String size) {
		return (null == size) ? font.getSize() : Integer.parseInt(size);
	}

	public static Font getFont(String fontStr) {
		if (fontStr != null) {
			String[] s = fontStr.split("-");
			return new Font(s[0], getFontStyle(s[1]), getNumber(s[2]));
		} else {
			return new Font("Monospaced", Font.PLAIN, 18);
		}
	}

	private static int getFontStyle(String s) {
		if ("PLAIN".equalsIgnoreCase(s)) {
			return Font.PLAIN;
		} else if ("BOLD".equalsIgnoreCase(s)) {
			return Font.BOLD;
		} else {
			return Font.ITALIC;
		}
	}

	private static int getNumber(String string) {
		return Integer.parseInt(string);
	}

}
