/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/dialog/DigitalDialog.java,v 1.5.2.11 2007/07/31 08:27:48 frdm Exp $
 * $Revision: 1.5.2.11 $
 * $Date: 2007/07/31 08:27:48 $
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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ListIterator;

import javax.swing.JButton;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.applet.symbol.DigitalEditable;
import org.F11.scada.util.FontUtil;
import org.apache.log4j.Logger;

/**
 * デジタルデータを操作するダイアログクラスです。 指示動作は DigitalButton で定義します。
 * ダイアログクラスでは、ボタンと指示動作の関連を定義します。 キャンセルボタンは、指示動作を実行しないままダイアログを閉じます。
 */
public class DigitalDialog extends WifeDialog implements ActionListener {
	private static final long serialVersionUID = 3786955747253504330L;
	private final Logger logger = Logger.getLogger(DigitalDialog.class);
	/** デジタルボタンの参照 */
	private DigitalEditable symbol;

	/**
	 * コンストラクタ。
	 * 
	 * @param frame 親のフレーム。
	 */
	public DigitalDialog(Frame frame) {
		super(frame);
		this.getContentPane().setLayout(null);
	}

	/**
	 * コンストラクタ。
	 * 
	 * @param dialog 親ダイアログ。
	 */
	public DigitalDialog(Dialog dialog) {
		super(dialog);
		this.getContentPane().setLayout(null);
	}

	/**
	 * イテレーターをセットします
	 * 
	 * @param listIterator 編集可能シンボルのイテレーター
	 */
	public void setListIterator(ListIterator listIterator) {
		logger.info("setListIterator開始");
		symbol = (DigitalEditable) listIterator.next();
	}

	public void show() {
		logger.info("show開始");
		Rectangle dialogBounds = getBounds();
		dialogBounds.setLocation(symbol.getPoint());
		setLocation(WifeUtilities.getInScreenPoint(screenSize, dialogBounds));
		/* ボタン名称設定 */
		for (int i = 0, textPos = 0; i < getContentPane().getComponentCount(); i++) {
			Component compo = getContentPane().getComponent(i);
			if (compo instanceof JButton) {
				String bt = this.symbol.getButtonString(textPos);
				if (bt != null)
					((JButton) compo).setText(bt);
				textPos++;
			}
		}
		setDefaultFocus();
		super.show();
	}

	/**
	 * 終了する時にフォーカストラバースをデフォルトに戻します。
	 * 
	 * @see java.awt.Dialog#dispose()
	 */
	public void dispose() {
		logger.info("dispose開始");
		setDefaultFocus();
		super.dispose();
	}

	public void selectAll() {
		logger.info("selectAll開始");
	}

	/**
	 * 押されたボタンの動作を実行します。
	 */
	public void actionPerformed(ActionEvent e) {
		logger.info("actionPerformed開始");
		DialogButton button = (DialogButton) e.getSource();
		button.pushButton();
	}

	/**
	 * ダイアログにボタンを追加します。
	 * 
	 * @param text ボタン名称
	 * @param 指示動作番号
	 * @param rec ボタンの大きさと配置位置
	 * @param foreground 文字色
	 * @param background 背景色
	 * @param font フォント名
	 * @param fontStyle フォントスタイル
	 * @param fontSize フォントサイズ
	 */
	public void add(
			String text,
			int actionNo,
			Rectangle rec,
			String foreground,
			String background,
			String font,
			String fontStyle,
			String fontSize) {
		logger.info("add開始");
		invalidate();
		DialogButton button = DialogButton.createDialogButton(this, actionNo);
		button.setText(text);
		button.setBounds(rec);
		if (null != ColorFactory.getColor(foreground)) {
			button.setForeground(ColorFactory.getColor(foreground));
		}
		if (null != ColorFactory.getColor(background)) {
			button.setBackground(ColorFactory.getColor(background));
		}
		FontUtil.setFont(font, fontStyle, fontSize, button);
		getContentPane().add(button);
		validate();
	}

	/**
	 * ダイアログに表示するボタンの基底クラスです。
	 */
	static abstract class DialogButton extends JButton {
		protected static final Logger logger = Logger
				.getLogger(DialogButton.class);
		/** 親ダイアログの参照です。 */
		protected DigitalDialog dialog;
		/** ボタンに定義された指示動作番号です */
		protected int actionNo;

		/**
		 * アイコンボタンを作成するコンストラクタです。
		 * 
		 * @param dialog 親ダイアログの参照
		 * @param actionNo ボタンに割り当てる指示動作番号
		 */
		protected DialogButton(DigitalDialog dialog, int actionNo) {
			super();
			this.dialog = dialog;
			this.actionNo = actionNo;
			addActionListener(this.dialog);
		}

		/**
		 * 仮想関数です。 サブクラスで、ボタンが押下されたときの、処理を記述します。
		 */
		abstract public void pushButton();

		static public DialogButton createDialogButton(
				DigitalDialog dialog,
				int actionNo) {
			logger.info("createDialogButton開始");
			if (actionNo == 6) {
				return new CancelButton(dialog, actionNo);
			} else {
				return new SendButton(dialog, actionNo);
			}
		}
	}

	/**
	 * 指示動作送信ボタンクラスです。
	 */
	static final class SendButton extends DialogButton {
		private static final long serialVersionUID = -1307199387393391804L;

		/**
		 * コンストラクタ
		 * 
		 * @param dialog ボタンを追加するダイアログの参照
		 * @param actionNo ボタンの指示動作
		 */
		public SendButton(DigitalDialog dialog, int actionNo) {
			super(dialog, actionNo);
		}

		/**
		 * デジタルボタンの指示動作を実行します。
		 */
		public void pushButton() {
			logger.info("pushButton開始");
			if (ConfirmUtil.isConfirm(dialog)) {
				dialog.symbol.pushButton(actionNo);
				dialog.dispose();
			}
		}
	}

	/**
	 * キャンセルボタンクラスです。
	 */
	static final class CancelButton extends DialogButton {
		private static final long serialVersionUID = -4881864928924466057L;

		/**
		 * コンストラクタ
		 * 
		 * @param dialog ボタンを追加するダイアログの参照
		 * @param actionNo ボタンの指示動作
		 */
		public CancelButton(DigitalDialog dialog, int actionNo) {
			super(dialog, actionNo);
		}

		/**
		 * デジタルボタンの指示動作を実行します。
		 */
		public void pushButton() {
			logger.info("pushButton開始");
			dialog.dispose();
		}
	}
}
