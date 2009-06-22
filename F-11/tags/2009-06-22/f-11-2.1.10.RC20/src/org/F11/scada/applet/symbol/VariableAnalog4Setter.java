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

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog4;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.xwife.server.WifeDataProvider;


/**
 * アナログの可変値を設定するクラスです。 
 * @author hori
 */
public class VariableAnalog4Setter extends AbstractValueSetter {
	/**
	 * コンストラクタ
	 * @param providerName
	 * @param holderName
	 */
	public VariableAnalog4Setter(String providerName, String holderName) {
	    super(providerName, holderName);
	}

	/**
	 * 値をホルダに設定し、書込みメソッドを呼び出します。
	 * @param variableValue 書き込む値
	 */
	public void writeValue(Object variableValue) {
		DataHolder dh = Manager.getInstance().findDataHolder(provider, holder);
		if (dh != null) {
			WifeData wd = (WifeData) dh.getValue();
			if (wd instanceof WifeDataAnalog4 && variableValue instanceof Analog4Data) {
				WifeDataAnalog4 da = (WifeDataAnalog4) wd;
				ConvertValue conv =
				    (ConvertValue) dh.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
				Analog4Data value = (Analog4Data) variableValue;
				double[] data = new double[4];
				for (int i = 0; i < data.length; i++) {
					data[i] = conv.convertInputValue(value.getValue(i));
				}

				dh.setValue(da.valueOf(data), new Date(), WifeQualityFlag.GOOD);

				try {
					dh.syncWrite();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isFixed() {
		return false;
	}

	/**
	 * VariableAnalog4Setterクラスの writeValue に渡す値のクラスです。
	 */
	public static final class Analog4Data {
		private static String[] data;
		private Analog4Data(String[] strData) {
			data = new String[4];
			System.arraycopy(strData, 0, data, 0, data.length);
		}
		public static Analog4Data valueOf(String[] strData) {
			return new Analog4Data(strData);
		}
		public String getValue(int index) {
			return data[index];
		}
	}
}