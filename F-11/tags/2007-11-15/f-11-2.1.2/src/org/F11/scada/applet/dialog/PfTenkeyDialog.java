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
package org.F11.scada.applet.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.applet.symbol.TenkeyEditable;
import org.apache.log4j.Logger;

/**
 * @author hori
 */
public class PfTenkeyDialog extends WifeDialog implements ActionListener {
	private static final long serialVersionUID = 7536699905972474236L;
	/** 数値のスピナーコンポーネント */
	private JSpinner spinner;
	/** 編集対象シンボル */
	private TenkeyEditable symbol;
	/** 親コンポーネントイテレーター */
	private ListIterator listIterator;

	/** 最大値・最小値のラベル */
	private JLabel maxLabel;
	private JLabel minLabel;

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

	/** 力率の種別 */
	JRadioButton LA;
	JRadioButton LE;

	/** ロギングクラスです */
	private static Logger logger = Logger.getLogger(PfTenkeyDialog.class);

	/**
	 * コンストラクタ
	 * 
	 * @param frame 親のフレームです
	 */
	public PfTenkeyDialog(Frame frame) {
		super(frame);
		init();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param dialog 親のダイアログです
	 */
	public PfTenkeyDialog(Dialog dialog) {
		super(dialog);
		init();
	}

	/**
	 * このダイアログを表示します。
	 */
	public void show() {
		logger.info("show開始");
		Rectangle dialogBounds = getBounds();
		dialogBounds.setLocation(symbol.getPoint());
		setLocation(WifeUtilities.getInScreenPoint(screenSize, dialogBounds));
		setDialogValue();
		setTitle(symbol.getDialogTitle());
		selectAll();
		super.show();
	}

	public void selectAll() {
		logger.info("selectAll開始");
		JSpinner.NumberEditor editer = (JSpinner.NumberEditor) spinner
				.getEditor();
		JFormattedTextField text = editer.getTextField();
		text.requestFocusInWindow();
		text.selectAll();
	}

	/**
	 * 終了する時にフォーカストラバースをデフォルトに戻します。
	 * 
	 * @see java.awt.Dialog#dispose()
	 */
	public void dispose() {
		logger.info("dispose開始");
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
		logger.info("setListIterator開始");
		this.listIterator = listIterator;
		// 一つ目のシンボルを設定します。
		symbol = (TenkeyEditable) listIterator.next();
	}

	/**
	 * 編集可能アナログオブジェクトを設定します。
	 */
	public void setDialogValue() {
		logger.info("setDialogValue開始");
		double initialValue = Double
				.parseDouble(symbol.getValue().substring(2));
		double minValue = Math.min(Math.abs(symbol.getConvertMax()), Math
				.abs(symbol.getConvertMin()));
		double maxValue = Math.max(Math.abs(symbol.getConvertMax()), Math
				.abs(symbol.getConvertMin()));
		String formatString = symbol.getFormatString().trim();
		DecimalFormat format = new DecimalFormat(formatString);
		int max = format.getMaximumFractionDigits();
		double stepSize = Math.pow(0.1, max);
		spinner.setModel(new SpinnerNumberModel(
				initialValue,
				minValue,
				maxValue,
				stepSize));
		SelectedFieldNumberEditor editer = new SelectedFieldNumberEditor(
				spinner,
				formatString);
		spinner.setEditor(editer);
		JFormattedTextField text = editer.getTextField();
		text.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (e.getSource() instanceof JTextComponent) {
					final JTextComponent textComp = ((JTextComponent) e
							.getSource());
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							textComp.selectAll();
						}
					});
				}
			}
		});

		maxLabel.setText("MAX :  " + format.format(maxValue));
		minLabel.setText("MIN :  " + format.format(minValue));

		if (symbol.getValue().startsWith("LA"))
			LA.setSelected(true);
		else
			LE.setSelected(true);
	}

	/**
	 * 初期処理です。
	 */
	private void init() {
		logger.info("init開始");
		Box displayBox = Box.createHorizontalBox();

		spinner = new JSpinner();

		Box limitBox = Box.createVerticalBox();
		maxLabel = new JLabel();
		minLabel = new JLabel();
		limitBox.add(maxLabel);
		limitBox.add(minLabel);
		displayBox.add(spinner);
		displayBox.add(Box.createHorizontalGlue());
		displayBox.add(limitBox);

		JPanel keyPanel = new JPanel(new BorderLayout());

		JPanel pfKey = new JPanel();
		LE = new JRadioButton("LE  ");
		LA = new JRadioButton("LA  ");
		pfKey.add(LE);
		pfKey.add(LA);
		ButtonGroup group = new ButtonGroup();
		group.add(LE);
		group.add(LA);
		keyPanel.add(pfKey, BorderLayout.NORTH);

		JPanel tenKey = new JPanel(new GridLayout(4, 3));

		TenkeyButton VK_0 = new TenkeyButton(this, GraphicManager
				.get("/images/tenkey/0.png"), "0");
		TenkeyButton VK_1 = new TenkeyButton(this, GraphicManager
				.get("/images/tenkey/1.png"), "1");
		TenkeyButton VK_2 = new TenkeyButton(this, GraphicManager
				.get("/images/tenkey/2.png"), "2");
		TenkeyButton VK_3 = new TenkeyButton(this, GraphicManager
				.get("/images/tenkey/3.png"), "3");
		TenkeyButton VK_4 = new TenkeyButton(this, GraphicManager
				.get("/images/tenkey/4.png"), "4");
		TenkeyButton VK_5 = new TenkeyButton(this, GraphicManager
				.get("/images/tenkey/5.png"), "5");
		TenkeyButton VK_6 = new TenkeyButton(this, GraphicManager
				.get("/images/tenkey/6.png"), "6");
		TenkeyButton VK_7 = new TenkeyButton(this, GraphicManager
				.get("/images/tenkey/7.png"), "7");
		TenkeyButton VK_8 = new TenkeyButton(this, GraphicManager
				.get("/images/tenkey/8.png"), "8");
		TenkeyButton VK_9 = new TenkeyButton(this, GraphicManager
				.get("/images/tenkey/9.png"), "9");
		TenkeyButton VK_PERIOD = new TenkeyButton(this, GraphicManager
				.get("/images/tenkey/f.png"), ".");
		tenKey.add(VK_7);
		tenKey.add(VK_8);
		tenKey.add(VK_9);
		tenKey.add(VK_4);
		tenKey.add(VK_5);
		tenKey.add(VK_6);
		tenKey.add(VK_1);
		tenKey.add(VK_2);
		tenKey.add(VK_3);
		tenKey.add(VK_0);
		tenKey.add(VK_PERIOD);
		keyPanel.add(tenKey, BorderLayout.CENTER);

		JPanel manipulatePanel = new JPanel(new GridLayout(6, 1));
		OkButton okButton = new OkButton(this, "OK");
		PreviousButton previousButton = new PreviousButton(this, "前項目");
		NextButton nextButton = new NextButton(this, "次項目");
		CancelButton cancelButton = new CancelButton(this, "Cancel");
		manipulatePanel.add(okButton);
		manipulatePanel.add(previousButton);
		manipulatePanel.add(nextButton);
		manipulatePanel.add(cancelButton);
		keyPanel.add(manipulatePanel, BorderLayout.EAST);

		getContentPane().add(displayBox, BorderLayout.NORTH);
		getContentPane().add(keyPanel, BorderLayout.CENTER);

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

	/**
	 * 各ボタンの押下時の動作を処理します。
	 */
	public void actionPerformed(ActionEvent e) {
		logger.info("actionPerformed開始");
		((DialogButton) e.getSource()).pushButton();
	}

	/**
	 * ダイアログに表示するボタンの基底クラスです。
	 */
	private static abstract class DialogButton extends JButton {
		protected final Logger logger = Logger.getLogger(DialogButton.class);
		/** 親ダイアログの参照です。 */
		protected PfTenkeyDialog dialog;

		/**
		 * アイコンボタンを作成するコンストラクタです。
		 * 
		 * @param dialog 親ダイアログの参照
		 * @param icon ボタンに表示するアイコン
		 */
		protected DialogButton(PfTenkeyDialog dialog, Icon icon) {
			super(icon);
			this.dialog = dialog;
			init();
		}

		/**
		 * テキスト表示ボタンを作成するコンストラクタです。
		 * 
		 * @param dialog 親ダイアログの参照
		 * @param text ボタンに表示するテキスト
		 */
		protected DialogButton(PfTenkeyDialog dialog, String text) {
			super(text);
			this.dialog = dialog;
			init();
		}

		/**
		 * 各種初期処理です。
		 */
		private void init() {
			addActionListener(dialog);
		}

		/**
		 * このボタンに対応づけるキーマップを定義します。
		 * 
		 * @param textValue 対応づけるキー(VK_ここの部分)
		 */
		protected void setInoutKeyMap(String textValue) {
			logger.info("setInoutKeyMap開始");

			// define action
			Action key = new AbstractAction(textValue) {
				private static final long serialVersionUID = -8762041381820697344L;

				public void actionPerformed(ActionEvent e) {
					pushButton();
				}
			};

			// associate action with key
			InputMap imap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			if (getKeyStroke(textValue) == null) {
				System.out.println("Not KeyStroke : " + textValue);
			}
			imap.put(getKeyStroke(textValue), key.getValue(Action.NAME));
			ActionMap amap = getActionMap();
			amap.put(key.getValue(Action.NAME), key);
		}

		/**
		 * 仮想関数です。 サブクラスで、ボタンが押下されたときの、処理を記述します。
		 */
		abstract public void pushButton();

		/**
		 * テンキーとそれ以外で微妙に異なるので、数字キーはサブクラスでオーバーライドします。
		 * 
		 * @return KeyStroke の処理に依存しています。
		 */
		protected KeyStroke getKeyStroke(String textValue) {
			logger.info("getKeyStroke開始");
			return KeyStroke.getKeyStroke(textValue);
		}

	}

	/**
	 * テンキーダイアログで使用するボタンクラスです。 0 から 9 とマイナス、ピリオドを表します。
	 */
	private static class TenkeyButton extends DialogButton {
		private static final long serialVersionUID = -2164668271204649141L;
		/** ボタン押下によって挿入する文字 */
		private final String textValue;

		/**
		 * コンストラクタ。 指定されたアイコンでボタンを表示します。
		 * 
		 * @param dialog 親ダイアログの参照
		 * @param icon ボタンに表示するアイコン
		 * @param textValue ボタン押下によって挿入する文字
		 */
		public TenkeyButton(PfTenkeyDialog dialog, Icon icon, String textValue) {
			super(dialog, icon);
			this.textValue = textValue;
			setFocusable(false);
			setInoutKeyMap(textValue);
		}

		/**
		 * ボタンが押下されたときの動作です。 現在のキャレット位置に、ボタンのテキストを挿入します。
		 * 但し、テキストフィールドが選択されている時は、選択部分を削除してその位置に、 ボタンのテキストを挿入します。
		 */
		public void pushButton() {
			logger.info("pushButton開始");
			JSpinner.NumberEditor editer = (JSpinner.NumberEditor) dialog.spinner
					.getEditor();
			JFormattedTextField field = editer.getTextField();
			try {
				if (field.getSelectedText() != null) {
					field.getDocument().remove(
							field.getSelectionStart(),
							(field.getSelectionEnd() - field
									.getSelectionStart()));
				}
				field.getDocument().insertString(
						field.getCaretPosition(),
						textValue,
						null);
			} catch (javax.swing.text.BadLocationException ex) {
				ex.printStackTrace();
			}
		}

		/**
		 * 数字キーのキーストロークを返します(テンキー含む)。
		 * 
		 * @return KeyStroke の処理に依存しています。
		 */
		protected KeyStroke getKeyStroke(String textValue) {
			return KeyStroke.getKeyStroke("typed " + textValue);
		}
	}

	/**
	 * OKボタンクラスです。
	 */
	private static class OkButton extends DialogButton {
		private static final long serialVersionUID = 654521012672746024L;
		private final OKAction action;

		public OkButton(PfTenkeyDialog dialog, String text) {
			super(dialog, text);
			action = new OKAction(dialog);
			setInoutKeyMap("ENTER");
		}

		public void pushButton() {
			logger.info("pushButton開始");
			if (ConfirmUtil.isConfirm((Component) dialog)) {
				PfTenkeyDialog.logger.debug("OK Pressed");
				try {
					action.doAction();
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(
							this,
							"入力値がMIN未満又はMAXより上です。",
							this.getClass().getName(),
							JOptionPane.ERROR_MESSAGE);
					// e.printStackTrace();
					return;
				}
				dialog.dispose();
			}
		}
	}

	/**
	 * Cancelボタンクラスです。
	 */
	private static class CancelButton extends DialogButton {
		private static final long serialVersionUID = 8080821747782300763L;

		public CancelButton(PfTenkeyDialog dialog, String text) {
			super(dialog, text);
			setInoutKeyMap("ESCAPE");
		}

		public void pushButton() {
			logger.info("pushButton開始");
			dialog.dispose();
		}
	}

	/**
	 * 前項目ボタンクラスです。
	 */
	private static class PreviousButton extends DialogButton {
		private static final long serialVersionUID = -1613380129677962385L;
		private final OKAction action;

		public PreviousButton(PfTenkeyDialog dialog, String text) {
			super(dialog, text);
			action = new OKAction(dialog);
			setInoutKeyMap("shift TAB");
		}

		/**
		 * 前項目のセル内容を判定して、セル内容を反映させたテンキーダイアログを表示します。 テンプレートメソッドです。
		 */
		public void pushButton() {
			logger.info("pushButton開始");
			if (ConfirmUtil.isConfirm((Component) dialog)) {
				logger.debug("PreviousButton Pressed");
				try {
					action.doAction();
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(
							this,
							"入力値がMIN未満又はMAXより上です。",
							this.getClass().getName(),
							JOptionPane.ERROR_MESSAGE);
					// e.printStackTrace();
					return;
				}
				dialog.symbol = (TenkeyEditable) dialog.listIterator.previous();
				dialog.show();
			}
		}
	}

	/**
	 * 次項目ボタンクラスです。
	 */
	private static class NextButton extends DialogButton {
		private static final long serialVersionUID = 5472101426133401950L;
		private final OKAction action;

		public NextButton(PfTenkeyDialog dialog, String text) {
			super(dialog, text);
			action = new OKAction(dialog);
			setInoutKeyMap("TAB");
		}

		/**
		 * 次項目のセルの座標を算出します。最大セルの次は table の原点座標を算出します。
		 */
		public void pushButton() {
			logger.info("pushButton開始");
			if (ConfirmUtil.isConfirm((Component) dialog)) {
				PfTenkeyDialog.logger.debug("NextButton Pressed");
				try {
					action.doAction();
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(
							this,
							"入力値がMIN未満又はMAXより上です。",
							this.getClass().getName(),
							JOptionPane.ERROR_MESSAGE);
					// e.printStackTrace();
					return;
				}
				dialog.symbol = (TenkeyEditable) dialog.listIterator.next();
				dialog.show();
			}
		}
	}

	private static class OKAction {
		private final Logger logger = Logger.getLogger(OKAction.class);
		/** 親ダイアログの参照です。 */
		private final PfTenkeyDialog dialog;

		OKAction(PfTenkeyDialog dialog) {
			this.dialog = dialog;
		}

		void doAction() throws ParseException {
			logger.info("doAction開始");
			JSpinner.NumberEditor editer = (JSpinner.NumberEditor) dialog.spinner
					.getEditor();
			JFormattedTextField field = editer.getTextField();
			field.commitEdit();

			String value = field.getText();
			PfTenkeyDialog.logger.debug("Value : " + value);
			if (dialog.symbol == null) {
				PfTenkeyDialog.logger.warn("Remote TenkeyEditable is null");
				return;
			}
			if (dialog.LA.isSelected())
				value = "LA" + value;
			else
				value = "LE" + value;
			dialog.symbol.setValue(value);
		}
	}
}
