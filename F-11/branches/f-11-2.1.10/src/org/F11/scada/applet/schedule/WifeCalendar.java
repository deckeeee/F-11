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
 * 休日、特殊日設定カレンダークラスです。
 * @todo 表示する月の設定を分離する。とりあえず一年カレンダー固定。
 */
public class WifeCalendar {
	/**	メイン画面コンポーネント */
	private ScrollableBasePanel mainPanel;
	/** ボタン種別マネージャー */
	private DayTypeButtonManager buttonManager;

	private static Logger logger;

	/**
	 * コンストラクタ
	 * @param alarmRef リモートデータオブジェクト
	 */
	public WifeCalendar(String dataProvider, String dataHolder) {
		this(dataProvider, dataHolder, null);
	}

	/**
	 * コンストラクタ
	 * @param alarmRef リモートデータオブジェクト
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
	 * メイン画面のコンポーネントを返します。
	 */
	public JComponent getMainPanel() {
		JScrollPane scpane = new JScrollPane(mainPanel);
		return scpane;
	}

	/**
	 * ツールバーを返します。
	 */
	public JComponent getToolBar() {
		return buttonManager.getToolBar();
	}

	/**
	 * 状態ボタンを管理するクラスです。
	 */
	static class DayTypeButtonManager
		implements ActionListener, DataReferencerOwner, DataValueChangeListener {
		/** DataHolderタイプ情報です。 */
		private static final Class[][] WIFE_TYPE_INFO =
			new Class[][] { { DataHolder.class, WifeData.class }
		};
		/** 現在の状態 */
		private DayTypeButton state;
		/** 状態ボタンのツールバー */
		private JToolBar toolBar;
		/** カレンダーデータの参照 */
		private WifeDataCalendar dataCalendar;
		/** カレンダーデータの参照 */
		private WifeDataCalendar undoDataCalendar;
		/** プロパティチェンジサポート */
		private SwingPropertyChangeSupport changeSupport;
		/** 取消処理イベント名です */
		private static final String CANCEL_EVENT_NAME = "calendar_cancel";
		/** 状態表示エリア */
		private JLabel stateLabel;
		/** メッセージフォーッマッター */
		private MessageFormat message;
		/** データリファレンサ */
		private DataReferencer dataReferencer;
		/** 認証コントロールの参照 */
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
//TODO 最大特殊日が5日までは、制限無しにすること
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
			message = new MessageFormat("{0}設定中です。");
		}

		private void setStatusLabel() {
			Object[] m = { state.getMessage()};
			stateLabel.setText(message.format(m));
			stateLabel.setBackground(state.getColor());
			stateLabel.setOpaque(true);
		}

		/**
		 * データ変更イベント処理
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
		 * DataHolderタイプ情報を返します。
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
	 * 設定するタイプを表すボタンクラスです。
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
		/** カレンダーに設定する日付タイプです */
		private DayType dayType;

		/**
		 * プライベートコンストラクタ
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
		 * 日付データタイプのインデックスを返します。
		 */
		int getType() {
			return dayType.getType();
		}

		/**
		 * 日付データタイプの色を返します。
		 */
		Color getColor() {
			return dayType.getColor();
		}

		String getMessage() {
			return dayType.message;
		}

		/**
		 * カレンダーで設定する、日付の種類を表す、タイプセーフ enum クラスです。
		 */
		static final class DayType {
			static final DayType HOLIDAY = new DayType(0, "休", "休日", "red", new Dimension(36, 36));
			static final DayType SPECIAL01 =
				new DayType(1, "特1", "特殊日1", "cyan", new Dimension(36, 36));
			static final DayType SPECIAL02 =
				new DayType(2, "特2", "特殊日2", "springgreen", new Dimension(36, 36));
			static final DayType SPECIAL03 =
				new DayType(3, "特3", "特殊日3", "dodgerblue", new Dimension(36, 36));
			static final DayType SPECIAL04 =
				new DayType(4, "特4", "特殊日4", "yellow", new Dimension(36, 36));
			static final DayType SPECIAL05 =
				new DayType(5, "特5", "特殊日5", "magenta", new Dimension(36, 36));

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
	 * 一月分を表示する JPanel を生成するクラスです。
	 */
	static class MonthPanelFactory {
		/** 現在のカレンダー参照 */
		private Calendar currentCalendar = Calendar.getInstance();
		/** １週間の日付の数 */
		private static final int WEEK_COUNT = 7;
		/** １月の最大表示可能行 */
		private static final int MONTH_ROW_COUNT = 7;
		/** 月表示のフォーマット文字列 */
		private String defaultMonthLabelFormat = "yyyy 年 M 月";
		/** 状態ボタンマネージャーの参照です。 */
		private DayTypeButtonManager buttonManager;
		/** 生成パネル数 */
		private int panelCount;
		/** ボタンアクション実行タイマー */
		private final Timer timer = new Timer(true);

