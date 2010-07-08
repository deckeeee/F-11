/*
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
 */
package org.F11.scada.applet.operationlog;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.F11.scada.Globals;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.exception.RemoteRuntimeException;
import org.F11.scada.server.operationlog.OperationLoggingFinderService;
import org.F11.scada.server.operationlog.dto.FinderConditionDto;
import org.F11.scada.server.operationlog.dto.OperationLoggingFinderDto;
import org.F11.scada.util.RmiUtil;
import org.F11.scada.util.ThreadUtil;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class DefaultOperationLoggingTableModel implements OperationLoggingTableModel {
    private static final Integer DEFAULT_LIMIT = new Integer(20);

    private final DefaultTableModel model;
    private OperationLoggingFinderService service;
    private FinderConditionDto finder;
    private int currentPage = 1;
    private int allPage;
    private Long[] pageIds;
    private final Integer limit;
    private boolean isPrefix;
    private boolean isDisplayDigital;

    public DefaultOperationLoggingTableModel() {
        this(lookup());
    }

    private static OperationLoggingFinderService lookup() {
    	return (OperationLoggingFinderService) RmiUtil.lookupServer(OperationLoggingFinderService.class);
    }

    public DefaultOperationLoggingTableModel(OperationLoggingFinderService service) {
        this.service = service;
        setPrefix();
        model = new DefaultTableModel(getTitle(), 0);
        ClientConfiguration configuration = new ClientConfiguration();
        limit = configuration.getInteger("operation.limit", DEFAULT_LIMIT);
        isDisplayDigital = configuration.getBoolean("operation.isDisplayDigital", true);
    }

    private String[] getTitle() {
    	return isPrefix ?
    			new String[] { "ID", "日時", "IP", "ユーザー", "変更前", "変更後", "ポイント記号", "ポイント名称", "ポイント詳細" }
    		:
    			new String[] { "ID", "日時", "IP", "ユーザー", "変更前", "変更後", "ポイント記号", "ポイント名称" };
    }

	private void setPrefix() {
		Exception exception = null;
		for (int i = 1; i <= Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
	        try {
	        	isPrefix = service.isPrefix();
	            exception = null;
	            break;
	        } catch (RemoteException e) {
	            service = lookup();
				ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				exception = e;
	            continue;
	        }
		}
		if (exception != null) {
		    throw new RemoteRuntimeException(exception);
		}
	}

    public void find(FinderConditionDto finder) {
        this.finder = (FinderConditionDto) finder.clone();
        this.finder.setLimit(limit);
        
        Exception exception = null;
		for (int i = 1; i <= Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
	        try {
	            int count = service.getCount(this.finder);
	            if (count > 0) {
	                allPage = getAllPage(count, limit.intValue());
	                pageIds = new Long[allPage];
	                List data = modifyTableModel(this.finder);
	                pageIds[0] = getPageId(data);
	                currentPage = 1;
	            } else {
	                allPage = 0;
	                pageIds = new Long[0];
	                modifyTableModel(this.finder);
	                currentPage = 1;
	            }
	            model.fireTableDataChanged();
	            exception = null;
	            break;
	        } catch (RemoteException e) {
	            service = lookup();
				ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				exception = e;
	            continue;
	        }
		}
		if (exception != null) {
		    throw new RemoteRuntimeException(exception);
		}
    }

    private int getAllPage(int count, int limit) {
        return (count % limit == 0) ? (count / limit) : (count / limit + 1);
    }

    private List modifyTableModel(FinderConditionDto finder) throws RemoteException {
        clearTableModel();
        List data = service.getOperationLogging(finder);
        modifyTableModel(data);
        return data;
    }

    private void clearTableModel() {
        while(0 < model.getRowCount()) {
            model.removeRow(0);
        }
    }

    private void modifyTableModel(List data) {
        for (Iterator i = data.iterator(); i.hasNext();) {
            OperationLoggingFinderDto dto = (OperationLoggingFinderDto) i.next();
            Object[] obj = getObjectArray(dto);
            model.addRow(obj);
        }
    }
    
    private Object[] getObjectArray(OperationLoggingFinderDto dto) {
        Object[] obj = new Object[getTitle().length];
        obj[0] = new Long(dto.getId());
        obj[1] = DateFormatUtils.format(dto.getOpeDate(), "yyyy/MM/dd HH:mm:ss");
        obj[2] = dto.getOpeIp();
        obj[3] = dto.getOpeUser();
        obj[4] = getOpeValue(dto.getOpeBeforeValue());
        obj[5] = getOpeValue(dto.getOpeAfterValue());
        obj[6] = dto.getUnit();
        obj[7] = dto.getName();
        if (isPrefix) {
        	obj[8] = dto.getMessage();
        }
        return obj;
    }

    private Object getOpeValue(String value) {
		return isDisplayDigital
			? value
					: ("true".equals(value) || "false".equals(value))
					? ""
							: value;
	}

	private Long getPageId(List data) {
        OperationLoggingFinderDto dto = (OperationLoggingFinderDto) data.get(0);
        return new Long(dto.getId());
    }

    public void next() {
        if ((currentPage + 1) <= allPage) {
            FinderConditionDto dto = (FinderConditionDto) finder.clone();
            Long id = (Long) model.getValueAt(model.getRowCount() - 1, 0);
            Long nextId = new Long(id.longValue() - 1);
            dto.setCurrentId(nextId);
            try {
                List data = modifyTableModel(dto);
                pageIds[currentPage] = getPageId(data);
                currentPage++;
                model.fireTableDataChanged();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void previous() {
        if ((currentPage - 1) > 0) {
            FinderConditionDto dto = (FinderConditionDto) finder.clone();
            dto.setCurrentId(pageIds[currentPage - 2]);
            try {
                modifyTableModel(dto);
                currentPage--;
                model.fireTableDataChanged();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getAllPage() {
        return allPage;
    }

    public boolean isPrefix() {
    	return isPrefix;
    }

    public int getColumnCount() {
        return model.getColumnCount();
    }

    public int getRowCount() {
        return model.getRowCount();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Class getColumnClass(int columnIndex) {
        return model.getColumnClass(columnIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return model.getValueAt(rowIndex, columnIndex);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        model.setValueAt(aValue, rowIndex, columnIndex);
    }

    public String getColumnName(int columnIndex) {
        return model.getColumnName(columnIndex);
    }

    public void addTableModelListener(TableModelListener l) {
        model.addTableModelListener(l);
    }

    public void removeTableModelListener(TableModelListener l) {
        model.removeTableModelListener(l);
    }
}
