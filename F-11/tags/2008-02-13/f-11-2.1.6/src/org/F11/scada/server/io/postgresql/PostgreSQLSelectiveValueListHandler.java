/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/postgresql/Attic/PostgreSQLSelectiveValueListHandler.java,v 1.1.2.2 2006/02/09 01:09:18 frdm Exp $
 * $Revision: 1.1.2.2 $
 * $Date: 2006/02/09 01:09:18 $
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

package org.F11.scada.server.io.postgresql;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.F11.scada.data.LoggingRowData;
import org.F11.scada.server.io.SelectHandler;
import org.F11.scada.server.io.SelectiveValueListHandlerElement;

/**
 * ロギングデータのハンドラクラスです。
 * 定義されたロギングデータをデータストレージより読みとります。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PostgreSQLSelectiveValueListHandler
		implements SelectiveValueListHandlerElement {

    private final String name;
    private final SelectHandler handler;

    public PostgreSQLSelectiveValueListHandler(String name, SelectHandler handler) {
        this.name = name;
        this.handler = handler;
    }
    
    public SortedMap getInitialData(List holderStrings) {
        TreeMap map = new TreeMap();
        try {
            List list = handler.select(name, holderStrings);
    		for (Iterator it = list.iterator(); it.hasNext(); ) {
    			LoggingRowData data = (LoggingRowData) it.next();
    			map.put(data.getTimestamp(), data.getList());
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public SortedMap getInitialData(List holderStrings, int limit) {
        TreeMap map = new TreeMap();
        try {
            List list = handler.select(name, holderStrings, limit);
    		for (Iterator it = list.iterator(); it.hasNext(); ) {
    			LoggingRowData data = (LoggingRowData) it.next();
    			map.put(data.getTimestamp(), data.getList());
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map getUpdateLoggingData(Timestamp key, List holderStrings) {
        Map map = new HashMap();
        try {
            List list = handler.select(name, holderStrings, key);
    		for (Iterator it = list.iterator(); it.hasNext(); ) {
    			LoggingRowData data = (LoggingRowData) it.next();
    			map.put(data.getTimestamp(), data.getList());
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
