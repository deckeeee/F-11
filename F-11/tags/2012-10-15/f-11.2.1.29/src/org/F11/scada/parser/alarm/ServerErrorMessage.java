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

package org.F11.scada.parser.alarm;

/**
 * �T�[�o�[�R�l�N�V�����G���[���b�Z�[�W�N���X
 * ���b�Z�[�W���e�Ɖ����t�@�C���̃p�X��ێ����܂�
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ServerErrorMessage {
	/** �\�����b�Z�[�W */
	private String message;
	/** �����t�@�C���̃p�X */
	private String sound;
	
	/**
	 * �\�����b�Z�[�W��Ԃ��܂�
	 * @return �\�����b�Z�[�W
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * �\�����b�Z�[�W��ݒ肵�܂�
	 * @param string �\�����b�Z�[�W
	 */
	public void setMessage(String string) {
		message = string;
	}

	/**
	 * �����t�@�C���̃p�X��Ԃ��܂�
	 * @return �����t�@�C���̃p�X
	 */
	public String getSound() {
		return sound;
	}

	/**
	 * �����t�@�C���̃p�X��ݒ肵�܂�
	 * @param string �����t�@�C���̃p�X
	 */
	public void setSound(String string) {
		sound = string;
	}

}
