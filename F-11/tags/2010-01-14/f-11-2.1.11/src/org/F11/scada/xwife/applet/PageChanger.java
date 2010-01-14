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
 * �y�[�W�؊��̃C���^�[�t�F�C�X�ł��B
 * @author hori <hori@users.sourceforge.jp>
 */
public interface PageChanger {
	
	/**
	 * �w��L�[�̃y�[�W��\�����܂��B
	 * @param pageChange �y�[�W�؊��C�x���g
	 */
	public void changePage(PageChangeEvent pageChange);

	/**
	 * �ݒ肳��Ă����ʃ��b�N���[�h�̏�Ԃ�Ԃ��܂��B
	 * @return ���b�N���[�h�Ȃ� true�A�����łȂ��Ȃ� false
	 */
	public boolean isDisplayLock();
	
	/**
	 * ��ʃ��b�N���[�h��ݒ肵�܂��B
	 * @param isDisplayLock ��ʃ��b�N����ꍇ�� true�A�����łȂ��ꍇ�� false
	 */
	public void setDisplayLock(boolean isDisplayLock);

	/**
	 * Robot�N���X�ŃV�t�g�L�[����������B
	 *
	 */
	void pressShiftKey();

	/**
	 * �x�񉹂�炵�܂��B
	 * @param soundPath �����t�@�C��
	 */
	void playAlarm(String soundPath);
	
	/**
	 * �x�񉹂��~���܂��B
	 */
	void stopAlarm();
}
