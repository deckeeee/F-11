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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.WifeUtilities;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.Task;

/**
 * MySQLの設定を実行するタスク
 * 
 * @author maekawa
 * 
 */
public class PreferenceTask extends Task<Void, Void> {
	@Resource
	private String rootUser;
	@Resource
	private String rootPass;
	private final JTextField userField;
	private final JTextField passField;
	private final JTextField dbnameField;
	private final String base;

	public PreferenceTask(
			Application application,
			JTextField userField,
			JTextField passField,
			JTextField dbnameField,
			String base) {
		super(application);
		this.userField = userField;
		this.passField = passField;
		this.dbnameField = dbnameField;
		this.base = base;
		getContext().getResourceMap(PreferenceTask.class).injectFields(this);
	}

	@Override
	protected Void doInBackground() throws Exception {
		Class.forName(WifeUtilities.getJdbcDriver());
		Connection con = null;
		try {
			con = DriverManager.getConnection(getJdbcUri(), rootUser, rootPass);
			createUser(con);
			createDatabase(con);
		} finally {
			if (con != null) {
				con.close();
			}
		}
		createTables();
		return null;
	}

	private String getJdbcUri() {
		return "jdbc:"
			+ WifeUtilities.getDBMSName()
			+ "://"
			+ EnvironmentManager.get("/server/jdbc/servername", "")
			+ "/"
			+ "mysql"
			+ EnvironmentManager.get("/server/jdbc/option", "");
	}

	private void executeSql(Connection con, String sql) throws SQLException {
		Statement st = con.createStatement();
		try {
			st.execute(sql);
		} finally {
			if (st != null) {
				st.close();
			}
		}
	}

	private void createUser(Connection con) throws SQLException {
		executeSql(con, getCreateUserSql());
		setProgress(0, 0, 2);
	}

	private String getCreateUserSql() {
		return "GRANT ALL PRIVILEGES ON *.* TO "
			+ userField.getText()
			+ "@localhost IDENTIFIED BY '"
			+ passField.getText()
			+ "' WITH GRANT OPTION;";
	}

	private void createDatabase(Connection con) throws SQLException {
		executeSql(con, getCreateDbSql());
		setProgress(1, 0, 2);
	}

	private String getCreateDbSql() {
		return "CREATE DATABASE " + dbnameField.getText() + ";";
	}

	private void createTables() throws SQLException, IOException {
		getCreateTablesSql();
		setProgress(2, 0, 2);
	}

	private void getCreateTablesSql() throws IOException {
		ProcessBuilder bp = new ProcessBuilder();
		bp.command(
			base + "\\bin\\mysql.exe",
			"-u",
			rootUser,
			"-p" + rootPass,
			dbnameField.getText());
		Process p = bp.start();
		BufferedWriter out =
			new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		File file = getFile();
		BufferedReader in = new BufferedReader(new FileReader(file));
		inputSqlFile(out, in);
	}

	private void inputSqlFile(BufferedWriter out, BufferedReader in)
			throws IOException {
		try {
			while (true) {
				String s = in.readLine();
				if (null == s) {
					out.write("\\q");
					out.newLine();
					break;
				}
				out.write(s);
				out.newLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	private File getFile() {
		File file = new File("./SQL/MySQL/create.sql");
		if (file.exists()) {
			return file;
		} else {
			return showDialog();
		}
	}

	private File showDialog() {
		JFileChooser chooser = getFileChooser();
		int ret = chooser.showOpenDialog(passField);
		if (ret == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else {
			JOptionPane.showMessageDialog(
				passField,
				"テーブル生成SQLファイル(create.sql)を選択してください。",
				"テーブル生成SQLファイル選択エラー",
				JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	private JFileChooser getFileChooser() {
		JFileChooser chooser = new JFileChooser(".");
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".sql")
					|| f.getName().endsWith(".SQL");
			}

			@Override
			public String getDescription() {
				return "SQLファイル";
			}
		});
		return chooser;
	}
}
