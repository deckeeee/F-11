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

package org.F11.scada.applet.ngraph.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import org.F11.scada.applet.ngraph.editor.component.Button;
import org.F11.scada.applet.ngraph.editor.component.Colleague;
import org.F11.scada.applet.ngraph.editor.component.GroupTableModel;
import org.F11.scada.applet.ngraph.editor.component.Label;
import org.F11.scada.applet.ngraph.editor.component.Mediator;
import org.F11.scada.applet.ngraph.editor.component.UnitTableModel;
import org.F11.scada.applet.ngraph.editor.service.UnitSearchService;
import org.F11.scada.applet.ngraph.editor.service.UnitSearchServiceImpl;
import org.F11.scada.util.ComponentUtil;
import org.F11.scada.util.TableUtil;
import org.apache.log4j.Logger;

public class EditorMainPanel extends JDialog implements Mediator {
	private final Logger logger = Logger.getLogger(EditorMainPanel.class);
	private GroupTableModel groupTableModel;
	private JTable groupTable;
	private Button createButton = new Button("新規作成", this);
	private Button renameButton = new Button("名称変更", this);
	private Button groupDeleteButton = new Button("削除", this);
	private Button editButton = new Button("編集", this);
	private Button cancelButton = new Button("ｷｬﾝｾﾙ", this);
	private Button updateButton = new Button("更新", this);
	private Button deleteButton = new Button("削除", this);
	private Button searchButton = new Button("検索", this);
	private Button insertButton = new Button("登録", this);
	private UnitTableModel unitTableModel;
	private JTable unitTable;
	private JTextField searchUnit = new JTextField(25);
	private JTextField searchMark = new JTextField(6);
	private JTextField searchName = new JTextField(35);
	private Label groupNoLabel;
	private Label groupLabel;
	private final UnitSearchService searchService;
	private final UnitTableModel searchTableModel;
	private JTable searchTable;

	public EditorMainPanel(Frame frame) {
		super(frame, "トレンドグラフ操作", true);
		searchTableModel = new UnitTableModel();
		searchService = new UnitSearchServiceImpl();
		add(getTabPane(), BorderLayout.CENTER);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		init();
		setSize(1030, 850);
	}

