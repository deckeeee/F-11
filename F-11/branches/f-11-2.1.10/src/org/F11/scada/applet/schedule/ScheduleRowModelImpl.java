/*
 * =============================================================================
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

package org.F11.scada.applet.schedule;

public class ScheduleRowModelImpl implements ScheduleRowModel {
	/** �I�����ꂽ�s�ԍ� */
	private int selectRow;
	/** DefaultScheduleModel�̎Q�� */
	private ScheduleModel scheduleModel;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param scheduleModel DefaultScheduleModel�̎Q��
	 * @param selectRow �I���s
	 */
	public ScheduleRowModelImpl(ScheduleModel scheduleModel, int selectRow) {
		this.scheduleModel = scheduleModel;
		this.selectRow = selectRow;
	}

	/**
	 * �X�P�W���[���񐔂̐��i�񐔁j��Ԃ��܂�
	 */
	public int getColumnCount() {
		return scheduleModel.getDataSchedule().getNumberSize();
	}

	/**
	 * On ���Ԃ�Ԃ��܂�
	 * 
	 * @param column ��ԍ�
	 */
	public int getOnTime(int column) {
		return scheduleModel.getDataSchedule().getOnTime(selectRow, column);
	}

	/**
	 * Off ���Ԃ�Ԃ��܂�
	 * 
	 * @param column ��ԍ�
	 */
	public int getOffTime(int column) {
		return scheduleModel.getDataSchedule().getOffTime(selectRow, column);
	}

	/**
	 * On ���Ԃ�ݒ肵�܂�
	 * 
	 * @param column ��ԍ�
	 * @param time ����
	 */
	public void setOnTime(int column, int time) {
		scheduleModel.setDataSchedule(scheduleModel.getDataSchedule()
				.setOnTime(selectRow, column, time));
	}

	/**
	 * Off ���Ԃ�ݒ肵�܂�
	 * 
	 * @param column ��ԍ�
	 * @param time ����
	 */
	public void setOffTime(int column, int time) {
		scheduleModel.setDataSchedule(scheduleModel.getDataSchedule()
				.setOffTime(selectRow, column, time));
	}

	/**
	 * ���̃��f���̍��ږ��i�s���j��Ԃ��܂��B
	 */
	public String getDayIndexName() {
		return scheduleModel.getDataSchedule().getDayIndexName(selectRow);
	}

	/**
	 * ���X�i�[�Ƀo�E���Y�v���p�e�B�ύX�C�x���g��ʒm���܂��B
	 * 
	 * @param oldValue �ύX�O�̒l
	 * @param newValue �ύX��̒l
	 */
	public void firePropertyChange(Object oldValue, Object newValue) {
		scheduleModel.firePropertyChange(oldValue, newValue);
		// �ύX�Ɠ����ɏ�����
		scheduleModel.setValue();
		scheduleModel.writeData();
	}
}
