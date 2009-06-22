package org.F11.scada.security;

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

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.F11.scada.security.auth.Subject;

/**
 * WIFE �̃A�N�Z�X���䔻��N���X�́A�����[�g���\�b�h�Ăяo���C���^�[�t�F�C�X�ł��B
 * �T�u�N���X�Ń��\�[�X�̃A�N�Z�X�����F�ƁA���[�U�[�F�؂��������܂��B
 */
public interface AccessControlable extends Remote {
	/*
	 * ���\�[�X�̃A�N�Z�X�����F���s���܂��B
	 * �w�肵�� Subject �� permission �Ŏw�肳�ꂽ�A�N�Z�X���A������Ă��邩���肵�܂��B
	 * @param subject Subject
	 * @param permission �A�N�Z�X��
	 * @return ������Ă���ꍇ�� true�A�����łȂ��ꍇ�� false ��Ԃ��܂��B
	 * @throws RemoteException RMI �Ăяo���Ɏ��s�����ꍇ
	public boolean checkPermission(Subject subject, WifePermission permission)
			throws RemoteException;
	 */

	/**
	 * �S�Ă̕ҏW�\�V���{�����\�[�X�̃A�N�Z�X�����F���s���܂��B
	 * �w�肵�� Subject �� destinations �Ŏw�肳�ꂽ�A�N�Z�X���A������Ă��邩���肵�܂��B
	 * @param subject Subject
	 * @param destinations �ҏW�\�V���{�����ێ����Ă���f�[�^�z���_�[���̔z��
	 * @return ������Ă���ꍇ�� true�A�����łȂ��ꍇ�� false �� Boolean�z��̃��X�g��Ԃ��܂��B
	 * @throws RemoteException RMI �Ăяo���Ɏ��s�����ꍇ
	 */
	public List checkPermission(Subject subject, String[][] destinations)
			throws RemoteException;

	/**
	 * ���[�U�[�̔F�؂��s���܂��B
	 * �F�؂����������ꍇ�A�F�؂������[�U�[�̃v�����V�p�����֘A�Â��� Subject ��Ԃ��܂��B
	 * �F�؂Ɏ��s�����ꍇ�� null ��Ԃ��܂��B
	 * @param user �F�؂��郆�[�U�[��
	 * @param password �F�؂��郆�[�U�[�̈Í������ꂽ�p�X���[�h
	 * @return �F�؂����������ꍇ�A�F�؂������[�U�[�̃v�����V�p�����֘A�Â��� Subject ��Ԃ��܂��B
	 * �F�؂Ɏ��s�����ꍇ�� null ��Ԃ��܂��B
	 * @throws RemoteException RMI �Ăяo���Ɏ��s�����ꍇ
	 */
	public Subject checkAuthentication(String user, String password)
			throws RemoteException;
	
	/**
	 * �N���C�A���g��IP�A�h���X���烍�O�A�E�g���̃��[�U�����擾���܂��B
	 * @param local �N���C�A���g��IP�A�h���X
	 * @return ���O�A�E�g���̃��[�U��
	 * @throws RemoteException
	 */
	public String getLogoutUser(InetAddress local) throws RemoteException;
}
