/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.xwife.applet.alarm;

import org.apache.commons.configuration.Configuration;

/**
 * �x��ꗗ�̗���̃N���X�ł��B
 * 
 * @author maekawa
 * 
 */
public class AlarmColumn {
	public static final int UNIT_SIZE = 150;
	public static final int DEFAULT_SIZE = 78;
	public static final int DATE_SIZE = 141;
	public static final int ROW_HEADER_SIZE = 50;
	public static final String ROW_HEADER_FORMAT = "0000";
	private final Configuration configuration;

	public AlarmColumn(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * �@��L���̗�
	 * 
	 * @return �@��L���̗�
	 */
	public int getUnitSize() {
		return configuration.getInt(
			"org.F11.scada.xwife.applet.alarm.unitSize",
			UNIT_SIZE);
	}

	/**
	 * �����̗�
	 * 
	 * @return �����̗�
	 */
	public int getAttributeSize() {
		return configuration.getInt(
			"org.F11.scada.xwife.applet.alarm.attributeSize",
			DEFAULT_SIZE);
	}

	/**
	 * �x��E��Ԃ̗�
	 * 
	 * @return �x��E��Ԃ̗�
	 */
	public int getStatusSize() {
		return configuration.getInt(
			"org.F11.scada.xwife.applet.alarm.statusSize",
			DEFAULT_SIZE);
	}

	/**
	 * ��ʂ̗�
	 * 
	 * @return ��ʂ̗�
	 */
	public int getSortSize() {
		return configuration.getInt(
			"org.F11.scada.xwife.applet.alarm.sortSize",
			DEFAULT_SIZE);
	}

	/**
	 * �m�F���̗�
	 * 
	 * @return �m�F���̗�
	 */
	public int getCheckSize() {
		return DEFAULT_SIZE;
	}

	/**
	 * ������̕�
	 * 
	 * @return ������̕�
	 */
	public int getDateSize() {
		return configuration.getInt(
			"org.F11.scada.xwife.applet.alarm.dateSize",
			DATE_SIZE);
	}
	
	/**
	 * ����1, 2, 3�̕�
	 * 
	 * @return ����1, 2, 3�̕�
	 */
	public int getAttributeNSize() {
		return DEFAULT_SIZE;
	}
}
