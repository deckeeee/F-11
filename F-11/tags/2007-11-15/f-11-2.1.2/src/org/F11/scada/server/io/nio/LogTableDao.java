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

/**
 * ���M���O�e�[�u���𑀍삷��Dao�C���^�[�t�F�C�X�ł��B
 * @author maekawa
 */
public interface LogTableDao {
	/**
	 * �w�肵���e�[�u����LogDto�̃��X�g�̓��e���o�b�`�}�����܂��B
	 * @param tableName �e�[�u����
	 * @param dtos LogDto�̃��X�g
	 * @return �}���������R�[�h����
	 */
	int insert(String tableName, List dtos);
	/**
	 * ���r�W�������擾���܂��B
	 * @param tableName �e�[�u����
	 * @param time �C�x���g��������
	 * @param holderId �z���_ID
	 * @return ���r�W�������擾���܂��B
	 */
	int getRevision(String tableName, Timestamp time, String holderId);
	/**
	 * �w�肵�����O�e�[�u���̑S���R�[�h��Ԃ��܂��B
	 * @param tableName �e�[�u����
	 * @return �w�肵�����O�e�[�u���̑S���R�[�h��Ԃ��܂��B
	 */
	List select(String tableName);
	/**
	 * �w�肵�����O�e�[�u������A�����̃f�[�^�z���_�ƍő僌�R�[�h���Ń��R�[�h�𒊏o���܂��B
	 * @param tableName �e�[�u����
	 * @param holderId �f�[�^�z���_ID
	 * @param limit �ő僌�R�[�h��
	 * @return �w�肵�����O�e�[�u������A�����̃f�[�^�z���_�ƍő僌�R�[�h���Ń��R�[�h�𒊏o���܂��B
	 */
	List select(String tableName, String holderId, int limit);
	/**
	 * �����Ŏw�肵���������V�������R�[�h�̃��X�g��Ԃ��܂��B
	 * @param tableName �e�[�u����
	 * @param dataHolders ���o����f�[�^�z���_�[ID
	 * @param time ���o����f�[�^�z���_�[�̌�����������
	 * @return �����Ŏw�肵���������V�������R�[�h�̃��X�g��Ԃ��܂��B
	 */
	List select(String tableName, String holderId, Timestamp time);
	/**
	 * �e�[�u�����̍ŌÃ��R�[�h��Ԃ��܂��B
	 * @param tableName �e�[�u����
	 * @param holderId �f�[�^�z���_�[ID
	 * @return �e�[�u�����̍ŌÃ��R�[�h��Ԃ��܂��B
	 */
	List selectFirst(String tableName, String holderId);
	/**
	 * �e�[�u�����̍ŐV���R�[�h��Ԃ��܂��B
	 * @param tableName �e�[�u����
	 * @param holderId �f�[�^�z���_�[ID
	 * @return �e�[�u�����̍ŐV���R�[�h��Ԃ��܂��B
	 */
	List selectLast(String tableName, String holderId);

	/**
	 * �����������Â����R�[�h�𒊏o���܂�
	 * @param tableName �e�[�u����
	 * @param holderId �z���_ID
	 * @param start ����
	 * @param limit ���R�[�h����
	 * @return �����������Â����R�[�h�𒊏o���܂�
	 */
	List selectBefore(String tableName, String holderId, Timestamp start, int limit);

	/**
	 * ���������ȏ�V�������R�[�h�𒊏o���܂�
	 * @param tableName �e�[�u����
	 * @param holderId �z���_ID
	 * @param start ����
	 * @param limit ���R�[�h����
	 * @return ���������ȏ�V�������R�[�h�𒊏o���܂�
	 */
	List selectAfter(String tableName, String holderId, Timestamp start, int limit);

	/**
	 * �����Ŏw�肵�������Ń��R�[�h��LoggingData�̃��X�g��Ԃ��܂��B
	 * @param tableName �e�[�u����
	 * @param holderId ���o����f�[�^�z���_�[
	 * @param startTime ���o����f�[�^�z���_�[�̌�����������
	 * @param endTime ���o����f�[�^�z���_�[�̌�����������
	 * @return �����Ŏw�肵���������V�������R�[�h��LoggingData�̃��X�g��Ԃ��܂��B
	 */
	List select(String tableName, String holderId, Timestamp startTime, Timestamp endTime);
}
