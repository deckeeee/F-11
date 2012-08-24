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
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.tool.conf.StreamManager;
import org.apache.log4j.Logger;

public class SchColorsDialog extends JDialog {
	private static final long serialVersionUID = 558379658169656742L;
	private static final Logger log = Logger.getLogger(SchColorsDialog.class);

	private final StreamManager manager;

	private String oncolor;
	private String offcolor;
	private String barcolor;

	public SchColorsDialog(StreamManager manager, Frame parent) {
		super(parent, "スケジュールバーの色設定", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);

		// ON色
		JLabel label = new JLabel("ON色：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox(new Vector(ColorFactory.getColorNames()));
		oncolor = manager.getClientConf("barmatrix.oncolor", "RED")
				.toUpperCase();
		cb.setSelectedItem(oncolor);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					oncolor = (String) e.getItem();
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
		// OFF色
		label = new JLabel("OFF色：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		cb = new JComboBox(new Vector(ColorFactory.getColorNames()));
		offcolor = manager.getClientConf("barmatrix.offcolor", "LIME")
				.toUpperCase();
		cb.setSelectedItem(offcolor);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					offcolor = (String) e.getItem();
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
		// bar色
		label = new JLabel("bar色：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		cb = new JComboBox(new Vector(ColorFactory.getColorNames()));
		barcolor = manager.getClientConf("barmatrix2.barcolor", "PINK")
				.toUpperCase();
		cb.setSelectedItem(barcolor);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					barcolor = (String) e.getItem();
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);

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
		manager.setClientConf("barmatrix.oncolor", oncolor);
		manager.setClientConf("barmatrix.offcolor", offcolor);
		manager.setClientConf("barmatrix2.barcolor", barcolor);
		dispose();
	}
	private void push_cansel() {
		dispose();
	}

}
