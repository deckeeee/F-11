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

package org.F11.scada.tool.conf.io;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * �^�X�N��Bean�N���X�ł��B
 * 
 * @author maekawa
 * 
 */
public class TimeSetTaskBean {
	/** �^�X�N�̃v���p�e�B */
	private Map<String, String> attribute = new HashMap<String, String>();
	/** �����̃��X�g */
	private List<TimeSetBean> timeList;

	/**
	 * �v���p�e�B��Ԃ��܂��B
	 * 
	 * @param key �v���p�e�B��
	 * @return �v���p�e�B��Ԃ��܂��B
	 */
	public String get(String key) {
		return attribute.get(key);
	}

	/**
	 * �v���p�e�B��ݒ肵�܂��B
	 * 
	 * @param key �v���p�e�B��
	 * @param value �v���p�e�B�l
	 * @return �ݒ�ȑO�̃v���p�e�B�l
	 */
	public String put(String key, String value) {
		return attribute.put(key, value);
	}

	/**
	 * �w��v���p�e�B������� true ���Ȃ���� false ��Ԃ��܂��B
	 * 
	 * @param key �v���p�e�B��
	 * @return �w��v���p�e�B������� true ���Ȃ���� false ��Ԃ��܂��B
	 */
	public boolean containsKey(String key) {
		return attribute.containsKey(key);
	}

	/**
	 * �^�X�N�����̃��X�g��Ԃ��܂��B
	 * 
	 * @return �����̃��X�g��Ԃ��܂��B
	 */
	public List<TimeSetBean> getTimeList() {
		return timeList;
	}

	/**
	 * �^�X�N�����̃��X�g��ݒ肵�܂��B
	 * 
	 * @param timeList �^�X�N�����̃��X�g
	 */
	public void setTimeList(List<TimeSetBean> timeList) {
		this.timeList = timeList;
	}

	@Override
	public String toString() {
		return "attribute=" + attribute.toString() + " ,timeList=" + timeList;
	}
}