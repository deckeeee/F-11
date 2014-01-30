/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.invoke;

import java.sql.SQLException;
import java.sql.Timestamp;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmTableJournal;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.HistoryCheck;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.xwife.applet.alarm.event.CheckEvent;
import org.F11.scada.xwife.server.AlarmDataProvider;
import org.apache.log4j.Logger;

public class SetNoncheckTable implements InvokeHandler {
	private Logger logger = Logger.getLogger(SetNoncheckTable.class);
	private final HistoryCheck historyCheck;

	public SetNoncheckTable(HistoryCheck historyCheck) {
		this.historyCheck = historyCheck;
	}

	public Object invoke(Object[] args) {
		updateTableModel(args);
		return null;
	}

	private void updateTableModel(Object[] args) {
		CheckEvent evt = (CheckEvent) args[0];
		Integer point = new Integer(evt.getPoint());
		String provider = evt.getProvider();
		String holder = evt.getHolder();
		Timestamp date = evt.getTimestamp();

		DataHolder dh = getDataHolder();
		AlarmTableModel model = (AlarmTableModel) dh.getValue();
		DataValueChangeEventKey key = new DataValueChangeEventKey(point
				.intValue(), provider, holder, Boolean.TRUE, date);
		Object[] rows = (Object[]) args[1];
		setHistoryCheck(key, date, rows);
		model.setJournal(AlarmTableJournal.createRowDataRemoveOpe(key, rows));
		dh.setValue(model, date, WifeQualityFlag.GOOD);
		updateHistoryCheck(args, model, rows);
	}

	private DataHolder getDataHolder() {
		return Manager.getInstance().findDataHolder(
				AlarmDataProvider.PROVIDER_NAME,
				AlarmDataProvider.NONCHECK);
	}

	private void setHistoryCheck(DataValueChangeEventKey key, Timestamp date, Object[] rows) {
		DataHolder dh = 
			Manager.getInstance().findDataHolder(AlarmDataProvider.PROVIDER_NAME, AlarmDataProvider.HISTORY);
		AlarmTableModel historyModel = (AlarmTableModel) dh.getValue();
		historyModel.setJournal(AlarmTableJournal.createRowDataModifyOpe(key, rows));
		dh.setValue(historyModel, date, WifeQualityFlag.GOOD);
	}

	private void updateHistoryCheck(Object[] args, AlarmTableModel model, Object[] rows) {
		CheckEvent evt = (CheckEvent) args[0];
		Integer point = new Integer(evt.getPoint());
		String provider = evt.getProvider();
		String holder = evt.getHolder();
		try {
			historyCheck.doHistoryCheck(
					point,
					provider,
					holder,
					(Timestamp) rows[7]);
		} catch (SQLException e) {
			logger.error("確認処理中にDBエラーが発生しました : ", e);
		}
	}
}
