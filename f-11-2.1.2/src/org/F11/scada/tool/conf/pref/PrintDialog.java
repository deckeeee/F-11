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
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.F11.scada.tool.conf.StreamManager;
import org.F11.scada.tool.conf.alarm.FontDialog;
import org.apache.log4j.Logger;

public class PrintDialog extends JDialog {
	static final long serialVersionUID = -4444931079909390433L;
	static final Logger log = Logger.getLogger(PrintDialog.class);

	private StreamManager manager;

	private String size;
	private String orientation;
	private final JTextField fontName = new JTextField();
	private final JTextField pagelines = new JTextField();

	public PrintDialog(StreamManager manager, Frame parent) {
		super(parent, "警報一覧印字設定", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);
		// 用紙サイズ
		JLabel label = new JLabel("用紙サイズ：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox(new String[]{"ISO_A4", "ISO_B5"});
		size = manager.getPreferences("/server/alarm/print/size", "ISO_A4");
		cb.setSelectedItem(size);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					size = (String) e.getItem();
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
		// 用紙方向
		label = new JLabel("用紙方向：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		cb = new JComboBox();
		cb.addItem("縦");
		cb.addItem("横");
		orientation = manager.getPreferences("/server/alarm/print/orientation",
				"PORTRAIT");
		if ("PORTRAIT".equals(orientation))
			cb.setSelectedItem("縦");
		else if ("LANDSCAPE".equals(orientation))
			cb.setSelectedItem("横");
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("縦".equals(e.getItem()))
						orientation = "PORTRAIT";
					else if ("横".equals(e.getItem()))
						orientation = "LANDSCAPE";
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
		// フォント
		label = new JLabel("フォント：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		fontName.setText(manager.getPreferences("/server/alarm/print/font",
				"Monospaced,PLAIN,10"));
		fontName.setEditable(false);
		panel.add(fontName);
		c.weightx = 0.5;
		c.gridwidth = 1;
		gridbag.setConstraints(fontName, c);
		JButton but = new JButton("Font");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringTokenizer st = new StringTokenizer(fontName.getText(),
						",");
				FontDialog dlg = new FontDialog(PrintDialog.this);
				if (dlg.showFontDialog(st.nextToken(), st.nextToken(), st
						.nextToken())) {
					fontName.setText(dlg.getFontName() + ","
							+ dlg.getFontStyle() + "," + dlg.getFontSize());
				}
			}
		});
		panel.add(but);
		c.weightx = 0.5;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(but, c);
		// １頁行数
		label = new JLabel("１頁行数：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		pagelines.setText(manager.getPreferences(
				"/server/alarm/print/pagelines", "10"));
		panel.add(pagelines);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(pagelines, c);

		mainPanel.add(panel, BorderLayout.CENTER);

		panel = new JPanel(new GridLayout(1, 0));
		but = new JButton("ＯＫ");
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
		manager.setPreferences("/server/alarm/print/size", size);
		manager.setPreferences("/server/alarm/print/orientation", orientation);
		manager.setPreferences("/server/alarm/print/font", fontName.getText());
		manager.setPreferences("/server/alarm/print/pagelines", pagelines
				.getText());
		dispose();
	}
	private void push_cansel() {
		dispose();
	}
}