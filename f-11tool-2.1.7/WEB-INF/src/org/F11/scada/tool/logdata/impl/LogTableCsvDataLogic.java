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

package org.F11.scada.tool.logdata.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.F11.scada.server.io.nio.LogTableSelectService;
import org.F11.scada.tool.logdata.CsvCreatorUtil;
import org.F11.scada.tool.logdata.DataConditionsForm;
import org.F11.scada.tool.logdata.MakeCsvService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class LogTableCsvDataLogic implements MakeCsvService {
	private final Log log = LogFactory.getLog(LogTableCsvDataLogic.class);
	private final Connection connection;

	public LogTableCsvDataLogic(Connection connection) {
		this.connection = connection;
	}

	public List getHeaderData(DataConditionsForm form) {
		CsvCreatorUtil creator = new CsvCreatorUtil();
		return creator.getHeaderData(form, connection);
	}

	public List getCsvData(DataConditionsForm form) {
		try {
			return getCsvDatas(form);
		} catch (Exception e) {
			log.error("sql exeute error : ", e);
			return Collections.EMPTY_LIST;
		}
	}

	private List getCsvDatas(DataConditionsForm form) throws IOException, SQLException {
		String loggingName = form.getTableString();
		CsvCreatorUtil creator = new CsvCreatorUtil();
		List holderDatas = creator.getHolderDatas(loggingName);
		S2Container container = SingletonS2ContainerFactory.getContainer();
		LogTableSelectService service =
			(LogTableSelectService) container.getComponent(LogTableSelectService.class);
		return service.select(loggingName, holderDatas, creator.getStartTime(form), creator.getEndTime(form));
	}
}
