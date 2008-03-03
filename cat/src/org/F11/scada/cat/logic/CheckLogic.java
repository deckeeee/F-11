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

package org.F11.scada.cat.logic;

import java.io.IOException;

import org.F11.scada.cat.component.CheckableItem;

/**
 * チェック処理ロジックです。
 * 
 * @author maekawa
 * 
 */
public interface CheckLogic extends CheckableItem {
	/**
	 * チェック処理を実行します。
	 * 
	 * @param path 処理対象物件フォルダパス
	 * @param task Taskオブジェクトの参照
	 * @throws IOException ファイル処理での例外
	 */
	void execute(String path, ExecuteTask task)
			throws IOException;
}
