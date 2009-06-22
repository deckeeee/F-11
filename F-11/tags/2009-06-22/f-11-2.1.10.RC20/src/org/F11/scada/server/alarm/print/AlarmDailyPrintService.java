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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.scheduling.DailyIterator;
import org.F11.scada.scheduling.Schedule;
import org.F11.scada.scheduling.Scheduler;
import org.F11.scada.scheduling.SchedulerTask;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.log4j.Logger;

/**
 * �f�[�^�ύX�C�x���g�l���f�[�^�x�[�X�Ɋi�[���A���ւ��ň�����J�n����N���X
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmDailyPrintService extends AbstractPrintService {
	/** ���M���OAPI */
	private static Logger log = Logger.getLogger(AlarmDailyPrintService.class);
	/**
	 * �x�����T�[�r�X�����������܂��B�f�[�^�x�[�X�ɖ�����̃��R�[�h�����݂���΁A�S�Ď擾�������f�[�^�����������܂��B
	 * 
	 * @param printDAO �x�����f�[�^�x�[�XDAO
	 * @param printer �v�����^�[�I�u�W�F�N�g
	 * @throws SQLException �f�[�^�x�[�X�G���[������
	 */
	public AlarmDailyPrintService(AlarmPrintDAO printDAO, AlarmPrinter printer) {
		super(printDAO, printer);
		// ������e��������
		try {
			printLineDatas = new ArrayList<PrintLineData>(printDAO.findAll());
		} catch (SQLException e) {
			log.error("�x�񃁃b�Z�[�W����������Ɏ��s���܂���", e);
		}
		setScheduler();
		start();
		log.info("constracted AlarmDailyPrintService.");
	}

	private void setScheduler() {
		Schedule schedule =
			new Schedule(new DailyPrintTask(), getDailyIterator());
		Scheduler scheduler = new Scheduler();
		scheduler.schedule(schedule);
	}

	private DailyIterator getDailyIterator() {
		String time =
			EnvironmentManager.get("/server/alarm/print/printdate", "00:00:00");
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
		String clazz =
			EnvironmentManager.get("/server/alarm/print/className", "");
		try {
			Date d = f.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			if ("org.F11.scada.server.alarm.print.AlarmDailyPrintService"
				.equals(clazz)) {
				log.info("����" + time + "�Ɍx�񃁃b�Z�[�W��������܂�");
			}
			return new DailyIterator(cal.get(Calendar.HOUR_OF_DAY), cal
				.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		} catch (ParseException e) {
			if ("org.F11.scada.server.alarm.print.AlarmDailyPrintService"
				.equals(clazz)) {
				log
					.warn("/server/alarm/print/printdate���s���ł��B00:00:00����ŋN�����܂��B");
			}
			return new DailyIterator(0, 0, 0);
		}
	}

	/**
	 * �f�[�^�ύX�C�x���g���f�[�^�x�[�X�ƃ��X�g�ɒǉ����܂� ���̃��\�b�h�� public �ɂȂ��Ă���̂́AAcpect
	 * �ɂ��g�����U�N�V�������\�ɂ���ׂł��B
	 * 
	 * @param key �f�[�^�ύX�C�x���g
	 */
	public void insertEvent(DataValueChangeEventKey key) {
		lock.lock();
		try {
			if (printDAO.isAlarmPrint(key)) {
				printDAO.insert(key);
				PrintLineData data = printDAO.find(key);
				printLineDatas.add(data);
			}
		} catch (SQLException e) {
			log.error("���b�Z�[�W���DB�}���ŃG���[����", e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * ����f�[�^���X�g��1�y�[�W�Ɉ������s���ȏ�ł���Έ���������J�n����B ���̌�ێ���������f�[�^���X�g�ƃf�[�^�x�[�X���N���A�[����B
	 * 
	 * @exception SQLException �f�[�^�x�[�X�G���[������
	 */
	private void print() throws SQLException {
		lock.lock();
		try {
			printer.print(printLineDatas);
			printDAO.deleteAll();
			printLineDatas.clear();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * ���ݒ莞���Ɉ������^�X�N
	 * 
	 * @author maekawa
	 * 
	 */
	private class DailyPrintTask extends SchedulerTask {
		@Override
		public void run() {
			try {
				print();
			} catch (SQLException e) {
				log.error("���b�Z�[�W�����DB�G���[����", e);
			}
		}
	}
}
