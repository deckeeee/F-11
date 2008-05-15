/*
 * =============================================================================
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
 *
 */

package org.F11.scada.applet.schedule.point;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;

import jp.gr.javacons.jim.Manager;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.security.AccessControlable;
import org.F11.scada.security.auth.Subject;
import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.server.schedule.point.dto.ScheduleSearchDto;
import org.F11.scada.util.AttributesUtil;
import org.F11.scada.util.RmiErrorUtil;
import org.F11.scada.util.TableUtil;
import org.F11.scada.xwife.applet.WifeDataProviderProxy;
import org.apache.log4j.Logger;
import org.seasar.dao.pager.PagerViewHelper;

public class SchedulePointFinder {
	private final Logger logger = Logger.getLogger(SchedulePointFinder.class);
	private ScheduleSearchDto searchDto;
	private JDialog dialog;
	private JTextField unitField;
	private JTextField groupField;
	private JButton groupSelect;
	private JTextField nameField;
	private JRadioButton groupConnect;
	private JRadioButton groupNonConnect;
	private final String pageId;

	public SchedulePointFinder(Frame frame, String pageId) {
		this(frame, pageId, new SchedulePointTableModelImpl(frame));
	}

	public SchedulePointFinder(
			Frame frame,
			String pageId,
			SchedulePointTableModel model) {
		this.pageId = pageId;
		try {
			SchedulePointDto dto = model.find(getInitDto());
			searchDto = dto.getDto();
			init(frame, model);
		} catch (RemoteException e) {
			RmiErrorUtil.error(logger, e, dialog);
		}
	}

	private ScheduleSearchDto getInitDto() {
		ScheduleSearchDto dto = new ScheduleSearchDto();
		dto.setLimit(getLimit());
		dto.setPageId(pageId);
		dto.setGroupNo(new Integer(0));
		return dto;
	}

	private int getLimit() {
		ClientConfiguration configuration = new ClientConfiguration();
		return configuration.getInt(
			"org.F11.scada.applet.schedule.point.limit",
			25);
	}

	private void init(Frame frame, SchedulePointTableModel model)
			throws RemoteException {
		dialog = new JDialog(frame, "スケジュール操作", true);
		dialog.addWindowListener(new WindowListenerImpl());
		JTabbedPane tab =
			new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		tab.addTab("スケジュール機器一覧", getMainPanel(model));
		if (isSeparateSchedule()) {
			tab.addTab(
				"マスタ→個別転送",
				new DuplicateMasterToSeparate(dialog, pageId));
		}
		dialog.getContentPane().add(tab);
		dialog.setSize(1030, 800);
		dialog.setResizable(false);
		WifeUtilities.setScreenCenter(dialog);
		dialog.setVisible(true);
	}

	private Component getMainPanel(SchedulePointTableModel model) {
		JPanel searchPanel = getSearchPanel(model);
		JPanel tablePanel = getTablePanel(model);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(searchPanel, BorderLayout.NORTH);
		mainPanel.add(tablePanel, BorderLayout.CENTER);
		return mainPanel;
	}

