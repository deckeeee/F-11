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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.symbol.Analog4Editable;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.applet.symbol.HandCursorListener;
import org.F11.scada.applet.symbol.SymbolCollection;
import org.F11.scada.applet.symbol.TenkeyEditable;
import org.F11.scada.applet.symbol.ValueSetter;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;

/**
 * �A�i���O�S�f�[�^���̓_�C�A���O�N���X�ł�
 * 
 * @author hori
 */
public class UDLimitDialog extends WifeDialog implements SymbolCollection,
		ActionListener {
	private static final long serialVersionUID = -9125442703863418392L;
	/** �l���x���̔z��ł� */
	private String[] valueTitle;
	/** �l�{�^���̃��X�g�ł� */
	private List buttonList;
	/** �ҏW�ΏۃV���{�� */
	private Analog4Editable symbol;
	/** �e�_�C�A���O�̃^�C�g�������� */
	private String[] dialogTitles;

	private final PageChanger changer;

	/** ���M���O�N���X�ł� */
	private final Logger logger = Logger.getLogger(UDLimitDialog.class);

	/**
	 * �R���X�g���N�^
	 * 
	 * @param frame �e�̃t���[���ł�
	 */
	public UDLimitDialog(
			Frame frame,
			String labels,
			String titles,
			PageChanger changer) {
		super(frame);
		this.changer = changer;
		setLabels(labels);
		setTitles(titles);
		init();
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param dialog �e�̃_�C�A���O�ł�
	 */
	public UDLimitDialog(
			Dialog dialog,
			String labels,
			String titles,
			PageChanger changer) {
		super(dialog);
		this.changer = changer;
		setLabels(labels);
		setTitles(titles);
		init();
	}

	/**
	 * ���������ł��B
	 */
	private void init() {
		buttonList = new ArrayList();
		getContentPane().add(createValuePanel(), BorderLayout.CENTER);
		getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
	}

	private void setLabels(String labels) {
		StringTokenizer st = new StringTokenizer(getLabels(labels), "|");
		valueTitle = new String[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			valueTitle[i] = st.nextToken();
		}
	}

	private String getLabels(String labels) {
		return (null == labels || "".equals(labels))
			? "����x�� ON :|OFF :|�����x�� ON :|OFF :"
			: labels;
	}

	private void setTitles(String titles) {
		StringTokenizer st = new StringTokenizer(getTitles(titles), "|");
		dialogTitles = new String[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			dialogTitles[i] = st.nextToken();
		}
	}

	private String getTitles(String titles) {
		return (null == titles || "".equals(titles))
			? "�㉺��|�㉺��|�㉺��|�㉺��"
			: titles;
	}

	private JComponent createValuePanel() {
		JPanel basePanel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;

		for (int i = 0; i < valueTitle.length; i++) {
			JPanel panelL = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			panelL.add(new JLabel(valueTitle[i]));
			c.gridy = i;
			c.gridx = 0;
			basePanel.add(panelL, c);

			ValueButton b = new ValueButton(this, dialogTitles[i], changer);
			buttonList.add(b);
			JPanel panelB = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panelB.add(b);
			c.gridx = 1;
			basePanel.add(panelB, c);
		}

		return basePanel;
	}

	private JComponent createButtonPanel() {
		JComponent okButton = new OkButton(this, "OK");
		JComponent cancelButton = new CancelButton(this, "Cancel", changer);

		JPanel basePanel = new JPanel(new FlowLayout());
		basePanel.add(okButton);
		basePanel.add(cancelButton);
		return basePanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.applet.dialog.WifeDialog#setListIterator(java.util.ListIterator
	 * )
	 */
	public void setListIterator(ListIterator listIterator) {
		// ��ڂ̃V���{����ݒ肵�܂��B
		symbol = (Analog4Editable) listIterator.next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		AbstractAnalog4Button button = (AbstractAnalog4Button) e.getSource();
		button.pushButton();
	}

	private void setValue() {
		String values[] = new String[buttonList.size()];
		int i = 0;
		for (Iterator it = buttonList.iterator(); it.hasNext(); i++) {
			values[i] = ((ValueButton) it.next()).getText();
		}
		symbol.setValue(values);
	}

	/**
	 * ���̃_�C�A���O��\�����܂��B
	 */
	public void show() {
		Rectangle dialogBounds = getBounds();
		dialogBounds.setLocation(symbol.getPoint());
		setLocation(WifeUtilities.getInScreenPoint(screenSize, dialogBounds));

		String[] value = symbol.getValues();
		int i = 0;
		for (Iterator it = buttonList.iterator(); it.hasNext(); i++) {
			ValueButton b = (ValueButton) it.next();
			b.setText(value[i]);
		}

		setDefaultFocus();
		super.show();
	}

	public void selectAll() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.applet.symbol.SymbolCollection#listIterator(java.util.List)
	 */
	public ListIterator listIterator(List para) {
		return new Analog4Iterator(para, buttonList);
	}

	/**
	 * �����V���{���C�e���[�^�[�N���X�ł��B
	 */
	private static final class Analog4Iterator implements ListIterator {
		private final Logger logger = Logger.getLogger(Analog4Iterator.class);
		/** �V���{���̃��X�g�̎Q�Ƃł� */
		private List symbols;
		/** ���X�g�C�e���[�^�[�ł� */
		private ListIterator listIterator;
		/** ���E�t�����[�h�̕ێ� */
		private boolean isPreviousMode;
		/** �N���b�N���ꂽ�{�^���̃C���f�b�N�X�ł� */
		private int startIndex;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param para �C�ӂ̃p�����[�^�[
		 */
		Analog4Iterator(List para, List buttonList) {
			symbols = new ArrayList(buttonList);
			startIndex = ((Integer) para.get(0)).intValue();
		}

		public boolean hasNext() {
			return true;
		}

		public Object next() {
			if (listIterator == null)
				listIterator = symbols.listIterator(startIndex);

			if (isPreviousMode) {
				isPreviousMode = false;
				try {
					listIterator.next();
				} catch (NoSuchElementException ex) {
					listIterator = symbols.listIterator(symbols.size());
					listIterator.next();
				}
			}

			try {
				return listIterator.next();
			} catch (NoSuchElementException ex) {
				listIterator = symbols.listIterator();
				return listIterator.next();
			}
		}

		public boolean hasPrevious() {
			return true;
		}

		public Object previous() {
			if (listIterator == null)
				listIterator = symbols.listIterator(symbols.size());
			if (!isPreviousMode) {
				isPreviousMode = true;
				try {
					listIterator.previous();
				} catch (NoSuchElementException ex) {
					listIterator = symbols.listIterator(symbols.size());
					listIterator.previous();
				}
			}

			try {
				return listIterator.previous();
			} catch (NoSuchElementException ex) {
				listIterator = symbols.listIterator(symbols.size());
				return listIterator.previous();
			}
		}

		public int nextIndex() {
			int index = listIterator.nextIndex();
			if (isPreviousMode && index == symbols.size()) {
				ListIterator lit = symbols.listIterator();
				index = lit.nextIndex();
			}
			return index;
		}

		public int previousIndex() {
			int index = listIterator.previousIndex();
			if (!isPreviousMode && index < 0) {
				ListIterator lit = symbols.listIterator(symbols.size());
				index = lit.previousIndex();
			}
			return index;
		}

		public void add(Object o) {
			// non suport
			throw new UnsupportedOperationException();
		}

		public void remove() {
			// non suport
			throw new UnsupportedOperationException();
		}

		public void set(Object o) {
			// non suport
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * �A�i���O�S�f�[�^���̓_�C�A���O�̃{�^���̊��N���X�ł�
	 */
	private abstract static class AbstractAnalog4Button extends JButton {
		protected final Logger logger =
			Logger.getLogger(AbstractAnalog4Button.class);
		/** �e���L�[�_�C�A���O�̎Q�Ƃł� */
		protected UDLimitDialog parent;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param dialog �X�P�W���[���_�C�A���O�̎Q��
		 */
		protected AbstractAnalog4Button(UDLimitDialog parent) {
			this.parent = parent;
		}

		/**
		 * ���̃{�^���ɑΉ��Â���L�[�}�b�v���`���܂��B
		 * 
		 * @param textValue �Ή��Â���L�[(VK_�����̕���)
		 */
		protected void setInoutKeyMap(String textValue) {
			Action key = new AbstractAction(textValue) {

				private static final long serialVersionUID =
					-7608204746374153424L;

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
		 * �e���L�[�Ƃ���ȊO�Ŕ����ɈقȂ�̂ŁA�����L�[�̓T�u�N���X�ŃI�[�o�[���C�h���܂��B
		 * 
		 * @return KeyStroke �̏����Ɉˑ����Ă��܂��B
		 */
		protected KeyStroke getKeyStroke(String textValue) {
			return KeyStroke.getKeyStroke(textValue);
		}

		/**
		 * ���z���\�b�h�ł��B �{�^�����������ꂽ���̏������L�q���܂��B
		 */
		abstract public void pushButton();
	}

	/**
	 * OK �{�^���N���X�ł�
	 */
	private final static class OkButton extends AbstractAnalog4Button {
		private static final long serialVersionUID = -8998952770311421202L;

		OkButton(UDLimitDialog parent, String title) {
			super(parent);
			addActionListener(this.parent);
			setText(title);
			setInoutKeyMap("ENTER");
		}

		/**
		 * �{�^�����������ꂽ���̏������L�q���܂��B
		 */
		public void pushButton() {
			this.parent.setValue();
			this.parent.dispose();
		}
	}

	/**
	 * CANCEL �{�^���N���X�ł�
	 */
	private final static class CancelButton extends AbstractAnalog4Button {
		private static final long serialVersionUID = 1902621274602131022L;

		CancelButton(UDLimitDialog parent, String title, PageChanger changer) {
			super(parent);
			addActionListener(this.parent);
			setText(title);
			setInoutKeyMap("ESCAPE");
			ActionMapUtil.setActionMap(this, changer);
		}

		/**
		 * �{�^�����������ꂽ���̏������L�q���܂��B
		 */
		public void pushButton() {
			this.parent.dispose();
		}
	}

	/**
	 * �l�ݒ�p�̃{�^���N���X�ł�
	 */
	private final static class ValueButton extends AbstractAnalog4Button
			implements TenkeyEditable {

		private static final long serialVersionUID = 2282230004915772778L;

		private final String dialogTitle;

		private final PageChanger changer;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param dialog �X�P�W���[�������ݒ�_�C�A���O�̎Q��
		 * @param time ����
		 * @param hour ���ԁE���̎��
		 */
		ValueButton(
				UDLimitDialog parent,
				String dialogTitle,
				PageChanger changer) {
			super(parent);
			this.dialogTitle = dialogTitle;
			this.changer = changer;
			init();
			addMouseListener(new HandCursorListener());
		}

		/**
		 * �e����������
		 */
		private void init() {
			Border bb = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
			Border eb = BorderFactory.createEmptyBorder(1, 3, 1, 3);
			setBorder(new CompoundBorder(bb, eb));
			setOpaque(true);
			setBackground(ColorFactory.getColor("WHITE"));
			addActionListener(this.parent);
			this.setPreferredSize(new Dimension(60, 22));
		}

		/**
		 * �ҏW����ׂ̃_�C�A���O��Ԃ��܂��B
		 * 
		 * @param window �e�E�B���h�E
		 * @param collection �x�[�X�N���X�̃C���X�^���X
		 * @param �C�ӂ̃p�����[�^���X�g
		 * @todo �C�ӂ̃p�����[�^�͂��������A�^����������ׂ������B
		 */
		public WifeDialog getDialog(
				Window window,
				SymbolCollection collection,
				List para) {
			WifeDialog d =
				DialogFactory.get(
					window,
					parent.symbol.getSecondDialogName(),
					changer);
			if (d == null)
				logger
					.warn(this.getClass().getName() + " : UDLimitDialog null");
			d.setListIterator(collection.listIterator(para));
			return d;
		}

		/**
		 * �ݒ�_�C�A���O�̍���� Point �I�u�W�F�N�g��Ԃ��܂��B
		 */
		public Point getPoint() {
			Point p = this.getLocationOnScreen();
			p.y += getSize().height;
			return p;
		}

		/**
		 * �ݒ�_�C�A���O�̍���� Point �I�u�W�F�N�g��ݒ肵�܂��B
		 * 
		 * @param point �ݒ�_�C�A���O�̍���� Point
		 */
		public void setPoint(Point point) {
			// NOP
		}

		/**
		 * �V���{���̒l��Ԃ��܂�
		 */
		public String getValue() {
			return getText();
		}

		/**
		 * �V���{���ɒl��ݒ肵�܂�
		 */
		public void setValue(String value) {
			ConvertValue conv = parent.symbol.getConvertValue();
			double in = conv.convertInputValue(value);
			setVisible(false);
			setText(conv.convertStringValue(in, getFormatString()));
			setVisible(true);
		}

		/**
		 * �ŏ��l��Ԃ��܂�
		 */
		public double getConvertMin() {
			ConvertValue conv = parent.symbol.getConvertValue();
			return conv.getConvertMin();
		}

		/**
		 * �ő�l��Ԃ��܂�
		 */
		public double getConvertMax() {
			ConvertValue conv = parent.symbol.getConvertValue();
			return conv.getConvertMax();
		}

		/**
		 * ���l�\���t�H�[�}�b�g�������Ԃ��܂�
		 */
		public String getFormatString() {
			return parent.symbol.getFormatString();
		}

		/**
		 * �l�C���{�^�����������ꂽ���̏������L�q���܂��B
		 */
		public void pushButton() {
			List para = new ArrayList();
			para.add(new Integer(this.parent.buttonList.indexOf(this)));
			WifeDialog tenkeyDialog =
				getDialog(this.parent, this.parent, para);
			tenkeyDialog.show();
			// logger.info("" + buttonList.indexOf(evt.getSource()));
		}

		public void setEditable(boolean[] editable) {
		}

		public boolean isEditable() {
			return true;
		}

		/*
		 * @see org.F11.scada.applet.symbol.Editable#getDestinations()
		 */
		public String[] getDestinations() {
			return new String[0];
		}

		/**
		 * �������ݐ�̒ǉ��͂��Ȃ��B
		 * 
		 * @see org.F11.scada.applet.symbol.Editable#addDestination(Map)
		 */
		public void addDestination(Map atts) {
		}

		/**
		 * �������ݐ�̒ǉ��͂��Ȃ��B
		 * 
		 * @see org.F11.scada.applet.symbol.Editable#addElement(Map)
		 */
		public void addValueSetter(ValueSetter setter) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
		 */
		public boolean isTabkeyMove() {
			return isVisible();
		}

		public String getDialogTitle() {
			return dialogTitle;
		}
	}

}
