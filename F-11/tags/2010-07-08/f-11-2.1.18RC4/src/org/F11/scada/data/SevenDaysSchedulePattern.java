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
package org.F11.scada.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * @author hori
 */
public class SevenDaysSchedulePattern implements SchedulePattern, Serializable {
	private static final long serialVersionUID = -3603146153928950069L;
	/** 項目のインデックスを定義。 */
	private final SevenDaysPatternItem[] indexs;
	/** このインスタンスのハッシュコードです */
	private transient volatile int hashCode;

	/**
	 * コンストラクタ
	 */
	public SevenDaysSchedulePattern() {
		indexs = new SevenDaysPatternItem[SevenDaysPatternItem.PATTERN_SIZE];
		init();
	}

	private void init() {
		indexs[0] = SevenDaysPatternItem.DAY0;
		indexs[1] = SevenDaysPatternItem.DAY1;
		indexs[2] = SevenDaysPatternItem.DAY2;
		indexs[3] = SevenDaysPatternItem.DAY3;
		indexs[4] = SevenDaysPatternItem.DAY4;
		indexs[5] = SevenDaysPatternItem.DAY5;
		indexs[6] = SevenDaysPatternItem.DAY6;
	}

	/**
	 * このスケジュールパターンの総数を返します。
	 * @return スケジュールパターンの総数
	 */
	public int size() {
		return indexs.length;
	}

	/**
	 * 引数の項目にあたるインデックスを返します。
	 * @param n 項目の種類
	 * @return 引数の項目にあたるインデックス
	 */
	public int getDayIndex(int n) {
		checkIndex(n);
		return indexs[n].getIndex();
	}

	/**
	 * 引数の項目にあたるインデックス名を返します。
	 * @param n 項目の種類
	 * @return 引数の項目にあたるインデックス名
	 */
	public String getDayIndexName(int n) {
		checkIndex(n);
		return indexs[n].getIndexName();
	}

	public int getTopSize() {
		return SevenDaysPatternItem.PATTERN_SIZE;
	}

	/**
	 * 引数で指定された特殊日のインデックスを返します。
	 * @param 特殊日の種類番号(0 から始まります)
	 */
	public int getSpecialDayOfIndex(int n) {
		throw new UnsupportedOperationException("Argument = " + n);
	}

	private void checkIndex(int n) {
		if (n < 0 || n >= indexs.length)
			throw new IllegalArgumentException("Argument = " + n);
	}

	/**
	 * このオブジェクトと他のオブジェクトが等しいかどうかを示します。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SevenDaysSchedulePattern)) {
			return false;
		}
		SevenDaysSchedulePattern sp = (SevenDaysSchedulePattern) obj;
		return Arrays.equals(indexs, sp.indexs);
	}

	/**
	 * このオブジェクトのハッシュコードを返します。
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			for (int i = 0; i < indexs.length; i++) {
				result = 37 * result + indexs[i].hashCode();
			}
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * このオブジェクトの文字列表現を返します。
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("indexs=");
		buffer.append(indexs.toString());
		return buffer.toString();
	}



	/**
	 * 7日個別スケジュールのタイプセーフemunクラスです。
	 */
	private final static class SevenDaysPatternItem implements Serializable {
		private static final long serialVersionUID = 5729740520442131274L;
		static final int PATTERN_SIZE = 7;
		static final SevenDaysPatternItem DAY0 = new SevenDaysPatternItem(0);
		static final SevenDaysPatternItem DAY1 = new SevenDaysPatternItem(1);
		static final SevenDaysPatternItem DAY2 = new SevenDaysPatternItem(2);
		static final SevenDaysPatternItem DAY3 = new SevenDaysPatternItem(3);
		static final SevenDaysPatternItem DAY4 = new SevenDaysPatternItem(4);
		static final SevenDaysPatternItem DAY5 = new SevenDaysPatternItem(5);
		static final SevenDaysPatternItem DAY6 = new SevenDaysPatternItem(6);

		private final int index;

		SevenDaysPatternItem(int index) {
			this.index = index;
		}

		int getIndex() {
			return index;
		}

		String getIndexName() {
			SimpleDateFormat format = new SimpleDateFormat("M月d日(E)");
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, index);
			return format.format(calendar.getTime());
		}

		/**
		 * このオブジェクトと他のオブジェクトが等しいかどうかを示します。
		 */
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof SevenDaysPatternItem)) {
				return false;
			}
			SevenDaysPatternItem sp = (SevenDaysPatternItem) obj;
			return this.index == sp.index;
		}

		/**
		 * このオブジェクトのハッシュコードを返します。
		 */
		public int hashCode() {
			return 37 * 17 + index;
		}

		/**
		 * このオブジェクトの文字列表現を返します。
		 */
		public String toString() {
			return "index=" + index + ", indexName=" + getIndexName();
		}
	}
}
