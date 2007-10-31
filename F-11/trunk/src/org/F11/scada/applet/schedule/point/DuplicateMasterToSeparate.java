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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.sql.Timestamp;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.table.TableColumnModel;

import jp.gr.javacons.jim.Manager;

import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.server.schedule.SchedulePointService;
import org.F11.scada.server.schedule.point.dto.DuplicateSeparateScheduleDto;
import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.server.schedule.point.dto.ScheduleSearchDto;
import org.F11.scada.util.RmiErrorUtil;
import org.F11.scada.util.RmiUtil;
import org.F11.scada.util.TableUtil;
import org.F11.scada.xwife.applet.WifeDataProviderProxy;
import org.apache.log4j.Logger;

public class DuplicateMasterToSeparate extends JPanel {
	private static final long serialVersionUID = 6478886014157963404L;
	private final Logger logger = Logger
			.getLogger(DuplicateMasterToSeparate.class);
	private final SchedulePointService proxy;
	private final JDialog dialog;
	private final JTable schedulePointTable;
	private ScheduleGroupDto srcGroupDto;

	public DuplicateMasterToSeparate(JDialog dialog, String pageId) {
		this(
				(SchedulePointService) RmiUtil
						.lookupServer(SchedulePointService.class),
				dialog,
				new SchedulePointTableModelImpl(true, dialog),
				pageId);
	}

	public DuplicateMasterToSeparate(
			SchedulePointService proxy,
			JDialog dialog,
			SchedulePointTableModel pointTableModel,
			String pageId) {
		super(new BorderLayout());
		this.proxy = proxy;
		this.dialog = dialog;
		schedulePointTable = new JTable(pointTableModel);
		init(pageId);
	}

	public void setSrcGroupDto(ScheduleGroupDto srcGroupDto) {
		this.srcGroupDto = srcGroupDto;
	}

	public ScheduleGroupDto getSrcGroupDto() {
		return srcGroupDto;
	}

	private void init(String pageId) {
		try {
			add(getMainBox(pageId), BorderLayout.CENTER);
			add(getCloseButton(), BorderLayout.SOUTH);
		} catch (RemoteException e) {
			RmiErrorUtil.error(logger, e, dialog);
		}
	}

