/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.invoke;

import java.util.List;

import org.F11.scada.applet.ngraph.editor.SeriesPropertyData;
import org.F11.scada.server.dao.PointTableDao;
import org.F11.scada.server.dao.PointTableDto;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

public class UnitSerachService implements InvokeHandler {
	private Logger logger = Logger.getLogger(UnitSerachService.class);
	private final PointTableDao dao;

	public UnitSerachService() {
		S2Container container = S2ContainerUtil.getS2Container();
		this.dao = (PointTableDao) container.getComponent(PointTableDao.class);
	}

	public Object invoke(Object[] args) {
		SeriesPropertyData unit = (SeriesPropertyData) args[0];
		PointTableDto dto = getPointTableDto(unit);
		List<PointTableDto> pointTable = dao.getPointTable(dto);
//		logger.info(pointTable);
		return pointTable;
	}

	private PointTableDto getPointTableDto(SeriesPropertyData unit) {
		PointTableDto dto = new PointTableDto();
		dto.setUnit(getConstraction(unit.getUnit()));
		dto.setName(getConstraction(unit.getName()));
		dto.setUnitMark(getConstraction(unit.getMark()));
		return dto;
	}

	private String getConstraction(String s) {
		return "".equals(s) ? null : '%' + s + '%';
	}
}
