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

package org.F11.scada.server.alarm.table;

import org.F11.scada.server.alarm.DataValueChangeEventKey;

/**
 * 警報音個別設定テーブルを含んだ、警報音判定ロジック。
 * @author maekawa
 */
public interface SoundStrategy {
	/**
	 * 警報音個別設定テーブルを含んだ、警報音判定ロジックで音の発生タイプを返します。
	 * @param attributeSoundType 属性の警報音発生タイプ
	 * @param key データ変更イベント
	 * @return 警報音発生タイプ
	 */
	Integer getSoundType(int attributeSoundType, DataValueChangeEventKey key);
}
