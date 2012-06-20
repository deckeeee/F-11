/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/CreateHolderData.java,v 1.4.6.1 2004/11/29 07:12:47 frdm Exp $
 * $Revision: 1.4.6.1 $
 * $Date: 2004/11/29 07:12:47 $
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.gr.javacons.jim.QualityFlag;

/**
 * データホルダーを生成する為に必要な情報を保持するクラス。
 * WifeDataProviderProxyオブジェクト生成時に、サーバーからRMI経由で転送し
 * サーバーに保持されているDataHolderを再現します。
 * このクラスは不変クラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class CreateHolderData implements Serializable {
	private static final long serialVersionUID = 4735383098026717379L;
	private final String holderName;
	private final ConvertValue convertValue;
	private final Map demandData;
	private final WifeData wifeData;
	private final Date date;
	private final QualityFlag qualityFlag;
	private final String provider;

	/**
	 * コンストラクタ
	 * @param holder データホルダー名
	 * @param value データ値の byte配列
	 * @param convertValue 値変換オブジェクト
	 * @param demandData デマンドデータ
	 * @param wifeData 値オブジェクト
	 * @param date データ更新時刻
	 * @param qualityFlag クォリティフラグ
	 */
	public CreateHolderData(
			String holderName,
			WifeData wifeData,
			ConvertValue convertValue,
			Map demandData,
			Date date,
			QualityFlag qualityFlag,
			String provider) {

	    if (holderName == null) {
			throw new IllegalArgumentException("Argument `holderName' need not null.");
		}
		if (wifeData == null) {
			throw new IllegalArgumentException("Argument `wifeData' need not null.");
		}
	    if (provider == null) {
			throw new IllegalArgumentException("Argument `provider' need not null.");
		}
		this.holderName = holderName;
		this.wifeData = wifeData.valueOf(wifeData.toByteArray());
		this.convertValue = convertValue;
		if (demandData != null) {
			this.demandData = new HashMap(demandData);
		} else {
			this.demandData = null;
		}
		if (date == null) {
			throw new IllegalArgumentException("date is null.");
		}
		if (qualityFlag == null) {
			throw new IllegalArgumentException("qualityFlag is null.");
		}
		this.date = new Date(date.getTime());
		this.qualityFlag = qualityFlag;
		this.provider = provider;
	}

	/**
	 * データホルダー名を返します。
	 * @return データホルダー名 
	 */
	public String getHolder() {
		return holderName;
	}
	
	/**
	 * 値変換オブジェクトを返します。
	 * @return 値変換オブジェクト
	 */
	public ConvertValue getConvertValue() {
		return convertValue;
	}

	/**
	 * デマンドデータを返します。
	 * @return デマンドデータ
	 */
	public Map getDemandData() {
		if (demandData != null){
			return Collections.unmodifiableMap(demandData);
		} else {
			return Collections.unmodifiableMap(new HashMap());
		}
	}

	/**
	 * データ値を返します。
	 * @return データ値
	 */
	public WifeData getWifeData() {
		return wifeData.valueOf(wifeData.toByteArray());
	}
	
	/**
	 * このオブジェクトの文字列形式を返します。
	 * 返される文字列は、将来的に変更される可能性があります。開発中のデバッグ以外に
	 * 使用する事は推奨されません。
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("provider=").append(provider)
		.append(", holderName=").append(holderName)
		.append(", convertValue=").append(convertValue)
		.append(", demandData=").append(demandData)
		.append(", wifeData=").append(wifeData)
		.append(", date=").append(date)
		.append(", qualityFlag=").append(qualityFlag);

		return b.toString();
	}
	
	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new CreateHolderData(
				holderName,
				wifeData,
				convertValue,
				demandData,
				date,
				qualityFlag,
				provider);
	}

	/**
	 * データ更新時刻を返します。
	 * @return データ更新時刻
	 */
	public Date getDate() {
		return new Date(date.getTime());
	}

	/**
	 * クォリティフラグを返します。
	 * @return クォリティフラグ
	 */
	public QualityFlag getQualityFlag() {
		return qualityFlag;
	}

	public String getProvider() {
	    return provider;
	}
}
