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

import java.util.ArrayList;
import java.util.Collections;

import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.HolderString;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ItemArrayDaoImplTest extends S2DaoTestCase {
    private ItemArrayDao dao;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(ItemArrayDaoImplTest.class);
    }

    /**
     * Constructor for ItemArrayDaoImplTest.
     * @param arg0
     */
    public ItemArrayDaoImplTest(String arg0) {
        super(arg0);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        include("ItemArrayDao.dicon");
    }

    public void testGetItemsNonSystem() throws Exception {
        String[] providers = {"P1", "P1", "P1", "P1"};
       String[] holders = {"D_500_BcdSingle", "D_501_BcdSingle", "D_502_BcdSingle", "D_1900000_Digital"};
        ArrayList col = createList(providers, holders);

        Item[] items = dao.getItemsNonSystem(col);
        assertNotNull(items);
        assertEquals(3, items.length);
        for (int i = 0; i < items.length; i++)
            System.out.println(items[i]);

        String[] providers2 = {"P1"};
        String[] holders2 = {"D_1900000_Digital"};
        col = createList(providers2, holders2);
        Item[] items2 = dao.getItemsNonSystem(col);
        assertEquals(0, items2.length);
    }

    public void testGetItems() throws Exception {
        String[] providers = {"P1", "P1", "P1", "P1"};
        String[] holders = {"D_500_BcdSingle", "D_501_BcdSingle", "D_502_BcdSingle", "D_1900000_Digital"};
        ArrayList col = createList(providers, holders);

        Item[] items = dao.getItems(col);
        assertNotNull(items);
        assertEquals(4, items.length);
        for (int i = 0; i < items.length; i++)
            System.out.println(items[i]);

        String[] providers2 = {"P1"};
        String[] holders2 = {"D_1900000_Digital"};
        col = createList(providers2, holders2);
        Item[] items2 = dao.getItems(col);
        assertEquals(1, items2.length);
    }

    /**
     * @param providers
     * @param holders
     * @return
     */
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
    
    public void testEmptyCollection() throws Exception {
        Item[] items = dao.getItems(Collections.EMPTY_LIST);
        assertEquals(0, items.length);
    }


    public void testGetItemsMultiProvider() throws Exception {
        String[] providers = {"P1", "P1", "P1", "P2"};
        String[] holders = {"D_500_BcdSingle", "D_501_BcdSingle", "D_502_BcdSingle", "D_1900000_Digital"};
        ArrayList col = createList(providers, holders);

        Item[] items = dao.getItems(col);
        assertNotNull(items);
        assertEquals(4, items.length);
        for (int i = 0; i < items.length; i++)
            System.out.println(items[i]);

        String[] providers2 = {"P1"};
        String[] holders2 = {"D_1900000_Digital"};
        col = createList(providers2, holders2);
        Item[] items2 = dao.getItems(col);
        assertEquals(1, items2.length);
    }
}