	private JPanel getSearchPanel(SchedulePointTableModel model) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("検索条件"));
		panel.add(getConditionPanel(), BorderLayout.NORTH);
		panel.add(getSearchButton(model), BorderLayout.SOUTH);
		JPanel searchPanel = new JPanel();
		searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		searchPanel.add(panel);
		return searchPanel;
	}

	private Component getConditionPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 20));

		GridBagConstraints c = new GridBagConstraints();
		Insets zeroInsets = new Insets(0, 0, 0, 0);
		Insets centerInsets = new Insets(0, 30, 0, 0);
		int fieldSize = 30;

		JLabel unitLabel = new JLabel("記号 : ");
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(unitLabel, c);
		unitField = new JTextField(fieldSize);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		panel.add(unitField, c);

		JLabel groupLabel = new JLabel("グループ名 : ");
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 2;
		c.insets = centerInsets;
		panel.add(groupLabel, c);
		groupField = new JTextField(fieldSize);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 3;
		c.insets = zeroInsets;
		panel.add(groupField, c);
		c.gridx = 4;
		c.insets = new Insets(0, 5, 0, 0);
		groupSelect = new JButton("選択...");
		groupSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScheduleGroupSelect select =
					new ScheduleGroupSelect(dialog, null, pageId);
				select.setVisible(true);
				setScheduleGroup(select);
			}

			private void setScheduleGroup(ScheduleGroupSelect select) {
				ScheduleGroupDto scheDto = select.getScheduleGroupDto();
				if (null != scheDto) {
					groupField.setText("" + scheDto.getGroupName());
				}
			}
		});
		panel.add(groupSelect, c);

		JLabel nameLabel = new JLabel("機器名称 : ");
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = zeroInsets;
		panel.add(nameLabel, c);
		nameField = new JTextField(fieldSize);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		panel.add(nameField, c);

		JLabel masterLabel = new JLabel("割付・未割付 : ");
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 2;
		c.insets = centerInsets;
		panel.add(masterLabel, c);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 3;
		c.insets = zeroInsets;
		panel.add(getConnect(), c);

		return panel;
	}

	private Component getConnect() {
		JPanel panel = new JPanel();
		ButtonGroup group = new ButtonGroup();
		groupConnect = new JRadioButton("割付済", true);
		group.add(groupConnect);
		panel.add(groupConnect);
		groupNonConnect = new JRadioButton("未割付");
		group.add(groupNonConnect);
		panel.add(groupNonConnect);

		ActionListener listner =
			new ConnectChangeListener(groupField, groupSelect);
		groupConnect.addActionListener(listner);
		groupNonConnect.addActionListener(listner);

		return panel;
	}

	private Component getSearchButton(SchedulePointTableModel model) {
		JButton searchButton =
			new JButton("検索", GraphicManager
				.get("/toolbarButtonGraphics/general/Find24.gif"));
		searchButton.addActionListener(new FindAction(model, dialog));
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 30, 15, 30));
		panel.add(searchButton, BorderLayout.EAST);
		return panel;
	}

	private JPanel getTablePanel(SchedulePointTableModel model) {
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(getButtonPanel(model), BorderLayout.NORTH);
		JTable table = getTable(model);
		JScrollPane pane = new JScrollPane(table);
		pane.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(
			0,
			15,
			10,
			15), BorderFactory.createTitledBorder("スケジュール機器一覧")));
		tablePanel.add(pane, BorderLayout.CENTER);
		tablePanel.add(getSelectButton(table), BorderLayout.SOUTH);
		return tablePanel;
	}

	private JTable getTable(SchedulePointTableModel model) {
		JTable table = new JTable(model);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		String[] titles = SchedulePointRowDto.getTitles();
		table.removeColumn(table.getColumn(titles[0]));
		if (!isSeparateSchedule()) {
			table.removeColumn(table.getColumn(titles[5]));
		}
		table.removeColumn(table.getColumn(titles[6]));
		table.removeColumn(table.getColumn(titles[7]));
		table.removeColumn(table.getColumn(titles[8]));
		table.removeColumn(table.getColumn(titles[9]));

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumnModel columnModel = table.getColumnModel();
		TableUtil.setPreferredWidth(40, 0, columnModel);
		TableUtil.setPreferredWidth(150, 1, columnModel);
		TableUtil.setPreferredWidth(150, 2, columnModel);
		int nameWidth = 557;
		int kobetuWidth = 80;
		if (isSeparateSchedule()) {
			TableUtil.setPreferredWidth(nameWidth, 3, columnModel);
			TableUtil.setPreferredWidth(kobetuWidth, 4, columnModel);
		} else {
			TableUtil
				.setPreferredWidth(nameWidth + kobetuWidth, 3, columnModel);
		}

		table.addMouseListener(new TableListener(
			dialog,
			isSeparateSchedule(),
			pageId));
		return table;
	}

	private boolean isSeparateSchedule() {
		ClientConfiguration configuration = new ClientConfiguration();
		return configuration.getBoolean(
			"org.F11.scada.applet.schedule.point.SeparateSchedule",
			false);
	}

	private Component getButtonPanel(SchedulePointTableModel model) {
		JPanel panel = new JPanel();
		JButton prev =
			new JButton(GraphicManager
				.get("/toolbarButtonGraphics/navigation/Back24.gif"));
		prev.setToolTipText("前ページ");
		prev.addActionListener(new PrevAction(model, dialog));
		panel.add(prev);
		PageLabel pageLabel = new PageLabel();
		model.addTableModelListener(pageLabel);
		panel.add(pageLabel);
		JButton next =
			new JButton(GraphicManager
				.get("/toolbarButtonGraphics/navigation/Forward24.gif"));
		next.setToolTipText("次ページ");
		next.addActionListener(new NextAction(model));
		panel.add(next);
		return panel;
	}

	private Component getSelectButton(JTable table) {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 15));
		box.add(Box.createHorizontalGlue());
		JButton modifyButton = new JButton("変更");
		modifyButton.addActionListener(new ModifyAction(
			table,
			dialog,
			isSeparateSchedule(),
			pageId));
		box.add(modifyButton);
		box.add(Box.createHorizontalStrut(5));
		JButton closeButton = new JButton("閉じる");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		box.add(closeButton);
		return box;
	}

	private class NextAction implements ActionListener {
		private final SchedulePointTableModel model;

		NextAction(SchedulePointTableModel model) {
			this.model = model;
		}

		public void actionPerformed(ActionEvent e) {
			PagerViewHelper helper = new PagerViewHelper(searchDto);
			if (helper.isNext()) {
				searchDto.setOffset(helper.getNextOffset());
				try {
					model.find(searchDto);
				} catch (RemoteException ex) {
					RmiErrorUtil.error(logger, ex, dialog);
				}
			}
		}
	}

	private class PrevAction implements ActionListener {
		private final SchedulePointTableModel model;
		private final JDialog dialog;

		PrevAction(SchedulePointTableModel model, JDialog dialog) {
			this.model = model;
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			PagerViewHelper helper = new PagerViewHelper(searchDto);
			if (helper.isPrev()) {
				searchDto.setOffset(helper.getPrevOffset());
				try {
					model.find(searchDto);
				} catch (RemoteException ex) {
					RmiErrorUtil.error(logger, ex, dialog);
				}
			}
		}
	}

	private class PageLabel extends JLabel implements TableModelListener {
		private static final long serialVersionUID = -3963853599143549968L;

		PageLabel() {
			setPageText();
		}

		public void tableChanged(TableModelEvent e) {
			setPageText();
		}

		private void setPageText() {
			MessageFormat format =
				new MessageFormat("{0, number, integer} / {1, number, integer}");
			PagerViewHelper helper =
				new PagerViewHelper(searchDto, Integer.MAX_VALUE);
			setText(format.format(new Object[] {
				new Integer(getCurrentPageCount(helper)),
				new Integer(getPageCount(helper)) }));
		}

		private int getCurrentPageCount(PagerViewHelper helper) {
			return helper.getCount() <= 0 ? 0 : helper.getPageCount();
		}

		private int getPageCount(PagerViewHelper helper) {
			return helper.getCount() / helper.getLimit() + getMod(helper);
		}

		private int getMod(PagerViewHelper helper) {
			return helper.getCount() % helper.getLimit() == 0 ? 0 : 1;
		}
	}

	private class FindAction implements ActionListener {
		private final SchedulePointTableModel model;
		private final JDialog dialog;

		FindAction(SchedulePointTableModel model, JDialog dialog) {
			this.model = model;
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			searchDto.setUnit(AttributesUtil.getNonNullString(unitField
				.getText()));
			searchDto.setName(AttributesUtil.getNonNullString(nameField
				.getText()));
			searchDto.setGroupName(AttributesUtil.getNonNullString(groupField
				.getText()));
			if (groupNonConnect.isSelected()) {
				searchDto.setGroupNo(null);
			} else {
				searchDto.setGroupNo(new Integer(0));
			}
			searchDto.setOffset(0);
			try {
				searchDto = model.find(searchDto).getDto();
			} catch (RemoteException ex) {
				RmiErrorUtil.error(logger, ex, dialog);
			}
			model.fireTableDataChanged();
		}
	}

	private static class TableListener extends MouseAdapter {
		private final JDialog dialog;
		private final boolean isSeparateSchedule;
		private final CheckPermissionUtil util;
		private final String pageId;

		TableListener(JDialog dialog, boolean isSeparateSchedule, String pageId) {
			this.dialog = dialog;
			this.isSeparateSchedule = isSeparateSchedule;
			this.util = new CheckPermissionUtil(dialog);
			this.pageId = pageId;
		}

		public void mousePressed(MouseEvent e) {
			if (e.getClickCount() == 2) {
				JTable table = (JTable) e.getSource();
				int row = table.rowAtPoint(e.getPoint());
				SchedulePointTableModel model =
					(SchedulePointTableModel) table.getModel();
				if (util.checkPermission(model, row)) {
					SchedulePointModify modify =
						new SchedulePointModify(
							dialog,
							model,
							row,
							isSeparateSchedule,
							pageId);
					modify.setVisible(true);
				}
			}
		}
	}

	private static class ModifyAction implements ActionListener {
		private final JTable table;
		private final JDialog dialog;
		private final boolean isSeparateSchedule;
		private final CheckPermissionUtil util;
		private final String pageId;

		ModifyAction(
				JTable table,
				JDialog dialog,
				boolean isSeparateSchedule,
				String pageId) {
			this.table = table;
			this.dialog = dialog;
			this.isSeparateSchedule = isSeparateSchedule;
			this.util = new CheckPermissionUtil(dialog);
			this.pageId = pageId;
		}

		public void actionPerformed(ActionEvent e) {
			int row = table.getSelectedRow();
			if (row >= 0) {
				SchedulePointTableModel model =
					(SchedulePointTableModel) table.getModel();
				if (util.checkPermission(model, row)) {
					SchedulePointModify modify =
						new SchedulePointModify(
							dialog,
							model,
							row,
							isSeparateSchedule,
							pageId);
					modify.setVisible(true);
				}
			}
		}
	}

	private static class WindowListenerImpl extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			JDialog dialog = (JDialog) e.getSource();
			dialog.dispose();
		}
	}

	private static class ConnectChangeListener implements ActionListener {
		private final JTextField groupField;
		private final JButton groupSelect;
		private volatile boolean isConnect = true;

		public ConnectChangeListener(JTextField groupField, JButton groupSelect) {
			this.groupField = groupField;
			this.groupSelect = groupSelect;
		}

		public void actionPerformed(ActionEvent e) {
			AbstractButton b = (AbstractButton) e.getSource();
			if (!isConnect && "割付済".equals(b.getText()) && b.isSelected()) {
				groupField.setText(null);
				groupField.setEnabled(true);
				groupField.setBackground(SystemColor.textHighlightText);
				groupSelect.setEnabled(true);
				isConnect = true;
			} else if (isConnect && "未割付".equals(b.getText()) && b.isSelected()) {
				groupField.setText(null);
				groupField.setEnabled(false);
				groupField.setBackground(SystemColor.textInactiveText);
				groupSelect.setEnabled(false);
				isConnect = false;
			}
		}
	}

	private static class CheckPermissionUtil {
		private final Logger logger =
			Logger.getLogger(CheckPermissionUtil.class);
		private AccessControlable controlable;
		private final JDialog dialog;

		public CheckPermissionUtil(JDialog dialog) {
			this.dialog = dialog;
			try {
				controlable =
					(AccessControlable) Naming.lookup(WifeUtilities
						.createRmiActionControl());
			} catch (MalformedURLException e) {
				RmiErrorUtil.error(logger, e, dialog);
			} catch (RemoteException e) {
				RmiErrorUtil.error(logger, e, dialog);
			} catch (NotBoundException e) {
				RmiErrorUtil.error(logger, e, dialog);
			}
		}

		boolean checkPermission(SchedulePointTableModel model, int row) {
			Manager manager = Manager.getInstance();
			SchedulePointRowDto dto = model.getSchedulePointRowDto(row);
			WifeDataProviderProxy proxy =
				(WifeDataProviderProxy) manager.getDataProvider(dto
					.getGroupNoProvider());
			Subject subject = proxy.getSubject();
			try {
				List ret =
					controlable.checkPermission(
						subject,
						new String[][] { { getHolderId(dto) } });
				Boolean[] b = (Boolean[]) ret.get(0);
				return b[0].booleanValue();
			} catch (RemoteException e) {
				RmiErrorUtil.error(logger, e, dialog);
				return false;
			}
		}

		private String getHolderId(SchedulePointRowDto dto) {
			return dto.getGroupNoProvider() + "_" + dto.getGroupNoHolder();
		}
	}
}
