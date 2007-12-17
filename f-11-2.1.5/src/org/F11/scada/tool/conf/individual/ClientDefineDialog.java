/*
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
 */
package org.F11.scada.tool.conf.individual;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.F11.scada.tool.conf.client.ClientLocDialog;
import org.F11.scada.tool.conf.io.ClientDefineBean;
import org.apache.log4j.Logger;

public class ClientDefineDialog extends JDialog {
	private static final long serialVersionUID = -1851320143479970207L;
	private static final Logger log = Logger.getLogger(ClientLocDialog.class);

	private final JTextField ipAddress = new JTextField();
	private final JTextField defaultUserName = new JTextField();
	private ClientDefineBean retBean = null;

	private ClientDefineDialog(Frame parent, String title, ClientDefineBean bean) {
		super(parent, title, true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);

		// IPアドレス
		JLabel label = new JLabel("IPアドレス：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		ipAddress.setText(bean.getIpAddress());
		panel.add(ipAddress);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(ipAddress, c);
		// デフォルトユーザー
		label = new JLabel("デフォルトユーザー：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		defaultUserName.setText(bean.getDefaultUserName());
		panel.add(defaultUserName);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(defaultUserName, c);

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
		retBean = new ClientDefineBean();
		retBean.setIpAddress(ipAddress.getText());
		retBean.setDefaultUserName(defaultUserName.getText());
		dispose();
	}
	private void push_cansel() {
		retBean = null;
		dispose();
	}

	public static ClientDefineBean showClientDefineDialog(Frame parent,
			String title, ClientDefineBean bean) {
		ClientDefineDialog dlg = new ClientDefineDialog(parent, title, bean);
		dlg.setVisible(true);
		return dlg.retBean;
	}
}
