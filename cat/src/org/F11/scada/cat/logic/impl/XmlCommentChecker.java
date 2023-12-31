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

/**
 * XMLのコメント状態を保持するヘルパークラス
 * 
 * @author maekawa
 *
 */
class XmlCommentChecker {
	/** コメント処理中の有無 */
	private boolean isComment;

	/**
	 * 行を読み込んでコメント内かどうかを判定します。
	 * 
	 * @param line 処理する行
	 */
	void checkComment(String line) {
		String startStr = "<!--";
		int start = line.lastIndexOf(startStr);
		if (start >= 0) {
			isComment = true;
		}
		String endStr = "-->";
		int end = line.indexOf(endStr, start + startStr.length());
		if (end >= 0) {
			isComment = false;
		}
	}
	
	/**
	 * コメント内であれば true を 無ければ false を返します。
	 * 
	 * @return コメント内であれば true を 無ければ false を返します。
	 */
	boolean isComment() {
		return isComment;
	}
}
