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

package org.F11.scada.server.io.nio.dbms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.F11.scada.server.dao.DatabaseMetaDataUtil;
import org.F11.scada.util.ClassUtil;
import org.F11.scada.util.ResourceUtil;

public abstract class DbmsManager {
	private static Map dbmses = new HashMap();

	static {
		Properties dbmsClasses = ResourceUtil.getProperties("/org/F11/scada/server/io/nio/dbms/dbms.properties");
		for (Iterator i = dbmsClasses.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			String productName = (String) entry.getKey();
			String className = (String) entry.getValue();
			Dbms dbms = (Dbms) ClassUtil.newInstance(className);
			dbmses.put(productName, dbms);
		}
	}
	
	public static Dbms getDbms() {
		String db = DatabaseMetaDataUtil.getDatabaseProductName();
		if (dbmses.containsKey(db)) {
			return (Dbms) dbmses.get(db);
		} else {
			throw new IllegalStateException("未対応のデータベースプロダクトです (" + db + ")");
		}
	}
}
