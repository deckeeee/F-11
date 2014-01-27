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

package org.F11.scada.server.alarm.table;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * point_table �̓��e��\���N���X�ł��B
 * <ul>
 * <li>point �|�C���gID
 * <li>unit�@�|�C���g�L��
 * <li>name�@�|�C���g����
 * <li>mark�@�|�C���g�L���@�P�ʖ���
 * </ul>
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PointTableBean implements Serializable {
	/** �V���A���C�YID */
	private static final long serialVersionUID = -2234470409106572622L;
	/** �|�C���gID */
	private final int point;
	/** �|�C���g�L�� */
	private final String unit;
	/** �|�C���g���� */
	private final String name;
	/** �|�C���g�L���@�P�ʖ��� */
	private final String mark;

	/**
	 * ���̃I�u�W�F�N�g�����������܂�
	 * 
	 * @param point �|�C���gID
	 * @param unit �|�C���g�L��
	 * @param name �|�C���g����
	 * @param mark �|�C���g�L���@�P�ʖ���
	 */
	public PointTableBean(int point, String unit, String name, String mark) {
		this.point = point;
		this.unit = unit;
		this.name = name;
		this.mark = mark;
	}

	/**
	 * �|�C���g�L���P�ʖ��̂�Ԃ��܂��B
	 * @return �|�C���g�L���P�ʖ���
	 */
	public String getMark() {
		return mark;
	}

	/**
	 * �|�C���g���̂�Ԃ��܂��B
	 * @return �|�C���g����
	 */
	public String getName() {
		return name;
	}

	/**
	 * �|�C���gID��Ԃ��܂��B
	 * @return �|�C���gID
	 */
	public int getPoint() {
		return point;
	}

	/**
	 * �|�C���g�L����Ԃ��܂��B
	 * @return �|�C���g�L��
	 */
	public String getUnit() {
		return unit;
	}
	
	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new PointTableBean(point, unit, name, mark);
	}
	
	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 */
	public String toString() {
		return "{point="
			+ point
			+ ", unit="
			+ unit
			+ ", name="
			+ name
			+ ", mark="
			+ mark
			+ "}";
	}

}
