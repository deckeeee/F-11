/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/HolderData.java,v 1.7 2003/10/31 04:38:50 frdm Exp $
 * $Revision: 1.7 $
 * $Date: 2003/10/31 04:38:50 $
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * データホルダの値を生成する為の情報を保持するクラスです。
 * RMIでシリアライズしたDataHolderで通信するのは重い為このクラス経由で通信します。
 * このクラスは不変クラスです。
 * 
 * <p><b>注意：このクラスのhashCode()メソッド、equals(Object o)メソッドは、意図的に内部フィールド
 * valueを考慮していません。</b>
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class HolderData implements Serializable {
	private static final long serialVersionUID = 2668107215141257936L;
	/** データホルダー名 */
	private final String holder;
	/** データ値のバイト表現 */
	private final byte[] value;
	/** 最終更新日時 */
	private final long time;
	/** デマンドグラフデータ */
	private final Map demandData;
	
	/**
	 * コンストラクタ
	 * @param holder データホルダー名
	 * @param value データ値のbyte配列
	 * @param time 最終更新日時
	 * @param デマンドグラフデータ
	 */
	public HolderData(String holder, byte[] value, long time, Map demandData) {
		if (holder == null) {
			throw new IllegalArgumentException("holder need not null.");
		}
		if (value == null) {
			throw new IllegalArgumentException("value need not null.");
		}
		this.holder = holder;
		this.value = new byte[value.length];
		System.arraycopy(value, 0, this.value, 0, value.length);
		this.time = time;
		if (demandData != null) {
			this.demandData = new LinkedHashMap(demandData);
		} else {
			this.demandData = null;
		}
	}

	/**
	 * データホルダー名を返します。
	 * @return データホルダー名
	 */
	public String getHolder() {
		return holder;
	}

	/**
	 * データ値のbyte配列を返します。
	 * @return データ値のbyte配列
	 */
	public byte[] getValue() {
		byte[] b = new byte[value.length];
		System.arraycopy(value, 0, b, 0, value.length);
		return b;
	}

	/**
	 * データの更新日時を返します。
	 * @return long 更新日時
	 */
	public long getTime() {
		return time;
	}
	
	/**
	 * デマンドグラフデータを返します。
	 * @return デマンドグラフデータを返します。
	 */
	public Map getDemandData() {
		return demandData != null
			? Collections.unmodifiableMap(demandData)
			: null;
	}

	/**
	 * このオブジェクトと指定されたオブジェクトを比較します。
	 * このメソッドは意図的にホルダ名のみを比較するよう実装されています。
	 * @param obj 比較対象のオブジェクト
	 * @return ホルダ名が同様なら true を異なるなら false を返します
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof HolderData)) {
			return false;
		}
		HolderData hd = (HolderData)obj;
		return hd.holder.equals(holder);
	}

	/**
	 * このオブジェクトのハッシュコードを返します。
	 * このメソッドは意図的にホルダ名のみから、ハッシュを返すよう実装されています。
	 * 
	 * @return ホルダ名のハッシュを返します
	 */
	public int hashCode() {
		return holder.hashCode();
	}

	/**
	 * このオブジェクトの文字列表現を返します。
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new HolderData(holder, value, time, demandData);
	}

}
