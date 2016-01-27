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

package org.F11.scada.server.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.F11.scada.Globals;

/**
 * Class��`�N���X�ł�
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ClassConfig {
	/** ���s����R�}���h�N���X�� */
	private String className;
	/** �����̃v���p�e�B */
	private Properties properties;
	
	/**
	 * ���s����R�}���h�N���X����Ԃ��܂�
	 * @return ���s����R�}���h�N���X��
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * ���s����R�}���h�N���X����ݒ肵�܂�
	 * @param string ���s����R�}���h�N���X��
	 */
	public void setClassName(String string) {
		className = string;
	}

	/**
	 * �����v���p�e�B��Ԃ��܂�
	 * @param key �v���p�e�B��
	 * @return �v���p�e�B�l
	 */
	public String getProperty(String key) {
		if (properties == null) {
			return Globals.NULL_STRING;
		}
		return properties.getProperty(key);
	}

	/**
	 * �v���p�e�B��ǉ����܂�
	 * @param key �v���p�e�B��
	 * @param value �v���p�e�B�l
	 */
	public void addProperty(String key, String value) {
		if (properties == null) {
			properties = new Properties();
		}
		properties.setProperty(key, value);
	}

	/**
	 * �v���p�e�B�̃}�b�v��Ԃ��܂�
	 * @return �v���p�e�B�̃}�b�v��Ԃ��܂�
	 */
	public Map getProperties() {
		return null == properties ? new HashMap() : Collections.unmodifiableMap(properties);
	}
}
