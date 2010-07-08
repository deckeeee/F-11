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
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.tool.conf.StreamManager;

public class AlarmDefineTab extends JScrollPane implements DocumentListener {
	private static final long serialVersionUID = 198832965065236122L;

	private final Frame frameParent;
	private final StreamManager manager;

	private final JTextField newsFont = new JTextField();
	private final JFormattedTextField newsLines = new JFormattedTextField(
			new DecimalFormat("0"));
	private final JTextField tableFont = new JTextField();
	private final JFormattedTextField tableLines = new JFormattedTextField(
			new DecimalFormat("0"));
	private final JTextField servErrMessage = new JTextField();
	private final JTextField servErrSound = new JTextField();
	private final JTextField initPage = new JTextField();
	private final JTextField titleText = new JTextField();
	private final JTextField titleIcon = new JTextField();

	public AlarmDefineTab(Frame parent, StreamManager manager) {
		super();
		this.frameParent = parent;
		this.manager = manager;
		init();
	}

	private void init() {
		JPanel mainPanel = new JPanel(new GridLayout(0, 2));
		// �ŐV�x�� �w�i�F
		mainPanel.add(new JLabel("�ŐV�x�� �w�i�F�F"));
		JComboBox cb = new JComboBox(getColors());
		cb.setSelectedItem(manager.getAlarmDefine(
				"/alarm/news/backGround",
				"WHITE").toUpperCase());
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					manager.setAlarmDefine("/alarm/news/backGround", (String) e
							.getItem());
				}
			}
		});
		mainPanel.add(cb);
		// �ŐV�x�� �t�H���g
		mainPanel.add(new JLabel("�@�@�@�@ �t�H���g�F"));
		Box box = new Box(BoxLayout.X_AXIS);
		newsFont.setText(manager.getAlarmDefine(
				"/alarm/news/font/type",
				"Monospaced")
				+ ","
				+ manager.getAlarmDefine("/alarm/news/font/style", "PLAIN")
						.toUpperCase()
				+ ","
				+ manager.getAlarmDefine("/alarm/news/font/point", "18"));
		newsFont.setEditable(false);
		box.add(newsFont);
		JButton but = new JButton("Font");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FontDialog dlg = new FontDialog(frameParent);
				StringTokenizer st = new StringTokenizer(
						newsFont.getText(),
						",");
				String name = "", type = "", size = "";
				if (st.hasMoreTokens()) {
					name = st.nextToken();
					if (st.hasMoreTokens()) {
						type = st.nextToken();
						if (st.hasMoreTokens()) {
							size = st.nextToken();
						}
					}
				}
				if (dlg.showFontDialog(name, type, size)) {
					newsFont.setText(dlg.getFontName() + ","
							+ dlg.getFontStyle() + "," + dlg.getFontSize());
					manager.setAlarmDefine("/alarm/news/font/type", dlg
							.getFontName());
					manager.setAlarmDefine("/alarm/news/font/style", dlg
							.getFontStyle());
					manager.setAlarmDefine("/alarm/news/font/point", dlg
							.getFontSize());
				}
			}
		});
		box.add(but);
		mainPanel.add(box);
		// �ŐV�x�� �s��
		mainPanel.add(new JLabel("�@�@�@�@ �s���F"));
		newsLines.setText(manager.getAlarmDefine(
				"/alarm/news/linecount/value",
				"5"));
		newsLines.getDocument().addDocumentListener(this);
		mainPanel.add(newsLines);
		// �x�񗚗� �w�i�F
		mainPanel.add(new JLabel("�x�񗚗� �w�i�F�F"));
		cb = new JComboBox(getColors());
		cb.setSelectedItem(manager.getAlarmDefine(
				"/alarm/table/backGround",
				"WHITE").toUpperCase());
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					manager.setAlarmDefine(
							"/alarm/table/backGround",
							(String) e.getItem());
				}
			}
		});
		mainPanel.add(cb);
		// �x�񗚗� ���ږ��\���F
		mainPanel.add(new JLabel("�@�@�@�@ ���ږ��\���F�F"));

		cb = new JComboBox(getColors());
		cb.setSelectedItem(manager.getAlarmDefine(
				"/alarm/table/headerForeGround",
				"BLACK").toUpperCase());
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					manager.setAlarmDefine(
							"/alarm/table/headerForeGround",
							(String) e.getItem());
				}
			}
		});
		mainPanel.add(cb);
		// �x�񗚗� ���ږ��w�i�F
		mainPanel.add(new JLabel("�@�@�@�@ ���ږ��w�i�F�F"));
		cb = new JComboBox(getColors());
		cb.setSelectedItem(manager.getAlarmDefine(
				"/alarm/table/headerBackGround",
				"SILVER").toUpperCase());
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					manager.setAlarmDefine(
							"/alarm/table/headerBackGround",
							(String) e.getItem());
				}
			}
		});
		mainPanel.add(cb);
		// �x�񗚗� �^�u�ԍ�
		mainPanel.add(new JLabel("�@�@�@�@ �f�t�H���g�\���^�u�F"));
		cb = new JComboBox(new String[] { "�T�}��", "�q�X�g��", "����", "������", "���m�F" });
		String number = manager
				.getAlarmDefine("/alarm/table/defaultTabNo", "1");
		if (number.length() <= 0)
			number = "0";
		cb.setSelectedIndex(Integer.parseInt(number));
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					manager.setAlarmDefine("/alarm/table/defaultTabNo", String
							.valueOf(((JComboBox) e.getSource())
									.getSelectedIndex()));
				}
			}
		});
		mainPanel.add(cb);
		// �x�񗚗� �t�H���g
		mainPanel.add(new JLabel("�@�@�@�@ �t�H���g�F"));
		box = new Box(BoxLayout.X_AXIS);
		tableFont.setText(manager.getAlarmDefine(
				"/alarm/table/font/type",
				"Monospaced")
				+ ","
				+ manager.getAlarmDefine("/alarm/table/font/style", "PLAIN")
						.toUpperCase()
				+ ","
				+ manager.getAlarmDefine("/alarm/table/font/point", "14"));
		tableFont.setEditable(false);
		box.add(tableFont);
		but = new JButton("Font");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FontDialog dlg = new FontDialog(frameParent);
				StringTokenizer st = new StringTokenizer(
						tableFont.getText(),
						",");
				String name = "", type = "", size = "";
				if (st.hasMoreTokens()) {
					name = st.nextToken();
					if (st.hasMoreTokens()) {
						type = st.nextToken();
						if (st.hasMoreTokens()) {
							size = st.nextToken();
						}
					}
				}
				if (dlg.showFontDialog(name, type, size)) {
					tableFont.setText(dlg.getFontName() + ","
							+ dlg.getFontStyle() + "," + dlg.getFontSize());
					manager.setAlarmDefine("/alarm/table/font/type", dlg
							.getFontName());
					manager.setAlarmDefine("/alarm/table/font/style", dlg
							.getFontStyle());
					manager.setAlarmDefine("/alarm/table/font/point", dlg
							.getFontSize());
				}
			}
		});
		box.add(but);
		mainPanel.add(box);
		// �x�񗚗� �s��
		mainPanel.add(new JLabel("�@�@�@�@ �s���F"));
		tableLines.setText(manager.getAlarmDefine(
				"/alarm/table/linecount/value",
				"20"));
		tableLines.getDocument().addDocumentListener(this);
		mainPanel.add(tableLines);
		// �T�[�o�[�G���[ ���b�Z�[�W
		mainPanel.add(new JLabel("�T�[�o�[�G���[ ���b�Z�[�W�F"));
		servErrMessage.setText(manager.getAlarmDefine(
				"/alarm/server/message",
				"�T�[�o�[�R�l�N�V�����G���["));
		servErrMessage.getDocument().addDocumentListener(this);
		mainPanel.add(servErrMessage);
		// �T�[�o�[�G���[ �x��
		mainPanel.add(new JLabel("�@�@�@�@�@�@�@ �x�񉹁F"));
		servErrSound.setText(manager.getAlarmDefine("/alarm/server/sound", ""));
		servErrSound.getDocument().addDocumentListener(this);
		mainPanel.add(servErrSound);
		// ** Attribute ***
		// displayLogin true | false ... It is the existence of a login button
		// display to a tool bar at the time of an applet.
		mainPanel.add(new JLabel("Web�����O�C���{�^���\���F"));
		cb = new JComboBox(new String[] { "����", "���Ȃ�" });
		if ("true".equals(manager.getAlarmDefine(
				"/alarm/toolbar/displayLogin",
				"true"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
						manager.setAlarmDefine(
								"/alarm/toolbar/displayLogin",
								"true");
					} else {
						manager.setAlarmDefine(
								"/alarm/toolbar/displayLogin",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		// �����\���y�[�W��
		JLabel label = new JLabel("�����\���y�[�W���F");
		label.setToolTipText("tree�Ɏw�肪�Ȃ��ꍇ�B");
		mainPanel.add(label);
		initPage.setText(manager.getAlarmDefine("/alarm/init/initPage", ""));
		initPage.getDocument().addDocumentListener(this);
		mainPanel.add(initPage);
		// �N���C�A���g�^�C�g��
		mainPanel.add(new JLabel("�N���C�A���g�^�C�g���F"));
		titleText.setText(manager.getAlarmDefine("/alarm/title/text", "F-11"));
		titleText.getDocument().addDocumentListener(this);
		mainPanel.add(titleText);
		// �^�C�g���A�C�R��
		mainPanel.add(new JLabel("�^�C�g���A�C�R���F"));
		titleIcon.setText(manager.getAlarmDefine("/alarm/title/image", ""));
		titleIcon.getDocument().addDocumentListener(this);
		mainPanel.add(titleIcon);

		JPanel scPanel = new JPanel(new BorderLayout());
		scPanel.add(mainPanel, BorderLayout.NORTH);
		this.setViewportView(scPanel);
	}

	private Vector getColors() {
		return new Vector(ColorFactory.getColorNames());
	}

	public void changedUpdate(DocumentEvent e) {
		eventPeformed(e);
	}

	public void insertUpdate(DocumentEvent e) {
		eventPeformed(e);
	}

	public void removeUpdate(DocumentEvent e) {
		eventPeformed(e);
	}

	private void eventPeformed(DocumentEvent e) {
		if (e.getDocument() == newsLines.getDocument()) {
			manager.setAlarmDefine("/alarm/news/linecount/value", newsLines
					.getText());
		} else if (e.getDocument() == tableLines.getDocument()) {
			manager.setAlarmDefine("/alarm/table/linecount/value", tableLines
					.getText());
		} else if (e.getDocument() == servErrMessage.getDocument()) {
			manager.setAlarmDefine("/alarm/server/message", servErrMessage
					.getText());
		} else if (e.getDocument() == servErrSound.getDocument()) {
			manager.setAlarmDefine("/alarm/server/sound", servErrSound
					.getText());
		} else if (e.getDocument() == initPage.getDocument()) {
			manager.setAlarmDefine("/alarm/init/initPage", initPage.getText());
		} else if (e.getDocument() == titleText.getDocument()) {
			manager.setAlarmDefine("/alarm/title/text", titleText.getText());
		} else if (e.getDocument() == titleIcon.getDocument()) {
			manager.setAlarmDefine("/alarm/title/image", titleIcon.getText());
		}
	}

}
