/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.tool.conf.pref;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;

import org.F11.scada.tool.conf.StreamManager;
import org.F11.scada.tool.conf.alarm.FontDialog;
import org.apache.log4j.Logger;

public class PrintDialog extends JDialog {
	static final long serialVersionUID = -4444931079909390433L;
	static final Logger log = Logger.getLogger(PrintDialog.class);

	private StreamManager manager;

	private String size;
	private String orientation;
	private final JTextField fontName = new JTextField();
	private final JTextField pagelines = new JTextField();
	private String printMode;
	private final JFormattedTextField printModeDate =
		new JFormattedTextField(new SimpleDateFormat("HH:mm:ss"));
	private final JTextField enable = new JTextField();

	public PrintDialog(StreamManager manager, Frame parent) {
		super(parent, "警報一覧印字設定", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);
		setSize(manager, gridbag, c, panel);
		setOrientation(manager, gridbag, c, panel);
		setFont(manager, gridbag, c, panel);
		setPageLine(manager, gridbag, c, panel);
		setPrintMode(manager, gridbag, c, panel);
		setPrintDate(manager, gridbag, c, panel);
		setEnable(manager, gridbag, c, panel);

		mainPanel.add(panel, BorderLayout.CENTER);

		panel = new JPanel(new GridLayout(1, 0));
		setOk(panel);
		setCancel(panel);
		mainPanel.add(panel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(parent);
	}

	private void setSize(
			StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		// 用紙サイズ
		JLabel label = new JLabel("用紙サイズ：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox(new String[] { "ISO_A4", "ISO_B5" });
		size = manager.getPreferences("/server/alarm/print/size", "ISO_A4");
		cb.setSelectedItem(size);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					size = (String) e.getItem();
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
	}

	private void setOrientation(
			StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		// 用紙方向
		JLabel label = new JLabel("用紙方向：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox();
		cb.addItem("縦");
		cb.addItem("横");
		orientation =
			manager.getPreferences(
				"/server/alarm/print/orientation",
				"PORTRAIT");
		if ("PORTRAIT".equals(orientation))
			cb.setSelectedItem("縦");
		else if ("LANDSCAPE".equals(orientation))
			cb.setSelectedItem("横");
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("縦".equals(e.getItem()))
						orientation = "PORTRAIT";
					else if ("横".equals(e.getItem()))
						orientation = "LANDSCAPE";
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
	}

	private void setFont(
			StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		// フォント
		JLabel label = new JLabel("フォント：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		fontName.setText(manager.getPreferences(
			"/server/alarm/print/font",
			"Monospaced,PLAIN,10"));
		fontName.setEditable(false);
		panel.add(fontName);
		c.weightx = 0.5;
		c.gridwidth = 1;
		gridbag.setConstraints(fontName, c);
		JButton but = new JButton("Font");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringTokenizer st =
					new StringTokenizer(fontName.getText(), ",");
				FontDialog dlg = new FontDialog(PrintDialog.this);
				if (dlg.showFontDialog(st.nextToken(), st.nextToken(), st
					.nextToken())) {
					fontName.setText(dlg.getFontName()
						+ ","
						+ dlg.getFontStyle()
						+ ","
						+ dlg.getFontSize());
				}
			}
		});
		panel.add(but);
		c.weightx = 0.5;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(but, c);
	}

	private void setPageLine(
			StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		// １頁行数
		JLabel label = new JLabel("１頁行数：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		pagelines.setText(manager.getPreferences(
			"/server/alarm/print/pagelines",
			"10"));
		panel.add(pagelines);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(pagelines, c);
	}

	private void setPrintMode(
			StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		// 用紙サイズ
		JLabel label = new JLabel("印刷モード：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox(new String[] { "ページ毎", "日毎" });
		printMode =
			manager.getPreferences(
				"/server/alarm/print/className",
				"org.F11.scada.server.alarm.print.AlarmPrintService");
		cb.setSelectedItem(getPrintMode());
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					printMode = getPrintMode((String) e.getItem());
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
	}

	private String getPrintMode() {
		return "org.F11.scada.server.alarm.print.AlarmDailyPrintService"
			.equals(printMode) ? "日毎" : "ページ毎";
	}

	private String getPrintMode(String s) {
		return "日毎".equals(s)
			? "org.F11.scada.server.alarm.print.AlarmDailyPrintService"
			: "org.F11.scada.server.alarm.print.AlarmPrintService";
	}

	private void setPrintDate(
			StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("日毎印刷の時刻：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		printModeDate.setText(manager.getPreferences(
			"/server/alarm/print/printdate",
			"00:00:00"));
		printModeDate.setFocusLostBehavior(JFormattedTextField.COMMIT);
		printModeDate.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				if (input instanceof JFormattedTextField) {
					JFormattedTextField ftf = (JFormattedTextField) input;
					AbstractFormatter formatter = ftf.getFormatter();
					if (formatter != null) {
						String text = ftf.getText();
						try {
							formatter.stringToValue(text);
							return true;
						} catch (ParseException pe) {
							JOptionPane.showMessageDialog(
								printModeDate,
								"HH:mm:ssで入力してください");
							return false;
						}
					}
				}
				return true;
			}

			@Override
			public boolean shouldYieldFocus(JComponent input) {
				return verify(input);
			}
		});
		panel.add(printModeDate);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(printModeDate, c);
	}

	private void setEnable(
			StreamManager manager2,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("印刷 オン/オフ ホルダ：");
		label.setToolTipText("警報印刷のオン/オフを制御するデジタルホルダIDを設定します。この設定が無い場合は常に印刷します。");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		enable
			.setText(manager.getPreferences("/server/alarm/print/enable", ""));
		panel.add(enable);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(enable, c);
	}

	private void setOk(JPanel panel) {
		JButton but = new JButton("ＯＫ");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_ok();
			}
		});
		panel.add(but);
	}

	private void setCancel(JPanel panel) {
		JButton but = new JButton("キャンセル");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_cansel();
			}
		});
		panel.add(but);
	}

	private void push_ok() {
		log.debug("push_ok");
		manager.setPreferences("/server/alarm/print/size", size);
		manager.setPreferences("/server/alarm/print/orientation", orientation);
		manager.setPreferences("/server/alarm/print/font", fontName.getText());
		manager.setPreferences("/server/alarm/print/pagelines", pagelines
			.getText());
		manager.setPreferences("/server/alarm/print/className", printMode);
		manager.setPreferences("/server/alarm/print/printdate", printModeDate
			.getText());
		manager.setPreferences("/server/alarm/print/enable", enable.getText());
		dispose();
	}

	private void push_cansel() {
		dispose();
	}
}