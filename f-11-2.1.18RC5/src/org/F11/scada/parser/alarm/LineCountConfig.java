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

package org.F11.scada.parser.alarm;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * �s������ێ�����ݒ�JavaBean�N���X�ł��B
 * 
 * @author hori <hoti@users.sourceforge.jp>
 */
public final class LineCountConfig {
	/** �ʏ�̍s�� */
	private int value;
	/** �k�����̍s�� */
	private int min;

	public LineCountConfig() {
		this(0, 0);
	}
	public LineCountConfig(int value, int min) {
		this.value = value;
		this.min = min;
	}
	/**
	 * �ʏ�̍s����Ԃ��܂��B
	 * 
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * �ʏ�̍s����ݒ肵�܂��B
	 * 
	 * @return
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * �k�����̍s����Ԃ��܂��B
	 * 
	 * @return
	 */
	public int getMin() {
		return min;
	}

	/**
	 * �k�����̍s����ݒ肵�܂��B
	 * 
	 * @return
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * ���̃I�u�W�F�N�g�̕��������Ԃ��܂��B jakarta commons Lang, ToStringBuilder�̎����Ɉˑ����Ă��܂��B
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
