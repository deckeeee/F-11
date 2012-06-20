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
 */
package org.F11.scada.server.frame;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.F11.scada.server.register.HolderString;
import org.F11.scada.server.register.PageXmlRuleSet;
import org.F11.scada.server.register.PageXmlUtil;
import org.apache.commons.digester.RuleSet;

/**
 * ページ定義XMLと更新日付を保持する不変クラスです。
 * 
 * @author hori
 */
public class PageDefine implements Serializable {
	private static int hashCode(byte[] array) {
		final int prime = 31;
		if (array == null)
			return 0;
		int result = 1;
		for (int index = 0; index < array.length; index++) {
			result = prime * result + (int) array[index];
		}
		return result;
	}

	private static final long serialVersionUID = 7604502733977351241L;
	/** ページ作成(更新)日付 */
	private final long editTime;
	/** このページが使用しているホルダのセット */
	private final Set<HolderString> holdersSet;
	/** このページのキャッシュ属性 */
	private final boolean isCache;
	/** ページ定義の圧縮済みデータ */
	private final byte[] srcXmlData;
	/** 圧縮前のデータ長 */
	private int srcXmlDataLength;
	/** 圧縮後のデータ長 */
	private int compressedDataLength;

	/**
	 * コンストラクタ
	 */
	public PageDefine(long editTime, String srcXml) {
		this(editTime, srcXml, new PageXmlRuleSet());
	}

	public PageDefine(long editTime, String srcXml, RuleSet ruleSet) {
		this.editTime = editTime;
		holdersSet = PageXmlUtil.getHolderStrings(srcXml, ruleSet);
		isCache = PageXmlUtil.isCache(srcXml);
		srcXmlData = compress(srcXml);
	}

	/**
	 * デシリアライズ時に使用するコンストラクタ
	 * 
	 * @param editTime
	 * @param srcXml
	 * @param holdersSet
	 * @param isCache
	 */
	private PageDefine(
			long editTime,
			Set<HolderString> holdersSet,
			boolean isCache,
			byte[] srcXmlData,
			int srcXmlDataLength,
			int compressedDataLength) {
		this.editTime = editTime;
		this.holdersSet = holdersSet;
		this.isCache = isCache;
		this.srcXmlData = srcXmlData;
		this.srcXmlDataLength = srcXmlDataLength;
		this.compressedDataLength = compressedDataLength;
	}

	private byte[] compress(String srcXml) {
		try {
			byte[] input = srcXml.getBytes("UTF-8");
			srcXmlDataLength = input.length;
			Deflater deflater = new Deflater(Deflater.BEST_SPEED);
			deflater.setInput(input);
			deflater.finish();
			byte[] output = new byte[input.length];
			compressedDataLength = deflater.deflate(output);
			return output;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	/**
	 * 更新日付を返します。
	 * 
	 * @return long
	 */
	public long getEditTime() {
		return editTime;
	}

	/**
	 * ページ定義のXML表現を返します。
	 * 
	 * @return String
	 */
	public String getSrcXml() {
		return decomporess();
	}

	private String decomporess() {
		try {
			Inflater inflater = new Inflater();
			inflater.setInput(srcXmlData, 0, compressedDataLength);
			byte[] result = new byte[srcXmlDataLength];
			int resultLength = inflater.inflate(result);
			inflater.end();
			return new String(result, 0, resultLength, "UTF-8");
		} catch (DataFormatException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * このオブジェクトの文字列表現を返します。
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("editTime=").append(new Timestamp(editTime));
		buffer.append(", srcXml=").append(getSrcXml());
		buffer.append(", holdersSet=" + holdersSet);
		return buffer.toString();
	}

	/**
	 * 防御的readResolveメソッド。 不正にデシリアライズされるのを防止します。
	 * 
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new PageDefine(
			editTime,
			holdersSet,
			isCache,
			srcXmlData,
			srcXmlDataLength,
			compressedDataLength);
	}

	/**
	 * このページが保持しているデータホルダーのセットを返します
	 * 
	 * @return このページが保持しているデータホルダーのセットを返します
	 */
	public Set<HolderString> getDataHolders() {
		return new HashSet<HolderString>(holdersSet);
	}

	/**
	 * このページがキャッシュ指定されているかを返します。
	 */
	public boolean isCache() {
		return isCache;
	}

	/**
	 * このオブジェクトのハッシュを返します
	 * 
	 * @return このオブジェクトのハッシュ
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + compressedDataLength;
		result = prime * result + (int) (editTime ^ (editTime >>> 32));
		result =
			prime * result + ((holdersSet == null) ? 0 : holdersSet.hashCode());
		result = prime * result + (isCache ? 1231 : 1237);
		result = prime * result + PageDefine.hashCode(srcXmlData);
		result = prime * result + srcXmlDataLength;
		return result;
	}

	/**
	 * このオブジェクトの値を比べ、同じならば true を返します。
	 * 
	 * @param obj 比較対象のオブジェクト
	 * @return このオブジェクトの値を比べ、同じならば true を返します。
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PageDefine other = (PageDefine) obj;
		if (compressedDataLength != other.compressedDataLength)
			return false;
		if (editTime != other.editTime)
			return false;
		if (holdersSet == null) {
			if (other.holdersSet != null)
				return false;
		} else if (!holdersSet.equals(other.holdersSet))
			return false;
		if (isCache != other.isCache)
			return false;
		if (!Arrays.equals(srcXmlData, other.srcXmlData))
			return false;
		if (srcXmlDataLength != other.srcXmlDataLength)
			return false;
		return true;
	}
}
