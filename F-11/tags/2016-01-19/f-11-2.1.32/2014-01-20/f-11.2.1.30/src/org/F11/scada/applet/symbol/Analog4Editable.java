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

import org.F11.scada.data.ConvertValue;

/**
 * ４個のアナログ値を編集をするシンボルのインターフェイスです。
 * @author hori
 */
public interface Analog4Editable extends Editable {
	/**
	 * シンボルの値を返します
	 */
	public String[] getValues();
	/**
	 * シンボルに値を設定します
	 */
	public void setValue(String[] values);
	/**
	 * ConvertValueを返します 
	 */
	public ConvertValue getConvertValue();
	/**
	 * 数値表示フォーマット文字列を返します
	 */
	public String getFormatString();
	/**
	 * 値を編集する為のダイアログ名を返します。
	 */
	public String getSecondDialogName();
	/**
	 * 値を編集する為のダイアログ名を設定します。
	 */
	public void setSecondDialogName(String secondDialogName);
	/**
	 * ダイアログのタイトルを返します
	 * @return ダイアログのタイトルを返します
	 */
	String getDialogName();
}
