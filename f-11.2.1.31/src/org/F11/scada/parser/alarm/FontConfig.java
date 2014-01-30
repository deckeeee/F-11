/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/alarm/FontConfig.java,v 1.1 2003/10/23 09:58:34 frdm Exp $
 * $Revision: 1.1 $
 * $Date: 2003/10/23 09:58:34 $
 * 
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
 */

package org.F11.scada.parser.alarm;

import java.awt.Font;
import java.util.HashMap;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * フォント情報を保持する設定JavaBeanクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class FontConfig {
	/** フォントスタイルの文字列と定数のマップです */
	private static HashMap styleMap = new HashMap();
	/** フォント種類 */
	private String type;
	/** フォントポイント数 */
	private int point;
	/** フォントスタイル */
	private String style;
	
	static {
		styleMap.put("PLAIN", new Integer(Font.PLAIN));
		styleMap.put("BOLD", new Integer(Font.BOLD));
		styleMap.put("ITALIC", new Integer(Font.ITALIC));
		styleMap.put("BOLD+ITALIC", new Integer(Font.BOLD + Font.ITALIC));
	};

	/**
	 * フォントポイント数を返します
	 * @return フォントポイント数
	 */
	public int getPoint() {
		return point;
	}

	/**
	 * フォントスタイルを返します
	 * @return フォントスタイル
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * フォントタイプを返します
	 * @return フォントタイプ
	 */
	public String getType() {
		return type;
	}

	/**
	 * フォントポイント数を設定します
	 * @param i フォントポイント数
	 */
	public void setPoint(int i) {
		point = i;
	}

	/**
	 * フォントスタイルを設定します
	 * @param i フォントスタイル
	 */
	public void setStyle(String string) {
		style = string;
	}

	/**
	 * フォントタイプを設定します
	 * @param string フォントタイプ
	 */
	public void setType(String string) {
		type = string;
	}

	/**
	 * このオブジェクトの文字列情報を返します。
	 * jakarta commons Lang, ToStringBuilderの実装に依存しています。
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * フォントスタイルをFontで定義されている定数で返します。
	 * 定義されていない文字列が指定された場合は、Font.PLAINを返します。
	 * @return Fontで定義されている定数
	 */
	private int getFontStyle() {
		String key = style.toUpperCase();
		return styleMap.containsKey(key)
			? ((Integer) styleMap.get(key)).intValue()
			: Font.PLAIN;
	}
	
	/**
	 * プロパティより生成したフォントを返します。
	 * @return プロパティより生成したフォントを返します。
	 */
	public Font getFont() {
		return new Font(type, getFontStyle(), point);
	}
}
