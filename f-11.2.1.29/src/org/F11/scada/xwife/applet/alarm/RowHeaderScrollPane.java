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

package org.F11.scada.xwife.applet.alarm;

import java.awt.Component;
import java.awt.FontMetrics;
import java.text.DecimalFormat;
import java.text.Format;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.xwife.applet.AbstractWifeApplet;

/**
 * 行ヘッダーつきのスクロールペインです。
 *
 * @author maekawa
 *
 */
public class RowHeaderScrollPane extends JScrollPane {
	private static final long serialVersionUID = -8851432987048936042L;

	public RowHeaderScrollPane(JTable table, AbstractWifeApplet wifeApplet) {
		this(table, Integer.parseInt(EnvironmentManager.get(
				"/server/alarm/maxrow", "5000")), wifeApplet);
	}

	public RowHeaderScrollPane(
			JTable table,
			int headerMax,
			AbstractWifeApplet wifeApplet) {
		super(table);
		JList rowHeader = new JList(new RowHeaderListModel(table));
		int rowHeaderWidth =
			wifeApplet.getConfiguration().getInt(
					"org.F11.scada.xwife.applet.alarm.rowheader.width",
					AlarmColumn.ROW_HEADER_SIZE);
		rowHeader.setFixedCellWidth(rowHeaderWidth);
		rowHeader.setFixedCellHeight(table.getRowHeight()
			+ table.getRowMargin()
			- 1);
		rowHeader.setCellRenderer(new RowHeaderRenderer(table, wifeApplet));
		setRowHeaderView(rowHeader);
	}

	public RowHeaderScrollPane(JTable table, int headerMax, String format) {
		super(table);
		JList rowHeader = new JList(new RowHeaderListModel(table));
		FontMetrics metrics = table.getFontMetrics(table.getFont());
		int width = metrics.stringWidth(format) + 8;
		rowHeader.setFixedCellWidth(width);
		rowHeader.setFixedCellHeight(table.getRowHeight()
			+ table.getRowMargin()
			- 1);
		rowHeader.setCellRenderer(new RowHeaderRenderer(table, format));
		setRowHeaderView(rowHeader);
	}

	private static class RowHeaderListModel extends AbstractListModel {
		private static final long serialVersionUID = -5819486827292315938L;
		private final TableModel model;

		public RowHeaderListModel(JTable table) {
			model = table.getModel();
		}

		public Object getElementAt(int index) {
			return new Integer(index);
		}

		public int getSize() {
			return model.getRowCount();
		}
	}

	private static class RowHeaderRenderer extends JLabel implements
			ListCellRenderer {
		private static final long serialVersionUID = 6437070695891031555L;
		private final String format;

		RowHeaderRenderer(JTable table, AbstractWifeApplet wifeApplet) {
			this(table, wifeApplet.getConfiguration().getString(
					"org.F11.scada.xwife.applet.alarm.rowheader.format",
					AlarmColumn.ROW_HEADER_FORMAT));
		}

		RowHeaderRenderer(JTable table, String format) {
			JTableHeader header = table.getTableHeader();
			setOpaque(true);
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setHorizontalAlignment(CENTER);
			setForeground(header.getForeground());
			setBackground(header.getBackground());
			setFont(header.getFont());
			this.format = format;
		}

		public Component getListCellRendererComponent(
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {
			Format f = new DecimalFormat(format);
			setText(f.format(index + 1));
			return this;
		}
	}
}
