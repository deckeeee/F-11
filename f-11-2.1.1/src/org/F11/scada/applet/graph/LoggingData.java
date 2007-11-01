package org.F11.scada.applet.graph;

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

import java.sql.Timestamp;

import org.F11.scada.data.LoggingRowData;
import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.collections.primitives.DoubleIterator;
import org.apache.commons.collections.primitives.DoubleList;

/**
 * ロギングデータを表すクラスです。
 */
public class LoggingData implements DoubleIterator, LoggingRowData, java.io.Serializable {
	/** serial version UID */
	private static final long serialVersionUID = 1439365531437150528L;
	/**
	 * タイムスタンプ
	 * @serial このレコードのタイムスタンプ
	 */
	private final Timestamp timestamp;
	/**
	 * シリーズデータリスト
	 * @serial このレコードのシリーズデータリスト
	 */
	private final DoubleList seriesList;
	/** シリーズデータ反復子 */
	private transient DoubleIterator seriesIterator;

	/**
	 * コンストラクタ
	 * @param timestamp タイムスタンプ
	 * @param seriesList シリーズデータリスト
	 */
	public LoggingData (Timestamp timestamp, DoubleList seriesList) {
		this.timestamp = timestamp;
		this.seriesList = seriesList;
	}

	/**
	 * レコードのタイムスタンプを返します
	 * @return レコードのタイムスタンプ
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * このレコードの指定列値を返します。
	 * @param column 指定列
	 * @return このレコードの指定列値
	 */
	public double getDouble(int column) {
		return seriesList.get(column);
	}

	/**
	 * シリーズデータを返し、ポインタを次に進めます。
	 * @return シリーズデータのインスタンス。
	 */
	public double next() {
		createIterator();
		return seriesIterator.next();
	}

	/**
	 * 次のシリーズデータが存在すれば、true を返します。
	 * @return 次のシリーズデータが存在すれば、true を存在しなければ false を返します。
	 */
	public boolean hasNext() {
		createIterator();
		return seriesIterator.hasNext();
	}

	/**
	 * シリーズデータリストのポインタを最初に設定します。
	 */
	public void first() {
		seriesIterator = seriesList.iterator();
	}

	/**
	 * このメソッドはサポートしていません。
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private void createIterator() {
		if (seriesIterator == null) {
			seriesIterator = seriesList.iterator();
		}
	}
	
	/**
	 * このオブジェクトの文字列表現を返します。
	 */
	public String toString() {
		return "timestamp=" + timestamp + ", seriesList=" + seriesList;
	}

	/**
	 * このレコードのデータのリストを返します。
	 * @return このレコードのデータのリストを返します。
	 */
	public DoubleList getList() {
		return new ArrayDoubleList(seriesList);
	}

}
