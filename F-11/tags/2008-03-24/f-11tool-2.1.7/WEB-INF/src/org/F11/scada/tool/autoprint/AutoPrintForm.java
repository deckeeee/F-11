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

package org.F11.scada.tool.autoprint;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.struts.validator.ValidatorForm;

/**
 * ��������p�����[�^�i�[�����̃A�N�V�����t�H�[���N���X�ł�
 * @author hori
 */
public class AutoPrintForm extends ValidatorForm {
	/** �X�P�W���[���^�X�N�� */
	private String name;
	/** �X�P�W���[���� */
	private String schedule;
	/** ��������L�� */
	private boolean auto_flag;
	/** �p�����[�^�[�̓����\�� */
	private Timestamp paramdate;
	/** �\������ */
	private String displayname;
	/** �� */
	private int month;
	/** �� */
	private int day;
	/** �� */
	private int hour;
	/** �� */
	private int minute;

	/**
	 * ���̃A�N�V�����t�H�[���I�u�W�F�N�g�����������܂��B
	 * �^�X�N���E�X�P�W���[�����͋󔒁A��������͖����A������EPOCH�ƂȂ�܂��B
	 */
	public AutoPrintForm() {
		super();
		setName("");
		setSchedule("");
		setAutoflag(false);
		setParamdate(new Timestamp(0));
		displayname = "";
	}

	/**
	 * ��������̗L����Ԃ��܂�
	 * @return ��������̗L����Ԃ��܂�
	 */
	public boolean getAutoflag() {
		return auto_flag;
	}

	/**
	 * ����Ԃ��܂�
	 * @return ��
	 */
	public int getDay() {
		return day;
	}

	/**
	 * ����Ԃ��܂�
	 * @return ��
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * ����Ԃ��܂�
	 * @return ��
	 */
	public int getMinute() {
		return minute;
	}

	/**
	 * ����Ԃ��܂�
	 * @return ��
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * �^�X�N����Ԃ��܂�
	 * @return �^�X�N��
	 */
	public String getName() {
		return name;
	}

	/**
	 * �p�����[�^�[�̓����\����Ԃ��܂�
	 * @return �p�����[�^�[�̓����\��
	 */
	public Timestamp getParamdate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		paramdate.setTime(cal.getTimeInMillis());
		return paramdate;
	}

	/**
	 * �X�P�W���[������Ԃ��܂�
	 * @return �X�P�W���[����
	 */
	public String getSchedule() {
		return schedule;
	}

	/**
	 * �����󎚂̗L����ݒ肵�܂�
	 * @param b �����󎚂̗L��
	 */
	public void setAutoflag(boolean b) {
		auto_flag = b;
	}

	/**
	 * ����ݒ肵�܂�
	 * @param i ��
	 */
	public void setDay(int i) {
		day = i;
	}

	/**
	 * ����ݒ肵�܂�
	 * @param i ��
	 */
	public void setHour(int i) {
		hour = i;
	}

	/**
	 * ����ݒ肵�܂�
	 * @param i ��
	 */
	public void setMinute(int i) {
		minute = i;
	}

	/**
	 * ����ݒ肵�܂�
	 * @param i ��
	 */
	public void setMonth(int i) {
		month = i;
	}

	/**
	 * �^�X�N����ݒ肵�܂�
	 * @param string �^�X�N��
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * �p�����[�^�[�̓����\����ݒ肵�܂�
	 * @param timestamp �p�����[�^�[�̓����\��
	 */
	public void setParamdate(Timestamp timestamp) {
		if (timestamp == null)
			return;
		paramdate = timestamp;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(paramdate.getTime());
		month = cal.get(Calendar.MONTH) + 1;
		day = cal.get(Calendar.DAY_OF_MONTH);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
	}

	/**
	 * �X�P�W���[������ݒ肵�܂�
	 * @param string �X�P�W���[����
	 */
	public void setSchedule(String string) {
		schedule = string;
	}

    public String getDisplayname() {
        return displayname;
    }
    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }
}
