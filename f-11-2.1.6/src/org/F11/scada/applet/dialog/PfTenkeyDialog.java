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
	/** ���l�̃X�s�i�[�R���|�[�l���g */
	private JSpinner spinner;
	/** �ҏW�ΏۃV���{�� */
	private TenkeyEditable symbol;
	/** �e�R���|�[�l���g�C�e���[�^�[ */
	private ListIterator listIterator;

	/** �ő�l�E�ŏ��l�̃��x�� */
	private JLabel maxLabel;
	private JLabel minLabel;

	/** �f�t�H���g�t�H�[�J�X�g���o�[�X�̎Q�� */
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

	/** �͗��̎�� */
	JRadioButton LA;
	JRadioButton LE;

	/** ���M���O�N���X�ł� */
	private static Logger logger = Logger.getLogger(PfTenkeyDialog.class);

	/**
	 * �R���X�g���N�^
	 * 
	 * @param frame �e�̃t���[���ł�
	 */
	public PfTenkeyDialog(Frame frame) {
		super(frame);
		init();
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param dialog �e�̃_�C�A���O�ł�
	 */
	public PfTenkeyDialog(Dialog dialog) {
		super(dialog);
		init();
	}

	/**
	 * ���̃_�C�A���O��\�����܂��B
	 */
	public void show() {
		logger.info("show�J�n");
		Rectangle dialogBounds = getBounds();
		dialogBounds.setLocation(symbol.getPoint());
		setLocation(WifeUtilities.getInScreenPoint(screenSize, dialogBounds));
		setDialogValue();
		setTitle(symbol.getDialogTitle());
		selectAll();
		super.show();
	}

	public void selectAll() {
		logger.info("selectAll�J�n");
		JSpinner.NumberEditor editer = (JSpinner.NumberEditor) spinner
				.getEditor();
		JFormattedTextField text = editer.getTextField();
		text.requestFocusInWindow();
		text.selectAll();
	}

	/**
	 * �I�����鎞�Ƀt�H�[�J�X�g���o�[�X���f�t�H���g�ɖ߂��܂��B
	 * 
	 * @see java.awt.Dialog#dispose()
	 */
	public void dispose() {
		logger.info("dispose�J�n");
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
	 * �C�e���[�^�[���Z�b�g���܂�
	 * 
	 * @param listIterator �ҏW�\�V���{���̃C�e���[�^�[
	 */
	public void setListIterator(ListIterator listIterator) {
		logger.info("setListIterator�J�n");
		this.listIterator = listIterator;
		// ��ڂ̃V���{����ݒ肵�܂��B
		symbol = (TenkeyEditable) listIterator.next();
	}

	/**
	 * �ҏW�\�A�i���O�I�u�W�F�N�g��ݒ肵�܂��B
	 */
	public void setDialogValue() {
		logger.info("setDialogValue�J�n");
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
	 * ���������ł��B
	 */
	private void init() {
		logger.info("init�J�n");
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
		PreviousButton previousButton = new PreviousButton(this, "�O����");
		NextButton nextButton = new NextButton(this, "������");
		CancelButton cancelButton = new CancelButton(this, "Cancel");
		manipulatePanel.add(okButton);
		manipulatePanel.add(previousButton);
		manipulatePanel.add(nextButton);
		manipulatePanel.add(cancelButton);
		keyPanel.add(manipulatePanel, BorderLayout.EAST);

		getContentPane().add(displayBox, BorderLayout.NORTH);
		getContentPane().add(keyPanel, BorderLayout.CENTER);

		// �^�u�L�[�̓��̓C�x���g���L�[�{�[�h�t�H�[�J�X�}�l�[�W���[�ɉ���肳���ׁA
		// �L�[�{�[�h�t�H�[�J�X�}�l�[�W���[���A�^�u�L�[�̊��蓖�Ă��폜���܂��B
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
	 * �e�{�^���̉������̓�����������܂��B
	 */
	public void actionPerformed(ActionEvent e) {
		logger.info("actionPerformed�J�n");
		((DialogButton) e.getSource()).pushButton();
	}

	/**
	 * �_�C�A���O�ɕ\������{�^���̊��N���X�ł��B
	 */
	private static abstract class DialogButton extends JButton {
		protected final Logger logger = Logger.getLogger(DialogButton.class);
		/** �e�_�C�A���O�̎Q�Ƃł��B */
		protected PfTenkeyDialog dialog;

		/**
		 * �A�C�R���{�^�����쐬����R���X�g���N�^�ł��B
		 * 
		 * @param dialog �e�_�C�A���O�̎Q��
		 * @param icon �{�^���ɕ\������A�C�R��
		 */
		protected DialogButton(PfTenkeyDialog dialog, Icon icon) {
			super(icon);
			this.dialog = dialog;
			init();
		}

		/**
		 * �e�L�X�g�\���{�^�����쐬����R���X�g���N�^�ł��B
		 * 
		 * @param dialog �e�_�C�A���O�̎Q��
		 * @param text �{�^���ɕ\������e�L�X�g
		 */
		protected DialogButton(PfTenkeyDialog dialog, String text) {
			super(text);
			this.dialog = dialog;
			init();
		}

		/**
		 * �e�평�������ł��B
		 */
		private void init() {
			addActionListener(dialog);
		}

		/**
		 * ���̃{�^���ɑΉ��Â���L�[�}�b�v���`���܂��B
		 * 
		 * @param textValue �Ή��Â���L�[(VK_�����̕���)
		 */
		protected void setInoutKeyMap(String textValue) {
			logger.info("setInoutKeyMap�J�n");

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
		 * ���z�֐��ł��B �T�u�N���X�ŁA�{�^�����������ꂽ�Ƃ��́A�������L�q���܂��B
		 */
		abstract public void pushButton();

		/**
		 * �e���L�[�Ƃ���ȊO�Ŕ����ɈقȂ�̂ŁA�����L�[�̓T�u�N���X�ŃI�[�o�[���C�h���܂��B
		 * 
		 * @return KeyStroke �̏����Ɉˑ����Ă��܂��B
		 */
		protected KeyStroke getKeyStroke(String textValue) {
			logger.info("getKeyStroke�J�n");
			return KeyStroke.getKeyStroke(textValue);
		}

	}

	/**
	 * �e���L�[�_�C�A���O�Ŏg�p����{�^���N���X�ł��B 0 ���� 9 �ƃ}�C�i�X�A�s���I�h��\���܂��B
	 */
	private static class TenkeyButton extends DialogButton {
		private static final long serialVersionUID = -2164668271204649141L;
		/** �{�^�������ɂ���đ}�����镶�� */
		private final String textValue;

		/**
		 * �R���X�g���N�^�B �w�肳�ꂽ�A�C�R���Ń{�^����\�����܂��B
		 * 
		 * @param dialog �e�_�C�A���O�̎Q��
		 * @param icon �{�^���ɕ\������A�C�R��
		 * @param textValue �{�^�������ɂ���đ}�����镶��
		 */
		public TenkeyButton(PfTenkeyDialog dialog, Icon icon, String textValue) {
			super(dialog, icon);
			this.textValue = textValue;
			setFocusable(false);
			setInoutKeyMap(textValue);
		}

		/**
		 * �{�^�����������ꂽ�Ƃ��̓���ł��B ���݂̃L�����b�g�ʒu�ɁA�{�^���̃e�L�X�g��}�����܂��B
		 * �A���A�e�L�X�g�t�B�[���h���I������Ă��鎞�́A�I�𕔕����폜���Ă��̈ʒu�ɁA �{�^���̃e�L�X�g��}�����܂��B
		 */
		public void pushButton() {
			logger.info("pushButton�J�n");
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
		 * �����L�[�̃L�[�X�g���[�N��Ԃ��܂�(�e���L�[�܂�)�B
		 * 
		 * @return KeyStroke �̏����Ɉˑ����Ă��܂��B
		 */
		protected KeyStroke getKeyStroke(String textValue) {
			return KeyStroke.getKeyStroke("typed " + textValue);
		}
	}

	/**
	 * OK�{�^���N���X�ł��B
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
			logger.info("pushButton�J�n");
			if (ConfirmUtil.isConfirm((Component) dialog)) {
				PfTenkeyDialog.logger.debug("OK Pressed");
				try {
					action.doAction();
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(
							this,
							"���͒l��MIN��������MAX����ł��B",
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
	 * Cancel�{�^���N���X�ł��B
	 */
	private static class CancelButton extends DialogButton {
		private static final long serialVersionUID = 8080821747782300763L;

		public CancelButton(PfTenkeyDialog dialog, String text) {
			super(dialog, text);
			setInoutKeyMap("ESCAPE");
		}

		public void pushButton() {
			logger.info("pushButton�J�n");
			dialog.dispose();
		}
	}

	/**
	 * �O���ڃ{�^���N���X�ł��B
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
		 * �O���ڂ̃Z�����e�𔻒肵�āA�Z�����e�𔽉f�������e���L�[�_�C�A���O��\�����܂��B �e���v���[�g���\�b�h�ł��B
		 */
		public void pushButton() {
			logger.info("pushButton�J�n");
			if (ConfirmUtil.isConfirm((Component) dialog)) {
				logger.debug("PreviousButton Pressed");
				try {
					action.doAction();
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(
							this,
							"���͒l��MIN��������MAX����ł��B",
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
	 * �����ڃ{�^���N���X�ł��B
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
		 * �����ڂ̃Z���̍��W���Z�o���܂��B�ő�Z���̎��� table �̌��_���W���Z�o���܂��B
		 */
		public void pushButton() {
			logger.info("pushButton�J�n");
			if (ConfirmUtil.isConfirm((Component) dialog)) {
				PfTenkeyDialog.logger.debug("NextButton Pressed");
				try {
					action.doAction();
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(
							this,
							"���͒l��MIN��������MAX����ł��B",
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
		/** �e�_�C�A���O�̎Q�Ƃł��B */
		private final PfTenkeyDialog dialog;

		OKAction(PfTenkeyDialog dialog) {
			this.dialog = dialog;
		}

		void doAction() throws ParseException {
			logger.info("doAction�J�n");
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
