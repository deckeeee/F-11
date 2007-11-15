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

import java.rmi.RemoteException;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.F11.scada.server.schedule.SchedulePointService;
import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.F11.scada.util.RmiUtil;

public class ScheduleGroupTableModelImpl extends AbstractTableModel implements
		ScheduleGroupTableModel {
	private static final long serialVersionUID = -8723983103775995648L;
	private static final String[] TITLE = ScheduleGroupDto.getGroupTitles();
	private List data;

	public ScheduleGroupTableModelImpl(String pageId, boolean removeTop)
			throws RemoteException {
		this((SchedulePointService) RmiUtil
				.lookupServer(SchedulePointService.class), pageId, removeTop);
	}

	public ScheduleGroupTableModelImpl(
			SchedulePointService proxy,
			String pageId,
			boolean removeTop) throws RemoteException {
		ScheduleGroupDto dto = new ScheduleGroupDto();
		dto.setPageId(pageId);
		data = proxy.getScheduleGroup(dto);
		if (removeTop && 0 < data.size()) {
			data.remove(0);
		}
	}

	public int getColumnCount() {
		return TITLE.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		ScheduleGroupDto dto = (ScheduleGroupDto) data.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return dto.getId();
		case 1:
			return dto.getGroupNo();
		case 2:
			return dto.getGroupName();
		case 3:
			return dto.getProvider();
		case 4:
			return dto.getHolder();
		case 5:
			return dto.getPageId();
		default:
			return "err...";
		}
	}

	public String getColumnName(int column) {
		return TITLE[column];
	}

	public Class getColumnClass(int columnIndex) {
		Object obj = getValueAt(0, columnIndex);
		return obj != null ? obj.getClass() : String.class;
	}

	public ScheduleGroupDto getScheduleGroupDto(int row) {
		return (ScheduleGroupDto) data.get(row);
	}
}
