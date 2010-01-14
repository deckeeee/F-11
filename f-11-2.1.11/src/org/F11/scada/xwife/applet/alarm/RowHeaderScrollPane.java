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
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.apache.commons.configuration.Configuration;

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
			"/server/alarm/maxrow",
			"5000")), wifeApplet);
	}

	public RowHeaderScrollPane(
			JTable table,
			int headerMax,
			AbstractWifeApplet wifeApplet) {
		super(table);
		JList rowHeader = getList(headerMax);
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

	private JList getList(int maxRow) {
		Vector<Integer> lm = new Vector<Integer>(maxRow);
		for (int i = 0; i < maxRow; i++) {
			lm.add(i);
		}
		return new JList(lm);
	}

	private static class RowHeaderRenderer extends JLabel implements
			ListCellRenderer {
		private static final long serialVersionUID = 6437070695891031555L;
		private final Format format;

		RowHeaderRenderer(JTable table, AbstractWifeApplet wifeApplet) {
			JTableHeader header = table.getTableHeader();
			setOpaque(true);
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setHorizontalAlignment(CENTER);
			setForeground(header.getForeground());
			setBackground(header.getBackground());
			setFont(header.getFont());
			Configuration configuration = wifeApplet.getConfiguration();
			format =
				new DecimalFormat(configuration.getString(
					"org.F11.scada.xwife.applet.alarm.rowheader.format",
					AlarmColumn.ROW_HEADER_FORMAT));
		}

		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus) {
			setText(format.format(index + 1));
			return this;
		}
	}
}
