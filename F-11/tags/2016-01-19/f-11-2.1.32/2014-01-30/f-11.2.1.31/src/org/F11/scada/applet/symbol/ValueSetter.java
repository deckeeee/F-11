/*
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2003 Freedom, Inc. All Rights Reserved.
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


/**
 * デジタル及びアナログの値を設定するインターフェイスです。
 * @author hori
 */
public interface ValueSetter {

	/**
	 * 値をホルダに設定し、書込みメソッドを呼び出します。
	 * @param variableValue 設定する値（インスタンスによっては無視されます）
	 */
	public void writeValue(Object variableValue);

	/**
	 * データプロバイダ名＋データホルダー名をアンダーバーで結合した文字列配列を返します。
	 * @return データプロバイダ名＋データホルダー名をアンダーバーで結合した文字列配列
	 */
	public String getDestination();
	
	/**
	 * 値の設定が固定値ならtrueを、任意の入力値ならfalseを返します。
	 * @return 値の設定が固定値ならtrueを、任意の入力値ならfalseを返します。
	 */
	public boolean isFixed();
}
