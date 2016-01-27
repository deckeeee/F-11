/*
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
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
 */
package org.F11.scada.tool.conf.client;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.F11.scada.tool.conf.StreamManager;

public class ClientConf3Tab extends JScrollPane implements DocumentListener {
	private static final long serialVersionUID = 4106233686607085743L;
	private final Frame frameParent;
	private final StreamManager manager;

	private final JFormattedTextField closeDialogTime =
		new JFormattedTextField(new DecimalFormat("#####"));
	private final JTextField closeDialogTitle = new JTextField();
	private final JTextField closeDialogNotes = new JTextField();

	public ClientConf3Tab(Frame parent, StreamManager manager) {
		super();
		this.frameParent = parent;
		this.manager = manager;
		init();
	}

	private void init() {
		JPanel mainPanel = new JPanel(new GridLayout(0, 2));
		setOnlyMeMode(mainPanel);
		setCloseDialogTime(mainPanel);
		setCloseDialogTitle(mainPanel);
		setCloseDialogNotes(mainPanel);

		JPanel scPanel = new JPanel(new BorderLayout());
		scPanel.add(mainPanel, BorderLayout.NORTH);
		this.setViewportView(scPanel);
	}

	private void setOnlyMeMode(JPanel mainPanel) {
		JLabel label = new JLabel("クライアント二重起動防止を行う：");
		label.setToolTipText("クライアント二重起動防止を行う");
		mainPanel.add(label);
		JComboBox cb = new JComboBox(new String[] { "行わない", "行う", });
		cb.setToolTipText("クライアント二重起動防止を行う");
		if ("false".equals(manager.getClientConf(
			"org.F11.scada.xwife.applet.isOnlyMeMode",
			"false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("行わない".equals(e.getItem())) {
						manager.setClientConf(
							"org.F11.scada.xwife.applet.isOnlyMeMode",
							"false");
					} else {
						manager.setClientConf(
							"org.F11.scada.xwife.applet.isOnlyMeMode",
							"true");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void setCloseDialogTime(JPanel mainPanel) {
		JLabel label = new JLabel("二重起動ダイアログ表示秒：");
		label.setToolTipText("二重起動ダイアログの表示時間を秒で設定します");
		mainPanel.add(label);
		JPanel panel = new JPanel(new GridLayout(1, 0));
		closeDialogTime.setText(manager.getClientConf(
			"org.F11.scada.xwife.applet.OnlyMeDialog.max",
			"60"));
		closeDialogTime.setInputVerifier(new NumberVerifier("数値"));
		closeDialogTime.setFocusLostBehavior(JFormattedTextField.COMMIT);
		closeDialogTime.getDocument().addDocumentListener(this);
		closeDialogTime.setToolTipText("二重起動ダイアログの表示時間を秒で設定します");
		panel.add(closeDialogTime);
		mainPanel.add(panel);
	}

	private void setCloseDialogTitle(JPanel mainPanel) {
		JLabel label = new JLabel("二重起動ダイアログタイトル：");
		label.setToolTipText("二重起動ダイアログタイトルを設定します");
		mainPanel.add(label);
		JPanel panel = new JPanel(new GridLayout(1, 0));
		closeDialogTitle.setText(manager.getClientConf(
			"org.F11.scada.xwife.applet.OnlyMeDialog.title",
			"クライアント二重起動"));
		closeDialogTitle.getDocument().addDocumentListener(this);
		closeDialogTitle.setToolTipText("二重起動ダイアログタイトルを設定します");
		panel.add(closeDialogTitle);
		mainPanel.add(panel);
	}

	private void setCloseDialogNotes(JPanel mainPanel) {
		JLabel label = new JLabel("二重起動ダイアログ文言：");
		label.setToolTipText("二重起動ダイアログ文言を設定します");
		mainPanel.add(label);
		JPanel panel = new JPanel(new GridLayout(1, 0));
		closeDialogNotes.setText(manager.getClientConf(
			"org.F11.scada.xwife.applet.OnlyMeDialog.notes",
			"既にクライアントが起動しています。"));
		closeDialogNotes.getDocument().addDocumentListener(this);
		closeDialogNotes.setToolTipText("二重起動ダイアログ文言を設定します");
		panel.add(closeDialogNotes);
		mainPanel.add(panel);
	}

	public void changedUpdate(DocumentEvent e) {
		eventPaformed(e);
	}

	public void insertUpdate(DocumentEvent e) {
		eventPaformed(e);
	}

	public void removeUpdate(DocumentEvent e) {
		eventPaformed(e);
	}

	private void eventPaformed(DocumentEvent e) {
		if (e.getDocument() == closeDialogTime.getDocument()) {
			manager.setClientConf(
				"org.F11.scada.xwife.applet.OnlyMeDialog.max",
				closeDialogTime.getText());
		} else if (e.getDocument() == closeDialogTitle.getDocument()) {
			manager.setClientConf(
				"org.F11.scada.xwife.applet.OnlyMeDialog.title",
				closeDialogTitle.getText());
		} else if (e.getDocument() == closeDialogNotes.getDocument()) {
			manager.setClientConf(
				"org.F11.scada.xwife.applet.OnlyMeDialog.notes",
				closeDialogNotes.getText());
		}
	}

	private static class NumberVerifier extends InputVerifier {
		private final String format;

		public NumberVerifier(String format) {
			this.format = format;
		}

		@Override
		public boolean verify(JComponent input) {
			if (input instanceof JFormattedTextField) {
				JFormattedTextField ftf = (JFormattedTextField) input;
				AbstractFormatter formatter = ftf.getFormatter();
				if (formatter != null) {
					String text = ftf.getText();
					try {
						formatter.stringToValue(text);
						return true;
					} catch (ParseException pe) {
						JOptionPane.showMessageDialog(ftf, format
							+ "形式で入力してください。");
						return false;
					}
				}
			}
			return true;
		}

		@Override
		public boolean shouldYieldFocus(JComponent input) {
			return verify(input);
		}
	}
}
