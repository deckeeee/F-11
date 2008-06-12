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

import static org.F11.scada.util.ComponentUtil.HTML_END;
import static org.F11.scada.util.ComponentUtil.HTML_START;
import static org.F11.scada.util.ComponentUtil.addLabel;
import static org.F11.scada.util.ComponentUtil.addTextArea;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.F11.scada.tool.conf.StreamManager;

public class ScreenShot extends JDialog {
	private static final long serialVersionUID = 1390763118325194936L;
	private static final String VISIBLE =
		"org.F11.scada.xwife.applet.isShowScreenShot";
	private static final String PATH_NAME =
		"org.F11.scada.xwife.applet.comp.ScreenShotAction.savePathName";
	private static final String SHOT_KEY =
		"org.F11.scada.xwife.applet.comp.screenShotKey";
	private final StreamManager manager;
	private final JTextField fkey = new JTextField(6);
	private final JTextField path = new JTextField(12);

	public ScreenShot(Frame frameParent, StreamManager manager) {
		super(frameParent, "スクリーンショット", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		panel.add(getCenter(), BorderLayout.CENTER);
		panel.add(getSouth(), BorderLayout.SOUTH);
		add(panel);
		pack();
		setLocationRelativeTo(frameParent);
	}

	private Component getCenter() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("スクリーンショット設定"));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		setVisible(c, panel);
		setFkey(c, panel);
		setPath(c, panel);
		return panel;
	}

	private void setVisible(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("スクリーンショット機能の有無：");
		addLabel(c, panel, label);
		JComboBox cb = new JComboBox(new String[] { "有り", "無し" });
		if ("true".equals(manager.getClientConf(VISIBLE, "false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("有り".equals(e.getItem())) {
						manager.setClientConf(VISIBLE, "true");
					} else {
						manager.setClientConf(VISIBLE, "false");
					}
				}
			}
		});
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(cb, c);
	}

	private void setFkey(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("ショートカットキー：");
		addLabel(c, panel, label);
		fkey.setToolTipText(HTML_START
			+ "スクリーンショットのショートカットキーを設定します。<br>"
			+ "ショートカットキーを設定しない場合は空文字を設定。"
			+ HTML_END);
		addTextArea(c, panel, fkey, manager.getClientConf(SHOT_KEY, "F11"));
	}

	private void setPath(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("ファイル保存フォルダ：");
		addLabel(c, panel, label);
		path.setToolTipText(HTML_START
			+ "スクリーンショットのファイルを保存するフォルダを指定します。<br>"
			+ "パスの区切り文字は\"/\"を使用してください。<br>"
			+ "初期値の\".\"を指定すると、物件フォルダ(通常起動)又はデスクトップ(JWS起動)となります。"
			+ HTML_END);
		addTextArea(c, panel, path, manager.getClientConf(PATH_NAME, "."));
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
				manager.setClientConf(SHOT_KEY, fkey.getText());
				manager.setClientConf(PATH_NAME, path.getText());
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
