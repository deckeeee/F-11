/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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
 * 
 */

package org.F11.scada.tool.conf.client;

import static org.F11.scada.util.ComponentUtil.addLabel;
import static org.F11.scada.util.ComponentUtil.addTextArea;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.F11.scada.tool.conf.StreamManager;

/**
 * 警報一覧の列幅設定ダイアログ
 * 
 * @author maekawa
 * 
 */
public class AlarmTableColumn extends JDialog {
	private static final long serialVersionUID = -3024378393789154566L;
	private final StreamManager manager;
	private final JTextField unit = new JTextField();
	private final JTextField attribute = new JTextField();
	private final JTextField status = new JTextField();
	private final JTextField sort = new JTextField();

	public AlarmTableColumn(Frame frameParent, StreamManager manager) {
		super(frameParent, "警報一覧", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel panel = getCenter();
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		mainPanel.add(panel, BorderLayout.CENTER);
		mainPanel.add(getSouth(), BorderLayout.SOUTH);
		add(mainPanel);
		pack();
		setLocationRelativeTo(frameParent);
	}

	private JPanel getCenter() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("列幅 (ﾋﾟｸｾﾙ)"));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		setUnit(c, panel);
		setAttribute(c, panel);
		setStatus(c, panel);
		setSort(c, panel);
		return panel;
	}

	private void setUnit(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("機器記号：");
		addLabel(c, panel, label);
		addTextArea(c, panel, unit, manager.getClientConf(
			"org.F11.scada.xwife.applet.alarm.unitSize",
			"150"));
	}

	private void setAttribute(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("属性：");
		addLabel(c, panel, label);
		addTextArea(c, panel, attribute, manager.getClientConf(
			"org.F11.scada.xwife.applet.alarm.attributeSize",
			"78"));
	}

	private void setStatus(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("状態：");
		addLabel(c, panel, label);
		addTextArea(c, panel, status, manager.getClientConf(
			"org.F11.scada.xwife.applet.alarm.statusSize",
			"78"));
	}

	private void setSort(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("種別：");
		addLabel(c, panel, label);
		addTextArea(c, panel, sort, manager.getClientConf(
			"org.F11.scada.xwife.applet.alarm.sortSize",
			"78"));
	}

	private Component getSouth() {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		box.add(Box.createHorizontalGlue());
		box.add(getOk());
		box.add(Box.createHorizontalStrut(5));
		box.add(getCancel());
		return box;
	}

	private JButton getOk() {
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.unitSize",
					unit.getText());
				manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.attributeSize",
					attribute.getText());
				manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.statusSize",
					status.getText());
				manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.sortSize",
					sort.getText());
				dispose();
			}
		});
		return button;
	}

	private JButton getCancel() {
		JButton button = new JButton("CANCEL");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		return button;
	}
}
