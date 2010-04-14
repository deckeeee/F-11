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

import java.util.Collection;

import org.F11.scada.server.entity.Item;

/**
 * HolderStringのコレクションより、Itemオブジェクトの配列を返します。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface ItemArrayDao {
    /** S2Daoアノテーション */
    public static final Class BEAN = Item.class;

    /**
     * HolderStringのコレクションより、システム属性以外のItemオブジェクトの配列を返します。
     * @param holders HolderStringのコレクション
     * @return Itemオブジェクトの配列
     */
    Item[] getItemsNonSystem(Collection holders);

    /**
     * HolderStringのコレクションより、Itemオブジェクトの配列を返します。
     * @param holders HolderStringのコレクション
     * @return Itemオブジェクトの配列
     */
    Item[] getItems(Collection holders);
}
