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
import java.util.LinkedList;
import java.util.List;

/**
 * @author hori
 */
public class TwoDaysSchedulePattern implements SchedulePattern, Serializable {
	static final long serialVersionUID = -4465547942929089419L;
	/** 今日を表すインデックスの定数です。 */
	public static final int TODAY = 0;
	/** 明日を表すインデックスの定数です。 */
	public static final int TOMORROW = 1;
	/** 項目のインデックスを定義。 */
	private final List indexs;
	/** このインスタンスのハッシュコードです */
	private transient volatile int hashCode;

	/**
	 * コンストラクタ
	 */
	public TwoDaysSchedulePattern() {
		indexs = new LinkedList();
		init();
	}

	private void init() {
		indexs.add(SchedulePatternItem.TODAY);
		indexs.add(SchedulePatternItem.TOMORROW);
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
		throw new UnsupportedOperationException("Argument = " + n);
	}

	private void checkIndex(int n) {
		if (n < 0 || n >= indexs.size())
			throw new IllegalArgumentException("Argument = " + n);
	}

	public int getTopSize() {
		return 2;
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
	 * このオブジェクトと他のオブジェクトが等しいかどうかを示します。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof TwoDaysSchedulePattern)) {
			return false;
		}

		TwoDaysSchedulePattern sp = (TwoDaysSchedulePattern)obj;

		return this.indexs.equals(sp.indexs);
	}

	/**
	 * このオブジェクトのハッシュコードを返します。
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			result = 37 * result + indexs.hashCode();
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * 特殊日以外を表すタイプセーフemunクラスです。
	 * 例）
	 * @todo 定義方法を外部ファイルで外に出す？
	 */
	private final static class SchedulePatternItem implements Serializable {
		static final long serialVersionUID = -5213928260544704641L;
		static final SchedulePatternItem TODAY = new SchedulePatternItem(0, "今日");
		static final SchedulePatternItem TOMORROW = new SchedulePatternItem(1, "明日");

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
			return indexName;
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
