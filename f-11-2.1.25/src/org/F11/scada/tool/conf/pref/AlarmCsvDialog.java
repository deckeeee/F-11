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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.F11.scada.tool.conf.StreamManager;
import org.apache.log4j.Logger;

public class AlarmCsvDialog extends JDialog {
	static final Logger log = Logger.getLogger(AlarmCsvDialog.class);

	private StreamManager manager;

	private String csvout;
	private final JTextField hour = new JTextField();
	private final JTextField minute = new JTextField();
	private final JTextField path = new JTextField();
	private final JTextField file = new JTextField();

	public AlarmCsvDialog(StreamManager manager, Frame parent) {
		super(parent, "警報一覧CSV出力設定", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);
		setCsvout(manager, gridbag, c, panel);
		setHour(manager, gridbag, c, panel);
		setMinute(manager, gridbag, c, panel);
		setPath(manager, gridbag, c, panel);
		setFile(manager, gridbag, c, panel);

		mainPanel.add(panel, BorderLayout.CENTER);

		panel = new JPanel(new GridLayout(1, 0));
		setOk(panel);
		setCancel(panel);
		mainPanel.add(panel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(parent);
	}

	private void setCsvout(
			StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("CSV出力の有無：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox(new String[] { "無", "有" });
		csvout = manager.getPreferences("/server/alarm/csvout", "false");
		if (Boolean.parseBoolean(csvout)) {
			cb.setSelectedItem("有");
		} else {
			cb.setSelectedItem("無");
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String s = (String) e.getItem();
					if ("有".equals(s)) {
						csvout = "true";
					} else {
						csvout = "false";
					}
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
	}

	private void setHour(
			StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("出力時：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		hour.setText(manager.getPreferences("/server/alarm/csvout/hour", "0"));
		panel.add(hour);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(hour, c);
	}

	private void setMinute(
			StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("出力分：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		minute.setText(manager.getPreferences(
			"/server/alarm/csvout/minute",
			"10"));
		panel.add(minute);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(minute, c);
	}

	private void setPath(
			StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("ファイル出力パス：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		path.setText(manager.getPreferences(
			"/server/alarm/csvout/path",
			"C:/careerbackup"));
		path.setToolTipText("CSVファイルを出力するパスを設定します。パスの区切りは / (スラッシュ)で記述して下さい");
		panel.add(path);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(path, c);
	}

	private void setFile(
			StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("ファイル名：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		file.setText(manager.getPreferences(
			"/server/alarm/csvout/file",
			"'career'yyyyMMdd'.csv'"));
		panel.add(file);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(file, c);
	}

	private void setOk(JPanel panel) {
		JButton but = new JButton("ＯＫ");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.setPreferences("/server/alarm/csvout", csvout);
				manager.setPreferences("/server/alarm/csvout/hour", hour
					.getText());
				manager.setPreferences("/server/alarm/csvout/minute", minute
					.getText());
				manager.setPreferences("/server/alarm/csvout/path", path
					.getText());
				manager.setPreferences("/server/alarm/csvout/file", file
					.getText());
				dispose();
			}
		});
		panel.add(but);
	}

	private void setCancel(JPanel panel) {
		JButton but = new JButton("キャンセル");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(but);
	}
}