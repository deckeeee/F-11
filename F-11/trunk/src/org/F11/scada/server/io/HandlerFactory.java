package org.F11.scada.server.io;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import org.F11.scada.server.event.LoggingDataListener;

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

/**
 * �f�[�^�n���h���[�̃t�@�N�g���[�N���X�ł��B
 * getHandlerFactory ���\�b�h�ŁA�w��̃t�@�N�g���[�I�u�W�F�N�g�𐶐����܂��B
 * @see#getHandlerFactory()
 * <!-- Abstract Factory Pattern ���g�p���Ă��܂��B -->
 */
public abstract class HandlerFactory {
	/**
	 * �����Ŏw�肵���N���X���́A�t�@�N�g���[�I�u�W�F�N�g�𐶐����܂��B
	 * @param className �t�@�N�g���[�I�u�W�F�N�g��
	 * @return �������ꂽ�t�@�N�g���[�I�u�W�F�N�g�̃C���X�^���X
	 */
	public static HandlerFactory getHandlerFactory(String className) {
		HandlerFactory factory = null;
		try {
			factory = (HandlerFactory) Class.forName(className).newInstance();
		} catch (ClassNotFoundException ex) {
			System.out.println("�N���X " + className + " ��������܂���B");
		} catch (Exception ex2) {
			ex2.printStackTrace();
		}
		return factory;
	}

	/**
	 * �f�[�^�X�V�p�f�[�^�n���h����Ԃ��t�@�N�g���[���\�b�h�ł��B�T�u�N���X�Ŏ������Ă��������B
	 * @param device �f�o�C�X��(�ʏ�̓e�[�u�����A�t�@�C����)
	 * @return �f�[�^�X�V�p�f�[�^�n���h��
	 * @exception SQLException DBMS�R�l�N�g�����s�����Ƃ��X���[����܂��B
	 */
	public abstract LoggingDataListener createStoreHandler(String device) throws SQLException;

	/**
	 * �N���C�A���g�n���h���C���^�[�t�F�C�X�I�u�W�F�N�g��Ԃ��t�@�N�g���[���\�b�h�ł��B�T�u�N���X�Ŏ������Ă��������B
	 * @param device �f�o�C�X��(�ʏ�̓e�[�u�����A�t�@�C����)
	 * @param dataHolders �f�[�^�z���_�[�̃��X�g
	 * @return �N���C�A���g�n���h���C���^�[�t�F�C�X�I�u�W�F�N�g��Ԃ��t�@�N�g���[
	 * @exception MalformedURLException ���O���K�؂Ȍ`���� URL �łȂ��ꍇ
	 * @exception RemoteException RMI���W�X�g���ɐڑ��ł��Ȃ��ꍇ
	 */
	public abstract ValueListHandlerElement createValueListHandler(
			String device,
			List dataHolders)
			throws MalformedURLException, RemoteException, SQLException;

	/**
	 * �O���t�p�̃Z���N�g�n���h����Ԃ��܂��B
	 * @param device �e�[�u����
	 * @return �O���t�p�̃Z���N�g�n���h����Ԃ��܂��B
	 */
	public abstract SelectiveValueListHandlerElement createSelectviveHandler(String device);

	/**
	 * �O���t�p�̑S�f�[�^�n���h����Ԃ��܂��B
	 * @param device �e�[�u����
	 * @return �O���t�p�̑S�f�[�^�n���h����Ԃ��܂��B
	 */
	public abstract SelectiveAllDataValueListHandlerElement createAllDataSelectviveHandler(String device);
}
