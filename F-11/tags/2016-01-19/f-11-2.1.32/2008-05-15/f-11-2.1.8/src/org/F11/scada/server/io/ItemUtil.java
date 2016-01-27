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

package org.F11.scada.server.io;

import java.util.Collection;
import java.util.Map;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.entity.MultiRecordDefine;
import org.F11.scada.server.register.HolderString;
import org.apache.commons.collections.primitives.DoubleList;

public interface ItemUtil {

	ConvertValue[] createConvertValue(Collection holders,
			String tablename);

	Item[] getItems(Collection holders, Map itemPool);

	Item getItem(HolderString dataHolder, Map itemPool);

	/**
	 * Itemオブジェクトの配列を、プロバイダ名：Itemオブジェクトのリストのマップに変換します。
	 * @param items Itemオブジェクトの配列
	 * @return プロバイダ名：Itemオブジェクトのリストのマップ
	 */
	Map getItemMap(Item[] items);

	DoubleList createHolderValue(Collection dataHolders,
			String tableName);

	Map createDateHolderValuesMap(Collection dataHolders,
			String tableName, MultiRecordDefine multiRecordDefine);

	Map createConvertValueMap(Collection holders,
			String tableTame);

	Map createConvertValueMap(Collection holders);

}