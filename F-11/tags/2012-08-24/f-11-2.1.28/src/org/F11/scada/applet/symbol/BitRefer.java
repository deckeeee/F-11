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

package org.F11.scada.applet.symbol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;

import org.F11.scada.WifeException;
import org.F11.scada.applet.expression.Expression;
import org.apache.log4j.Logger;

/**
 * デジタルビットの計算クラスです。
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class BitRefer implements CompositeProperty {
	/** 参照するリファレンサのリストです。 */
	protected List dataReferencers = new ArrayList();
	/** 結果に使う計算式です。 */
	protected Expression expression;
	/** 結果が On の場合の下位プロパティです。 */
	private CompositeProperty symbolTrue;
	/** 結果が Off の場合の下位プロパティです。 */
	private CompositeProperty symbolFalse;
	
	private static Logger logger = Logger.getLogger(BitRefer.class);

	public BitRefer(String value, DataReferencerOwner jimGUI) {
		expression = new Expression();
		expression.toPostfix(value);
		for (Iterator it = expression.getProviderHolderNames().iterator(); it.hasNext(); ) {
			String tag = (String) it.next();
			int p = tag.indexOf('_');
			if (0 < p) {
				DataReferencer dr =
					new DataReferencer(
						tag.substring(0, p),
						tag.substring(p + 1));
				dataReferencers.add(dr);
				dr.connect(jimGUI);
			}
		}
	}

	/**
	 * プロパティを設定します。
	 * １回目はTrue側、２回目はFalse側に設定します。3回目を設定すると例外をスローします。
	 */
	public void addCompositeProperty(CompositeProperty property) {
		if (symbolTrue == null) {
			symbolTrue = property;
		} else if (symbolFalse == null) {
			symbolFalse = property;
		} else {
		    throw new IllegalStateException("It is full properties of instance.");
		}
	}

	/**
	 * ビットの参照結果で異なるプロパティを返します。
	 * @param key   プロパティの名称
	 */
	public String getProperty(String key) {
	    String property = null;
		try {
			if (expression.booleanValue()) {
				/** ビットTrue側のプロパティを取得 */
				if (symbolTrue != null) {
				    property = symbolTrue.getProperty(key);
				}
			} else {
				/** ビットFalse側のプロパティを取得 */
				if (symbolFalse != null) {
				    property = symbolFalse.getProperty(key);
				}
			}
		} catch (WifeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("プロパティ取得エラー: ", e);
			}
		}
		return property;
	}

	/**
	 * 親シンボルをManagerから登録解除します。
	 */
	public void disconnectReferencer(DataReferencerOwner jimGUI) {
	    if (symbolTrue != null && symbolTrue instanceof BitRefer) {
	        BitRefer bitRefer = (BitRefer) symbolTrue;
	        bitRefer.disconnectReferencer(jimGUI);
	        symbolTrue = null;
	    }
	    if (symbolFalse != null && symbolFalse instanceof BitRefer) {
	        BitRefer bitRefer = (BitRefer) symbolFalse;
	        bitRefer.disconnectReferencer(jimGUI);
	        symbolFalse = null;
	    }

		for (Iterator it = dataReferencers.iterator(); it.hasNext();) {
			DataReferencer dr = (DataReferencer) it.next();
			dr.disconnect(jimGUI);
		}
		dataReferencers.clear();
		dataReferencers = null;
		expression = null;
	}
}
