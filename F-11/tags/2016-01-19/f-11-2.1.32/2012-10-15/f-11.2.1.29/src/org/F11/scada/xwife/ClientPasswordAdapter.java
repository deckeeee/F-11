/*
 * =============================================================================
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
 *
 */

package org.F11.scada.xwife;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import org.F11.scada.util.ComponentUtil;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.apache.commons.configuration.Configuration;

public class ClientPasswordAdapter extends WindowAdapter {
	private final JFrame parent;
	private final AbstractWifeApplet applet;

	public ClientPasswordAdapter(JFrame parent, AbstractWifeApplet applet) {
		this.parent = parent;
		this.applet = applet;
	}

	public void windowClosing(WindowEvent e) {
		ClientCloseDialog f = new ClientCloseDialog(parent, applet);
		ComponentUtil.setCenter(JFrame.class, f);
		f.setVisible(true);
		if (f.isAuthenticate()) {
			System.exit(0);
		}
	}

	static private class ClientCloseDialog extends JDialog {
		private static final long serialVersionUID = -6185465181806107486L;
		private boolean isAuthenticate;
		private AbstractWifeApplet _applet;
		private JPasswordField passwordField;

		public ClientCloseDialog(JFrame parent, AbstractWifeApplet applet) {
			super(parent, true);
			_applet = applet;
			setTitle("終了認証");
			setResizable(false);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			passwordField = new JPasswordField(9);
			final JButton button = new JButton("OK");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (null != _applet) {
						Configuration configuration =
							_applet.getConfiguration();
						String password =
							configuration.getString(
								"org.F11.scada.applet.dialog.password",
								"password");
						String pw = new String(passwordField.getPassword());
						isAuthenticate = password.equals(pw);
					} else {
						String pw = new String(passwordField.getPassword());
						isAuthenticate = "password".equals(pw);
					}
					if (!isAuthenticate) {
						JOptionPane.showMessageDialog(
							_applet,
							"ﾊﾟｽﾜｰﾄﾞ認証に失敗しました",
							"ﾊﾟｽﾜｰﾄﾞ認証に失敗",
							JOptionPane.ERROR_MESSAGE);
					}
					dispose();
				}
			});

			final JButton cancel = new JButton("ｷｬﾝｾﾙ");
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(1, 2));
			panel.add(new JLabel("Password:", SwingConstants.LEFT));
			panel.add(passwordField);

			Box box = Box.createHorizontalBox();
			box.add(Box.createHorizontalGlue());
			box.add(button);
			box.add(Box.createHorizontalStrut(3));
			box.add(cancel);

			JLabel l = new JLabel("終了するにはﾊﾟｽﾜｰﾄﾞ認証が必要です。");
			add(l, BorderLayout.NORTH);
			add(panel, BorderLayout.CENTER);
			add(box, BorderLayout.SOUTH);
			pack();
		}

		public boolean isAuthenticate() {
			return isAuthenticate;
		}
	}

	public static void main(String[] args) {
		ClientCloseDialog c = new ClientCloseDialog(null, null);
		c.setVisible(true);
	}
}
