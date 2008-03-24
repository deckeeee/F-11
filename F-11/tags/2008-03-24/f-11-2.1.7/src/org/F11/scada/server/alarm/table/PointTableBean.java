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

package org.F11.scada.server.alarm.table;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * point_table の内容を表すクラスです。
 * <ul>
 * <li>point ポイントID
 * <li>unit　ポイント記号
 * <li>name　ポイント名称
 * <li>mark　ポイント記号　単位名称
 * </ul>
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PointTableBean implements Serializable {
	/** シリアライズID */
	private static final long serialVersionUID = -2234470409106572622L;
	/** ポイントID */
	private final int point;
	/** ポイント記号 */
	private final String unit;
	/** ポイント名称 */
	private final String name;
	/** ポイント記号　単位名称 */
	private final String mark;

	/**
	 * このオブジェクトを初期化します
	 * 
	 * @param point ポイントID
	 * @param unit ポイント記号
	 * @param name ポイント名称
	 * @param mark ポイント記号　単位名称
	 */
	public PointTableBean(int point, String unit, String name, String mark) {
		this.point = point;
		this.unit = unit;
		this.name = name;
		this.mark = mark;
	}

	/**
	 * ポイント記号単位名称を返します。
	 * @return ポイント記号単位名称
	 */
	public String getMark() {
		return mark;
	}

	/**
	 * ポイント名称を返します。
	 * @return ポイント名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * ポイントIDを返します。
	 * @return ポイントID
	 */
	public int getPoint() {
		return point;
	}

	/**
	 * ポイント記号を返します。
	 * @return ポイント記号
	 */
	public String getUnit() {
		return unit;
	}
	
	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new PointTableBean(point, unit, name, mark);
	}
	
	/**
	 * このオブジェクトの文字列表現を返します。
	 */
	public String toString() {
		return "{point="
			+ point
			+ ", unit="
			+ unit
			+ ", name="
			+ name
			+ ", mark="
			+ mark
			+ "}";
	}

}
