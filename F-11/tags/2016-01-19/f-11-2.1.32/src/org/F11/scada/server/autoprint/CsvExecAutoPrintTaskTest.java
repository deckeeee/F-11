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

package org.F11.scada.server.autoprint;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.F11.scada.server.io.AutoPrintDataService;
import org.F11.scada.test.util.TestUtil;
import org.apache.log4j.Logger;

public class CsvExecAutoPrintTaskTest extends TestCase {
	private final static String TMP_NAME = "csvtest";
	private final Logger logger = Logger.getLogger(CsvExecAutoPrintTaskTest.class);

	public CsvExecAutoPrintTaskTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testWriteCsv() throws Exception {
		CsvExecAutoPrintTask task = new CsvExecAutoPrintTask(new TestAutoPrintDataServiceFactory());
		task.setName("testTask");
		task.setLogg("test_log_table");
		task.setAutoPrintDataStore();
		File tmp = File.createTempFile(TMP_NAME, null);
		tmp.deleteOnExit();
		String path = tmp.getPath();
		String dir = getDirectory(path);
		String file = tmp.getName();
		logger.info(dir);
		logger.info(file);
		task.setTmpdir(dir);
		task.setTmpfile(file);
		task.setData_head("true");
		task.setHead("head1");
		task.setHead("head2");
		task.setExecute("cmd");
		task.setExecute("/C");
		task.setExecute("echo");
		task.setExecute("A");
		task.setAutoPrintSchedule(new TestAutoPrintSchedule());
		TestUtil.sleep(2000L);
		BufferedReader in = new BufferedReader(new FileReader(tmp));
		try {
			assertEquals("1行目", "head1", in.readLine());
			assertEquals("2行目", "head2", in.readLine());
			assertEquals("3行目", "日付,時刻,unit-1,unit-2", in.readLine());
			assertEquals("4行目", "日付,時刻,name-1,name-2", in.readLine());
			assertEquals("5行目", "日付,時刻,kg,㌧㌦", in.readLine());
			assertEquals("6行目", "2006/01/01,00:00:00,10.0,999.9", in.readLine());
		} finally {
			if (null != in) {
				in.close();
			}
			tmp.delete();
		}
	}

	private String getDirectory(String path) {
		return path.substring(0, path.indexOf(TMP_NAME) - 1);
	}

	private static class TestAutoPrintDataServiceFactory implements AutoPrintDataServiceFactory {
		public AutoPrintDataService getAutoPrintDataService(String tableName) {
			return new TestAutoPrintDataService();
		}

		private static class TestAutoPrintDataService implements AutoPrintDataService {

			public Map getAutoPrintSchedules() {
				return null;
			}

			public List getLoggingDataList(String tableName, Timestamp start, Timestamp end, List dataHolders) {
				ArrayList data = new ArrayList();
				data.add("2006/01/01,00:00:00,10.0,999.9");
				return data;
			}

			public List getLoggingHeddarList(String tableName, List dataHolders) {
				ArrayList header = new ArrayList();
				HashMap map = new HashMap();
				map.put("unit", "unit-1");
				map.put("name", "name-1");
				map.put("unit_mark", "kg");
				header.add(map);
				map = new HashMap();
				map.put("unit", "unit-2");
				map.put("name", "name-2");
				map.put("unit_mark", "㌧㌦");
				header.add(map);
				return header;
			}

			public void setAutoPrintSchedule(String name, AutoPrintSchedule schedule) {
			}
			
		}
	}


	private static class TestAutoPrintSchedule implements AutoPrintSchedule {

		public String getDate() {
			return null;
		}

		public Timestamp getEndTime() {
			return null;
		}

		public String getScheduleName() {
			return null;
		}

		public Timestamp getStartTime() {
			return null;
		}

		public Timestamp getTimestamp() {
			return null;
		}

		public boolean isAutoOn() {
			return true;
		}

		public boolean isNow() {
			return true;
		}

		public AutoPrintSchedule showParamDialog(Frame frame) {
			return null;
		}
		
	}
}
