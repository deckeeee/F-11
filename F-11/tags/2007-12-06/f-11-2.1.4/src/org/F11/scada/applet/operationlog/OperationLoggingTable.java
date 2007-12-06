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
 */
package org.F11.scada.applet.operationlog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.F11.scada.applet.symbol.GraphicManager;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OperationLoggingTable extends JPanel {
	private static final long serialVersionUID = 2817593802937592223L;
	private static final String DATE_WIDTH_STRING = "8888/88/88 88:88:88";
	private static final String IP_WIDTH_STRING = "255.255.255.255";
	private static final String USER_WIDTH_STRING = "HOGEHOGEHOGE";
	private static final String UNIT_WIDTH_STRING = "HOGEHOGEHOGE";
	private JButton foword;
	private JButton back;
	private JLabel page;

	public OperationLoggingTable(final OperationLoggingTableModel model) {
		super(new BorderLayout());
		foword = new JButton(GraphicManager
				.get("/toolbarButtonGraphics/navigation/Forward24.gif"));
		foword.setPreferredSize(new Dimension(32, 32));
		foword.setToolTipText("次ページ");
		back = new JButton(GraphicManager
				.get("/toolbarButtonGraphics/navigation/Back24.gif"));
		back.setPreferredSize(new Dimension(32, 32));
		back.setToolTipText("前ページ");
		page = new JLabel(format(model.getCurrentPage(), model.getAllPage()));

		foword.addActionListener(new FowordAction(model));
		back.addActionListener(new BackAction(model));
		JTable table = new JTable(model);
		removeTableColumn(table, 0);
		FontMetrics metrics = table.getFontMetrics(table.getFont());
		setPreferredWidth(table, 0, metrics.stringWidth(DATE_WIDTH_STRING) + 8);
		setPreferredWidth(table, 1, metrics.stringWidth(IP_WIDTH_STRING) + 8);
		setPreferredWidth(table, 2, metrics.stringWidth(USER_WIDTH_STRING) + 8);
		setPreferredWidth(table, 5, metrics.stringWidth(UNIT_WIDTH_STRING) + 8);

		Box opeBox = Box.createHorizontalBox();
		opeBox.add(Box.createHorizontalGlue());
		opeBox.add(back);
		opeBox.add(page);
		opeBox.add(foword);
		opeBox.add(Box.createHorizontalGlue());
		add(opeBox, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);

		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				page
						.setText(format(model.getCurrentPage(), model
								.getAllPage()));
			}
		});
	}

	private void removeTableColumn(JTable table, int columnNo) {
		TableColumnModel cm = table.getColumnModel();
		TableColumn column = cm.getColumn(columnNo); // id column
		table.removeColumn(column);
	}

	private void setPreferredWidth(JTable table, int columnNo, int width) {
		TableColumn tc = table.getColumn(table.getColumnName(columnNo));
		tc.setPreferredWidth(width);
		tc.setMaxWidth(tc.getPreferredWidth());
	}

	private String format(int current, int all) {
		return all == 0 ? " / " : current + "/" + all;
	}

	private static class FowordAction implements ActionListener {
		private OperationLoggingTableModel model;

		FowordAction(OperationLoggingTableModel model) {
			this.model = model;
		}

		public void actionPerformed(ActionEvent e) {
			model.next();
		}
	}

	private static class BackAction implements ActionListener {
		private OperationLoggingTableModel model;

		BackAction(OperationLoggingTableModel model) {
			this.model = model;
		}

		public void actionPerformed(ActionEvent e) {
			model.previous();
		}
	}
}
