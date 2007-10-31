/*
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

package org.F11.scada.applet.symbol;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * 色定義ファイル "/resources/colors.txt" を基に、文字列から Color クラスを返します。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ColorFactory {
	/** 唯一のインスタンスです（シングルトンパターン） */
	private static ColorFactory instance = new ColorFactory();
	/** 色名とColorクラスのマップです。 */
	private Map colorMap;

	/**
	 * private コンストラクタ。new 演算子を利用してインスタンスを生成できません。
	 */
	private ColorFactory() {
		colorMap = new HashMap(200);

		URL url = getClass().getResource("/resources/colors.txt");
		InputStream ir = null;
		BufferedReader reader = null;
		try {
			// convert url to buffered string
			ir = url.openStream();
			reader =
				new BufferedReader(
					new InputStreamReader(ir, "SJIS"));

			// read one line at a time, put into Map
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if (!line.startsWith("//")) {
					StringTokenizer st = new StringTokenizer(line);
					colorMap.put(st.nextToken().toUpperCase(),
						Color.decode("0x" + st.nextToken().toUpperCase()));
				}
			}
			ir.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (ir != null) {
				try {
					ir.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文字列から Color クラスを返す、ファクトリーメソッドです。
	 * @param nm 定義済み色名
	 * @return 定義済み色名が存在する場合 対応する Color クラス。ない場合 null を返します。
	 */
	public static Color getColor(String nm) {
		if (nm == null)
			return null;
		return (Color)instance.colorMap.get(nm.toUpperCase());
	}

	/**
	 * 保持している色名のリストを返します。
	 * @return 定義済み色名が存在する場合 対応する Color クラス。ない場合 null を返します。
	 */
	public static Set getColorNames() {
		return new TreeSet(instance.colorMap.keySet());
	}
}
