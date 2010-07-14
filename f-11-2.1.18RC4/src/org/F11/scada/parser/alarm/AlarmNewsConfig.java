/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/alarm/AlarmNewsConfig.java,v 1.2.4.2 2005/07/05 10:26:39 hori Exp $
 * $Revision: 1.2.4.2 $
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
 * 最新情報の設定クラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmNewsConfig {
	/** デフォルトの背景色 */
	private static final Color DEFAULT_BACKGROUND = ColorFactory.getColor("lightgrey");
	/** フォント設定 */
	private FontConfig fontConfig;
	/** 背景色 */
	private String backGround;
	/** 行数定義 */
	private LineCountConfig lineCountConfig = new LineCountConfig(5, 1);

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
	 * 背景色を返します。
	 * 
	 * @return 背景色を返します
	 */
	public String getBackGround() {
		return backGround;
	}

	/**
	 * 背景色を設定します。
	 * 
	 * @param color
	 *            背景色を設定します
	 */
	public void setBackGround(String color) {
		this.backGround = color;
	}

	/**
	 * 背景色の Color オブジェクトを返します。
	 * 
	 * @return 背景色の Color オブジェクトを返します
	 */
	public Color getBackGroundColor() {
		return ColorFactory.getColor(backGround) == null ? DEFAULT_BACKGROUND : ColorFactory
				.getColor(backGround);
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

}
