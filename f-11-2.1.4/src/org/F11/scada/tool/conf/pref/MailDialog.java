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

public class MailDialog extends JDialog {
	static final long serialVersionUID = -4624012659717323094L;
	static final Logger log = Logger.getLogger(MailDialog.class);

	private final StreamManager manager;

	private final JTextField disableHolder = new JTextField();
	private final JTextField mailfrom = new JTextField();
	private final JTextField subject = new JTextField();
	private String field;
	private String datemode;
	private String sentmail;
	private final JTextField retryCount = new JTextField();
	private final JTextField waitTimeMilli = new JTextField();
	private final JTextField smtpPort = new JTextField();
	private final JTextField user = new JTextField();
	private final JTextField password = new JTextField();

	public MailDialog(StreamManager manager, Frame parent) {
		super(parent, "���[���ݒ�", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);
		// ���M�֎~�t���O
		JLabel label = new JLabel("���M�֎~�t���O�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		disableHolder.setText(manager.getPreferences(
				"/server/mail/smtp/disableHolder",
				""));
		panel.add(disableHolder);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(disableHolder, c);
		// FROM
		label = new JLabel("���M�ҁF");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		mailfrom.setText(manager
				.getPreferences("/server/mail/message/from", ""));
		panel.add(mailfrom);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(mailfrom, c);
		// Subject
		label = new JLabel("�����F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		subject.setText(manager.getPreferences(
				"/server/mail/message/subject",
				""));
		panel.add(subject);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(subject, c);
		// �A�h���X�i�[�t�B�[���h [TO, BCC, CC]
		label = new JLabel("��M�҃t�B�[���h�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox();
		cb.addItem("TO");
		cb.addItem("BCC");
		cb.addItem("CC");
		field = manager.getPreferences(
				"/server/mail/message/address/field",
				"TO");
		cb.setSelectedItem(field);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					field = (String) e.getItem();
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
		// ���M�������x�񔭐������ɂ��� (true�x�񔭐��� : false���M����)
		label = new JLabel("���M�����F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		cb = new JComboBox();
		cb.addItem("���M����");
		cb.addItem("�x�񔭐�����");
		datemode = manager.getPreferences(
				"/server/mail/message/datemode",
				"false");
		if ("false".equals(datemode))
			cb.setSelectedItem("���M����");
		else
			cb.setSelectedItem("�x�񔭐�����");
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("���M����".equals(e.getItem())) {
						datemode = "false";
					} else if ("�x�񔭐�����".equals(e.getItem())) {
						datemode = "true";
					}
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
		// ���M���[���L�^�̗L��
		label = new JLabel("���M�L�^�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		cb = new JComboBox();
		cb.addItem("����");
		cb.addItem("�L��");
		sentmail = manager.getPreferences("/server/alarm/sentmail", "false");
		if ("false".equals(sentmail))
			cb.setSelectedItem("����");
		else
			cb.setSelectedItem("�L��");
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem()))
						sentmail = "false";
					else if ("�L��".equals(e.getItem()))
						sentmail = "true";
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
		// ���M���[���G���[�����g���C��
		label = new JLabel("���g���C�񐔁F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		retryCount.setText(manager.getPreferences(
				"/server/mail/message/retry",
				"5"));
		panel.add(retryCount);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(retryCount, c);
		// ���M���[���G���[�����g���C�ҋ@����(�~���b)
		label = new JLabel("���g���C�ҋ@����(�~���b)�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		waitTimeMilli.setText(manager.getPreferences(
				"/server/mail/message/wait",
				"1000"));
		panel.add(waitTimeMilli);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(waitTimeMilli, c);

		mailSender(panel, c);
		smtpPort(panel, c);
		user(panel, c);
		password(panel, c);

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

	private void mailSender(JPanel panel, GridBagConstraints c) {
		c.weightx = 1.0;
		c.gridwidth = 1;
		panel.add(new JLabel("SMTP��ʁF"), c);
		JComboBox cb = new JComboBox();
		cb.addItem("�F�؂Ȃ�(�ʏ�)");
		cb.addItem("�F�ؗL��(SMTP-AUTH)");
		String prefix = manager.getPreferences(
				"/server/mail/smtp/sender",
				"alarmMail");
		if ("alarmMail".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�F�؂Ȃ�(�ʏ�)".equals(e.getItem()))
						manager.setPreferences(
								"/server/mail/smtp/sender",
								"alarmMail");
					else
						manager.setPreferences(
								"/server/mail/smtp/sender",
								"smtpAuthAlarmMail");
				}
			}
		});
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(cb, c);
	}

	private void smtpPort(JPanel panel, GridBagConstraints c) {
		c.weightx = 1.0;
		c.gridwidth = 1;
		panel.add(new JLabel("SMTP�|�[�g(�ʏ��25)�F"), c);
		smtpPort
				.setText(manager.getPreferences("/server/mail/smtp/port", "25"));
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(smtpPort, c);
	}

	private void user(JPanel panel, GridBagConstraints c) {
		c.weightx = 1.0;
		c.gridwidth = 1;
		panel.add(new JLabel("SMTP���[�U�[(�F�؂���̂ݕK�v)�F"), c);
		user.setText(manager.getPreferences("/server/mail/smtp/user", ""));
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(user, c);
	}

	private void password(JPanel panel, GridBagConstraints c) {
		c.weightx = 1.0;
		c.gridwidth = 1;
		panel.add(new JLabel("SMTP�p�X���[�h(�F�؂���̂ݕK�v)�F"), c);
		password.setText(manager.getPreferences(
				"/server/mail/smtp/password",
				""));
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(password, c);
	}

	private void push_ok() {
		log.debug("push_ok");
		manager.setPreferences("/server/mail/smtp/disableHolder", disableHolder
				.getText());
		manager.setPreferences("/server/mail/message/from", mailfrom.getText());
		manager.setPreferences("/server/mail/message/subject", subject
				.getText());
		manager.setPreferences("/server/mail/message/address/field", field);
		manager.setPreferences("/server/mail/message/datemode", datemode);
		manager.setPreferences("/server/alarm/sentmail", sentmail);
		manager.setPreferences("/server/mail/message/retry", retryCount
				.getText());
		manager.setPreferences("/server/mail/message/wait", waitTimeMilli
				.getText());
		manager.setPreferences("/server/mail/smtp/port", smtpPort.getText());
		manager.setPreferences("/server/mail/smtp/user", user.getText());
		manager
				.setPreferences("/server/mail/smtp/password", password
						.getText());
		dispose();
	}

	private void push_cansel() {
		dispose();
	}
}