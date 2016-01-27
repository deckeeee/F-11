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
package org.F11.scada.parser;

import java.util.Stack;

import org.xml.sax.Attributes;

/**
 * SAXパーサーの状態を表すクラスのインターフェイスです。(Use State Pattern.)
 * このインターフェイスを実装するクラスは、タグにあわせて下位の状態オブジェクトを
 * 生成し、状態移管を表現します。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface State {
	/**
	 * タグイベントが発生した時に呼び出します。
	 * 一般的には状態スタックに自分自身をpushします。
	 * @param tagName タグ名称
	 * @param atts タグ属性オブジェクト
	 * @param stack 状態スタック
	 */
	public void add(String tagName, Attributes atts, Stack stack);
	
	/**
	 * 終了タグイベントが発生した時に呼び出します。
	 * 一般的には状態スタックから自分自身をpopします。
	 * @param tagName タグ名称
	 * @param stack 状態スタック
	 */
	public void end(String tagName, Stack stack);
}
