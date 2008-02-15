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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.parser.alarm.AlarmDefine;
import org.F11.scada.parser.alarm.AlarmTableConfig;
import org.F11.scada.util.TableUtil;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.F11.scada.xwife.applet.SortColumnUtil;
import org.F11.scada.xwife.server.AlarmDataProvider;

public class CareerPanel extends JPanel {
	private static final long serialVersionUID = -2749104266106306188L;
	private AlarmTable career;
	/** メインアプレットの参照です */
	private AbstractWifeApplet wifeApplet;
	private static final String STATUS_FIELD_STRING = "警報・状態";
	private static final String DATE_FIELD_STRING = "8888/88/88 88:88:88";
	private int UNIT_FIELD_WIDTH = 150;

	public CareerPanel(AbstractWifeApplet wifeApplet) {
		super(new BorderLayout());
		this.wifeApplet = wifeApplet;
		addCareer(new AlarmDefine().getAlarmConfig().getAlarmTableConfig());
		setAlarmTableCellRenderer(career);
	}

	private void addCareer(AlarmTableConfig alarmTableConfig) {
		DataHolder dh = Manager.getInstance().findDataHolder(
				AlarmDataProvider.PROVIDER_NAME,
				AlarmDataProvider.CAREER);
		career = new AlarmTable(dh, wifeApplet, alarmTableConfig);
		career.setAutoCreateColumnsFromModel(false);
		removeColumns(career, 12);
		career.removeColumn(career.getColumn(career.getColumnName(5)));
		career.setBackground(alarmTableConfig.getBackGroundColor());
		JTableHeader tableHeader = career.getTableHeader();
		tableHeader.setBackground(alarmTableConfig.getHeaderBackGroundColor());
		tableHeader.setForeground(alarmTableConfig.getHeaderForeGroundColor());

		TableUtil.setColumnWidth(career, 0, DATE_FIELD_STRING);
		TableUtil.setColumnWidth(career, 1, UNIT_FIELD_WIDTH);
		TableUtil.setColumnWidth(career, 3, STATUS_FIELD_STRING);
		SortColumnUtil.removeSortColumn(
				career,
				4,
				wifeApplet,
				STATUS_FIELD_STRING);
		JScrollPane sp = new JScrollPane(career);
		add(sp, BorderLayout.CENTER);
	}

	/**
	 * 先頭カラムから n カラムを削除します。
	 * 
	 * @param table 対象のテーブル
	 * @param removeColumnCount 削除するカラム数
	 */
	private void removeColumns(JTable table, int removeColumnCount) {
		for (int i = removeColumnCount - 1; i >= 0; i--) {
			table.removeColumn(table.getColumn(table.getColumnName(0)));
		}
	}

	/**
	 * 対象のテーブルに AlarmTableCellRenderer を設定します。
	 * 
	 * @param table 対象のテーブル
	 */
	private void setAlarmTableCellRenderer(JTable table) {
		AlarmTableCellRenderer cellRecderer = new AlarmTableCellRenderer();
		for (int i = table.getColumnCount(); i > 0; i--) {
			DefaultTableColumnModel cmodel = (DefaultTableColumnModel) table
					.getColumnModel();
			TableColumn column = cmodel.getColumn(i - 1);
			column.setCellRenderer(cellRecderer);
		}
	}

	public void addTableModelListener(TableModelListener l) {
		career.getModel().addTableModelListener(l);
	}
}
