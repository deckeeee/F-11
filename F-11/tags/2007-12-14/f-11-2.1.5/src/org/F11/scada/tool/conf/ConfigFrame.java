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
package org.F11.scada.tool.conf;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.xml.parsers.FactoryConfigurationError;

import org.F11.scada.tool.conf.alarm.AlarmDefineTab;
import org.F11.scada.tool.conf.client.ClientConf2Tab;
import org.F11.scada.tool.conf.client.ClientConfTab;
import org.F11.scada.tool.conf.individual.IndividualTab;
import org.F11.scada.tool.conf.pref.PreferencesTab;
import org.F11.scada.tool.conf.remove.RemoveTab;
import org.F11.scada.tool.conf.timeset.TimeSetTab;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

public class ConfigFrame extends JFrame {
	static final long serialVersionUID = 8454287945673610127L;
	private static Logger log;

	private final StreamManager manager;
	private final JButton saveButton = new JButton("�K�p");

	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public ConfigFrame(String title) throws Exception {
		super(title);
		manager = new StreamManager(saveButton);
		manager.load();
	}

	private void init() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		JTabbedPane tabbed = new JTabbedPane();

		tabbed.addTab("��{�@�\", null, new PreferencesTab(this, manager),
				"�uPreferences.xml�v�̓��e��ݒ肵�܂��B");
		tabbed.addTab("�x�񗚗�", null, new AlarmDefineTab(this, manager),
				"�uAlarmDefine.xml�v�̓��e��ݒ肵�܂��B");
		tabbed.addTab("�N���C�A���g�ݒ�", null, new ClientConfTab(this, manager),
				"�uClientConfiguration.xml�v�̓��e��ݒ肵�܂��B");
		tabbed.addTab("�N���C�A���g����", null, new ClientConf2Tab(this, manager),
				"�uClientConfiguration.xml�v�̗����֘A��ݒ肵�܂��B");
		tabbed.addTab("�N���C�A���gIP��", null, new IndividualTab(this, manager),
				"�uClientsDefine.xml�v�̓��e��ݒ肵�܂��B");
		tabbed.addTab("���v�ݒ�", null, new TimeSetTab(this, manager),
				"�uTimeSet.xml�v�̓��e��ݒ肵�܂��B");
		tabbed.addTab("�����폜�ݒ�", null, new RemoveTab(this, manager),
				"�uRemoveDefine.dicon�v�̓��e��ݒ肵�܂��B");

		mainPanel.add(tabbed, BorderLayout.CENTER);

		// button
		JPanel butPanel = new JPanel(new GridLayout(1, 0));
		saveButton.setEnabled(false);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_save();
			}
		});
		butPanel.add(saveButton);
		JButton but = new JButton("�n�j");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_ok();
			}
		});
		butPanel.add(but);
		but = new JButton("�L�����Z��");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_cansel();
			}
		});
		butPanel.add(but);
		mainPanel.add(butPanel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel);
		pack();
	}

	private void push_save() {
		try {
			manager.save();
			saveButton.setEnabled(false);
		} catch (Exception e) {
			log.error("save error", e);
		}
	}

	private void push_ok() {
		push_save();
		dispose();
	}

	private void push_cansel() {
		dispose();
	}

	/**
	 * @throws FactoryConfigurationError
	 */
	private static void createLog() throws FactoryConfigurationError {
		File file = new File("./log");
		file.mkdir();
		Class clazz = ConfigFrame.class;
		log = Logger.getLogger(clazz.getName());

		URL url = clazz.getResource("/resources/server_log4j.xml");
		if (url != null) {
			DOMConfigurator.configure(url);
		} else {
			url = clazz
					.getResource("/resources/xwife_server_main_log4j.properties");
			PropertyConfigurator.configure(url);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			createLog();

			ConfigFrame frame = new ConfigFrame("cur (Configfile Update Routine)");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.init();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} catch (Exception ex) {
			log.fatal("�ُ�I��", ex);
			System.exit(1);
		}
	}

}
