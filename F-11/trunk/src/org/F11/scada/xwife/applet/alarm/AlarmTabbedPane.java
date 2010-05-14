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

import static org.F11.scada.util.TableUtil.removeColumn;
import static org.F11.scada.util.TableUtil.removeColumns;
import static org.F11.scada.util.TableUtil.setColumnWidth;

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
import org.F11.scada.util.AlarmTableTitleUtil;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.F11.scada.xwife.applet.AttributeNColumnUtil;
import org.F11.scada.xwife.applet.SortColumnUtil;
import org.F11.scada.xwife.applet.alarm.event.CheckTableListener;
import org.F11.scada.xwife.server.AlarmDataProvider;

public class AlarmTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 3237221180569339474L;

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
	/** �x��ꗗ�񕝊Ǘ��N���X */
	private final AlarmColumn alarmColumn;
	private final AlarmTableTitleUtil alarmTableTitleUtil =
		new AlarmTableTitleUtil();

	/**
	 * �v���C�x�[�g�ȃR���X�g���N�^�ł��B createAlarmTabbedPane ���g�p���ăC���X�^���X�𐶐����Ă��������B
	 */
	public AlarmTabbedPane(AbstractWifeApplet wifeApplet, int tabPlacement) {
		super(tabPlacement);
		this.wifeApplet = wifeApplet;
		this.alarmDefine = new AlarmDefine();
		alarmColumn = new AlarmColumn(wifeApplet.getConfiguration());
		createAlarmTables();
		setSelectedIndex(alarmDefine.getAlarmConfig().getAlarmTableConfig()
				.getDefaultTabNo());
	}

	/**
	 * �T�}���A�q�X�g���A�����e�[�u���𐶐����܂��B
	 */
	private void createAlarmTables() {
		AlarmTableConfig alarmTableConfig =
			alarmDefine.getAlarmConfig().getAlarmTableConfig();

		addSummary(alarmTableConfig);
		addHistory(alarmTableConfig);
		addCareer(alarmTableConfig);
		addOccurrence(alarmTableConfig);
		addNoncheck(alarmTableConfig);
	}

	private void addNoncheck(AlarmTableConfig alarmTableConfig) {
		if (wifeApplet.getConfiguration().getBoolean(
				"org.F11.scada.xwife.applet.alarm.noncheck", false)) {
			DataHolder dh =
				Manager.getInstance().findDataHolder(
						AlarmDataProvider.PROVIDER_NAME,
						AlarmDataProvider.NONCHECK);
			noncheck =
				new AlarmTable(dh, wifeApplet, alarmTableConfig,
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

			setColumnWidth(noncheck, "�����E�^�]", alarmColumn.getDateSize());
			setColumnWidth(noncheck, "�����E��~", alarmColumn.getDateSize());
			setColumnWidth(noncheck, "�L��", alarmColumn.getUnitSize());
			setColumnWidth(noncheck, "����", alarmColumn.getAttributeSize());
			setColumnWidth(noncheck, "���", alarmColumn.getSortSize());
			setColumnWidth(noncheck, "�m�F", alarmColumn.getCheckSize());
			if (AttributeNColumnUtil.isAttributeDisplay()) {
				setColumnWidth(noncheck, alarmTableTitleUtil
						.getAttributeString("����1"), alarmColumn
						.getAttributeNSize());
				setColumnWidth(noncheck, alarmTableTitleUtil
						.getAttributeString("����2"), alarmColumn
						.getAttributeNSize());
				setColumnWidth(noncheck, alarmTableTitleUtil
						.getAttributeString("����3"), alarmColumn
						.getAttributeNSize());
			} else {
				removeColumn(noncheck, alarmTableTitleUtil
						.getAttributeString("����1"));
				removeColumn(noncheck, alarmTableTitleUtil
						.getAttributeString("����2"));
				removeColumn(noncheck, alarmTableTitleUtil
						.getAttributeString("����3"));
			}
			if (!isShowAttributeColumn()) {
				removeColumn(noncheck, "����");
			}

			addTab("���m�F", null, new RowHeaderScrollPane(noncheck, wifeApplet),
					"���m�F");
			setAlarmTableCellRenderer(noncheck);
		}
	}

	private boolean isShowAttributeColumn() {
		return wifeApplet.getConfiguration().getBoolean(
				"org.F11.scada.xwife.applet.alarm.showAttributeColumn", true);
	}

	private void addOccurrence(AlarmTableConfig alarmTableConfig) {
		if (wifeApplet.getConfiguration().getBoolean(
				"org.F11.scada.xwife.applet.alarm.occurrence", false)) {
			DataHolder dh =
				Manager.getInstance().findDataHolder(
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

			setColumnWidth(occurrence, 0, alarmColumn.getDateSize());
			setColumnWidth(occurrence, 1, alarmColumn.getDateSize());
			setColumnWidth(occurrence, 2, alarmColumn.getUnitSize());
			setColumnWidth(occurrence, 4, alarmColumn.getAttributeSize());
			setColumnWidth(occurrence, 5, alarmColumn.getStatusSize());
			SortColumnUtil.removeSortColumn(occurrence, 6, wifeApplet,
					alarmColumn.getSortSize());
			if (AttributeNColumnUtil.isAttributeDisplay()) {
				setColumnWidth(occurrence, alarmTableTitleUtil
						.getAttributeString("����1"), alarmColumn
						.getAttributeNSize());
				setColumnWidth(occurrence, alarmTableTitleUtil
						.getAttributeString("����2"), alarmColumn
						.getAttributeNSize());
				setColumnWidth(occurrence, alarmTableTitleUtil
						.getAttributeString("����3"), alarmColumn
						.getAttributeNSize());
			} else {
				removeColumn(occurrence, alarmTableTitleUtil
						.getAttributeString("����1"));
				removeColumn(occurrence, alarmTableTitleUtil
						.getAttributeString("����2"));
				removeColumn(occurrence, alarmTableTitleUtil
						.getAttributeString("����3"));
			}
			if (!isShowAttributeColumn()) {
				removeColumn(occurrence, 4);
			}
			addTab("������", null,
					new RowHeaderScrollPane(occurrence, wifeApplet), "������");
			setAlarmTableCellRenderer(occurrence);
		}
	}

	private void addSummary(AlarmTableConfig alarmTableConfig) {
		DataHolder dh =
			Manager.getInstance().findDataHolder(
					AlarmDataProvider.PROVIDER_NAME, AlarmDataProvider.SUMMARY);
		summary = new AlarmTable(dh, wifeApplet, alarmTableConfig);
		summary.setAutoCreateColumnsFromModel(false);
		removeColumns(summary, 7);
		summary.setBackground(alarmTableConfig.getBackGroundColor());
		JTableHeader tableHeader = summary.getTableHeader();
		tableHeader.setBackground(alarmTableConfig.getHeaderBackGroundColor());
		tableHeader.setForeground(alarmTableConfig.getHeaderForeGroundColor());

		setColumnWidth(summary, 0, alarmColumn.getDateSize());
		setColumnWidth(summary, 1, alarmColumn.getDateSize());
		setColumnWidth(summary, 2, alarmColumn.getUnitSize());
		setColumnWidth(summary, 4, alarmColumn.getAttributeSize());
		setColumnWidth(summary, 5, alarmColumn.getStatusSize());
		SortColumnUtil.removeSortColumn(summary, 6, wifeApplet, alarmColumn
				.getSortSize());
		if (AttributeNColumnUtil.isAttributeDisplay()) {
			setColumnWidth(summary, alarmTableTitleUtil
					.getAttributeString("����1"), alarmColumn.getAttributeNSize());
			setColumnWidth(summary, alarmTableTitleUtil
					.getAttributeString("����2"), alarmColumn.getAttributeNSize());
			setColumnWidth(summary, alarmTableTitleUtil
					.getAttributeString("����3"), alarmColumn.getAttributeNSize());
		} else {
			removeColumn(summary, alarmTableTitleUtil.getAttributeString("����1"));
			removeColumn(summary, alarmTableTitleUtil.getAttributeString("����2"));
			removeColumn(summary, alarmTableTitleUtil.getAttributeString("����3"));
		}
		if (!isShowAttributeColumn()) {
			removeColumn(summary, "����");
		}

		addTab("�T�}��", null, new RowHeaderScrollPane(summary, wifeApplet), "�T�}��");
		setAlarmTableCellRenderer(summary);
	}

	private void addHistory(AlarmTableConfig alarmTableConfig) {
		DataHolder dh =
			Manager.getInstance().findDataHolder(
					AlarmDataProvider.PROVIDER_NAME, AlarmDataProvider.HISTORY);
		history =
			new AlarmTable(dh, wifeApplet, alarmTableConfig, new int[] { 4 });
		history.setAutoCreateColumnsFromModel(false);
		history.addMouseListener(new HistoryTableListener(wifeApplet));
		removeColumns(history, 7);
		history.setBackground(alarmTableConfig.getBackGroundColor());
		JTableHeader tableHeader = history.getTableHeader();
		tableHeader.setBackground(alarmTableConfig.getHeaderBackGroundColor());
		tableHeader.setForeground(alarmTableConfig.getHeaderForeGroundColor());

		setColumnWidth(history, "�����E�^�]", alarmColumn.getDateSize());
		setColumnWidth(history, "�����E��~", alarmColumn.getDateSize());
		setColumnWidth(history, "�L��", alarmColumn.getUnitSize());
		setColumnWidth(history, "����", alarmColumn.getAttributeSize());
		setColumnWidth(history, "�m�F", alarmColumn.getCheckSize());
		if (AttributeNColumnUtil.isAttributeDisplay()) {
			setColumnWidth(history, alarmTableTitleUtil
					.getAttributeString("����1"), alarmColumn.getAttributeNSize());
			setColumnWidth(history, alarmTableTitleUtil
					.getAttributeString("����2"), alarmColumn.getAttributeNSize());
			setColumnWidth(history, alarmTableTitleUtil
					.getAttributeString("����3"), alarmColumn.getAttributeNSize());
		} else {
			removeColumn(history, alarmTableTitleUtil.getAttributeString("����1"));
			removeColumn(history, alarmTableTitleUtil.getAttributeString("����2"));
			removeColumn(history, alarmTableTitleUtil.getAttributeString("����3"));
		}
		SortColumnUtil.removeSortColumn(history, 5, wifeApplet, alarmColumn
				.getSortSize());
		if (!isShowAttributeColumn()) {
			removeColumn(history, "����");
		}
		addTab("�q�X�g��", null, new RowHeaderScrollPane(history, wifeApplet),
				"�q�X�g��");
		setAlarmTableCellRenderer(history);
	}

	private void addCareer(AlarmTableConfig alarmTableConfig) {
		DataHolder dh =
			Manager.getInstance().findDataHolder(
					AlarmDataProvider.PROVIDER_NAME, AlarmDataProvider.CAREER);
		career = new AlarmTable(dh, wifeApplet, alarmTableConfig);
		career.setAutoCreateColumnsFromModel(false);
		removeColumns(career, 12);
		career.removeColumn(career.getColumn(career.getColumnName(9)));
		career.setBackground(alarmTableConfig.getBackGroundColor());
		JTableHeader tableHeader = career.getTableHeader();
		tableHeader.setBackground(alarmTableConfig.getHeaderBackGroundColor());
		tableHeader.setForeground(alarmTableConfig.getHeaderForeGroundColor());

		setColumnWidth(career, 0, alarmColumn.getDateSize());
		setColumnWidth(career, 1, alarmColumn.getUnitSize());
		setColumnWidth(career, 3, alarmColumn.getAttributeSize());
		setColumnWidth(career, 4, alarmColumn.getStatusSize());
		SortColumnUtil.removeSortColumn(career, 5, wifeApplet, alarmColumn
				.getSortSize());
		if (AttributeNColumnUtil.isAttributeDisplay()) {
			setColumnWidth(career, alarmTableTitleUtil
					.getAttributeString("����1"), alarmColumn.getAttributeNSize());
			setColumnWidth(career, alarmTableTitleUtil
					.getAttributeString("����2"), alarmColumn.getAttributeNSize());
			setColumnWidth(career, alarmTableTitleUtil
					.getAttributeString("����3"), alarmColumn.getAttributeNSize());
		} else {
			removeColumn(career, alarmTableTitleUtil.getAttributeString("����1"));
			removeColumn(career, alarmTableTitleUtil.getAttributeString("����2"));
			removeColumn(career, alarmTableTitleUtil.getAttributeString("����3"));
		}
		if (!isShowAttributeColumn()) {
			removeColumn(career, "����");
		}
		addTab("����", null, new RowHeaderScrollPane(career, wifeApplet), "����");
		setAlarmTableCellRenderer(career);
	}

	/**
	 * �Ώۂ̃e�[�u���� AlarmTableCellRenderer ��ݒ肵�܂��B
	 *
	 * @param table �Ώۂ̃e�[�u��
	 */
	private void setAlarmTableCellRenderer(JTable table) {
		AlarmTableCellRenderer cellRecderer = new AlarmTableCellRenderer();
		for (int i = table.getColumnCount(); i > 0; i--) {
			DefaultTableColumnModel cmodel =
				(DefaultTableColumnModel) table.getColumnModel();
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
