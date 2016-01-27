/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataDigital.java,v 1.2.6.2 2005/04/18 09:48:59 frdm Exp $
 * $Revision: 1.2.6.2 $
 * $Date: 2005/04/18 09:48:59 $
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
import java.math.BigInteger;

/**
 * デジタル定義を表すクラスです。
 * 値を得る場合にvalueOfメソッドを使用してください。new演算子を使用したインスタンス作成はできません。
 * @see #valueOf(boolean bit, int bitNo)
 */
public final class WifeDataDigital implements WifeData, Serializable {
	private static final long serialVersionUID = 3067843484717072714L;
	/**
	 * デジタルビット
	 */
	private final boolean bit;
	/**
	 * ビット番号
	 */
	private final int bitNo;

	/** ビット位置に対するワードデータ（byte配列） */
	private static final byte[][] bitNoToByte = {
		{(byte)0x00, (byte)0x01}, {(byte)0x00, (byte)0x02}, {(byte)0x00, (byte)0x04}, {(byte)0x00, (byte)0x08},
		{(byte)0x00, (byte)0x10}, {(byte)0x00, (byte)0x20}, {(byte)0x00, (byte)0x40}, {(byte)0x00, (byte)0x80},
		{(byte)0x01, (byte)0x00}, {(byte)0x02, (byte)0x00}, {(byte)0x04, (byte)0x00}, {(byte)0x08, (byte)0x00},
		{(byte)0x10, (byte)0x00}, {(byte)0x20, (byte)0x00}, {(byte)0x40, (byte)0x00}, {(byte)0x80, (byte)0x00},
	};

	/** 予めインスタンスを作成しておく。（３２種類しか必要ない為） */
	private static final int BIT_PER_WORD = 16;
	private static final WifeDataDigital[][] DIGITAL = new WifeDataDigital[2][BIT_PER_WORD];
	static {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < BIT_PER_WORD; j++) {
				if (i == 0) {
					DIGITAL[i][j] = new WifeDataDigital(false, j);
				} else {
					DIGITAL[i][j] = new WifeDataDigital(true, j);
				}
			}
		}

	}
	private static final byte[] DIGITAL_ALL_ZERO = {(byte)0x00, (byte)0x00};

	/**
	 * コンストラクタ
	 * デジタルビットクラスを作成します。
	 * @param bitNo このデジタルビットのワードに対するビット位置を指定します。
	 */
	private WifeDataDigital(boolean bit, int bitNo) {
		if (bitNo < 0 || bitNo > 15) {
			throw new IllegalArgumentException("Illegal WifeDataDigital : Digital bit " + bitNo);
		}
		this.bitNo = bitNo;
		this.bit = bit;
	}

	/**
	 * デジタルビットがONの場合、自分のビット列のみをＯＮにしたbyte配列を返します。
	 * デジタルビットがOFFの場合、値が 0 のbyte配列を返します。
	 */
	public byte[] toByteArray() {
		return bit ? bitNoToByte[bitNo] : DIGITAL_ALL_ZERO;
	}

	/**
	 * WifeDataDigitalの文字列表現を返します。
	 * デジタルビットがON場合、ビットNoの10進表現を返します。
	 * デジタルビットがOFF場合、"false"を返します。
	 */
	public String toString() {
		return bit ? String.valueOf(bitNo) : "false";
	}

	/**
	 * このWifeDataDigitalのOnOff状態と、パラメータの値とを比較した結果を返します。
	 * @param onOff 比較したい値
	 * @return パラメータの値と等しければ true を返します。
	 */
	public boolean isOnOff(boolean onOff) {
		if (bit == onOff) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 指定した値を持ったWifeDataDigitalを返します。
	 * @param bit true - on : false - off
	 * @param bitNo WORD長に対するbit番号
	 */
	public static WifeDataDigital valueOf(boolean bit, int bitNo) {
		return bit ? DIGITAL[1][bitNo] : DIGITAL[0][bitNo];
	}

	/**
	 * 指定したビット番号がOffのWifeDataDigitalを返します。
	 */
	public static WifeDataDigital valueOfFalse(int bitNo) {
		return valueOf(false, bitNo);
	}

	/**
	 * 指定したビット番号がOnのWifeDataDigitalを返します。
	 */
	public static WifeDataDigital valueOfTrue(int bitNo) {
		return valueOf(true, bitNo);
	}

	/**
	 * このデジタルデータの引数のインスタンスを返します。
	 * @param b true or false
	 * @return このデジタルデータの引数のインスタンスを返します。
	 */
	public WifeDataDigital valueOf(boolean b) {
	    return b ? DIGITAL[1][bitNo] : DIGITAL[0][bitNo];
	}

	/**
	 * 指定した値を持ったWifeDataDigitalを返します。
	 * @param b バイト配列
	 */
	public WifeData valueOf(byte[] b) {
		BigInteger bi = new BigInteger(b);
		return bi.testBit(bitNo) ? DIGITAL[1][bitNo] : DIGITAL[0][bitNo];
	}

	/**
	 * このデータのワード長を返します。
	 * @return このデータのワード長
	 */
	public int getWordSize() {
		return 1;
	}

	/**
	 * デシリアライズの際生成したインスタンスを破棄してオリジナルを返す。
	 */
	private Object readResolve() throws ObjectStreamException {
		return bit ? DIGITAL[1][bitNo] : DIGITAL[0][bitNo];
	}
}
