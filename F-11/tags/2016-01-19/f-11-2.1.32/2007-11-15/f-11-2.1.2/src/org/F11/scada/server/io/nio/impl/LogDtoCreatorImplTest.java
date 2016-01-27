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

package org.F11.scada.server.io.nio.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.WifeException;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.communicater.CommunicaterFactory;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.entity.MultiRecordDefine;
import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.event.WifeEventListener;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.test.util.TimestampUtil;
import org.apache.commons.collections.primitives.DoubleList;
import org.seasar.extension.unit.S2TestCase;

public class LogDtoCreatorImplTest extends S2TestCase {
	public LogDtoCreatorImplTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLogDtoCreatorImpl() throws Exception {
		LogDtoCreatorImpl dtoCre = new LogDtoCreatorImpl(new TestItemUtil(), new TestCommunicaterFactory());
		Timestamp ts = TimestampUtil.parse("2006/01/01 10:00:00");
		List list = dtoCre.getLogDtoList(createLoggingDataEvent(ts));
		assertNotNull(list);
		assertEquals(1, list.size());
		Object[] dto = (Object[]) list.get(0);
		assertEquals(ts, dto[0]);
		assertEquals(new Integer(0), dto[1]);
		assertEquals("P1_D_1900000_Digital", dto[2]);
		assertEquals(new Double(1.0D), dto[3]);
	}


	private LoggingDataEvent createLoggingDataEvent(Timestamp ts) {
		ArrayList list = new ArrayList();
		LoggingDataEvent evt = new LoggingDataEvent(this, ts, list);
		return evt;
	}


	private static class TestItemUtil implements ItemUtil {

		public ConvertValue[] createConvertValue(Collection holders, String tablename) {
			return null;
		}

		public Map createConvertValueMap(Collection holders, String tableTame) {
			return null;
		}

		public Map createConvertValueMap(Collection holders) {
			return null;
		}

		public Map createDateHolderValuesMap(Collection dataHolders, String tableName, MultiRecordDefine multiRecordDefine) {
			return null;
		}

		public DoubleList createHolderValue(Collection dataHolders, String tableName) {
			return null;
		}

		public Item getItem(HolderString dataHolder, Map itemPool) {
			return null;
		}

		public Map getItemMap(Item[] items) {
			HashMap map = new HashMap();
			ArrayList list = new ArrayList();
			list.add(getItem());
			map.put("P1", list);
			return map;
		}

		public Item[] getItems(Collection holders, Map itemPool) {
			return new Item[]{getItem()};
		}

		private Item getItem() {
			Item item = new Item();
	        item.setPoint(new Integer(0));
	        item.setProvider("P1");
	        item.setHolder("D_1900000_Digital");
	        item.setDataArgv("00");
	        item.setComCycle(0);
	        item.setComCycleMode(true);
	        item.setComMemoryAddress(100);
	        item.setComMemoryKinds(0);
	        item.setOffDelay(new Integer(10));
			return item;
		}
		
	}


	private static class TestCommunicaterFactory implements CommunicaterFactory {

		public Communicater createCommunicator(Environment device) throws Exception {
			return new TestCommunicater();
		}

		private static class TestCommunicater implements Communicater {

			public void addReadCommand(Collection commands) {
			}

			public void removeReadCommand(Collection commands) {
			}

			public void addWifeEventListener(WifeEventListener l) {
			}

			public void close() throws InterruptedException {
			}

			public void removeWifeEventListener(WifeEventListener l) {
			}

			public Map syncRead(Collection commands, boolean sameDataBalk) throws InterruptedException, IOException, WifeException {
				Iterator i = commands.iterator();
				HashMap map = new HashMap();
				WifeCommand wc = (WifeCommand) i.next();
				map.put(wc, new byte[]{(byte) 0x00, (byte) 0x01});
				return map;
			}

			public Map syncRead(Collection commands) throws InterruptedException, IOException, WifeException {
				return syncRead(commands, true);
			}

			public void syncWrite(Map commands) throws InterruptedException, IOException, WifeException {
			}
			
		}
	}
}
