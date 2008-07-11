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

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeQualityFlag;


/**
 * アナログの可変値を設定するクラスです。 
 * @author hori
 */
public class VariableAnalogSetter implements ValueSetter {
	/** 書込み対象ホルダ指定 */
	protected String providerName;
	protected String holderName;

	/**
	 * コンストラクタ
	 * @param providerName
	 * @param holderName
	 */
	public VariableAnalogSetter(String providerName, String holderName) {
		this.providerName = providerName;
		this.holderName = holderName;
	}

	/**
	 * 値をホルダに設定し、書込みメソッドを呼び出します。
	 * @param variableValue 書き込む値
	 */
	public void writeValue(Object variableValue) {
		DataHolder dh = Manager.getInstance().findDataHolder(providerName, holderName);
		if (dh == null)
			return;
		WifeData wd = (WifeData) dh.getValue();
		if (wd instanceof WifeDataAnalog) {
			WifeDataAnalog da = (WifeDataAnalog) wd;
			ConvertValue conv = (ConvertValue) dh.getParameter("convert");
			dh.setValue(
				da.valueOf(conv.convertInputValue((String) variableValue)),
				new java.util.Date(),
				WifeQualityFlag.GOOD);

			try {
				dh.syncWrite();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * データプロバイダ名＋データホルダー名をアンダーバーで結合した文字列配列を返します。
	 * @return データプロバイダ名＋データホルダー名をアンダーバーで結合した文字列配列
	 */
	public String getDestination() {
		return (holderName == null) ? "" : (providerName + "_" + holderName);
	}

	public boolean isFixed() {
		return false;
	}
}