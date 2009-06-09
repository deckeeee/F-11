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
 *
 */

package org.F11.scada.applet.schedule;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.schedule.DefaultScheduleDialog;
import org.F11.scada.applet.symbol.HandCursorListener;
import org.F11.scada.applet.symbol.ReferencerOwnerSymbol;
import org.F11.scada.xwife.applet.PageChanger;

/**
 * �e�[�u�����̃X�P�W���[���r���[�N���X�ł��B
 */
public class TableScheduleView {
	/** �e�[�u���ł��B */
	private JTable table;
	/** �X�P�W���[�����f���̎Q�Ƃł� */
	private ScheduleModel scheduleModel;
	private final boolean isNonTandT;
	private final String pageId;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param scheduleModel �X�P�W���[���f�[�^���f���ł�
	 * @param isSort
	 */
	public TableScheduleView(
			ScheduleModel scheduleModel,
			boolean isSort,
			boolean isNonTandT,
			String pageId,
			boolean isLenient,
			PageChanger changer) {
		this.scheduleModel = scheduleModel;
		this.isNonTandT = isNonTandT;
		this.pageId = pageId;
		init(isSort, isNonTandT, isLenient, changer);
	}

	/**
	 * ����������
	 * 
	 * @param isSort
	 * @param isNonTandT
	 */
	private void init(
			boolean isSort,
			final boolean isNonTandT,
			boolean isLenient,
			PageChanger changer) {
		TableScheduleModel model =
			new TableScheduleModel(scheduleModel, isNonTandT);
		table = new ScheduleTable(model, scheduleModel);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.addMouseListener(new ScheduleTableListener(
			model,
			isSort,
			isNonTandT,
			isLenient,
			changer));
		DefaultTableCellRenderer renderer = new ScheduleCellRenderer();
		DefaultTableColumnModel dc =
			(DefaultTableColumnModel) table.getColumnModel();
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn column = dc.getColumn(i);
			column.setCellRenderer(renderer);
		}
		table
			.addMouseMotionListener(new TableHandCursorListener(scheduleModel));
	}

	/**
	 * �r���[�R���|�[�l���g��Ԃ��܂��B �X�N���[���y�C���ɍڂ��Ă��܂�
	 */
	public JComponent createView() {
		return new JScrollPane(table);
	}

	/**
	 * �c�[���o�[�R���|�[�l���g��Ԃ��܂��B
	 */
	public JComponent createToolBar() {
		return new ScheduleToolBar(scheduleModel, isNonTandT, pageId);
	}

	/**
	 * �e�[�u�����̃X�P�W���[���f�[�^���f�� �e�[�u���ŊǗ�����ׂɁAScheduleModel �I�u�W�F�N�g���e�[�u�����f���Ń��b�v���܂��B
	 */
	private static class TableScheduleModel extends AbstractTableModel
			implements PropertyChangeListener {
		private static final long serialVersionUID = 5809072037679501027L;
		/** �X�P�W���[�����f���̎Q�� */
		private ScheduleModel scheduleModel;
		/** �J�����^�C�g���̔z�� */
		private String[] title;
		/** ScheduleRowModel �̔z�� */
		private ScheduleRowModel[] rowModel;
		/** �����E�����̕\���L�� */
		private final boolean isNonTandT;

		public TableScheduleModel(
				ScheduleModel scheduleModel,
				boolean isNonTandT) {
			super();
			this.scheduleModel = scheduleModel;
			this.isNonTandT = isNonTandT;
			init();
		}

		private void init() {
			createRowModels();
			createTitles();
			scheduleModel.addPropertyChangeListener(this);
		}

		private void createRowModels() {
			rowModel = new ScheduleRowModel[scheduleModel.getPatternSize()];
			for (int i = 0; i < rowModel.length; i++) {
				rowModel[scheduleModel.getDayIndex(i)] =
					scheduleModel.getScheduleRowModel(i);
			}
		}

		private void createTitles() {
			title = new String[getColumnCount() * 2 + 1];
			title[0] = "����";
			for (int i = 1; i < getColumnCount() * 2 + 1; i++) {
				if ((i % 2) != 0) {
					title[i] = (i + 1) / 2 + "���ON";
				} else {
					title[i] = i / 2 + "���OFF";
				}
			}
		}

		public int getRowCount() {
			return isNonTandT
				? scheduleModel.getPatternSize() - 2
				: scheduleModel.getPatternSize();
		}

		public int getColumnCount() {
			return rowModel[0].getColumnCount() * 2 + 1;
		}

		public Object getValueAt(int row, int column) {
			if (isNonTandT) {
				row += 2;
			}
			// �f�[�^�s : ���ځF������ ���ڈȊO�FInteger
			// ��Ԃ��B
			if (column == 0) {
				return rowModel[row].getDayIndexName();
			} else {
				int dataColumn = (column - 1) / 2;
				if (column % 2 != 0) {
					return new Integer(rowModel[row].getOnTime(dataColumn));
				} else {
					return new Integer(rowModel[row].getOffTime(dataColumn));
				}
			}
		}

		public String getColumnName(int column) {
			return title[column];
		}

		public Class getColumnClass(int columnIndex) {
			return getValueAt(0, columnIndex).getClass();
		}

		public ScheduleRowModel getScheduleRowModel(int selectRow) {
			return rowModel[selectRow];
		}

		public void propertyChange(PropertyChangeEvent evt) {
			fireTableChanged(new TableModelEvent(this));
		}
	}

	/**
	 * �ꗗ�\��\�����郌���_���[�N���X�ł��B
	 */
	private static class ScheduleCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -496261334014109240L;

		public Component getTableCellRendererComponent(
				JTable table,
				Object value,
				boolean isSelected,
				boolean hasFocus,
				int row,
				int column) {
			if (value instanceof Integer) {
				Integer v = (Integer) value;
				ScheduleFormat fmt = new ScheduleFormat();
				setText(fmt.format(v));
				return this;
			} else {
				return super.getTableCellRendererComponent(
					table,
					value,
					isSelected,
					hasFocus,
					row,
					column);
			}
		}

		/**
		 * �X�P�W���[���f�[�^�̃t�H�[�}�b�^�[�N���X�ł��B �ȈՎ����ł��B
		 */
		private final static class ScheduleFormat {
			String format(Object o) {
				int number = ((Integer) o).intValue();
				DecimalFormat fmt = new DecimalFormat("00");
				int hour = number / 100;
				int minute = number % 100;
				return fmt.format(hour) + " : " + fmt.format(minute);
			}
		}
	}

