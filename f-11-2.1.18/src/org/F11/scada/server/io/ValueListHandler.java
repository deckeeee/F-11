/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/ValueListHandler.java,v 1.7 2003/10/15 08:36:51 frdm Exp $
 * $Revision: 1.7 $
 * $Date: 2003/10/15 08:36:51 $
 * 
 * =============================================================================
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

package org.F11.scada.server.io;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.SortedMap;

/**
 * ���M���O�f�[�^�̃n���h���E�����[�g�C���^�[�t�F�C�X�ł��B
 * ��`���ꂽ���M���O�f�[�^��RMI���o�R���āA�f�[�^�X�g���[�W���ǂ݂Ƃ�܂��B
 */
public interface ValueListHandler extends Remote {
	/**
	 * ���R�[�h�I�u�W�F�N�g��Ԃ��܂��B
	 * @param name �n���h����
	 * @return ���R�[�h�I�u�W�F�N�g
	 */
	public Object next(String name) throws RemoteException;

	/**
	 * ���̃��R�[�h�I�u�W�F�N�g�����݂��鎞�́Atrue ��Ԃ��܂��B
	 * @param name �n���h����
	 * @return ���̃��R�[�h�I�u�W�F�N�g�����݂��鎞�́Atrue �������łȂ��ꍇ�� false ��Ԃ��܂��B
	 */
	public boolean hasNext(String name) throws RemoteException;

	/**
	 * �ŏ��̃^�C���X�^���v��Ԃ��܂��B
	 * @param name �n���h����
	 */
	public Object firstKey(String name) throws RemoteException;

	/**
	 * �Ō�̃^�C���X�^���v��Ԃ��܂��B
	 * @param name �n���h����
	 */
	public Object lastKey(String name) throws RemoteException;

	/**
	 * �^�C���X�^���v������ key �ȑO�̃��R�[�h���������A�|�C���^���ʒu�Â��܂��B
	 * ���̃��\�b�h�ňʒu�Â���ꂽ�|�C���^�́A key �ȑO�̃��R�[�h���P�܂݂܂��B
	 * �A���Akey ���擪���R�[�h�ȑO�̃��R�[�h�������ꍇ�́A�擪���R�[�h����ɂȂ�܂��B
	 * @param name �n���h����
	 * @param key �������郌�R�[�h�̃^�C���X�^���v
	 */
	public void findRecord(String name, Timestamp key) throws RemoteException;
	
	/**
	 * key�Ŏw�肳�ꂽ�����ȍ~�̃��M���O�f�[�^��Map�C���X�^���X�ŕԂ��܂��B
	 * @param name �n���h����
	 * @param key �X�V����
	 */
	public Map getUpdateLoggingData(String name, Timestamp key) throws RemoteException;
	
	/**
	 * �������p�f�[�^��SortedMap��Ԃ��܂��B
	 * @param name �n���h����
	 * @return �������p�f�[�^��SortedMap��Ԃ��܂��B
	 */
	public SortedMap getInitialData(String name) throws RemoteException;

	/**
	 * �����̃n���h����Ԃ��܂��B
	 * 
	 * @param name �n���h����
	 * @return �����̃n���h����Ԃ��܂��B
	 */
	ValueListHandlerElement getValueListHandlerElement(String name) throws RemoteException;

}
