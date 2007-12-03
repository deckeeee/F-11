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

package org.F11.scada.server.dao;

import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.HolderString;
import org.apache.log4j.Logger;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * ItemDaoのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ItemDaoTest extends S2DaoTestCase {
	private final Logger logger = Logger.getLogger(ItemDaoTest.class);
    private ItemDao itemDao;

    /**
     * @param arg0
     */
    public ItemDaoTest(String arg0) {
        super(arg0);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        include("ItemDao.dicon");
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(ItemDaoTest.class);
    }

    public void testGetSystemItemsTx() {
    	Item update = itemDao.selectItem("P2", "D_1900000_Digital");
    	update.setSystem(true);
    	itemDao.updateItem(update);
    	
        Item[] items = itemDao.getSystemItems("P2", true);
        assertNotNull(items);
        assertEquals(1, items.length);
        Item item = items[0];
        assertEquals(new Integer(0), item.getPoint());
        assertEquals("P2", item.getProvider());
        assertEquals("D_1900000_Digital", item.getHolder());
        assertEquals(0, item.getComCycle());
        assertTrue(item.isComCycleMode());
        assertEquals(0, item.getComMemoryKinds());
        assertEquals(10000, item.getComMemoryAddress());
        assertFalse(item.isBFlag());
        assertEquals(0, item.getMessageId());
        assertEquals(0, item.getAttributeId());
        assertEquals(0, item.getDataType());
        assertEquals("0", item.getDataArgv());
        assertEquals("計測一覧表", item.getJumpPath());
        assertTrue(item.isAutoJumpFlag());
        assertEquals(0, item.getAutoJumpPriority());
        assertEquals("", item.getOnSoundPath());
        assertEquals("", item.getOffSoundPath());
        assertNull(item.getAnalogTypeId());
        assertNull(item.getEmailGroupId());
        assertNull(item.getEmailSendMode());
        assertNull(item.getOffDelay());
        assertTrue(item.isSystem());
        for (int i = 0; i < items.length; i++) {
            logger.info(items[i]);
            assertTrue(items[i].isSystem());
        }
    }
    
    public void testGetItemTx() throws Exception {
    	Item update = itemDao.selectItem("P2", "D_1900000_Digital");
    	update.setSystem(true);
    	itemDao.updateItem(update);

    	HolderString hs = new HolderString();
        hs.setProvider("P2");
        hs.setHolder("D_1900000_Digital");
        Item item = itemDao.getItem(hs);
        assertEquals("D_1900000_Digital", item.getHolder());
        assertEquals(0, item.getComCycle());
        assertTrue(item.isComCycleMode());
        assertEquals(0, item.getComMemoryKinds());
        assertEquals(10000, item.getComMemoryAddress());
        assertFalse(item.isBFlag());
        assertEquals(0, item.getMessageId());
        assertEquals(0, item.getAttributeId());
        assertEquals(0, item.getDataType());
        assertEquals("0", item.getDataArgv());
        assertEquals("計測一覧表", item.getJumpPath());
        assertTrue(item.isAutoJumpFlag());
        assertEquals(0, item.getAutoJumpPriority());
        assertEquals("", item.getOnSoundPath());
        assertEquals("", item.getOffSoundPath());
        assertNull(item.getAnalogTypeId());
        assertNull(item.getEmailGroupId());
        assertNull(item.getEmailSendMode());
        assertNull(item.getOffDelay());
        assertTrue(item.isSystem());
        System.out.println(item);
        assertTrue(item.isSystem());
    }

    public void testGetNoSystemItemsTx() {
        Item[] items = itemDao.getNoSystemItems();
        assertNotNull(items);
        for (int i = 0; i < items.length; i++) {
            logger.info(items[i]);
            assertFalse(items[i].isSystem());
        }
    }
}
