/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataSchedule.java,v 1.4.2.6 2007/03/19 01:12:19 frdm Exp $
 * $Revision: 1.4.2.6 $
 * $Date: 2007/03/19 01:12:19 $
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

import org.F11.scada.Globals;

/**
 * 一定範囲のメモリエリアを指定したONOFF回数と日数で、管理するスケジュールデータクラスです。 スケジュールデータを取得・設定するのに使用します。
 * このクラスは不変クラスです。
 */
public final class WifeDataSchedule implements WifeData, Serializable {
	private static final long serialVersionUID = 9131723169279672709L;
	/** スケジュール項目パターンです */
	private final SchedulePattern pattern;
	/** On/Off のタイムテーブルです */
	private final OnOffTime[][] timeTable;
	/** このインスタンスのハッシュコードです */
	private transient volatile int hashCode;
	/** このインスタンスのワードサイズです */
	private transient volatile int wordSize;
	/** グループ名称 */
	private final String groupName;

	/**
	 * プライベート・コンストラクタ
	 * 
	 * @param pattern スケジュールパターン
	 * @param number On/Off スケジュールの回数
	 * @param groupName グループ名称
	 */
	private WifeDataSchedule(
			SchedulePattern pattern,
			int number,
			String groupName) {
		this.pattern = pattern;
		timeTable = new OnOffTime[this.pattern.size()][number];
		for (int i = timeTable.length - 1; i >= 0; i--) {
			Arrays.fill(timeTable[i], new OnOffTime(0, 0));
		}
		if (groupName != null) {
			this.groupName = groupName;
		} else {
			this.groupName = Globals.NULL_STRING;
		}
	}

	/**
	 * プライベートコンストラクタ
	 * 
	 * @param pattern スケジュールパターン
	 * @param timeTable On/Off のタイムテーブルの配列
	 * @param groupName グループ名称
	 */
	private WifeDataSchedule(
			SchedulePattern pattern,
			OnOffTime[][] timeTable,
			String groupName) {
		this.pattern = pattern;
		this.timeTable = new OnOffTime[timeTable.length][timeTable[0].length];
		for (int i = 0; i < timeTable.length; i++) {
			for (int j = 0; j < timeTable[i].length; j++) {
				this.timeTable[i][j] = timeTable[i][j];
			}
		}
		if (groupName != null) {
			this.groupName = groupName;
		} else {
			this.groupName = Globals.NULL_STRING;
		}
	}

	/**
	 * このオブジェクトの文字列表現を返します。
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getClass().getName());
		buffer.append("[patten=" + pattern.toString() + ",\ntimeTable:\n");
		for (int i = 0; i < timeTable.length; i++) {
			for (int j = 0; j < timeTable[i].length; j++) {
				buffer.append(timeTable[i][j].toString() + " ");
			}
			buffer.append("\n");
		}
		buffer.append(", groupName=").append(groupName);
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * このオブジェクトと他のオブジェクトが等しいかどうかを示します。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WifeDataSchedule)) {
			return false;
		}
		WifeDataSchedule sc = (WifeDataSchedule) obj;

		if (!this.pattern.equals(sc.pattern))
			return false;

		for (int i = 0; i < timeTable.length; i++) {
			if (!Arrays.equals(this.timeTable[i], sc.timeTable[i]))
				return false;
		}

		if (!this.groupName.equals(sc.groupName)) {
			return false;
		}

		return true;
	}

	/**
	 * このオブジェクトのハッシュコードを返します。
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			result = 37 * result + pattern.hashCode();
			for (int i = 0; i < timeTable.length; i++) {
				for (int j = 0; j < timeTable[i].length; j++) {
					result = 37 * result + timeTable[i][j].hashCode();
				}
			}
			result = 37 * result + groupName.hashCode();
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * バイト配列をデータに変換します。
	 * 
	 * @param b バイト配列
	 */
	public WifeData valueOf(byte[] b) {
		int byteSize = getWordSize() * 2;
		if (byteSize != b.length)
			throw new IllegalArgumentException("DataSize : " + byteSize
					+ "  Argument : " + b.length);

		OnOffTime[][] timeTable = new OnOffTime[this.timeTable.length][this.timeTable[0].length];

		ByteArrayInputStream is = new ByteArrayInputStream(b);
		for (int i = 0; i < timeTable.length; i++) {
			for (int j = 0; j < timeTable[i].length; j++) {
				byte[] onOffByte = new byte[this.timeTable[i][j].getWordSize() * 2];
				is.read(onOffByte, 0, this.timeTable[i][j].getWordSize() * 2);
				timeTable[i][j] = this.timeTable[i][j].valueOf(onOffByte);
			}
		}
		return new WifeDataSchedule(this.pattern, timeTable, this.groupName);
	}

