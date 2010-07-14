/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeQualityFlag.java,v 1.3.6.1 2004/11/29 07:12:47 frdm Exp $
 * $Revision: 1.3.6.1 $
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

import jp.gr.javacons.jim.QualityFlag;

/**
 * WifeData の QualityFlag クラスです。
 * タイプセーフ enum クラス・パターンを使用して、QualityFlagインターフェイスを
 * ラップしています。
 * このクラスは不変クラスです。
 */
public final class WifeQualityFlag implements QualityFlag, Serializable {
	/** シリアルバージョンUIDです。 */
	private static final long serialVersionUID = 7268587145123423407L;
	/** クォリティに関するトップレベルの情報として，異常値であることを表す。 */
	public static final WifeQualityFlag BAD = new WifeQualityFlag(QualityFlag.BAD);
	/** クォリティに関するトップレベルの情報として，正常か異常かを判定できないことを表す。 */
	public static final WifeQualityFlag UNCERTAIN = new WifeQualityFlag(QualityFlag.UNCERTAIN);
	/** クォリティに関するトップレベルの情報として，正常値であることを表す。 */
	public static final WifeQualityFlag GOOD = new WifeQualityFlag(QualityFlag.GOOD);
	/** クォリティに関するトップレベルの情報として，データオブジェクト値に初期値が設定されていることを表す。 */
	public static final WifeQualityFlag INITIAL = new WifeQualityFlag(2);
	//シリアライズのために必要な処理
	private static int nextOrdinal = 0;
	private final int ordinal = nextOrdinal++;
	private static final WifeQualityFlag[] VALUES = {
		BAD, UNCERTAIN, GOOD, INITIAL,
	};

	/** クォリティを表す値です。 */
	private final transient int quality;

	/** クォリティの文字表現 */
	private static final String[] qualityString = {
	        "BAD", "UNCERTAIN", "INITIAL", "GOOD",
	};

	/**
	 * コンストラクタです。
	 * クォリティを表す定数を、引数に持ちます。private コンストラクタなので、
	 * 継承、や new 演算子でインスタンスを生成することができません。
	 * @param quality クォリティを表す定数
	 */
	private WifeQualityFlag(int quality) {
		this.quality = quality;
	}

	/**
	 * QualityFlag インターフェイスの実装です。
	 * @return クォリティを表す定数
	 */
	public int getQuality() {
		return quality;
	}

	/**
	 * デシリアライズによるインスタンスの生成を防ぎます。
	 */
	private Object readResolve() throws ObjectStreamException {
		return VALUES[ordinal];
	}

	/**
	 * QualityFlag インターフェイスの実装です。リミットは実装されていません。
	 */
	public int getLimit() {
		return QualityFlag.LIMIT_NOT_LIMITED;
	}

	/**
	 * QualityFlag インターフェイスの実装です。ステータスは実装されていません。
	 */
	public int getSubStatus() {
		return QualityFlag.SUBSTATUS_NON_SPECIFIC;
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return qualityString[quality];
    }
}
