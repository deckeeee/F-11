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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.LoggingRowData;
import org.F11.scada.server.io.SQLUtility;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.test.util.TimestampUtil;
import org.F11.scada.util.ConnectionUtil;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class PostgreSQLSelectHandlerTest extends DatabaseTestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
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
     * Constructor for PostgreSQLSelectHandlerTest.
     * @param arg0
     */
    public PostgreSQLSelectHandlerTest(String arg0) {
        super(arg0);
    }

    /*
     * 制限件数内で全てのデータをセレクト
     */
    public void testSelectStringList() throws Exception {
        PostgreSQLSelectHandler handler = new PostgreSQLSelectHandler();
        handler.setPostgreSQLUtility(new TestHSQLUtility());
        ArrayList l = new ArrayList();
        l.add(new HolderString("P1", "D_501_BcdSingle"));
        l.add(new HolderString("P1", "D_502_BcdSingle"));
        List data = handler.select("log_table_monthly", l);

        assertNotNull(data);
		assertEquals(31, data.size());
		LoggingRowData ld = (LoggingRowData) data.get(0);
		assertEquals(TimestampUtil.parse("2004/05/31 00:01:00"), ld.getTimestamp());
		assertEquals(53.27D, ld.getDouble(0), 0.01);
		assertEquals(55.75D, ld.getDouble(1), 0.01);

		ld = (LoggingRowData) data.get(30);
		assertEquals(TimestampUtil.parse("2004/05/01 00:01:00"), ld.getTimestamp());
		assertEquals(52.5D, ld.getDouble(0), 0.01);
		assertEquals(55.0D, ld.getDouble(1), 0.01);
    }

    /*
     * 指定した日時以上のデータをセレクト
     */
    public void testSelectStringListTimestamp() throws Exception {
        PostgreSQLSelectHandler handler = new PostgreSQLSelectHandler();
        handler.setPostgreSQLUtility(new TestHSQLUtility());
        ArrayList l = new ArrayList();
        l.add(new HolderString("P1", "D_501_BcdSingle"));
        l.add(new HolderString("P1", "D_502_BcdSingle"));
        List data = handler.select("log_table_monthly", l, TimestampUtil.parse("2004/05/15 00:01:00"));

        assertNotNull(data);
		assertEquals(16, data.size());
		LoggingRowData ld = (LoggingRowData) data.get(0);
		assertEquals(TimestampUtil.parse("2004/05/31 00:01:00"), ld.getTimestamp());
		assertEquals(53.27D, ld.getDouble(0), 0.01);
		assertEquals(55.75D, ld.getDouble(1), 0.01);

		ld = (LoggingRowData) data.get(15);
		assertEquals(TimestampUtil.parse("2004/05/16 00:01:00"), ld.getTimestamp());
		assertEquals(52.875D, ld.getDouble(0), 0.01);
		assertEquals(55.375D, ld.getDouble(1), 0.01);
    }

    /*
     * 一番古いデータをセレクト
     */
    public void testFirst() throws Exception {
        PostgreSQLSelectHandler handler = new PostgreSQLSelectHandler();
        handler.setPostgreSQLUtility(new TestHSQLUtility());
        ArrayList l = new ArrayList();
        l.add(new HolderString("P1", "D_501_BcdSingle"));
        l.add(new HolderString("P1", "D_502_BcdSingle"));
        LoggingRowData ld = handler.first("log_table_monthly", l);

        assertNotNull(ld);
		assertEquals(TimestampUtil.parse("2004/05/01 00:01:00"), ld.getTimestamp());
		assertEquals(52.5D, ld.getDouble(0), 0.01);
		assertEquals(55.0D, ld.getDouble(1), 0.01);
    }
    
    /*
     * 一番新しいデータをセレクト
     */
    public void testLast() throws Exception {
        PostgreSQLSelectHandler handler = new PostgreSQLSelectHandler();
        handler.setPostgreSQLUtility(new TestHSQLUtility());
        ArrayList l = new ArrayList();
        l.add(new HolderString("P1", "D_501_BcdSingle"));
        l.add(new HolderString("P1", "D_502_BcdSingle"));
        LoggingRowData ld = handler.last("log_table_monthly", l);

        assertNotNull(ld);
		assertEquals(TimestampUtil.parse("2004/05/31 00:01:00"), ld.getTimestamp());
		assertEquals(53.27D, ld.getDouble(0), 0.01);
		assertEquals(55.75D, ld.getDouble(1), 0.01);
    }

    /*
     * 指定した日時の範囲のデータをセレクト
     */
    public void testSelect() throws Exception {
        PostgreSQLSelectHandler handler = new PostgreSQLSelectHandler();
        handler.setPostgreSQLUtility(new TestHSQLUtility());
        ArrayList l = new ArrayList();
        l.add(new HolderString("P1", "D_501_BcdSingle"));
        l.add(new HolderString("P1", "D_502_BcdSingle"));
        List data = handler.selectBeforeAfter("log_table_monthly", l, TimestampUtil.parse("2004/05/15 00:01:00"), 8);
        
        Comparator comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                LoggingData l1 = (LoggingData) o1;
                LoggingData l2 = (LoggingData) o2;
                
                Timestamp t1 = l1.getTimestamp();
                Timestamp t2 = l2.getTimestamp();

                return t1.compareTo(t2);
            }
        };
        
        Collections.sort(data, comparator);

        assertNotNull(data);
        System.out.println(data);
		assertEquals(16, data.size());
		LoggingRowData ld = (LoggingRowData) data.get(0);
		assertEquals(TimestampUtil.parse("2004/05/07 00:01:00"), ld.getTimestamp());
		assertEquals(52.650D, ld.getDouble(0), 0.01);
		assertEquals(55.15D, ld.getDouble(1), 0.01);

		ld = (LoggingRowData) data.get(15);
		assertEquals(TimestampUtil.parse("2004/05/22 00:01:00"), ld.getTimestamp());
		assertEquals(53.025D, ld.getDouble(0), 0.01);
		assertEquals(55.525D, ld.getDouble(1), 0.01);
    }

    
    /**
     * HSQLDB用のSQL文字列生成クラス
     * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
     */
    static class TestHSQLUtility implements SQLUtility {
    	public static final String DATE_FIELD_NAME = "f_date";
        public String getSelectAllString(String name, List dataHolder) {
    		String[] columnNames = new String[dataHolder.size() + 1];
    		columnNames[0] = DATE_FIELD_NAME;
    		int count = 1;
    		for (Iterator i = dataHolder.iterator(); i.hasNext(); count++) {
    			HolderString hs = (HolderString) i.next();
    			columnNames[count] = "f_" + hs.getProvider() + "_" + hs.getHolder();
    		}

    		StringBuffer b = new StringBuffer();
    		b.append(getSelectString(name, columnNames, PostgreSQLValueListHandler.MAX_MAP_SIZE));
    		b.append(" WHERE f_revision = 0 ORDER BY ");
    		b.append(DATE_FIELD_NAME);
    		b.append(" DESC");
    		
    		return b.toString();
        }

        public String getSelectAllString(String name, List dataHolder, int limit) {
    		String[] columnNames = new String[dataHolder.size() + 1];
    		columnNames[0] = DATE_FIELD_NAME;
    		int count = 1;
    		for (Iterator i = dataHolder.iterator(); i.hasNext(); count++) {
    			HolderString hs = (HolderString) i.next();
    			columnNames[count] = "f_" + hs.getProvider() + "_" + hs.getHolder();
    		}

    		StringBuffer b = new StringBuffer();
    		b.append(getSelectString(name, columnNames, PostgreSQLValueListHandler.MAX_MAP_SIZE));
    		b.append(" WHERE f_revision = 0 ORDER BY ");
    		b.append(DATE_FIELD_NAME);
    		b.append(" DESC");
    		
    		return b.toString();
        }

        public String getSelectTimeString(String name, List dataHolder,
                Timestamp time) {
    		String[] columnNames = createFieldNames(dataHolder);

    		StringBuffer b = new StringBuffer();
    		b.append(getSelectString(name, columnNames, PostgreSQLValueListHandler.MAX_MAP_SIZE));
    		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		b.append(" WHERE f_revision = 0 AND f_date > '").append(f.format(time)).append("'");
    		b.append(" ORDER BY ");
    		b.append(DATE_FIELD_NAME);
    		b.append(" DESC");
    		
    		return b.toString();
        }
    	private String[] createFieldNames(List dataHolder) {
            String[] columnNames = new String[dataHolder.size() + 1];
    		columnNames[0] = DATE_FIELD_NAME;
    		int count = 1;
    		for (Iterator i = dataHolder.iterator(); i.hasNext(); count++) {
    			HolderString hs = (HolderString) i.next();
    			columnNames[count] = "f_" + hs.getProvider() + "_" + hs.getHolder();
    		}
            return columnNames;
        }
    	private String getSelectString(String tableName, String[] columnNames, int top) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("SELECT TOP ").append(top).append(" ");
            for (int i = 0; i < (columnNames.length - 1); i++) {
                buffer.append(columnNames[i]);
                buffer.append(", ");
            }
            buffer.append(columnNames[columnNames.length - 1]);
            buffer.append(" FROM ").append(tableName);
            return buffer.toString();
        }
    	
    	public String getFirstData(String name, List dataHolder) {
    		String[] columnNames = createFieldNames(dataHolder);

    		StringBuffer b = new StringBuffer();
    		b.append(getSelectString(name, columnNames, 1))
    		.append(" ORDER BY ").append(DATE_FIELD_NAME)
    		.append(" ASC");

    		return b.toString(); 
    	}

        public String getLastData(String name, List dataHolder) {
    		String[] columnNames = createFieldNames(dataHolder);
    		StringBuffer b = new StringBuffer();
    		b.append(getSelectString(name, columnNames, 1))
    		.append(" ORDER BY ").append(DATE_FIELD_NAME)
    		.append(" DESC");

    		return b.toString(); 
    	}
        
        public String getSelectBefore(String name, List dataHolder, Timestamp start, int limit) {

            String[] columnNames = createFieldNames(dataHolder);

    		StringBuffer b = new StringBuffer();
    		b.append(getSelectString(name, columnNames, limit));
    		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		b.append(" WHERE f_revision = 0 AND f_date < '").append(f.format(start)).append("'");
    		b.append(" ORDER BY ");
    		b.append(DATE_FIELD_NAME);
    		b.append(" DESC");
    		
    		return b.toString();
        }
        
        public String getSelectAfter(String name, List dataHolder, Timestamp start, int limit) {

            String[] columnNames = createFieldNames(dataHolder);

    		StringBuffer b = new StringBuffer();
    		b.append(getSelectString(name, columnNames, limit));
    		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		b.append(" WHERE f_revision = 0 AND f_date >= '").append(f.format(start)).append("'");
    		b.append(" ORDER BY ");
    		b.append(DATE_FIELD_NAME);
    		b.append(" ASC");
    		
    		return b.toString();
        }
    }
}
