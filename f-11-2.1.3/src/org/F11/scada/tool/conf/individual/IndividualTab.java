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
package org.F11.scada.tool.conf.individual;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
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

import org.F11.scada.tool.conf.StreamManager;
import org.F11.scada.tool.conf.io.ClientDefineBean;
import org.apache.log4j.Logger;

public class IndividualTab extends JPanel {
	private static final long serialVersionUID = 4385232561719232413L;
	private static final Logger log = Logger.getLogger(IndividualTab.class);

	private final Frame frameParent;
	private final StreamManager manager;
	private final List beansList;
	private final JTable table;

	public IndividualTab(Frame parent, StreamManager manager) {
		super(new BorderLayout());
		this.frameParent = parent;
		this.manager = manager;
		beansList = manager.getClientBeansList();
		table = new JTable(new ClientDefineTableModel());
		init();
	}

	private void init() {
		// クライアントリスト
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
		this.add(box, BorderLayout.EAST);
	}

	private void push_add() {
		// 追加ダイアログ
		int row = table.getSelectedRow();
		if (row < 0) {
			row = table.getRowCount();
		}
		log.debug("push_add " + row);
		ClientDefineBean bean = ClientDefineDialog.showClientDefineDialog(
				frameParent, "追加", new ClientDefineBean());
		if (bean != null) {
			beansList.add(bean);
			manager.setClientBeansList(beansList);
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
		if (JOptionPane.showConfirmDialog(frameParent, "削除します。よろしいですか？",
				"アイテム削除", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			beansList.remove(row);
			manager.setClientBeansList(beansList);
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
		ClientDefineBean be = (ClientDefineBean) beansList.get(row);
		ClientDefineBean bean = ClientDefineDialog.showClientDefineDialog(
				frameParent, "変更", be);
		if (bean != null) {
			beansList.set(row, bean);
			manager.setClientBeansList(beansList);
			fireTable();
		}
	}

	private void fireTable() {
		DefaultTableModel dataModel = (DefaultTableModel) table.getModel();
		while (0 < dataModel.getRowCount())
			dataModel.removeRow(dataModel.getRowCount() - 1);
		for (Iterator it = beansList.iterator(); it.hasNext();) {
			ClientDefineBean bean = (ClientDefineBean) it.next();
			dataModel.addRow(new String[]{bean.getIpAddress(),
					bean.getDefaultUserName()});
		}
		dataModel.fireTableDataChanged();
	}
}
