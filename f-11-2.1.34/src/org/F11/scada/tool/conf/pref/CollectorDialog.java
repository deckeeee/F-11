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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.Registry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.F11.scada.tool.conf.StreamManager;
import org.apache.log4j.Logger;

public class CollectorDialog extends JDialog {
	private static final long serialVersionUID = 3804465471216949027L;
	private static final Logger log = Logger.getLogger(CollectorDialog.class);

	public static final String RMI_RECV_PORT_COLLECTOR = "50000";

	private final StreamManager manager;

	private final JTextField port = new JTextField();
	private final JTextField recvPort = new JTextField();
	private final JTextField retryCount = new JTextField();

	public CollectorDialog(StreamManager manager, Frame parent) {
		super(parent, "�R���N�^�[�|�[�g�ݒ�", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		// rmi���W�X�g���|�[�gNo.
		JPanel panel = new JPanel(new GridLayout(0, 2));
		panel.add(new JLabel("rmi���W�X�g���|�[�gNo.�F"));
		port.setText(manager.getPreferences("/server/rmi/collectorserver/port",
				String.valueOf(Registry.REGISTRY_PORT)));
		panel.add(port);
		// rmi�T�[�r�X�|�[�gNo.(0:����)
		panel.add(new JLabel("rmi�T�[�r�X�|�[�gNo.(0:����)�F"));
		recvPort.setText(manager.getPreferences(
				"/server/rmi/collectorserver/rmiReceivePort",
				RMI_RECV_PORT_COLLECTOR));
		panel.add(recvPort);
		// rmi���g���C��(-1:����)
		panel.add(new JLabel("rmi���g���C��(-1:����)�F"));
		retryCount.setText(manager.getPreferences(
				"/server/rmi/collectorserver/retry/count", "-1"));
		panel.add(retryCount);
		mainPanel.add(panel, BorderLayout.CENTER);

		panel = new JPanel(new GridLayout(1, 0));
		JButton but = new JButton("�f�t�H���g");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_def();
			}
		});
		panel.add(but);
		but = new JButton("�n�j");
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

	private void push_def() {
		log.debug("push_def");
		port.setText(String.valueOf(Registry.REGISTRY_PORT));
		recvPort.setText(RMI_RECV_PORT_COLLECTOR);
	}
	private void push_ok() {
		log.debug("push_ok");
		manager.setPreferences("/server/rmi/collectorserver/port", port
				.getText());
		manager.setPreferences("/server/rmi/collectorserver/rmiReceivePort",
				recvPort.getText());
		manager.setPreferences("/server/rmi/collectorserver/retry/count",
				retryCount.getText());
		dispose();
	}
	private void push_cansel() {
		dispose();
	}
}