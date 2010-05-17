/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/event/LoggingDataEventTest.java,v 1.5.6.1 2004/11/29 07:12:52 frdm Exp $
 * $Revision: 1.5.6.1 $
 * $Date: 2004/11/29 07:12:52 $
 * 
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
package org.F11.scada.server.event;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.F11.scada.test.util.TestUtil;

/**
 * ロギングデータ変更イベントのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class LoggingDataEventTest extends TestCase {
	LoggingDataEvent event;
	Timestamp currentTime;
	List list;

	/**
	 * Constructor for LoggingDataEventTest.
	 * @param arg0
	 */
	public LoggingDataEventTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		list = Arrays.asList(TestUtil.createDataProvider().getDataHolders());
		currentTime = new Timestamp(System.currentTimeMillis());
		event = new LoggingDataEvent(this, currentTime, list);
	}

	public void testGetTimeStamp() {
		assertEquals(currentTime, event.getTimeStamp());
		assertSame(currentTime, event.getTimeStamp());
	}

	public void testGetHolders() {
		assertEquals(list, event.getHolders());
		assertSame(list, event.getHolders());
	}
	
	public void testCreateNullEvent() {
		try {
			new LoggingDataEvent(this, null, list);
			fail();
		} catch (IllegalArgumentException ex) {
		}

		try {
			new LoggingDataEvent(this, currentTime, null);
			fail();
		} catch (IllegalArgumentException ex) {
		}
	}

}
