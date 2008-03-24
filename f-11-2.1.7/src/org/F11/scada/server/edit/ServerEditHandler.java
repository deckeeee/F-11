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
package org.F11.scada.server.edit;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * �����󎚐ݒ�p�����[�^�̕ύX���󂯎��T�[�o�[�C���^�[�t�F�C�X�ł�
 * @author hori
 */
public interface ServerEditHandler extends Remote {
	/**
	 * �����󎚐ݒ�p�����[�^�̕ύX���T�[�o�[�ɒʒm���܂��B
	 */
	public void editAutoPrint() throws RemoteException;

	/**
	 * ��������̃T�[�o�[���̂�Ԃ��܂��B
	 * @return ��������̃T�[�o�[����
	 */
	public String getServerName() throws RemoteException;
}
