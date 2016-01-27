/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

import java.sql.Timestamp;

import javax.swing.event.TableModelEvent;

import junit.framework.TestCase;

import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.xwife.applet.PageChangeEvent;
import org.F11.scada.xwife.applet.PageChanger;
import org.F11.scada.xwife.applet.alarm.event.CheckEvent;
import org.F11.scada.xwife.applet.alarm.event.TestTableModel;
import org.apache.log4j.Logger;

public class PriorityControllerTest extends TestCase {

	/*
	 * カレントのテーブルモデルのテスト
	 */
	public void testTableChanged() {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		TestPageChanger changer = new TestPageChanger();
		PriorityController pc = new PriorityController(changer);
		Integer priority = new Integer(3);
		pc.tableChanged(getEvent(new TestTableModel("P1", "H1", ts, priority)));
		assertEquals("カレントモデルはP1_H1", "P1_H1", pc.getTableRowModel().getHolderId());
		assertTrue("じゃんぷ", changer.isPageChange);
		changer.resetPageChange();
		pc.tableChanged(getEvent(new TestTableModel("P1", "H2", ts, priority)));
		assertEquals("カレントモデルはP1_H2", "P1_H2", pc.getTableRowModel().getHolderId());
		assertTrue("じゃんぷ", changer.isPageChange);
		changer.resetPageChange();
		pc.tableChanged(getEvent(new TestTableModel(Boolean.FALSE, "P1", "H1", ts, priority)));
		assertEquals("カレントモデルはP1_H2", "P1_H2", pc.getTableRowModel().getHolderId());
		assertFalse("じゃんぷ無し", changer.isPageChange);
		changer.resetPageChange();
		pc.tableChanged(getEvent(new TestTableModel(Boolean.FALSE, "P1", "H2", ts, priority)));
		assertEquals("カレントモデルは_", "_", pc.getTableRowModel().getHolderId());
		assertFalse("じゃんぷ無し", changer.isPageChange);
		changer.resetPageChange();
	}

	private TableModelEvent getEvent(TestTableModel model) {
		return new TableModelEvent(model, 0, 0, 0, TableModelEvent.INSERT);
	}

	public void testCheckedEvent() throws Exception {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		PriorityController pc = new PriorityController(new TestPageChanger());
		Integer priority = new Integer(3);
		pc.tableChanged(getEvent(new TestTableModel("P1", "H1", ts, priority)));
		assertEquals("カレントモデルはP1_H1", "P1_H1", pc.getTableRowModel().getHolderId());
		pc.checkedEvent(new CheckEvent(
				getClass().getName(),
				new TestTableModel("P1", "H1", ts, priority),
				0,
				new Timestamp(System.currentTimeMillis())));
		assertEquals("カレントモデルは_", "_", pc.getTableRowModel().getHolderId());
	}

	public void testPriority() throws Exception {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		TestPageChanger changer = new TestPageChanger();
		PriorityController pc = new PriorityController(
				changer,
				new ClientConfiguration(
						"/org/F11/scada/xwife/applet/alarm/PriorityControllerTest.xml"));

		//復旧パターン
		pc.tableChanged(getEvent(new TestTableModel("P1", "H1", ts, new Integer(3))));
		assertTrue("じゃんぷ", changer.isPageChange);
		changer.resetPageChange();
		pc.tableChanged(getEvent(new TestTableModel("P1", "H2", ts, new Integer(1))));
		assertFalse("じゃんぷ無し", changer.isPageChange);
		changer.resetPageChange();
		pc.tableChanged(getEvent(new TestTableModel(Boolean.FALSE, "P1", "H1", ts, new Integer(3))));
		assertFalse("じゃんぷ無し", changer.isPageChange);
		changer.resetPageChange();
		pc.tableChanged(getEvent(new TestTableModel("P1", "H2", ts, new Integer(1))));
		assertTrue("じゃんぷ", changer.isPageChange);
		changer.resetPageChange();

		//確認パターン
		pc.tableChanged(getEvent(new TestTableModel("P1", "H1", ts, new Integer(3))));
		assertTrue("じゃんぷ", changer.isPageChange);
		changer.resetPageChange();
		pc.checkedEvent(new CheckEvent(
				getClass().getName(),
				new TestTableModel("P1", "H1", ts, new Integer(3)),
				0,
				new Timestamp(System.currentTimeMillis())));
		pc.tableChanged(getEvent(new TestTableModel("P1", "H2", ts, new Integer(2))));
		assertTrue("じゃんぷ", changer.isPageChange);
		changer.resetPageChange();
	}

	public void testNonPriority() throws Exception {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		TestPageChanger changer = new TestPageChanger();
		PriorityController pc = new PriorityController(changer);

		//復旧パターン
		pc.tableChanged(getEvent(new TestTableModel("P1", "H1", ts, new Integer(3))));
		assertTrue("じゃんぷ", changer.isPageChange);
		changer.resetPageChange();
		pc.tableChanged(getEvent(new TestTableModel("P1", "H2", ts, new Integer(1))));
		assertTrue("じゃんぷ", changer.isPageChange);
		changer.resetPageChange();
		pc.tableChanged(getEvent(new TestTableModel(Boolean.FALSE, "P1", "H1", ts, new Integer(3))));
		assertFalse("じゃんぷ無し", changer.isPageChange);
		changer.resetPageChange();
		pc.tableChanged(getEvent(new TestTableModel("P1", "H2", ts, new Integer(1))));
		assertTrue("じゃんぷ", changer.isPageChange);
		changer.resetPageChange();

		//確認パターン
		pc.tableChanged(getEvent(new TestTableModel("P1", "H1", ts, new Integer(3))));
		assertTrue("じゃんぷ", changer.isPageChange);
		changer.resetPageChange();
		pc.checkedEvent(new CheckEvent(
				getClass().getName(),
				new TestTableModel("P1", "H1", ts, new Integer(3)),
				0,
				new Timestamp(System.currentTimeMillis())));
		pc.tableChanged(getEvent(new TestTableModel("P1", "H2", ts, new Integer(2))));
		assertTrue("じゃんぷ", changer.isPageChange);
		changer.resetPageChange();
	}

	private static class TestPageChanger implements PageChanger {
		private final Logger logger = Logger.getLogger(TestPageChanger.class);
		volatile boolean isPageChange;

		public void changePage(PageChangeEvent pageChange) {
			logger.info(pageChange);
			isPageChange = true;
		}

		public boolean isDisplayLock() {
			return false;
		}

		public void playAlarm(String soundPath) {
			logger.info(soundPath);
		}

		public void pressShiftKey() {
			logger.info("pressShiftKey");
		}

		public void setDisplayLock(boolean isDisplayLock) {
			logger.info("" + isDisplayLock);
		}
		
		void resetPageChange() {
			isPageChange = false;
		}
		public void stopAlarm() {
		}
	}
}
