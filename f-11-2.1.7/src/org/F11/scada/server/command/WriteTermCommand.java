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

package org.F11.scada.server.command;

import java.util.Date;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * �w�肳�ꂽ�p�X�Ƀr�b�g�����o�͂���N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WriteTermCommand implements Command {
	/** �Œ�l */
	private static byte[] TRUE_DATA = {(byte) 0xFF, (byte) 0xFF};
	private static byte[] FALSE_DATA = {(byte) 0x00, (byte) 0x00};
	/** Logging API */
	private static Logger log = Logger.getLogger(WriteTermCommand.class);
	/** �X���b�h�v�[�����s�N���X */
	private static Executor executor = Executors.newCachedThreadPool();

	/** �v���o�C�_�� */
	private String provider;
	/** �z���_�� */
	private String holder;
	/** �������ޒl */
	private String value;

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * �R�}���h�����s���܂�
	 * @param evt �f�[�^�ύX�C�x���g
	 */
	public void execute(DataValueChangeEventKey evt) {
		if (null == provider) {
			throw new IllegalStateException("provider���ݒ肳��Ă��܂���");
		}
		if (null == holder) {
			throw new IllegalStateException("holder���ݒ肳��Ă��܂���");
		}
		if (null == value) {
			throw new IllegalStateException("value���ݒ肳��Ă��܂���");
		}

		try {
			executor.execute(new WriteTermCommandTask(evt));
		} catch (RejectedExecutionException e) {}
	}

	/**
	 * Executor �Ŏ��s�����^�X�N�̃N���X�ł��B
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	private class WriteTermCommandTask implements Runnable {
		/** �f�[�^�ύX�C�x���g�̎Q�� */
		private final DataValueChangeEventKey evt;

		/**
		 * �^�X�N�����������܂�
		 * @param evt �f�[�^�ύX�C�x���g
		 */
		WriteTermCommandTask(DataValueChangeEventKey evt) {
			this.evt = evt;
		}

		/**
		 * Executor �ɂ����s����郁�\�b�h�ł��B
		 */
		public void run() {
			if (evt.getValue().booleanValue()) {
				DataHolder dh = Manager.getInstance().findDataHolder(provider, holder);
				if (dh != null) {
					WifeData wd = (WifeData) dh.getValue();
					if (wd instanceof WifeDataAnalog) {
						writeAnalog(dh, wd);
					} else if (wd instanceof WifeDataDigital) {
						writeDigital(dh, wd);
					} else {
						log.error("�f�W�^�����̓A�i���O�ȊO�̃f�[�^���w�肳��Ă��܂��B : " + provider + "_" + holder);
					}
				} else {
					log.warn(provider + "_" + holder + " ���o�^����Ă��܂���");
				}
			}
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
				log.error("�f�W�^���f�[�^�������݃G���[" ,e);
			}
		}

		private void writeAnalog(DataHolder dh, WifeData wd) {
			WifeDataAnalog da = (WifeDataAnalog) wd;
			ConvertValue conv = (ConvertValue) dh.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
			double doubleValue = conv.convertInputValue(value);
			dh.setValue(
				da.valueOf(doubleValue),
				new Date(),
				WifeQualityFlag.GOOD);
			log.info(provider + "_" + holder + " �� " + doubleValue + " ����������");
			try {
				dh.syncWrite();
			} catch (Exception e) {
				log.error("�A�i���O�f�[�^�������݃G���[" ,e);
			}
		}

		private byte[] getSendData(String value) {
		    return Boolean.valueOf(value).booleanValue() ? TRUE_DATA : FALSE_DATA;
		}
	}
}
