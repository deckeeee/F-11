/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/schedule/WifeCalendar.java,v 1.13.2.9 2006/04/11 07:27:53 frdm Exp $
 * $Revision: 1.13.2.9 $
 * $Date: 2006/04/11 07:27:53 $
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

package org.F11.scada.applet.schedule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.SwingPropertyChangeSupport;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.applet.symbol.Editable;
import org.F11.scada.applet.symbol.HandCursorListener;
import org.F11.scada.applet.symbol.ReferencerOwnerSymbol;
import org.F11.scada.applet.symbol.ScrollableBasePanel;
import org.F11.scada.applet.symbol.SymbolCollection;
import org.F11.scada.applet.symbol.ValueSetter;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataCalendar;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.security.auth.login.Authenticationable;
import org.apache.log4j.Logger;

/**
 * �x���A������ݒ�J�����_�[�N���X�ł��B
 * @todo �\�����錎�̐ݒ�𕪗�����B�Ƃ肠������N�J�����_�[�Œ�B
 */
public class WifeCalendar {
	/**	���C����ʃR���|�[�l���g */
	private ScrollableBasePanel mainPanel;
	/** �{�^����ʃ}�l�[�W���[ */
	private DayTypeButtonManager buttonManager;

	private static Logger logger;

	/**
	 * �R���X�g���N�^
	 * @param alarmRef �����[�g�f�[�^�I�u�W�F�N�g
	 */
	public WifeCalendar(String dataProvider, String dataHolder) {
		this(dataProvider, dataHolder, null);
	}

	/**
	 * �R���X�g���N�^
	 * @param alarmRef �����[�g�f�[�^�I�u�W�F�N�g
	 */
	public WifeCalendar(
		String dataProvider,
		String dataHolder,
		Authenticationable authentication) {
		logger = Logger.getLogger(getClass().getName());

		mainPanel = new ScrollableBasePanel(new GridLayout(3, 4, 10, 10));

		final int year = 12;
		buttonManager = new DayTypeButtonManager(dataProvider, dataHolder, authentication);
		MonthPanelFactory mpf = new MonthPanelFactory(buttonManager);
		for (int i = 0; i < year; i++) {
			JComponent comp = mpf.next();
			mainPanel.add(comp);
		}
	}

	/**
	 * ���C����ʂ̃R���|�[�l���g��Ԃ��܂��B
	 */
	public JComponent getMainPanel() {
		JScrollPane scpane = new JScrollPane(mainPanel);
		return scpane;
	}

	/**
	 * �c�[���o�[��Ԃ��܂��B
	 */
	public JComponent getToolBar() {
		return buttonManager.getToolBar();
	}

