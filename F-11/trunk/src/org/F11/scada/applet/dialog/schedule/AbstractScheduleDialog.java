package org.F11.scada.applet.dialog.schedule;

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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.applet.schedule.ScheduleRowModel;
import org.F11.scada.applet.symbol.SymbolCollection;

/**
 * �X�P�W���[�������ݒ�_�C�A���O�N���X�ł��B
 */
public abstract class AbstractScheduleDialog extends JDialog implements SymbolCollection, ActionListener {
	/** �X�P�W���[���f�[�^�̔C�ӂ̈�s��\���f�[�^���f���ł� */
	protected ScheduleRowModel model;
	/** �����{�^���̃��X�g�ł� */
	protected List buttonList;
	/** �e���L�[�_�C�A���O�̎Q�Ƃł� */
	protected WifeDialog tenkeyDialog;
	/** ���t�f�[�^�̃\�[�g�L�� */
	protected boolean isSort;
	/** ���t���͂̑召�`�F�b�N�̗L�� */
	protected boolean isLenient;

	/**
	 * �R���X�g���N�^
	 * @param frame �e�̃t���[���ł�
	 * @param model �X�P�W���[���f�[�^�̔C�ӂ̈�s��\���f�[�^���f��
	 */
	public AbstractScheduleDialog(Frame frame, ScheduleRowModel model, boolean isSort, boolean isLenient) {
		super(frame);
		this.model = model;
		this.isSort = isSort;
		this.isLenient = isLenient;
		init();
	}

	/**
	 * �R���X�g���N�^
	 * @param dialog �e�̃_�C�A���O�ł�
	 * @param model �X�P�W���[���f�[�^�̔C�ӂ̈�s��\���f�[�^���f��
	 */
	public AbstractScheduleDialog(Dialog dialog, ScheduleRowModel model, boolean isSort, boolean isLenient) {
		super(dialog);
		this.model = model;
		this.isSort = isSort;
		this.isLenient = isLenient;
		init();
	}

	/**
	 * �e�����������ł��B
	 */
	private void init() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setTitle(model.getDayIndexName());
		Container cont = getContentPane();
		cont.add(createTimePanel(), BorderLayout.CENTER);
		cont.add(createOkCancel(), BorderLayout.SOUTH);
	}

	/**
	 * �����ݒ�p�R���|�[�l���g�𐶐����܂��B
	 */
	private JComponent createTimePanel() {
		int columnSize = model.getColumnCount();

		JPanel timePanel = new JPanel(new GridBagLayout());
		timePanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 1; c.gridy = 0;
		c.gridwidth = 3; c.gridheight = 1;
		c.weightx = 1.0; c.weighty = 1.0;
		c.insets = new Insets(20, 0, 10, 0);
		JLabel onLabel = new JLabel("ON����");
		timePanel.add(onLabel, c);

		c.gridx = 5; c.gridy = 0;
		c.insets = new Insets(20, 20, 10, 0);
		JLabel offLabel = new JLabel("OFF����");
		timePanel.add(offLabel, c);

		for (int i = 0; i < columnSize; i++) {
			createRow(timePanel, i);
		}

		return timePanel;
	}

	/**
	 * �����ݒ�p�R���|�[�l���g�𐶐����܂��B
	 */
	private void createRow(Container cont, int n) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0; c.weighty = 1.0;

		int display = n + 1;
		int insetSize = 1;
		int insetBottom = 15;

		c.gridx = 0; c.gridy = display;
		c.insets = new Insets(insetSize, insetSize, insetBottom, 20);
		JLabel numLabel = new JLabel(display + "���");
		cont.add(numLabel, c);

		c.gridx = 1; c.gridy = display;
		c.insets = new Insets(insetSize, insetSize, insetBottom, insetSize);
		JButton b1 = createTimeButton(this, model.getOnTime(n), true);
		cont.add(b1, c);

		c.gridx = 2; c.gridy = display;
		JLabel onColon = new JLabel("�F");
		onColon.setHorizontalAlignment(SwingConstants.CENTER);
		cont.add(onColon, c);

		c.gridx = 3; c.gridy = display;
		JButton b2 = createTimeButton(this, model.getOnTime(n), false);
		cont.add(b2, c);

		c.gridx = 5; c.gridy = display;
		c.insets = new Insets(insetSize, 20, insetBottom, insetSize);
		JButton b3 = createTimeButton(this, model.getOffTime(n), true);
		cont.add(b3, c);

		c.gridx = 6; c.gridy = display;
		c.insets = new Insets(insetSize, insetSize, insetBottom, insetSize);
		JLabel offColon = new JLabel("�F");
		offColon.setHorizontalAlignment(SwingConstants.CENTER);
		cont.add(offColon, c);

		c.gridx = 7; c.gridy = display;
		JButton b4 = createTimeButton(this, model.getOffTime(n), false);
		cont.add(b4, c);

		if (buttonList == null)
			buttonList = new ArrayList();

		buttonList.add(b1);
		buttonList.add(b2);
		buttonList.add(b3);
		buttonList.add(b4);
	}
	
	abstract protected JButton createTimeButton(
			AbstractScheduleDialog scheduleDialog, int time, boolean hour);

	/**
	 * OK / CANCEL �{�^���̃R���|�[�l���g�𐶐����܂��B
	 */
	private JComponent createOkCancel() {
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		buttonBox.add(Box.createHorizontalGlue());
		JButton okButton = createOkButton(this);
		buttonBox.add(okButton);

		CancelButton cancelButton = new CancelButton(this);
		buttonBox.add(cancelButton);
		return buttonBox;
	}
	
	abstract protected JButton createOkButton(AbstractScheduleDialog scheduleDialog);

	/**
	 * �e�{�^���̏��������s���܂��B
	 */
	public void actionPerformed(ActionEvent evt) {
		AbstractScheduleButton button = (AbstractScheduleButton)evt.getSource();
		button.pushButton();
	}

	/**
	 * �R���|�[�l���g��̎����V���{���C�e���[�^�[��Ԃ��܂��B
	 * @param para �C�ӂ̃p�����[�^�[
	 */
	public ListIterator listIterator(List para) {
		return new ScheduleIterator(para, buttonList);
	}

	/**
	 * �����V���{���C�e���[�^�[�N���X�ł��B
	 */
	private static final class ScheduleIterator implements ListIterator {
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
		 * @param para �C�ӂ̃p�����[�^�[
		 */
		ScheduleIterator(List para, List buttonList) {
			symbols = new ArrayList(buttonList);
			startIndex = ((Integer)para.get(0)).intValue();
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
}
