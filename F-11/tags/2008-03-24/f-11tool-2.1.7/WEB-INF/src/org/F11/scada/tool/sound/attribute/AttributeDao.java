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

package org.F11.scada.tool.sound.attribute;

import java.util.List;

public interface AttributeDao {
	public static final Class BEAN = AttributeDto.class;

	/**
	 * �����e�[�u���̑S�Ẵ��R�[�h��AttributeDto�̃��X�g�ŕԂ��܂��B
	 * @return �����e�[�u���̑S�Ẵ��R�[�h��AttributeDto�̃��X�g�ŕԂ��܂��B
	 */
	List getAllAttribute();
	public static final String getAllAttribute_QUERY = "ORDER BY attribute";

	/**
	 * ����Dto�̑���ID�ő����e�[�u���̃��R�[�h��AttributeDto�ŕԂ��܂��B
	 * @param dto ����ID��ݒ肵��Dto
	 * @return �����e�[�u���̃��R�[�h��AttributeDto�ŕԂ��܂��B
	 */
	AttributeDto getAttribute(AttributeDto dto);
	
	public static final String updateAttribute_PERSISTENT_PROPS = "soundType";
	int updateAttribute(AttributeDto dto);
}