	/**
	 * このデータの値をバイト配列変換して返します。
	 * 
	 * @return バイト配列
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream(getWordSize() * 2);

		for (int i = 0; i < timeTable.length; i++) {
			for (int j = 0; j < timeTable[i].length; j++) {
				try {
					os.write(timeTable[i][j].toByteArray());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return os.toByteArray();
	}

	/**
	 * このデータのワード長を返します。
	 * 
	 * @return このデータのワード長
	 */
	public int getWordSize() {
		if (wordSize == 0) {
			int size = 0;
			for (int i = timeTable.length - 1; i >= 0; i--) {
				for (int j = timeTable[i].length - 1; j >= 0; j--) {
					size += timeTable[i][j].getWordSize();
				}
			}
			wordSize = size;
		}
		return wordSize;
	}

	/**
	 * WifeDataSchedule クラスのファクトリメソッドです。 A&A 標準の項目パターンを使用した、初期化されたオブジェクトを返します。
	 * 
	 * @param special 特殊日の数
	 * @param number On/Off スケジュールの回数
	 * @return WifeDataSchedule のインスタンス
	 * @deprecated {@link #valueOf(int, int, String)}を使用して下さい。
	 */
	public static WifeDataSchedule valueOf(int special, int number) {
		return valueOf(special, number, null);
	}

	/**
	 * WifeDataSchedule クラスのファクトリメソッドです。 A&A 標準の項目パターンを使用した、初期化されたオブジェクトを返します。
	 * 
	 * @param special 特殊日の数
	 * @param number On/Off スケジュールの回数
	 * @param groupName グループ名称
	 * @return WifeDataSchedule のインスタンス
	 */
	public static WifeDataSchedule valueOf(
			int special,
			int number,
			String groupName) {
		return valueOf(new DefaultSchedulePattern(special), number, groupName);
	}

	/**
	 * WifeDataSchedule クラスのファクトリメソッドです。 引数で指定した項目パターンを使用して、初期化されたオブジェクトを返します。
	 * 
	 * @param pattern 項目パターン
	 * @param number On/Off スケジュールの回数
	 * @return WifeDataSchedule のインスタンス
	 */
	public static WifeDataSchedule valueOf(SchedulePattern pattern, int number) {
		return valueOf(pattern, number, null);
	}

	/**
	 * WifeDataSchedule クラスのファクトリメソッドです。 引数で指定した項目パターンを使用して、初期化されたオブジェクトを返します。
	 * 
	 * @param pattern 項目パターン
	 * @param number On/Off スケジュールの回数
	 * @param groupName グループ名称
	 * @return WifeDataSchedule のインスタンス
	 */
	public static WifeDataSchedule valueOf(
			SchedulePattern pattern,
			int number,
			String groupName) {
		return new WifeDataSchedule(pattern, number, groupName);
	}

	/**
	 * グループ名以外を複製したスケジュールオブジェクトを返します。
	 * 
	 * @param src グループ複製元を含んだスケジュールオブジェクト
	 * @return グループ名以外を複製したスケジュールオブジェクトを返します
	 */
	public WifeDataSchedule duplicate(WifeDataSchedule src) {
		return new WifeDataSchedule(pattern, timeTable, src.groupName);
	}

	/**
	 * 引数で指定されたスケジュールデータを返します。
	 */
	public int getOnTime(int weekOfDay, int number) {
		numberCheck(number);
		return timeTable[weekOfDay][number].getOnTime();
	}

