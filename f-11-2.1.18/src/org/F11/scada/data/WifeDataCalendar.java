/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataCalendar.java,v 1.5.2.1 2005/07/06 02:20:44 frdm Exp $
 * $Revision: 1.5.2.1 $
 * $Date: 2005/07/06 02:20:44 $
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * カレンダーのデータを表します。
 */
public class WifeDataCalendar implements WifeData, java.io.Serializable {
	/** シリアルID */
	private static final long serialVersionUID = 3766236079110879046L;
	/** ワードデータを表す BigInteger インスタンス */
	private final BigInteger[][] bitSet;
	/** 設定されているモード数 */
	private final int modeCount;
	/** 最大ビット数 */
	private static final int MAX_BIT = 31;
	/** 最小ビット数 */
	private static final int MIN_BIT = 0;
	/** 一月分のバイト数 */
	private static final int MONTH_DATA_BYTE = 4;
	/** １年分の月の数 */
	private static final int MONTH_YEAR_COUNT = 12;
	/** このインスタンスのハッシュコードです */
	private transient volatile int hashCode;

	/**
	 * コンストラクタ
	 * private アクセスなので、new 演算子でインスタンス生成はできません。
	 * 代わりに valueOf() メソッドを使用して、インスタンス生成してください。
	 */
	private WifeDataCalendar(BigInteger[][] bitSet, int modeCount) {
		this.bitSet = bitSet;
		this.modeCount = modeCount;
	}

	/**
	 * 0 で初期化した WifeDataCalendar インスタンスを作成する、ファクトリメソッドです。
	 * @param modeCount 日の種類数を指定します。（通常休日 + 特殊日）
	 * @return 0 で初期化した WifeDataCalendar インスタンス
	 */
	static public WifeDataCalendar valueOf(int modeCount) {
		byte[] b = creataZeroByteArray(MONTH_DATA_BYTE);
		BigInteger[][] bi = new BigInteger[modeCount][MONTH_YEAR_COUNT];
		for (int j = modeCount - 1; j >= 0; j--) {
			for (int i = MONTH_YEAR_COUNT - 1; i >= 0; i--) {
				bi[j][i] = new BigInteger(1, b);
			}
		}
		return new WifeDataCalendar(bi, modeCount);
	}

	/**
	 * 初期化したバイトデータを生成します。
	 * @param size 生成するバイト配列のサイズ
	 * @return 0 で初期化されたバイト配列
	 */
	private static byte[] creataZeroByteArray(int size) {
		byte[] b = new byte[size];
		Arrays.fill(b, (byte)0x00);
		return b;
	}

	/**
	 * バイト配列をデータに変換します。
	 * @param b バイト配列
	 * @return 引数で初期化された、WordData インスタンス(WifeDataにダウンキャスト)
	 */
	public WifeData valueOf(byte[] b) {
		if (b.length != MONTH_DATA_BYTE * MONTH_YEAR_COUNT * modeCount)
			throw new IllegalArgumentException("IllegalArgument length = " + b.length);

		BigInteger[][] bi = new BigInteger[modeCount][MONTH_YEAR_COUNT];
		ByteArrayInputStream is = new ByteArrayInputStream(b);
		byte[] b1 = new byte[MONTH_DATA_BYTE];

		for (int i = 0; i < modeCount; i++) {
			for (int j = 0; j < MONTH_YEAR_COUNT; j++) {
				is.read(b1, 0, MONTH_DATA_BYTE);
				bi[i][j] = new BigInteger(1, b1);
			}
		}

		return new WifeDataCalendar(bi, modeCount);
	}

	/**
	 * このデータの値をバイト配列変換して返します。
	 * @return バイト配列
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream(MONTH_DATA_BYTE * MONTH_YEAR_COUNT * modeCount);

		for (int i = 0; i < modeCount; i++) {
			for (int j = 0; j < MONTH_YEAR_COUNT; j++) {
				byte[] b = bitSet[i][j].toByteArray();
				if (b.length <= MONTH_DATA_BYTE) {
					byte[] rb = creataZeroByteArray(MONTH_DATA_BYTE);
					System.arraycopy(b, 0, rb, rb.length - b.length, b.length);
					try {
						os.write(rb);
					} catch (java.io.IOException ex) {
						ex.printStackTrace();
					}
				} else {
					byte[] rb = creataZeroByteArray(MONTH_DATA_BYTE);
					System.arraycopy(b, b.length - rb.length, rb, 0, rb.length);
					try {
						os.write(rb);
					} catch (java.io.IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		return os.toByteArray();
	}

	/**
	 * 指定したビットを設定した WifeDataCalendar を返します。
	 * @param mode 設定モード
	 * @param month 月
	 * @param bitNumber 日
	 * @return ビットを設定した、WifeDataCalendar インスタンス。
	 */
	public WifeDataCalendar setBit(int mode, int month, int bitNumber) {
		if (isNormalArgument(mode, month, bitNumber)) {
			int n = reverse(bitNumber);
			for (int i = 0; i < bitSet.length; i++) {
				if (i == mode) {
					if (bitSet[i][month].testBit(n)) {
						bitSet[i][month] = bitSet[i][month].clearBit(n);
					} else {
						bitSet[i][month] = bitSet[i][month].setBit(n);
					}
				} else {
					bitSet[i][month] = bitSet[i][month].clearBit(n);
				}
			}
			return new WifeDataCalendar(bitSet, modeCount);
		} else {
			throw new java.lang.IllegalArgumentException("IllegalArgument mode:" +
														 mode +
														 " month:" +
														 month +
														 " bitNumber:" +
														 bitNumber
														 );
		}
	}

