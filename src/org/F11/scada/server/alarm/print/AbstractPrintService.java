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

package org.F11.scada.server.alarm.print;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Service;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.log4j.Logger;

public abstract class AbstractPrintService implements AlarmDataStore, Runnable,
		Service {
	/** �x�����f�[�^�x�[�XDAO */
	protected final AlarmPrintDAO printDAO;
	/** ����f�[�^���X�g */
	protected List<PrintLineData> printLineDatas;
	/** �C�x���g�L���[ */
	protected final BlockingQueue<DataValueChangeEventKey> queue;
	/** �X���b�h */
	protected Thread thread;
	/** �v�����^�[�N���X */
	protected final AlarmPrinter printer;
	/** ���b�N�I�u�W�F�N�g */
	protected final ReentrantLock lock = new ReentrantLock();
	/** ���M���OAPI */
	private static Logger log = Logger.getLogger(AbstractPrintService.class);

	protected AbstractPrintService(AlarmPrintDAO printDAO, AlarmPrinter printer) {
		this.printDAO = printDAO;
		queue = new LinkedBlockingQueue<DataValueChangeEventKey>();
		this.printer = printer;
	}

	/**
	 * �f�[�^�ύX�C�x���g�l�𓊓����܂��B
	 * 
	 * @param key �f�[�^�ύX�C�x���g�l
	 */
	public void put(DataValueChangeEventKey key) {
		try {
			if (isPrint()) {
				queue.put(key);
			}
		} catch (InterruptedException e) {
			log.info("���荞�݂��������܂���", e);
		}
	}

	private boolean isPrint() {
		String dataHolderID =
			EnvironmentManager.get("/server/alarm/print/enable", "");
		if (null == dataHolderID || "".equals(dataHolderID)) {
			log.info("�󎚐ݒ�z���_���ݒ肳��Ă��܂���B��Ɉ󎚂��܂�");
			return true;
		} else {
			DataHolder hd = Manager.getInstance().findDataHolder(dataHolderID);
			if (hd == null) {
				log.info("�󎚐ݒ�z���_���ݒ肳��Ă��܂���B��Ɉ󎚂��܂�");
				return true;
			} else {
				Object obj = hd.getValue();
				if (WifeDataDigital.class.isInstance(obj)) {
					WifeDataDigital d = (WifeDataDigital) obj;
					return d.isOnOff(true);
				} else {
					log.info("�󎚐ݒ�z���_���f�W�^���^�C�v�ł͂���܂���B��Ɉ󎚂��܂�");
					return true;
				}
			}
		}
	}

	/**
	 * �L���[���f�[�^�ύX�C�x���g�����o���A����f�[�^���X�g�ɒǉ����f�[�^�x�[�X�ɒǉ�����B
	 */
	public void run() {
		Thread ct = Thread.currentThread();
		while (ct == thread) {
			try {
				insertEvent(queue.take());
			} catch (InterruptedException e) {
				log.info("���荞�݂��������܂���", e);
			}
		}
	}

	/**
	 * �f�[�^�ύX�C�x���g���f�[�^�x�[�X�ƃ��X�g�ɒǉ����܂� ���̃��\�b�h�� public �ɂȂ��Ă���̂́AAcpect
	 * �ɂ��g�����U�N�V�������\�ɂ���ׂł��B
	 * 
	 * @param key �f�[�^�ύX�C�x���g
	 */
	abstract public void insertEvent(DataValueChangeEventKey key);

	/**
	 * �T�[�o�[�X���b�h���J�n���܂��B
	 */
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.setName(getClass().getName());
			thread.start();
		}
	}

	/**
	 * �T�[�o�[�X���b�h���~���܂��B
	 */
	public void stop() {
		if (thread != null) {
			Thread th = thread;
			thread = null;
			th.interrupt();
		}
	}
}
