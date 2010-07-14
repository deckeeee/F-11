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

package org.F11.scada.server.goda.impl;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.F11.scada.server.goda.GodaFileSearch;
import org.F11.scada.server.goda.GodaTaskProperty;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.seasar.extension.unit.S2TestCase;

public class GodaFileSearchImplTest extends S2TestCase {
	private final Logger logger = Logger
			.getLogger(GodaFileSearchImplTest.class);
	GodaFileSearch search;

	public GodaFileSearchImplTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("GodaFileSearchImplTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * 前回送信ファイルが存在しない場合、昨日の日付を返す。
	 * 
	 * @throws Exception
	 */
	public void testGetPath() throws Exception {
		GodaTaskProperty property = null;
		try {
			property = new GodaTaskProperty();
			String tmp = System.getProperty("java.io.tmpdir");
			logger.info("temp file = " + tmp);
			property.setWatchPath(tmp);
			property.setFileFormat("yyyyMMdd'_C01_01.csv'");
			GodaFileSearchImpl searchImpl = (GodaFileSearchImpl) search;
			FileConfiguration fileConfiguration = searchImpl
					.getConfig(property);

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			assertEquals(DateFormatUtils.format(cal.getTime(), property
					.getFileFormat()), fileConfiguration
					.getString(GodaFileSearchImpl.LAST_SEND_FILE_KEY));
		} finally {
			if (null != property) {
				File file = new File(
						property.getWatchPath(),
						GodaFileSearchImpl.GODA_XML);
				file.delete();
			}
		}
	}

	/**
	 * 前回送信ファイルが存在する場合、その日付を返す。
	 * 
	 * @throws Exception
	 */
	public void testGetPathAlreadyLastDay() throws Exception {
		GodaTaskProperty property = null;
		try {
			property = new GodaTaskProperty();
			String tmp = System.getProperty("java.io.tmpdir");

			XMLPropertiesConfiguration configuration = new XMLPropertiesConfiguration();
			configuration.addProperty(
					GodaFileSearchImpl.LAST_SEND_FILE_KEY,
					"19700101_C01_01.csv");
			configuration.save(new File(tmp, GodaFileSearchImpl.GODA_XML));

			property.setWatchPath(tmp);
			property.setFileFormat("yyyyMMdd'_C01_01.csv'");

			GodaFileSearchImpl searchImpl = (GodaFileSearchImpl) search;
			FileConfiguration fileConfiguration = searchImpl
					.getConfig(property);
			assertEquals("19700101_C01_01.csv", fileConfiguration
					.getString(GodaFileSearchImpl.LAST_SEND_FILE_KEY));
		} finally {
			if (null != property) {
				File file = new File(
						property.getWatchPath(),
						GodaFileSearchImpl.GODA_XML);
				file.delete();
			}
		}
	}

	/**
	 * 前回送信ファイルの設定
	 * 
	 * @throws Exception
	 */
	public void testSetPath() throws Exception {
		GodaTaskProperty property = null;
		try {
			property = new GodaTaskProperty();
			String tmp = System.getProperty("java.io.tmpdir");
			property.setWatchPath(tmp);
			property.setFileFormat("yyyyMMdd'_C01_01.csv'");
			GodaFileSearchImpl searchImpl = (GodaFileSearchImpl) search;
			FileConfiguration fileConfiguration = searchImpl
					.getConfig(property);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			assertEquals(DateFormatUtils.format(cal.getTime(), property
					.getFileFormat()), fileConfiguration
					.getString(GodaFileSearchImpl.LAST_SEND_FILE_KEY));
			cal = Calendar.getInstance();
			search.setLastFile(property, cal.getTime());
			fileConfiguration = searchImpl.getConfig(property);
			assertEquals(DateFormatUtils.format(cal.getTime(), property
					.getFileFormat()), fileConfiguration
					.getString(GodaFileSearchImpl.LAST_SEND_FILE_KEY));
		} finally {
			if (null != property) {
				File file = new File(
						property.getWatchPath(),
						GodaFileSearchImpl.GODA_XML);
				file.delete();
			}
		}
	}

	public void testGetFiles() throws Exception {
		GodaTaskProperty property = null;
		File tmpfile = null;
		File tmpfile2 = null;
		try {
			property = new GodaTaskProperty();
			String tmp = System.getProperty("java.io.tmpdir");
			property.setWatchPath(tmp);
			property.setFileFormat("yyyyMMdd'_C01_01.csv'");

			tmpfile = new File(tmp, "19700101_C01_01.csv");
			tmpfile.createNewFile();

			tmpfile2 = new File(tmp, DateFormatUtils.format(
					new Date(),
					property.getFileFormat()));
			tmpfile2.createNewFile();

			File[] files = search.getFiles(property);
			assertEquals(0, files.length);
		} finally {
			if (null != property) {
				File file = new File(
						property.getWatchPath(),
						GodaFileSearchImpl.GODA_XML);
				file.delete();
			}
			if (null != tmpfile) {
				tmpfile.delete();
			}
			if (null != tmpfile2) {
				tmpfile2.delete();
			}
		}
	}
}
