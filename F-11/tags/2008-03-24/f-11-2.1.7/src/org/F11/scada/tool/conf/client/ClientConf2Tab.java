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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.F11.scada.tool.conf.StreamManager;

public class ClientConf2Tab extends JScrollPane implements DocumentListener {
	private static final long serialVersionUID = -2517467156903094845L;

	private final Frame frameParent;
	private final StreamManager manager;

	private final JTextField operLimit = new JTextField();
	private final JTextField alarmTableHeight = new JTextField();

	public ClientConf2Tab(Frame parent, StreamManager manager) {
		super();
		this.frameParent = parent;
		this.manager = manager;
		init();
	}

	private void init() {
		JPanel mainPanel = new JPanel(new GridLayout(0, 2));

		// ���샍�O����1�y�[�W�̍s��
		mainPanel.add(new JLabel("���샍�O�����\���s���F"));
		operLimit.setText(manager.getClientConf("operation.limit", "20"));
		operLimit.getDocument().addDocumentListener(this);
		mainPanel.add(operLimit);
		// ���샍�O���x��ꗗ�Ɋ܂߂邩�ǂ���
		mainPanel.add(new JLabel("���샍�O���x��ꗗ�Ɋ܂߂�F"));
		JComboBox cb = new JComboBox(new String[]{"����", "���Ȃ�"});
		if ("true".equals(manager.getClientConf("operationlogging.addalarm",
				"true"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
						manager.setClientConf("operationlogging.addalarm",
								"true");
					} else {
						manager.setClientConf("operationlogging.addalarm",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		// �^�C�vC�̌x��ꗗ���̍���
		mainPanel.add(new JLabel("�^�C�vC�x��ꗗ�� �����F"));
		alarmTableHeight.setText(manager.getClientConf(
				"xwife.applet.Applet.alarm.table.height", "180"));
		alarmTableHeight.getDocument().addDocumentListener(this);
		mainPanel.add(alarmTableHeight);
		// �^�C�vC�̌x��ꗗ���̗���\�����[�h (�����l��0) 0:�T�}���E�q�X�g���E������\��(�^�u�L��) 1:�����݂̂�\��(�^�u����)
		mainPanel.add(new JLabel("�^�C�vC�x��ꗗ�� ����\���F"));
		cb = new JComboBox(new String[]{"�^�u�L��", "�^�u����"});
		if ("0".equals(manager.getClientConf(
				"xwife.applet.Applet.alarm.table.type", "0"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�^�u�L��".equals(e.getItem())) {
						manager.setClientConf(
								"xwife.applet.Applet.alarm.table.type", "0");
					} else {
						manager.setClientConf(
								"xwife.applet.Applet.alarm.table.type", "1");
					}
				}
			}
		});
		mainPanel.add(cb);
		// �x�񗚗������̏����\���͈�(���P��) (�����l��1���O)
		// �����l�őI�����鍀�ڂƍ��ږ����c�_(|)��؂ŋL�q
		// �����l�őI�����郉�W�I�{�^����ݒ�
		// �����F�S��-SELECTALL �����E�^�]�̂�-SELECTTRUE �����E��~�̂�-SELECTFALSE
		// �m�F(�q�X�g���̂�)�F�S��-SELECTALL �m�F�ς�-SELECTTRUE ���m�F-SELECTFALSE
		mainPanel.add(new JLabel("�x�񗚗����������\���F"));
		JButton but = new JButton("�ڍ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// �x�񗚗������̏����\���ݒ�_�C�A���O
				new AlarmSearchDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
		// ���샍�O�����f�W�^���l�̕\��/��\��
		mainPanel.add(new JLabel("���샍�O�����f�W�^���l�\���F"));
		cb = new JComboBox(new String[]{"����", "���Ȃ�"});
		if ("true".equals(manager.getClientConf("operation.isDisplayDigital",
				"true"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
						manager.setClientConf("operation.isDisplayDigital",
								"true");
					} else {
						manager.setClientConf("operation.isDisplayDigital",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		// �������ꗗ�̕\��/��\��
		mainPanel.add(new JLabel("�������ꗗ�\���F"));
		cb = new JComboBox(new String[]{"����", "���Ȃ�"});
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.alarm.occurrence", "false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.occurrence",
								"true");
					} else {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.occurrence",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		// ���m�F�ꗗ�̕\��/��\��
		mainPanel.add(new JLabel("���m�F�ꗗ�\���F"));
		cb = new JComboBox(new String[]{"����", "���Ȃ�"});
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.alarm.noncheck", "false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.noncheck",
								"true");
					} else {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.noncheck",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		setNewAlarmCondition(mainPanel);
		setUseNewInfoMode(mainPanel);
		
		JPanel scPanel = new JPanel(new BorderLayout());
		scPanel.add(mainPanel, BorderLayout.NORTH);
		this.setViewportView(scPanel);
	}

	private void setNewAlarmCondition(JPanel mainPanel) {
		mainPanel.add(new JLabel("�x��ꗗ���������\���F"));
		JComboBox cb = new JComboBox(new String[]{"��", "�V"});
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.newalarm", "false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("��".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.newalarm",
								"false");
					} else {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.newalarm",
								"true");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void setUseNewInfoMode(JPanel mainPanel) {
		mainPanel.add(new JLabel("�ŐV�x��\�����[�h�F"));
		JComboBox cb = new JComboBox(new String[]{"��ɕ\��(��)", "���[�h�L��"});
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.alarm.AlarmStats.isUseNewInfoMode", "false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("��ɕ\��(��)".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.AlarmStats.isUseNewInfoMode",
								"false");
					} else {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.AlarmStats.isUseNewInfoMode",
								"true");
					}
				}
			}
		});
		mainPanel.add(cb);
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
		if (e.getDocument() == operLimit.getDocument()) {
			manager.setClientConf("operation.limit", operLimit.getText());
		} else if (e.getDocument() == alarmTableHeight.getDocument()) {
			manager.setClientConf("xwife.applet.Applet.alarm.table.height",
					alarmTableHeight.getText());
		}
	}

}
