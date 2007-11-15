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

package org.F11.scada.server.frame.editor;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * @author hori
 */
public class TaskItemDefine implements Serializable {
	private static final long serialVersionUID = -6423247209064704437L;
	/** 縦スケールの最小値 */
	private double verticalMinimum;
	/** 縦スケールの最大値 */
	private double verticalMaximum;
	/** 縦スケールの最小入力値 */
	private double verticalInputMinimum;
	/** 縦スケールの最大入力値 */
	private double verticalInputMaximum;
	/** データプロバイダ名 */
//	private String dataProviderName;
	/** データホルダ名 */
	private String dataHolderName;
	/** ポイント番号 */
	private int pointNo;
	/** ポイント記号 */
	private String pointUnit;
	/** ポイント名称 */
	private String pointName;
	/** ポイント単位 */
	private String pointUnitMark;

	/**
	 * 
	 */
	public TaskItemDefine(
		double verticalMinimum,
		double verticalMaximum,
		double verticalInputMinimum,
		double verticalInputMaximum,
//		String dataProviderName,
		String dataHolderName,
		int pointNo,
		String pointUnit,
		String pointName,
		String pointUnitMark) {
		this.verticalMinimum = verticalMinimum;
		this.verticalMaximum = verticalMaximum;
		this.verticalInputMinimum = verticalInputMinimum;
		this.verticalInputMaximum = verticalInputMaximum;
//		this.dataProviderName = dataProviderName;
		this.dataHolderName = dataHolderName;
		this.pointNo = pointNo;
		this.pointUnit = pointUnit;
		this.pointName = pointName;
		this.pointUnitMark = pointUnitMark;
	}

	/**
	 * @return
	 */
	public String getDataHolderName() {
		return dataHolderName;
	}

	/**
	 * @return
	 */
//	public String getDataProviderName() {
//		return dataProviderName;
//	}

	/**
	 * @return
	 */
	public String getPointName() {
		return pointName;
	}

	/**
	 * @return
	 */
	public double getVerticalInputMaximum() {
		return verticalInputMaximum;
	}

	/**
	 * @return
	 */
	public double getVerticalInputMinimum() {
		return verticalInputMinimum;
	}

	/**
	 * @return
	 */
	public double getVerticalMaximum() {
		return verticalMaximum;
	}

	/**
	 * @return
	 */
	public double getVerticalMinimum() {
		return verticalMinimum;
	}

	/**
	 * @return
	 */
	public String getPointUnit() {
		return pointUnit;
	}

	/**
	 * @return
	 */
	public String getPointUnitMark() {
		return pointUnitMark;
	}

	/**
	 * @return
	 */
	public int getPointNo() {
		return pointNo;
	}

	/**
	 * このオブジェクトの文字列表現を返します。
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TaskItemDefine[verMin=").append(verticalMinimum);
		sb.append(",verMax=").append(verticalMaximum);
		sb.append(",inpMin=").append(verticalInputMinimum);
		sb.append(",inpMax=").append(verticalInputMaximum);
//		sb.append(",dataProviderName=").append(dataProviderName);
		sb.append(",dataHolderName=").append(dataHolderName);
		sb.append(",pointNo=").append(pointNo);
		sb.append(",pointUnit=").append(pointUnit);
		sb.append(",pointName=").append(pointName);
		sb.append(",pointUnitMark=").append(pointUnitMark);
		sb.append("]");
		return sb.toString();
	}

	/**
	 * このオブジェクトの値を比べ、同じならば true を返します。
	 * @param obj 比較対象のオブジェクト
	 * @return このオブジェクトの値を比べ、同じならば true を返します。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof TaskItemDefine)) {
			return false;
		}

		TaskItemDefine td = (TaskItemDefine) obj;
		return verticalMinimum == td.verticalMinimum
			&& verticalMaximum == td.verticalMaximum
			&& verticalInputMinimum == td.verticalInputMinimum
			&& verticalInputMaximum == td.verticalInputMaximum
//			&& dataProviderName.equals(td.dataProviderName)
			&& dataHolderName.equals(td.dataHolderName)
			&& pointNo == td.pointNo
			&& pointUnit.equals(td.pointUnit)
			&& pointName.equals(td.pointName)
			&& pointUnitMark.equals(td.pointUnitMark);
	}

	/**
	 * このオブジェクトのハッシュを返します
	 * @return このオブジェクトのハッシュ
	 */
	public int hashCode() {
		int result = 17;
		result = (int) (37 * result + verticalMinimum);
		result = (int) (37 * result + verticalMaximum);
		result = (int) (37 * result + verticalInputMinimum);
		result = (int) (37 * result + verticalInputMaximum);
//		result = 37 * result + dataProviderName.hashCode();
		result = 37 * result + dataHolderName.hashCode();
		result = 37 * result + pointNo;
		result = 37 * result + pointUnit.hashCode();
		result = 37 * result + pointName.hashCode();
		result = 37 * result + pointUnitMark.hashCode();
		return result;
	}

	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new TaskItemDefine(
			verticalMinimum,
			verticalMaximum,
			verticalInputMinimum,
			verticalInputMaximum,
//			dataProviderName,
			dataHolderName,
			pointNo,
			pointUnit,
			pointName,
			pointUnitMark);
	}

}
