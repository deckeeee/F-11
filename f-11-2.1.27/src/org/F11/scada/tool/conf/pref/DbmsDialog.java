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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.F11.scada.tool.conf.StreamManager;
import org.apache.log4j.Logger;

public class DbmsDialog extends JDialog {
	private static final long serialVersionUID = -6959418485124273388L;
	private static final Logger log = Logger.getLogger(DbmsDialog.class);

	private final StreamManager manager;

	private String dbmsname;
	private String driver;
	private final JTextField option = new JTextField();
	private final JTextField dbname = new JTextField();
	private final JTextField username = new JTextField();
	private final JTextField password = new JTextField();

	public DbmsDialog(StreamManager manager, Frame parent) {
		super(parent, "データベース設定", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);
		// データベース種別（ドライバクラス名）
		JLabel label = new JLabel("データベース種別：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		JComboBox cb = new JComboBox();
		cb.addItem("PostgreSQL");
		cb.addItem("MySQL");
		cb.addItem("FireBird");
		dbmsname = manager.getPreferences("/server/jdbc/dbmsname", "");
		driver = manager.getPreferences("/server/jdbc/driver", "");
		if ("postgresql".equals(dbmsname))
			cb.setSelectedItem("PostgreSQL");
		else if ("mysql".equals(dbmsname))
			cb.setSelectedItem("MySQL");
		else if ("firebirdsql".equals(dbmsname))
			cb.setSelectedItem("FireBird");
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("PostgreSQL".equals(e.getItem())) {
						// データベース種別
						dbmsname = "postgresql";
						// JDBC ドライバクラス設定
						driver = "org.postgresql.Driver";
						// JDBC接続オプション
						option.setText("");
					} else if ("MySQL".equals(e.getItem())) {
						// データベース種別
						dbmsname = "mysql";
						// JDBC ドライバクラス設定
						driver = "com.mysql.jdbc.Driver";
						// JDBC接続オプション
						option.setText("?useUnicode=true"
								+ "&characterEncoding=Windows-31J"
								+ "&zeroDateTimeBehavior=convertToNull");
					} else if ("FireBird".equals(e.getItem())) {
						// データベース種別
						dbmsname = "firebirdsql";
						// JDBC ドライバクラス設定
						driver = "org.firebirdsql.jdbc.FBDriver";
						// JDBC接続オプション
						option.setText("?lc_ctype=EUCJ_0208");
					}
				}
			}
		});
		panel.add(cb);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(cb, c);
		// JDBC接続オプション
		label = new JLabel("jdbcオプション：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		option.setText(manager.getPreferences("/server/jdbc/option", ""));
		panel.add(option);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(option, c);
		// データベース使用環境定義
		label = new JLabel("データベース名：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		dbname.setText(manager.getPreferences("/server/jdbc/dbname", ""));
		panel.add(dbname);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(dbname, c);
		// ユーザー
		label = new JLabel("ユーザー名：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		username.setText(manager.getPreferences("/server/jdbc/username", ""));
		panel.add(username);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(username, c);
		// パスワード
		label = new JLabel("パスワード：");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		password.setText(manager.getPreferences("/server/jdbc/password", ""));
		panel.add(password);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(password, c);

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
		manager.setPreferences("/server/jdbc/dbmsname", dbmsname);
		manager.setPreferences("/server/jdbc/driver", driver);
		manager.setPreferences("/server/jdbc/option", option.getText());
		manager.setPreferences("/server/jdbc/dbname", dbname.getText());
		manager.setPreferences("/server/jdbc/username", username.getText());
		manager.setPreferences("/server/jdbc/password", password.getText());
		dispose();
	}
	private void push_cansel() {
		dispose();
	}
}