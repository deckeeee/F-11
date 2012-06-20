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
package org.F11.scada.tool.conf.remove;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.F11.scada.tool.conf.StreamManager;
import org.F11.scada.tool.conf.io.RemoveDefineBean;
import org.apache.log4j.Logger;

public class RemoveTab extends JPanel {
	private static final long serialVersionUID = 5071166852043093353L;
	private static final Logger log = Logger.getLogger(RemoveTab.class);

	private final Frame frameParent;
	private final StreamManager manager;
	private final List countList;
	private final List secondList;
	private final JTable tableC;
	private final JTable tableT;

	public RemoveTab(Frame parent, StreamManager manager) {
		super(new GridLayout(0, 1));
		this.frameParent = parent;
		this.manager = manager;
		countList = manager.getCountRemoveList();
		secondList = manager.getSecondRemoveList();
		tableC = new JTable(new RemoveTableModel());
		tableT = new JTable(new RemoveTableModel());
		init();
	}

	private void init() {
		// �ۑ������w�胊�X�g
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("�����w��F"), BorderLayout.NORTH);
		fireTable();
		tableC.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sc = new JScrollPane(tableC);
		sc.setPreferredSize(new Dimension(0, 0));
		panel.add(sc, BorderLayout.CENTER);
		// �{�^��
		Box box = new Box(BoxLayout.Y_AXIS);
		JButton but = new JButton("�ǉ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_addC();
			}
		});
		box.add(but);
		but = new JButton("�폜");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_delC();
			}
		});
		box.add(but);
		but = new JButton("�ύX");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_editC();
			}
		});
		box.add(but);
		but = new JButton("���");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_upC();
			}
		});
		box.add(but);
		but = new JButton("����");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_downC();
			}
		});
		box.add(but);
		panel.add(box, BorderLayout.EAST);
		this.add(panel, BorderLayout.NORTH);

		// �ۑ��b���w�胊�X�g
		panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("�b���w��F"), BorderLayout.NORTH);
		fireTable();
		tableT.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sc = new JScrollPane(tableT);
		sc.setPreferredSize(new Dimension(0, 0));
		panel.add(sc, BorderLayout.CENTER);
		// �{�^��
		box = new Box(BoxLayout.Y_AXIS);
		but = new JButton("�ǉ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_addT();
			}
		});
		box.add(but);
		but = new JButton("�폜");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_delT();
			}
		});
		box.add(but);
		but = new JButton("�ύX");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_editT();
			}
		});
		box.add(but);
		but = new JButton("���");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_upT();
			}
		});
		box.add(but);
		but = new JButton("����");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_downT();
			}
		});
		box.add(but);
		panel.add(box, BorderLayout.EAST);
		this.add(panel, BorderLayout.SOUTH);
	}

	private void push_addC() {
		// �ǉ��_�C�A���O
		int row = tableC.getSelectedRow();
		if (row < 0) {
			row = tableC.getRowCount();
		}
		log.debug("push_addC " + row);
		RemoveDefineBean bean = RemoveDefineDialog.showRemoveDefineDialog(
				frameParent, "�ǉ�", new RemoveDefineBean());
		if (bean != null) {
			countList.add(bean);
			manager.setCountRemoveList(countList);
			fireTable();
		}

	}

	private void push_delC() {
		// �폜�_�C�A���O
		int row = tableC.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "�I�����Ă�������");
			return;
		}
		log.debug("push_delC " + row);
		if (JOptionPane.showConfirmDialog(frameParent, "�폜���܂��B��낵���ł����H",
				"�A�C�e���폜", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			countList.remove(row);
			manager.setCountRemoveList(countList);
			fireTable();
		}
	}

	private void push_editC() {
		// �ύX�_�C�A���O
		int row = tableC.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "�I�����Ă�������");
			return;
		}
		log.debug("push_editC " + row);
		RemoveDefineBean be = (RemoveDefineBean) countList.get(row);
		RemoveDefineBean bean = RemoveDefineDialog.showRemoveDefineDialog(
				frameParent, "�ύX", be);
		if (bean != null) {
			countList.set(row, bean);
			manager.setCountRemoveList(countList);
			fireTable();
		}

	}

	private void push_upC() {
		int row = tableC.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "�I�����Ă�������");
			return;
		}
		if (0 < row) {
			log.debug("push_upC " + row);
			Object tmp = countList.remove(row);
			row--;
			countList.add(row, tmp);
			manager.setCountRemoveList(countList);
			fireTable();
			tableC.setRowSelectionInterval(row, row);
		}
	}

	private void push_downC() {
		int row = tableC.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "�I�����Ă�������");
			return;
		}
		if (row < tableC.getRowCount() - 1) {
			log.debug("push_downC " + row);
			Object tmp = countList.remove(row);
			row++;
			countList.add(row, tmp);
			manager.setCountRemoveList(countList);
			fireTable();
			tableC.setRowSelectionInterval(row, row);
		}
	}

	private void push_addT() {
		// �ǉ��_�C�A���O
		int row = tableT.getSelectedRow();
		if (row < 0) {
			row = tableT.getRowCount();
		}
		log.debug("push_addT " + row);
		RemoveDefineBean bean = RemoveDefineDialog.showRemoveDefineDialog(
				frameParent, "�ǉ�", new RemoveDefineBean());
		if (bean != null) {
			secondList.add(bean);
			manager.setSecondRemoveList(secondList);
			fireTable();
		}

	}

	private void push_delT() {
		// �폜�_�C�A���O
		int row = tableT.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "�I�����Ă�������");
			return;
		}
		log.debug("push_delT " + row);
		if (JOptionPane.showConfirmDialog(frameParent, "�폜���܂��B��낵���ł����H",
				"�A�C�e���폜", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			secondList.remove(row);
			manager.setSecondRemoveList(secondList);
			fireTable();
		}
	}

	private void push_editT() {
		// �ύX�_�C�A���O
		int row = tableT.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "�I�����Ă�������");
			return;
		}
		log.debug("push_editT " + row);
		RemoveDefineBean be = (RemoveDefineBean) secondList.get(row);
		RemoveDefineBean bean = RemoveDefineDialog.showRemoveDefineDialog(
				frameParent, "�ύX", be);
		if (bean != null) {
			secondList.set(row, bean);
			manager.setSecondRemoveList(secondList);
			fireTable();
		}

	}

	private void push_upT() {
		int row = tableT.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "�I�����Ă�������");
			return;
		}
		if (0 < row) {
			log.debug("push_upT " + row);
			Object tmp = secondList.remove(row);
			row--;
			secondList.add(row, tmp);
			manager.setSecondRemoveList(secondList);
			fireTable();
			tableT.setRowSelectionInterval(row, row);
		}
	}

	private void push_downT() {
		int row = tableT.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "�I�����Ă�������");
			return;
		}
		if (row < tableT.getRowCount() - 1) {
			log.debug("push_downT " + row);
			Object tmp = secondList.remove(row);
			row++;
			secondList.add(row, tmp);
			manager.setSecondRemoveList(secondList);
			fireTable();
			tableT.setRowSelectionInterval(row, row);
		}
	}

	private void fireTable() {
		DefaultTableModel dataModel = (DefaultTableModel) tableC.getModel();
		while (0 < dataModel.getRowCount())
			dataModel.removeRow(dataModel.getRowCount() - 1);
		for (Iterator it = countList.iterator(); it.hasNext();) {
			RemoveDefineBean bean = (RemoveDefineBean) it.next();
			String day = "����";
			if (!bean.isDaily())
				day = "���� " + bean.getExecuteDay() + " ��";
			StringBuffer sb = new StringBuffer();
			sb.append(bean.getExecuteHour()).append(":");
			sb.append(bean.getExecuteMinute()).append(":");
			sb.append(bean.getExecuteSecond());
			dataModel.addRow(new String[]{bean.getTableName(),
					bean.getDateFieldName(),
					String.valueOf(bean.getRemoveValue()), day, sb.toString()});
		}
		dataModel.fireTableDataChanged();

		dataModel = (DefaultTableModel) tableT.getModel();
		while (0 < dataModel.getRowCount())
			dataModel.removeRow(dataModel.getRowCount() - 1);
		for (Iterator it = secondList.iterator(); it.hasNext();) {
			RemoveDefineBean bean = (RemoveDefineBean) it.next();
			String day = "����";
			if (!bean.isDaily())
				day = "���� " + bean.getExecuteDay() + " ��";
			StringBuffer sb = new StringBuffer();
			sb.append(bean.getExecuteHour()).append(":");
			sb.append(bean.getExecuteMinute()).append(":");
			sb.append(bean.getExecuteSecond());
			dataModel.addRow(new String[]{bean.getTableName(),
					bean.getDateFieldName(),
					String.valueOf(bean.getRemoveValue()), day, sb.toString()});
		}
		dataModel.fireTableDataChanged();
	}
}
