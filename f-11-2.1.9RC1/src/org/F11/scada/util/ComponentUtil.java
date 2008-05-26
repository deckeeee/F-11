/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2007 Freedom, Inc. All Rights Reserved.
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
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

public abstract class ComponentUtil {
	/**
	 * コンポーネント内にマウスポイントがあるか判定します。
	 * 
	 * @param rectangle コンポーネント位置
	 * @param point マウスポイント位置
	 * @return コンポーネント内にマウスポイントがある場合 true、無い場合 false を返します。
	 */
	public static boolean contains(Rectangle rectangle, Point point) {
		return point.x >= 0
			&& point.y >= 0
			&& rectangle.width >= point.x
			&& rectangle.height >= point.y;
	}

	/**
	 * コンテナ内の対象クラス子コンポーネントを返します。
	 * 
	 * @param klass 検索するクラス
	 * @param container 検索対象になるコンテナオブジェクト
	 * @return 検索対象コンテナオブジェクト内に、検索するクラスが存在する場合はそのコンポーネントを存在しない場合は null を返します。
	 */
	public static Component getChildrenComponent(
			Class<?> klass,
			Container container) {
		Component ret = null;
		Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if (klass.isInstance(component)) {
				ret = component;
				break;
			}
		}
		return ret;
	}

	/**
	 * {@link SwingUtilities#getAncestorOfClass}の型推論版。 コンポーネント階層で comp
	 * の上位を検索するための簡易メソッドであり、見つかった c クラスの最初のオブジェクトを返します。c クラスが見つからない場合は null
	 * を返します。
	 * 
	 * @param <C> 検索するクラス
	 * @param c 検索するクラスリテラル
	 * @param comp 検索対象コンポーネント
	 * @return 上位に検索対象のクラスがあればそのオブジェクトを無い場合は null を返します
	 */
	public static <C extends Container> C getAncestorOfClass(
			Class<C> c,
			Component comp) {
		return (C) SwingUtilities.getAncestorOfClass(c, comp);
	}
}