	/**
	 * �e�[�u���̃N���b�N���Ď�����A�}�E�X���X�i�[�N���X�ł��B ���ڕ������N���b�N�����Ƃ��ɁA���ԕҏW�p�̃_�C�A���O��\�����܂��B
	 */
	private static class ScheduleTableListener extends MouseAdapter {
		private JDialog dialog;
		private TableScheduleModel tableScheduleModel;
		private final boolean isSort;
		private final boolean isNonTandT;
		private final boolean isLenient;
		private final PageChanger changer;

		ScheduleTableListener(
				TableScheduleModel tableScheduleModel,
				boolean isSort,
				boolean isNonTandT,
				boolean isLenient,
				PageChanger changer) {
			this.tableScheduleModel = tableScheduleModel;
			this.isSort = isSort;
			this.isNonTandT = isNonTandT;
			this.isLenient = isLenient;
			this.changer = changer;
		}

		public void mousePressed(MouseEvent e) {
			if (tableScheduleModel.scheduleModel.isEditable()) {
				JTable table = (JTable) e.getSource();
				int row = table.rowAtPoint(e.getPoint());
				if (isNonTandT) {
					row += 2;
				}
				int column = table.columnAtPoint(e.getPoint());
				Object o = table.getValueAt(row, column);

				if (o instanceof String) {
					if (dialog != null) {
						dialog.dispose();
					}

					Frame frame = WifeUtilities.getParentFrame(table);
					TableScheduleModel model =
						(TableScheduleModel) table.getModel();
					dialog =
						new DefaultScheduleDialog(
							frame,
							model.getScheduleRowModel(row),
							isSort,
							isLenient,
							changer);
					dialog.pack();
					// �N���b�N���ꂽ�Z�����A�_�C�A���O�\���ʒu���Z�o�B
					Rectangle r = table.getCellRect(row, column, false);
					Point t = table.getLocationOnScreen();
					r.translate(t.x, t.y);
					r.translate(table
						.getColumnModel()
						.getColumn(column)
						.getWidth(), table.getRowHeight());

					Dimension screenSize = frame.getToolkit().getScreenSize();

					Rectangle dialogBounds = dialog.getBounds();
					dialogBounds.setLocation(r.getLocation());
					dialog.setLocation(WifeUtilities.getInScreenPoint(
						screenSize,
						dialogBounds.getBounds()));
					dialog.show();
				}
			}
		}
	}

	private static class ScheduleTable extends JTable implements
			ReferencerOwnerSymbol, ActionListener {
		private static final long serialVersionUID = -1538845369494455721L;
		private final ScheduleModel scheduleModel;
		private final Timer timer;

		ScheduleTable(TableModel tableModel, ScheduleModel scheduleModel) {
			super(tableModel);
			this.scheduleModel = scheduleModel;
			timer = new Timer(1000, this);
			timer.start();
		}

		public void disConnect() {
			scheduleModel.disConnect();
			timer.removeActionListener(this);
			timer.stop();
		}

		public void actionPerformed(ActionEvent e) {
			repaint();
		}
	}

	private static class TableHandCursorListener extends MouseMotionAdapter {
		private final ScheduleModel scheduleModel;

		public TableHandCursorListener(ScheduleModel scheduleModel) {
			this.scheduleModel = scheduleModel;
		}

		public void mouseMoved(MouseEvent e) {
			if (HandCursorListener.handcursor) {
				JTable table = (JTable) e.getComponent();
				final int column = table.columnAtPoint(e.getPoint());
				final Component comp = (Component) e.getSource();
				if (0 == column) {
					if (scheduleModel.isEditable()) {
						comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
					} else {
						comp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
				} else {
					comp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		}
	}
}