	/**
	 * ��ԃ{�^�����Ǘ�����N���X�ł��B
	 */
	static class DayTypeButtonManager
		implements ActionListener, DataReferencerOwner, DataValueChangeListener {
		/** DataHolder�^�C�v���ł��B */
		private static final Class[][] WIFE_TYPE_INFO =
			new Class[][] { { DataHolder.class, WifeData.class }
		};
		/** ���݂̏�� */
		private DayTypeButton state;
		/** ��ԃ{�^���̃c�[���o�[ */
		private JToolBar toolBar;
		/** �J�����_�[�f�[�^�̎Q�� */
		private WifeDataCalendar dataCalendar;
		/** �J�����_�[�f�[�^�̎Q�� */
		private WifeDataCalendar undoDataCalendar;
		/** �v���p�e�B�`�F���W�T�|�[�g */
		private SwingPropertyChangeSupport changeSupport;
		/** ��������C�x���g���ł� */
		private static final String CANCEL_EVENT_NAME = "calendar_cancel";
		/** ��ԕ\���G���A */
		private JLabel stateLabel;
		/** ���b�Z�[�W�t�H�[�b�}�b�^�[ */
		private MessageFormat message;
		/** �f�[�^���t�@�����T */
		private DataReferencer dataReferencer;
		/** �F�؃R���g���[���̎Q�� */
		private Authenticationable authentication;

		DayTypeButtonManager(String dataProvider, String dataHolder) {
			dataReferencer = new DataReferencer(dataProvider, dataHolder);
			dataReferencer.connect(this);
			init();
		}

		DayTypeButtonManager(
			String dataProvider,
			String dataHolder,
			Authenticationable authentication) {
			this(dataProvider, dataHolder);
			this.authentication = authentication;
		}

		private void init() {
			toolBar = new JToolBar("Calendar");
			toolBar.setFloatable(false);

			try {
				DataHolder dh = dataReferencer.getDataHolder();
				WifeData wd = (WifeData) dh.getValue();
				if (wd instanceof WifeDataCalendar) {
					dataCalendar = (WifeDataCalendar) wd;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (logger.isDebugEnabled()) {
				logger.debug(dataCalendar.toString());
				logger.debug("" + dataCalendar.getWordSize());
			}
			undoDataCalendar = (WifeDataCalendar) dataCalendar.valueOf(dataCalendar.toByteArray());

			state = DayTypeButton.createHoliday();
			createToolBar(state);
//TODO �ő�������5���܂ł́A���������ɂ��邱��
			DecimalFormat fmt = new DecimalFormat("00");
			for (int i = 1, spCount = dataCalendar.getSpecialDayCount();
					i <= spCount && i <= 5; i++) {
				Method method;
				try {
					method =
						DayTypeButton.class.getMethod(
							"createSpecial" + fmt.format(i),
							new Class[0]);
					createToolBar((DayTypeButton) method.invoke(new Object(), new Object[0]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			toolBar.addSeparator();
			createStatusLabel();
			setStatusLabel();
		}

		private void createToolBar(DayTypeButton button) {
			toolBar.add(button);
			button.addActionListener(this);
		}

		private void createStatusLabel() {
			stateLabel = new JLabel();
			WifeUtilities.setFontSize(stateLabel, 1.4);
			toolBar.add(stateLabel);
			message = new MessageFormat("{0}�ݒ蒆�ł��B");
		}

		private void setStatusLabel() {
			Object[] m = { state.getMessage()};
			stateLabel.setText(message.format(m));
			stateLabel.setBackground(state.getColor());
			stateLabel.setOpaque(true);
		}

		/**
		 * �f�[�^�ύX�C�x���g����
		 */
		public void dataValueChanged(DataValueChangeEvent evt) {
			Object o = evt.getSource();
			if (logger.isDebugEnabled()) {
				logger.debug("Calendar : " + o);
			}
			if (!(o instanceof DataHolder)) {
				return;
			}
			if (dataCalendar != null && undoDataCalendar != null) {
				if (dataCalendar.equals(undoDataCalendar)) {
					WifeDataCalendar cal = (WifeDataCalendar) ((DataHolder) o).getValue();
					firePropertyChange(CANCEL_EVENT_NAME, dataCalendar, cal);
					dataCalendar = cal;
					undoDataCalendar = (WifeDataCalendar) dataCalendar.valueOf(cal.toByteArray());
				}
			}
		}

		/**
		 * DataHolder�^�C�v����Ԃ��܂��B
		 */
		public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
			return WIFE_TYPE_INFO;
		}

		Color getColor() {
			return state.getColor();
		}

		JToolBar getToolBar() {
			return toolBar;
		}

		boolean testBit(int month, int day) {
			return dataCalendar.testBit(state.getType(), month, day);
		}

		boolean setBit(int month, int day) {
			if (state == null)
				return false;
			dataCalendar = dataCalendar.setBit(state.getType(), month, day);
			return true;
		}

		Color searchColor(int month, int day) {
			Color color = null;
			for (Iterator it = values().iterator();
					it.hasNext();) {
				DayTypeButton button = (DayTypeButton) it.next();
				if (dataCalendar.testBit(button.getType(), month, day)) {
					color = button.getColor();
					break;
				}
			}
			return color;
		}

		List values() {
			int spCount = dataCalendar.getSpecialDayCount() + 1;
			ArrayList l = new ArrayList(spCount);
			for (int i = 0; i < spCount; i++) {
				l.add(DayTypeButton.VALUES.get(i));
			}
			return l;
		}

		public void actionPerformed(ActionEvent evt) {
			DayTypeButton button = (DayTypeButton) evt.getSource();
			state = button;
			setStatusLabel();
		}

		void addPropertyChangeListener(String eventName, PropertyChangeListener listener) {
			if (changeSupport == null) {
				changeSupport = new SwingPropertyChangeSupport(this);
			}
			changeSupport.addPropertyChangeListener(eventName, listener);
		}

		void removePropertyChangeListener(String eventName, PropertyChangeListener listener) {
			if (changeSupport == null) {
				return;
			}
			changeSupport.removePropertyChangeListener(eventName, listener);
		}

		void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
			if (changeSupport == null) {
				return;
			}
			changeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}
		
		public void disConnect() {
		    dataReferencer.disconnect(this);

		    if (changeSupport != null) {
			    PropertyChangeListener[] listeners = changeSupport.getPropertyChangeListeners();
			    for (int i = 0; i < listeners.length; i++) {
	                changeSupport.removePropertyChangeListener(listeners[i]);
	            }
		    }
		}
	}

	/**
	 * �ݒ肷��^�C�v��\���{�^���N���X�ł��B
	 */
	static final class DayTypeButton extends JButton {
	    private static final long serialVersionUID = -4266480488017783674L;
		static final DayTypeButton HOLIDAY = new DayTypeButton(DayType.HOLIDAY);
		static final DayTypeButton SPECIAL01 = new DayTypeButton(DayType.SPECIAL01);
		static final DayTypeButton SPECIAL02 = new DayTypeButton(DayType.SPECIAL02);
		static final DayTypeButton SPECIAL03 = new DayTypeButton(DayType.SPECIAL03);
		static final DayTypeButton SPECIAL04 = new DayTypeButton(DayType.SPECIAL04);
		static final DayTypeButton SPECIAL05 = new DayTypeButton(DayType.SPECIAL05);

		private static final DayTypeButton[] PRIVATE_VALUES =
			{ HOLIDAY, SPECIAL01, SPECIAL02, SPECIAL03, SPECIAL04, SPECIAL05, };
		static final List VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));
		/** �J�����_�[�ɐݒ肷����t�^�C�v�ł� */
		private DayType dayType;

		/**
		 * �v���C�x�[�g�R���X�g���N�^
		 */
		private DayTypeButton(DayType dayType) {
			super();
			this.dayType = dayType;
			this.setPreferredSize(dayType.getSize());
			this.setMinimumSize(getPreferredSize());
			this.setMaximumSize(getPreferredSize());
			init();
		}

		static DayTypeButton createHoliday() {
			return new DayTypeButton(DayType.HOLIDAY);
		}

		public static DayTypeButton createSpecial01() {
			return new DayTypeButton(DayType.SPECIAL01);
		}

		public static DayTypeButton createSpecial02() {
			return new DayTypeButton(DayType.SPECIAL02);
		}

		public static DayTypeButton createSpecial03() {
			return new DayTypeButton(DayType.SPECIAL03);
		}

		public static DayTypeButton createSpecial04() {
			return new DayTypeButton(DayType.SPECIAL04);
		}

		public static DayTypeButton createSpecial05() {
			return new DayTypeButton(DayType.SPECIAL05);
		}

		private void init() {
			setText(dayType.toString());
			setBackground(dayType.getColor());
			setOpaque(true);
		}

		/**
		 * ���t�f�[�^�^�C�v�̃C���f�b�N�X��Ԃ��܂��B
		 */
		int getType() {
			return dayType.getType();
		}

		/**
		 * ���t�f�[�^�^�C�v�̐F��Ԃ��܂��B
		 */
		Color getColor() {
			return dayType.getColor();
		}

		String getMessage() {
			return dayType.message;
		}

		/**
		 * �J�����_�[�Őݒ肷��A���t�̎�ނ�\���A�^�C�v�Z�[�t enum �N���X�ł��B
		 */
		static final class DayType {
			static final DayType HOLIDAY = new DayType(0, "�x", "�x��", "red", new Dimension(36, 36));
			static final DayType SPECIAL01 =
				new DayType(1, "��1", "�����1", "cyan", new Dimension(36, 36));
			static final DayType SPECIAL02 =
				new DayType(2, "��2", "�����2", "springgreen", new Dimension(36, 36));
			static final DayType SPECIAL03 =
				new DayType(3, "��3", "�����3", "dodgerblue", new Dimension(36, 36));
			static final DayType SPECIAL04 =
				new DayType(4, "��4", "�����4", "yellow", new Dimension(36, 36));
			static final DayType SPECIAL05 =
				new DayType(5, "��5", "�����5", "magenta", new Dimension(36, 36));

			private int type;
			private String name;
			private String message;
			private Color color;
			private Dimension size;

			private DayType(
				int type,
				String name,
				String message,
				String colorName,
				Dimension size) {
				this.type = type;
				this.name = name;
				this.message = message;
				this.color = ColorFactory.getColor(colorName);
				this.size = size;
			}

			Color getColor() {
				return color;
			}

			public String toString() {
				return name;
			}

			int getType() {
				return type;
			}

			Dimension getSize() {
				return size;
			}
		}
	}

	/**
	 * �ꌎ����\������ JPanel �𐶐�����N���X�ł��B
	 */
	static class MonthPanelFactory {
		/** ���݂̃J�����_�[�Q�� */
		private Calendar currentCalendar = Calendar.getInstance();
		/** �P�T�Ԃ̓��t�̐� */
		private static final int WEEK_COUNT = 7;
		/** �P���̍ő�\���\�s */
		private static final int MONTH_ROW_COUNT = 7;
		/** ���\���̃t�H�[�}�b�g������ */
		private String defaultMonthLabelFormat = "yyyy �N M ��";
		/** ��ԃ{�^���}�l�[�W���[�̎Q�Ƃł��B */
		private DayTypeButtonManager buttonManager;
		/** �����p�l���� */
		private int panelCount;
		/** �{�^���A�N�V�������s�^�C�}�[ */
		private final Timer timer = new Timer(true);

		/**
		 * �R���X�g���N�^
		 */
		public MonthPanelFactory(DayTypeButtonManager buttonManager) {
			this.buttonManager = buttonManager;
		}

		/**
		 * ���݂̃J�����_�[�p�l����Ԃ��A���t�������ɂ��܂��B
		 * @return ���݂̌��̃J�����_�[�p�l���I�u�W�F�N�g
		 */
		public MonthPanel next() {
			MonthPanel p = getMonthPanel();
			buttonManager.addPropertyChangeListener(DayTypeButtonManager.CANCEL_EVENT_NAME, p);
			currentCalendar.add(Calendar.MONTH, 1);
			return p;
		}

		/**
		 * ���݂̌��̃J�����_�[�p�l����Ԃ��܂��B
		 * @return ���݂̌��̃J�����_�[�p�l���I�u�W�F�N�g
		 */
		private MonthPanel getMonthPanel() {
			panelCount++;

			Calendar cal = (Calendar) currentCalendar.clone();
			DateFormat dform = new SimpleDateFormat(defaultMonthLabelFormat);

			MonthPanel monthPanel = new MonthPanel(new BorderLayout(), cal, buttonManager, timer);
			JLabel m = new JLabel(dform.format(cal.getTime()));
			m.setFont(m.getFont().deriveFont((float) (m.getFont().getSize2D() * 1.4)));
			if (panelCount == 1) {
				m.setBackground(ColorFactory.getColor("aquamarine"));
				m.setOpaque(true);
			}
			JPanel p = new JPanel(new GridLayout(MONTH_ROW_COUNT, WEEK_COUNT));
			JLabel[] weekLabels = createWeekLabels();
			JButton[][] dayButtons = createDayButtons(monthPanel, monthPanel);

			for (int i = 0; i < MONTH_ROW_COUNT; i++) {
				for (int j = 0; j < WEEK_COUNT; j++) {
					if (i == 0) {
						p.add(weekLabels[j]);
					} else {
						p.add(dayButtons[i - 1][j]);
					}
				}
			}
			monthPanel.add(m, BorderLayout.NORTH);
			monthPanel.add(p, BorderLayout.CENTER);

			return monthPanel;
		}

		/**
		 * �f�t�H���g�̃��P�[�����g�p���āA�j���̃��x���z����쐬���܂��B
		 */
		private JLabel[] createWeekLabels() {
			Calendar cal = (Calendar) currentCalendar.clone();
			cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			DateFormat dform = new SimpleDateFormat("E");

			JLabel[] weekLabels = new JLabel[WEEK_COUNT];
			for (int i = 0; i < WEEK_COUNT; i++) {
				weekLabels[i] = new JLabel(dform.format(cal.getTime()));
				weekLabels[i].setBorder(BorderFactory.createEtchedBorder());
				weekLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
					weekLabels[i].setForeground(ColorFactory.getColor("red"));
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
					weekLabels[i].setForeground(ColorFactory.getColor("blue"));
				cal.add(Calendar.DATE, 1);
			}
			return weekLabels;
		}

		/**
		 * �f�t�H���g�̃��P�[�����g�p���āA�����̃{�^���z����쐬���܂��B
		 * @param monthPanel 
		 */
		private JButton[][] createDayButtons(ActionListener l, MonthPanel monthPanel) {
			int row = MONTH_ROW_COUNT - 1;
			DayButton[][] dayButtons = new DayButton[row][WEEK_COUNT];
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < WEEK_COUNT; j++) {
					dayButtons[i][j] = new DayButton(monthPanel);
					dayButtons[i][j].setMargin(new Insets(0, 0, 0, 0));
					dayButtons[i][j].setEnabled(false);
				}
			}
			Calendar cal = (Calendar) currentCalendar.clone();
			cal.set(Calendar.DATE, 1);
			int currentMonth = cal.get(Calendar.MONTH);
			DateFormat dform = new SimpleDateFormat("d");
			Map weekIndex = createWeekIndex();
			while (currentMonth == cal.get(Calendar.MONTH)) {
				int index =
					((Integer) weekIndex.get(new Integer(cal.get(Calendar.DAY_OF_WEEK))))
						.intValue();
				dayButtons[cal.get(Calendar.WEEK_OF_MONTH)
					- 1][index].setText(dform.format(cal.getTime()));
				dayButtons[cal.get(Calendar.WEEK_OF_MONTH) - 1][index].setEnabled(true);
				dayButtons[cal.get(Calendar.WEEK_OF_MONTH) - 1][index].addActionListener(l);
				dayButtons[cal.get(Calendar.WEEK_OF_MONTH)
					- 1][index].setDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
				setColor(dayButtons[cal.get(Calendar.WEEK_OF_MONTH) - 1][index], cal);
				cal.add(Calendar.DATE, 1);
			}
			return dayButtons;
		}

		/**
		 * �j���ƃC���f�b�N�X�̑Ή��e�[�u�����쐬���܂��B
		 */
		private Map createWeekIndex() {
			Map index = new HashMap(7);
			Calendar cal = (Calendar) currentCalendar.clone();
			cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			for (int i = 0; i < 7; i++) {
				index.put(new Integer(cal.get(Calendar.DAY_OF_WEEK)), new Integer(i));
				cal.add(Calendar.DATE, 1);
			}
			return index;
		}

		private void setColor(JButton button, Calendar cal) {
			Color color =
				buttonManager.searchColor(cal.get(Calendar.MONTH), cal.get(Calendar.DATE) - 1);
			if (color != null) {
				button.setBackground(color);
				button.setOpaque(true);
			} else {
				button.setOpaque(false);
			}
		}

		/**
		 * �ꌎ����\������ JPanel ��ێ�����N���X�ł��B
		 */
		static class MonthPanel
			extends JPanel
			implements ActionListener, PropertyChangeListener, Editable, ReferencerOwnerSymbol {
		    
			private static final long ACTION_WAIT_TIME = 250L;
			private static final long serialVersionUID = 4557141194939440464L;
			/** ���̃p�l���������ł� */
			private int month;
			/** ��ԃ{�^���}�l�[�W���[�̎Q�Ƃł��B */
			private DayTypeButtonManager buttonManager;
			/** �ҏW�\�t���O */
			private boolean isEditable;
			/** �A�N�V�������s�t���O */
			private boolean isAction = true;
			/** �A�N�V�������s�^�C�}�[ */
			private final Timer timer;

			MonthPanel(
				LayoutManager layoutManager,
				Calendar calendar,
				DayTypeButtonManager buttonManager,
				Timer timer) {
				super(layoutManager);
				this.month = calendar.get(Calendar.MONTH);
				this.buttonManager = buttonManager;
				if (this.buttonManager.authentication != null) {
					this.buttonManager.authentication.addEditable(this);
				}
				this.timer = timer;
			}

			/**
			 * ���t���N���b�N���ꂽ���̏����ł��B
			 */
			public void actionPerformed(ActionEvent evt) {
				if (isEditable() && isAction) {
					JButton b = (JButton) evt.getSource();
					int day = Integer.parseInt(b.getText()) - 1;
					if (!buttonManager.setBit(month, day)) {
						return;
					}
					if (buttonManager.testBit(month, day)) {
						b.setBackground(buttonManager.getColor());
						b.setOpaque(true);
					} else {
						b.setOpaque(false);
					}

					// �ύX����ɏ�����
					DataHolder dh = buttonManager.dataReferencer.getDataHolder();
					dh.setValue(buttonManager.dataCalendar, new Date(), WifeQualityFlag.GOOD);
					try {
						dh.syncWrite();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					buttonManager.undoDataCalendar =
						(WifeDataCalendar) buttonManager.dataCalendar.valueOf(
							buttonManager.dataCalendar.toByteArray());
					isAction = false;
					timer.schedule(new ActionTimerTask(this), ACTION_WAIT_TIME);
				}
			}

			/**
			 * ����������ꂽ�Ƃ��ɌĂ΂�܂��B
			 */
			public void propertyChange(PropertyChangeEvent evt) {
				//				logger.info(month + "���@������� " + evt.getSource());
				cancelEvent(evt);
			}

			private void cancelEvent(PropertyChangeEvent evt) {
				WifeDataCalendar dataCalendar = (WifeDataCalendar) evt.getNewValue();

				Component[] comps = getComponents();
				for (int j = 0; j < comps.length; j++) {
					Container cont = (Container) comps[j];
					Component[] comps2 = cont.getComponents();
					for (int i = 0; i < comps2.length; i++) {
						//						logger.info("" + comps2[i]);
						if (comps2[i] instanceof JButton) {
							JButton button = (JButton) comps2[i];
							if (button.isEnabled()) {
								int day = Integer.parseInt(button.getText()) - 1;
								button.setOpaque(false);
								for (Iterator it = buttonManager.values().iterator();
									it.hasNext();
									) {
									DayTypeButton dayTypebutton = (DayTypeButton) it.next();
									if (dataCalendar
										.testBit(dayTypebutton.getType(), month, day)) {
										button.setBackground(dayTypebutton.getColor());
										button.setOpaque(true);
										break;
									}
								}
								button.repaint();
							}
						}
					}
				}
			}

			/**
			 * �ҏW����ׂ̃_�C�A���O��Ԃ��܂��B
			 * @param window �e�E�B���h�E
			 * @param collection �x�[�X�N���X�̃C���X�^���X
			 * @param �C�ӂ̃p�����[�^���X�g
			 * @todo �C�ӂ̃p�����[�^�͂��������A�^����������ׂ������B
			 */
			public WifeDialog getDialog(
				Window window,
				SymbolCollection collection,
				java.util.List para) {
				throw new UnsupportedOperationException();
			}
			/**
			 * �ݒ�_�C�A���O�̍���� Point �I�u�W�F�N�g��Ԃ��܂��B
			 */
			public Point getPoint() {
				throw new UnsupportedOperationException();
			}
			/**
			 * �ݒ�_�C�A���O�̍���� Point �I�u�W�F�N�g��ݒ肵�܂��B
			 */
			public void setPoint(Point point) {
				throw new UnsupportedOperationException();
			}

			/**
			 * ���̃V���{���̕ҏW�\�t���O��ݒ肵�܂��B
			 * @param editable �ҏW�\�ɂ���Ȃ� true �������łȂ���� false ��ݒ肵�܂��B
			 */
			public void setEditable(boolean[] editable) {
				this.isEditable = true;
				for (int i = 0; i < editable.length; i++) {
					if (!editable[i]) {
						this.isEditable = false;
					}
				}
			}

			/**
			 * ���̃V���{�����ҏW�\���ǂ�����Ԃ��܂��B
			 * @return �ҏW�\�ȏꍇ�� true �������łȂ���� false ��Ԃ��܂��B
			 */
			public boolean isEditable() {
				return this.isEditable;
			}

			/*
			 * @see org.F11.scada.applet.symbol.Editable#getDestinations()
			 */
			public String[] getDestinations() {
				if (isConnect()) {
					DataHolder dh = buttonManager.dataReferencer.getDataHolder();
					StringBuffer buffer = new StringBuffer();
					DataProvider dp = dh.getDataProvider();
					if (dp == null) {
					    logger.debug("null dp : " + dh.getDataHolderName());
					    return new String[0];
					}
					buffer.append(dh.getDataProvider().getDataProviderName());
					buffer.append("_");
					buffer.append(dh.getDataHolderName());
					return new String[] { buffer.toString()};
				} else {
					return new String[0];
				}
			}

			private boolean isConnect() {
				if (buttonManager.authentication == null) {
					return false;
				}
				if (buttonManager.dataReferencer == null) {
					return false;
				}
				if (buttonManager.dataReferencer.getDataHolder() == null) {
					return false;
				}
				return true;
			}

			/**
			 * �������ݐ�̒ǉ��͂��Ȃ��B
			 * @see org.F11.scada.applet.symbol.Editable#addDestination(Map)
			 */
			public void addDestination(Map atts) {
			}

			/**
			 * �������ݐ�̒ǉ��͂��Ȃ��B
			 * @see org.F11.scada.applet.symbol.Editable#addElement(Map)
			 */
			public void addValueSetter(ValueSetter setter) {
			}

			/* (non-Javadoc)
			 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
			 */
			public boolean isTabkeyMove() {
				return true;
			}

			public void disConnect() {
			    buttonManager.disConnect();
			    if (null != buttonManager.authentication) {
			    	buttonManager.authentication.removeEditable(this);
			    }
			}

			public void setAction(boolean isAction) {
				this.isAction = isAction;
			}


			private static class ActionTimerTask extends TimerTask {
				private final MonthPanel monthPanel;
				ActionTimerTask(MonthPanel monthPanel) {
					this.monthPanel = monthPanel;
				}
				public void run() {
					monthPanel.setAction(true);
				}
			}
		}

		/**
		 * ���t�{�^���N���X�ł��B
		 */
		static class DayButton extends JButton {
		    private static final long serialVersionUID = -4266480488017783674L;
			private int dayOfWeek;

			DayButton(MonthPanel monthPanel) {
				super();
				addMouseListener(new HandCursorListener(monthPanel));
			}

			void setDayOfWeek(int dayOfWeek) {
				this.dayOfWeek = dayOfWeek;
			}

			public void setOpaque(boolean isOpaque) {
				super.setOpaque(isOpaque);
				if (isOpaque) {
					this.setForeground(ColorFactory.getColor("black"));
				} else {
					if (dayOfWeek == Calendar.SUNDAY)
						this.setForeground(ColorFactory.getColor("red"));
					if (dayOfWeek == Calendar.SATURDAY)
						this.setForeground(ColorFactory.getColor("blue"));
				}
			}
		}
	}
}
