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

import java.util.Date;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;


/**
 * デジタルの固定値を設定するクラスです。 
 * @author hori
 */
public class FixedDigitalSetter extends AbstractValueSetter {
	/** 固定値 */
	private static byte[] trueData = {(byte) 0xFF, (byte) 0xFF};
	private static byte[] falseData = {(byte) 0x00, (byte) 0x00};
	private final boolean fixedValue;

	/**
	 * コンストラクタ
	 * @param providerName
	 * @param holderName
	 * @param fixedValue	固定値
	 */
	public FixedDigitalSetter(String providerName, String holderName, String fixedValue) {
	    super(providerName, holderName);
	    this.fixedValue = Boolean.valueOf(fixedValue).booleanValue();
	}

	/**
	 * 固定値をホルダに設定し、書込みメソッドを呼び出します。
	 * @param variableValue 無視します。
	 */
	public void writeValue(Object variableValue) {
		DataHolder dh = Manager.getInstance().findDataHolder(provider, holder);
		if (dh != null) {
			WifeData wd = (WifeData) dh.getValue();
			if (wd instanceof WifeDataDigital) {
				WifeDataDigital dd = (WifeDataDigital) wd;
				dh.setValue((WifeData) dd.valueOf(getSendData()), new Date(), WifeQualityFlag.GOOD);

				try {
					dh.syncWrite();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private byte[] getSendData() {
	    return fixedValue ? trueData : falseData;
	}

	public boolean isFixed() {
		return true;
	}
}