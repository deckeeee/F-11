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
import org.F11.scada.xwife.server.WifeDataProvider;


/**
 * �A�i���O�̌Œ�l��ݒ肷��N���X�ł��B 
 * @author hori
 */
public class FixedAnalogSetter extends AbstractValueSetter {
	/** �Œ�l */
	private String fixedValue;

	/**
	 * �R���X�g���N�^
	 * @param providerName
	 * @param holderName
	 * @param fixedValue �Œ�l
	 */
	public FixedAnalogSetter(String providerName, String holderName, String fixedValue) {
	    super(providerName, holderName);
		this.fixedValue = fixedValue;
	}

	/**
	 * �Œ�l���z���_�ɐݒ肵�A�����݃��\�b�h���Ăяo���܂��B
	 * @param variableValue �������܂��B
	 */
	public void writeValue(Object variableValue) {
		DataHolder dh = Manager.getInstance().findDataHolder(provider, holder);
		if (dh != null) {
			WifeData wd = (WifeData) dh.getValue();
			if (wd instanceof WifeDataAnalog) {
				WifeDataAnalog da = (WifeDataAnalog) wd;
				ConvertValue conv = (ConvertValue) dh.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
				dh.setValue(
					da.valueOf(conv.convertInputValue(fixedValue)),
					new java.util.Date(),
					WifeQualityFlag.GOOD);

				try {
					dh.syncWrite();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isFixed() {
		return true;
	}
}