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

package org.F11.scada.server.invoke;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.server.dao.HistoryTableDao;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.xwife.server.AlarmDataProvider;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

/**
 * ヒストリ確認欄を全てチェック済みに更新するハンドラです。
 * 負荷の影響を考えてクライアント間通信はせず、サーバー内テーブルとヒストリテーブル(DB)を更新するのみです。
 * この処理の結果を反映するには、クライアントの再起動が必要になります。
 * 
 * @author maekawa
 * 
 */
public class HistoryAllCheck implements InvokeHandler {
	private final Logger logger = Logger.getLogger(HistoryAllCheck.class);
	/** ヒストリテーブルのDao */
	private final HistoryTableDao dao;

	public HistoryAllCheck() {
		S2Container container = (S2Container) S2ContainerUtil.getS2Container();
		dao = (HistoryTableDao) container.getComponent(HistoryTableDao.class);
	}

	public Object invoke(Object[] args) {
		checkedTableModel();
		removeTableModel();
		updateHistoryTable();
		logger.info("全確認処理終了");
		return null;
	}

	private void checkedTableModel() {
		DataHolder dh =
			Manager.getInstance().findDataHolder(
				AlarmDataProvider.PROVIDER_NAME,
				AlarmDataProvider.HISTORY);
		AlarmTableModel model = (AlarmTableModel) dh.getValue();
		for (int i = 0, count = model.getRowCount(); i < count; i++) {
			Object b = model.getValueAt(i, model.getColumnCount() - 1);
			if (null == b) {
				model.setValueAt("＊＊＊＊", i, model.getColumnCount() - 1);
			}
		}
	}

	private void removeTableModel() {
		DataHolder dh =
			Manager.getInstance().findDataHolder(
				AlarmDataProvider.PROVIDER_NAME,
				AlarmDataProvider.NONCHECK);
		AlarmTableModel model = (AlarmTableModel) dh.getValue();
		for (int i = 0, count = model.getRowCount(); i < count; i++) {
			model.removeRow(0);
		}
	}

	private void updateHistoryTable() {
		dao.updateAllCheck();
	}
}
