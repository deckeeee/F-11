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
	private final JComboBox exeDay = new JComboBox(new String[]{"����", "���� 1 ��",
			"���� 2 ��", "���� 3 ��", "���� 4 ��", "���� 5 ��", "���� 6 ��", "���� 7 ��",
			"���� 8 ��", "���� 9 ��", "���� 10 ��", "���� 11 ��", "���� 12 ��", "���� 13 ��",
			"���� 14 ��", "���� 15 ��", "���� 16 ��", "���� 17 ��", "���� 18 ��", "���� 19 ��",
			"���� 20 ��", "���� 21 ��", "���� 22 ��", "���� 23 ��", "���� 24 ��", "���� 25 ��",
			"���� 26 ��", "���� 27 ��", "���� 28 ��", "���� 29 ��", "���� 30 ��", "���� 31 ��"});
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

		// �e�[�u�����
		JLabel label = new JLabel("�e�[�u����ʁF");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox(new String[]{"���M���O�f�[�^", "�q�X�g��", "����",
				"���샍�O", "�x�񃁁[�����M����"});
		String name = bean.getTableName();
		tableName.setEditable(false);
		if ("hisotry_table".equals(name)) {
			cb.setSelectedItem("�q�X�g��");
		} else if ("career_table".equals(name)) {
			cb.setSelectedItem("����");
		} else if ("operation_logging_table".equals(name)) {
			cb.setSelectedItem("���샍�O");
		} else if ("alarm_email_sent_table".equals(name)) {
			cb.setSelectedItem("�x�񃁁[�����M����");
		} else {
			cb.setSelectedItem("���M���O�f�[�^");
			tableName.setEditable(true);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�q�X�g��".equals(e.getItem())) {
						tableName.setText("hisotry_table");
						tableName.setEditable(false);
						fieldName.setText("off_date");
					} else if ("����".equals(e.getItem())) {
						tableName.setText("career_table");
						tableName.setEditable(false);
						fieldName.setText("entrydate");
					} else if ("���샍�O".equals(e.getItem())) {
						tableName.setText("operation_logging_table");
						tableName.setEditable(false);
						fieldName.setText("ope_date");
					} else if ("�x�񃁁[�����M����".equals(e.getItem())) {
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
		// �e�[�u����
		label = new JLabel("�e�[�u�����F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		tableName.setText(bean.getTableName());
		panel.add(tableName);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(tableName, c);
		// ���t�t�B�[���h��
		label = new JLabel("���t���ږ��F");
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
		// �c��
		label = new JLabel("�c���F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		removeValue.setText(bean.getRemoveValue());
		panel.add(removeValue);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(removeValue, c);
		// ���s��
		label = new JLabel("���s���F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		if (bean.isDaily()) {
			exeDay.setSelectedItem("����");
		} else {
			exeDay.setSelectedIndex(Integer.parseInt(bean.getExecuteDay()));
		}
		panel.add(exeDay);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(exeDay, c);
		// ���s����
		label = new JLabel("���s�����F");
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
		JButton but = new JButton("�n�j");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_ok();
			}
		});
		panel.add(but);
		but = new JButton("�L�����Z��");
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
