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

package org.F11.scada.server;

import jp.gr.javacons.jim.DataHolder;

public interface ScheduleHolderOwner {
    /**
     * スケジュールデータを含むデータホルダをマネージャーに設定します。
     * @param point ポイントID
     * @param dh データホルダ
     * @return 設定したデータホルダ
     * @version 2.0.21
     */
    DataHolder putScheduleHolder(Integer point, DataHolder dh);

    /**
     * スケジュールデータを含むデータホルダをマネージャーから削除します。
     * @param point ポイントID
     * @return 削除したデータホルダ
     * @version 2.0.21
     */
    DataHolder removeScheduleHolder(Integer point);
}
