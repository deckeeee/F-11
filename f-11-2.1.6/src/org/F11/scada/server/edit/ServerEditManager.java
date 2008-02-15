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

package org.F11.scada.server.edit;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.autoprint.AutoPrintEditor;

/**
 * ��������ҏW�@�\�����I�u�W�F�N�g�ɁA�ύX�C�x���g�𔭐�������N���X�ł�
 * @author hori
 */
public class ServerEditManager extends UnicastRemoteObject
		implements ServerEditHandler {

	private static final long serialVersionUID = 944970692114214286L;
	/** ��������G�f�B�^�[�I�u�W�F�N�g */
	private AutoPrintEditor editor;

	/**
	 * ��������̕ύX�C�x���g���Ǘ�����}�l�[�W���[�����������܂�
	 * @param port RMI�g�p�|�[�g
	 * @param editor ��������G�f�B�^�[�I�u�W�F�N�g
	 * @throws RemoteException �����[�g�G���[������
	 * @throws MalformedURLException �����[�g�G���[������
	 */
	public ServerEditManager(int port, AutoPrintEditor editor)
			throws RemoteException, MalformedURLException {

		super(port);
		Naming.rebind(WifeUtilities.createRmiServerEditManager(), this);
		this.editor = editor;
	}

	/**
	 * �����󎚐ݒ�p�����[�^�̕ύX���T�[�o�[�ɒʒm���܂�
	 * @exception RemoteException �����[�g�G���[������
	 */
	public void editAutoPrint() throws RemoteException {
		editor.reloadAutoPrint();
	}

	/**
	 * ��������̃T�[�o�[���̂�Ԃ��܂��B
	 * @return ��������̃T�[�o�[����
	 */
	public String getServerName() throws RemoteException {
		return editor.getServerName();
	}
}
