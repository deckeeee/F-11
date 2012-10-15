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

public class CalendarDialog extends JDialog {
	static final Logger log = Logger.getLogger(CalendarDialog.class);

	private StreamManager manager;

	private final JTextField labelHoriday = new JTextField();
	private final JTextField labelSpecial01 = new JTextField();
	private final JTextField labelSpecial02 = new JTextField();
	private final JTextField labelSpecial03 = new JTextField();
	private final JTextField labelSpecial04 = new JTextField();
	private final JTextField labelSpecial05 = new JTextField();
	private final JTextField messageHoriday = new JTextField();
	private final JTextField messageSpecial01 = new JTextField();
	private final JTextField messageSpecial02 = new JTextField();
	private final JTextField messageSpecial03 = new JTextField();
	private final JTextField messageSpecial04 = new JTextField();
	private final JTextField messageSpecial05 = new JTextField();

	public CalendarDialog(StreamManager manager, Frame parent) {
		super(parent, "�J�����_�[������ݒ�", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);
		setLabelHoriday(manager, gridbag, c, panel);
		setLabelSpcial01(manager, gridbag, c, panel);
		setLabelSpcial02(manager, gridbag, c, panel);
		setLabelSpcial03(manager, gridbag, c, panel);
		setLabelSpcial04(manager, gridbag, c, panel);
		setLabelSpcial05(manager, gridbag, c, panel);

		setMessageHoriday(manager, gridbag, c, panel);
		setMessageSpcial01(manager, gridbag, c, panel);
		setMessageSpcial02(manager, gridbag, c, panel);
		setMessageSpcial03(manager, gridbag, c, panel);
		setMessageSpcial04(manager, gridbag, c, panel);
		setMessageSpcial05(manager, gridbag, c, panel);

		mainPanel.add(panel, BorderLayout.CENTER);

		panel = new JPanel(new GridLayout(1, 0));
		setOk(panel);
		setCancel(panel);
		mainPanel.add(panel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(parent);
	}

	private void setLabelHoriday(StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("�{�^��1�̕����F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		labelHoriday.setText(manager.getPreferences("/server/calendar/label/horiday","�x"));
		panel.add(labelHoriday);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(labelHoriday, c);
	}

	private void setLabelSpcial01(StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("�{�^��2�̕����F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		labelSpecial01.setText(manager.getPreferences("/server/calendar/label/special01","��1"));
		panel.add(labelSpecial01);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(labelSpecial01, c);
	}

	private void setLabelSpcial02(StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("�{�^��3�̕����F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		labelSpecial02.setText(manager.getPreferences("/server/calendar/label/special02","��2"));
		panel.add(labelSpecial02);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(labelSpecial02, c);
	}

	private void setLabelSpcial03(StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("�{�^��4�̕����F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		labelSpecial03.setText(manager.getPreferences("/server/calendar/label/special03","��3"));
		panel.add(labelSpecial03);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(labelSpecial03, c);
	}

	private void setLabelSpcial04(StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("�{�^��5�̕����F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		labelSpecial04.setText(manager.getPreferences("/server/calendar/label/special04","��4"));
		panel.add(labelSpecial04);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(labelSpecial04, c);
	}

	private void setLabelSpcial05(StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("�{�^��6�̕����F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		labelSpecial05.setText(manager.getPreferences("/server/calendar/label/special05","��5"));
		panel.add(labelSpecial05);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(labelSpecial05, c);
	}

	private void setMessageHoriday(StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("�{�^��1�ɑ΂��郁�b�Z�[�W�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		messageHoriday.setText(manager.getPreferences("/server/calendar/message/horiday","�x��"));
		panel.add(messageHoriday);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(messageHoriday, c);
	}

	private void setMessageSpcial01(StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("�{�^��2�ɑ΂��郁�b�Z�[�W�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		messageSpecial01.setText(manager.getPreferences("/server/calendar/message/special01","�����1"));
		panel.add(messageSpecial01);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(messageSpecial01, c);
	}

	private void setMessageSpcial02(StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("�{�^��3�ɑ΂��郁�b�Z�[�W�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		messageSpecial02.setText(manager.getPreferences("/server/calendar/message/special02","�����2"));
		panel.add(messageSpecial02);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(messageSpecial02, c);
	}

	private void setMessageSpcial03(StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("�{�^��4�ɑ΂��郁�b�Z�[�W�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		messageSpecial03.setText(manager.getPreferences("/server/calendar/message/special03","�����3"));
		panel.add(messageSpecial03);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(messageSpecial03, c);
	}

	private void setMessageSpcial04(StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("�{�^��5�ɑ΂��郁�b�Z�[�W�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		messageSpecial04.setText(manager.getPreferences("/server/calendar/message/special04","�����4"));
		panel.add(messageSpecial04);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(messageSpecial04, c);
	}

	private void setMessageSpcial05(StreamManager manager,
			GridBagLayout gridbag,
			GridBagConstraints c,
			JPanel panel) {
		JLabel label = new JLabel("�{�^��6�ɑ΂��郁�b�Z�[�W�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		messageSpecial05.setText(manager.getPreferences("/server/calendar/message/special05","�����5"));
		panel.add(messageSpecial05);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(messageSpecial05, c);
	}

	private void setOk(JPanel panel) {
		JButton but = new JButton("�n�j");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.setPreferences(
					"/server/calendar/label/horiday",
					labelHoriday.getText());
				manager.setPreferences(
					"/server/calendar/label/special01",
					labelSpecial01.getText());
				manager.setPreferences(
					"/server/calendar/label/special02",
					labelSpecial02.getText());
				manager.setPreferences(
					"/server/calendar/label/special03",
					labelSpecial03.getText());
				manager.setPreferences(
					"/server/calendar/label/special04",
					labelSpecial04.getText());
				manager.setPreferences(
					"/server/calendar/label/special05",
					labelSpecial05.getText());
				manager.setPreferences(
					"/server/calendar/message/horiday",
					messageHoriday.getText());
				manager.setPreferences(
					"/server/calendar/message/special01",
					messageSpecial01.getText());
				manager.setPreferences(
					"/server/calendar/message/special02",
					messageSpecial02.getText());
				manager.setPreferences(
					"/server/calendar/message/special03",
					messageSpecial03.getText());
				manager.setPreferences(
					"/server/calendar/message/special04",
					messageSpecial04.getText());
				manager.setPreferences(
					"/server/calendar/message/special05",
					messageSpecial05.getText());
				dispose();
			}
		});
		panel.add(but);
	}

	private void setCancel(JPanel panel) {
		JButton but = new JButton("�L�����Z��");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(but);
	}
}