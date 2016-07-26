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
	/** �x��ꗗ�\�̎Q�Ƃł��B */
	private JTable table;
	/** ���C���A�v���b�g�̎Q�� */
	private AbstractWifeApplet wifeApplet;
	/** �_�u���N���b�N�𖳎������ */
	private final int[] nonActionColumn;

	/**
	 * �R���X�g���N�^�B
	 * �Ώۂ̈ꗗ�\�ɑ΂��āA�W�����v���X�i�[��o�^���܂��B
	 * @param table �x��ꗗ�\�̎Q�Ƃł��B
	 * @param wifeApplet ���C���A�v���b�g�̎Q��
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
	 * �ꗗ�\�̃_�u���N���b�N�����m���Č��m���āA�W�����v�Ώۂ̃c���[�p�X�������
	 * �e�[�u�����f�����狁�߁A�Ώۂ̑���c���[�ɓW�J���߂𔭌����܂��B
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
