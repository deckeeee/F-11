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
import java.awt.FontMetrics;

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
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.F11.scada.xwife.server.AlarmDataProvider;

public class CareerPanel extends JPanel {
	private static final long serialVersionUID = -2749104266106306188L;
	private AlarmTable career;
	/** メインアプレットの参照です */
	private AbstractWifeApplet wifeApplet;
	/**
	 * 日付、記号、状態のカラムのサイズです。
	 */
	private int DATE_FIELD_WIDTH = 120;
	private int UNIT_FIELD_WIDTH = 150;
	private int STATS_FIELD_WIDTH = 70;
	
	public CareerPanel(AbstractWifeApplet wifeApplet) {
		super(new BorderLayout());
		this.wifeApplet = wifeApplet;
		addCareer(new AlarmDefine().getAlarmConfig().getAlarmTableConfig());
		setAlarmTableCellRenderer(career);
	}

	private void addCareer(AlarmTableConfig alarmTableConfig) {
		DataHolder dh = Manager.getInstance().findDataHolder(AlarmDataProvider.PROVIDER_NAME,
				AlarmDataProvider.CAREER);
		career = new AlarmTable(dh, wifeApplet, alarmTableConfig);
		career.setAutoCreateColumnsFromModel(false);
		//			removeColumns(career, 7);
		removeColumns(career, 11);
		career.setBackground(alarmTableConfig.getBackGroundColor());
		JTableHeader tableHeader = career.getTableHeader();
		tableHeader.setBackground(alarmTableConfig
				.getHeaderBackGroundColor());
		tableHeader.setForeground(alarmTableConfig
				.getHeaderForeGroundColor());
		FontMetrics metrics = career.getFontMetrics(career.getFont());
		DATE_FIELD_WIDTH = metrics.stringWidth("8888/88/88 88:88:88") + 8;
		STATS_FIELD_WIDTH = metrics.stringWidth("警報・状態") + 8;

		TableColumn tc = career.getColumn(career.getColumnName(0));
		tc.setPreferredWidth(DATE_FIELD_WIDTH);
		tc.setMaxWidth(tc.getPreferredWidth());
		tc = career.getColumn(career.getColumnName(1));
		tc.setPreferredWidth(UNIT_FIELD_WIDTH);
		tc.setMaxWidth(tc.getPreferredWidth());
		tc = career.getColumn(career.getColumnName(3));
		tc.setPreferredWidth(STATS_FIELD_WIDTH);
		tc.setMaxWidth(tc.getPreferredWidth());
		JScrollPane sp = new JScrollPane(career);
		add(sp, BorderLayout.CENTER);
	}

	/**
	 * 先頭カラムから n カラムを削除します。
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
