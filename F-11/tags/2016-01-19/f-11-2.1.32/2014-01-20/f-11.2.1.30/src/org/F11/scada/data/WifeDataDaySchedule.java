/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataDaySchedule.java,v 1.3.6.1 2005/07/06 02:20:44 frdm Exp $
 * $Revision: 1.3.6.1 $
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
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * 一定範囲のメモリエリアを指定したONOFF回数と日数で、管理するスケジュールデータクラスです。
 * スケジュールデータを取得・設定するのに使用します。
 */
public final class WifeDataDaySchedule implements WifeData, Serializable {
	private static final long serialVersionUID = -1239012272356993819L;
	/** On/Off のタイムテーブルです */
	private final OnOffTime[] timeTable;
	/** このインスタンスのハッシュコードです */
	private transient volatile int hashCode;
	/** このインスタンスのワードサイズです */
	private transient volatile int wordSize;

	/**
	 * プライベート・コンストラクタ
	 * @param number On/Off スケジュールの回数
	 */
	private WifeDataDaySchedule(int number) {
		timeTable = new OnOffTime[number];
		Arrays.fill(timeTable, new OnOffTime(0, 0));
	}

	/**
	 * プライベートコンストラクタ
	 * @param timeTable On/Off のタイムテーブルの配列
	 */
	private WifeDataDaySchedule(OnOffTime[] timeTable) {
		this.timeTable = timeTable;
	}

	/**
	 * このオブジェクトの文字列表現を返します。
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getClass().getName());
		buffer.append("{");
		for (int i = 0; i < timeTable.length; i++) {
			buffer.append(timeTable[i].toString());
			if (i != (timeTable.length - 1)) {
			    buffer.append(" ");
			}
		}
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * このオブジェクトと他のオブジェクトが等しいかどうかを示します。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WifeDataDaySchedule)) {
			return false;
		}
		WifeDataDaySchedule sc = (WifeDataDaySchedule)obj;

		if (!Arrays.equals(this.timeTable, sc.timeTable))
			return false;
		return true;
	}

	/**
	 * このオブジェクトのハッシュコードを返します。
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			for (int i = 0; i < timeTable.length; i++) {
				result = 37 * result + timeTable[i].hashCode();
			}
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * バイト配列をデータに変換します。
	 * @param b バイト配列
	 */
	public WifeData valueOf(byte[] b) {
		int byteSize = getWordSize() * 2;
		if (byteSize != b.length)
			throw new IllegalArgumentException("DataSize : " +  byteSize + "  Argument : " + b.length);

		OnOffTime[] timeTable =
			new OnOffTime[this.timeTable.length];

		ByteArrayInputStream is = new ByteArrayInputStream(b);
		for (int i = 0; i < timeTable.length; i++) {
			byte[] onOffByte = new byte[this.timeTable[i].getWordSize() * 2];
			is.read(onOffByte, 0, this.timeTable[i].getWordSize() * 2);
			timeTable[i] = this.timeTable[i].valueOf(onOffByte);
		}
		return new WifeDataDaySchedule(timeTable);
	}

	/**
	 * このデータの値をバイト配列変換して返します。
	 * @return バイト配列
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream(getWordSize() * 2);

		for (int i = 0; i < timeTable.length; i++) {
			try {
				os.write(timeTable[i].toByteArray());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return os.toByteArray();
	}

	/**
	 * このデータのワード長を返します。
	 * @return このデータのワード長
	 */
	public int getWordSize() {
		if (wordSize == 0) {
			int size = 0;
			for (int i = timeTable.length - 1; i >= 0; i--) {
				size += timeTable[i].getWordSize();
			}
			wordSize = size;
		}
		return wordSize;
	}

	/**
	 * WifeDataDaySchedule クラスのファクトリメソッドです。
	 * 初期化されたオブジェクトを返します。
	 * @param number On/Off スケジュールの回数
	 * @return WifeDataDaySchedule のインスタンス
	 */
	public static WifeDataDaySchedule valueOf(int number) {
		return new WifeDataDaySchedule(number);
	}

	/**
	 * 引数で指定されたスケジュールデータを返します。
	 */
	public int getOnTime(int number) {
		numberCheck(number);
		return timeTable[number].getOnTime();
	}

	/**
	 * 引数で指定されたスケジュールデータを返します。
	 */
	public int getOffTime(int number) {
		numberCheck(number);
		return timeTable[number].getOffTime();
	}

	/**
	 * 引数で指定されたスケジュールデータを設定した <code>WifeDataDaySchedule</code> を返します。
	 */
	public WifeDataDaySchedule setOnTime(int number, int time) {
		numberCheck(number);

		timeTable[number] =
			timeTable[number].setOnTime(time);
		return new WifeDataDaySchedule(this.timeTable);
	}

	/**
	 * 引数で指定されたスケジュールデータを設定した <code>WifeDataDaySchedule</code> を返します。
	 */
	public WifeDataDaySchedule setOffTime(int number, int time) {
		numberCheck(number);

		timeTable[number] =
			timeTable[number].setOffTime(time);
		return new WifeDataDaySchedule(this.timeTable);
	}

	/**
	 * 引数をチェックします。
	 */
	private void numberCheck(int number) {
		if (number < 0 || number >= timeTable.length) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("number = ").append(number);
			buffer.append(" [numberMax = ").append(timeTable.length).append("]");
			throw new IllegalArgumentException(buffer.toString());
		}
	}

	/**
	 * スケジュールデータ回数の総数を返します。
	 */
	public int getNumberSize() {
		return timeTable.length;
	}	

	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new  WifeDataDaySchedule(timeTable);
	}
}
