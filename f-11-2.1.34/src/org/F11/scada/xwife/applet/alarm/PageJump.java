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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.F11.scada.xwife.applet.PageChangeEvent;

public class PageJump extends MouseAdapter {
	/** 警報一覧表の参照です。 */
	private JTable table;
	/** メインアプレットの参照 */
	private AbstractWifeApplet wifeApplet;
	/** ダブルクリックを無視する列 */
	private final int[] nonActionColumn;

	/**
	 * コンストラクタ。
	 * 対象の一覧表に対して、ジャンプリスナーを登録します。
	 * @param table 警報一覧表の参照です。
	 * @param wifeApplet メインアプレットの参照
	 */
	public PageJump(JTable table, AbstractWifeApplet wifeApplet) {
		this(table, wifeApplet, new int[0]);
	}

	public PageJump(JTable table, AbstractWifeApplet wifeApplet, int[] nonActionColumn) {
		this.table = table;
		this.wifeApplet = wifeApplet;
		this.table.addMouseListener(this);
		this.table.setColumnSelectionAllowed(false);
		this.table.setRowSelectionAllowed(false);
		this.nonActionColumn = nonActionColumn;
		Arrays.sort(this.nonActionColumn);
	}

	/**
	 * 一覧表のダブルクリックを検知して検知して、ジャンプ対象のツリーパス文字列を
	 * テーブルモデルから求め、対象の操作ツリーに展開命令を発効します。
	 */
	public void mouseReleased(MouseEvent e) {
		if (e.getClickCount() == 2) {
			Point origin = e.getPoint();
			int row = table.rowAtPoint(origin);
			int column = table.columnAtPoint(origin);
			if (isPageJump(row, column)) {
				TableModel tm = (TableModel) table.getModel();
				Object o = tm.getValueAt(row, 0);
				if (null != o && o instanceof String) {
					String s = (String) o;
					PageChangeEvent che = new PageChangeEvent(this, s, false);
					wifeApplet.changePage(che);
				}
			}
		}
	}

	private boolean isPageJump(int row, int column) {
		return row != -1 && column != -1 && isCheckColumn(column);
	}

	private boolean isCheckColumn(int column) {
		return 0 > Arrays.binarySearch(nonActionColumn, column);
	}
}
