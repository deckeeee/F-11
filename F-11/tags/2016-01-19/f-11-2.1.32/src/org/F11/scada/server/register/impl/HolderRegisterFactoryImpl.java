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

package org.F11.scada.server.register.impl;

import java.util.HashMap;
import java.util.Map;

import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.HolderRegister;
import org.F11.scada.server.register.HolderRegisterFactory;

/**
 * HolderRegisterFactory‚ÌŽÀ‘•ƒNƒ‰ƒX
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class HolderRegisterFactoryImpl implements HolderRegisterFactory {
    private final Map registerMap;

    public HolderRegisterFactoryImpl() {
        this.registerMap = new HashMap();
    }

    public void putHolderRegister(int[] key, HolderRegister register) {
        for (int i = 0; i < key.length; i++) {
            registerMap.put(new Integer(key[i]), register);
        }
    }

    public HolderRegister getHolderRegister(Item item) {
        Integer key = new Integer(item.getDataType());
        if (registerMap.containsKey(key)) {
            return (HolderRegister) registerMap.get(key);
        } else {
    	    throw new IllegalArgumentException("data type : " + item.getDataType());
        }
    }
}
