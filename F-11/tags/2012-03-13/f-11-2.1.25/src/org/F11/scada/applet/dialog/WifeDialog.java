package org.F11.scada.applet.dialog;

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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.JDialog;

public abstract class WifeDialog extends JDialog {
	/** 現在の画面サイズ */
	protected Dimension screenSize;
	/** デフォルトフォーカストラバースの参照 */
	protected static Set forward;
	protected static Set backward;
	static {
		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		forward = kfm
				.getDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		backward = kfm
				.getDefaultFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param frame 親フレーム
	 */
	protected WifeDialog(Frame frame) {
		super(frame, true);
		init(frame);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param dialog 親ダイアログ
	 */
	protected WifeDialog(Dialog dialog) {
		super(dialog, true);
		init(dialog);
	}

	private void init(Window window) {
		this.screenSize = window.getToolkit().getScreenSize();
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public void dispose() {
		setDefaultFocus();
		super.dispose();
	}

	protected void setDefaultFocus() {
		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		kfm.setDefaultFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				forward);
		kfm.setDefaultFocusTraversalKeys(
				KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				backward);
	}

	/**
	 * イテレーターをセットします
	 * 
	 * @param listIterator 編集可能シンボルのイテレーター
	 */
	abstract public void setListIterator(ListIterator listIterator);

}
