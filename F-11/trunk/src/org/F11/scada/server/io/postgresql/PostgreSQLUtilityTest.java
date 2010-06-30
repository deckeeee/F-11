/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/postgresql/PostgreSQLUtilityTest.java,v 1.5.2.12 2007/10/17 09:04:36 frdm Exp $
 * $Revision: 1.5.2.12 $
 * $Date: 2007/10/17 09:04:36 $
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

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.WifeException;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.communicater.CommunicaterFactory;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.communicater.EnvironmentMap;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.event.WifeEventListener;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.test.util.TimestampUtil;
import org.F11.scada.xwife.server.communicater.EnvironmentPropertyFiles;
import org.seasar.extension.unit.S2TestCase;

/**
 * SQLユーティリティーのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PostgreSQLUtilityTest extends S2TestCase {
	DataHolder[] dataHolders;
	Timestamp currentTime;
	static final String TABLE_NAME = "TABLE";
	private PostgreSQLUtility utility;

	/**
	 * Constructor for PostgreSQLUtilityTest.
	 * @param arg0
	 */
	public PostgreSQLUtilityTest(String arg0) {
		super(arg0);
	}

	protected void setUp() {
	    include("PostgreSQLUtilityTest.dicon");
		try {
			dataHolders =
				TestUtil.createDataProvider().getDataHolders();
		} catch (DataProviderDoesNotSupportException e) {
			e.printStackTrace();
		}
		currentTime = new Timestamp(System.currentTimeMillis());
		this.utility = (PostgreSQLUtility) getComponent("sql1");
	}

	public static CommunicaterFactory createCommunicaterFactoryMock() {
	    return new TestCommunicaterFactory();
	}

	public static CommunicaterFactory createCommunicaterFactoryMock2() {
		return new TestCommunicaterFactory2();
	}

	/*
	 * String getInsertString のテスト(String, List, Timestamp, int)
	 */
	public void testGetInsertString() throws IOException {
		String device = EnvironmentManager.get("/server/device", "");
		Environment[] environments = EnvironmentPropertyFiles.getEnvironments(device);
		EnvironmentMap.putAll(environments);
	    HolderString hs1 = new HolderString();
	    hs1.setProvider("P1");
	    hs1.setHolder("D_500_BcdSingle");
	    HolderString hs2 = new HolderString();
	    hs2.setProvider("P1");
	    hs2.setHolder("D_501_BcdSingle");
	    HolderString hs3 = new HolderString();
	    hs3.setProvider("P1");
	    hs3.setHolder("D_1900000_Digital");
	    ArrayList l = new ArrayList();
	    l.add(hs1);
	    l.add(hs2);
	    l.add(hs3);

	    String sql =
	        utility.getInsertString(
				TABLE_NAME,
				l,
				currentTime,
				0);
		System.out.println(sql);
		StringBuffer exp = new StringBuffer();
		exp.append("INSERT INTO ");
		exp.append(TABLE_NAME);
		exp.append("(f_date, f_revision, f_P1_D_1900000_Digital, f_P1_D_500_BcdSingle, f_P1_D_501_BcdSingle)");
		exp.append(" VALUES('");
		exp.append(currentTime.toString());
		exp.append("', 0, 'false', 2500.0, 3000.0)");
		assertEquals(exp.toString(), sql);
	}

	/*
	 * String getCreateString のテスト(String, List)
	 */
	public void testGetCreateString() {
	    HolderString hs = new HolderString();
	    hs.setProvider("P1");
	    hs.setHolder("D_500_BcdSingle");
	    HolderString hs2 = new HolderString();
	    hs2.setProvider("P1");
	    hs2.setHolder("D_501_BcdSingle");
	    ArrayList l = new ArrayList();
	    l.add(hs);
	    l.add(hs2);

	    String sql =
	        utility.getCreateString(TABLE_NAME, l);
		System.out.println(sql);
		StringBuffer exp = new StringBuffer();
		exp.append("CREATE TABLE ");
		exp.append(TABLE_NAME);
		exp.append(" (f_date DATETIME NOT NULL, f_revision INTEGER NOT NULL, f_P1_D_500_BcdSingle double precision, f_P1_D_501_BcdSingle double precision, PRIMARY KEY(f_date, f_revision))");
		assertEquals(exp.toString(), sql);
	}

	/*
	 * String getAlterString のテスト(String, DataHolder)
	 */
	public void testGetAlterString() {
	    HolderString hs = new HolderString();
	    hs.setProvider("P1");
	    hs.setHolder("D_500_BcdSingle");
		String sql =
		    utility.getAlterString(TABLE_NAME, hs);
//		System.out.println(sql);
		StringBuffer exp = new StringBuffer();
		exp.append("ALTER TABLE ");
		exp.append(TABLE_NAME);
		exp.append(" ADD COLUMN f_P1_D_500_BcdSingle double precision");
		assertEquals(exp.toString(), sql);
	}

	public void testGetSelectAllLimitZeroString() {
		String sql =
		    utility.getSelectAllLimitZeroString(TABLE_NAME);
//		System.out.println(sql);
		StringBuffer exp = new StringBuffer();
		exp.append("SELECT * FROM ");
		exp.append(TABLE_NAME);
		exp.append(" LIMIT 0");
		assertEquals(exp.toString(), sql);
	}

	public void testGetRevisionString() {
		String sql =
		    utility.getRevisionString(TABLE_NAME, currentTime);
//		System.out.println(sql);
		StringBuffer exp = new StringBuffer();
		exp.append("SELECT f_revision FROM ");
		exp.append(TABLE_NAME);
		exp.append(" WHERE f_date='");
		exp.append(currentTime.toString());
		exp.append("' ORDER BY f_revision DESC");
		assertEquals(exp.toString(), sql);
	}

	public void testGetSelectAllString() {
	    HolderString hs1 = new HolderString();
	    hs1.setProvider("P1");
	    hs1.setHolder("D_500_BcdSingle");
	    HolderString hs2 = new HolderString();
	    hs2.setProvider("P1");
	    hs2.setHolder("D_501_BcdSingle");
	    ArrayList l = new ArrayList();
	    l.add(hs1);
	    l.add(hs2);

	    String sql =
	        utility.getSelectAllString(TABLE_NAME, l, PostgreSQLValueListHandler.MAX_MAP_SIZE);
//		System.out.println(sql);
		StringBuffer exp = new StringBuffer();
		exp.append("SELECT f_date, f_P1_D_500_BcdSingle, f_P1_D_501_BcdSingle FROM ");
		exp.append(TABLE_NAME);
		exp.append(" WHERE f_revision = 0 ORDER BY f_date DESC LIMIT ").append(PostgreSQLValueListHandler.MAX_MAP_SIZE);
		assertEquals(exp.toString(), sql);
	}

	public void testGetSelectTimeString() {
	    HolderString hs1 = new HolderString();
	    hs1.setProvider("P1");
	    hs1.setHolder("D_500_BcdSingle");
	    HolderString hs2 = new HolderString();
	    hs2.setProvider("P1");
	    hs2.setHolder("D_501_BcdSingle");
	    ArrayList l = new ArrayList();
	    l.add(hs1);
	    l.add(hs2);
	    Timestamp time = new Timestamp(System.currentTimeMillis());

	    String sql =
	        utility.getSelectTimeString(TABLE_NAME, l, time);
		System.out.println(sql);
		StringBuffer exp = new StringBuffer();
		exp.append("SELECT f_date, f_P1_D_500_BcdSingle, f_P1_D_501_BcdSingle FROM ");
		exp.append(TABLE_NAME);
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		exp.append(" WHERE f_revision = 0 AND f_date > '").append(f.format(time)).append("'");
		exp.append(" ORDER BY f_date DESC LIMIT ").append(PostgreSQLValueListHandler.MAX_MAP_SIZE);
		assertEquals(exp.toString(), sql);
	}

	private HolderString createHolderString(String provider, String holder) {
	    HolderString hs = new HolderString();
	    hs.setProvider(provider);
	    hs.setHolder(holder);
	    return hs;
	}

	public void testGetFirstData() {
	    ArrayList l = new ArrayList();
	    l.add(createHolderString("P1", "H1"));
	    l.add(createHolderString("P1", "H2"));

	    String sql = utility.getFirstData(TABLE_NAME, l);
	    assertEquals("SELECT f_date, f_P1_H1, f_P1_H2 FROM TABLE ORDER BY f_date ASC LIMIT 1", sql);
	}

	public void testGetLastData() {
	    ArrayList l = new ArrayList();
	    l.add(createHolderString("P1", "H1"));
	    l.add(createHolderString("P1", "H2"));

	    String sql = utility.getLastData(TABLE_NAME, l);
	    assertEquals("SELECT f_date, f_P1_H1, f_P1_H2 FROM TABLE ORDER BY f_date DESC LIMIT 1", sql);
	}

	public void testGetSelectBefore() throws Exception {
	    ArrayList l = new ArrayList();
	    l.add(new HolderString("P1", "H1"));
	    l.add(new HolderString("P1", "H2"));

	    String sql = utility.getSelectBefore(TABLE_NAME, l, TimestampUtil
                .parse("2005/01/01 00:00:00"), 4);
	    assertEquals("SELECT f_date, f_P1_H1, f_P1_H2 FROM TABLE WHERE f_revision = 0 AND f_date < '2005/01/01 00:00:00' ORDER BY f_date DESC LIMIT " + 4, sql);
	}

	public void testGetSelectAfter() throws Exception {
	    ArrayList l = new ArrayList();
	    l.add(new HolderString("P1", "H1"));
	    l.add(new HolderString("P1", "H2"));

	    String sql = utility.getSelectAfter(TABLE_NAME, l, TimestampUtil
                .parse("2005/01/01 00:00:00"), 4);
	    assertEquals("SELECT f_date, f_P1_H1, f_P1_H2 FROM TABLE WHERE f_revision = 0 AND f_date >= '2005/01/01 00:00:00' ORDER BY f_date ASC LIMIT " + 4, sql);
	}
