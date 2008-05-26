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

package org.F11.scada.util;

/**
 * 頁の履歴を表すインターフェイス
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface PageHistory {
    /**
     * 履歴内に頁が存在しない事を表す文字列です。
     */
    public static final String EOP = "org.F11.scada.util.PageHistory.EOP";

    /**
     * ポインタの次に頁履歴を追加します。
     * @param pageId
     */
    void set(String pageId);

    /**
     * 存在すれば次の頁履歴を返します。
     * @return 次の頁履歴の頁ID
     */
    String next();

    /**
     * 存在すれば前の頁履歴を返します。
     * @return 前の頁履歴の頁ID
     */
    String previous();
}
