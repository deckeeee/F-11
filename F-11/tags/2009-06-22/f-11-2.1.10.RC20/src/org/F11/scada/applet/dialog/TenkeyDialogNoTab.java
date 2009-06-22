/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/dialog/TenkeyDialogNoTab.java,v 1.3.2.11 2007/07/12 09:41:33 frdm Exp $
 * $Revision: 1.3.2.11 $
 * $Date: 2007/07/12 09:41:33 $
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
import java.util.ListIterator;

import javax.swing.JPanel;

import org.F11.scada.applet.symbol.TenkeyEditable;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;

/**
 * 数値設定用テンキー型ダイアログクラスです。
 */
public class TenkeyDialogNoTab extends AbstractTenkeyDialog {
	private static final Logger log = Logger.getLogger(TenkeyDialogNoTab.class);
	private static final long serialVersionUID = 1572895218419518896L;

	/**
	 * コンストラクタ
	 * 
	 * @param frame 親のフレームです
	 */
	public TenkeyDialogNoTab(Frame frame, PageChanger changer) {
		super(frame, changer);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param dialog 親のダイアログです
	 */
	public TenkeyDialogNoTab(Dialog dialog, PageChanger changer) {
		super(dialog, changer);
	}

	/**
	 * イテレーターをセットします
	 * 
	 * @param listIterator 編集可能シンボルのイテレーター
	 */
	public void setListIterator(ListIterator listIterator) {
		log.info("setListIterator開始");
		// 一つ目のシンボルを設定します。
		symbol = (TenkeyEditable) listIterator.next();
	}

	public ListIterator listIterator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 初期処理です。
	 */
	protected void setManipulatePanel(JPanel keyPanel) {
		log.info("setManipulatePanel開始");
		JPanel manipulatePanel = new JPanel(new GridLayout(6, 1));
		OkButton okButton = new OkButton(this, "OK");
		CancelButton cancelButton = new CancelButton(this, "Cancel", changer);
		manipulatePanel.add(okButton);
		manipulatePanel.add(cancelButton);
		keyPanel.add(manipulatePanel, BorderLayout.EAST);
	}
}
