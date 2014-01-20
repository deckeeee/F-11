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

package org.F11.scada.xwife.explorer.timeset;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import jp.gr.javacons.jim.Manager;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.test.util.TimestampUtil;
import org.F11.scada.util.FontUtil;
import org.F11.scada.util.SystemTimeUtil;
import org.F11.scada.util.ThreadUtil;
import org.F11.scada.xwife.explorer.ExplorerElement;
import org.apache.commons.digester.Digester;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * 時刻設定パネルクラスです。
 *
 * @author maekawa
 *
 */
public class TimeSet extends JPanel implements ExplorerElement {
	private static final long serialVersionUID = -347087975394647855L;
	private int minuteMin;
	private int minuteMax;
	private boolean isTestMode;

	public TimeSet() {
		super(new BorderLayout());
		setMinuteLimits();
		setBackground(SystemColor.window);
		setOpaque(true);
		add(new TimePanel(), BorderLayout.CENTER);
		add(createTimeSetButton(), BorderLayout.SOUTH);
	}

	private void setMinuteLimits() {
		isTestMode = Boolean.valueOf(
				EnvironmentManager.get("/server/systemtime/testMode", "false"))
				.booleanValue();
		minuteMin = Integer.parseInt(EnvironmentManager.get(
				"/server/systemtime/miunteMin",
				"6"));
		minuteMax = Integer.parseInt(EnvironmentManager.get(
				"/server/systemtime/miunteMax",
				"54"));
	}

	private Component createTimeSetButton() {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
		box.add(Box.createHorizontalGlue());
		box.add(createSetButton(this));
		return box;
	}