	private void init() {
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Button b = (Button) e.getSource();
				b.performMediator();
			}
		});
		cancelButton.setEnabled(false);
		cancelButton.setMargin(new Insets(2, 10, 2, 10));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Button b = (Button) e.getSource();
				b.performMediator();
			}
		});
		updateButton.setEnabled(false);
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Button b = (Button) e.getSource();
				b.performMediator();
			}
		});
		deleteButton.setEnabled(false);
		searchButton.setEnabled(false);
		insertButton.setEnabled(false);
		unitTable.setEnabled(false);
		searchUnit.setEnabled(false);
		searchMark.setEnabled(false);
		searchName.setEnabled(false);
		searchTable.setEnabled(false);
		createButton.setMargin(new Insets(2, 2, 2, 2));
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String groupName =
					JOptionPane.showInputDialog("グループ名を入力してください");
				if (null != groupName && !"".equals(groupName)) {
					GroupData gd =
						new GroupData(
							groupTableModel.getRowCount(),
							groupName,
							new ArrayList<UnitData>());
					groupTableModel.insertRow(gd);
				}
			}
		});
		renameButton.setMargin(new Insets(2, 2, 2, 2));
		renameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = groupTable.getSelectedRow();
				if (0 <= row) {
					GroupData gd = groupTableModel.getGroupData(row);
					String groupName =
						JOptionPane.showInputDialog("グループ名を入力してください", gd
							.getGroupName());
					if (null != groupName) {
						gd.setGroupName(groupName);
						groupTableModel.updateRow(row);
						groupLabel.setText(groupName);
					}
				}
			}
		});
		groupDeleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = groupTable.getSelectedRow();
				if (0 <= selectedRow) {
					groupTableModel.removeRow(selectedRow);
				}
			}
		});
	}

	private Component getTabPane() {
		JTabbedPane tab = new JTabbedPane();
		tab.addTab("グループ編集", getGroupEdit());
		return tab;
	}

	private Component getGroupEdit() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getWest(), BorderLayout.WEST);
		panel.add(getCenter(), BorderLayout.CENTER);
		panel.add(getSouth(), BorderLayout.SOUTH);
		return panel;
	}

	private Component getWest() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
		panel.setPreferredSize(new Dimension(350, 180));
		panel.add(getWestNorth(), BorderLayout.NORTH);
		panel.add(getWestCenter(), BorderLayout.CENTER);
		return panel;
	}

	private Component getWestNorth() {
		Box box = Box.createHorizontalBox();
		box.add(createButton);
		box.add(renameButton);
		box.add(groupDeleteButton);
		box.add(Box.createHorizontalGlue());
		box.add(editButton);
		return box;
	}

	private Component getWestCenter() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getTable(), BorderLayout.CENTER);
		return panel;
	}

	private Component getTable() {
		groupTableModel = new GroupTableModel();
		groupTable = new JTable(groupTableModel);
		groupTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableUtil.setColumnWidth(groupTable, 0, 50);
		return new JScrollPane(groupTable);
	}

	private Component getCenter() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));
		panel.add(getCenterNorth(), BorderLayout.NORTH);
		panel.add(getCenterCenter(), BorderLayout.CENTER);
		return panel;
	}

	private Component getCenterNorth() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(300, 187));
		panel.setBorder(BorderFactory.createTitledBorder("編集グループ"));
		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		panel2.add(getCenterNorthNorth(), BorderLayout.NORTH);
		panel2.add(getCenterNorthCenter(), BorderLayout.CENTER);
		panel2.add(getCenterNorthEast(), BorderLayout.EAST);
		panel.add(panel2);
		return panel;
	}

	private Component getCenterNorthNorth() {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 2));
		box.add(new JLabel("名称："));
		groupNoLabel = getGroupNoLabel();
		box.add(groupNoLabel);
		box.add(Box.createHorizontalStrut(5));
		groupLabel = getGroupLabel();
		box.add(groupLabel);
		box.add(Box.createHorizontalStrut(10));
		box.add(updateButton);
		box.add(cancelButton);
		box.add(Box.createHorizontalStrut(70));
		return box;
	}

	private Label getGroupNoLabel() {
		Label groupNoLabel = new Label(true);
		groupNoLabel.setMaximumSize(new Dimension(50, 20));
		groupNoLabel.setBackground(Color.white);
		groupNoLabel.setOpaque(true);
		groupNoLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		return groupNoLabel;
	}

	private Label getGroupLabel() {
		Label groupLabel = new Label(false);
		groupLabel.setMaximumSize(new Dimension(330, 20));
		groupLabel.setBackground(Color.white);
		groupLabel.setOpaque(true);
		groupLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		return groupLabel;
	}

	private Component getCenterNorthCenter() {
		unitTableModel = new UnitTableModel();
		addListeners(unitTableModel);
		unitTable = new JTable(unitTableModel);
		unitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumn column =
			unitTable.getColumn(unitTable.getModel().getColumnName(0));
		column.setCellRenderer(TableUtil.getColorTableCellRenderer());
		TableUtil.setColumnWidth(unitTable, 0, 10);
		TableUtil.setColumnWidth(unitTable, 1, 120);
		TableUtil.setColumnWidth(unitTable, 3, 60);
		return new JScrollPane(unitTable);
	}

	private void addListeners(UnitTableModel model) {
		groupTable.addMouseListener(model);
		groupTable.addKeyListener(model);
		groupTable.addMouseListener(groupNoLabel);
		groupTable.addKeyListener(groupNoLabel);
		groupTable.addMouseListener(groupLabel);
		groupTable.addKeyListener(groupLabel);
	}

	private Component getCenterCenter() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("登録機器選択"));
		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		panel2.add(getCenterCenterCenter(), BorderLayout.CENTER);
		panel2.add(getCenterCenterEast(), BorderLayout.EAST);
		panel.add(panel2, BorderLayout.CENTER);
		return panel;
	}

	private Component getCenterCenterCenterNorth() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("検索条件"));
		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
		panel2.add(getCenterCenterNorthCenter(), BorderLayout.CENTER);
		panel2.add(getCenterCenterNorthEast(), BorderLayout.EAST);
		panel.add(panel2, BorderLayout.CENTER);
		return panel;
	}

	private Component getCenterCenterNorthCenter() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		panel.add(new JLabel("記号："), c);
		panel.add(searchUnit, c);
		panel.add(new JLabel("単位："), c);
		panel.add(searchMark, c);
		c.gridy = 1;
		panel.add(new JLabel("機器名称："), c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(searchName, c);
		return panel;
	}

	private Component getCenterCenterNorthEast() {
		Box box = Box.createVerticalBox();
		box.add(Box.createVerticalGlue());
		box.add(searchButton);
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UnitData ud = new UnitData();
				ud.setUnitNo(searchUnit.getText());
				ud.setUnitName(searchName.getText());
				ud.setUnitMark(searchMark.getText());
				searchTableModel.setValueAt(searchService.getUnitDataList(ud));
			}
		});
		return box;
	}

	private Component getCenterCenterCenter() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		panel.add(getCenterCenterCenterNorth(), BorderLayout.NORTH);
		panel.add(getCenterCenterCenterCenter(), BorderLayout.CENTER);
		return panel;
	}

	private Component getCenterCenterCenterCenter() {
		searchTable = new JTable(searchTableModel);
		searchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableUtil.setColumnWidth(searchTable, 1, 120);
		TableUtil.setColumnWidth(searchTable, 3, 60);
		TableUtil.removeColumn(searchTable, 0);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		panel.add(new JScrollPane(searchTable));
		return panel;
	}

	private Component getCenterCenterEast() {
		Box box = Box.createVerticalBox();
		box.add(Box.createVerticalStrut(85));
		insertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = searchTable.getSelectedRow();
				if (0 <= selectedRow) {
					UnitData data = searchTableModel.getRow(selectedRow);
					unitTableModel.insertRow(data);
				}
			}
		});
		box.add(insertButton);
		return box;
	}

	private Component getCenterNorthEast() {
		Box box = Box.createVerticalBox();
		box.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = unitTable.getSelectedRow();
				if (0 <= selectedRow) {
					unitTableModel.removeRow(selectedRow);
				}
			}
		});
		box.add(deleteButton);
		return box;
	}

	private Component getSouth() {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 13));
		box.add(Box.createHorizontalGlue());
		JButton button = new JButton("閉じる");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton) e.getSource();
				JDialog dialog =
					ComponentUtil.getAncestorOfClass(JDialog.class, button);
				dialog.dispose();
			}
		});
		box.add(button);
		return box;
	}

	public void colleaguChanged(Colleague colleague) {
		if (0 <= groupTable.getSelectedRow()) {
			if (colleague == editButton) {
				createButton.setEnabled(false);
				renameButton.setEnabled(false);
				groupDeleteButton.setEnabled(false);
				editButton.setEnabled(false);
				groupTable.setEnabled(false);
				cancelButton.setEnabled(true);
				updateButton.setEnabled(true);
				deleteButton.setEnabled(true);
				searchButton.setEnabled(true);
				insertButton.setEnabled(true);
				unitTable.setEnabled(true);
				searchUnit.setEnabled(true);
				searchMark.setEnabled(true);
				searchName.setEnabled(true);
				searchTable.setEnabled(true);
			} else if (colleague == cancelButton) {
				unitTableModel.undo();
				changeGroupMode();
			} else if (colleague == updateButton) {
				unitTableModel.commit();
				changeGroupMode();
			}
		}
	}

	private void changeGroupMode() {
		createButton.setEnabled(true);
		renameButton.setEnabled(true);
		groupDeleteButton.setEnabled(true);
		editButton.setEnabled(true);
		groupTable.setEnabled(true);
		cancelButton.setEnabled(false);
		updateButton.setEnabled(false);
		deleteButton.setEnabled(false);
		searchButton.setEnabled(false);
		insertButton.setEnabled(false);
		unitTable.setEnabled(false);
		searchUnit.setEnabled(false);
		searchMark.setEnabled(false);
		searchName.setEnabled(false);
		searchTable.setEnabled(false);
	}

	public static void main(String[] args) {
		final JFrame f = new JFrame("トレンドグラフ操作");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JButton b = new JButton("トレンドグラフ操作");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EditorMainPanel(f).setVisible(true);
			}
		});
		f.add(b);
		f.pack();
		f.setVisible(true);
	}
}
