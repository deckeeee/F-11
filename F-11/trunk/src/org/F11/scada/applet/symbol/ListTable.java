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

package org.F11.scada.applet.symbol;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.applet.symbol.table.GroupableTableHeader;
import org.F11.scada.util.TableUtil;
import org.F11.scada.util.ThreadUtil;
import org.apache.log4j.Logger;

/**
 * 一覧表示のテーブルクラスです。
 * 
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class ListTable extends JTable implements SymbolCollection,
		ReferencerOwnerSymbol, ActionListener {
	private static final long serialVersionUID = -704161366081491152L;
	private WifeDialog dialog;
	private static Logger log = Logger.getLogger(ListTable.class);
	private static boolean isCustomTipLocation;
	static {
		ClientConfiguration configuration = new ClientConfiguration();
		isCustomTipLocation = configuration.getBoolean(
				"xwife.applet.Applet.customTipLocation",
				false);
	}

	/** 点滅用のタイマーです */
	private WifeTimer timer = WifeTimer.getInstance();

	public ListTable(TableSymbol model) {
		super(model);
		setDefaultRenderer(JLabel.class, new ColorCellRenderer());
		addMouseListener(new MouseAdapter() {
			private int row;
			private int column;

			public void mousePressed(MouseEvent e) {
				row = rowAtPoint(e.getPoint());
				column = columnAtPoint(e.getPoint());
			}

			public void mouseReleased(MouseEvent e) {
				if (row == rowAtPoint(e.getPoint())
						&& column == columnAtPoint(e.getPoint())) {
					this_mouseClicked(e);
				}
			}
		});
		addMouseMotionListener(new TableHandCursorListener());
		setTableHeader(createDefaultTableHeader());
		timer.addActionListener(this);
	}

	/**
	 * グルーピング可能なテーブルヘッドクラスを返すようオーバーライドされています。
	 * 
	 * @return グルーピング可能なテーブルヘッドクラスオブジェクト
	 */
	protected JTableHeader createDefaultTableHeader() {
		return new GroupableTableHeader(super.columnModel);
	}

	/**
	 * マウスクリックイベント
	 */
	private void this_mouseClicked(MouseEvent e) {
		final int row = rowAtPoint(e.getPoint());
		final int column = columnAtPoint(e.getPoint());
		final Frame frame = WifeUtilities.getParentFrame(this);
		Object o = getValueAt(row, column);

		if (o instanceof Editable) {
			final Editable edit = (Editable) o;
			if (edit.isEditable()) {
				final SymbolCollection collec = (SymbolCollection) this;
				if (dialog != null) {
					dialog.dispose();
				}
				List para = new ArrayList();
				para.add(edit.getClass());
				para.add(new Integer(row));
				para.add(new Integer(column));
				dialog = edit.getDialog(frame, collec, para);
//				dialog.selectAll();
				dialog.show();
			}
		} else if (TrendJumpButton.class.isInstance(o)) {
			TrendJumpButton b = (TrendJumpButton) o;
			b.setDialogLocation(TableUtil.getDialogPoint(this, row, column));
			b.changePage();
		}
	}

	public ListIterator listIterator(List para) {
		return new TableListIterator(this, (Class) para.get(0), (Integer) para
				.get(1), (Integer) para.get(2));
	}

	/**
	 * 一覧表を表示するレンダラークラスです。
	 */
	static class ColorCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 7509694798950592301L;

		public Component getTableCellRendererComponent(
				JTable table,
				Object value,
				boolean isSelected,
				boolean hasFocus,
				int row,
				int column) {
			if (value instanceof JLabel) {
				JLabel iconSymbol = (JLabel) value;

				if (iconSymbol.isVisible()) {
					return iconSymbol;
				} else {
					setIcon(null);
					setText(null);
					setForeground(table.getForeground());
					setBackground(table.getBackground());
				}
				return this;
			} else {
				setIcon(null);
				return super.getTableCellRendererComponent(
						table,
						value,
						isSelected,
						hasFocus,
						row,
						column);
			}
		}
	}

	private static final class TableListIterator implements ListIterator {
		private List symbols;
		private ListIterator listIterator;
		private boolean isPreviousMode;

		TableListIterator(
				ListTable table,
				Class symbolClass,
				Integer row,
				Integer column) {
			symbols = new ArrayList(table.getRowCount()
					* table.getColumnCount());

			for (int i = 0, currentRow = row.intValue(), currentColumn = column
					.intValue(); i < table.getRowCount()
					* table.getColumnCount(); i++) {
				Object o = table.getValueAt(currentRow, currentColumn);
				if (symbolClass.isInstance(o) && o instanceof Editable) {
					Editable edit = (Editable) o;
					Symbol s = (Symbol) edit;
					if (edit.isEditable() && (s.isVisible() || s.isBlink())) {
						edit.setPoint(TableUtil.getDialogPoint(
								table,
								currentRow,
								currentColumn));
						symbols.add(edit);
					}
				}

				if ((table.getColumnCount() - 1) > currentColumn) {
					currentColumn++;
				} else {
					currentColumn = 0;
					if ((table.getRowCount() - 1) > currentRow) {
						currentRow++;
					} else {
						currentRow = 0;
					}
				}
			}
		}

		public boolean hasNext() {
			return true;
		}

		public Object next() {
			if (listIterator == null)
				listIterator = symbols.listIterator();

			if (isPreviousMode) {
				isPreviousMode = false;
				try {
					listIterator.next();
				} catch (NoSuchElementException ex) {
					listIterator = symbols.listIterator(symbols.size());
					listIterator.next();
				}
			}

			try {
				return listIterator.next();
			} catch (NoSuchElementException ex) {
				listIterator = symbols.listIterator();
				return listIterator.next();
			}
		}

		public boolean hasPrevious() {
			return true;
		}

		public Object previous() {
			if (listIterator == null)
				listIterator = symbols.listIterator(symbols.size());
			if (!isPreviousMode) {
				isPreviousMode = true;
				try {
					listIterator.previous();
				} catch (NoSuchElementException ex) {
					listIterator = symbols.listIterator(symbols.size());
					listIterator.previous();
				}
			}

			try {
				return listIterator.previous();
			} catch (NoSuchElementException ex) {
				listIterator = symbols.listIterator(symbols.size());
				return listIterator.previous();
			}
		}

		public int nextIndex() {
			int index = listIterator.nextIndex();
			if (isPreviousMode && index == symbols.size()) {
				ListIterator lit = symbols.listIterator();
				index = lit.nextIndex();
			}
			return index;
		}

		public int previousIndex() {
			int index = listIterator.previousIndex();
			if (!isPreviousMode && index < 0) {
				ListIterator lit = symbols.listIterator(symbols.size());
				index = lit.previousIndex();
			}
			return index;
		}

		public void add(Object o) {
			// non suport
			throw new UnsupportedOperationException();
		}

		public void remove() {
			// non suport
			throw new UnsupportedOperationException();
		}

		public void set(Object o) {
			// non suport
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent)
	 */
	public Point getToolTipLocation(MouseEvent event) {
		JTable table = (JTable) event.getComponent();
		final int row = table.rowAtPoint(event.getPoint());
		final int column = table.columnAtPoint(event.getPoint());
		Object obj = table.getValueAt(row, column);
		if (obj instanceof Symbol) {
			Symbol symbol = (Symbol) obj;
			if (null == symbol.getToolTipText()
					|| "".equals(symbol.getToolTipText())) {
				return null;
			}
		}
		Rectangle r = table.getCellRect(row, column, false);
		if (isCustomTipLocation) {
			Point p = new Point(event.getX() + 15, r.y + r.height + 5);
			log.debug("ToolTipLocation : " + p);
			return p;
		} else {
			Point p = new Point(r.x + 15, r.y + r.height + 5);
			log.debug("ToolTipLocation : " + p);
			return p;
		}
	}

	public JToolTip createToolTip() {
		return new YellowToolTip(this);
	}

	public void disConnect() {
		timer.removeActionListener(this);
		for (int i = 0, row = getRowCount(); i < row; i++) {
			for (int j = 0, column = getColumnCount(); j < column; j++) {
				Object obj = getValueAt(i, j);
				if (obj instanceof ReferencerOwnerSymbol) {
					ReferencerOwnerSymbol symbol = (ReferencerOwnerSymbol) obj;
					if (log.isDebugEnabled()) {
						log.debug("disConnect : " + symbol);
					}
					symbol.disConnect();
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		try {
			TableSymbol symbol = (TableSymbol) getModel();
			symbol.fireTableDataChanged();
		} catch (Exception ex) {
			log.error("例外発生:", ex);
			ThreadUtil.printSS();
		}
	}

	private static class TableHandCursorListener extends MouseMotionAdapter {

		public void mouseMoved(MouseEvent e) {
			if (HandCursorListener.handcursor) {
				Object obj = getTableObject(e);
				Component comp = (Component) e.getSource();
				if (obj instanceof Editable || obj instanceof TrendJumpButton) {
					comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
				} else {
					comp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		}

		private Object getTableObject(MouseEvent e) {
			JTable table = (JTable) e.getComponent();
			final int row = table.rowAtPoint(e.getPoint());
			final int column = table.columnAtPoint(e.getPoint());
			return table.getValueAt(row, column);
		}
	}

}
