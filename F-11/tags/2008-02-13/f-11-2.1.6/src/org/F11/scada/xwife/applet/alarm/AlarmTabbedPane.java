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

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.parser.alarm.AlarmDefine;
import org.F11.scada.parser.alarm.AlarmTableConfig;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.util.TableUtil;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.F11.scada.xwife.applet.SortColumnUtil;
import org.F11.scada.xwife.applet.alarm.event.CheckTableListener;
import org.F11.scada.xwife.server.AlarmDataProvider;

public class AlarmTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 3237221180569339474L;
	private static final String STATUS_FIELD_STRING = "�x��E���";
	private static final String DATE_FIELD_STRING = "8888/88/88 88:88:88";
	/**
	 * ���t�A�L���A��Ԃ̃J�����̃T�C�Y�ł��B
	 */
	private static final int UNIT_FIELD_WIDTH = 150;

	/** �T�}���A�q�X�g���A�����̃e�[�u���ł��B */
	private AlarmTable summary;
	private AlarmTable history;
	private AlarmTable career;
	private AlarmTable occurrence;
	private AlarmTable noncheck;
	/** ���C���A�v���b�g�̎Q�Ƃł� */
	private AbstractWifeApplet wifeApplet;
	/** �x��ݒ�v���p�e�B�I�u�W�F�N�g�ł� */
	private AlarmDefine alarmDefine;

	/**
	 * �v���C�x�[�g�ȃR���X�g���N�^�ł��B createAlarmTabbedPane ���g�p���ăC���X�^���X�𐶐����Ă��������B
	 */
	public AlarmTabbedPane(AbstractWifeApplet wifeApplet, int tabPlacement) {
		super(tabPlacement);
		this.wifeApplet = wifeApplet;
		this.alarmDefine = new AlarmDefine();
		createAlarmTables();
		setSelectedIndex(alarmDefine.getAlarmConfig().getAlarmTableConfig()
				.getDefaultTabNo());
	}

	/**
	 * �T�}���A�q�X�g���A�����e�[�u���𐶐����܂��B
	 */
	private void createAlarmTables() {
		AlarmTableConfig alarmTableConfig = alarmDefine.getAlarmConfig()
				.getAlarmTableConfig();

		addSummary(alarmTableConfig);
		addHistory(alarmTableConfig);
		addCareer(alarmTableConfig);
		addOccurrence(alarmTableConfig);
		addNoncheck(alarmTableConfig);
	}

	private void addNoncheck(AlarmTableConfig alarmTableConfig) {
		if (wifeApplet.getConfiguration().getBoolean(
				"org.F11.scada.xwife.applet.alarm.noncheck",
				false)) {
			DataHolder dh = Manager.getInstance().findDataHolder(
					AlarmDataProvider.PROVIDER_NAME,
					AlarmDataProvider.NONCHECK);
			noncheck = new AlarmTable(
					dh,
					wifeApplet,
					alarmTableConfig,
					new int[] { 5 });
			noncheck.setAutoCreateColumnsFromModel(false);
			noncheck.addMouseListener(new NoncheckTableListener(wifeApplet));
			removeColumns(noncheck, 7);
			noncheck.setBackground(alarmTableConfig.getBackGroundColor());
			JTableHeader tableHeader = noncheck.getTableHeader();
			tableHeader.setBackground(alarmTableConfig
					.getHeaderBackGroundColor());
			tableHeader.setForeground(alarmTableConfig
					.getHeaderForeGroundColor());

			TableUtil.setColumnWidth(noncheck, 0, DATE_FIELD_STRING);
			TableUtil.setColumnWidth(noncheck, 1, DATE_FIELD_STRING);
			TableUtil.setColumnWidth(noncheck, 2, UNIT_FIELD_WIDTH);
			TableUtil.setColumnWidth(noncheck, 4, STATUS_FIELD_STRING);
			TableUtil.setColumnWidth(noncheck, 5, STATUS_FIELD_STRING);

			JScrollPane sp = new JScrollPane(noncheck);
			addTab("���m�F", null, sp, "���m�F");
			setAlarmTableCellRenderer(noncheck);
		}
	}

	private void addOccurrence(AlarmTableConfig alarmTableConfig) {
		if (wifeApplet.getConfiguration().getBoolean(
				"org.F11.scada.xwife.applet.alarm.occurrence",
				false)) {
			DataHolder dh = Manager.getInstance().findDataHolder(
					AlarmDataProvider.PROVIDER_NAME,
					AlarmDataProvider.OCCURRENCE);
			occurrence = new AlarmTable(dh, wifeApplet, alarmTableConfig);
			occurrence.setAutoCreateColumnsFromModel(false);
			removeColumns(occurrence, 7);
			occurrence.setBackground(alarmTableConfig.getBackGroundColor());
			JTableHeader tableHeader = occurrence.getTableHeader();
			tableHeader.setBackground(alarmTableConfig
					.getHeaderBackGroundColor());
			tableHeader.setForeground(alarmTableConfig
					.getHeaderForeGroundColor());

			TableUtil.setColumnWidth(occurrence, 0, DATE_FIELD_STRING);
			TableUtil.setColumnWidth(occurrence, 1, DATE_FIELD_STRING);
			TableUtil.setColumnWidth(occurrence, 2, UNIT_FIELD_WIDTH);
			TableUtil.setColumnWidth(occurrence, 4, STATUS_FIELD_STRING);
			SortColumnUtil.removeSortColumn(
					occurrence,
					5,
					wifeApplet,
					STATUS_FIELD_STRING);
			JScrollPane sp = new JScrollPane(occurrence);
			addTab("������", null, sp, "������");
			setAlarmTableCellRenderer(occurrence);
		}
	}

	private void addSummary(AlarmTableConfig alarmTableConfig) {
		DataHolder dh = Manager.getInstance().findDataHolder(
				AlarmDataProvider.PROVIDER_NAME,
				AlarmDataProvider.SUMMARY);
		summary = new AlarmTable(dh, wifeApplet, alarmTableConfig);
		summary.setAutoCreateColumnsFromModel(false);
		removeColumns(summary, 7);
		summary.setBackground(alarmTableConfig.getBackGroundColor());
		JTableHeader tableHeader = summary.getTableHeader();
		tableHeader.setBackground(alarmTableConfig.getHeaderBackGroundColor());
		tableHeader.setForeground(alarmTableConfig.getHeaderForeGroundColor());

		TableUtil.setColumnWidth(summary, 0, DATE_FIELD_STRING);
		TableUtil.setColumnWidth(summary, 1, DATE_FIELD_STRING);
		TableUtil.setColumnWidth(summary, 2, UNIT_FIELD_WIDTH);
		TableUtil.setColumnWidth(summary, 4, STATUS_FIELD_STRING);
		SortColumnUtil.removeSortColumn(
				summary,
				5,
				wifeApplet,
				STATUS_FIELD_STRING);
		JScrollPane sp = new JScrollPane(summary);
		addTab("�T�}��", null, sp, "�T�}��");
		setAlarmTableCellRenderer(summary);
	}

	private void addHistory(AlarmTableConfig alarmTableConfig) {
		DataHolder dh = Manager.getInstance().findDataHolder(
				AlarmDataProvider.PROVIDER_NAME,
				AlarmDataProvider.HISTORY);
		history = new AlarmTable(
				dh,
				wifeApplet,
				alarmTableConfig,
				new int[] { 4 });
		history.setAutoCreateColumnsFromModel(false);
		history.addMouseListener(new HistoryTableListener(wifeApplet));
		removeColumns(history, 7);
		history.setBackground(alarmTableConfig.getBackGroundColor());
		JTableHeader tableHeader = history.getTableHeader();
		tableHeader.setBackground(alarmTableConfig.getHeaderBackGroundColor());
		tableHeader.setForeground(alarmTableConfig.getHeaderForeGroundColor());

		TableUtil.setColumnWidth(history, 0, DATE_FIELD_STRING);
		TableUtil.setColumnWidth(history, 1, DATE_FIELD_STRING);
		TableUtil.setColumnWidth(history, 2, UNIT_FIELD_WIDTH);
		TableUtil.setColumnWidth(history, 5, STATUS_FIELD_STRING);
		SortColumnUtil.removeSortColumn(
				history,
				4,
				wifeApplet,
				STATUS_FIELD_STRING);
		JScrollPane sp = new JScrollPane(history);
		addTab("�q�X�g��", null, sp, "�q�X�g��");
		setAlarmTableCellRenderer(history);
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
		addTab("����", null, sp, "����");
		setAlarmTableCellRenderer(career);
	}

	/**
	 * �擪�J�������� n �J�������폜���܂��B
	 * 
	 * @param table �Ώۂ̃e�[�u��
	 * @param removeColumnCount �폜����J������
	 */
	private void removeColumns(JTable table, int removeColumnCount) {
		for (int i = removeColumnCount - 1; i >= 0; i--) {
			table.removeColumn(table.getColumn(table.getColumnName(0)));
		}
	}

	/**
	 * �Ώۂ̃e�[�u���� AlarmTableCellRenderer ��ݒ肵�܂��B
	 * 
	 * @param table �Ώۂ̃e�[�u��
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

	public void addCheckTableListener(CheckTableListener l) {
		addHistoryCheckListener(l);
		addNoncheckListener(l);
	}

	private void addHistoryCheckListener(CheckTableListener l) {
		AlarmTableModel model = (AlarmTableModel) history.getModel();
		model.addCheckTableListener(l);
	}

	private void addNoncheckListener(CheckTableListener l) {
		if (null != noncheck) {
			AlarmTableModel model = (AlarmTableModel) noncheck.getModel();
			model.addCheckTableListener(l);
		}
	}
}
