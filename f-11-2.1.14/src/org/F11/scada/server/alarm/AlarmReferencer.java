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

package org.F11.scada.server.alarm;

import java.util.SortedSet;

import javax.swing.table.TableModel;

import jp.gr.javacons.jim.DataReferencer;

/**
 * 警報イベントのリファレンサー
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface AlarmReferencer extends TableModel {
    /**
     * データリファレンサーオブジェクトを追加します
     * @param rf データリファレンサーオブジェクト
     */
    void addReferencer(DataReferencer dr);

    /**
     * データリファレンサーオブジェクトを削除します
     * @param rf データリファレンサーオブジェクト
     */
    void removeReferencer(DataReferencer dr);

    /**
     * データリファレンサーのソート済みセットを返します
     * @return データリファレンサーのソート済みセット
     */
    SortedSet getReferencers();
    
    /**
     * データストア・オブジェクトを追加します
     * @param store　データストア・オブジェクト
     */
    boolean addDataStore(AlarmDataStore store);
}
