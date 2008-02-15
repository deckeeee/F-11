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
package org.F11.scada.tool.conf.remove;

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

import org.F11.scada.tool.conf.io.RemoveDefineBean;
import org.apache.log4j.Logger;

public class RemoveDefineDialog extends JDialog {
	private static final long serialVersionUID = -319564773378121610L;
	private static final Logger log = Logger
			.getLogger(RemoveDefineDialog.class);

	private final JTextField tableName = new JTextField();
	private final JTextField fieldName = new JTextField();
	private final JTextField removeValue = new JTextField();
	private final JComboBox exeDay = new JComboBox(new String[]{"毎日", "毎月 1 日",
			"毎月 2 日", "毎月 3 日", "毎月 4 日", "毎月 5 日", "毎月 6 日", "毎月 7 日",
			"毎月 8 日", "毎月 9 日", "毎月 10 日", "毎月 11 日", "毎月 12 日", "毎月 13 日",
			"毎月 14 日", "毎月 15 日", "毎月 16 日", "毎月 17 日", "毎月 18 日", "毎月 19 日",
			"毎月 20 日", "毎月 21 日", "毎月 22 日", "毎月 23 日", "毎月 24 日", "毎月 25 日",
			"毎月 26 日", "毎月 27 日", "毎月 28 日", "毎月 29 日", "毎月 30 日", "毎月 31 日"});
	private final JComboBox exeHour = new JComboBox(new String[]{"0", "1", "2",
			"3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
			"15", "16", "17", "18", "19", "20", "21", "22", "23"});
	private final JComboBox exeMinute = new JComboBox(new String[]{"0", "1",
			"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
			"14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24",
			"25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
			"36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46",
			"47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57",
			"58", "59"});
	private final JComboBox exeSecond = new JComboBox(new String[]{"0", "1",
			"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
			"14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24",
			"25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
			"36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46",
			"47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57",
			"58", "59"});
	private RemoveDefineBean retBean;

	private RemoveDefineDialog(Frame parent, String title, RemoveDefineBean bean) {
		super(parent, title, true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);

		// テーブル種別
		JLabel label = new JLabel("テーブル種別：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox(new String[]{"ロギングデータ", "ヒストリ", "履歴",
				"操作ログ", "警報メール送信履歴"});
		String name = bean.getTableName();
		tableName.setEditable(false);
		if ("hisotry_table".equals(name)) {
			cb.setSelectedItem("ヒストリ");
		} else if ("career_table".equals(name)) {
			cb.setSelectedItem("履歴");
		} else if ("operation_logging_table".equals(name)) {
			cb.setSelectedItem("操作ログ");
		} else if ("alarm_email_sent_table".equals(name)) {
			cb.setSelectedItem("警報メール送信履歴");
		} else {
			cb.setSelectedItem("ロギングデータ");
			tableName.setEditable(true);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("ヒストリ".equals(e.getItem())) {
						tableName.setText("hisotry_table");
						tableName.setEditable(false);
						fieldName.setText("off_date");
					} else if ("履歴".equals(e.getItem())) {
						tableName.setText("career_table");
						tableName.setEditable(false);
						fieldName.setText("entrydate");
					} else if ("操作ログ".equals(e.getItem())) {
						tableName.setText("operation_logging_table");
						tableName.setEditable(false);
						fieldName.setText("ope_date");
					} else if ("警報メール送信履歴".equals(e.getItem())) {
						tableName.setText("alarm_email_sent_table");
						tableName.setEditable(false);
						fieldName.setText("sentdate");
					} else {
						tableName.setText("log_");
						tableName.setEditable(true);
						fieldName.setText("f_date");
					}
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
		// テーブル名
		label = new JLabel("テーブル名：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		tableName.setText(bean.getTableName());
		panel.add(tableName);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(tableName, c);
		// 日付フィールド名
		label = new JLabel("日付項目名：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		fieldName.setText(bean.getDateFieldName());
		fieldName.setEditable(false);
		panel.add(fieldName);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(fieldName, c);
		// 残数
		label = new JLabel("残数：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		removeValue.setText(bean.getRemoveValue());
		panel.add(removeValue);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(removeValue, c);
		// 実行日
		label = new JLabel("実行日：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		if (bean.isDaily()) {
			exeDay.setSelectedItem("毎日");
		} else {
			exeDay.setSelectedIndex(Integer.parseInt(bean.getExecuteDay()));
		}
		panel.add(exeDay);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(exeDay, c);
		// 実行時刻
		label = new JLabel("実行時刻：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JPanel timePanel = new JPanel(new GridLayout(1, 0));
		exeHour.setSelectedItem(bean.getExecuteHour());
		timePanel.add(exeHour);
		exeMinute.setSelectedItem(bean.getExecuteMinute());
		timePanel.add(exeMinute);
		exeSecond.setSelectedItem(bean.getExecuteSecond());
		timePanel.add(exeSecond);
		panel.add(timePanel);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(timePanel, c);

		mainPanel.add(panel, BorderLayout.CENTER);

		panel = new JPanel(new GridLayout(1, 0));
		JButton but = new JButton("ＯＫ");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_ok();
			}
		});
		panel.add(but);
		but = new JButton("キャンセル");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_cansel();
			}
		});
		panel.add(but);
		mainPanel.add(panel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(parent);

	}

	private void push_ok() {
		log.debug("push_ok");
		retBean = new RemoveDefineBean();
		retBean.setTableName(tableName.getText());
		retBean.setDateFieldName(fieldName.getText());
		retBean.setRemoveValue(removeValue.getText());
		if (exeDay.getSelectedIndex() == 0)
			retBean.setDaily(true);
		else
			retBean.setDaily(false);
		retBean.setExecuteDay(String.valueOf(exeDay.getSelectedIndex()));
		retBean.setExecuteHour((String) exeHour.getSelectedItem());
		retBean.setExecuteMinute((String) exeMinute.getSelectedItem());
		retBean.setExecuteSecond((String) exeSecond.getSelectedItem());

		dispose();
	}
	private void push_cansel() {
		retBean = null;
		dispose();
	}

	public static RemoveDefineBean showRemoveDefineDialog(Frame parent,
			String title, RemoveDefineBean bean) {
		RemoveDefineDialog dlg = new RemoveDefineDialog(parent, title, bean);
		dlg.setVisible(true);
		return dlg.retBean;
	}
}