	private JButton createSetButton(final TimeSet timeset) {
		JButton button = new JButton("時刻の設定");
		final Component parent = this;
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isOpenDialog()) {
					new TimeSetDialog(
							getFrame(timeset),
							minuteMin,
							minuteMax,
							isTestMode).setVisible(true);
				} else {
					JOptionPane.showMessageDialog(
							parent,
							getMessage(),
							"時刻設定警告",
							JOptionPane.WARNING_MESSAGE);
				}
			}

			private Frame getFrame(Component component) {
				return (Frame) SwingUtilities.getAncestorOfClass(
						Frame.class,
						component);
			}

			private boolean isOpenDialog() {
				Calendar cal = Calendar.getInstance();
				int minute = cal.get(Calendar.MINUTE);
				return isTestMode
						|| (minuteMin <= minute && minute <= minuteMax);
			}

			private String getMessage() {
				return "システムに重大な影響を与える為、この時間帯では時間変更ができません。\n" + "変更可能時間 : 各時間の"
						+ (minuteMin) + "分 ～ " + (minuteMax) + "分の間";
			}
		});
		return button;
	}

	public Component getComponent() {
		return this;
	}

	private static class TimePanel extends JPanel implements Runnable {
		private static final long serialVersionUID = -4562053833641098265L;
		private Thread thread;
		private JLabel dateLabel;
		private JLabel timeLabel;

		TimePanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
			setBackground(SystemColor.window);
			setOpaque(true);
			add(createMainPanel());
			start();
		}

		private JPanel createMainPanel() {
			JPanel main = new JPanel();
			main.setBorder(BorderFactory.createTitledBorder("日時"));
			main.add(createDateTimeBox(), BorderLayout.CENTER);
			main.setBackground(SystemColor.window);
			main.setOpaque(true);
			return main;
		}

		private Box createDateTimeBox() {
			Box box = Box.createVerticalBox();
			dateLabel = createDateLabel();
			box.add(dateLabel);
			timeLabel = createTimeLabel();
			box.add(timeLabel);
			return box;
		}

		private JLabel createDateLabel() {
			JLabel dateLabel = new JLabel(createDateText());
			dateLabel
					.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
			FontUtil.setFont("Monospaced", "PLAIN", "36", dateLabel);
			return dateLabel;
		}

		private JLabel createTimeLabel() {
			JLabel timeLabel = new JLabel(createTimeText());
			timeLabel
					.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			FontUtil.setFont("Monospaced", "PLAIN", "36", timeLabel);
			return timeLabel;
		}

		public void run() {
			Thread ct = Thread.currentThread();
			while (ct == thread) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						timeLabel.setText(createTimeText());
						dateLabel.setText(createDateText());
					}
				});
				ThreadUtil.sleep(250L);
			}
		}

		private String createTimeText() {
			return DateFormatUtils.format(new Date(), "  HH時mm分ss秒");
		}

		private String createDateText() {
			return DateFormatUtils.format(new Date(), "yyyy年MM月dd日");
		}

		public void start() {
			if (null == thread) {
				thread = new Thread(this);
				thread.start();
			}
		}

		public void stop() {
			if (null != thread) {
				Thread ct = thread;
				thread = null;
				ct.interrupt();
			}
		}
	}

	private static class TimeSetDialog extends JDialog implements
			ActionListener {
		private static final long serialVersionUID = -577413929861897943L;
		private String hour;
		private JSpinner hourSpinner;
		private JSpinner minute;
		private JSpinner second;
		private final int minuteMin;
		private final int minuteMax;
		private final Frame parent;
		private final Timer timer;
		private final boolean isTestMode;

		TimeSetDialog(
				Frame frame,
				int minuteMin,
				int minuteMax,
				boolean isTestMode) {
			super(frame, "時刻の設定", true);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			parent = frame;
			this.minuteMin = minuteMin;
			this.minuteMax = minuteMax;
			this.isTestMode = isTestMode;
			getContentPane().add(createTimeComponent(), BorderLayout.CENTER);
			getContentPane().add(createButtonBox(), BorderLayout.SOUTH);
			pack();
			WifeUtilities.setCenter(this);
			timer = new Timer(250, this);
		}

		private Component createButtonBox() {
			Box box = Box.createHorizontalBox();
			box.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
			box.add(Box.createHorizontalGlue());
			box.add(createOk());
			box.add(Box.createHorizontalStrut(5));
			box.add(createCancel());
			return box;
		}

		private JButton createOk() {
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new OkAction(this));
			return okButton;
		}

		private JButton createCancel() {
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			return cancelButton;
		}

		private Component createTimeComponent() {
			JPanel main = new JPanel(new BorderLayout());
			main.setBorder(BorderFactory.createEmptyBorder(20, 15, 10, 15));

			JPanel timePanel = new JPanel(new BorderLayout());
			timePanel.setBorder(BorderFactory.createTitledBorder("時刻設定"));

			timePanel.add(createTimeBox());
			main.add(timePanel);
			return main;
		}

		private Box createTimeBox() {
			Box box = Box.createHorizontalBox();
			box.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
			Date now = new Date();
			if (isTestMode) {
				createTestMode(box, now);
			} else {
				createNoTestMode(box, now);
			}
			box.add(minute);
			JLabel minuteMark = new JLabel("分");
			FontUtil.setFont("Monospaced", "PLAIN", "24", minuteMark);
			box.add(minuteMark);
			box.add(second);
			JLabel secondMark = new JLabel("秒");
			FontUtil.setFont("Monospaced", "PLAIN", "24", secondMark);
			box.add(secondMark);
			spinnerMask();
			return box;
		}

		private void createTestMode(Box box, Date now) {
			hourSpinner = createSpinner(now, "HH", 0, 23);
			JLabel hourLabel = new JLabel("時");
			FontUtil.setFont("Monospaced", "PLAIN", "24", hourLabel);
			minute = createSpinner(now, "mm", 0, 59);
			second = createSpinner(now, "ss", 0, 59);
			box.add(hourSpinner);
			box.add(hourLabel);
		}

		private void createNoTestMode(Box box, Date now) {
			hour = DateFormatUtils.format(now, "HH");
			JLabel hourLabel = new JLabel(getHour() + "時");
			FontUtil.setFont("Monospaced", "PLAIN", "24", hourLabel);
			minute = createSpinner(now, "mm", minuteMin, minuteMax);
			second = createSpinner(now, "ss", 0, 59);
			box.add(hourLabel);
		}

		private JSpinner createSpinner(Date now, String format, int min, int max) {
			SpinnerNumberModel model = new SpinnerNumberModel(Integer
					.parseInt(DateFormatUtils.format(now, format)), min, max, 1);
			JSpinner spinner = new JSpinner(model);
			spinner.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
			JSpinner.NumberEditor editor = new JSpinner.NumberEditor(
					spinner,
					"00");
			spinner.setEditor(editor);
			FontUtil.setFont("Monospaced", "PLAIN", "24", spinner);
			return spinner;
		}

		private void spinnerMask() {
			int value = Integer.parseInt(minute.getValue().toString());
			if (minuteMin > value && value > minuteMax) {
				minute.setEnabled(false);
			}
		}

		Date getSetTime() {
			String dateStr = DateFormatUtils.format(new Date(), "yyyy/MM/dd");
			String timeStr = getHour() + ":" + minute.getValue().toString()
					+ ":" + second.getValue().toString();
			return TimestampUtil.parse(dateStr + " " + timeStr);
		}

		private String getHour() {
			return isTestMode ? hourSpinner.getValue().toString() : hour;
		}

		public void setVisible(boolean b) {
			timer.start();
			super.setVisible(b);
		}

		public void dispose() {
			timer.stop();
			timer.removeActionListener(this);
			super.dispose();
		}

		private boolean isOpenDialog() {
			Calendar cal = Calendar.getInstance();
			int minute = cal.get(Calendar.MINUTE);
			return isTestMode || (minuteMin <= minute && minute <= minuteMax);
		}

		public void actionPerformed(ActionEvent e) {
			if (!isOpenDialog()) {
				JOptionPane.showMessageDialog(
						parent,
						getMessage(),
						"時刻設定警告",
						JOptionPane.WARNING_MESSAGE);
				dispose();
			}
		}

		private String getMessage() {
			return "システムに重大な影響を与える為、この時間帯では時間変更ができません。\n"
					+ "時間変更ダイアログをCalcelします。";
		}
	}

	private static class OkAction implements ActionListener {
		private final Logger log = Logger.getLogger(OkAction.class);

		private final TimeSetDialog dialog;
		private List holderStrings;

		OkAction(TimeSetDialog dialog) {
			this(dialog, "/resources/TimeSet.xml");
		}

		OkAction(TimeSetDialog dialog, String file) {
			this.dialog = dialog;
			loadTimeSet(file);
		}

		private void loadTimeSet(String file) {
			URL xml = getClass().getResource(file);
			if (xml == null) {
				log.info("Not Found " + file);
			} else {
				parse(xml);
			}
		}

		private void parse(URL xml) {
			Digester digester = createDigester();
			InputStream is = null;
			try {
				is = xml.openStream();
				digester.parse(is);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private Digester createDigester() {
			Digester digester = new Digester();
			digester.setNamespaceAware(true);
			digester.push(this);
			digester.addObjectCreate(
					"timeset/timesettask/read",
					HolderString.class);
			digester.addSetNext("timeset/timesettask/read", "addHolderString");
			digester.addSetProperties("timeset/timesettask/read");
			digester.addObjectCreate(
					"timeset/timesettask/write",
					HolderString.class);
			digester.addSetNext("timeset/timesettask/write", "addHolderString");
			digester.addSetProperties("timeset/timesettask/write");
			return digester;
		}

		public void addHolderString(HolderString holderString) {
			if (null == holderStrings) {
				holderStrings = new ArrayList();
			}
			holderStrings.add(holderString);
		}

		public void actionPerformed(ActionEvent e) {
			Date date = dialog.getSetTime();
			log.info("set datetime=" + date);
			SystemTimeUtil util = new SystemTimeUtil();
			if (null != holderStrings) {
				for (Iterator i = holderStrings.iterator(); i.hasNext();) {
					HolderString holderString = (HolderString) i.next();
					log.info("set holder=" + holderString);
					util.setPlcTime(Manager.getInstance().findDataHolder(
							holderString.getProvider(),
							holderString.getHolder()), date);
				}
			}
			util.setSystemTime(date);
			dialog.dispose();
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("時刻設定テスト");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new TimeSet(), BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}
}