/*
	public void testGetInsertPreparedStatment() throws Exception {
		String device = EnvironmentManager.get("/server/device", "");
		Environment[] environments = EnvironmentPropertyFiles.getEnvironments(device);
		EnvironmentMap.putAll(environments);
	    HolderString hs1 = new HolderString();
	    hs1.setProvider("P1");
	    hs1.setHolder("D_500_BcdSingle");
	    HolderString hs2 = new HolderString();
	    hs2.setProvider("P1");
	    hs2.setHolder("D_501_BcdSingle");
	    ArrayList l = new ArrayList();
	    l.add(hs1);
	    l.add(hs2);

	    String sql =
	        utility.getInsertPreparedStatment(
				TABLE_NAME,
				l,
				currentTime,
				0);
		System.out.println(sql);
		StringBuilder exp = new StringBuilder();
		exp.append("INSERT INTO ");
		exp.append(TABLE_NAME);
		exp.append(" (f_date, f_revision, f_P1_D_500_BcdSingle, f_P1_D_501_BcdSingle)");
		exp.append(" VALUES(?, ?, ?, ?)");
		assertEquals(exp.toString(), sql);
	}
*/
	public void testGetInsertStringError() throws IOException {
		String device = EnvironmentManager.get("/server/device", "");
		Environment[] environments = EnvironmentPropertyFiles.getEnvironments(device);
		EnvironmentMap.putAll(environments);
	    HolderString hs1 = new HolderString();
	    hs1.setProvider("P1");
	    hs1.setHolder("D_500_BcdSingle");
	    HolderString hs2 = new HolderString();
	    hs2.setProvider("P1");
	    hs2.setHolder("D_501_BcdSingle");
	    HolderString hs3 = new HolderString();
	    hs3.setProvider("P2");
	    hs3.setHolder("D_1900000_Digital");
	    ArrayList l = new ArrayList();
	    l.add(hs3);
	    l.add(hs1);
	    l.add(hs2);

	    PostgreSQLUtility utility2 = (PostgreSQLUtility) getComponent("sql2");
	    String sql =
	        utility2.getInsertString(
				TABLE_NAME,
				l,
				currentTime,
				0);
		System.out.println(sql);
		StringBuffer exp = new StringBuffer();
		exp.append("INSERT INTO ");
		exp.append(TABLE_NAME);
		exp.append("(f_date, f_revision, f_P1_D_500_BcdSingle, f_P1_D_501_BcdSingle, f_P2_D_1900000_Digital)");
		exp.append(" VALUES('");
		exp.append(currentTime.toString());
		exp.append("', 0, 2000.0, 2500.0, 'false')");
		assertEquals(exp.toString(), sql);
	}

	public void testGetPaddingSql() throws Exception {
	    HolderString hs1 = new HolderString();
	    hs1.setProvider("P1");
	    hs1.setHolder("D_500_BcdSingle");
	    HolderString hs2 = new HolderString();
	    hs2.setProvider("P1");
	    hs2.setHolder("D_501_BcdSingle");
	    HolderString hs3 = new HolderString();
	    hs3.setProvider("P2");
	    hs3.setHolder("D_1900000_Digital");
	    ArrayList<HolderString> l = new ArrayList<HolderString>();
	    l.add(hs1);
	    l.add(hs2);
	    l.add(hs3);
	    System.out.println(utility.getPaddingSql("test", l, currentTime, 0));
	}

	static class TestCommunicaterFactory implements CommunicaterFactory {
        public Communicater createCommunicator(Environment device)
                throws Exception {
            return new TestCommunicater();
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
            public Map syncRead(Collection commands, boolean sameDataBalk)
                    throws InterruptedException, IOException, WifeException {
                HashMap map = new HashMap();
                int value = 2000;
                for (Iterator i = commands.iterator(); i.hasNext(); value += 500) {
                    WifeCommand c = (WifeCommand) i.next();
                    map.put(c, WifeUtilities.toByteArray(String.valueOf(value)));
                }
                return map;
            }
            public Map syncRead(Collection commands)
                    throws InterruptedException, IOException, WifeException {
                return null;
            }
            public void syncWrite(Map commands) throws InterruptedException,
                    IOException, WifeException {
            }
        }
	}


	static class TestCommunicaterFactory2 implements CommunicaterFactory {
        public Communicater createCommunicator(Environment device)
                throws Exception {
            return new TestCommunicater();
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
            public Map syncRead(Collection commands, boolean sameDataBalk)
                    throws InterruptedException, IOException, WifeException {
            	for (Iterator i = commands.iterator(); i.hasNext();) {
					WifeCommand command = (WifeCommand) i.next();
					if ("P2".equals(command.getDeviceID())) {
						throw new WifeException();
					}
				}
                HashMap map = new HashMap();
                int value = 2000;
                for (Iterator i = commands.iterator(); i.hasNext(); value += 500) {
                    WifeCommand c = (WifeCommand) i.next();
                    map.put(c, WifeUtilities.toByteArray(String.valueOf(value)));
                }
                return map;
            }
            public Map syncRead(Collection commands)
                    throws InterruptedException, IOException, WifeException {
                return null;
            }
            public void syncWrite(Map commands) throws InterruptedException,
                    IOException, WifeException {
            }
        }
	}

}
