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

package org.F11.scada.parser.alarm;

/**
 * クライアントのページ設定を保持します
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class Page {
	/**
	 * クライアント最大保持ページ数
	 * 但し、負数はキャッシュクリアーしない事を示す
	 * @since 1.1.1
	 */
	private int cacheMax = -1;
	
	/**
	 * クライアント最大保持ページ数を返します
	 * @return クライアント最大保持ページ数
	 */
	public int getCacheMax() {
		return cacheMax;
	}

	/**
	 * クライアント最大保持ページ数を設定します
	 * @param i クライアント最大保持ページ数
	 */
	public void setCacheMax(int i) {
		cacheMax = i;
	}

}
