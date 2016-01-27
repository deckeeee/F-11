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

import org.F11.scada.EnvironmentManager;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.log4j.Logger;

/**
 * �f�[�^�ύX�C�x���g�l���f�[�^�x�[�X�Ɋi�[���A����ݒ茏���ȏ�Ȃ������J�n����N���X
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmPrintService extends AbstractPrintService {
	/** 1�y�[�W�Ɉ������s�� */
	private final int maxLine;
	/** ���M���OAPI */
	private static Logger log = Logger.getLogger(AlarmPrintService.class);

	/**
	 * �x�����T�[�r�X�����������܂��B�f�[�^�x�[�X�ɖ�����̃��R�[�h�����݂���΁A�S�Ď擾�������f�[�^�����������܂��B
	 * 
	 * @param printDAO �x�����f�[�^�x�[�XDAO
	 * @param printer �v�����^�[�I�u�W�F�N�g
	 * @throws SQLException �f�[�^�x�[�X�G���[������
	 */
	public AlarmPrintService(AlarmPrintDAO printDAO, AlarmPrinter printer) {
		super(printDAO, printer);
		this.maxLine =
			Integer.parseInt(EnvironmentManager.get(
				"/server/alarm/print/pagelines",
				"10"));
		start();
		log.info("constracted AlarmPrintService.");
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
			if (printLineDatas == null) {
				// ������e��������
				printLineDatas =
					new ArrayList<PrintLineData>(printDAO.findAll());
				print();
			}
			if (printDAO.isAlarmPrint(key)) {
				printDAO.insert(key);
				PrintLineData data = printDAO.find(key);
				printLineDatas.add(data);
				print();
			}
		} catch (SQLException e) {
			log.error("�������DB�G���[���������܂����B", e);
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
			if (maxLine <= printLineDatas.size()) {
				printer.print(printLineDatas);
				printDAO.deleteAll();
				printLineDatas.clear();
			}
		} finally {
			lock.unlock();
		}
	}
}