	/**
	 * 指定したビットをクリアした WifeDataCalendar を返します。
	 * @return ビットをクリアした、WifeDataCalendar インスタンス。
	public WifeDataCalendar clearBit(int mode, int month, int bitNumber) {
		if (isNormalArgument(mode, month, bitNumber)) {
			int n = reverse(bitNumber);
			bitSet[mode][month] = bitSet[mode][month].clearBit(n);
			return new WifeDataCalendar(bitSet, modeCount);
		} else {
			throw new java.lang.IllegalArgumentException("IllegalArgument mode:" +
														 mode +
														 " month:" +
														 month +
														 " bitNumber:" +
														 bitNumber
														 );
		}
	}
	 */

	/**
	 * 指定したビットをテストします。
	 * @param mode 設定モード
	 * @param month 月
	 * @param bitNumber 日
	 * @return 指定したビットが On なら true、Off なら false を返します。
	 */
	public boolean testBit(int mode, int month, int bitNumber) {
		if (isNormalArgument(mode, month, bitNumber)) {
			int n = reverse(bitNumber);
			return bitSet[mode][month].testBit(n);
		} else {
			throw new java.lang.IllegalArgumentException("IllegalArgument mode:" +
														 mode +
														 " month:" +
														 month +
														 " bitNumber:" +
														 bitNumber
														 );
		}
	}

	/**
	 * ビット演算の引数が正当かどうかを判定します。
	 * @param mode 設定モード
	 * @param month 月
	 * @param bitNumber 日
	 * @return 全ての引数が正常なら true、なければ false を返します。
	 */
	private boolean isNormalArgument(int mode, int month, int bitNumber) {
		if (mode < 0 || mode >= modeCount)
			return false;
		if (month < 0 || month >= MONTH_YEAR_COUNT)
			return false;
		if (bitNumber < MIN_BIT || bitNumber > MAX_BIT)
			return false;

		return true;
	}

	/**
	 * ビット位置を反転させます。
	 */
	private int reverse(int n) {
		return n < 16 ? 16 + n : n - 16;
	}

	/**
	 * オブジェクトと指定されたオブジェクトを比較します。
	 * 内部ビット表現の比較は、BigInteger クラスの equals(Object x) に依存しています。
	 */
	public boolean equals(Object x) {
		if (x == this) {
			return true;
		}
		if (!(x instanceof WifeDataCalendar)) {
			return false;
		}

		WifeDataCalendar wd = (WifeDataCalendar)x;
		if (wd.modeCount != this.modeCount)
			return false;

		for (int i = 0; i < bitSet.length; i++) {
			if (!Arrays.equals(bitSet[i], wd.bitSet[i]))
				return false;
		}
		return true;
	}

	/**
	 * ハッシュコード値を返します。
	 * 内部ビット表現のハッシュコード値は、BigInteger クラスの hashCode() に依存しています。
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			result = 37 * result + modeCount;
			for (int i = 0; i < bitSet.length; i++) {
				for (int j = 0; j < bitSet[i].length; j++) {
					result = 37 * result + bitSet[i][j].hashCode();
				}
			}
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * 文字列表現を返します。
	 * 内部ビットの文字列表現は、BigInteger クラスの toString(16) に依存しています。
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("modeCount:" + modeCount);
		buffer.append(",bit:");
		for (int i = 0; i < modeCount; i++) {
			for (int j = 0; j < MONTH_YEAR_COUNT; j++) {
				buffer.append(bitSet[i][j].toString(16).toUpperCase());
			}
			if (i != (modeCount - 1)) {
			    buffer.append(" ");
			}
		}
		return buffer.toString();
	}

	/**
	 * このデータのワード長を返します。
	 * @return このデータのワード長
	 */
	public int getWordSize() {
		return (MONTH_DATA_BYTE * MONTH_YEAR_COUNT * modeCount) / 2;
	}
	
	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new  WifeDataCalendar(bitSet, modeCount);
	}

	/**
	 * このカレンダーで使用する特殊日の数を返します。
	 * @return このカレンダーで使用する特殊日の数を返します。
	 */	
	public int getSpecialDayCount() {
		return modeCount - 1;
	}
}