	private Component getMainBox(String pageId) throws RemoteException {
		Box box = Box.createHorizontalBox();
		box.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(
				10,
				15,
				10,
				15), BorderFactory.createTitledBorder("マスタ　→　個別転送")));
		box.add(getGroup(pageId));
		box.add(getArrow());
		box.add(getPoint());
		box.add(getSelectButton());
		return box;
	}

	private Component getGroup(String pageId) throws RemoteException {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

		JTable table = getGroupTable(pageId);
		JScrollPane sc = new JScrollPane(table);
		sc.setPreferredSize(new Dimension(200, 0));
		panel.add(sc, BorderLayout.CENTER);
		panel.add(getSearchButton(table), BorderLayout.SOUTH);
		return panel;
	}

	private Component getSearchButton(JTable table) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		JButton button = new JButton("検索");
		button.addActionListener(new SearchAction(
				this,
				table,
				schedulePointTable));
		box.add(button);
		box.add(Box.createHorizontalGlue());
		return box;
	}

	private JTable getGroupTable(String pageId) throws RemoteException {
		JTable table = new JTable(new ScheduleGroupTableModelImpl(pageId, true));
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		String[] titles = ScheduleGroupDto.getGroupTitles();
		table.removeColumn(table.getColumn(titles[0]));
		table.removeColumn(table.getColumn(titles[3]));
		table.removeColumn(table.getColumn(titles[4]));
		table.removeColumn(table.getColumn(titles[5]));

		TableColumnModel columnModel = table.getColumnModel();
		TableUtil.setWidth(40, 0, columnModel);

		table.addMouseListener(new TableListener(this, schedulePointTable));
		return table;
	}

	private Component getArrow() {
		return new JLabel(GraphicManager.get("/images/bigarrow.png"));
	}

	private Component getPoint() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
		panel.add(getPointTable());
		return panel;
	}

	private Component getPointTable() {
		String[] titles = SchedulePointRowDto.getTitles();
		schedulePointTable
				.removeColumn(schedulePointTable.getColumn(titles[0]));
		schedulePointTable
				.removeColumn(schedulePointTable.getColumn(titles[1]));
		schedulePointTable
				.removeColumn(schedulePointTable.getColumn(titles[2]));
		schedulePointTable
				.removeColumn(schedulePointTable.getColumn(titles[5]));
		schedulePointTable
				.removeColumn(schedulePointTable.getColumn(titles[6]));
		schedulePointTable
				.removeColumn(schedulePointTable.getColumn(titles[7]));
		schedulePointTable
				.removeColumn(schedulePointTable.getColumn(titles[8]));
		schedulePointTable
				.removeColumn(schedulePointTable.getColumn(titles[9]));

		TableColumnModel columnModel = schedulePointTable.getColumnModel();
		TableUtil.setWidth(150, 0, columnModel);
		return new JScrollPane(schedulePointTable);
	}

	private Component getSelectButton() {
		Box box = Box.createVerticalBox();
		box.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));
		JButton clearButton = new JButton("クリア");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schedulePointTable.clearSelection();
			}
		});
		box.add(clearButton);
		box.add(Box.createVerticalStrut(5));
		JButton selectAllButton = new JButton("すべて");
		selectAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schedulePointTable.selectAll();
			}
		});
		box.add(selectAllButton);
		box.add(Box.createVerticalStrut(20));
		JButton duplicateButton = new JButton("複写");
		duplicateButton.addActionListener(new SetValueListener(
				this,
				schedulePointTable));
		box.add(duplicateButton);
		box.add(Box.createVerticalGlue());
		return box;
	}

	private Component getCloseButton() {
		JButton closeButton = new JButton("閉じる");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
		box.add(Box.createHorizontalGlue());
		box.add(closeButton);
		return box;
	}

	private static class TableListener extends MouseAdapter {
		private final Logger logger = Logger.getLogger(TableListener.class);
		private final DuplicateMasterToSeparate mainPanel;
		private final JTable schedulePointTable;

		public TableListener(
				DuplicateMasterToSeparate mainPanel,
				JTable schedulePointTable) {
			this.mainPanel = mainPanel;
			this.schedulePointTable = schedulePointTable;
		}

		public void mousePressed(MouseEvent e) {
			if (e.getClickCount() == 2) {
				JTable table = (JTable) e.getSource();
				int row = table.rowAtPoint(e.getPoint());
				ScheduleGroupTableModel model = (ScheduleGroupTableModel) table
						.getModel();
				ScheduleGroupDto dto = model.getScheduleGroupDto(row);
				if (ScheduleSearchDtoUtil.isSearch(dto, mainPanel)) {
					try {
						SchedulePointTableModel pointTableModel = (SchedulePointTableModel) schedulePointTable
								.getModel();
						pointTableModel.findByGroup(ScheduleSearchDtoUtil
								.getSearchDto(dto));
						schedulePointTable.selectAll();
						mainPanel.setSrcGroupDto(dto);
					} catch (RemoteException ex) {
						RmiErrorUtil.error(logger, ex, mainPanel);
					}
				}
			}
		}
	}

	private static class SearchAction implements ActionListener {
		private final Logger logger = Logger.getLogger(SearchAction.class);
		private final DuplicateMasterToSeparate mainPanel;
		private final JTable scheduleGroupTable;
		private final JTable schedulePointTable;

		SearchAction(
				DuplicateMasterToSeparate mainPanel,
				JTable scheduleGroupTable,
				JTable schedulePointTable) {
			this.mainPanel = mainPanel;
			this.scheduleGroupTable = scheduleGroupTable;
			this.schedulePointTable = schedulePointTable;
		}

		public void actionPerformed(ActionEvent e) {
			int row = scheduleGroupTable.getSelectedRow();
			if (row >= 0) {
				ScheduleGroupTableModel model = (ScheduleGroupTableModel) scheduleGroupTable
						.getModel();
				ScheduleGroupDto dto = model.getScheduleGroupDto(row);
				if (ScheduleSearchDtoUtil.isSearch(dto, mainPanel)) {
					try {
						SchedulePointTableModel pointTableModel = (SchedulePointTableModel) schedulePointTable
								.getModel();
						pointTableModel.findByGroup(ScheduleSearchDtoUtil
								.getSearchDto(dto));
						schedulePointTable.selectAll();
						mainPanel.setSrcGroupDto(dto);
					} catch (RemoteException ex) {
						RmiErrorUtil.error(logger, ex, mainPanel);
					}
				}
			}
		}
	}

	private static abstract class ScheduleSearchDtoUtil {
		static ScheduleSearchDto getSearchDto(ScheduleGroupDto dto) {
			ScheduleSearchDto searchDto = new ScheduleSearchDto();
			searchDto.setGroupNo(dto.getGroupNo());
			searchDto.setGroupName(dto.getGroupName());
			searchDto.setPageId(dto.getPageId());
			return searchDto;
		}

		static boolean isSearch(
				ScheduleGroupDto dto,
				DuplicateMasterToSeparate mainPanel) {
			if (null != mainPanel.getSrcGroupDto()) {
				ScheduleGroupDto srcDto = mainPanel.getSrcGroupDto();
				return srcDto.getGroupNo() != dto.getGroupNo();
			}
			return true;
		}
	}

	private class SetValueListener implements ActionListener {
		private final Logger logger = Logger.getLogger(SetValueListener.class);
		private final DuplicateMasterToSeparate mainPanel;
		private final JTable schedulePointTable;

		public SetValueListener(
				DuplicateMasterToSeparate mainPanel,
				JTable schedulePointTable) {
			this.mainPanel = mainPanel;
			this.schedulePointTable = schedulePointTable;
		}

		public void actionPerformed(ActionEvent e) {
			String[] option = { "はい", "いいえ" };
			ScheduleGroupDto groupDto = mainPanel.getSrcGroupDto();
			if (null != groupDto) {
				SchedulePointRowDto[] rowDtos = getSelectPoint();
				if (0 < rowDtos.length) {
					String srcGroup = groupDto.getGroupName();
					int rc = JOptionPane.showOptionDialog(
							mainPanel,
							getMessage(srcGroup),
							srcGroup + "を選択した個別スケジュールへ転送",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							option,
							option[1]);
					if (rc == JOptionPane.OK_OPTION) {
						logger.info("src=" + mainPanel.getSrcGroupDto());
						try {
							DuplicateSeparateScheduleDto dto = getDuplicateSeparateScheduleDto(rowDtos);
							proxy.duplicateSeparateSchedule(dto);
						} catch (RemoteException e1) {
							RmiErrorUtil.error(logger, e1, mainPanel);
						}
					}
				} else {
					JOptionPane.showMessageDialog(
							mainPanel,
							"転送先のグループが選択されていません。",
							"転送先のグループが選択されていません",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(
						mainPanel,
						"転送元のグループを選択後、検索してください。",
						"転送元のグループを選択後、検索してください",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		private DuplicateSeparateScheduleDto getDuplicateSeparateScheduleDto(
				SchedulePointRowDto[] rowDtos) {
			ScheduleGroupDto groupDto = mainPanel.getSrcGroupDto();
			DuplicateSeparateScheduleDto dto = new DuplicateSeparateScheduleDto(
					groupDto,
					rowDtos,
					getUser(groupDto),
					getIpAddress(),
					new Timestamp(System.currentTimeMillis()));
			return dto;
		}

		private String getIpAddress() {
			try {
				InetAddress address = InetAddress.getLocalHost();
				return address.getHostAddress();
			} catch (UnknownHostException e) {
				logger.error("IPアドレスの取得時にエラー発生", e);
			}
			return "";
		}

		private String getUser(ScheduleGroupDto groupDto) {
			WifeDataProviderProxy proxy = (WifeDataProviderProxy) Manager
					.getInstance().getDataProvider(groupDto.getProvider());
			return proxy.getSubject().getUserName();
		}

		private Component getMessage(String srcGroup) {
			String msg = "<html>" + "<B>「" + srcGroup + "」</B>"
					+ "を選択した個別スケジュールへ転送します。<br>よろしいですか？" + "</html>";
			JLabel message = new JLabel(msg);
			return message;
		}

		private SchedulePointRowDto[] getSelectPoint() {
			int[] selectRows = schedulePointTable.getSelectedRows();
			SchedulePointRowDto[] dest = new SchedulePointRowDto[selectRows.length];
			SchedulePointTableModel model = (SchedulePointTableModel) schedulePointTable
					.getModel();
			for (int i = 0; i < selectRows.length; i++) {
				dest[i] = model.getSchedulePointRowDto(selectRows[i]);
			}
			return dest;
		}
	}
}
