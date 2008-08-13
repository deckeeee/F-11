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

import static org.F11.scada.cat.util.CollectionUtil.list;

import java.util.Collections;
import java.util.List;

import org.xml.sax.Attributes;

/**
 * 属性オブジェクトを扱うユーティリティークラスです。
 * 
 * @author maekawa
 */
public abstract class AttributesUtil {
	/**
	 * 属性名の値を返します。但し、値が指定されていない場合・ヌル文字の場合は、nullを返します。
	 * 
	 * @param attname 属性名
	 * @param atts 属性オブジェクト
	 * @return 属性名の値を返します。
	 */
	public static String getValue(String attname, Attributes atts) {
		return getNonNullString(atts.getValue(attname));
	}

	/**
	 * 文字列が null や空白の場合 null を以外の場合は引数の文字列を返します。
	 * 
	 * @param str 判定する文字列
	 * @return 文字列が null や空白の場合 null を以外の場合は引数の文字列を返します。
	 */
	public static String getNonNullString(String str) {
		return (str == null || "".equals(str)) ? null : str;
	}

	/**
	 * 属性名の値をbooleanで返します。但し、値が指定されていない場合・ヌル文字の場合は、falseを返します。
	 * 値文字列の判定にはBoolean#valueOfメソッドを使用しています。
	 * 
	 * @param attname 属性名
	 * @param atts 属性オブジェクト
	 * @return 属性名の値を返します。
	 * @see Boolean#valueOf(java.lang.String)
	 */
	public static boolean getBooleanValue(String attname, Attributes atts) {
		return Boolean.valueOf(getValue(attname, atts)).booleanValue();
	}

	/**
	 * 引数の文字列が null か 空白の場合 true を返します。
	 * 
	 * @param str 判定する文字列
	 * @return 引数の文字列が null か 空白の場合 true を返します。
	 */
	public static boolean isSpaceOrNull(String str) {
		return str == null || "".equals(str);
	}

	public static long getLongValue(String s) {
		if (isSpaceOrNull(s)) {
			return 0L;
		} else {
			try {
				return Long.parseLong(s);
			} catch (Exception e) {
				return 0L;
			}
		}
	}

	/**
	 * 文字列が null か空白なら null を以外の場合は"%"で囲んだ文字列を返します。
	 * 
	 * @param s 文字列
	 * @return 文字列が null か空白なら null を以外の場合は"%"で囲んだ文字列を返します。
	 */
	public static String getLikeString(String s) {
		return isSpaceOrNull(s) ? null : "%" + s + "%";
	}

	/**
	 * 引数の文字列を「,」で分割します
	 * 
	 * @param tables テーブル名を「,」で区切った文字列
	 * @return テーブル名があればテーブル名のリストを無ければ空のリストを返します。
	 */
	public static List<String> getTables(String tables) {
		if (null != tables) {
			List<String> l = list();
			String[] s = tables.split("\\,");
			for (String string : s) {
				l.add(string.trim());
			}
			return l;
		} else {
			return Collections.emptyList();
		}
	}
}
