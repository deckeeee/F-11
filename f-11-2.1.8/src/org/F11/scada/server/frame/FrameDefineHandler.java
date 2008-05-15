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
package org.F11.scada.server.frame;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.F11.scada.parser.tree.TreeDefine;
import org.F11.scada.xwife.applet.Session;

/**
 * ��ʒ�`�̃n���h���E�����[�g�C���^�[�t�F�C�X�ł��B
 * ��`��RMI���o�R���ēǂ݂Ƃ�܂��B
 */
public interface FrameDefineHandler extends Remote {
	/**
	 * key�Ŏw�肳�ꂽ�����ȍ~�Ƀy�[�W��`���ύX����Ă���΁AXML�Œ�`��Ԃ��܂��B
	 * @param name �y�[�W��
	 * @param key �X�V����
	 * @param session �v���N���C�A���g�̃Z�b�V�������
	 * @return String �y�[�W��`��XML�\���B�ύX�������̓y�[�W�������̏ꍇnull
	 */
	public PageDefine getPage(String name, long key, Session session) throws RemoteException;

	/**
	 * key�Ŏw�肳�ꂽ�����ȍ~�ɃX�e�[�^�X�o�[��`���ύX����Ă���΁AXML�Œ�`��Ԃ��܂��B
	 * @param key �X�V����
	 * @return String �X�e�[�^�X�o�[��`��XML�\���B�ύX�����̏ꍇnull
	 */
	public PageDefine getStatusbar(long key) throws RemoteException;

	/**
	 * ���[�U�[���̃��j���[�c���[��Ԃ��܂��B �w�胆�[�U�[�Ƀ��j���[��`��������΁A�f�t�H���g�̃��j���[�c���[��Ԃ��܂��B
	 * @param user ���[�U�[��
	 * @return ���j���[�c���[�̒�`
	 * @throws RemoteException
	 */
	public TreeDefine getMenuTreeRoot(String user) throws RemoteException;
	
	/**
	 * �L���b�V���w�肳��Ă���y�[�W���̃��X�g��Ԃ��܂��B
	 * @return �L���b�V���w�肳��Ă���y�[�W���̃��X�g��Ԃ��܂��B
	 */
	List getCachePages() throws RemoteException;
}
