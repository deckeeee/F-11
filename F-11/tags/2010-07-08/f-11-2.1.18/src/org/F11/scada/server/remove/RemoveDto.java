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

package org.F11.scada.server.remove;


public class RemoveDto {
	/** �폜�Ώۃe�[�u�� */
	private String tableName;
	/** �폜�Ώۓ����t�B�[���h�� */
	private String dateFieldName;
	/** �폜����l */
	private int removeValue;

	/**
	 * �폜�Ώۓ����t�B�[���h����Ԃ��܂�
	 * @return �폜�Ώۓ����t�B�[���h����Ԃ��܂�
	 */
	public String getDateFieldName() {
		return dateFieldName;
	}
	/**
	 * �폜�Ώۓ����t�B�[���h����ݒ肵�܂�
	 * @param dateFieldName �폜�Ώۓ����t�B�[���h����ݒ肵�܂�
	 */
	public void setDateFieldName(String dateFieldName) {
		this.dateFieldName = dateFieldName;
	}
	/**
	 * �폜����l��Ԃ��܂�
	 * @return �폜����l��Ԃ��܂�
	 */
	public int getRemoveValue() {
		return removeValue;
	}
	/**
	 * �폜����l��ݒ肵�܂�
	 * @param removePeriod �폜����l��ݒ肵�܂�
	 */
	public void setRemoveValue(int removePeriod) {
		this.removeValue = removePeriod;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String toString() {
		return "tableName=" + tableName + ", dateFieldName=" + dateFieldName
				+ ", removePeriod=" + removeValue;
	}
}
