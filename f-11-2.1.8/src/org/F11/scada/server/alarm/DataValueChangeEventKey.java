/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/DataValueChangeEventKey.java,v 1.3.6.1 2006/03/07 09:33:56 frdm Exp $
 * $Revision: 1.3.6.1 $
 * $Date: 2006/03/07 09:33:56 $
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
import java.util.Date;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataValueChangeEvent;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * DataValueChangeEvent から各値を簡単にアクセスする為の補助クラスです。
 * このクラスはシリアライズすることが可能です。デジタルデータを含むデータ変更イベントは、
 * このクラスを介して通信します。
 * 
 * このクラスは不変クラスです。クラスの機能を含めたい時は、継承ではなく委譲モデルを使用して下さい。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class DataValueChangeEventKey implements Serializable {
	private static final long serialVersionUID = 299536944241600068L;
	/**
	 * ポイント
	 * @serial このデータが属するポイント
	 */ 
	private final int point;
	/**
	 * データプロバイダ名
	 * @serial データプロバイダ名
	 */
	private final String provider;
	/**
	 * データホルダー名
	 * @serial データホルダー名
	 */
	private final String holder;
	/**
	 * データ値
	 * @serial データ値
	 */
	private final Boolean value;
	/**
	 * データ変更時刻
	 * @serial データ変更時刻
	 */
	private final Timestamp date;

	/**
	 * WifeDataDigitalのデータ値変更イベントを各値に変換します。
	 * @param evt WifeDataDigitalのデータ値変更イベント
	 */
	public DataValueChangeEventKey(DataValueChangeEvent evt) {
		this(evt, true);
	}

	public DataValueChangeEventKey(DataValueChangeEvent evt, boolean digtalCheck) {
		DataHolder dh = (DataHolder) evt.getSource();
		Date date = evt.getTimeStamp();
		point =
			((Integer) dh.getParameter(WifeDataProvider.PARA_NAME_POINT))
				.intValue();
		provider = dh.getDataProvider().getDataProviderName();
		holder = dh.getDataHolderName();
		Object obj = evt.getValue();
		if (digtalCheck) {
			if (obj instanceof WifeDataDigital) {
				if (((WifeDataDigital) obj).toString().equals("false")) {
					value = Boolean.FALSE;
				} else {
					value = Boolean.TRUE;
				}
			} else {
				throw new IllegalArgumentException("DataHolder is not WifeDataDigital.");
			}
		} else {
			value = Boolean.FALSE;
		}
		this.date = new Timestamp(date.getTime());
	}

	public DataValueChangeEventKey(
			int point,
			String provider,
			String holder,
			Boolean value,
			Timestamp date) {
		this.point = point;
		this.provider = provider;
		this.holder = holder;
		this.value = value;
		this.date = new Timestamp(date.getTime());
	}

	/**
	 * ポイントを返します。
	 * @return int
	 */
	public int getPoint() {
		return point;
	}
	
	/**
	 * データプロバイダ名を返します。
	 * @return String
	 */
	public String getProvider() {
		return provider;
	}
	
	/**
	 * データホルダー名を返します。
	 * @return String
	 */
	public String getHolder() {
		return holder;
	}
	
	/**
	 * データ値を返します。
	 * @return Boolean
	 */
	public Boolean getValue() {
		return value;
	}
	
	/**
	 * データ変更時刻を返します。
	 * @return Timestamp
	 */
	public Timestamp getTimeStamp() {
		return new Timestamp(date.getTime());
	}
	
	/**
	 * データ変更時刻を設定した DataValueChangeEventKey オブジェクトを返します。
	 * @param t 設定時刻
	 * @return DataValueChangeEventKey
	 */
	public DataValueChangeEventKey setTimeStamp(Timestamp t) {
		if (t == null) {
			throw new IllegalArgumentException("Set Time is null.");
		}
		
		return new DataValueChangeEventKey(
			point, provider, holder, value, new Timestamp(t.getTime()));
	}

	/**
	 * このオブジェクトの文字列表現を返します。
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("point=").append(point);
		buffer.append(",provider=").append(provider);
		buffer.append(",holder=").append(holder);
		buffer.append(",value=").append(value);
		buffer.append(",date=").append(date);
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
		
		if (!(obj instanceof DataValueChangeEventKey)) {
			return false;
		}
		
		DataValueChangeEventKey dv = (DataValueChangeEventKey) obj;
		return point == dv.point
				&& provider.equals(dv.provider)
				&& holder.equals(dv.holder)
				&& value.equals(dv.value)
				&& date.equals(dv.date);
	}

	/**
	 * このオブジェクトのハッシュを返します
	 * @return このオブジェクトのハッシュ
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + point;
		result = 37 * result + provider.hashCode();
		result = 37 * result + holder.hashCode();
		result = 37 * result + value.hashCode();
		result = 37 * result + date.hashCode();
		return result;
	}

	
	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new DataValueChangeEventKey(point, provider, holder, value, date);
	}
}
