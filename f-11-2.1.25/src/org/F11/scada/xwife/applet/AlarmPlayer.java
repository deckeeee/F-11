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
package org.F11.scada.xwife.applet;

/**
 * �x�� �Đ��E��~�̃C���^�[�t�F�C�X�ł�
 * @author hori
 */
public interface AlarmPlayer {
	/**
	 * �w�肳�ꂽURL�̉��������[�v�Đ����܂�
	 * @param path
	 */
	public void playAlarm(String path);

	/**
	 * �Đ����̉����~���܂�
	 */
	public void stopAlarm();

	/**
	 * �ݒ肳��Ă���Đ��֎~���[�h�̏�Ԃ�Ԃ��܂��B
	 * @return �Đ��֎~���[�h�Ȃ� true�A�����łȂ��Ȃ� false
	 */
	public boolean isAlarmSoundLock();
	
	/**
	 * �Đ��֎~���[�h��ݒ肵�܂��B
	 * @param isDisplayLock �Đ��֎~����ꍇ�� true�A�����łȂ��ꍇ�� false
	 */
	public void setAlarmSoundLock(boolean isAlarmSoundLock);
}
