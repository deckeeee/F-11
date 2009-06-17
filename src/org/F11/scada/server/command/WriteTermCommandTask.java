/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.command;

import java.util.Date;
import java.util.Map;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

/**
 * Executor �Ŏ��s�����^�X�N�̃N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WriteTermCommandTask implements Runnable {
	/** ���M���OAPI */
	private final Logger log = Logger.getLogger(WriteTermCommandTask.class);
	/** �Œ�l */
	private static final byte[] TRUE_DATA = { (byte) 0xFF, (byte) 0xFF };
	private static final byte[] FALSE_DATA = { (byte) 0x00, (byte) 0x00 };
	/** �f�[�^�ύX�C�x���g�̎Q�� */
	private final DataValueChangeEventKey evt;
	/** �v���o�C�_�� */
	private final String provider;
	/** �z���_�� */
	private final String holder;
	/** �������ޒl */
	private final String value;
	/**  */
	private Map<HolderString, WriteTermCommandTask> map;

	/**
	 * �^�X�N�����������܂�
	 * 
	 * @param evt �f�[�^�ύX�C�x���g
	 */
	WriteTermCommandTask(
			DataValueChangeEventKey evt,
			String provider,
			String holder,
			String value) {
		this.evt = evt;
		this.provider = provider;
		this.holder = holder;
		this.value = value;
	}

	WriteTermCommandTask(
			DataValueChangeEventKey evt,
			String provider,
			String holder,
			String value,
			Map<HolderString, WriteTermCommandTask> map) {
		this.evt = evt;
		this.provider = provider;
		this.holder = holder;
		this.value = value;
		this.map = map;
	}

	/**
	 * Executor �ɂ����s����郁�\�b�h�ł��B
	 */
	public void run() {
		if (evt.getValue().booleanValue()) {
			DataHolder dh =
				Manager.getInstance().findDataHolder(provider, holder);
			if (dh != null) {
				WifeData wd = (WifeData) dh.getValue();
				if (wd instanceof WifeDataAnalog) {
					writeAnalog(dh, wd);
				} else if (wd instanceof WifeDataDigital) {
					writeDigital(dh, wd);
				} else {
					log.error("�f�W�^�����̓A�i���O�ȊO�̃f�[�^���w�肳��Ă��܂��B : "
						+ provider
						+ "_"
						+ holder);
				}
			} else {
				log.warn(provider + "_" + holder + " ���o�^����Ă��܂���");
			}
		}
		remove();
	}

	private void writeDigital(DataHolder dh, WifeData wd) {
		WifeDataDigital dd = (WifeDataDigital) wd;
		dh.setValue(
			(WifeData) dd.valueOf(getSendData(value)),
			new Date(),
			WifeQualityFlag.GOOD);
		log.info(provider + "_" + holder + " �� " + value + " ����������");
		try {
			dh.syncWrite();
		} catch (Exception e) {
			log.error("�f�W�^���f�[�^�������݃G���[", e);
		}
	}

	private void writeAnalog(DataHolder dh, WifeData wd) {
		WifeDataAnalog da = (WifeDataAnalog) wd;
		ConvertValue conv =
			(ConvertValue) dh.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
		double doubleValue = conv.convertInputValue(value);
		dh.setValue(da.valueOf(doubleValue), new Date(), WifeQualityFlag.GOOD);
		log.info(provider + "_" + holder + " �� " + doubleValue + " ����������");
		try {
			dh.syncWrite();
		} catch (Exception e) {
			log.error("�A�i���O�f�[�^�������݃G���[", e);
		}
	}

	private byte[] getSendData(String value) {
		return Boolean.valueOf(value).booleanValue() ? TRUE_DATA : FALSE_DATA;
	}

	private void remove() {
		if (null != map) {
			HolderString key = new HolderString(provider, holder);
			if (map.containsKey(key)) {
				map.remove(key);
			}
		}
	}
}
