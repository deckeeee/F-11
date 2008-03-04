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
 */

package org.F11.scada.server.deploy;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * ファイルの一覧を生成するクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FileLister {

	/**
	 * 引数でフィルタリングされたファイルをコレクションで返します。
	 * 
	 * @param root 一覧生成のルートとなるディレクトリ
	 * @param filter ファイルフィルター
	 * @return 引数でフィルタリングされたファイルをコレクションで返します。
	 */
	public Collection<File> listFiles(File root, FileFilter filter) {
		if (root == null) {
			throw new IllegalArgumentException("root is null.");
		}

		if (!root.exists()) {
			throw new IllegalArgumentException("root not exists.");
		}

		if (filter == null) {
			throw new IllegalArgumentException("filter is null.");
		}

		ArrayList<File> result = new ArrayList<File>();

		File[] files = root.listFiles(filter);

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					result.addAll(listFiles(file, filter));
				} else {
					result.add(file);
				}
			}
		}

		return result;
	}
}
