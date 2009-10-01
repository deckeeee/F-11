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
import java.io.StringReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ngraph.GraphProperties;
import org.F11.scada.applet.ngraph.editor.component.Button;
import org.F11.scada.applet.ngraph.editor.component.Colleague;
import org.F11.scada.applet.ngraph.editor.component.Label;
import org.F11.scada.applet.ngraph.editor.component.Mediator;
import org.F11.scada.applet.ngraph.editor.component.SeriesPropertyTableModel;
import org.F11.scada.applet.ngraph.editor.component.SeriesTableModel;
import org.F11.scada.applet.ngraph.editor.component.SpanDialog;
import org.F11.scada.applet.ngraph.editor.service.UnitSearchService;
import org.F11.scada.applet.ngraph.editor.service.UnitSearchServiceImpl;
import org.F11.scada.data.DataAccessable;
import org.F11.scada.server.invoke.TrendFileService;
import org.F11.scada.util.ComponentUtil;
import org.F11.scada.util.TableUtil;
import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;

/**
 * グラフ操作ダイアログ
 * 
 * @author maekawa
 * 
 */
public class EditorMainPanel extends JDialog implements Mediator {
	private static final long serialVersionUID = -1995919687610018906L;
	private final Logger logger = Logger.getLogger(EditorMainPanel.class);
	private static final int INPUT_HISTORY_MAX = 10;
	private DataAccessable accessable;
	private final GraphProperties graphProperties;
	private SeriesTableModel groupTableModel;
	private JTable groupTable;
	private Label groupNoLabel;
	private Label groupLabel;
	private SeriesPropertyTableModel unitTableModel;
	private JTable unitTable;
	private final UnitSearchService searchService;
	private final SeriesPropertyTableModel searchTableModel;
	private JTable searchTable;
	private Button groupCreateButton = new Button("新規作成", this);
	private Button groupRenameButton = new Button("名称変更", this);
	private Button groupDeleteButton = new Button("削除", this);
	private Button groupEditButton = new Button("編集", this);
	private Button unitCancelButton = new Button("ｷｬﾝｾﾙ", this);
	private Button unitUpdateButton = new Button("更新", this);
	private Button unitDeleteButton = new Button("削除", this);
	private Button unitSpanButton = new Button("ｽｹｰﾙ変更", this);
	private Button searchButton = new Button("検索", this);
	private Button searchInsertButton = new Button("登録", this);
	private JComboBox searchUnit = new JComboBox();
	private JComboBox searchMark = new JComboBox();
	private JComboBox searchName = new JComboBox();
	private final PageData page;

	public EditorMainPanel(Frame frame, GraphProperties graphProperties) {
		super(frame, "トレンドグラフ操作", true);
		this.graphProperties = graphProperties;
		String collectorServer = WifeUtilities.createRmiManagerDelegator();
		logger.debug("collectorServer : " + collectorServer);
		try {
			accessable = (DataAccessable) Naming.lookup(collectorServer);
		} catch (Exception e) {
			logger.error("サーバー接続エラー", e);
		}
		page = getPageData();
		searchTableModel =
			new SeriesPropertyTableModel(page.getTrend3Data().getSeriesColors());
		searchService = new UnitSearchServiceImpl(page);
		add(getTabPane(), BorderLayout.CENTER);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		init();
		setSize(1030, 850);
		ComponentUtil.setCenter(Frame.class, this);
	}

	private PageData getPageData() {
		PageData page = new PageData();
		Digester digester = new Digester();
		digester.addRuleSet(new TrendRuleSet());
		digester.push(page);
		try {
			String xml =
				(String) accessable.invoke("TrendFileService", new Object[] {
					TrendFileService.READ_OP,
					graphProperties.getPagefile(),
					"" });
			digester.parse(new StringReader(xml));
		} catch (Exception e) {
			logger.error("ファイルが無いか、サーバー接続エラーです。 : "
				+ graphProperties.getPagefile(), e);
		}
		return page;
	}

	private void init() {
		setGroupEditButton();
		setUnitCancelButton();
		setUnitUpdateButton();
		unitDeleteButton.setEnabled(false);
		unitSpanButton.setEnabled(false);
		searchButton.setEnabled(false);
		searchInsertButton.setEnabled(false);
		unitTable.setEnabled(false);
		searchUnit.setEnabled(false);
		searchMark.setEnabled(false);
		searchName.setEnabled(false);

		setSearchBox();

		searchTable.setEnabled(false);
		setGroupCreateButton();
		setGroupRenameButton();
		setGroupDeleteButton();
		setUnitDeleteButton();
		setUnitSpanButton();
	}

