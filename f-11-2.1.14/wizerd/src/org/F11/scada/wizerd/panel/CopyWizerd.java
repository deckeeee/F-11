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

package org.F11.scada.wizerd.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;

import org.F11.scada.wizerd.Main;
import org.F11.scada.wizerd.util.ComponentUtil;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 * MySQLのインストールコピーを処理するパネルです。
 * 
 * @author maekawa
 * 
 */
public class CopyWizerd extends Wizerd {
	private JTextField srcField;
	private JTextField dstField;
	private JTextArea console;

	public CopyWizerd() {
		super(new BorderLayout());
		add(getNorth(), BorderLayout.NORTH);
		add(getCenter(), BorderLayout.CENTER);
		Application
			.getInstance()
			.getContext()
			.getResourceMap(CopyWizerd.class)
			.injectComponents(this);
	}

	private Component getNorth() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		JLabel label = new JLabel();
		label.setName("titleLabel");
		CompoundBorder b =
			new CompoundBorder(
				BorderFactory.createLoweredBevelBorder(),
				BorderFactory.createEmptyBorder(0, 20, 0, 20));
		label.setBorder(b);
		panel.add(label, BorderLayout.CENTER);
		return panel;
	}

	private Component getCenter() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
		panel.add(getMainCenter(), BorderLayout.CENTER);
		return panel;
	}

	private Component getMainCenter() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("場所の指定"));
		panel.add(getPlaceSelect(), BorderLayout.CENTER);
		return panel;
	}

	private Component getPlaceSelect() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 0, 2);
		setSrcFields(panel, c);
		c.gridy = 1;
		setDstFields(panel, c);
		c.gridy = 2;
		setConsole(panel, c);
		return panel;
	}

	private void setSrcFields(JPanel panel, GridBagConstraints c) {
		srcField = new JTextField();
		srcField.setName("srcField");
		setFields(panel, c, srcField);
	}

	private void setDstFields(JPanel panel, GridBagConstraints c) {
		dstField = new JTextField();
		dstField.setName("dstField");
		setFields(panel, c, dstField);
	}

	private void setFields(JPanel panel, GridBagConstraints c, JTextField field) {
		JLabel label = new JLabel();
		label.setName(field.getName() + "Label");
		panel.add(label, c);
		ComponentUtil.setDropTarget(field);
		panel.add(field, c);
		JButton button = new JButton();
		button.setName(field.getName() + "Button");
		button.addActionListener(new FileChooseAction(field));
		panel.add(button, c);
	}

	private void setConsole(JPanel panel, GridBagConstraints c) {
		c.gridwidth = 3;
		c.insets = new Insets(5, 0, 0, 0);
		console = new JTextArea();
		console.setName("consoleTextArea");
		panel.add(new JScrollPane(console), c);
	}

	@Override
	public void init() {
		dstField.requestFocus();
	}

	@Override
	public Task<Void, Void> execute(Main main) {
		if (showCheckDialog()) {
			CopyTask copyTask = new CopyTask(main, srcField, dstField, console);
			copyTask.addTaskListener(main.getTaskListener());
			return copyTask;
		} else {
			return null;
		}
	}

	private boolean showCheckDialog() {
		String[] option = { "はい", "いいえ" };
		int rc =
			JOptionPane.showOptionDialog(
				this,
				"MySQLを対象の場所へコピーします。よろしいですか？",
				"MySQLコピー",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				option,
				option[1]);
		return rc == JOptionPane.OK_OPTION;
	}

	@Override
	public String getBaseDirectory() {
		return dstField.getText();
	}
}
