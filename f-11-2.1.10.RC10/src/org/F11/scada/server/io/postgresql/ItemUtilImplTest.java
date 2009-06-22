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

package org.F11.scada.server.io.postgresql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.F11.scada.WifeException;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.communicater.CommunicaterFactory;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.event.WifeEventListener;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.register.HolderString;
import org.seasar.extension.unit.S2TestCase;

public class ItemUtilImplTest extends S2TestCase {
	private ItemUtil util;

	protected void setUp() throws Exception {
		include("ItemUtilImplTest.dicon");
		util = (ItemUtil) getComponent(ItemUtilImpl.class);
		assertNotNull(util);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreateConvertValue() throws Exception {
        String[] providers = { "P1", "P1", "P1", "P1" };
		String[] holders = { "D_501_BcdSingle", "D_500_BcdSingle",
				"D_502_BcdSingle", "D_503_BcdSingle" };
		ArrayList col = createList(providers, holders);
		ConvertValue[] cvs = util.createConvertValue(col, "log_table_minute");
		assertEquals(4, cvs.length);
		// Logging.xmlのカラム順でコンバーターが返される。
		assertEquals(0.0, cvs[0].getConvertMin(), 0);
		assertEquals(50.0, cvs[0].getConvertMax(), 0);
		assertEquals(0.0, cvs[1].getConvertMin(), 0);
		assertEquals(100.0, cvs[1].getConvertMax(), 0);
		assertEquals(0.0, cvs[2].getConvertMin(), 0);
		assertEquals(200.0, cvs[2].getConvertMax(), 0);
		assertEquals(0.0, cvs[3].getConvertMin(), 0);
		assertEquals(300.0, cvs[3].getConvertMax(), 0);
	}
	
	private ArrayList createList(String[] providers, String[] holders) {
        ArrayList col = new ArrayList();
        for (int i = 0; i < providers.length; i++) {
            HolderString hs = new HolderString();
            hs.setProvider(providers[i]);
            hs.setHolder(holders[i]);
            col.add(hs);
        }
        return col;
	}

	public static CommunicaterFactory getCommunicaterFactory() {
		return new TestCommunicaterFactory();
	}
	
	static class TestCommunicaterFactory implements CommunicaterFactory {
		public Communicater createCommunicator(Environment device) throws Exception {
			return null;
		}

		static class TestCommunicater implements Communicater {

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
				return null;
			}

			public Map syncRead(Collection commands) throws InterruptedException, IOException, WifeException {
				return null;
			}

			public void syncWrite(Map commands) throws InterruptedException, IOException, WifeException {
			}
			
		}
	}
}
