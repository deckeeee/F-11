/*
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

package org.F11.scada.applet.dialog.schedule;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * スケジュールダイアログのボタンの基底クラスです
 */
abstract class AbstractScheduleButton extends JButton {
	/** スケジュール時刻設定ダイアログの参照 */
	protected AbstractScheduleDialog scheduleDialog;

	/**
	 * コンストラクタ
	 * @param dialog スケジュールダイアログの参照
	 */
	protected AbstractScheduleButton(AbstractScheduleDialog scheduleDialog) {
		this.scheduleDialog = scheduleDialog;
	}


	/**
	 * このボタンに対応づけるキーマップを定義します。
	 * @param textValue 対応づけるキー(VK_ここの部分)
	 */
	protected void setInoutKeyMap(String textValue) {
		// define action
		Action key = new AbstractAction(textValue) {
			private static final long serialVersionUID = 7068324749390647020L;

			public void actionPerformed(ActionEvent e) {
				pushButton();
			}
		};

		// associate action with key
		InputMap imap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		if (getKeyStroke(textValue) == null) {
			System.out.println("Not KeyStroke : " + textValue);
		}
		imap.put(
			getKeyStroke(textValue),
			key.getValue(Action.NAME)
		);
		ActionMap amap = getActionMap();
		amap.put(key.getValue(Action.NAME), key);
	}

	/**
	 * テンキーとそれ以外で微妙に異なるので、数字キーはサブクラスでオーバーライドします。
	 * @return KeyStroke の処理に依存しています。
	 */
	protected KeyStroke getKeyStroke(String textValue) {
		return KeyStroke.getKeyStroke(textValue);
	}

	/**
	 * 仮想メソッドです。
	 * ボタンが押下された時の処理を記述します。
	 */
	abstract public void pushButton();
}
