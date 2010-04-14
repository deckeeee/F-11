package org.F11.scada.server.event;

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

import org.F11.scada.WifeException;

/**
 * Wife�V�X�e���C�x���g�I�u�W�F�N�g
 */
public class WifeEvent extends java.util.EventObject
{
	private static final long serialVersionUID = -6323589548514689083L;

	/** ���M�R�}���h�̃l�^�ł��B */
	private WifeCommand define;

	/** �Ǎ��݃R�}���h�̌��ʂ�����܂��B */
	private byte[] readData;
	/** �ʐM�G���[��Ԃł��B */
	private boolean errorFlg;
	/** �G���[�������̗�O�ł��B */
	private WifeException errorException;

	/**
	 * �R���X�g���N�^ WifeEvent
	 * @param source �C�x���g�̔�����
	 * @param oldValue �������̃C�x���g���e
	 * @param newValue �ʐM��̃C�x���g���e
	 */
	public WifeEvent(Object source, WifeCommand define)
	{
		super(source);
		this.define = define;
		this.readData = null;
		errorFlg = false;
		errorException = null;
	}

	/**
	 * ��������DeviceID��Ԃ��B
	 * @return ������DeviceID
	 */
	public String getDeviceID()
	{
		return define.getDeviceID();
	}

	/**
	 * �������̃C�x���g���e��Ԃ��B
	 * @return �������C�x���g���e
	 */
	public WifeCommand getDefine()
	{
		return define;
	}

	/**
	 * �ǂݍ��񂾂̃f�[�^��Ԃ��܂��B
	 * ���[�h���C�g��ʂ����C�g�̎���null��Ԃ��܂��B
	 */
	 public byte[] getReadData() {
		return readData;
	 }

	/**
	 * �ǂݍ��񂾃f�[�^��ݒ肵�܂��B
	 * ���̃��\�b�h�͒ʐM���W���[���Ŏg�p���܂��B
	 */
	 public void setReadData(byte [] readdata) {
		this.readData = readdata;
	 }

	 /**
	  * �ʐM�G���[�̃t���O��Ԃ�Ԃ��܂��B
	  */
	 public boolean isError() {
		return errorFlg;
	 }

	 /**
	  * �G���[�������̗�O��Ԃ��܂��B
	  */
	 public WifeException getErrorException() {
		return errorException;
	 }

	 /**
	  * �G���[�������̗�O��ݒ肵�܂��B
	  */
	 public void setErrorException(WifeException errorException) {
		errorFlg = true;
		this.errorException = errorException;
	 }

	/**
	 * �I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 */
	 public String toString() {
		return super.toString() + " " + define.toString();
	 }
}