		/**
		 * コンストラクタ
		 */
		public MonthPanelFactory(DayTypeButtonManager buttonManager) {
			this.buttonManager = buttonManager;
		}

		/**
		 * 現在のカレンダーパネルを返し、日付を次月にします。
		 * @return 現在の月のカレンダーパネルオブジェクト
		 */
		public MonthPanel next() {
			MonthPanel p = getMonthPanel();
			buttonManager.addPropertyChangeListener(DayTypeButtonManager.CANCEL_EVENT_NAME, p);
			currentCalendar.add(Calendar.MONTH, 1);
			return p;
		}

		/**
		 * 現在の月のカレンダーパネルを返します。
		 * @return 現在の月のカレンダーパネルオブジェクト
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
		 * デフォルトのロケールを使用して、曜日のラベル配列を作成します。
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
		 * デフォルトのロケールを使用して、当月のボタン配列を作成します。
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
		 * 曜日とインデックスの対応テーブルを作成します。
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
		 * 一月分を表示する JPanel を保持するクラスです。
		 */
		static class MonthPanel
			extends JPanel
			implements ActionListener, PropertyChangeListener, Editable, ReferencerOwnerSymbol {
		    
			private static final long ACTION_WAIT_TIME = 250L;
			private static final long serialVersionUID = 4557141194939440464L;
			/** このパネルが持つ月です */
			private int month;
			/** 状態ボタンマネージャーの参照です。 */
			private DayTypeButtonManager buttonManager;
			/** 編集可能フラグ */
			private boolean isEditable;
			/** アクション実行フラグ */
			private boolean isAction = true;
			/** アクション実行タイマー */
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
			 * 日付がクリックされた時の処理です。
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

					// 変更直後に書込み
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
			 * 取消処理されたときに呼ばれます。
			 */
			public void propertyChange(PropertyChangeEvent evt) {
				//				logger.info(month + "月　取消処理 " + evt.getSource());
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
			 * 編集する為のダイアログを返します。
			 * @param window 親ウィンドウ
			 * @param collection ベースクラスのインスタンス
			 * @param 任意のパラメータリスト
			 * @todo 任意のパラメータはもう少し、型を強制するべきかも。
			 */
			public WifeDialog getDialog(
				Window window,
				SymbolCollection collection,
				java.util.List para) {
				throw new UnsupportedOperationException();
			}
			/**
			 * 設定ダイアログの左上の Point オブジェクトを返します。
			 */
			public Point getPoint() {
				throw new UnsupportedOperationException();
			}
			/**
			 * 設定ダイアログの左上の Point オブジェクトを設定します。
			 */
			public void setPoint(Point point) {
				throw new UnsupportedOperationException();
			}

			/**
			 * このシンボルの編集可能フラグを設定します。
			 * @param editable 編集可能にするなら true をそうでなければ false を設定します。
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
			 * このシンボルが編集可能かどうかを返します。
			 * @return 編集可能な場合は true をそうでなければ false を返します。
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
			 * 書き込み先の追加はしない。
			 * @see org.F11.scada.applet.symbol.Editable#addDestination(Map)
			 */
			public void addDestination(Map atts) {
			}

			/**
			 * 書き込み先の追加はしない。
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
		 * 日付ボタンクラスです。
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
