/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.F11.scada.server.communicater;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.F11.scada.WifeException;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.event.WifeEventListener;

/**
 * �O���@��Ƃ̒ʐM�C���^�[�t�F�[�X�ł��B
 */
public interface Communicater {
	/**
	 * ����Communicater����܂��B
	 */
	public void close() throws InterruptedException;

	/**
	 * �Ǎ��ݒʐM�R�}���h�̒ǉ��B�i�C�ӎ����j ���񓯊��Ǎ��݂��œK������ׁA�\�ߓo�^���Ă����܂��B
	 * @param commands
	 */
	public void addReadCommand(Collection<WifeCommand> commands);

	/**
	 * �Ǎ��ݒʐM�R�}���h�̍폜�B�i�C�ӎ����j
	 * @param commands
	 */
	public void removeReadCommand(Collection<WifeCommand> commands);

	/**
	 * �ʐM�C�x���g���󂯎�郊�X�i�[��ǉ����܂��B�i�C�ӎ����j
	 * @param l ���X�i�[�I�u�W�F�N�g
	 */
	public void addWifeEventListener(WifeEventListener l);

	/**
	 * �ʐM�C�x���g���󂯎�郊�X�i�[���폜���܂��B�i�C�ӎ����j
	 * @param l ���X�i�[�I�u�W�F�N�g
	 */
	public void removeWifeEventListener(WifeEventListener l);

	/**
	 * �����Ǎ��݁B�Ǎ��ݒʐM����������܂Ńu���b�N���܂��BsameDataBalk �� true �ɂ����ꍇ�Ɠ��l�ł��B
	 * @param commands WifeCommand�̃R���N�V����
	 * @return �R�}���h�I�u�W�F�N�g(WifeCommand)�ƃo�C�g�z��̃}�b�v��Ԃ��܂��B
	 */
	public Map<WifeCommand, byte[]> syncRead(Collection<WifeCommand> commands)
			throws InterruptedException, IOException, WifeException;

	/**
	 * �����Ǎ��݁B�Ǎ��ݒʐM����������܂Ńu���b�N���܂��B
	 * @param commands WifeCommand�̃R���N�V����
	 * @param sameDataBalk true �̏ꍇ�A�O��ʐM���ʂ������ꍇ��̃}�b�v��Ԃ��܂��Bfalse �̏ꍇ�͏�ɒʐM���ʂ�Ԃ��܂��B
	 * @return �R�}���h�I�u�W�F�N�g(WifeCommand)�ƃo�C�g�z��̃}�b�v��Ԃ��܂��B
	 */
	public Map<WifeCommand, byte[]> syncRead(Collection<WifeCommand> commands, boolean sameDataBalk)
			throws InterruptedException, IOException, WifeException;

	/**
	 * ���������݁B�����ݒʐM����������܂Ńu���b�N���܂��B
	 * @param commands WifeCommand���L�[�ɂ���byte[]��Map
	 */
	public void syncWrite(Map<WifeCommand, byte[]> commands)
			throws InterruptedException, IOException, WifeException;
}
