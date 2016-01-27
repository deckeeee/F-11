/*
 * Projrct    F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All
 * Rights Reserved.
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * タイムスタンプデータクラスです。
 * このクラスは不変クラスでかつ、new演算子でインスタンスを生成することができません。
 * valueOf～メソッドを使用してインスタンスを作成してください。
 * @author hori
 */
public final class WifeDataTimestamp implements WifeData, Serializable {
	static final long serialVersionUID = 933696283955624139L;

	private transient static final int TIMESTAMP_WORD_COUNT = 8;
	/** 曜日番号変換テーブル */
	private transient static final Map WEEK2NO;
	static {
		WEEK2NO = new HashMap();
		WEEK2NO.put(new Integer(Calendar.SUNDAY), new Integer(0));
		WEEK2NO.put(new Integer(Calendar.MONDAY), new Integer(1));
		WEEK2NO.put(new Integer(Calendar.TUESDAY), new Integer(2));
		WEEK2NO.put(new Integer(Calendar.WEDNESDAY), new Integer(3));
		WEEK2NO.put(new Integer(Calendar.THURSDAY), new Integer(4));
		WEEK2NO.put(new Integer(Calendar.FRIDAY), new Integer(5));
		WEEK2NO.put(new Integer(Calendar.SATURDAY), new Integer(6));
	}
	/** Calendar 型の value を表す。シリアライズしない */
	private transient final Calendar calendarValue;
	/** このオブジェクトが保持する値 */
	private long value;

	/**
	 * コンストラクタ
	 */
	private WifeDataTimestamp(int year, int month, int date, int hour, int minute, int second) {
		this.calendarValue = new GregorianCalendar(year, month, date, hour, minute, second);
		this.value = calendarValue.getTimeInMillis();
	}
	private WifeDataTimestamp(long millis) {
		this.calendarValue = new GregorianCalendar();
		this.calendarValue.setTimeInMillis(millis);
		this.value = calendarValue.getTimeInMillis();
	}

	/**
	 * このデータのワード長を返します。
	 * @return このデータのワード長
	 */
	public int getWordSize() {
		return TIMESTAMP_WORD_COUNT;
	}

	/**
	 * このデータの値をバイト配列変換して返します。
	 * @return バイト配列
	 */
	public byte[] toByteArray() {
		byte[] ret = new byte[TIMESTAMP_WORD_COUNT * 2];
		Arrays.fill(ret, (byte) 0x00);
		int pos = 0;
		byte[] b = WifeBCD.valueOf(calendarValue.get(Calendar.YEAR));
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		b = WifeBCD.valueOf(calendarValue.get(Calendar.MONTH) + 1);
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		b = WifeBCD.valueOf(calendarValue.get(Calendar.DAY_OF_MONTH));
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		Integer week = new Integer(calendarValue.get(Calendar.DAY_OF_WEEK));
		b = WifeBCD.valueOf(((Integer) WEEK2NO.get(week)).intValue());
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		b = WifeBCD.valueOf(calendarValue.get(Calendar.HOUR_OF_DAY));
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		b = WifeBCD.valueOf(calendarValue.get(Calendar.MINUTE));
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		b = WifeBCD.valueOf(calendarValue.get(Calendar.SECOND));
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		ret[pos++] = 0x00;
		ret[pos++] = 0x01;
		return ret;
	}

	/**
	 * バイト配列をデータに変換します。
	 * @param b バイト配列
	 */
	public WifeData valueOf(byte[] b) {
		int[] paras = new int[TIMESTAMP_WORD_COUNT];
		byte[] item = new byte[2];
		for (int i = 0; i < TIMESTAMP_WORD_COUNT - 1; i++) {
			System.arraycopy(b, i * 2, item, 0, item.length);
			paras[i] = (int) WifeBCD.valueOf(item);
		}

		return new WifeDataTimestamp(
			paras[0],
			paras[1] - 1,
			paras[2],
			paras[4],
			paras[5],
			paras[6]);
	}

	public static WifeDataTimestamp valueOfType1(long millis) {
		return new WifeDataTimestamp(millis);
	}

	public Calendar calendarValue() {
		Calendar retcal = new GregorianCalendar();
		retcal.setTimeInMillis(this.value);
		return retcal;
	}

	/**
	 * このオブジェクトと指定されたオブジェクトを比較します。値に関してはCalendarクラスのequalsメソッドの規則が
	 * あてはまります。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WifeDataTimestamp)) {
			return false;
		}
		WifeDataTimestamp wd = (WifeDataTimestamp) obj;
		return wd.value == value;
	}

	/**
	 * このオブジェクトのハッシュコードを返します。
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + (int) value;
		return result;
	}

	/**
	 * オブジェクトの文字列表現を返します。
	 * {アナログデータ;アナログデータの形式}の書式で表示されます。
	 * 尚、この表示形式は将来変更される可能性があります。
	 */
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd EEE HH:mm:ss.SSS");
		return "{" + format.format(calendarValue.getTime()) + "}";
	}

	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new WifeDataTimestamp(value);
	}
}
