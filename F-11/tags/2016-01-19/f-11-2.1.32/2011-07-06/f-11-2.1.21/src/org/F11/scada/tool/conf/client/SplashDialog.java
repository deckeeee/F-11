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

public class SplashDialog extends JDialog {
	private static final long serialVersionUID = -800821801227900341L;
	private static final Logger log = Logger.getLogger(SplashDialog.class);

	private final StreamManager manager;

	private String splashOn;
	private final JTextField splashTitle = new JTextField();
	private final JTextField splashImage = new JTextField();

	public SplashDialog(StreamManager manager, Frame parent) {
		super(parent, "�X�v���b�V���ݒ�", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);

		// �X�v���b�V���̗L�� �����l��false
		JLabel label = new JLabel("�X�v���b�V���\���F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox(new String[]{"����", "���Ȃ�"});
		splashOn = manager.getClientConf(
				"org.F11.scada.xwife.applet.splash.on", "false");
		if ("false".equals(splashOn))
			cb.setSelectedIndex(1);
		else
			cb.setSelectedIndex(0);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem()))
						splashOn = "true";
					else
						splashOn = "false";
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
		// �X�v���b�V���̃^�C�g��
		label = new JLabel("�^�C�g���F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		splashTitle.setText(manager.getClientConf(
				"org.F11.scada.xwife.applet.splash.title", "�V�X�e���N����"));
		panel.add(splashTitle);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(splashTitle, c);
		// �X�v���b�V���̃C���[�W
		label = new JLabel("�C���[�W�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		splashImage.setText(manager
				.getClientConf("org.F11.scada.xwife.applet.splash.image",
						"/images/splash.png"));
		panel.add(splashImage);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(splashImage, c);

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
		manager.setClientConf("org.F11.scada.xwife.applet.splash.on", splashOn);
		manager.setClientConf("org.F11.scada.xwife.applet.splash.title",
				splashTitle.getText());
		manager.setClientConf("org.F11.scada.xwife.applet.splash.image",
				splashImage.getText());
		dispose();
	}
	private void push_cansel() {
		dispose();
	}

}
