/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.applet.ngraph.editor.service;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ngraph.editor.SeriesPropertyData;
import org.F11.scada.data.DataAccessable;
import org.F11.scada.exception.RemoteRuntimeException;
import org.F11.scada.server.dao.PointTableDto;
import org.apache.log4j.Logger;

public class UnitSearchServiceImpl implements UnitSearchService {
	private final Logger logger = Logger.getLogger(UnitSearchServiceImpl.class);
	private DataAccessable alarmRef;

	public UnitSearchServiceImpl() {
		String collectorServer = WifeUtilities.createRmiManagerDelegator();
		try {
			alarmRef = (DataAccessable) Naming.lookup(collectorServer);
		} catch (Exception e) {
			logger.info("サーバーのリモート参照取得に失敗しました:", e);
		}
	}

	public List<SeriesPropertyData> getSeriesPropertyDataList(SeriesPropertyData spd) {
		try {
			List<PointTableDto> pointList =
				(List<PointTableDto>) alarmRef.invoke(
					"UnitSearchService",
					new Object[] { spd });
			return convertList(pointList);
		} catch (RemoteException e) {
			throw new RemoteRuntimeException(e);
		}
	}

	private List<SeriesPropertyData> convertList(List<PointTableDto> pointList) {
		ArrayList<SeriesPropertyData> unitList =
			new ArrayList<SeriesPropertyData>(pointList.size());
		//TODO 必要な値をDBから持ってくること。
		for (PointTableDto pointTableDto : pointList) {
			unitList.add(getSeriesPropertyData(
				0,
				true,
				null,
				pointTableDto.getUnit(),
				pointTableDto.getName(),
				pointTableDto.getUnitMark(),
				0F,
				0F,
				"",
				""));
		}
		return unitList;
	}

	private SeriesPropertyData getSeriesPropertyData(
			int index,
			boolean visible,
			String color,
			String unit,
			String name,
			String mark,
			Float min,
			Float max,
			String verticalFormat,
			String holder) {
		SeriesPropertyData spd = new SeriesPropertyData();
		spd.setIndex(index);
		spd.setVisible(visible);
		spd.setColor(color);
		spd.setUnit(unit);
		spd.setName(name);
		spd.setMark(mark);
		spd.setMin(min);
		spd.setMax(max);
		spd.setVerticalFormat(verticalFormat);
		spd.setHolder(holder);
		return spd;
	}
}
