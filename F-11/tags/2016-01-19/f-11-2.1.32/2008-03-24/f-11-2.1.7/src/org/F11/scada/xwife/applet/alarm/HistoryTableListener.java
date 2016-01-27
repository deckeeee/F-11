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

package org.F11.scada.xwife.applet.alarm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Timer;

import javax.swing.JTable;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.security.auth.Subject;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.util.TableUtil;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.F11.scada.xwife.applet.alarm.event.CheckEvent;
import org.F11.scada.xwife.server.AlarmDataProvider;

public class HistoryTableListener extends MouseAdapter implements LimitedAction {
	private static final int CHECK_COLUMN = 12;
	/** 連続クリック防止タイム */
	private static final long ACTION_WAIT_TIME = 500L;
	private final AbstractWifeApplet wifeApplet;
	/** 警報確認クリック許可フラグ */
	private volatile boolean isCheck = true;
	/** 連続クリック防止タイマー */
	private final Timer timer;

	public HistoryTableListener(AbstractWifeApplet wifeApplet) {
		super();
		this.wifeApplet = wifeApplet;
		timer = new Timer(true);
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public void mousePressed(MouseEvent e) {
		// 未ログイン時は確認不許可
		if (wifeApplet.getSubject() == Subject.getNullSubject())
			return;

		if (e.getClickCount() == 1 && isCheck) {
			JTable table = (JTable) e.getSource();
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			Object o = table.getValueAt(row, column);
			if (TableUtil.getModelColumn(e) == CHECK_COLUMN && o == null) {
				table.setValueAt("＊＊＊＊", row, column);
				DataHolder dh = Manager.getInstance().findDataHolder(
						AlarmDataProvider.PROVIDER_NAME,
						AlarmDataProvider.HISTORY);
				dh.setParameter("row", new Integer(row));
				dh.setParameter("column", new Integer(column));
				try {
					dh.syncWrite();
				} catch (DataProviderDoesNotSupportException ex) {
					ex.printStackTrace();
				}
				AlarmTableModel model = (AlarmTableModel) table.getModel();
				CheckEvent checkEvent = new CheckEvent(
						AlarmDataProvider.HISTORY,
						model,
						row,
						new Timestamp(System.currentTimeMillis()));
				model.fireCheckEvent(checkEvent);
			}
			isCheck = false;
			timer.schedule(new ActionTimerTask(this), ACTION_WAIT_TIME);
		}
	}
}