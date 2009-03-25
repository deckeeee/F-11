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

package org.F11.scada.applet.ngraph;


import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.F11.scada.applet.ngraph.event.GraphChangeEvent;
import org.F11.scada.util.TableUtil;

public class SeriesTable extends JTable implements Mediator, Colleague {
	private final Mediator mediator;
	private TableListener tableListener;
	private TableKeyListener tableKeyListener;

	public SeriesTable(TableModel model, Mediator mediator) {
		super(model);
		this.mediator = mediator;
		init();
		setListener();
	}

	private void init() {
		final Font font = new Font("Monospaced", Font.PLAIN, 18);
		setFont(font);
		FontMetrics metrics = getFontMetrics(font);
		setRowHeight(metrics.getHeight());
		setColorCellRenderer();
		setDoubleCellRenderer(5);
		setDoubleCellRenderer(6);
		setUnitMarkCellRenderer();
		setButtonRenderer();
		JTableHeader header = getTableHeader();
		header.setFont(font);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableModel model = getModel();
		TableUtil.setColumnWidth(this, 0, model.getColumnName(0));
		TableUtil.setColumnWidth(this, 1, model.getColumnName(1));
		TableUtil.setColumnWidth(this, 2, model.getColumnName(2));
		TableUtil.setColumnWidth(this, 3, 120);
		TableUtil.setColumnWidth(this, 5, 80);
		TableUtil.setColumnWidth(this, 6, 80);
		TableUtil.setColumnWidth(this, 7, 60);
		setRowSelectionInterval(0, 0);
	}

	private void setColorCellRenderer() {
		TableColumn column = getColumn(getModel().getColumnName(1));
		column.setCellRenderer(TableUtil.getColorTableCellRenderer());
	}

	private void setDoubleCellRenderer(int columnNo) {
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 6983574027499560694L;

			@Override
			protected void setValue(Object value) {
				if (value instanceof String) {
					setHorizontalAlignment(RIGHT);
					super.setValue(value);
				}
			}
		};
		TableColumn column = getColumn(getModel().getColumnName(columnNo));
		column.setCellRenderer(renderer);
	}

	private void setUnitMarkCellRenderer() {
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 950013017797970834L;

			@Override
			protected void setValue(Object value) {
				if (value instanceof String) {
					super.setValue(String.format("[%s]", value));
				} else {
					super.setValue("[---]");
				}
			}
		};
		TableColumn column = getColumn(getModel().getColumnName(7));
		column.setCellRenderer(renderer);
	}

	private void setButtonRenderer() {
		TableModel model = getModel();
		getColumn(model.getColumnName(2)).setCellRenderer(
			new ButtonTableCellRenderer());
		getColumn(model.getColumnName(2)).setCellEditor(
			new ButtonTableCellEditor(new JCheckBox()));
	}

	private void setListener() {
		tableListener = new TableListener(this);
		addMouseListener(tableListener);
		tableKeyListener = new TableKeyListener(this);
		addKeyListener(tableKeyListener);
	}

	public void performColleagueChange(GraphChangeEvent e) {
	}

	public void colleaguChanged(Colleague colleague) {
		if (tableListener == colleague) {
			tableListener.performColleagueChange(getGraphChangeEvent());
		} else if (tableKeyListener == colleague) {
			tableKeyListener.performColleagueChange(getGraphChangeEvent());
		}
		mediator.colleaguChanged(this);
	}

	public GraphChangeEvent getGraphChangeEvent() {
		return mediator.getGraphChangeEvent();
	}

	private static class TableListener extends MouseAdapter implements
			Colleague {
		private final Mediator mediator;
		private MouseEvent event;

		public TableListener(Mediator mediator) {
			this.mediator = mediator;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			event = e;
			mediator.colleaguChanged(this);
		}

		public void performColleagueChange(GraphChangeEvent e) {
			JTable table = (JTable) event.getSource();
			e.getView().setSelectSeries(table.rowAtPoint(event.getPoint()));
		}
	}

	private static class TableKeyListener extends KeyAdapter implements
			Colleague {
		private final Mediator mediator;
		private KeyEvent event;

		public TableKeyListener(Mediator mediator) {
			this.mediator = mediator;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			event = e;
			mediator.colleaguChanged(this);
		}

		public void performColleagueChange(GraphChangeEvent e) {
			JTable table = (JTable) event.getSource();
			e.getView().setSelectSeries(table.getSelectedRow());
		}
	}
}
