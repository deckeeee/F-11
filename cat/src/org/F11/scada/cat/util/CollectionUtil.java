/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.cat.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * コレクションに関するユーティリティークラス。このクラスをstaticインポートし、それぞれのメソッドを呼び出すことで以下の呼び出しが可能になります。
 * 
 * <pre>
 *  List&lt;String&gt; strings = list(&quot;A&quot;, &quot;B&quot;, &quot;C&quot;);
 *  Map&lt;String, String&gt; stringMap = map($(&quot;X&quot;, &quot;1&quot;), $(&quot;Y&quot;, &quot;2&quot;));
 *  Set&lt;String&gt; stringSet = set(&quot;A&quot;, &quot;B&quot;, &quot;C&quot;);
 * </pre>
 * 
 * メソッド呼び出しの際に使用するクラスが類推できる為、new で記述するよりもわずらわしさが減少します。
 * 
 * @author maekawa
 * 
 */
public abstract class CollectionUtil {
	/**
	 * 指定したクラスのArrayListを生成します
	 * 
	 * @param <T> クラス
	 * @return 指定したクラスのArrayListを生成します
	 */
	public static <T> List<T> list() {
		return new ArrayList<T>();
	}

	/**
	 * 指定したクラスと初期容量のArrayListを生成します
	 * 
	 * @param <T> クラス
	 * @param size 初期容量
	 * @return 指定したクラスと初期容量のArrayListを生成します
	 */
	public static <T> List<T> list(int size) {
		return new ArrayList<T>(size);
	}

	/**
	 * 指定した内容でリストを生成します。
	 * 
	 * @param <T> クラス
	 * @param elements 初期値
	 * @return 指定した内容でリストを生成します。
	 */
	public static <T> List<T> list(T... elements) {
		return new ArrayList<T>(Arrays.asList(elements));
	}

	/**
	 * 指定したクラスのHashSetを生成します
	 * 
	 * @param <T> クラス
	 * @return 指定したクラスのHashSetを生成します
	 */
	public static <T> Set<T> set() {
		return new HashSet<T>();
	}

	/**
	 * 指定したクラスのHashSetを初期容量サイズで生成します
	 * 
	 * @param <T> クラス
	 * @param size 初期容量サイズ
	 * @return 指定したクラスのHashSetを生成します
	 */
	public static <T> Set<T> set(int size) {
		return new HashSet<T>(size);
	}

	/**
	 * 指定した内容でセットを生成します。
	 * 
	 * @param <T> クラス
	 * @param elements 初期値
	 * @return 指定した内容でセットを生成します。
	 */
	public static <T> Set<T> set(T... elements) {
		return new HashSet<T>(Arrays.asList(elements));
	}

	/**
	 * 指定したクラスのHashMapを生成します
	 * 
	 * @param <K> キーになるクラス
	 * @param <V> 値になるクラス
	 * @return 指定したクラスのHashMapを生成します
	 */
	public static <K, V> Map<K, V> map() {
		return new HashMap<K, V>();
	}

	private static class Pair<A, B> {
		private A first;
		private B second;

		public Pair(A first, B second) {
			this.first = first;
			this.second = second;
		}

		public A getFirst() {
			return first;
		}

		public B getSecond() {
			return second;
		}
	}

	/**
	 * 指定されたリテラルでマップを生成します。
	 * 
	 * <pre>
	 * Map&lt;Integer, String&gt; m = map($(0, &quot;0&quot;), $(1, &quot;1&quot;));
	 * </pre>
	 * 
	 * のように記述します。
	 * 
	 * @param <K> キーのクラス
	 * @param <V> 値のクラス
	 * @param entries エントリーのリテラル
	 * @return 指定されたリテラルでマップを生成します。
	 */
	public static <K, V> Map<K, V> map(Pair<K, V>... entries) {
		Map<K, V> map = new HashMap<K, V>();
		for (Pair<K, V> entry : entries) {
			map.put(entry.getFirst(), entry.getSecond());
		}
		return map;
	}

	/**
	 * 値とキーのペアを生成します。
	 * 
	 * @param <A> キーのクラス
	 * @param <B> 値のクラス
	 * @param first キー値
	 * @param second 値
	 * @return 値とキーのペアを生成します。
	 */
	public static <A, B> Pair<A, B> $(A first, B second) {
		return new Pair<A, B>(first, second);
	}
}
