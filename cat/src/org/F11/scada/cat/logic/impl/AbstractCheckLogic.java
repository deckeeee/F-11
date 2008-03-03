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

package org.F11.scada.cat.logic.impl;

import java.io.File;

import org.F11.scada.cat.logic.CheckLogic;

/**
 * チェックロジックの基底クラス
 * 
 * @author maekawa
 *
 */
public abstract class AbstractCheckLogic implements CheckLogic {
	protected static final String CHECK_LOG_FOLDER = "check";
	protected static final String PAGEDEFINE = "pagedefine";
	/** この処理が選択されているかの有無 */
	protected boolean isSelected;
	/** エラーログ出力ファイル */
	protected File outFile;

	protected File getOutFile(String file) {
		File checkFolder = new File(CHECK_LOG_FOLDER);
		if (!checkFolder.exists()) {
			checkFolder.mkdirs();
		}
		return new File(CHECK_LOG_FOLDER, file);
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean b) {
		isSelected = b;
	}

	protected File getRoot(String path) {
		File file = new File(path, PAGEDEFINE);
		return file;
	}
}