	private void setGroupEditButton() {
		groupEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Button b = (Button) e.getSource();
				b.performMediator();
			}
		});
	}

	private void setUnitCancelButton() {
		unitCancelButton.setEnabled(false);
		unitCancelButton.setMargin(new Insets(2, 10, 2, 10));
		unitCancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Button b = (Button) e.getSource();
				b.performMediator();
			}
		});
	}

	private void setUnitUpdateButton() {
		unitUpdateButton.setEnabled(false);
		unitUpdateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Button b = (Button) e.getSource();
				b.performMediator();
				writePage();
			}
		});
	}

	private void setSearchBox() {
		searchUnit.setEditable(true);
		searchUnit.setPreferredSize(new Dimension(250, 20));
		searchUnit.setMinimumSize(new Dimension(250, 20));
		searchUnit.setMaximumSize(new Dimension(250, 20));
		searchMark.setEditable(true);
		searchMark.setPreferredSize(new Dimension(80, 20));
		searchMark.setMinimumSize(new Dimension(80, 20));
		searchMark.setMaximumSize(new Dimension(80, 20));
		searchName.setEditable(true);
		searchName.setPreferredSize(new Dimension(390, 20));
		searchName.setMinimumSize(new Dimension(390, 20));
		searchName.setMaximumSize(new Dimension(390, 20));
	}

	private void setGroupCreateButton() {
		groupCreateButton.setMargin(new Insets(2, 2, 2, 2));
		groupCreateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String groupName =
					JOptionPane.showInputDialog("グループ名を入力してください");
				if (null != groupName && !"".equals(groupName)) {
					SeriesData sd =
						new SeriesData(
							groupTableModel.getRowCount() + 1,
							groupName,
							new ArrayList<SeriesPropertyData>());
					groupTableModel.insertRow(sd);
				}
			}
		});
	}

	private void setGroupRenameButton() {
		groupRenameButton.setMargin(new Insets(2, 2, 2, 2));
		groupRenameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = groupTable.getSelectedRow();
				if (0 <= row) {
					SeriesData sd = groupTableModel.getGroupData(row);
					String groupName =
						JOptionPane.showInputDialog("グループ名を入力してください", sd
							.getGroupName());
					if (null != groupName) {
						sd.setGroupName(groupName);
						groupTableModel.updateRow(row);
						groupLabel.setText(groupName);
						page
							.getTrend3Data()
							.getSeriesDatas()
							.get(row)
							.setGroupName(groupName);
						writePage();
					}
				}
			}
		});
	}

	private void setGroupDeleteButton() {
		groupDeleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = groupTable.getSelectedRow();
				if (0 <= selectedRow) {
					int rt =
						JOptionPane.showConfirmDialog(
							ComponentUtil.getAncestorOfClass(
								JDialog.class,
								groupDeleteButton),
							"グループを削除します。",
							"グループ削除",
							JOptionPane.OK_CANCEL_OPTION);
					if (rt == JOptionPane.OK_OPTION) {
						groupTableModel.removeRow(selectedRow);
						writePage();
					}
				}
			}
		});
	}

	private void setUnitDeleteButton() {
		unitDeleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = unitTable.getSelectedRow();
				if (0 <= selectedRow) {
					unitTableModel.removeRow(selectedRow);
				}
			}
		});
	}

	private void setUnitSpanButton() {
		unitSpanButton.setMargin(new Insets(2, 0, 2, 0));
		unitSpanButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = unitTable.getSelectedRow();
				if (0 <= selectedRow) {
					SeriesPropertyData spd =
						unitTableModel.getRow(unitTable.getSelectedRow());
					new SpanDialog(ComponentUtil.getAncestorOfClass(
						JDialog.class,
						unitSpanButton), spd).setVisible(true);
					unitTableModel.fireTableRowsUpdated(
						selectedRow,
						selectedRow);
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
		box.add(groupCreateButton);
		box.add(groupRenameButton);
		box.add(groupDeleteButton);
		box.add(Box.createHorizontalGlue());
		box.add(groupEditButton);
		return box;
	}

	private Component getWestCenter() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getTable(), BorderLayout.CENTER);
		return panel;
	}

	private Component getTable() {
		groupTableModel = new SeriesTableModel(page.getTrend3Data());
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
		box.add(unitUpdateButton);
		box.add(unitCancelButton);
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
		unitTableModel =
			new SeriesPropertyTableModel(page.getTrend3Data().getSeriesColors());
		addListeners(unitTableModel);
		unitTable = new JTable(unitTableModel);
		unitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumn column =
			unitTable.getColumn(unitTable.getModel().getColumnName(0));
		column.setCellRenderer(TableUtil.getColorTableCellRenderer());
		TableUtil.setColumnWidth(unitTable, 0, 10);
		TableUtil.setColumnWidth(unitTable, 1, 50);
		TableUtil.setColumnWidth(unitTable, 2, 50);
		TableUtil.setColumnWidth(unitTable, 3, 120);
		TableUtil.setColumnWidth(unitTable, 5, 60);
		return new JScrollPane(unitTable);
	}

	private void addListeners(SeriesPropertyTableModel model) {
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
		c.insets = new Insets(3, 3, 3, 3);
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
				SeriesPropertyData ud = new SeriesPropertyData();
				String searchUnitItem =
					(String) searchUnit.getEditor().getItem();
				ud.setUnit(searchUnitItem);
				String searchUnitNameItem =
					(String) searchName.getEditor().getItem();
				ud.setName(searchUnitNameItem);
				String searchUnitMarkItem =
					(String) searchMark.getEditor().getItem();
				ud.setMark(searchUnitMarkItem);
				searchTableModel.setValueAt(searchService
					.getSeriesPropertyDataList(ud));
				addItem(searchUnit, searchUnitItem);
				addItem(searchName, searchUnitNameItem);
				addItem(searchMark, searchUnitMarkItem);
			}

			private void addItem(JComboBox combo, String str) {
				if (str != null && str.length() != 0) {
					combo.setVisible(false);
					DefaultComboBoxModel model =
						(DefaultComboBoxModel) combo.getModel();
					model.removeElement(str);
					model.insertElementAt(str, 0);
					if (model.getSize() > INPUT_HISTORY_MAX) {
						model.removeElementAt(INPUT_HISTORY_MAX);
					}
					combo.setSelectedIndex(0);
					combo.setVisible(true);
				}
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
		TableUtil.setColumnWidth(searchTable, 3, 120);
		TableUtil.setColumnWidth(searchTable, 5, 60);
		TableUtil.removeColumn(searchTable, 0);
		TableUtil.removeColumn(searchTable, 0);
		TableUtil.removeColumn(searchTable, 0);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		panel.add(new JScrollPane(searchTable));
		return panel;
	}

	private Component getCenterCenterEast() {
		Box box = Box.createVerticalBox();
		box.add(Box.createVerticalStrut(85));
		searchInsertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = searchTable.getSelectedRow();
				if (0 <= selectedRow) {
					SeriesPropertyData data =
						searchTableModel.getRow(selectedRow);
					unitTableModel.insertRow(new SeriesPropertyData(data));
				}
			}
		});
		box.add(searchInsertButton);
		return box;
	}

	private Component getCenterNorthEast() {
		Box box = Box.createVerticalBox();
		box.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		box.add(unitDeleteButton);
		box.add(Box.createVerticalStrut(10));
		box.add(unitSpanButton);
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
			if (colleague == groupEditButton) {
				groupCreateButton.setEnabled(false);
				groupRenameButton.setEnabled(false);
				groupDeleteButton.setEnabled(false);
				groupEditButton.setEnabled(false);
				groupTable.setEnabled(false);
				unitCancelButton.setEnabled(true);
				unitUpdateButton.setEnabled(true);
				unitDeleteButton.setEnabled(true);
				unitSpanButton.setEnabled(true);
				searchButton.setEnabled(true);
				searchInsertButton.setEnabled(true);
				unitTable.setEnabled(true);
				searchUnit.setEnabled(true);
				searchMark.setEnabled(true);
				searchName.setEnabled(true);
				searchTable.setEnabled(true);
			} else if (colleague == unitCancelButton) {
				unitTableModel.undo();
				changeGroupMode();
			} else if (colleague == unitUpdateButton) {
				unitTableModel.commit();
				changeGroupMode();
			}
		}
	}

	private void changeGroupMode() {
		groupCreateButton.setEnabled(true);
		groupRenameButton.setEnabled(true);
		groupDeleteButton.setEnabled(true);
		groupEditButton.setEnabled(true);
		groupTable.setEnabled(true);
		unitCancelButton.setEnabled(false);
		unitUpdateButton.setEnabled(false);
		unitDeleteButton.setEnabled(false);
		unitSpanButton.setEnabled(false);
		searchButton.setEnabled(false);
		searchInsertButton.setEnabled(false);
		unitTable.setEnabled(false);
		searchUnit.setEnabled(false);
		searchMark.setEnabled(false);
		searchName.setEnabled(false);
		searchTable.setEnabled(false);
	}

	private void writePage() {
		try {
			accessable.invoke("TrendFileService", new Object[] {
				TrendFileService.SAVE_OP,
				graphProperties.getPagefile(),
				page.getXmlString() });
		} catch (RemoteException ex) {
			logger.error("ページファイル出力時にエラーが発生しました。", ex);
		}
	}

	public static void main(String[] args) {
		final JFrame f = new JFrame("トレンドグラフ操作");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JButton b = new JButton("トレンドグラフ操作");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphProperties p = new GraphProperties();
				p.setPagefile("trend4.xml");
				new EditorMainPanel(f, p).setVisible(true);
			}
		});
		f.add(b);
		f.pack();
		f.setVisible(true);
	}
}
