/*
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

package org.F11.scada.server.io.nio;

import java.sql.Timestamp;
import java.util.List;

import org.F11.scada.data.LoggingRowData;

/**
 * LoggingData�̃��X�g��Ԃ��܂��B
 * @author maekawa
 */
public interface LogTableSelectService {
	/**
	 * LoggingData�̃��X�g��Ԃ��܂��B
	 * @param tableName �e�[�u����
	 * @param dataHolders ���o����f�[�^�z���_�[�̃��X�g
	 * @param limit ���R�[�h�����̍ő�l
	 * @return LoggingData�̃��X�g��Ԃ��܂��B
	 */
	List select(String tableName, List dataHolders, int limit);

	/**
	 * �����Ŏw�肵���������V�������R�[�h��LoggingData�̃��X�g��Ԃ��܂��B
	 * @param tableName �e�[�u����
	 * @param dataHolders ���o����f�[�^�z���_�[�̃��X�g
	 * @param time ���o����f�[�^�z���_�[�̌�����������
	 * @return �����Ŏw�肵���������V�������R�[�h��LoggingData�̃��X�g��Ԃ��܂��B
	 */
	List select(String tableName, List dataHolders, Timestamp time);

	/**
	 * �e�[�u�����̍ŌÃ��R�[�h��Ԃ��܂��B
	 * @param tableName �e�[�u����
	 * @param dataHolders ���o����f�[�^�z���_�[�̃��X�g
	 * @return �e�[�u�����̍ŌÃ��R�[�h��Ԃ��܂��B
	 */
	LoggingRowData selectFirst(String tableName, List dataHolders);

	/**
	 * �e�[�u�����̍ŐV���R�[�h��Ԃ��܂��B
	 * @param tableName �e�[�u����
	 * @param dataHolders ���o����f�[�^�z���_�[�̃��X�g
	 * @return �e�[�u�����̍ŐV���R�[�h��Ԃ��܂��B
	 */
	LoggingRowData selectLast(String tableName, List dataHolders);
	/**
	 * �����̓����̑O��̃��R�[�h�𒊏o���ĕԂ��܂�
	 * @param name �e�[�u����
	 * @param dataHolders ���o����f�[�^�z���_�[�̃��X�g
	 * @param start ����
	 * @param limit �ő匏��
	 * @return �����̓����̑O��̃��R�[�h�𒊏o���ĕԂ��܂�
	 */
	List selectBeforeAfter(String name, List dataHolders, Timestamp start, int limit);

	/**
	 * �����Ŏw�肵��������CSV������̃��X�g��Ԃ��܂��B
	 * @param tableName �e�[�u����
	 * @param dataHolders ���o����f�[�^�z���_�[�̃��X�g
	 * @param startTime ���o����f�[�^�z���_�[�̌�����������
	 * @param endTime ���o����f�[�^�z���_�[�̌�����������
	 * @return �����Ŏw�肵���������V�������R�[�h��LoggingData�̃��X�g��Ԃ��܂��B
	 */
	List select(String tableName, List dataHolders, Timestamp startTime, Timestamp endTime);
}