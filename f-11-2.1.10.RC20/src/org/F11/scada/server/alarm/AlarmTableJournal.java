/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/AlarmTableJournal.java,v 1.3 2003/02/21 05:08:55 frdm Exp $
 * $Revision: 1.3 $
 * $Date: 2003/02/21 05:08:55 $
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
package org.F11.scada.server.alarm;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

/**
 * クライアント・サーバー間の警報・状態テーブルモデル更新に使用する行データクラスです。
 * このクラスは不変クラスです。クラスの機能を内包する場合に、拡張ではなく委譲モデルを使用して下さい。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class AlarmTableJournal implements Serializable {
	private static final long serialVersionUID = -5727184066915388566L;
	/** 追加行データを表します。 */
	public static final int INSERT_OPERATION = 1;
	/** 削除行データを表します。 */
	public static final int REMOVE_OPERATION = 2;
	/** 更新行データを表します。 */
	public static final int MODIFY_OPERATION = 0;

	private final DataValueChangeEventKey key;
	private final int operationType;
	private final Object[] data;

	/**
	 * 行データを生成します。このオブジェクトは警報一覧のテーブルモデルを、クライアント・サーバー間で同期するために使用します。
	 * @param key 行のキーオブジェクト
	 * @param data テーブルモデルの行
	 * @param rowType 行操作種別
	 */	
	private AlarmTableJournal(DataValueChangeEventKey key, Object[] data, int rowType) {
		this.key = key;
		this.data = new Object[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length);
		this.operationType = rowType;
	}

	/**
	 * 追加行データを生成します。
	 * @param key 行のキーオブジェクト
	 * @param data テーブルモデルの行
	 * @return 追加行データ
	 */
	public static AlarmTableJournal createRowDataAddOpe(
			DataValueChangeEventKey key,
			Object[] data) {
		checkArgument(key, data);
		return new AlarmTableJournal(key, data, INSERT_OPERATION);
	}
	
	/**
	 * 削除行データを生成します。
	 * @param key 行のキーオブジェクト
	 * @param data テーブルモデルの行
	 * @return 削除行データ
	 */
	public static AlarmTableJournal createRowDataRemoveOpe(
			DataValueChangeEventKey key,
			Object[] data) {
		checkArgument(key, data);
		return new AlarmTableJournal(key, data, REMOVE_OPERATION);
	}

	/**
	 * 更新行データを生成します。
	 * @param key 行のキーオブジェクト
	 * @param data テーブルモデルの行
	 * @return 更新行データ
	 */
	public static AlarmTableJournal createRowDataModifyOpe(
			DataValueChangeEventKey key,
			Object[] data) {
		checkArgument(key, data);
		return new AlarmTableJournal(key, data, MODIFY_OPERATION);
	}
	
	private static void checkArgument(DataValueChangeEventKey key, Object[] data) {
		if (key == null) {
			throw new IllegalArgumentException("key is null.");
		}
		if (data == null) {
			throw new IllegalArgumentException("data is null.");
		}
	}

	/**
	 * テーブルモデルに使用する行データを返します。
	 * @return テーブルモデルに使用する行データ
	 */
	public Object[] getData() {
		Object[] rd = new Object[data.length];
		System.arraycopy(data, 0, rd, 0, data.length);
		return rd;
	}

	/**
	 * 行データの操作方法を返します。戻り値は以下の定数で返されます。
	 * <UL>
	 * <LI>ADD_OPERATION 追加行データ
	 * <LI>REMOVE_OPERATION 削除行データ
	 * <LI>MODIFY_OPERATION 更新行データ
	 * </UL>
	 * @return 行データの操作方法をADD_OPERATION, REMOVE_OPERATION, MODIFY_OPERATIONのいずれかの
	 * 定数で返します。
	 */
	public int getOperationType() {
		return operationType;
	}

	/**
	 * 行データのタイムスタンプを返します。
	 * @return 行データのタイムスタンプ
	 */	
	public Timestamp getTimestamp() {
		return key.getTimeStamp();
	}

	/**
	 * 行データのタイムスタンプを設定した AlarmTableJournal オブジェクトを返します。
	 * @param t 設定するタイムスタンプ
	 * @return AlarmTableJournal オブジェクト
	 */	
	public AlarmTableJournal setTimestamp(Timestamp t) {
		if (t == null) {
			throw new IllegalArgumentException("Set Time is null.");
		}
		
		return new AlarmTableJournal(key.setTimeStamp(t), data, operationType);
	}
	
	/**
	 * 行データのポイントを返します。
	 * @return 行データのポイント
	 */
	public int getPoint() {
		return key.getPoint();
	}
	
	/**
	 * 行データのデータプロバイダ名を返します。
	 * @return 行データのデータプロバイダ名
	 */
	public String getProvider() {
		return key.getProvider();
	}
	
	/**
	 * 行データのデータホルダ名を返します。
	 * @return 行データのデータホルダ名
	 */
	public String getHolder() {
		return key.getHolder();
	}
	
	/*
	 * このオブジェクトのタイムスタンプが、引数のタイムスタンプより大きければ true をそうでなければ false を返します。
	 * @param t タイムスタンプ
	 * @return このオブジェクトのタイムスタンプが、引数のタイムスタンプより大きければ true をそうでなければ false を返します。
	public boolean isSendData(Timestamp t) {
		Timestamp ts = key.getTimeStamp();
		return (ts.compareTo(t) > 0) ? true : false;
	}
	 */

	/**
	 * このオブジェクトの文字列表現を返します。
	 * @return このオブジェクトの文字列表現
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(key.toString());
		buffer.append(",operationType=" + operationType);
		buffer.append(",data=" + Arrays.asList(data).toString());
		return buffer.toString();
	}
	
	/**
	 * このオブジェクトの値を比べ、同じならば true を返します。
	 * @param obj 比較対象のオブジェクト
	 * @return このオブジェクトの値を比べ、同じならば true を返します。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {	
			return true;
		}
		
		if (!(obj instanceof AlarmTableJournal)) {
			return false;
		}
		
		AlarmTableJournal aj = (AlarmTableJournal) obj;
		return key.equals(aj.key)
				&& operationType == aj.operationType
				&& Arrays.equals(data, aj.data);
	}

	/**
	 * このオブジェクトのハッシュを返します
	 * @return このオブジェクトのハッシュ
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + key.hashCode();
		result = 37 * result + operationType;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				result = 37 * result + data[i].hashCode();
			}
		}
		return result;
	}

	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new AlarmTableJournal(key, data, operationType);
	}
}
