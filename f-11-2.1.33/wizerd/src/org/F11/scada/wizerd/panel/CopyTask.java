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
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 * ファイルコピーのタスク 内部でxcopyのプロセスを起動します。
 * 
 * @author maekawa
 * 
 */
public class CopyTask extends Task<Void, Void> {
	private static final int MYSQL_FILE_COUNT = 670;
	private final JTextField srcField;
	private final JTextField dstField;
	private final JTextArea console;
	private Process process;

	public CopyTask(
			Application application,
			JTextField srcField,
			JTextField dstField,
			JTextArea console) {
		super(application);
		this.srcField = srcField;
		this.dstField = dstField;
		this.console = console;
	}

	@Override
	protected Void doInBackground() throws Exception {
		crearConsole();
		copyFiles();
		registryService();
		startService();
		return null;
	}

	private void copyFiles() throws IOException, BadLocationException {
		ProcessBuilder processBuilder =
			new ProcessBuilder(
				"xcopy",
				"/i",
				"/s",
				"/y",
				srcField.getText(),
				dstField.getText());
		process = processBuilder.start();
		writeConsole(process);
	}

	private void writeConsole(Process p)
			throws IOException,
			BadLocationException {
		BufferedReader in =
			new BufferedReader(new InputStreamReader(p.getInputStream()));
		try {
			writeConsole(in);
		} finally {
			if (null != in) {
				in.close();
			}
		}
	}

	private void writeConsole(BufferedReader in)
			throws IOException,
			BadLocationException {
		for (int i = 0;; i++) {
			final String str = in.readLine();
			if (null == str) {
				break;
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					console.append(str + "\n");
				}
			});
			setProgress(i, 0, MYSQL_FILE_COUNT);
		}
	}

	private void crearConsole() throws BadLocationException {
		Document doc = console.getDocument();
		doc.remove(0, doc.getLength());
	}

	private void registryService() throws IOException, BadLocationException {
		ProcessBuilder processBuilder =
			new ProcessBuilder(
				dstField.getText() + "\\bin\\mysqld-nt.exe",
				"--install",
				"MySql",
				"--defaults-file=" + dstField.getText() + "\\my.ini");
		Process process = processBuilder.start();
		writeConsole(process);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				console.append("MySqlサービスを登録しました。\n");
			}
		});
	}

	private void startService() throws IOException, BadLocationException {
		ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "net start MySql");
		Process process = pb.start();
		writeConsole(process);
	}

	@Override
	protected void cancelled() {
		if (process != null) {
			process.destroy();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					console.append("コピーを中止しました。\n");
				}
			});
		}
		super.cancelled();
	}
}
