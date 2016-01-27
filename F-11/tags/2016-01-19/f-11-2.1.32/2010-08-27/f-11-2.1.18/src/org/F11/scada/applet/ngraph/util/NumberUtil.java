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

package org.F11.scada.applet.ngraph.util;

/**
 * 数値に関するユーティリティークラス
 * 
 * @author maekawa
 *
 */
public abstract class NumberUtil {
	/**
	 * フォーマット文字列より、スピナーの増分を算出する。
	 * 
	 * @param format フォーマット文字列
	 * @return スピナーの増分
	 */
	public static float getStep(String format) {
		int period = format.indexOf('.');
		if (period < 0) {
			return 1F;
		} else {
			double step = format.length() - period - 1;
			return 1F / (float) Math.pow(10D, step);
		}
	}
}