	/**
	 * 引数で指定されたスケジュールデータを返します。
	 */
	public int getOffTime(int weekOfDay, int number) {
		numberCheck(number);
		return timeTable[weekOfDay][number].getOffTime();
	}

	/**
	 * 引数で指定されたスケジュールデータを設定した <code>WifeDataSchedule</code> を返します。
	 */
	public WifeDataSchedule setOnTime(int weekOfDay, int number, int time) {
		numberCheck(number);

		timeTable[weekOfDay][number] = timeTable[weekOfDay][number]
				.setOnTime(time);
		return new WifeDataSchedule(
				this.pattern,
				this.timeTable,
				this.groupName);
	}

	/**
	 * 引数で指定されたスケジュールデータを設定した <code>WifeDataSchedule</code> を返します。
	 */
	public WifeDataSchedule setOffTime(int weekOfDay, int number, int time) {
		numberCheck(number);

		timeTable[weekOfDay][number] = timeTable[weekOfDay][number]
				.setOffTime(time);
		return new WifeDataSchedule(
				this.pattern,
				this.timeTable,
				this.groupName);
	}

	/**
	 * 引数で指定されたグループ名を設定した <code>WifeDataSchedule</code> を返します。
	 * 
	 * @param groupName グループ名
	 * @version 2.0.21
	 */
	public WifeDataSchedule setGroupName(String groupName) {
		return new WifeDataSchedule(this.pattern, this.timeTable, groupName);
	}

	/**
	 * 引数をチェックします。
	 */
	private void numberCheck(int number) {
		if (number < 0 || number >= timeTable[0].length) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("number = ").append(number);
			buffer.append(" [numberMax = ").append(timeTable[0].length).append(
					"]");
			throw new IllegalArgumentException(buffer.toString());
		}
	}

	/**
	 * スケジュール項目の総数を返します。
	 */
	public int getPatternSize() {
		return pattern.size();
	}

	/**
	 * スケジュールデータ回数の総数を返します。
	 */
	public int getNumberSize() {
		return timeTable[0].length;
	}

	/**
	 * 引数で指定された特殊日のインデックスを返します。
	 * 
	 * @param 特殊日の種類番号
	 */
	public int getSpecialDayOfIndex(int n) {
		return pattern.getSpecialDayOfIndex(n);
	}

	/**
	 * 引数の項目(曜日)にあたるインデックス名を返します。
	 * 
	 * @param n 項目の種類
	 * @return 引数の項目にあたるインデックス名
	 */
	public String getDayIndexName(int n) {
		return pattern.getDayIndexName(n);
	}

	/**
	 * 引数の項目(曜日)にあたるインデックスを返します。
	 * 
	 * @param n 項目の種類(曜日)
	 * @return 引数の項目にあたるインデックス
	 */
	public int getDayIndex(int n) {
		return pattern.getDayIndex(n);
	}

	/**
	 * グループ名称をかえします。
	 * 
	 * @return グループ名称をかえします
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * 引数のオブジェクトのインデックス0からのデータをこのオブジェクトにコピーしそれを返します。
	 * 
	 * @param dest コピーするオブジェクト
	 * @return 引数のオブジェクトのインデックス0からのデータをこのオブジェクトにコピーしそれを返します。
	 */
	public WifeDataSchedule duplicateTodayAndTomorrow(WifeDataSchedule dest) {
		WifeDataSchedule on = duplicate(this);
		for (int i = 0; i < getPatternSize(); i++) {
			for (int j = 0; j < getNumberSize(); j++) {
				on = on.setOnTime(i, j, dest.getOnTime(i, j));
				on = on.setOffTime(i, j, dest.getOffTime(i, j));
			}
		}
		return on;
	}


	/**
	 * スケジュール画面で上方にくる日の個数(今日明日なら2, 未来7日なら7など)を返します。
	 * @return スケジュール画面で上方にくる日の個数(今日明日なら2, 未来7日なら7など)を返します。
	 */
	public int getTopSize() {
		return pattern.getTopSize();
	}

	/**
	 * 防御的readResolveメソッド。 不正にデシリアライズされるのを防止します。
	 * 
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new WifeDataSchedule(pattern, timeTable, groupName);
	}
}
