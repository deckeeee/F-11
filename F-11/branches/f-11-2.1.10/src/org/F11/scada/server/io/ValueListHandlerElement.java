/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/ValueListHandlerElement.java,v 1.3 2003/10/15 08:36:51 frdm Exp $
 * $Revision: 1.3 $
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

import java.sql.Timestamp;
import java.util.Map;
import java.util.SortedMap;

import org.F11.scada.server.event.LoggingDataListener;

/**
 * ���M���O�f�[�^�̃n���h���G�������g�̃C���^�[�t�F�C�X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface ValueListHandlerElement extends LoggingDataListener {
	/**
	 * ���R�[�h�I�u�W�F�N�g��Ԃ��܂��B
	 * @return ���R�[�h�I�u�W�F�N�g
	 */
	public Object next();

	/**
	 * ���̃��R�[�h�I�u�W�F�N�g�����݂��鎞�́Atrue ��Ԃ��܂��B
	 * @return ���̃��R�[�h�I�u�W�F�N�g�����݂��鎞�́Atrue �������łȂ��ꍇ�� false ��Ԃ��܂��B
	 */
	public boolean hasNext();

	/**
	 * �ŏ��̃^�C���X�^���v��Ԃ��܂��B
	 */
	public Object firstKey();

	/**
	 * �Ō�̃^�C���X�^���v��Ԃ��܂��B
	 */
	public Object lastKey();

	/**
	 * �^�C���X�^���v������ key �ȑO�̃��R�[�h���������A�|�C���^���ʒu�Â��܂��B
	 * ���̃��\�b�h�ňʒu�Â���ꂽ�|�C���^�́A key �ȑO�̃��R�[�h���P�܂݂܂��B
	 * �A���Akey ���擪���R�[�h�ȑO�̃��R�[�h�������ꍇ�́A�擪���R�[�h����ɂȂ�܂��B
	 * @param key �������郌�R�[�h�̃^�C���X�^���v
	 */
	public void findRecord(Timestamp key);
	
	/**
	 * key�Ŏw�肳�ꂽ�����ȍ~�̃��M���O�f�[�^��Map�C���X�^���X�ŕԂ��܂��B
	 * @param key �X�V����
	 */
	public Map getUpdateLoggingData(Timestamp key);
	
	/**
	 * �ێ����Ă���f�[�^���X�V���ꂽ��ɒʒm������ǉ����܂��B
	 * @param listener
	 */
	public void addLoggingDataListener(LoggingDataListener listener);
	
	/**
	 * �ʒm����폜���܂��B
	 * @param listener
	 */
	public void removeLoggingDataListener(LoggingDataListener listener);

	/**
	 * �������p�f�[�^��SortedMap��Ԃ��܂��B
	 * @return �������p�f�[�^��SortedMap��Ԃ��܂��B
	 */
	public SortedMap getInitialData();
}
