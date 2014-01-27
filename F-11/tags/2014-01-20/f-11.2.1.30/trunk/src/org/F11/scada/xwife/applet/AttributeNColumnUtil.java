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

package org.F11.scada.xwife.applet;

import org.F11.scada.EnvironmentManager;

/**
 * 属性nを表示するモードに関するユーティリティクラス
 * 
 * @author maekawa
 *
 */
public abstract class AttributeNColumnUtil {
	/**
	 * 属性nを表示するかどうかを返す。
	 * 
	 * @return 属性nを表示するかどうかを返す。表示する場合は true をそうで無い場合は false を返す。
	 */
	public static boolean isAttributeDisplay() {
		return Boolean.parseBoolean(EnvironmentManager.get(
			"/server/alarm/attributen/enable",
			"false"));
	}

	/**
	 * 最新警報を組み立てる列を抽出します。
	 * 
	 * @param i 列番号
	 * @param value 列の内容
	 * @return 表示する列の場合 true をそうで無い場合は false を返す。
	 */
	public static boolean isDisplayColumn(int i, Object value) {
		if (!AttributeNColumnUtil.isAttributeDisplay()) {
			return null != value && 17 != i && 18 != i && 19 != i && 20 != i;
		}
		return null != value && 17 != i;
	}

}
