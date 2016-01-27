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
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.F11.scada.tool.conf.TimeSetManager;
import org.F11.scada.tool.conf.io.TimeSetBean;
import org.apache.log4j.Logger;

public class TimeSetTab extends JPanel {
	private static final long serialVersionUID = 8338273548448721017L;
	private static final Logger log = Logger.getLogger(TimeSetTab.class);

	private final Frame frameParent;
	private final TimeSetManager manager;
	private final List<TimeSetBean> beansList;
	private final JTable table;
	private final int parentRow;
	private final TimeSetTaskDialog timeSetTaskDialog;

	public TimeSetTab(
			Frame parent,
			TimeSetManager manager,
			int row,
			TimeSetTaskDialog timeSetTaskDialog) {
		super(new BorderLayout());
		this.frameParent = parent;
		this.manager = manager;
		parentRow = row;
		this.timeSetTaskDialog = timeSetTaskDialog;
		beansList =
			manager.getTimeSetBeansList(timeSetTaskDialog.getTaskName());
		table = new JTable(new TimeSetListTableModel());
		init();
	}

	private void init() {
		JPanel northPanel = new JPanel();

		add(northPanel, BorderLayout.NORTH);
		// ホルダリスト
		fireTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sc = new JScrollPane(table);
		this.add(sc, BorderLayout.CENTER);
		// ボタン
		Box box = new Box(BoxLayout.Y_AXIS);
		JButton but = new JButton("追加");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_add();
			}
		});
		box.add(but);
		but = new JButton("削除");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_del();
			}
		});
		box.add(but);
		but = new JButton("変更");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_edit();
			}
		});
		box.add(but);
		but = new JButton("上へ");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_up();
			}
		});
		box.add(but);
		but = new JButton("下へ");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_down();
			}
		});
		box.add(but);
		this.add(box, BorderLayout.EAST);
	}

	private void push_add() {
		// 追加ダイアログ
		int row = table.getSelectedRow();
		if (row < 0) {
			row = table.getRowCount();
		}
		log.debug("push_add " + row);
		String taskName = timeSetTaskDialog.getTaskName();
		if (null != taskName && !"".equals(taskName)) {
			if (0 > parentRow) {
				if (JOptionPane.showConfirmDialog(
					frameParent,
					"タスクを登録します。よろしいですか？",
					"タスクを登録",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					timeSetTaskDialog.insert(timeSetTaskDialog.getTaskName());
					setTimeSetBean();
				}
			} else {
				setTimeSetBean();
			}
		} else {
			JOptionPane.showMessageDialog(frameParent, "タスク名を入力してください");
			timeSetTaskDialog.nameFocus();
		}
	}

	private void setTimeSetBean() {
		TimeSetBean bean =
			TimeSetDialog.showClientDefineDialog(
				frameParent,
				"追加",
				new TimeSetBean());
		if (bean != null) {
			beansList.add(bean);
			manager.setTimeSetBeansList(
				timeSetTaskDialog.getTaskName(),
				beansList);
			fireTable();
		}
	}

	private void push_del() {
		// 削除ダイアログ
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "選択してください");
			return;
		}
		log.debug("push_del " + row);
		if (JOptionPane.showConfirmDialog(
			frameParent,
			"削除します。よろしいですか？",
			"アイテム削除",
			JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			beansList.remove(row);
			manager.setTimeSetBeansList(
				timeSetTaskDialog.getTaskName(),
				beansList);
			fireTable();
		}
	}

	private void push_edit() {
		// 変更ダイアログ
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "選択してください");
			return;
		}
		log.debug("push_edit " + row);
		TimeSetBean be = beansList.get(row);
		TimeSetBean bean =
			TimeSetDialog.showClientDefineDialog(frameParent, "変更", be);
		if (bean != null) {
			beansList.set(row, bean);
			manager.setTimeSetBeansList(
				timeSetTaskDialog.getTaskName(),
				beansList);
			fireTable();
		}
	}

	private void push_up() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "選択してください");
			return;
		}
		if (0 < row) {
			log.debug("push_up " + row);
			TimeSetBean bean = beansList.remove(row);
			row--;
			beansList.add(row, bean);
			manager.setTimeSetBeansList(
				timeSetTaskDialog.getTaskName(),
				beansList);
			fireTable();
			table.setRowSelectionInterval(row, row);
		}
	}

	private void push_down() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(frameParent, "選択してください");
			return;
		}
		if (row < table.getRowCount() - 1) {
			log.debug("push_down " + row);
			TimeSetBean bean = beansList.remove(row);
			row++;
			beansList.add(row, bean);
			manager.setTimeSetBeansList(
				timeSetTaskDialog.getTaskName(),
				beansList);
			fireTable();
			table.setRowSelectionInterval(row, row);
		}
	}

	private void fireTable() {
		DefaultTableModel dataModel = (DefaultTableModel) table.getModel();
		while (0 < dataModel.getRowCount())
			dataModel.removeRow(dataModel.getRowCount() - 1);
		for (TimeSetBean bean : beansList) {
			dataModel.addRow(new String[] {
				bean.getKind(),
				bean.getProvider(),
				bean.getHolder() });
		}
		dataModel.fireTableDataChanged();
	}
}
