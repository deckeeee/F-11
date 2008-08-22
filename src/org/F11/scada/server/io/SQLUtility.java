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

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public interface SQLUtility {
	String getSelectAllString(String name, List dataHolder, int limit);

	String getSelectTimeString(String name, List dataHolder, Timestamp time);

	String getFirstData(String name, List dataHolder);

	String getLastData(String name, List dataHolder);

	/**
	 * �����̃^�C���X�^���v���O(�������܂܂Ȃ�)�̃f�[�^��Ԃ��܂�
	 * 
	 * @param name �e�[�u����
	 * @param data ���o�z���_�̃��X�g
	 * @param start ���̃^�C���X�^���v���O�̃f�[�^��Ԃ��܂�
	 * @param limit �ő匏��
	 * @return �����̃^�C���X�^���v���O(�������܂܂Ȃ�)�̃f�[�^��Ԃ��܂�
	 * @see #getSelectAfter(String, List, Timestamp, int)
	 */
	String getSelectBefore(
			String name,
			List dataHolder,
			Timestamp start,
			int limit);

	/**
	 * �����̃^�C���X�^���v�ȏ�(�������܂�)�̃f�[�^��Ԃ��܂�
	 * 
	 * @param name �e�[�u����
	 * @param data ���o�z���_�̃��X�g
	 * @param start ���̃^�C���X�^���v�ȏ�̃f�[�^��Ԃ��܂�
	 * @param limit �ő匏��
	 * @return �����̃^�C���X�^���v�ȏ�(�������܂�)�̃f�[�^��Ԃ��܂�
	 * @see #getSelectBefore(String, List, Timestamp, int)
	 */
	String getSelectAfter(
			String name,
			List dataHolder,
			Timestamp start,
			int limit);

	/**
	 * �����̃^�C���X�^���v���O(�������܂܂Ȃ�)�̃f�[�^��Ԃ��܂�
	 * 
	 * @param name �e�[�u����
	 * @param data ���o�z���_�̃��X�g
	 * @param start ���̃^�C���X�^���v���O�̃f�[�^��Ԃ��܂�
	 * @param limit �ő匏��
	 * @param tables �g�p����e�[�u����
	 * @return �����̃^�C���X�^���v���O(�������܂܂Ȃ�)�̃f�[�^��Ԃ��܂�
	 * @see #getSelectAfter(String, List, Timestamp, int)
	 */
	String getSelectBefore(
			String name,
			List dataHolder,
			Timestamp start,
			int limit,
			List<String> tables);

	/**
	 * �����̃^�C���X�^���v�ȏ�(�������܂�)�̃f�[�^��Ԃ��܂�
	 * 
	 * @param name �e�[�u����
	 * @param data ���o�z���_�̃��X�g
	 * @param start ���̃^�C���X�^���v�ȏ�̃f�[�^��Ԃ��܂�
	 * @param limit �ő匏��
	 * @param tables �g�p����e�[�u����
	 * @return �����̃^�C���X�^���v�ȏ�(�������܂�)�̃f�[�^��Ԃ��܂�
	 * @see #getSelectBefore(String, List, Timestamp, int)
	 */
	String getSelectAfter(
			String name,
			List dataHolder,
			Timestamp start,
			int limit,
			List<String> tables);

	String getSelectAllString(
			String name,
			List dataHolder,
			int limit,
			List<String> tables);

	String getSelectTimeString(
			String name,
			List dataHolder,
			Timestamp time,
			List<String> tables);

	String getFirstData(String name, List dataHolder, List<String> tables);

	String getLastData(String name, List dataHolder, List<String> tables);

	/**
	 * �����̃^�C���X�^���v�͈�(start�ȏ�,end����)�̃f�[�^��Ԃ��܂�
	 * @param name �e�[�u����
	 * @param data ���o�z���_�̃��X�g
	 * @param start ���̃^�C���X�^���v�ȏ�
	 * @param end ���̃^�C���X�^���v����
	 * @return �����̃^�C���X�^���v�͈͂̃f�[�^��Ԃ��܂�
	 * @see #getSelectBefore(String, List, Timestamp, Timestamp)
	 */
	String getSelectPeriod(String name, List dataHolder, Timestamp start,
			Timestamp end);
	String getSelectPeriod(String name, List dataHolder, Timestamp start,
			Timestamp end, List<String> tables);
}
