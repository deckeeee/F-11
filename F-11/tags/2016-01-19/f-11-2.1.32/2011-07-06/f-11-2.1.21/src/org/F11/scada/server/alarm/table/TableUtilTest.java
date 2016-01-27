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

package org.F11.scada.server.alarm.table;

import java.util.Date;

import javax.swing.table.DefaultTableModel;

import junit.framework.TestCase;

/**
 * TableUtil�̃e�X�g�P�[�X
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TableUtilTest extends TestCase {
	private DefaultTableModel model;
	private Date now;

	/**
	 * Constructor for TableUtilTest.
	 * @param arg0
	 */
	public TableUtilTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		Object[] title = {"point", "�L��", "����", "����"};
		now = new Date();
		Object[][] data ={
			{new Integer(0), "�L��A", "����A", now},
			{new Integer(1), "�L��B", "����B", now},
			{new Integer(2), "�L��C", "����C", now},
			{new Integer(1), "�L��B", "����B", now},
		};
		
		model = new DefaultTableModel(data, title);
	}

	public void testSetPoint() {
		// �ύX�O���e�X�g
		assertEquals(new Integer(0), model.getValueAt(0, 0));
		assertEquals("�L��A", model.getValueAt(0, 1));
		assertEquals("����A", model.getValueAt(0, 2));
		assertEquals(now, model.getValueAt(0, 3));

		assertEquals(new Integer(1), model.getValueAt(1, 0));
		assertEquals("�L��B", model.getValueAt(1, 1));
		assertEquals("����B", model.getValueAt(1, 2));
		assertEquals(now, model.getValueAt(1, 3));

		assertEquals(new Integer(2), model.getValueAt(2, 0));
		assertEquals("�L��C", model.getValueAt(2, 1));
		assertEquals("����C", model.getValueAt(2, 2));
		assertEquals(now, model.getValueAt(2, 3));

		assertEquals(new Integer(1), model.getValueAt(3, 0));
		assertEquals("�L��B", model.getValueAt(3, 1));
		assertEquals("����B", model.getValueAt(3, 2));
		assertEquals(now, model.getValueAt(3, 3));

		PointTableBean a = new PointTableBean(1, "�L��B", "����B", "");
		PointTableBean b = new PointTableBean(1, "�L��2", "����2", "");
		TableUtil.setPoint(model, b, a);

		// �ύX����e�X�g
		assertEquals(new Integer(0), model.getValueAt(0, 0));
		assertEquals("�L��A", model.getValueAt(0, 1));
		assertEquals("����A", model.getValueAt(0, 2));
		assertEquals(now, model.getValueAt(0, 3));

		assertEquals(new Integer(1), model.getValueAt(1, 0));
		assertEquals("�L��2", model.getValueAt(1, 1));
		assertEquals("����2", model.getValueAt(1, 2));
		assertEquals(now, model.getValueAt(1, 3));

		assertEquals(new Integer(2), model.getValueAt(2, 0));
		assertEquals("�L��C", model.getValueAt(2, 1));
		assertEquals("����C", model.getValueAt(2, 2));
		assertEquals(now, model.getValueAt(2, 3));

		assertEquals(new Integer(1), model.getValueAt(3, 0));
		assertEquals("�L��2", model.getValueAt(3, 1));
		assertEquals("����2", model.getValueAt(3, 2));
		assertEquals(now, model.getValueAt(3, 3));
	}

	public void testSetPointEscape() throws Exception {
		setPointEscape("\\");
		setPointEscape("+");
		setPointEscape("*");
		setPointEscape("[");
		setPointEscape("]");
		setPointEscape("{");
		setPointEscape("}");
		setPointEscape("?");
		setPointEscape("(");
		setPointEscape(")");
		setPointEscape("^");
		setPointEscape("$");
		setPointEscape("|");

		setPointEscape("=");
		setPointEscape("-");
		setPointEscape("!");
		setPointEscape("\"");
		setPointEscape("'");
		setPointEscape("%");
		setPointEscape("&");
		setPointEscape("<");
		setPointEscape(">");
	}

	private void setPointEscape(String s) throws Exception {
		PointTableBean a = new PointTableBean(1, "�L��B", "����B", "");
		PointTableBean b = new PointTableBean(1, "�L��2" + s, "����2" + s, "");
		TableUtil.setPoint(model, b, a);

		// �ύX����e�X�g
		assertEquals(new Integer(0), model.getValueAt(0, 0));
		assertEquals("�L��A", model.getValueAt(0, 1));
		assertEquals("����A", model.getValueAt(0, 2));
		assertEquals(now, model.getValueAt(0, 3));

		assertEquals(new Integer(1), model.getValueAt(1, 0));
		assertEquals("�L��2" + s, model.getValueAt(1, 1));
		assertEquals("����2" + s, model.getValueAt(1, 2));
		assertEquals(now, model.getValueAt(1, 3));

		assertEquals(new Integer(2), model.getValueAt(2, 0));
		assertEquals("�L��C", model.getValueAt(2, 1));
		assertEquals("����C", model.getValueAt(2, 2));
		assertEquals(now, model.getValueAt(2, 3));

		assertEquals(new Integer(1), model.getValueAt(3, 0));
		assertEquals("�L��2" + s, model.getValueAt(3, 1));
		assertEquals("����2" + s, model.getValueAt(3, 2));
		assertEquals(now, model.getValueAt(3, 3));

		TableUtil.setPoint(model, a, b);

		assertEquals(new Integer(0), model.getValueAt(0, 0));
		assertEquals("�L��A", model.getValueAt(0, 1));
		assertEquals("����A", model.getValueAt(0, 2));
		assertEquals(now, model.getValueAt(0, 3));

		assertEquals(new Integer(1), model.getValueAt(1, 0));
		assertEquals("�L��B", model.getValueAt(1, 1));
		assertEquals("����B", model.getValueAt(1, 2));
		assertEquals(now, model.getValueAt(1, 3));

		assertEquals(new Integer(2), model.getValueAt(2, 0));
		assertEquals("�L��C", model.getValueAt(2, 1));
		assertEquals("����C", model.getValueAt(2, 2));
		assertEquals(now, model.getValueAt(2, 3));

		assertEquals(new Integer(1), model.getValueAt(3, 0));
		assertEquals("�L��B", model.getValueAt(3, 1));
		assertEquals("����B", model.getValueAt(3, 2));
		assertEquals(now, model.getValueAt(3, 3));
	}
}
