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

package org.F11.scada.test.util;

import org.F11.scada.server.entity.Item;
import org.seasar.dao.unit.S2DaoTestCase;

public class ItemDaoTest extends S2DaoTestCase {
	private ItemDao dao;

	public ItemDaoTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		include("ItemDaoTest.dicon");
	}

	/*
	 * Test method for 'org.F11.scada.test.util.ItemDao.insert(Item)'
	 */
	public void testInsertTx() throws Exception {
		Item item = new Item();
		item.setPoint(new Integer(0));
		item.setProvider("P1");
		item.setHolder("H0");
		item.setComCycle(0);
		item.setComCycleMode(true);
		item.setComMemoryKinds(0);
		item.setComMemoryAddress(0);
		item.setBFlag(false);
		item.setMessageId(0);
		item.setAttributeId(0);
		item.setDataType(0);
		item.setDataArgv("0");
		item.setSystem(true);
		dao.insert(item);
	}

}
