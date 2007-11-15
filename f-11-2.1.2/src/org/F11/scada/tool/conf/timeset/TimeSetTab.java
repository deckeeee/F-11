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
package org.F11.scada.tool.conf.timeset;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import org.F11.scada.tool.conf.StreamManager;
import org.F11.scada.tool.conf.io.TimeSetBean;
import org.apache.log4j.Logger;

public class TimeSetTab extends JPanel implements DocumentListener {
	private static final long serialVersionUID = 8338273548448721017L;
	private static final Logger log = Logger.getLogger(TimeSetTab.class);

	private final Frame frameParent;
	private final StreamManager manager;
	private final List beansList;
	private final JTable table;

	private final JTextField offsetTime = new JTextField();

	public TimeSetTab(Frame parent, StreamManager manager) {
		super(new BorderLayout());
		this.frameParent = parent;
		this.manager = manager;
		beansList = manager.getTimeSetBeansList();
		table = new JTable(new TimeSetListTableModel());
		init();
	}

	private void init() {
		JPanel northPanel = new JPanel();
		// ����
		setCombobox(northPanel);
		// offset
		setOffset(northPanel);
		setMillisecondMode(northPanel);
		
		add(northPanel, BorderLayout.NORTH);
		// �z���_���X�g
		fireTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sc = new JScrollPane(table);
		this.add(sc, BorderLayout.CENTER);
		// �{�^��
		Box box = new Box(BoxLayout.Y_AXIS);
		JButton but = new JButton("�ǉ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_add();
			}
		});
		box.add(but);
		but = new JButton("�폜");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_del();
			}
		});
		box.add(but);
		but = new JButton("�ύX");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_edit();
			}
		});
		box.add(but);
		but = new JButton("���");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_up();
			}
		});
		box.add(but);
		but = new JButton("����");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_down();
			}
		});
		box.add(but);
		this.add(box, BorderLayout.EAST);
	}

	private void setMillisecondMode(JPanel northPanel) {
		northPanel.add(new JLabel("�~���b���[�h�F"));
		JComboBox cb = new JComboBox(new String[]{"�ʏ�", "�~���b���[�h"});
		String schedule = manager.getTimeSet("milliOffsetMode", "false");
		if ("true".equals(schedule)) {
			cb.setSelectedItem("�~���b���[�h");
		} else {
			cb.setSelectedItem("�ʏ�");
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�ʏ�".equals(e.getItem())) {
						manager.setTimeSet("milliOffsetMode", "false");
					} else {
						manager.setTimeSet("milliOffsetMode", "true");
					}
				}
			}
		});
		northPanel.add(cb);
	}

	private void setOffset(JPanel northPanel) {
		northPanel.add(new JLabel("�I�t�Z�b�g�F"));
		offsetTime.setText(manager.getTimeSet("offset", ""));
		offsetTime.getDocument().addDocumentListener(this);
		northPanel.add(offsetTime);
	}

	private void setCombobox(JPanel northPanel) {
		northPanel.add(new JLabel("�X�P�W���[���F"));
		JComboBox cb = new JComboBox(new String[]{"���Ԋu", "10���Ԋu", "���Ԋu", "���Ԋu",
				"���Ԋu", "�N�Ԋu"});
		String schedule = manager.getTimeSet("schedule", "");
		if ("MINUTE".equals(schedule)) {
			cb.setSelectedItem("���Ԋu");
		} else if ("TENMINUTE".equals(schedule)) {
			cb.setSelectedItem("10���Ԋu");
		} else if ("HOUR".equals(schedule)) {
			cb.setSelectedItem("���Ԋu");
		} else if ("DAILY".equals(schedule)) {
			cb.setSelectedItem("���Ԋu");
		} else if ("MONTHLY".equals(schedule)) {
			cb.setSelectedItem("���Ԋu");
		} else if ("YEARLY".equals(schedule)) {
			cb.setSelectedItem("�N�Ԋu");
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("���Ԋu".equals(e.getItem())) {
						manager.setTimeSet("schedule", "MINUTE");
					} else if ("10���Ԋu".equals(e.getItem())) {
						manager.setTimeSet("schedule", "TENMINUTE");
					} else if ("���Ԋu".equals(e.getItem())) {
						manager.setTimeSet("schedule", "HOUR");
					} else if ("���Ԋu".equals(e.getItem())) {
						manager.setTimeSet("schedule", "DAILY");
					} else if ("���Ԋu".equals(e.getItem())) {
						manager.setTimeSet("schedule", "MONTHLY");
					} else if ("�N�Ԋu".equals(e.getItem())) {
						manager.setTimeSet("schedule", "YEARLY");
					}
				}
			}
		});
		northPanel.add(cb);
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
		if (e.getDocument() == offsetTime.getDocument()) {
			manager.setTimeSet("offset", offsetTime.getText());
		}

	}

	private void push_add() {
		// �ǉ��_�C�A���O
		int row = table.getSelectedRow();
		if (row < 0) {
			row = table.getRowCount();
		}
		log.debug("push_add " + row);
		TimeSetBean bean = TimeSetDialog.showClientDefineDialog(frameParent,
				"�ǉ�", new TimeSetBean());
		if (bean != null) {
			beansList.add(bean);
			manager.setTimeSetBeansList(beansList);
			fireTable();
		}

	}

	private void push_del() {
		// �폜�_�C�A���O
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "�I�����Ă�������");
			return;
		}
		log.debug("push_del " + row);
		if (JOptionPane.showConfirmDialog(frameParent, "�폜���܂��B��낵���ł����H",
				"�A�C�e���폜", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			beansList.remove(row);
			manager.setTimeSetBeansList(beansList);
			fireTable();
		}
	}

	private void push_edit() {
		// �ύX�_�C�A���O
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "�I�����Ă�������");
			return;
		}
		log.debug("push_edit " + row);
		TimeSetBean be = (TimeSetBean) beansList.get(row);
		TimeSetBean bean = TimeSetDialog.showClientDefineDialog(frameParent,
				"�ύX", be);
		if (bean != null) {
			beansList.set(row, bean);
			manager.setTimeSetBeansList(beansList);
			fireTable();
		}
	}

	private void push_up() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "�I�����Ă�������");
			return;
		}
		if (0 < row) {
			log.debug("push_up " + row);
			Object tmp = beansList.remove(row);
			row--;
			beansList.add(row, tmp);
			manager.setTimeSetBeansList(beansList);
			fireTable();
			table.setRowSelectionInterval(row, row);
		}
	}

	private void push_down() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "�I�����Ă�������");
			return;
		}
		if (row < table.getRowCount() - 1) {
			log.debug("push_down " + row);
			Object tmp = beansList.remove(row);
			row++;
			beansList.add(row, tmp);
			manager.setTimeSetBeansList(beansList);
			fireTable();
			table.setRowSelectionInterval(row, row);
		}
	}

	private void fireTable() {
		DefaultTableModel dataModel = (DefaultTableModel) table.getModel();
		while (0 < dataModel.getRowCount())
			dataModel.removeRow(dataModel.getRowCount() - 1);
		for (Iterator it = beansList.iterator(); it.hasNext();) {
			TimeSetBean bean = (TimeSetBean) it.next();
			dataModel.addRow(new String[]{bean.getKind(), bean.getProvider(),
					bean.getHolder()});
		}
		dataModel.fireTableDataChanged();
	}

}
