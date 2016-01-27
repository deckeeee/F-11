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
package org.F11.scada.tool.conf.alarm;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class FontDialog extends JDialog {
	static final long serialVersionUID = 6728348474675941491L;
	private final JList fontNames = new JList(new String[]{"Serif",
			"SansSerif", "Monospaced", "Dialog", "DialogInput"});
	private final JList fontStyles = new JList(new String[]{"PLAIN", "BOLD",
			"ITALIC"});
	private final JList fontSizes = new JList(new String[]{"6", "7", "8", "9",
			"10", "11", "12", "13", "14", "15", "16", "18", "20", "24", "36",
			"48", "60", "72"});
	private boolean retcode = false;

	public FontDialog(Frame owner) throws HeadlessException {
		super(owner, "フォント選択", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public FontDialog(Dialog owner) throws HeadlessException {
		super(owner, "フォント選択", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public boolean showFontDialog(String fontName, String fontStyle,
			String fontSize) throws HeadlessException {
		init();
		fontNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fontNames.setSelectedValue(fontName, true);
		fontStyles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fontStyles.setSelectedValue(fontStyle.toUpperCase(), true);
		fontSizes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fontSizes.setSelectedValue(fontSize, true);
		setVisible(true);
		return retcode;
	}
	private void init() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel selPanel = new JPanel(new GridLayout(1, 3));
		// フォント名
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(new JLabel("名称"));
		JScrollPane panel = new JScrollPane(fontNames);
		box.add(panel);
		selPanel.add(box);
		// スタイル
		box = new Box(BoxLayout.Y_AXIS);
		box.add(new JLabel("スタイル"));
		panel = new JScrollPane(fontStyles);
		box.add(panel);
		selPanel.add(box);
		// ポイントサイズ
		box = new Box(BoxLayout.Y_AXIS);
		box.add(new JLabel("サイズ"));
		panel = new JScrollPane(fontSizes);
		box.add(panel);
		selPanel.add(box);

		mainPanel.add(selPanel, BorderLayout.CENTER);
		JPanel butPanel = new JPanel(new GridLayout(1, 2));
		JButton but = new JButton("ＯＫ");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				retcode = true;
				dispose();
			}
		});
		butPanel.add(but);
		but = new JButton("キャンセル");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				retcode = false;
				dispose();
			}
		});
		butPanel.add(but);
		mainPanel.add(butPanel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(getParent());
	}

	public String getFontName() {
		return (String) fontNames.getSelectedValue();
	}

	public String getFontSize() {
		return (String) fontSizes.getSelectedValue();
	}

	public String getFontStyle() {
		return (String) fontStyles.getSelectedValue();
	}

}
