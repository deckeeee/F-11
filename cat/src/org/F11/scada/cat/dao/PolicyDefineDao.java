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

package org.F11.scada.cat.dao;

import java.util.List;

import org.F11.scada.cat.entity.PolicyDefineTable;

/**
 * ポリシーテーブルをアクセスするDaoインターフェイス
 * 
 * @author maekawa
 *
 */
public interface PolicyDefineDao {
	public static final Class BEAN = PolicyDefineTable.class;

	/**
	 * 指定された条件のポリシーをリストで返します。
	 * 
	 * @param dto 検索条件
	 * @return 指定された条件のポリシーをリストで返します。
	 */
	List<PolicyDefineTable> getPolicyDefines(PolicyDefineTable dto);
}
