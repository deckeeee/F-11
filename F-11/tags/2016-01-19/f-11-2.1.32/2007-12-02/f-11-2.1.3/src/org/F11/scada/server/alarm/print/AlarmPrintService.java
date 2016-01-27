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

package org.F11.scada.server.alarm.print;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Service;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.event.LoggingDataEventQueue;
import org.apache.log4j.Logger;

/**
 * �f�[�^�ύX�C�x���g�l���f�[�^�x�[�X�Ɋi�[���A����ݒ茏���ȏ�Ȃ������J�n����N���X
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmPrintService implements AlarmDataStore, Runnable, Service {
	/** �x�����f�[�^�x�[�XDAO */
	private final AlarmPrintDAO printDAO;
	/** ����f�[�^���X�g */
	private List printLineDatas;
	/** 1�y�[�W�Ɉ������s�� */
	private final int maxLine;
	/** �C�x���g�L���[ */
	private final LoggingDataEventQueue queue;
	/** �X���b�h */
	private Thread thread;
	/** �v�����^�[�N���X */
	private final AlarmPrinter printer;
	/** ���M���OAPI */
	private static Logger log = Logger.getLogger(AlarmPrintService.class);

	/**
	 * �x�����T�[�r�X�����������܂��B�f�[�^�x�[�X�ɖ�����̃��R�[�h�����݂���΁A�S�Ď擾�������f�[�^�����������܂��B
	 * @param printDAO �x�����f�[�^�x�[�XDAO
	 * @param printer �v�����^�[�I�u�W�F�N�g
	 * @throws SQLException �f�[�^�x�[�X�G���[������
	 */
	public AlarmPrintService(AlarmPrintDAO printDAO, AlarmPrinter printer) {
		this.printDAO = printDAO;
		this.maxLine =
			Integer.parseInt(
				EnvironmentManager.get("/server/alarm/print/pagelines", "10"));
		this.queue = new LoggingDataEventQueue();
		this.printer = printer;
		start();
		log.info("constracted AlarmPrintService.");
	}

	/**
	 * �f�[�^�ύX�C�x���g�l�𓊓����܂��B
	 * @param key �f�[�^�ύX�C�x���g�l
	 */
	public void put(DataValueChangeEventKey key) {
		this.queue.enqueue(key);
	}

	/**
	 * �L���[���f�[�^�ύX�C�x���g�����o���A����f�[�^���X�g�ɒǉ����f�[�^�x�[�X�ɒǉ�����B
	 */
	public void run() {
		Thread ct = Thread.currentThread();
		while (ct == this.thread) {
			insertEvent((DataValueChangeEventKey) this.queue.dequeue());
		}
	}

	/**
	 * �T�[�o�[�X���b�h���J�n���܂��B
	 */
	public void start() {
	    if (thread == null) {
			this.thread = new Thread(this);
			this.thread.setName(getClass().getName());
			this.thread.start();
	    }
	}

	/**
	 * �T�[�o�[�X���b�h���~���܂��B
	 */
	public void stop() {
	    if (thread != null) {
	        Thread th = thread;
			this.thread = null;
			th.interrupt();
	    }
	}

	/**
	 * �f�[�^�ύX�C�x���g���f�[�^�x�[�X�ƃ��X�g�ɒǉ����܂�
	 * ���̃��\�b�h�� public �ɂȂ��Ă���̂́AAcpect �ɂ��g�����U�N�V�������\�ɂ���ׂł��B
	 * @param key �f�[�^�ύX�C�x���g
	 */
	public void insertEvent(DataValueChangeEventKey key) {
		try {
			if (this.printLineDatas == null) {
				// ������e��������
				this.printLineDatas =
						new ArrayList(this.printDAO.findAll());
				print();
			}
			if (this.printDAO.isAlarmPrint(key)) {
				this.printDAO.insert(key);
				PrintLineData data = this.printDAO.find(key);
				this.printLineDatas.add(data);
				print();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����f�[�^���X�g��1�y�[�W�Ɉ������s���ȏ�ł���Έ���������J�n����B
	 * ���̌�ێ���������f�[�^���X�g�ƃf�[�^�x�[�X���N���A�[����B
	 * 
	 * @exception SQLException �f�[�^�x�[�X�G���[������
	 */
	private void print() throws SQLException {
		if (this.maxLine <= this.printLineDatas.size()) {
//			System.out.println(this.printLineDatas);
			this.printer.print(this.printLineDatas);
			this.printDAO.deleteAll();
			this.printLineDatas.clear();
//			System.out.println("Clear printLineDatas.");
		}
	}
}
