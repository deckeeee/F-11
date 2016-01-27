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

package org.F11.scada.server.converter;

import java.nio.ByteBuffer;

import org.F11.scada.WifeException;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.event.WifeCommand;

/**
 * Wife�ʐM���W���[���̃R�}���h�����C���^�[�t�F�C�X�B
 */
public interface Converter {
	/** ����ݒ肵�A���X�|���X�w�b�_��Ԃ��B*/
	public byte[] setEnvironment(Environment device);

	/** �Ǎ��݃R�}���h��ݒ肷��B*/
	public void setReadCommand(WifeCommand commdef) throws WifeException;
	/** �����݃R�}���h��ݒ肷��B*/
	public void setWriteCommand(WifeCommand commdef, byte[] data)
		throws WifeException;
	/** �R�}���h���擾�\���H */
	public boolean hasCommand();
	/** �R�}���h���쐬���A����̃R�}���h���������܂��B */
	public void nextCommand(ByteBuffer sendBuffer);
	/** �O����s�R�}���h���쐬���܂��B */
	public void retryCommand(ByteBuffer sendBuffer);

	/** ���M�f�[�^�Ǝ�M�f�[�^�̐��������������܂��B */
	public WifeException checkCommandResponce(ByteBuffer recvBuffer)
		throws WifeException;
	/** ��M�f�[�^����f�[�^�����擾���܂��B */
	public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData);
	/** �ʐM���̍ő咷��Ԃ��܂��B */
	public int getPacketMaxSize(WifeCommand commdef);
}
