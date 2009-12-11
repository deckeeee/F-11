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
 *
 */

package org.F11.scada.server.dao;

import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.HolderString;

/**
 * item_tableのDaoです。
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface ItemDao {
	public static final Class BEAN = Item.class;

	public Item[] getSystemItems(String provider, boolean system);

	public static final String getSystemItems_QUERY =
		"item_table.provider = ? AND item_table.system = ? ORDER BY item_table.point, item_table.provider, item_table.holder";

	public Item getItem(HolderString holderString);

	Item selectItem(String provider, String holder);

	public static final String selectItem_QUERY =
		"item_table.provider = ? AND item_table.holder = ? ORDER BY item_table.point, item_table.provider, item_table.holder";

	int updateItem(Item item);

	/**
	 * システム属性の無いアイテム定義を取得します。
	 *
	 * @return システム属性の無いアイテム定義を取得します。
	 */
	public Item[] getNoSystemItems();

	public static final String getNoSystemItems_QUERY =
		"item_table.system = '0' ORDER BY item_table.point, item_table.provider, item_table.holder";

	/**
	 * ジャンプページを更新します
	 */
	int updateJumpPage(String page, String provider, String holder);

	static final String updateJumpPage_SQL =
		"UPDATE item_table SET jump_path = ?, auto_jump_flag = '1'" +
		" WHERE system = '1' AND data_type = 0 AND provider = ? AND holder = ?";
}
