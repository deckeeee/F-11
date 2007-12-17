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
 * �A�i���O�̉ϒl��ݒ肷��N���X�ł��B 
 * @author hori
 */
public class VariableAnalogSetter implements ValueSetter {
	/** �����ݑΏۃz���_�w�� */
	protected String providerName;
	protected String holderName;

	/**
	 * �R���X�g���N�^
	 * @param providerName
	 * @param holderName
	 */
	public VariableAnalogSetter(String providerName, String holderName) {
		this.providerName = providerName;
		this.holderName = holderName;
	}

	/**
	 * �l���z���_�ɐݒ肵�A�����݃��\�b�h���Ăяo���܂��B
	 * @param variableValue �������ޒl
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
	 * �f�[�^�v���o�C�_���{�f�[�^�z���_�[�����A���_�[�o�[�Ō�������������z���Ԃ��܂��B
	 * @return �f�[�^�v���o�C�_���{�f�[�^�z���_�[�����A���_�[�o�[�Ō�������������z��
	 */
	public String getDestination() {
		return (holderName == null) ? "" : (providerName + "_" + holderName);
	}

	public boolean isFixed() {
		return false;
	}
}