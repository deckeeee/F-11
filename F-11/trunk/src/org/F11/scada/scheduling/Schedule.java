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

package org.F11.scada.scheduling;

/**
 * �X�P�W���[����\���N���X�ł��B
 * �X�P�W���[���͎��s�^�X�N�ƃC�e���[�^�[��ێ�����I�u�W�F�N�g�ł�
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class Schedule {
	/** ���s�^�X�N�I�u�W�F�N�g */
	private SchedulerTask task;
	/** �^�X�N�����s����Ԋu��񋟂���C�e���[�^ */
	private ScheduleIterator scheduleIterator;

	/**
	 * �^�X�N�����s����Ԋu��񋟂���C�e���[�^��Ԃ��܂�
	 * @return �^�X�N�����s����Ԋu��񋟂���C�e���[�^
	 */
	public ScheduleIterator getScheduleIterator() {
		return scheduleIterator;
	}

	/**
	 * ���s�^�X�N�I�u�W�F�N�g��Ԃ��܂�
	 * @return ���s�^�X�N�I�u�W�F�N�g
	 */
	public SchedulerTask getTask() {
		return task;
	}

	/**
	 * �^�X�N�����s����Ԋu��񋟂���C�e���[�^��ݒ肵�܂�
	 * @param iterator �^�X�N�����s����Ԋu��񋟂���C�e���[�^
	 */
	public void setScheduleIterator(ScheduleIterator iterator) {
		scheduleIterator = iterator;
	}

	/**
	 * ���s�^�X�N�I�u�W�F�N�g��ݒ肵�܂�
	 * @param task ���s�^�X�N�I�u�W�F�N�g
	 */
	public void setTask(SchedulerTask task) {
		this.task = task;
	}
}
