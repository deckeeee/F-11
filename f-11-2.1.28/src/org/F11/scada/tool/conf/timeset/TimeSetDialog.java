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
package org.F11.scada.tool.conf.timeset;

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

import org.F11.scada.tool.conf.io.TimeSetBean;
import org.apache.log4j.Logger;

public class TimeSetDialog extends JDialog {
	static final long serialVersionUID = -6031325506578324731L;
	private static final Logger log = Logger.getLogger(TimeSetDialog.class);

	private String kind;
	private final JTextField provider = new JTextField();
	private final JTextField holder = new JTextField();
	private TimeSetBean retBean = null;

	private TimeSetDialog(Frame parent, String title, TimeSetBean bean) {
		super(parent, title, true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);

		// 
		JLabel label = new JLabel("種別：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox(new String[]{"書込み", "読込み", "生存確認"});
		if (null != bean.getKind()) {
			kind = bean.getKind();
		} else {
			kind = "write";
		}
		if ("write".equals(kind)) {
			cb.setSelectedIndex(0);
		} else if ("read".equals(kind)) {
			cb.setSelectedIndex(1);
		} else if ("lifecheck".equals(kind)) {
			cb.setSelectedIndex(2);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("書込み".equals(e.getItem())) {
						kind = "write";
					} else if ("読込み".equals(e.getItem())) {
						kind = "read";
					} else if ("生存確認".equals(e.getItem())) {
						kind = "lifecheck";
					} else {
						System.out.println("アイテムが不正です。");
					}
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);

		// プロバイダ名
		label = new JLabel("プロバイダ名：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		provider.setText(bean.getProvider());
		panel.add(provider);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(provider, c);
		// ホルダ名
		label = new JLabel("ホルダ名：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		holder.setText(bean.getHolder());
		panel.add(holder);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(holder, c);

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
		retBean = new TimeSetBean();
		retBean.setKind(kind);
		retBean.setProvider(provider.getText());
		retBean.setHolder(holder.getText());
		dispose();
	}
	private void push_cansel() {
		retBean = null;
		dispose();
	}

	public static TimeSetBean showClientDefineDialog(Frame parent,
			String title, TimeSetBean bean) {
		TimeSetDialog dlg = new TimeSetDialog(parent, title, bean);
		dlg.setVisible(true);
		return dlg.retBean;
	}
}
