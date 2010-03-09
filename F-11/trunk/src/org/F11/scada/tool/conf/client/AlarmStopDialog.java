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

public class AlarmStopDialog extends JDialog {
	private static final long serialVersionUID = -8455312383961181411L;
	private static final Logger log = Logger.getLogger(AlarmStopDialog.class);

	private final StreamManager manager;

	private final JTextField keyName = new JTextField();
	private final JTextField stopEvent = new JTextField(20);
	private final JTextField stopWrite = new JTextField(20);

	public AlarmStopDialog(StreamManager manager, Frame parent) {
		super(parent, "�x�񉹒�~�ݒ�", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);

		// �x�񉹒�~�L�[�ݒ�B�����l�� F12�L�[
		JLabel label = new JLabel("��~�L�[�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		keyName.setText(manager.getClientConf(
				"xwife.applet.Applet.alarmStopKey", "F12"));
		panel.add(keyName);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(keyName, c);
		// �x�񉹒�~�f�W�^���C�x���g�ݒ�B�����l�͐ݒ薳��
		label = new JLabel("�f�W�^���C�x���g�ݒ�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		stopEvent.setText(manager.getClientConf(
				"xwife.applet.Applet.alarmStopKey.event", ""));
		panel.add(stopEvent);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(stopEvent, c);
		// �x�񉹒�~�f�W�^���C�x���g�������ݐݒ�B�����l�͐ݒ薳��
		label = new JLabel("�f�W�^���C�x���g�������ݐݒ�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		stopWrite.setText(manager.getClientConf(
				"xwife.applet.Applet.alarmStopKey.write", ""));
		panel.add(stopWrite);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(stopWrite, c);

		stopSoundOnly(panel, c);

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

	private void stopSoundOnly(JPanel mainPanel, GridBagConstraints c) {
		JLabel label = new JLabel("�x�񉹒�~�^�C�v�F");
		label.setToolTipText("���݂̂��~���邩�A�[���I�ɒ�~�{�^�����N���b�N���邩��ݒ�B���̂ݒ�~������");
		c.weightx = 1.0;
		c.gridwidth = 1;
		mainPanel.add(label, c);
		JComboBox cb = new JComboBox(new String[] { "��~�{�^���N���b�N", "���̂ݒ�~" });
		final String key = "xwife.applet.Applet.alarmStopKey.stopSoundOnly";
		if ("false".equals(manager.getClientConf(key, "false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("��~�{�^���N���b�N".equals(e.getItem())) {
						manager.setClientConf(key, "false");
					} else {
						manager.setClientConf(key, "true");
					}
				}
			}
		});
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		mainPanel.add(cb, c);
	}

	private void push_ok() {
		log.debug("push_ok");
		manager.setClientConf("xwife.applet.Applet.alarmStopKey", keyName
				.getText());
		manager.setClientConf("xwife.applet.Applet.alarmStopKey.event",
				stopEvent.getText());
		manager.setClientConf("xwife.applet.Applet.alarmStopKey.write",
				stopWrite.getText());
		dispose();
	}

	private void push_cansel() {
		dispose();
	}

}
