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

public class NextPrevDialog extends JDialog {
	private static final long serialVersionUID = -6290322463705514815L;
	private static final Logger log = Logger.getLogger(NextPrevDialog.class);

	private final StreamManager manager;

	private final JTextField backButIcon = new JTextField();
	private final JTextField forwardButIcon = new JTextField();
	private final JTextField butWidth = new JTextField();
	private final JTextField butHeight = new JTextField();

	public NextPrevDialog(StreamManager manager, Frame parent) {
		super(parent, "戻る・進むボタン設定", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);

		// 戻るアイコン
		JLabel label = new JLabel("戻るアイコン：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		backButIcon.setText(manager.getClientConf("BandFButton.backIcon",
				"/toolbarButtonGraphics/navigation/Back16.gif"));
		panel.add(backButIcon);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(backButIcon, c);
		// 進むアイコン
		label = new JLabel("進むアイコン：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		forwardButIcon.setText(manager.getClientConf("BandFButton.forwardIcon",
				"/toolbarButtonGraphics/navigation/Forward16.gif"));
		panel.add(forwardButIcon);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(forwardButIcon, c);
		// 横幅
		label = new JLabel("横幅：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		butWidth.setText(manager.getClientConf("BandFButton.width", "20"));
		panel.add(butWidth);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(butWidth, c);
		// 高さ
		label = new JLabel("高さ：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		butHeight.setText(manager.getClientConf("BandFButton.height", "20"));
		panel.add(butHeight);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(butHeight, c);

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
		manager.setClientConf("BandFButton.backIcon", backButIcon.getText());
		manager.setClientConf("BandFButton.forwardIcon", forwardButIcon
				.getText());
		manager.setClientConf("BandFButton.width", butWidth.getText());
		manager.setClientConf("BandFButton.height", butHeight.getText());
		dispose();
	}
	private void push_cansel() {
		dispose();
	}

}
