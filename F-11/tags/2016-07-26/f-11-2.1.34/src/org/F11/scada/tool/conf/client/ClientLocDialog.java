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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.F11.scada.tool.conf.StreamManager;
import org.apache.log4j.Logger;

public class ClientLocDialog extends JDialog {
	private static final long serialVersionUID = -361830816764939968L;
	private static final Logger log = Logger.getLogger(ClientLocDialog.class);

	private final StreamManager manager;

	private final JTextField sw = new JTextField();
	private final JTextField sh = new JTextField();
	private final JTextField lx = new JTextField();
	private final JTextField ly = new JTextField();

	public ClientLocDialog(StreamManager manager, Frame parent) {
		super(parent, "クライアントのサイズと位置設定", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);

		// 横幅
		JLabel label = new JLabel("横幅：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		sw.setText(manager.getClientConf(
				"xwife.applet.Applet.frame.size.width", "1152"));
		panel.add(sw);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(sw, c);
		// 高さ
		label = new JLabel("高さ：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		sh.setText(manager.getClientConf(
				"xwife.applet.Applet.frame.size.height", "864"));
		panel.add(sh);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(sh, c);
		// x
		label = new JLabel("Ｘ：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		lx.setText(manager.getClientConf(
				"xwife.applet.Applet.frame.location.x", "0"));
		panel.add(lx);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(lx, c);
		// y
		label = new JLabel("Ｙ：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		ly.setText(manager.getClientConf(
				"xwife.applet.Applet.frame.location.y", "0"));
		panel.add(ly);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(ly, c);

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
		manager.setClientConf("xwife.applet.Applet.frame.size.width", sw
				.getText());
		manager.setClientConf("xwife.applet.Applet.frame.size.height", sh
				.getText());
		manager.setClientConf("xwife.applet.Applet.frame.location.x", lx
				.getText());
		manager.setClientConf("xwife.applet.Applet.frame.location.y", ly
				.getText());
		dispose();
	}
	private void push_cansel() {
		dispose();
	}

}
