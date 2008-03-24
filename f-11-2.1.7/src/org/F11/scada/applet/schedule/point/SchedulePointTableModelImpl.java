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

import java.awt.Component;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import jp.gr.javacons.jim.Manager;

import org.F11.scada.WifeUtilities;
import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.security.AccessControlable;
import org.F11.scada.security.auth.Subject;
import org.F11.scada.server.schedule.SchedulePointService;
import org.F11.scada.server.schedule.point.dto.SchedulePointDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.server.schedule.point.dto.ScheduleSearchDto;
import org.F11.scada.util.RmiErrorUtil;
import org.F11.scada.util.RmiUtil;
import org.F11.scada.xwife.applet.WifeDataProviderProxy;
import org.apache.log4j.Logger;

public class SchedulePointTableModelImpl extends AbstractTableModel implements
		SchedulePointTableModel {
	private static final long serialVersionUID = -5909904917175846254L;
	private final Logger logger = Logger
			.getLogger(SchedulePointTableModelImpl.class);
	private static final String[] TITLE = SchedulePointRowDto.getTitles();
	private final SchedulePointService schedulePointService;
	private List data;
	private final boolean checkPermission;
	private AccessControlable controlable;
	private final Component parent;

	public SchedulePointTableModelImpl(Component parent) {
		this(false, parent);
	}

	public SchedulePointTableModelImpl(boolean checkPermission, Component parent) {
		this(
				(SchedulePointService) RmiUtil
						.lookupServer(SchedulePointService.class),
				checkPermission,
				parent);
	}

	public SchedulePointTableModelImpl(
			SchedulePointService schedulePointService,
			boolean checkPermission,
			Component parent) {
		this.schedulePointService = schedulePointService;
		this.checkPermission = checkPermission;
		this.parent = parent;
		try {
			this.schedulePointService.init();
		} catch (RemoteException e) {
			RmiErrorUtil.error(logger, e, parent);
		}
		data = Collections.EMPTY_LIST;

		try {
			controlable = (AccessControlable) Naming.lookup(WifeUtilities
					.createRmiActionControl());
		} catch (Exception e) {
			RmiErrorUtil.error(logger, e, parent);
		}
	}

	public SchedulePointDto find(ScheduleSearchDto dto) throws RemoteException {
		SchedulePointDto ret = schedulePointService.getSchedulePoint(dto);
		if (checkPermission) {
			data = checkPermission(ret.getRows());
		} else {
			data = ret.getRows();
		}
		fireTableDataChanged();
		return ret;
	}

	public SchedulePointDto findByGroup(ScheduleSearchDto dto)
			throws RemoteException {
		SchedulePointDto ret = schedulePointService
				.getSchedulePointByGroup(dto);
		if (checkPermission) {
			data = checkPermission(ret.getRows());
		} else {
			data = ret.getRows();
		}
		fireTableDataChanged();
		return ret;
	}

	private List checkPermission(List rows) throws RemoteException {
		if (rows.size() > 0) {
			String[][] holderIds = getHolderIds(rows);
			WifeDataProviderProxy proxy = getProxy(rows);
			Subject subject = proxy.getSubject();
			List booleans = controlable.checkPermission(subject, holderIds);
			return getFillteredRows(rows, booleans);
		} else {
			return rows;
		}
	}

	private List getFillteredRows(List rows, List booleans) {
		for (int i = rows.size(); 0 < i; i--) {
			if (isRemove(booleans.get(i - 1))) {
				rows.remove(i - 1);
			}
		}
		return rows;
	}

	private boolean isRemove(Object object) {
		Boolean[] b = (Boolean[]) object;
		return !b[0].booleanValue();
	}

	private WifeDataProviderProxy getProxy(List rows) {
		Manager manager = Manager.getInstance();
		SchedulePointRowDto dto = (SchedulePointRowDto) rows.get(0);
		return (WifeDataProviderProxy) manager.getDataProvider(dto
				.getGroupNoProvider());
	}

	private String[][] getHolderIds(List rows) {
		ArrayList list = new ArrayList(rows.size());
		for (Iterator i = rows.iterator(); i.hasNext();) {
			SchedulePointRowDto dto = (SchedulePointRowDto) i.next();
			list.add(new String[] { getHolderId(dto) });
		}
		list.trimToSize();
		return (String[][]) list.toArray(new String[0][0]);
	}

	private String getHolderId(SchedulePointRowDto dto) {
		return dto.getSeparateProvider() + "_" + dto.getSeparateHolder();
	}

	public int getColumnCount() {
		return SchedulePointRowDto.getColumn();
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		SchedulePointRowDto dto = (SchedulePointRowDto) data.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return dto.getId();
		case 1:
			return dto.getGroupNo();
		case 2:
			return dto.getGroupName();
		case 3:
			return dto.getUnit();
		case 4:
			return dto.getName();
		case 5:
			return dto.getSort().booleanValue() ? "—L‚è" : "–³‚µ";
		case 6:
			return dto.getGroupNoProvider();
		case 7:
			return dto.getGroupNoHolder();
		case 8:
			return dto.getSeparateProvider();
		case 9:
			return dto.getSeparateHolder();
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

	public SchedulePointRowDto getSchedulePointRowDto(int row) {
		return (SchedulePointRowDto) data.get(row);
	}

	public void setSchedulePointRowDto(SchedulePointRowDto dto, int row) {
		try {
			schedulePointService.updateSchedulePoint(dto);
			fireTableChanged(new TableModelEvent(this, row));
		} catch (RemoteException e) {
			RmiErrorUtil.error(logger, e, parent);
		}
	}

	public WifeDataSchedule getSeparateSchedule(int row) {
		SchedulePointRowDto dto = getSchedulePointRowDto(row);
		try {
			return schedulePointService.getSeparateSchedule(dto);
		} catch (RemoteException e) {
			RmiErrorUtil.error(logger, e, parent);
			return null;
		}
	}
}
