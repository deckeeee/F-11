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
package org.F11.scada.tool.conf.client;

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

public class AlarmSearchDialog extends JDialog {
	private static final long serialVersionUID = 2216618586240318425L;
	private static final Logger log = Logger.getLogger(AlarmSearchDialog.class);

	private final StreamManager manager;

	private final JTextField alarmSearch = new JTextField();
	private final JTextField alarmAttribute = new JTextField();
	private final JTextField alarmAttriName = new JTextField();
	private String alarmOn;
	private String checkOn;

	public AlarmSearchDialog(StreamManager manager, Frame parent) {
		super(parent, "�x�񗚗����������\���ݒ�", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel gridbagPanel = new JPanel(gridbag);

		// �x�񗚗������̏����\���͈�(���P��) (�����l��1���O)
		JLabel label = new JLabel("�͈�(���P��)�F");
		gridbagPanel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		alarmSearch.setText(manager.getClientConf(
				"xwife.applet.Applet.alarm.table.search", "1"));
		gridbagPanel.add(alarmSearch);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(alarmSearch, c);
		// �����l�őI�����鍀�ڂ��c�_(|)��؂ŋL�q
		label = new JLabel("�I������(�c�_���)�F");
		gridbagPanel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		alarmAttribute.setText(manager.getClientConf(
				"xwife.applet.Applet.alarm.table.search.attribute", ""));
		gridbagPanel.add(alarmAttribute);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(alarmAttribute, c);
		// �����l�őI�����鍀�ږ����c�_(|)��؂ŋL�q
		label = new JLabel("�I�����ږ�(�c�_���)�F");
		gridbagPanel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		alarmAttriName.setText(manager.getClientConf(
				"xwife.applet.Applet.alarm.table.search.attributename", ""));
		gridbagPanel.add(alarmAttriName);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(alarmAttriName, c);
		// �����l�őI�����郉�W�I�{�^����ݒ�
		// �����F�S��-SELECTALL �����E�^�]�̂�-SELECTTRUE �����E��~�̂�-SELECTFALSE
		label = new JLabel("�x��E��ԁF");
		gridbagPanel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox(new String[]{"�S��", "�����E�^�]�̂�", "�����E��~�̂�"});
		alarmOn = manager.getClientConf(
				"xwife.applet.Applet.alarm.table.search.alarmon", "SELECTALL");
		if ("SELECTALL".equals(alarmOn))
			cb.setSelectedIndex(0);
		else if ("SELECTTRUE".equals(alarmOn))
			cb.setSelectedIndex(1);
		else if ("SELECTFALSE".equals(alarmOn))
			cb.setSelectedIndex(2);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�S��".equals(e.getItem()))
						alarmOn = "SELECTALL";
					else if ("�����E�^�]�̂�".equals(e.getItem()))
						alarmOn = "SELECTTRUE";
					else if ("�����E��~�̂�".equals(e.getItem()))
						alarmOn = "SELECTFALSE";
				}
			}
		});
		gridbagPanel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
		// �m�F(�q�X�g���̂�)�F�S��-SELECTALL �m�F�ς�-SELECTTRUE ���m�F-SELECTFALSE
		label = new JLabel("�m�F�F");
		gridbagPanel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		cb = new JComboBox(new String[]{"�S��", "�m�F�ς�", "���m�F"});
		checkOn = manager.getClientConf(
				"xwife.applet.Applet.alarm.table.search.checkon", "SELECTALL");
		if ("SELECTALL".equals(checkOn))
			cb.setSelectedIndex(0);
		else if ("SELECTTRUE".equals(checkOn))
			cb.setSelectedIndex(1);
		else if ("SELECTFALSE".equals(checkOn))
			cb.setSelectedIndex(2);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�S��".equals(e.getItem()))
						checkOn = "SELECTALL";
					else if ("�m�F�ς�".equals(e.getItem()))
						checkOn = "SELECTTRUE";
					else if ("���m�F".equals(e.getItem()))
						checkOn = "SELECTFALSE";
				}
			}
		});
		gridbagPanel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);

		mainPanel.add(gridbagPanel, BorderLayout.CENTER);

		gridbagPanel = new JPanel(new GridLayout(1, 0));
		JButton but = new JButton("�n�j");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_ok();
			}
		});
		gridbagPanel.add(but);
		but = new JButton("�L�����Z��");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_cansel();
			}
		});
		gridbagPanel.add(but);
		mainPanel.add(gridbagPanel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(parent);
	}

	private void push_ok() {
		log.debug("push_ok");
		manager.setClientConf("xwife.applet.Applet.alarm.table.search",
				alarmSearch.getText());
		manager.setClientConf(
				"xwife.applet.Applet.alarm.table.search.attribute",
				alarmAttribute.getText());
		manager.setClientConf(
				"xwife.applet.Applet.alarm.table.search.attributename",
				alarmAttriName.getText());
		manager.setClientConf("xwife.applet.Applet.alarm.table.search.alarmon",
				alarmOn);
		manager.setClientConf("xwife.applet.Applet.alarm.table.search.checkon",
				checkOn);
		dispose();
	}
	private void push_cansel() {
		dispose();
	}

}
