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
package org.F11.scada.server.io;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.SortedMap;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public interface SelectiveAllDataValueListHandler extends SelectiveValueListHandler {

    /**
     * ��ԌÂ��f�[�^�̃^�C���X�^���v��Ԃ��܂��B
     * @param name �n���h����
	 * @param holderStrings ���o����f�[�^�z���_�[�̃��X�g
     * @return ��ԌÂ��f�[�^�̃^�C���X�^���v��Ԃ��܂��B
     * @throws RemoteException
     */
	Timestamp firstTime(String name, List holderStrings) throws RemoteException;

    /**
     * ��ԐV�����f�[�^�̃^�C���X�^���v��Ԃ��܂��B
     * @param name �n���h����
	 * @param holderStrings ���o����f�[�^�z���_�[�̃��X�g
     * @return ��ԐV�����f�[�^�̃^�C���X�^���v��Ԃ��܂��B
     * @throws RemoteException
     */
	Timestamp lastTime(String name, List holderStrings) throws RemoteException;
	
	
	/**
	 * �w��͈͂̃��M���O�f�[�^��Ԃ��܂��B
	 * @param name �n���h����
	 * @param holderStrings ���o����f�[�^�z���_�[�̃��X�g
	 * @param start ���o�J�n����
	 * @param limit �ő僌�R�[�h����
	 * @return �w��͈͂̃��M���O�f�[�^��Ԃ��܂��B
     * @throws RemoteException
	 */
	SortedMap getLoggingData(String name, List holderStrings, Timestamp start, int limit) throws RemoteException;
}
