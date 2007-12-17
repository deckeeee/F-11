/*
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
import java.util.Arrays;

/**
 * 文字列データクラス。0x00～0x19のバイト値は、表示する時に終端子として扱う。 0x30, 0x31, 0x00, 0x00
 * というバイト配列の場合、"01"という文字列を表示する。
 * 
 * @author maekawa
 * 
 */
public final class StringData implements WifeData, Serializable {
	private static final long serialVersionUID = 6120279582664201908L;
	/** 文字列データのバイト配列 */
	private final byte[] stringByte;
	/** このインスタンスのハッシュコードです */
	private transient volatile int hashCode;

	/**
	 * プライベートコンストラクタ。このクラスのインスタンスを生成するには、{@link #valueOf()}か{@link #valueOf(String)}を使用してください。
	 * 
	 * @param stringByte
	 */
	private StringData(byte[] stringByte) {
		if (null == stringByte) {
			this.stringByte = new byte[0];
		} else {
			this.stringByte = new byte[stringByte.length];
			System.arraycopy(
					stringByte,
					0,
					this.stringByte,
					0,
					stringByte.length);
		}
	}

	public int getWordSize() {
		return stringByte.length / 2;
	}

	public byte[] toByteArray() {
		byte[] ba = new byte[stringByte.length];
		System.arraycopy(stringByte, 0, ba, 0, stringByte.length);
		return ba;
	}

	public WifeData valueOf(byte[] b) {
		return new StringData(b);
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof StringData)) {
			return false;
		}
		StringData sd = (StringData) obj;

		return Arrays.equals(stringByte, sd.stringByte);
	}

	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			for (int i = 0; i < stringByte.length; i++) {
				result = 37 * result + (int) stringByte[i];
			}
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * バイト配列で表現される20～7Eまでの文字列データを返します。
	 */
	public String toString() {
		int i = 0;
		for (; i < stringByte.length && stringByte[i] >= (byte) 0x20
				&& stringByte[i] < (byte) 0x7F; i++) {
		}
		byte[] dispStr = new byte[i];
		System.arraycopy(stringByte, 0, dispStr, 0, dispStr.length);
		return new String(dispStr);
	}

	/**
	 * 引数の文字列でインスタンスを生成します。nullを入力した場合には空文字列のインスタンスを生成します。
	 * 
	 * @param s 文字列
	 * @return 引数の文字列でインスタンスを生成します。nullを入力した場合には空文字列のインスタンスを生成します。
	 */
	static StringData valueOf(String s) {
		if (null == s) {
			return new StringData(new byte[0]);
		} else {
			return new StringData(s.getBytes());
		}
	}

	/**
	 * 空文字列のインスタンスを生成します。
	 * 
	 * @return 空文字列のインスタンスを生成します。
	 */
	static StringData valueOf() {
		return new StringData(new byte[0]);
	}

	/**
	 * 0x00で初期化したsizeワードの文字列値を生成します。
	 * 
	 * @param size 文字列長(ワード)
	 * @return 0x00で初期化したsizeワードの文字列値を生成します。
	 */
	public static StringData valueOf(int size) {
		byte[] data = new byte[size * 2];
		Arrays.fill(data, (byte) 0x00);
		return new StringData(data);
	}

	/**
	 * 防御的readResolveメソッド。 不正にデシリアライズされるのを防止します。
	 * 
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new StringData(stringByte);
	}
}
