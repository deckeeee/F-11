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

package org.F11.scada.applet.ngraph;

import java.awt.Color;

import org.F11.scada.server.register.HolderString;

public class SeriesProperties {
	/** シリーズのインデックス */
	private int index;
	/** 表示・非表示 */
	private Boolean visible;
	/** 色 */
	private Color color;
	/** 機器番号 */
	private String unit;
	/** 機器名称 */
	private String name;
	/** 参照値 */
	private Float referenceValue;
	/** 現在値 */
	private Float nowValue;
	/** 単位記号 */
	private String unitMark;
	/** 目盛数値のフォーマット */
	private String verticalFormat;
	/** 表示最大値 */
	private float max;
	/** 表示最小値 */
	private float min;
	/** 現在値のホルダID */
	private HolderString holderString;

	/**
	 * シリーズのインデックス
	 * 
	 * @return シリーズのインデックス
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * シリーズのインデックスを設定します
	 * 
	 * @param index シリーズのインデックス
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * シリーズの色
	 * 
	 * @return シリーズの色
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * シリーズの色を設定します。
	 * 
	 * @param color シリーズの色
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * 縦スケールの目盛数値表示フォーマット
	 * 
	 * @return 縦スケールの目盛数値表示フォーマット
	 */
	public String getVerticalFormat() {
		return verticalFormat;
	}

	/**
	 * 縦スケールの目盛数値表示フォーマットを設定
	 * 
	 * @param verticalFormat 縦スケールの目盛数値表示フォーマット
	 */
	public void setVerticalFormat(String verticalFormat) {
		this.verticalFormat = verticalFormat;
	}

	/**
	 * シリーズの表示・非表示
	 * 
	 * @return シリーズの表示・非表示
	 */
	public Boolean isVisible() {
		return visible;
	}

	/**
	 * シリーズの表示・非表示を設定
	 * 
	 * @param visible シリーズの表示・非表示
	 */
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	/**
	 * 機器番号
	 * 
	 * @return 機器番号
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * 機器番号を設定
	 * 
	 * @param unit 機器番号
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * 機器名称
	 * 
	 * @return 機器名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 機器名称を設定
	 * 
	 * @param name 機器名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 参照値
	 * 
	 * @return 参照値
	 */
	public Float getReferenceValue() {
		return referenceValue;
	}

	/**
	 * 参照値を設定
	 * 
	 * @param referenceValue 参照値
	 */
	public void setReferenceValue(Float referenceValue) {
		this.referenceValue = referenceValue;
	}

	/**
	 * 現在値
	 * 
	 * @return 現在値
	 */
	public Float getNowValue() {
		return nowValue;
	}

	/**
	 * 現在値を設定
	 * 
	 * @param nowValue 現在値
	 */
	public void setNowValue(Float nowValue) {
		this.nowValue = nowValue;
	}

	/**
	 * 単位名称
	 * 
	 * @return 単位名称
	 */
	public String getUnitMark() {
		return unitMark;
	}

	/**
	 * 単位名称を設定
	 * 
	 * @param unitMark 単位名称
	 */
	public void setUnitMark(String unitMark) {
		this.unitMark = unitMark;
	}

	/**
	 * シリーズの表示最大値
	 * 
	 * @return シリーズの表示最大値
	 */
	public float getMax() {
		return max;
	}

	/**
	 * シリーズの表示最大値を設定
	 * 
	 * @param max シリーズの表示最大値
	 */
	public void setMax(float max) {
		this.max = max;
	}

	/**
	 * シリーズの表示最小値
	 * 
	 * @return シリーズの表示最小値
	 */
	public float getMin() {
		return min;
	}

	/**
	 * シリーズの表示最小値を設定
	 * 
	 * @param min シリーズの表示最小値
	 */
	public void setMin(float min) {
		this.min = min;
	}

	/**
	 * このシリーズが参照しているホルダを返します
	 * 
	 * @return このシリーズが参照しているホルダを返します
	 */
	public HolderString getHolderString() {
		return holderString;
	}

	/**
	 * このシリーズが参照しているホルダを設定します
	 * 
	 * @param holderString このシリーズが参照しているホルダを設定します
	 */
	public void setHolderString(HolderString holderString) {
		this.holderString = holderString;
	}
}
