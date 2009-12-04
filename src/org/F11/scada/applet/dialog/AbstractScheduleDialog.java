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
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.schedule.GraphicScheduleViewCreator;
import org.F11.scada.applet.schedule.ScheduleModel;
import org.F11.scada.applet.symbol.ScheduleEditable;
import org.F11.scada.applet.symbol.SymbolCollection;
import org.F11.scada.applet.symbol.TenkeyEditable;
import org.F11.scada.applet.symbol.ValueSetter;
import org.F11.scada.util.FontUtil;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;

/**
 * @author hori
 */
abstract public class AbstractScheduleDialog extends WifeDialog implements
		SymbolCollection, ActionListener {
	/** �ҏW�ΏۃV���{�� */
	private ScheduleEditable symbol;
	/** �O���[�v�ԍ��̃p�l���ł� */
	private JPanel groupNoPanel;
	/** �O���[�v�ԍ��{�^���ł� */
	private JButton grupNoButton;

	private final boolean isSort;
	private final boolean isLenient;
	/** ���M���O�N���X�ł� */
	private final Logger logger =
		Logger.getLogger(AbstractScheduleDialog.class);
	private final PageChanger changer;
	private GraphicScheduleViewCreator view;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param frame �e�̃t���[���ł�
	 */
	protected AbstractScheduleDialog(
			Frame frame,
			boolean isSort,
			boolean isLenient,
			PageChanger changer) {
		super(frame);
		this.isSort = isSort;
		this.isLenient = isLenient;
		this.changer = changer;
		init();
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param dialog �e�̃_�C�A���O�ł�
	 */
	protected AbstractScheduleDialog(
			Dialog dialog,
			boolean isSort,
			boolean isLenient,
			PageChanger changer) {
		super(dialog);
		this.isSort = isSort;
		this.isLenient = isLenient;
		this.changer = changer;
		init();
	}

	/**
	 * ���������ł��B
	 */
	private void init() {
		JPanel subPanel = new JPanel(new BorderLayout());
		JPanel manipulatePanel = new JPanel(new FlowLayout());
		OkButton okButton = new OkButton(this, "OK");
		CancelButton cancelButton = new CancelButton(this, "Cancel", changer);
		manipulatePanel.add(okButton);
		manipulatePanel.add(cancelButton);
		subPanel.add(manipulatePanel, BorderLayout.EAST);

		groupNoPanel = new JPanel(new FlowLayout());
		subPanel.add(groupNoPanel, BorderLayout.CENTER);

		getContentPane().add(subPanel, BorderLayout.SOUTH);
	}

	/**
	 * ���̃_�C�A���O��\�����܂��B
	 */
	public void show() {
		Rectangle dialogBounds = getBounds();
		dialogBounds.setLocation(symbol.getPoint());
		setLocation(WifeUtilities.getInScreenPoint(screenSize, dialogBounds));
		setDialogValue();
		setDefaultFocus();
		super.show();
	}

	public void dispose() {
		if (null != view) {
			view.removePropertyChangeListeners();
		}
		setDefaultFocus();
		super.dispose();
	}

	public void selectAll() {
	}

	/**
	 * �ҏW�\�A�i���O�I�u�W�F�N�g��ݒ肵�܂��B
	 */
	private void setDialogValue() {
		view =
			createView(symbol.getScheduleModel(), isSort, isLenient, changer);
		JComponent viewPanel = view.createView();
		getContentPane().add(viewPanel, BorderLayout.CENTER);

		grupNoButton = new GroupNoButton(this, symbol.getValue(), changer);
		FontUtil.setFont("SansSerif", "PLAIN", 16, grupNoButton);
		JLabel groupLabel = new JLabel("�O���[�vNo : ");
		FontUtil.setFont("SansSerif", "PLAIN", 16, groupLabel);
		groupNoPanel.add(groupLabel);
		groupNoPanel.add(grupNoButton);
	}

	abstract public GraphicScheduleViewCreator createView(
			ScheduleModel scheduleModel,
			boolean isSort,
			boolean isLenient,
			PageChanger changer);

	/**
	 * �C�e���[�^�[���Z�b�g���܂�
	 * 
	 * @param listIterator �ҏW�\�V���{���̃C�e���[�^�[
	 */
	public void setListIterator(ListIterator listIterator) {
		symbol = (ScheduleEditable) listIterator.next();
	}

	/**
	 * �e�{�^���̉������̓�����������܂��B
	 */
	public void actionPerformed(ActionEvent e) {
		((DialogButton) e.getSource()).pushButton();
	}

	/**
	 * �R���|�[�l���g��̎����V���{���C�e���[�^�[��Ԃ��܂��B
	 * 
	 * @param para �C�ӂ̃p�����[�^�[
	 */
	public ListIterator listIterator(List para) {
		List list = new ArrayList();
		list.add(grupNoButton);
		return new ScheduleIterator(para, list);
	}

	/**
	 * �����V���{���C�e���[�^�[�N���X�ł��B
	 */
	private static final class ScheduleIterator implements ListIterator {
		private final Logger logger = Logger.getLogger(ScheduleIterator.class);
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
		ScheduleIterator(List para, List buttonList) {
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
	 * �_�C�A���O�ɕ\������{�^���̊��N���X�ł��B
	 */
	private static abstract class DialogButton extends JButton {
		/** �e�_�C�A���O�̎Q�Ƃł��B */
		protected AbstractScheduleDialog dialog;

		/**
		 * �e�L�X�g�\���{�^�����쐬����R���X�g���N�^�ł��B
		 * 
		 * @param dialog �e�_�C�A���O�̎Q��
		 * @param text �{�^���ɕ\������e�L�X�g
		 */
		protected DialogButton(AbstractScheduleDialog dialog, String text) {
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
		 * ���z�֐��ł��B �T�u�N���X�ŁA�{�^�����������ꂽ�Ƃ��́A�������L�q���܂��B
		 */
		abstract public void pushButton();
	}

	/**
	 * OK�{�^���N���X�ł��B
	 */
	private static class OkButton extends DialogButton {
		private static final long serialVersionUID = -3466462930674647017L;
		private final Logger logger = Logger.getLogger(OkButton.class);

		public OkButton(AbstractScheduleDialog dialog, String text) {
			super(dialog, text);
		}

		public void pushButton() {
			dialog.symbol.getScheduleModel().setValue();
			dialog.symbol.getScheduleModel().writeData();
			dialog.symbol.setValue(dialog.grupNoButton.getText());
			dialog.dispose();
		}
	}

	/**
	 * Cancel�{�^���N���X�ł��B
	 */
	private static class CancelButton extends DialogButton {
		private static final long serialVersionUID = 6331220843398896457L;
		private final Logger logger = Logger.getLogger(CancelButton.class);

		public CancelButton(
				AbstractScheduleDialog dialog,
				String text,
				PageChanger changer) {
			super(dialog, text);
			ActionMapUtil.setActionMap(this, changer);
		}

		public void pushButton() {
			dialog.symbol.getScheduleModel().undoData();
			dialog.dispose();
		}
	}

	/**
	 * �O���[�v�ԍ��ݒ�p�̃{�^���N���X�ł�
	 */
	private final static class GroupNoButton extends DialogButton implements
			TenkeyEditable {
		private static final long serialVersionUID = -3613346010335544912L;
		private final Logger logger = Logger.getLogger(GroupNoButton.class);
		/** �O���[�v�ԍ� */
		private String groupNo;
		private final PageChanger changer;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param dialog �X�P�W���[�������ݒ�_�C�A���O�̎Q��
		 * @param time ����
		 * @param hour ���ԁE���̎��
		 */
		GroupNoButton(
				AbstractScheduleDialog scheduleDialog,
				String groupNo,
				PageChanger changer) {
			super(scheduleDialog, groupNo);
			this.groupNo = groupNo;
			this.changer = changer;
			init();
		}

		/**
		 * �e����������
		 */
		private void init() {
			DecimalFormat fmt = new DecimalFormat(getFormatString());
			setText(fmt.format(new Integer(groupNo)));
			Border bb = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
			Border eb = BorderFactory.createEmptyBorder(1, 3, 1, 3);
			setBorder(new CompoundBorder(bb, eb));
			setOpaque(true);
			setBackground(Color.WHITE);
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
			WifeDialog d = DialogFactory.get(window, "1", changer);
			if (d == null)
				logger.warn(this.getClass().getName()
					+ " : scheduleDialog null");
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
			setText(value);
		}

		/**
		 * �ŏ��l��Ԃ��܂�
		 */
		public double getConvertMin() {
			return dialog.symbol.getConvertValue().getConvertMin();
		}

		/**
		 * �ő�l��Ԃ��܂�
		 */
		public double getConvertMax() {
			return dialog.symbol.getConvertValue().getConvertMax();
		}

		/**
		 * ���l�\���t�H�[�}�b�g�������Ԃ��܂�
		 */
		public String getFormatString() {
			return dialog.symbol.getConvertValue().getPattern();
		}

		/**
		 * �����C���{�^�����������ꂽ���̏������L�q���܂��B
		 */
		public void pushButton() {
			List para = new ArrayList();
			para.add(new Integer(0));
			WifeDialog tenkeyDialog = getDialog(this.dialog, this.dialog, para);
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
			return "�O���[�vNo.";
		}
	}
}
