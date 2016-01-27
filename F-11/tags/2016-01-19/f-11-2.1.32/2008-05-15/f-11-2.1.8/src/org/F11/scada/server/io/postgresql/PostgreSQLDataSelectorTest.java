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
 */
package org.F11.scada.server.io.postgresql;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.F11.scada.data.LoggingRowData;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.test.util.TimestampUtil;
import org.F11.scada.util.ConnectionUtil;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.seasar.framework.container.S2Container;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class PostgreSQLDataSelectorTest extends DatabaseTestCase {

    /*
     * @see DatabaseTestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see DatabaseTestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

	protected IDatabaseConnection getConnection() throws Exception {
		return new DatabaseConnection(ConnectionUtil.getConnection());
	}

	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(new FileInputStream("src/org/F11/scada/server/io/postgresql/database.xml"));
	}

    /**
     * Constructor for PostgreSQLDataSelectorTest.
     * @param arg0
     */
    public PostgreSQLDataSelectorTest(String arg0) {
        super(arg0);
    }

    public void testGetSelectData() throws Exception {
        PostgreSQLDataSelector selector = new PostgreSQLDataSelector();
	    S2Container container = S2ContainerUtil.getS2Container();
	    ItemUtil util = (ItemUtil) container.getComponent("itemutil");

	    ArrayList l = new ArrayList();
        l.add(new HolderString("P1", "D_501_BcdSingle"));
        l.add(new HolderString("P1", "D_502_BcdSingle"));

        Map converValueMap = util.createConvertValueMap(l);
        
		String sql = "SELECT TOP 1 f_date, f_P1_D_501_BcdSingle, f_P1_D_502_BcdSingle FROM log_table_monthly ORDER BY f_date ASC";
		List data = selector.getSelectData("log_table_monthly", l, sql, converValueMap);
		assertNotNull(data);
		assertEquals(1, data.size());
		LoggingRowData ld = (LoggingRowData) data.get(0);
		assertEquals(TimestampUtil.parse("2004/05/01 00:01:00"), ld.getTimestamp());
		assertEquals(52.5D, ld.getDouble(0), 0.01);
		assertEquals(55.0D, ld.getDouble(1), 0.01);
    }

}
