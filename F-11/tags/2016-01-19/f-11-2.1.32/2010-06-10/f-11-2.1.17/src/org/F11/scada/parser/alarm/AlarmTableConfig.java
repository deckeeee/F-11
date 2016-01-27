/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/alarm/AlarmTableConfig.java,v 1.3.2.2 2005/07/05 10:26:39 hori Exp $
 * $Revision: 1.3.2.2 $
 * $Date: 2005/07/05 10:26:39 $
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

import java.awt.Color;

import org.F11.scada.applet.symbol.ColorFactory;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 警報・状態一覧の設定クラスです
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmTableConfig {
	/** デフォルトのテーブル背景色 */
	private static final Color DEFAULT_BACKGROUND = ColorFactory.getColor("white");
	/** デフォルトのヘッダー背景色 */
	private static final Color DEFAULT_HEADERBACKGROUND = ColorFactory.getColor("lightgrey");
	/** デフォルトのヘッダー文字色 */
	private static final Color DEFAULT_HEADERFOREGROUND = ColorFactory.getColor("black");
	/** フォント設定 */
	private FontConfig fontConfig;
	/** テーブル背景色 */
	private String backGround;
	/** ヘッダー背景色 */
	private String headerBackGround;
	/** ヘッダー文字色 */
	private String headerForeGround;
	/** デフォルト表示のタブ番号 (0～2) */
	private int defaultTabNo;
	/** 行数定義 */
	private LineCountConfig lineCountConfig = new LineCountConfig(20, 0);

	/**
	 * フォント設定を返します
	 * 
	 * @return フォント設定
	 */
	public FontConfig getFontConfig() {
		return fontConfig;
	}

	/**
	 * フォント設定を設定します
	 * 
	 * @param config
	 *            フォント設定
	 */
	public void setFontConfig(FontConfig config) {
		fontConfig = config;
	}

	/**
	 * テーブル背景色を返します。
	 * 
	 * @return テーブル背景色を返します
	 */
	public String getBackGround() {
		return backGround;
	}

	/**
	 * テーブル背景色を設定します。
	 * 
	 * @param color
	 *            テーブル背景色
	 */
	public void setBackGround(String color) {
		this.backGround = color;
	}

	/**
	 * ヘッダー背景色を返します。
	 * 
	 * @return ヘッダー背景色を返します
	 */
	public String getHeaderBackGround() {
		return headerBackGround;
	}

	/**
	 * ヘッダー背景色を設定します。
	 * 
	 * @param color
	 *            ヘッダー背景色
	 */
	public void setHeaderBackGround(String color) {
		this.headerBackGround = color;
	}

	/**
	 * ヘッダー文字色を返します。
	 * 
	 * @return ヘッダー文字色を返します
	 */
	public String getHeaderForeGround() {
		return headerForeGround;
	}

	/**
	 * ヘッダー文字色を設定します。
	 * 
	 * @param color
	 *            ヘッダー文字色
	 */
	public void setHeaderForeGround(String color) {
		this.headerForeGround = color;
	}

	/**
	 * テーブル背景色の Color オブジェクトを返します。
	 * 
	 * @return テーブル背景色の Color オブジェクトを返します。
	 */
	public Color getBackGroundColor() {
		return ColorFactory.getColor(backGround) == null ? DEFAULT_BACKGROUND : ColorFactory
				.getColor(backGround);
	}

	/**
	 * ヘッダー背景色の Color オブジェクトを返します。
	 * 
	 * @return ヘッダー背景色の Color オブジェクトを返します。
	 */
	public Color getHeaderBackGroundColor() {
		return ColorFactory.getColor(headerBackGround) == null
				? DEFAULT_HEADERBACKGROUND
				: ColorFactory.getColor(headerBackGround);
	}

	/**
	 * ヘッダー文字色の Color オブジェクトを返します。
	 * 
	 * @return ヘッダー文字色の Color オブジェクトを返します。
	 */
	public Color getHeaderForeGroundColor() {
		return ColorFactory.getColor(headerForeGround) == null
				? DEFAULT_HEADERFOREGROUND
				: ColorFactory.getColor(headerForeGround);
	}

	/**
	 * 行数定義を返します。
	 * 
	 * @return
	 */
	public LineCountConfig getLineCountConfig() {
		return lineCountConfig;
	}

	/**
	 * 行数定義を設定します。
	 * 
	 * @param lineCountConfig
	 */
	public void setLineCountConfig(LineCountConfig lineCountConfig) {
		this.lineCountConfig = lineCountConfig;
	}

	/**
	 * このオブジェクトの文字列表現を返します
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * デフォルト表示のタブ番号を返します
	 * 
	 * @return デフォルト表示のタブ番号
	 */
	public int getDefaultTabNo() {
		return defaultTabNo;
	}

	/**
	 * デフォルト表示のタブ番号を設定します。 0 <= i <= 2 で設定する必要があります。この範囲外の数値が渡された場合は、0 を設定します。
	 * 
	 * @param i
	 *            表示タブ番号 (0～2)
	 */
	public void setDefaultTabNo(int i) {
		defaultTabNo = i;
	}

}
