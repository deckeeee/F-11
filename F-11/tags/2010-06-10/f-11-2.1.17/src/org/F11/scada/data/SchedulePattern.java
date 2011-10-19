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

package org.F11.scada.data;


/**
 * �X�P�W���[���p�^�[���̃C���^�[�t�F�C�X�ł��B
 */
public interface SchedulePattern {
	/**
	 * ���̃X�P�W���[���p�^�[���̑�����Ԃ��܂��B
	 */
	public int size();
	/**
	 * �����̍��ڂɂ�����C���f�b�N�X��Ԃ��܂��B
	 * @param ���ڂ̎��
	 */
	public int getDayIndex(int n);
	/**
	 * �����̍��ڂɂ�����C���f�b�N�X����Ԃ��܂��B
	 * @param ���ڂ̎��
	 */
	public String getDayIndexName(int n);
	/**
	 * �����Ŏw�肳�ꂽ������̃C���f�b�N�X��Ԃ��܂��B
	 * @param ������̎�ޔԍ�
	 */
	public int getSpecialDayOfIndex(int n);

	/**
	 * �X�P�W���[����ʂŏ���ɂ�����̌�(���������Ȃ�2, ����7���Ȃ�7�Ȃ�)��Ԃ��܂��B
	 * @return �X�P�W���[����ʂŏ���ɂ�����̌�(���������Ȃ�2, ����7���Ȃ�7�Ȃ�)��Ԃ��܂��B
	 */
	int getTopSize();
}