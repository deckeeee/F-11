/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/dialog/TenkeyDialog.java,v 1.8.2.12 2007/07/26 01:11:36 frdm Exp $
 * $Revision: 1.8.2.12 $
 * $Date: 2007/07/26 01:11:36 $
 * 
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

package org.F11.scada.applet.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.JPanel;

import org.F11.scada.applet.symbol.TenkeyEditable;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;

/**
 * 数値設定用テンキー型ダイアログクラスです。
 */
public class TenkeyDialog extends AbstractTenkeyDialog {
	private static final long serialVersionUID = -5590430193792923661L;
	private static final Logger log = Logger.getLogger(TenkeyDialog.class);
	/** 親コンポーネントイテレーター */
	private ListIterator listIterator;
	/** デフォルトフォーカストラバースの参照 */
	private static final Set forward;
	private static final Set backward;
	static {
		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		forward = kfm
				.getDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		backward = kfm
				.getDefaultFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
	};

	/**
	 * コンストラクタ
	 * 
	 * @param frame 親のフレームです
	 */
	public TenkeyDialog(Frame frame, PageChanger changer) {
		super(frame, changer);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param dialog 親のダイアログです
	 */
	public TenkeyDialog(Dialog dialog, PageChanger changer) {
		super(dialog, changer);
	}

	/**
	 * 終了する時にフォーカストラバースをデフォルトに戻します。
	 * 
	 * @see java.awt.Dialog#dispose()
	 */
	public void dispose() {
		log.info("dispose開始");
		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		kfm.setDefaultFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				forward);
		kfm.setDefaultFocusTraversalKeys(
				KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				backward);

		super.dispose();
	}

	/**
	 * イテレーターをセットします
	 * 
	 * @param listIterator 編集可能シンボルのイテレーター
	 */
	public void setListIterator(ListIterator listIterator) {
		log.info("setListIterator開始");
		this.listIterator = listIterator;
		// 一つ目のシンボルを設定します。
		symbol = (TenkeyEditable) listIterator.next();
	}

	public ListIterator listIterator() {
		log.info("listIterator開始");
		return listIterator;
	}

	/**
	 * 初期処理です。
	 */
	protected void setManipulatePanel(JPanel keyPanel) {
		log.info("setManipulatePanel開始");
		JPanel manipulatePanel = new JPanel(new GridLayout(6, 1));
		final OkButton okButton = new OkButton(this, "OK");
		PreviousButton previousButton = new PreviousButton(this, "前項目");
		NextButton nextButton = new NextButton(this, "次項目");
		CancelButton cancelButton = new CancelButton(this, "Cancel", changer);
		manipulatePanel.add(okButton);
		manipulatePanel.add(previousButton);
		manipulatePanel.add(nextButton);
		manipulatePanel.add(cancelButton);
		keyPanel.add(manipulatePanel, BorderLayout.EAST);
		// タブキーの入力イベントをキーボードフォーカスマネージャーに横取りされる為、
		// キーボードフォーカスマネージャーより、タブキーの割り当てを削除します。
		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		kfm.setDefaultFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
		kfm.setDefaultFocusTraversalKeys(
				KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
	}
}
