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

package org.F11.scada.server.io.nio.dbms;

/**
 * �eDBMS�p��SQL���`����C���^�[�t�F�C�X�ł��B
 * @author maekawa
 */
public interface Dbms {
	/**
	 * ���M���O�e�[�u������SQL��Ԃ��܂�
	 * @param tableName �e�[�u����
	 * @return �e�[�u������SQL��Ԃ��܂�
	 */
	String getCreateSql(String tableName);
	/**
	 * ���M���O�e�[�u���폜SQL��Ԃ��܂�
	 * @param tableName �e�[�u����
	 * @return �e�[�u���폜SQL��Ԃ��܂�
	 */
	String getDropSql(String tableName);
	/**
	 * ���M���O�e�[�u����������SQL��Ԃ��܂�
	 * @param tableName �e�[�u����
	 * @return ���M���O�e�[�u����������SQL��Ԃ��܂�
	 */
	String[] getCreateIndexSql(String tableName);
	/**
	 * ���R�[�h�}��SQL��Ԃ��܂�
	 * @param tableName �e�[�u����
	 * @return ���R�[�h�}��SQL��Ԃ��܂�
	 */
	String getInsertSql(String tableName);
	/**
	 * ���r�W�����擾SQL��Ԃ��܂��B
	 * @param tableName �e�[�u����
	 * @return ���r�W�����擾SQL��Ԃ��܂��B
	 */
	String getRevisionSql(String tableName);
	/**
	 * �ő�f�t�H���g�����Ń��R�[�h����������SQL��Ԃ��܂�
	 * @param tableName �e�[�u����
	 * @return �ő�f�t�H���g�����Ń��R�[�h����������SQL��Ԃ��܂�
	 */
	String getSelectSql(String tableName);
	/**
	 * �w�茏���Ń��R�[�h����������SQL��Ԃ��܂�
	 * @param tableName �e�[�u����
	 * @param limit �ő僌�R�[�h��
	 * @return �w�茏���Ń��R�[�h����������SQL��Ԃ��܂�
	 */
	String getSelectHolderIdSql(String tableName, int limit);
	/**
	 * �����Ń��R�[�h����������SQL��Ԃ��܂�
	 * @param tableName �e�[�u����
	 * @return �����Ń��R�[�h����������SQL��Ԃ��܂�
	 */
	String getSelectTimeSql(String tableName);

	/**
	 * �ŌÃ��R�[�h����������SQL��Ԃ��܂�
	 * @param tableName �e�[�u����
	 * @return �ŌÃ��R�[�h����������SQL��Ԃ��܂�
	 */
	String getSelectFirstSql(String tableName);

	/**
	 * �ŐV���R�[�h����������SQL��Ԃ��܂�
	 * @param tableName �e�[�u����
	 * @return �ŐV���R�[�h����������SQL��Ԃ��܂�
	 */
	String getSelectLastSql(String tableName);

	/**
	 * ���������ȏ�V�������R�[�h�𒊏o����SQL��Ԃ��܂�
	 * @param tableName �e�[�u����
	 * @param limit ���R�[�h����
	 * @return ���������ȏ�V�������R�[�h�𒊏o����SQL��Ԃ��܂�
	 */
	String getSelectAfterSql(String tableName, int limit);

	/**
	 * �����������Â����R�[�h�𒊏o����SQL��Ԃ��܂�
	 * @param tableName �e�[�u����
	 * @param limit ���R�[�h����
	 * @return �����������Â����R�[�h�𒊏o����SQL��Ԃ��܂�
	 */
	String getSelectBeforeSql(String tableName, int limit);

	/**
	 * ���t�̊Ԃ𒊏o����SQL��Ԃ��܂�
	 * @param tableName �e�[�u����
	 * @return ���t�̊Ԃ𒊏o����SQL��Ԃ��܂�
	 */
	String getSelectBetweenTimeSql(String tableName);
}
