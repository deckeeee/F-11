/*
 * $Header$
 * $Revision$
 * $Date$
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
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.applet.symbol.TenkeyEditable;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;

/**
 * ���l�ݒ�p�e���L�[�^�_�C�A���O�N���X�ł��B
 */
public abstract class AbstractTenkeyDialog extends WifeDialog implements
		SpinnerDialog {
	private final Logger logger = Logger.getLogger(AbstractTenkeyDialog.class);
	/** ���l�̃X�s�i�[�R���|�[�l���g */
	protected JSpinner spinner;
	/** �ҏW�ΏۃV���{�� */
	protected TenkeyEditable symbol;
	/** �ő�l�E�ŏ��l�̃��x�� */
	protected JLabel maxLabel;
	protected JLabel minLabel;
	/** �_�C�A���O�ŗL�̓��͍ŏ��l */
	protected String dialogMin;
	/** �_�C�A���O�ŗL�̓��͍ő�l */
	protected String dialogMax;
	/** �y�[�W�ύX�I�u�W�F�N�g */
	protected final PageChanger changer;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param frame �e�̃t���[���ł�
	 */
	public AbstractTenkeyDialog(Frame frame, PageChanger changer) {
		super(frame);
		this.changer = changer;
		init();
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param dialog �e�̃_�C�A���O�ł�
	 */
	public AbstractTenkeyDialog(Dialog dialog, PageChanger changer) {
		super(dialog);
		this.changer = changer;
		init();
	}

	public void setDialogMin(String dialogMin) {
		this.dialogMin = dialogMin;
	}

	public void setDialogMax(String dialogMax) {
		this.dialogMax = dialogMax;
	}

	/**
	 * ���̃_�C�A���O��\�����܂��B
	 */
	public void show() {
		logger.info("�_�C�A���O�\���J�n");
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
		JFormattedTextField text = getEditor().getTextField();
		text.requestFocusInWindow();
		text.selectAll();
	}

	/**
	 * �ҏW�\�A�i���O�I�u�W�F�N�g��ݒ肵�܂��B
	 */
	public void setDialogValue() {
		logger.info("setDialogValue�J�n");
		String formatString = symbol.getFormatString();
		DecimalFormat format = new DecimalFormat(formatString);
		setSpinnerModel(format);
		setSpinnerEditor(formatString);
	}

	private void setSpinnerEditor(String formatString) {
		SelectedFieldNumberEditor editer =
			new SelectedFieldNumberEditor(spinner, formatString);
		spinner.setEditor(editer);
	}

	private void setSpinnerModel(DecimalFormat format) {
		double initialValue = 0D;
		try {
			initialValue = Double.parseDouble(symbol.getValue());
		} catch (NumberFormatException e) {
		}
		double minValue = getMinValue();
		initialValue = Math.max(initialValue, minValue);
		double maxValue = getMaxValue();
		initialValue = Math.min(initialValue, maxValue);
		int max = format.getMaximumFractionDigits();
		double stepSize = Math.pow(0.1, max);
		if (minValue > maxValue) {
			spinner.setModel(new SpinnerNumberModel(
				initialValue,
				maxValue,
				minValue,
				stepSize));
		} else {
			spinner.setModel(new SpinnerNumberModel(
				initialValue,
				minValue,
				maxValue,
				stepSize));
		}
		maxLabel.setText("MAX :  " + format.format(maxValue));
		minLabel.setText("MIN :  " + format.format(minValue));
	}

	private double getMaxValue() {
		double maxValue = 0D;
		if (null != dialogMax) {
			maxValue = Double.parseDouble(dialogMax);
		} else {
			maxValue = symbol.getConvertMax();
		}
		return maxValue;
	}

	private double getMinValue() {
		double minValue = 0D;
		if (null != dialogMin) {
			minValue = Double.parseDouble(dialogMin);
		} else {
			minValue = symbol.getConvertMin();
		}
		return minValue;
	}

	/**
	 * ���������ł��B
	 */
	protected void init() {
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
		JPanel tenKey = new JPanel(new GridLayout(4, 3));

		TenkeyButton VK_0 =
			new TenkeyButton(
				this,
				GraphicManager.get("/images/tenkey/0.png"),
				"0");
		TenkeyButton VK_1 =
			new TenkeyButton(
				this,
				GraphicManager.get("/images/tenkey/1.png"),
				"1");
		TenkeyButton VK_2 =
			new TenkeyButton(
				this,
				GraphicManager.get("/images/tenkey/2.png"),
				"2");
		TenkeyButton VK_3 =
			new TenkeyButton(
				this,
				GraphicManager.get("/images/tenkey/3.png"),
				"3");
		TenkeyButton VK_4 =
			new TenkeyButton(
				this,
				GraphicManager.get("/images/tenkey/4.png"),
				"4");
		TenkeyButton VK_5 =
			new TenkeyButton(
				this,
				GraphicManager.get("/images/tenkey/5.png"),
				"5");
		TenkeyButton VK_6 =
			new TenkeyButton(
				this,
				GraphicManager.get("/images/tenkey/6.png"),
				"6");
		TenkeyButton VK_7 =
			new TenkeyButton(
				this,
				GraphicManager.get("/images/tenkey/7.png"),
				"7");
		TenkeyButton VK_8 =
			new TenkeyButton(
				this,
				GraphicManager.get("/images/tenkey/8.png"),
				"8");
		TenkeyButton VK_9 =
			new TenkeyButton(
				this,
				GraphicManager.get("/images/tenkey/9.png"),
				"9");
		TenkeyButton VK_MINUS =
			new TenkeyButton(
				this,
				GraphicManager.get("/images/tenkey/-.png"),
				"-");
		TenkeyButton VK_PERIOD =
			new TenkeyButton(
				this,
				GraphicManager.get("/images/tenkey/f.png"),
				".");
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
		tenKey.add(VK_MINUS);
		tenKey.add(VK_PERIOD);
		keyPanel.add(tenKey, BorderLayout.CENTER);

		setManipulatePanel(keyPanel);

		getContentPane().add(displayBox, BorderLayout.NORTH);
		getContentPane().add(keyPanel, BorderLayout.CENTER);
	}

	protected abstract void setManipulatePanel(JPanel keyPanel);

	/**
	 * �e�{�^���̉������̓�����������܂��B
	 */
	public void actionPerformed(ActionEvent e) {
		logger.info("actionPerformed�J�n");
		((DialogButton) e.getSource()).pushButton();
	}

	public JSpinner.NumberEditor getEditor() {
		logger.info("getEditor�J�n");
		return (JSpinner.NumberEditor) spinner.getEditor();
	}

	public void setSymbol(TenkeyEditable symbol) {
		logger.info("setSymbol�J�n");
		this.symbol = symbol;
	}

	public Object getValue() {
		logger.info("getValue�J�n");
		return spinner.getValue();
	}

	public void setValue(String value) {
		logger.info("setValue�J�n");
		symbol.setValue(value);
	}

	public boolean hasSymbol() {
		logger.info("hasSymbol�J�n");
		return null != symbol;
	}

	/**
	 * �_�C�A���O�ɕ\������{�^���̊��N���X�ł��B
	 */
	private static abstract class DialogButton extends JButton {
		private final Logger logger = Logger.getLogger(DialogButton.class);
		/** �e�_�C�A���O�̎Q�Ƃł��B */
		protected SpinnerDialog dialog;

		/**
		 * �A�C�R���{�^�����쐬����R���X�g���N�^�ł��B
		 * 
		 * @param dialog �e�_�C�A���O�̎Q��
		 * @param icon �{�^���ɕ\������A�C�R��
		 */
		protected DialogButton(SpinnerDialog dialog, Icon icon) {
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
		protected DialogButton(SpinnerDialog dialog, String text) {
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

			Action key = new AbstractAction(textValue) {
				private static final long serialVersionUID =
					998365883073322803L;

				public void actionPerformed(ActionEvent e) {
					pushButton();
				}
			};

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
			logger.info("setInoutKeyMap�J�n");
			return KeyStroke.getKeyStroke(textValue);
		}

	}

	/**
	 * �e���L�[�_�C�A���O�Ŏg�p����{�^���N���X�ł��B 0 ���� 9 �ƃ}�C�i�X�A�s���I�h��\���܂��B
	 */
	private static class TenkeyButton extends DialogButton {
		private static final long serialVersionUID = 6388085380771446638L;
		private final Logger logger = Logger.getLogger(TenkeyButton.class);
		/** �{�^�������ɂ���đ}�����镶�� */
		private final String textValue;

		/**
		 * �R���X�g���N�^�B �w�肳�ꂽ�A�C�R���Ń{�^����\�����܂��B
		 * 
		 * @param dialog �e�_�C�A���O�̎Q��
		 * @param icon �{�^���ɕ\������A�C�R��
		 * @param textValue �{�^�������ɂ���đ}�����镶��
		 */
		public TenkeyButton(SpinnerDialog dialog, Icon icon, String textValue) {
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
			JSpinner.NumberEditor editer = dialog.getEditor();
			JFormattedTextField field = editer.getTextField();
			try {
				if (field.getSelectedText() != null) {
					field.getDocument().remove(
						field.getSelectionStart(),
						(field.getSelectionEnd() - field.getSelectionStart()));
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
			logger.info("getKeyStroke�J�n");
			return KeyStroke.getKeyStroke("typed " + textValue);
		}
	}

	/**
	 * OK�{�^���N���X�ł��B
	 */
	protected static class OkButton extends DialogButton {
		private static final long serialVersionUID = 8375079217654206011L;
		private final Logger logger = Logger.getLogger(OkButton.class);
		private final OKAction action;

		public OkButton(SpinnerDialog dialog, String text) {
			super(dialog, text);
			action = new OKAction(dialog);
			setInoutKeyMap("ENTER");
		}

		public void pushButton() {
			logger.info("pushButton�J�n");

			if (ConfirmUtil.isConfirm((Component) dialog)) {
				try {
					action.doAction();
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(
						this,
						"���͒l��MIN��������MAX����ł��B",
						this.getClass().getName(),
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				dialog.dispose();
			}
		}
	}

	/**
	 * Cancel�{�^���N���X�ł��B
	 */
	protected static class CancelButton extends DialogButton {
		private static final long serialVersionUID = -954344042797555189L;

		public CancelButton(
				SpinnerDialog dialog,
				String text,
				PageChanger changer) {
			super(dialog, text);
			setInoutKeyMap("ESCAPE");
			ActionMapUtil.setActionMap(this, changer);
		}

		public void pushButton() {
			dialog.dispose();
		}
	}

	/**
	 * �O���ڃ{�^���N���X�ł��B
	 */
	protected static class PreviousButton extends DialogButton {
		private static final long serialVersionUID = 8218856692075877374L;
		private final Logger logger = Logger.getLogger(PreviousButton.class);
		private final OKAction action;

		public PreviousButton(SpinnerDialog dialog, String text) {
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
				try {
					action.doAction();
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(
						this,
						"���͒l��MIN��������MAX����ł��B",
						this.getClass().getName(),
						JOptionPane.ERROR_MESSAGE);
					return;
				}

				for (int index = dialog.listIterator().previousIndex(), current =
					Integer.MAX_VALUE; index != current; current =
					dialog.listIterator().previousIndex()) {
					TenkeyEditable te =
						(TenkeyEditable) dialog.listIterator().previous();
					if (!te.isTabkeyMove()) {
						continue;
					} else {
						dialog.setSymbol(te);
						break;
					}
				}

				dialog.show();
			}
		}
	}

	/**
	 * �����ڃ{�^���N���X�ł��B
	 */
	protected static class NextButton extends DialogButton {
		private static final long serialVersionUID = -8375256752119877418L;
		private final Logger logger = Logger.getLogger(NextButton.class);
		private final OKAction action;

		public NextButton(SpinnerDialog dialog, String text) {
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
				try {
					action.doAction();
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(
						this,
						"���͒l��MIN��������MAX����ł��B",
						this.getClass().getName(),
						JOptionPane.ERROR_MESSAGE);
					return;
				}

				for (int index = dialog.listIterator().nextIndex(), current =
					Integer.MIN_VALUE; index != current; current =
					dialog.listIterator().nextIndex()) {
					TenkeyEditable te =
						(TenkeyEditable) dialog.listIterator().next();
					if (!te.isTabkeyMove()) {
						continue;
					} else {
						dialog.setSymbol(te);
						break;
					}
				}
				dialog.show();
			}
		}
	}

	protected static class OKAction {
		private final Logger logger = Logger.getLogger(OKAction.class);
		/** �e�_�C�A���O�̎Q�Ƃł��B */
		private final SpinnerDialog dialog;

		OKAction(SpinnerDialog dialog) {
			this.dialog = dialog;
		}

		void doAction() throws ParseException {
			logger.info("doAction�J�n");

			JSpinner.NumberEditor editer = dialog.getEditor();
			JFormattedTextField field = editer.getTextField();
			field.commitEdit();

			String value = editer.getFormat().format(dialog.getValue());
			if (dialog.hasSymbol()) {
				dialog.setValue(value);
			}
		}
	}
}
