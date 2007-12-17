/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/OnOffTime.java,v 1.2.6.2 2005/08/11 07:46:32 frdm Exp $
 * $Revision: 1.2.6.2 $
 * $Date: 2005/08/11 07:46:32 $
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
 *
 */
package org.F11.scada.data;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * タイムテーブルのOn / Off のデータのセットです。
 * スケジュールクラスの内部で使用します。
 * このクラスは不変クラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
final class OnOffTime implements Serializable {
	private static final long serialVersionUID = 734696440871603204L;
	/** On の時間を表すアナログデータです。 */
	private final WifeDataAnalog onTime;
	/** Off の時間を表すアナログデータです。 */
	private final WifeDataAnalog offTime;

	/**
	 * コンストラクタ
	 * @param onTime On の時間
	 * @param offTime Off の時間
	 */
	OnOffTime(int onTime, int offTime) {
		this.onTime = WifeDataAnalog.valueOfBcdSingle(onTime);
		this.offTime = WifeDataAnalog.valueOfBcdSingle(offTime);
	}

	/**
	 * On の時間を返します。
	 */
	int getOnTime() {
		return onTime.intValue();
	}

	/**
	 * Off の時間を返します。
	 */
	int getOffTime() {
		return offTime.intValue();
	}

	/**
	 * On の時間を反映した、OnOffTimeオブジェクトを返します。
	 */
	OnOffTime setOnTime(int onTime) {
		return new OnOffTime(onTime, this.offTime.intValue());
	}

	/**
	 * Off の時間を反映した、OnOffTimeオブジェクトを返します。
	 */
	OnOffTime setOffTime(int offTime) {
		return new OnOffTime(this.onTime.intValue(), offTime);
	}

	/**
	 * このオブジェクトの文字列表現を返します。
	 */
	public String toString() {
		java.text.DecimalFormat format = new java.text.DecimalFormat("0000");
		return format.format(onTime.intValue())
			+ ":"
			+ format.format(offTime.intValue());
	}

	/**
	 * このオブジェクトと他のオブジェクトが等しいかどうかを示します。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof OnOffTime)) {
			return false;
		}
		OnOffTime time = (OnOffTime) obj;
		return this.onTime.equals(time.onTime)
			&& this.offTime.equals(time.offTime);
	}

	/**
	 * このオブジェクトのハッシュコードを返します。
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + onTime.hashCode();
		result = 37 * result + offTime.hashCode();
		return result;
	}

	/**
	 * バイト配列をデータに変換します。
	 * <b>但し、 BCD 範囲外のバイト配列が、引数で指定された場合は無条件に 0 として扱います。</b>
	 * @param b バイト配列
	 */
	OnOffTime valueOf(byte[] b) {
		if (b == null) {
			throw new IllegalArgumentException("Argument need not null.");
		}

		if (b.length != 4) {
			throw new IllegalArgumentException("Illegal byte[] length:" + b.length);
		}
		
		byte[] onByteData = { b[0], b[1] };
		byte[] offByteData = { b[2], b[3] };

		WifeDataAnalog onTime = null;
		WifeDataAnalog offTime = null;
		try {
			onTime = (WifeDataAnalog) this.onTime.valueOf(onByteData);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			byte[] zero = new byte[] {(byte) 0x00, (byte) 0x00 };
			onTime = (WifeDataAnalog) this.onTime.valueOf(zero);
		}

		try {
			offTime = (WifeDataAnalog) this.onTime.valueOf(offByteData);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			byte[] zero = new byte[] {(byte) 0x00, (byte) 0x00 };
			offTime = (WifeDataAnalog) this.offTime.valueOf(zero);
		}

		return new OnOffTime(onTime.intValue(), offTime.intValue());
	}

	/**
	 * このデータの値をバイト配列変換して返します。
	 * @return バイト配列
	 */
	byte[] toByteArray() {
		byte[] onTimeByte = onTime.toByteArray();
		byte[] offTimeByte = offTime.toByteArray();
		byte[] returnByte = new byte[onTimeByte.length + offTimeByte.length];
		System.arraycopy(onTimeByte, 0, returnByte, 0, onTimeByte.length);
		System.arraycopy(
			offTimeByte,
			0,
			returnByte,
			onTimeByte.length,
			offTimeByte.length);
		return returnByte;
	}

	/**
	 * このデータのワード長を返します。
	 * @return このデータのワード長
	 */
	int getWordSize() {
		return onTime.getWordSize() + offTime.getWordSize();
	}

	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	Object readResolve() throws ObjectStreamException {
		return new OnOffTime(onTime.intValue(), offTime.intValue());
	}
}
