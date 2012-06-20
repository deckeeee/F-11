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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

import org.F11.scada.server.goda.GodaFileSearch;
import org.F11.scada.server.goda.GodaTaskProperty;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

public class GodaFileSearchImpl implements GodaFileSearch {
	static final String GODA_XML = "goda.xml";
	static final String LAST_SEND_FILE_KEY =
		"org.F11.scada.server.goda.impl.LastSendFileKey";
	private final Logger logger = Logger.getLogger(GodaFileSearchImpl.class);

	public File[] getFiles(GodaTaskProperty property) throws IOException {
		FileConfiguration configuration = getConfig(property);
		return getFiles(configuration, property);
	}

	private File[] getFiles(
			FileConfiguration configuration,
			GodaTaskProperty property) {
		File root = new File(property.getWatchPath());
		File[] files =
			root.listFiles(new CsvFilenameFilter(".*\\.[cC][sS][vV]"));
		logger.info(Arrays.asList(files));
		return tailFiles(files, configuration, property);
	}

	private File[] tailFiles(
			File[] files,
			FileConfiguration configuration,
			GodaTaskProperty property) {
		TreeSet fileSet = new TreeSet(Arrays.asList(files));
		File[] ret = getSendFiles(fileSet, configuration, property);
		logger.info(Arrays.asList(ret));
		return ret;
	}

	private File[] getSendFiles(
			TreeSet fileSet,
			FileConfiguration configuration,
			GodaTaskProperty property) {
		String lastFile = configuration.getString(LAST_SEND_FILE_KEY);
		return (File[]) new TreeSet(fileSet.tailSet(new File(property
			.getWatchPath(), lastFile))).headSet(
			new File(property.getWatchPath(), getLastFile(property))).toArray(
			new File[0]);
	}

	FileConfiguration getConfig(GodaTaskProperty property) throws IOException {
		try {
			File file = new File(property.getWatchPath(), GODA_XML);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				XMLPropertiesConfiguration configuration =
					new XMLPropertiesConfiguration();
				configuration.addProperty(
					LAST_SEND_FILE_KEY,
					getNowFile(property));
				configuration.save(file);
				return configuration;
			} else {
				return new XMLPropertiesConfiguration(file);
			}
		} catch (ConfigurationException e) {
			logger.error(GODA_XML + "書式エラー", e);
			throw new ConfigurationRuntimeException(e);
		}
	}

	private String getNowFile(GodaTaskProperty property) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return DateFormatUtils.format(cal.getTime(), property.getFileFormat());
	}

	public void setLastFile(GodaTaskProperty property, Date date)
			throws IOException {
		FileConfiguration configuration = getConfig(property);
		configuration.setProperty(LAST_SEND_FILE_KEY, DateFormatUtils.format(
			date,
			property.getFileFormat()));
		try {
			configuration.save();
		} catch (ConfigurationException e) {
			logger.error(GODA_XML + "書式エラー", e);
			throw new ConfigurationRuntimeException(e);
		}
	}

	private String getLastFile(GodaTaskProperty property) {
		Calendar cal = Calendar.getInstance();
		return DateFormatUtils.format(cal.getTime(), property.getFileFormat());
	}

	private static class CsvFilenameFilter implements FilenameFilter {
		private final String filterName;

		public CsvFilenameFilter(String filterName) {
			this.filterName = filterName;
		}

		public boolean accept(File dir, String name) {
			return name.matches(filterName);
		}
	}
}