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
import java.util.Collection;
import java.util.Iterator;

import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.HolderString;
import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.impl.AbstractDao;

/**
 * ItemArrayDao‚ÌŽÀ‘•ƒNƒ‰ƒX
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ItemArrayDaoImpl extends AbstractDao implements ItemArrayDao {
//    private static Logger logger = Logger.getLogger(ItemArrayDaoImpl.class);


    /**
     * @param daoMetaDataFactory
     */
    public ItemArrayDaoImpl(DaoMetaDataFactory daoMetaDataFactory) {
        super(daoMetaDataFactory);
    }
    
    /* (non-Javadoc)
     * @see org.F11.scada.server.dao.ItemArrayDao#getItem(java.util.Collection)
     */
    public Item[] getItemsNonSystem(Collection holders) {
        if (holders.isEmpty()) {
            return new Item[0];
        }

        String placeHolder = createNonSystemPlaceHolder(holders);
        return (Item[]) getEntityManager().findArray(
                placeHolder,
                createProviderAndHolderString(holders));
    }

    /* (non-Javadoc)
     * @see org.F11.scada.server.dao.ItemArrayDao#getItems(java.util.Collection)
     */
    public Item[] getItems(Collection holders) {
        if (holders.isEmpty()) {
            return new Item[0];
        }

        String placeHolder = createPlaceHolder(holders);
        return (Item[]) getEntityManager().findArray(
                placeHolder,
                createProviderAndHolderString(holders));
    }

    String createPlaceHolder(Collection holders) {
        StringBuffer b = new StringBuffer(holders.size() * 2);
        for (Iterator i = holders.iterator(); i.hasNext();) {
            i.next();
            if (i.hasNext()) {
                b.append("(item_table.provider=? AND item_table.holder=?) OR ");
            } else {
                b.append("(item_table.provider=? AND item_table.holder=?)");
            }
        }
        b.append(" ORDER BY item_table.provider, item_table.holder");
        return b.toString();
    }

    String createNonSystemPlaceHolder(Collection holders) {
        StringBuffer b = new StringBuffer(holders.size() * 2);
        for (Iterator i = holders.iterator(); i.hasNext();) {
            i.next();
            if (i.hasNext()) {
                b.append("(item_table.provider=? AND item_table.holder=? AND item_table.system = '0') OR ");
            } else {
                b.append("(item_table.provider=? AND item_table.holder=? AND item_table.system = '0')");
            }
        }
        b.append(" ORDER BY item_table.provider, item_table.holder");
        return b.toString();
    }
    
    String[] createProviderAndHolderString(Collection holders) {
        ArrayList providerList = new ArrayList(holders.size() * 4);
        for (Iterator i = holders.iterator(); i.hasNext();) {
            HolderString hs = (HolderString) i.next();
            providerList.add(hs.getProvider());
            providerList.add(hs.getHolder());
        }
        return (String[]) providerList.toArray(new String[0]);
    }
}
