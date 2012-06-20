/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.applet.dialog;

import java.awt.event.ActionListener;
import java.util.ListIterator;

import javax.swing.JSpinner;

import org.F11.scada.applet.symbol.TenkeyEditable;

/**
 * JSpinnerを持つダイアログのインターフェイスです。
 * @author maekawa
 */
public interface SpinnerDialog extends ActionListener {
	/**
	 * スピナー内のエディターを返します。
	 * @return スピナー内のエディターを返します。
	 */
	JSpinner.NumberEditor getEditor();
	/**
	 * ダイアログを閉じます。
	 */
	void dispose();
	/**
	 * ダイアログを開きます。
	 */
	void show();
	/**
	 * リストイテレーターを返します。
	 * @return リストイテレーターを返します。
	 */
	ListIterator listIterator();
	/**
	 * シンボルを設定します。
	 * @param symbol シンボル
	 */
	void setSymbol(TenkeyEditable symbol);

	/**
	 * スピナーの内容を返します。
	 * @return スピナーの内容を返します。
	 */
	Object getValue();

	/**
	 * スピナーに値を設定します。
	 * @param value スピナーに値を設定します。
	 */
	void setValue(String value);
	/**
	 * シンボルのインスタンスを保持しているか。
	 * @return 保持している場合はtrue そうでない場合は false を返します。
	 */
	boolean hasSymbol();
}
