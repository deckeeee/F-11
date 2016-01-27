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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.wizerd.Main;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 * MySQLのインストールコピーを処理するパネルです。
 * 
 * @author maekawa
 * 
 */
public class PreferenceWizerd extends Wizerd {
	private final Logger logger = Logger.getLogger(PreferenceWizerd.class);
	private JTextField userField;
	private JTextField passField;
	private JTextField dbnameField;
	private final String base;

	public PreferenceWizerd(String base) {
		super(new BorderLayout());
		this.base = base;
		add(getNorth(), BorderLayout.NORTH);
		add(getCenter(), BorderLayout.CENTER);
		Application.getInstance().getContext().getResourceMap(
			PreferenceWizerd.class).injectComponents(this);
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
		panel.setBorder(BorderFactory.createTitledBorder("MySQL設定"));
		panel.add(getPlaceSelect(), BorderLayout.CENTER);
		return panel;
	}

	private Component getPlaceSelect() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 0, 2);
		setUserFields(panel, c);
		c.gridy = 1;
		setPassFields(panel, c);
		c.gridy = 2;
		setDbnameFields(panel, c);
		return panel;
	}

	private void setUserFields(JPanel panel, GridBagConstraints c) {
		userField =
			new JTextField(EnvironmentManager.get(
				"/server/jdbc/username",
				"user"));
		userField.setName("userField");
		setFields(panel, c, userField);
	}

	private void setPassFields(JPanel panel, GridBagConstraints c) {
		passField =
			new JTextField(EnvironmentManager.get(
				"/server/jdbc/password",
				"pass1234"));
		passField.setName("passField");
		setFields(panel, c, passField);
	}

	private void setDbnameFields(JPanel panel, GridBagConstraints c) {
		dbnameField =
			new JTextField(EnvironmentManager.get("/server/jdbc/dbname", ""));
		dbnameField.setName("dbnameField");
		setFields(panel, c, dbnameField);
	}

	private void setFields(JPanel panel, GridBagConstraints c, JTextField field) {
		JLabel label = new JLabel();
		label.setName(field.getName() + "Label");
		panel.add(label, c);
		panel.add(field, c);
	}

	@Override
	public void init() {
		userField.requestFocus();
	}

	@Override
	public Task<Void, Void> execute(Main main) {
		Task<Void, Void> task =
			new PreferenceTask(main, userField, passField, dbnameField, base);
		task.addTaskListener(main.getPreferenceTaskListener());
		return task;
	}

	@Override
	public String getBaseDirectory() {
		return base;
	}
}
