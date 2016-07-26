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
package org.F11.scada.data;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * アナログデータを４word連ねたデータクラスです。
 * このクラスは不変クラスでかつ、new演算子でインスタンスを生成することができません。
 * valueOf～メソッドを使用してインスタンスを作成してください。
 * @author hori
 */
public final class WifeDataAnalog4 implements WifeData, Serializable {
	static final long serialVersionUID = 6642498079225768096L;
	
	private transient static final int ANALOG4_SIZE = 4;
	/** アナログデータ値 */
	private final WifeDataAnalog[] value;

	/**
	 * コンストラクタ
	 */
	private WifeDataAnalog4(WifeDataAnalog[] value) {
		if (value.length != ANALOG4_SIZE)
			throw new IllegalArgumentException();
		this.value = value;
		/*
		for (int i = 0; i < ANALOG4_SIZE; i++) {
			this.value[i] = (WifeDataAnalog)value[i].valueOf(value[i].doubleValue());
		}
		*/
	}

	/**
	 * BCD 4ワード長で構成されるWifeDataAnalog4のインスタンスを返します。
	 * @param values WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog4 valueOfBcdSingle(double[] values) {
		if (values.length != ANALOG4_SIZE)
			throw new IllegalArgumentException();
		WifeDataAnalog[] ana = new WifeDataAnalog[ANALOG4_SIZE];
		for (int i = 0; i < ana.length; i++) {
			ana[i] = WifeDataAnalog.valueOfBcdSingle(values[i]);
		}
		return new WifeDataAnalog4(ana);
	}

	/**
	 * HEX 4ワード長で構成されるWifeDataAnalog4のインスタンスを返します。
	 * @param values WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog4 valueOfHexSingle(double[] values) {
		if (values.length != ANALOG4_SIZE)
			throw new IllegalArgumentException();
		WifeDataAnalog[] ana = new WifeDataAnalog[ANALOG4_SIZE];
		for (int i = 0; i < ana.length; i++) {
			ana[i] = WifeDataAnalog.valueOfHexSingle(values[i]);
		}
		return new WifeDataAnalog4(ana);
	}

	/**
	 * SHX 4ワード長で構成されるWifeDataAnalog4のインスタンスを返します。
	 * @param values WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog4 valueOfShxSingle(double[] values) {
		if (values.length != ANALOG4_SIZE)
			throw new IllegalArgumentException();
		WifeDataAnalog[] ana = new WifeDataAnalog[ANALOG4_SIZE];
		for (int i = 0; i < ana.length; i++) {
			ana[i] = WifeDataAnalog.valueOfShxSingle(values[i]);
		}
		return new WifeDataAnalog4(ana);
	}

	/**
	 * バイト配列をアナログ値に変換しWifeDataAnalog4を返します。
	 * アナログ値以外のフィールドは以前の値を保持します。
	 * @param b バイト配列
	 */
	public WifeData valueOf(byte[] b) {
		WifeDataAnalog[] ana = new WifeDataAnalog[value.length];
		int pos = 0;
		for (int i = 0; i < value.length; i++) {
			byte[] tb = value[i].toByteArray();
			System.arraycopy(b, pos, tb, 0, tb.length);
			ana[i] = (WifeDataAnalog) value[i].valueOf(tb);
			pos += tb.length;
		}
		return new WifeDataAnalog4(ana);
	}

	/**
	 * 引数のアナログ値を変換しWifeDataAnalog4を返します。
	 * アナログ値以外のフィールドは以前の値を保持します。
	 * @param values アナログ値の配列
	 */
	public WifeData valueOf(double[] values) {
		if (values.length != ANALOG4_SIZE)
			throw new IllegalArgumentException();
		WifeDataAnalog[] ana = new WifeDataAnalog[this.value.length];
		for (int i = 0; i < this.value.length; i++) {
			ana[i] = (WifeDataAnalog) this.value[i].valueOf(values[i]);
		}
		return new WifeDataAnalog4(ana);
	}

	/**
	 * このオブジェクトの値を doubleの配列型として返します。
	 * @return このオブジェクトが表す数値の double配列
	 */
	public double[] doubleValues() {
		double[] values = new double[value.length];
		for (int i = 0; i < value.length; i++) {
			values[i] = value[i].doubleValue();
		}
		return values;
	}

	/**
	 * このオブジェクトの指定インデックス値を double型として返します。
	 * @return このオブジェクトが表す数値の double配列
	 */
	public double doubleValue(int index) {
		return value[index].doubleValue();
	}

	/**
	 * このオブジェクトのワード長を返します。
	 * @return このアナログオブジェクトのワード長
	 */
	public int getWordSize() {
		int ws = 0;
		for (int i = 0; i < value.length; i++) {
			ws += value[i].getWordSize();
		}
		return ws;
	}

	/**
	 * このデータの値をバイト配列変換して返します。
	 * @return バイト配列
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream ost = new ByteArrayOutputStream();
		for (int i = 0; i < value.length; i++) {
			byte[] ba = value[i].toByteArray();
			ost.write(ba, 0, ba.length);
		}
		return ost.toByteArray();
	}

	/**
	 * このオブジェクトと指定されたオブジェクトを比較します。個々のアナログ値に関してはWifeDataAnalogクラスの
	 * equalsメソッドの規則があてはまります。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WifeDataAnalog4)) {
			return false;
		}
		WifeDataAnalog4 wd = (WifeDataAnalog4) obj;
		for (int i = 0; i < value.length; i++) {
			if (!wd.value[i].equals(value[i]))
				return false;
		}
		return true;
	}

	/**
	 * このオブジェクトのハッシュコードを返します。
	 */
	public int hashCode() {
		int result = 17;
		for (int i = 0; i < value.length; i++) {
			result += value[i].hashCode();
		}
		return result;
	}

	/**
	 * オブジェクトの文字列表現を返します。
	 * {アナログデータ;アナログデータの形式}の書式で表示されます。
	 * 尚、この表示形式は将来変更される可能性があります。
	 */
	public String toString() {
		StringBuffer st = new StringBuffer();
		st.append("WifeDataAnalog4:");
		for (int i = 0; i < value.length; i++) {
			st.append(value[i].toString());
		}
		return st.toString();
	}

}
