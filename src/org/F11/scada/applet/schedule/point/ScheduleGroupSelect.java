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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.ActionMapUtil;
import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.F11.scada.util.RmiErrorUtil;
import org.F11.scada.util.TableUtil;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;

public class ScheduleGroupSelect extends JDialog {
	private static final long serialVersionUID = 1365889797979478376L;
	private final Logger logger = Logger.getLogger(ScheduleGroupSelect.class);
	private ScheduleGroupDto dto;
	private final PageChanger changer;

	public ScheduleGroupSelect(
			JDialog dialog,
			ScheduleGroupDto dto,
			String pageId,
			PageChanger changer) {
		super(dialog, "グループ選択", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.dto = dto;
		this.changer = changer;
		init(dialog, pageId);
		WifeUtilities.setCenter(this);
	}

	private void init(JDialog dialog, String pageId) {
		try {
			setMainPanel(pageId);
			pack();
		} catch (RemoteException e) {
			RmiErrorUtil.error(logger, e, dialog);
		}
		setResizable(false);
	}

	private void setMainPanel(String pageId) throws RemoteException {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		final JTable table = getTable(pageId);
		JScrollPane pane = new JScrollPane(table);
		panel.add(pane, BorderLayout.CENTER);
		JButton modifyButton = new JButton("選択");
		modifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScheduleGroupTableModel model =
					(ScheduleGroupTableModel) table.getModel();
				setScheduleGroupDto(model.getScheduleGroupDto(table
					.getSelectedRow()));
				dispose();
			}
		});
		JButton cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		ActionMapUtil.setActionMap(cancelButton, changer);
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		buttonBox.add(Box.createHorizontalStrut(300));
		buttonBox.add(modifyButton);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(cancelButton);
		panel.add(buttonBox, BorderLayout.SOUTH);
		getContentPane().add(panel);
	}

	private JTable getTable(String pageId) throws RemoteException {
		JTable table = new JTable(getModel(pageId));
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		String[] titles = ScheduleGroupDto.getGroupTitles();
		table.removeColumn(table.getColumn(titles[0]));
		table.removeColumn(table.getColumn(titles[3]));
		table.removeColumn(table.getColumn(titles[4]));
		table.removeColumn(table.getColumn(titles[5]));

		TableColumnModel columnModel = table.getColumnModel();
		TableUtil.setWidth(40, 0, columnModel);
		table.addMouseListener(new TableListener());

		if (null != dto && 0 < table.getRowCount()) {
			table.setRowSelectionInterval(dto.getGroupNo().intValue(), dto
				.getGroupNo()
				.intValue());
		}
		return table;
	}

	private TableModel getModel(String pageId) throws RemoteException {
		return null != dto
			? new ScheduleGroupTableModelImpl(pageId, false)
			: new ScheduleGroupTableModelImpl(pageId, true);
	}

	ScheduleGroupDto getScheduleGroupDto() {
		return dto;
	}

	void setScheduleGroupDto(ScheduleGroupDto dto) {
		this.dto = dto;
	}

	private class TableListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.getClickCount() == 2) {
				JTable table = (JTable) e.getSource();
				int row = table.rowAtPoint(e.getPoint());
				ScheduleGroupTableModel model =
					(ScheduleGroupTableModel) table.getModel();
				setScheduleGroupDto(model.getScheduleGroupDto(row));
				dispose();
			}
		}
	}
}
