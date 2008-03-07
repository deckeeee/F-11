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

package org.F11.scada.cat.util;

import static org.F11.scada.cat.util.CollectionUtil.set;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;

/**
 * 拡張子で抽出するファイルフィルタ。 文字列の可変引数で拡張子を指定する。<br/>例. new ExtFileFilter(".xml",
 * ".inc");
 * 
 * @author maekawa
 * 
 */
public class ExtFileFilter implements FileFilter {
	private final Set<String> extSet;

	/**
	 * 文字列の可変引数で拡張子を指定する。<br/>例. new ExtFileFilter(".xml",".inc");
	 * 
	 * @param ext 抽出する拡張子
	 */
	public ExtFileFilter(String... ext) {
		extSet = set(ext);
	}

	public boolean accept(File pathname) {
		String name = pathname.getName();
		int periodIndex = name.lastIndexOf('.');
		return pathname.isDirectory()
			|| (periodIndex >= 0 && extSet
				.contains(name.substring(periodIndex))) ? true : false;
	}
}
