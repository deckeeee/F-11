/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/GraphModel.java,v 1.6.6.1 2005/03/11 06:50:42 frdm Exp $
 * $Revision: 1.6.6.1 $
 * $Date: 2005/03/11 06:50:42 $
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

package org.F11.scada.applet.graph;

import java.beans.PropertyChangeListener;
import java.sql.Timestamp;

import org.F11.scada.Service;

/**
 * �O���t�f�[�^���f���̃C���^�[�t�F�C�X�ł��B
 */
public interface GraphModel extends Service {
	/**
	 * ���X�i�[��ǉ����܂��B
	 * @param l PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener l);

	/**
	 * ���X�i�[���폜���܂��B
	 * @param l PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener l);

	/**
	 * �w��̃^�X�N���玟�̃��R�[�h�I�u�W�F�N�g��Ԃ��܂��B
	 * @param name �^�X�N��
	 */	
	public Object get(String name);

	/**
	 * �w��̃^�X�N�Ɏ��̃��R�[�h�I�u�W�F�N�g������ꍇ�� true ��Ԃ��܂��B
	 * @param name �^�X�N��
	 */
	public boolean next(String name);

	/**
	 * �w��̃^�X�N����ŏ����R�[�h�̃^�C���X�^���v��Ԃ��܂��B
	 * @param name �^�X�N��
	 * @return �ŏ����R�[�h�̃^�C���X�^���v
	 */
	public Object firstKey(String name);

	/**
	 * �w��̃^�X�N����ŏI���R�[�h�̃^�C���X�^���v��Ԃ��܂��B
	 * @param name �^�X�N��
	 * @return �ŏI���R�[�h�̃^�C���X�^���v
	 */
	public Object lastKey(String name);

	/**
	 * �w��̃^�X�N����^�C���X�^���v������ key �ȑO�̃��R�[�h���������A�|�C���^���ʒu�Â��܂��B
	 * ���̃��\�b�h�ňʒu�Â���ꂽ�|�C���^�́A key �ȑO�̃��R�[�h���P�܂݂܂��B
	 * �A���Akey ���擪���R�[�h�ȑO�̃��R�[�h�������ꍇ�́A�擪���R�[�h����ɂȂ�܂��B
	 * @param name �^�X�N��
	 * @param key �������郌�R�[�h�̃^�C���X�^���v
	 */
	public void findRecord(String name, Timestamp key);
}
