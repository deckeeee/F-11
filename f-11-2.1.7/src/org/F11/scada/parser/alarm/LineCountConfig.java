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
 */

package org.F11.scada.parser.alarm;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 行数情報を保持する設定JavaBeanクラスです。
 * 
 * @author hori <hoti@users.sourceforge.jp>
 */
public final class LineCountConfig {
	/** 通常の行数 */
	private int value;
	/** 縮小時の行数 */
	private int min;

	public LineCountConfig() {
		this(0, 0);
	}
	public LineCountConfig(int value, int min) {
		this.value = value;
		this.min = min;
	}
	/**
	 * 通常の行数を返します。
	 * 
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * 通常の行数を設定します。
	 * 
	 * @return
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * 縮小時の行数を返します。
	 * 
	 * @return
	 */
	public int getMin() {
		return min;
	}

	/**
	 * 縮小時の行数を設定します。
	 * 
	 * @return
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * このオブジェクトの文字列情報を返します。 jakarta commons Lang, ToStringBuilderの実装に依存しています。
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
