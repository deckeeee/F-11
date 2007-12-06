package org.F11.scada.applet.symbol;

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

/**
 * テンキーダイアログで数値編集をするシンボルのインターフェイスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface TenkeyEditable extends Editable {
	/**
	 * シンボルの値を返します
	 */
	public String getValue();
	/**
	 * シンボルに値を設定します
	 */
	public void setValue(String value);
	/**
	 * 最小値を返します
	 */
	public double getConvertMin();
	/**
	 * 最大値を返します
	 */
	public double getConvertMax();
	/**
	 * 数値表示フォーマット文字列を返します
	 */
	public String getFormatString();
	/**
	 * ダイアログタイトルを返します。
	 * @return ダイアログタイトル
	 */
	String getDialogTitle();
}
