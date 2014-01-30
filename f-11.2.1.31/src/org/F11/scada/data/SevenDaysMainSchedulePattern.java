/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/Attic/SevenDaysMainSchedulePattern.java,v 1.1.2.1 2006/12/12 08:43:28 frdm Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2006/12/12 08:43:28 $
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * A&A 標準の項目パターンです。
 * このクラスは不変クラスです。
 */
public final class SevenDaysMainSchedulePattern implements SchedulePattern, Serializable {
	private static final long serialVersionUID = 5673887576894508771L;
	/** 項目のインデックスを定義。 */
	private final List indexs;
	/** 特殊日の数 */
	private final int specialDayCount;
	/** このインスタンスのハッシュコードです */
	private transient volatile int hashCode;

	/**
	 * コンストラクタ
	 * @param specialDayCount 特殊日の数
	 */
	public SevenDaysMainSchedulePattern(int specialDayCount) {
		this.specialDayCount = specialDayCount;
		indexs = new LinkedList();
		init();
	}

	private void init() {
		indexs.add(SchedulePatternItem.DAY0);
		indexs.add(SchedulePatternItem.DAY1);
		indexs.add(SchedulePatternItem.DAY2);
		indexs.add(SchedulePatternItem.DAY3);
		indexs.add(SchedulePatternItem.DAY4);
		indexs.add(SchedulePatternItem.DAY5);
		indexs.add(SchedulePatternItem.DAY6);
		indexs.add(SchedulePatternItem.SUNDAY);
		indexs.add(SchedulePatternItem.MONDAY);
		indexs.add(SchedulePatternItem.TUESDAY);
		indexs.add(SchedulePatternItem.WEDNESDAY);
		indexs.add(SchedulePatternItem.THURSDAY);
		indexs.add(SchedulePatternItem.FRIDAY);
		indexs.add(SchedulePatternItem.SATURDAY);
		indexs.add(SchedulePatternItem.HOLIDAY);
		for (int i = 0, j = indexs.size(); i < specialDayCount; i++) {
			indexs.add(SchedulePatternItem.valueOf(j++, "特殊日" + (i + 1)));
		}
	}

	/**
	 * このスケジュールパターンの総数を返します。
	 * @return スケジュールパターンの総数
	 */
	public int size() {
		return indexs.size();
	}

	/**
	 * 引数の項目にあたるインデックスを返します。
	 * @param n 項目の種類
	 * @return 引数の項目にあたるインデックス
	 */
	public int getDayIndex(int n) {
		checkIndex(n);
		return ((SchedulePatternItem)indexs.get(n)).getIndex();
	}

	/**
	 * 引数の項目にあたるインデックス名を返します。
	 * @param n 項目の種類
	 * @return 引数の項目にあたるインデックス名
	 */
	public String getDayIndexName(int n) {
		checkIndex(n);
		return ((SchedulePatternItem)indexs.get(n)).getIndexName();
	}

	/**
	 * 引数で指定された特殊日のインデックスを返します。
	 * @param 特殊日の種類番号(0 から始まります)
	 */
	public int getSpecialDayOfIndex(int n) {
		if (n < 0 || n >= specialDayCount)
			throw new IllegalArgumentException("Argument = " + n);
		return size() - specialDayCount + n;
	}

	private void checkIndex(int n) {
		if (n < 0 || n >= indexs.size())
			throw new IllegalArgumentException("Argument = " + n);
	}

	public int getTopSize() {
		return 7;
	}

	/**
	 * このオブジェクトの文字列表現を返します。
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("indexs=");
		buffer.append(indexs.toString());
		buffer.append("specialDayCount=" + specialDayCount);
		return buffer.toString();
	}

	/**
	 * このオブジェクトと他のオブジェクトが等しいかどうかを示します。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SevenDaysMainSchedulePattern)) {
			return false;
		}

		SevenDaysMainSchedulePattern sp = (SevenDaysMainSchedulePattern)obj;

		if (this.specialDayCount != sp.specialDayCount)
			return false;

		return this.indexs.equals(sp.indexs);
	}

	/**
	 * このオブジェクトのハッシュコードを返します。
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			result = 37 * result + specialDayCount;
			result = 37 * result + indexs.hashCode();
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * 特殊日以外を表すタイプセーフemunクラスです。
	 * 例）
	 * タイムテーブルを今日、明日、月、火、水、木、金、土、日、休日にする
	 * 各アイテムの index を 0, 1, 8, 2, 3, 4, 5, 6, 7, 9 にする。
	 * @todo 定義方法を外部ファイルで外に出す？
	 */
	private final static class SchedulePatternItem implements Serializable {
		private static final long serialVersionUID = 5544441232413765284L;
		private static final String NON_STRING = "org.F11.scada.data.NON_STRING";

		static final SchedulePatternItem DAY0 = new SchedulePatternItem(0, NON_STRING);
		static final SchedulePatternItem DAY1 = new SchedulePatternItem(1, NON_STRING);
		static final SchedulePatternItem DAY2 = new SchedulePatternItem(2, NON_STRING);
		static final SchedulePatternItem DAY3 = new SchedulePatternItem(3, NON_STRING);
		static final SchedulePatternItem DAY4 = new SchedulePatternItem(4, NON_STRING);
		static final SchedulePatternItem DAY5 = new SchedulePatternItem(5, NON_STRING);
		static final SchedulePatternItem DAY6 = new SchedulePatternItem(6, NON_STRING);
		static final SchedulePatternItem SUNDAY = new SchedulePatternItem(7, "日曜");
		static final SchedulePatternItem MONDAY = new SchedulePatternItem(8, "月曜");
		static final SchedulePatternItem TUESDAY = new SchedulePatternItem(9, "火曜");
		static final SchedulePatternItem WEDNESDAY = new SchedulePatternItem(10, "水曜");
		static final SchedulePatternItem THURSDAY = new SchedulePatternItem(11, "木曜");
		static final SchedulePatternItem FRIDAY = new SchedulePatternItem(12, "金曜");
		static final SchedulePatternItem SATURDAY = new SchedulePatternItem(13, "土曜");
		static final SchedulePatternItem HOLIDAY = new SchedulePatternItem(14, "休日");

		private final int index;
		private final String indexName;

		private SchedulePatternItem(int index, String indexName) {
			this.index = index;
			this.indexName = indexName;
		}

		int getIndex() {
			return index;
		}

		String getIndexName() {
			if (NON_STRING.equals(indexName)) {
				SimpleDateFormat format = new SimpleDateFormat("M月d日(E)");
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, index);
				return format.format(calendar.getTime());
			} else {
				return indexName;
			}
		}

		static SchedulePatternItem valueOf(int index, String indexName) {
			return new SchedulePatternItem(index, indexName);
		}

		/**
		 * このオブジェクトの文字列表現を返します。
		 */
		public String toString() {
			return "index=" + index + ",indexName=" + indexName;
		}

		/**
		 * このオブジェクトと他のオブジェクトが等しいかどうかを示します。
		 */
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof SchedulePatternItem)) {
				return false;
			}

			SchedulePatternItem sp = (SchedulePatternItem)obj;

			return this.index == sp.index && this.indexName.equals(sp.indexName);
		}

		/**
		 * このオブジェクトのハッシュコードを返します。
		 */
		public int hashCode() {
			int result = 17;
			result = 37 * result + index;
			result = 37 * result + indexName.hashCode();
			return result;
		}
	}
}
