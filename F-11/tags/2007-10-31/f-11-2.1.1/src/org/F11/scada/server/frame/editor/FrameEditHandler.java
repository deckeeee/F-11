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

package org.F11.scada.server.frame.editor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author hori
 */
public interface FrameEditHandler extends Remote {

	/**
	 * name�Ŏw�肳�ꂽ�y�[�W��`��XML�ŕԂ��܂��B
	 * @param name �y�[�W��
	 * @return String �y�[�W��`��XML�\���B�y�[�W�������̏ꍇnull
	 */
	public String getPageXml(String name) throws RemoteException; 

	/**
	 * name�Ŏw�肵���y�[�W��`��ݒ肵�܂��B
	 * @param name �y�[�W��
	 * @param xml �y�[�W��`
	 */
	public void setPageXml(String name, String xml) throws RemoteException;

	/**
	 * loggingName�Ŏw�肵�����M���O�t�@�C���ɕۑ�����鍀�ڂ̑������X�g��Ԃ��܂��B
	 * @param loggingName ���M���O�t�@�C����
	 * @return ���ڂ̑������X�g
	 */
	public List getLoggingHolders(String loggingName) throws RemoteException;
}
