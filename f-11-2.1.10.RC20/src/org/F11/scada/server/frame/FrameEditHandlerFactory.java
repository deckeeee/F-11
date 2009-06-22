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

package org.F11.scada.server.frame;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Map;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.server.frame.editor.FrameEditHandler;

/**
 * FrameEditHandler �I�u�W�F�N�g�𐶐�����t�@�N�g���[�N���X�ł��B
 * Preperence.properties �́u/server/FrameEditHandler�v�Őݒ肵�܂��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FrameEditHandlerFactory {
	private final int port;
	private final FrameDefineManager frameDefineManager;
	private final Map taskMap;

	/**
	 * FrameEditHandlerFactory �I�u�W�F�N�g�t�@�N�g���[�N���X�����������܂�
	 * @param port RMI�I�u�W�F�N�g�]���|�[�g�ԍ�
	 * @param defineManager �y�[�W��`�}�l�[�W���[
	 * @param taskMap ���M���O�^�X�N�̃}�b�v
	 */
	public FrameEditHandlerFactory(
			int port,
			FrameDefineManager defineManager,
			Map taskMap) {
		this.port = port;
		this.frameDefineManager = defineManager;
		this.taskMap = taskMap;
	}

	/**
	 * �ݒ肵�� FrameEditHandler �I�u�W�F�N�g�𐶐����Ԃ��܂��B
	 * @return �ݒ肵�� FrameEditHandler �I�u�W�F�N�g�𐶐����Ԃ��܂��B
	 * @throws RemoteException RMI���W�X�g���o�^�ŃG���[
	 * @throws MalformedURLException RMI���W�X�g���o�^�ŃG���[
	 */
	public FrameEditHandler createFrameEditHandler()
			throws RemoteException, MalformedURLException {

		FrameEditHandler handler = null;

		String editmode = EnvironmentManager.get("/server/FrameEditHandler", "");
		if ("XmlFrameDefineManager".equals(editmode)) {
			handler = new XmlFrameEditManager(port, taskMap);
		} else {
			handler = new FrameEditManager(port, frameDefineManager, taskMap);
		}
		
		return handler;
	}
}
