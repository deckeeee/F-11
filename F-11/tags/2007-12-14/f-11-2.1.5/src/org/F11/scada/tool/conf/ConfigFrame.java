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
	private final JButton saveButton = new JButton("適用");

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

		tabbed.addTab("基本機能", null, new PreferencesTab(this, manager),
				"「Preferences.xml」の内容を設定します。");
		tabbed.addTab("警報履歴", null, new AlarmDefineTab(this, manager),
				"「AlarmDefine.xml」の内容を設定します。");
		tabbed.addTab("クライアント設定", null, new ClientConfTab(this, manager),
				"「ClientConfiguration.xml」の内容を設定します。");
		tabbed.addTab("クライアント履歴", null, new ClientConf2Tab(this, manager),
				"「ClientConfiguration.xml」の履歴関連を設定します。");
		tabbed.addTab("クライアントIP別", null, new IndividualTab(this, manager),
				"「ClientsDefine.xml」の内容を設定します。");
		tabbed.addTab("時計設定", null, new TimeSetTab(this, manager),
				"「TimeSet.xml」の内容を設定します。");
		tabbed.addTab("自動削除設定", null, new RemoveTab(this, manager),
				"「RemoveDefine.dicon」の内容を設定します。");

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
		JButton but = new JButton("ＯＫ");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_ok();
			}
		});
		butPanel.add(but);
		but = new JButton("キャンセル");
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
			log.fatal("異常終了", ex);
			System.exit(1);
		}
	}

}
