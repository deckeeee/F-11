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
	 * 属性テーブルの全てのレコードをAttributeDtoのリストで返します。
	 * @return 属性テーブルの全てのレコードをAttributeDtoのリストで返します。
	 */
	List getAllAttribute();
	public static final String getAllAttribute_QUERY = "ORDER BY attribute";

	/**
	 * 引数Dtoの属性IDで属性テーブルのレコードをAttributeDtoで返します。
	 * @param dto 属性IDを設定したDto
	 * @return 属性テーブルのレコードをAttributeDtoで返します。
	 */
	AttributeDto getAttribute(AttributeDto dto);
	
	public static final String updateAttribute_PERSISTENT_PROPS = "soundType";
	int updateAttribute(AttributeDto dto);
}
