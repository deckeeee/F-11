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
import org.F11.scada.applet.ngraph.editor.PageData;
import org.F11.scada.applet.ngraph.editor.SeriesPropertyData;
import org.F11.scada.data.DataAccessable;
import org.F11.scada.exception.RemoteRuntimeException;
import org.F11.scada.server.dao.PointTableDto;
import org.apache.log4j.Logger;

public class UnitSearchServiceImpl implements UnitSearchService {
	private final Logger logger = Logger.getLogger(UnitSearchServiceImpl.class);
	private DataAccessable alarmRef;
	private final PageData page;

	public UnitSearchServiceImpl(PageData page) {
		String collectorServer = WifeUtilities.createRmiManagerDelegator();
		try {
			alarmRef = (DataAccessable) Naming.lookup(collectorServer);
		} catch (Exception e) {
			logger.error("サーバーのリモート参照取得に失敗しました:", e);
		}
		this.page = page;
	}

	public List<SeriesPropertyData> getSeriesPropertyDataList(
			SeriesPropertyData spd) {
		try {
			List<PointTableDto> pointList =
				(List<PointTableDto>) alarmRef.invoke(
					"UnitSearchService",
					new Object[] {
						spd,
						page
							.getTrend3Data()
							.getHorizontalScaleButtonProperty()
							.get(0)
							.getLogName() });
			return convertList(pointList);
		} catch (RemoteException e) {
			logger.error("ポイント情報取り込みにて、サーバーエラーが発生しました。:", e);
			throw new RemoteRuntimeException(e);
		}
	}

	private List<SeriesPropertyData> convertList(List<PointTableDto> pointList) {
		ArrayList<SeriesPropertyData> unitList =
			new ArrayList<SeriesPropertyData>(pointList.size());
		for (PointTableDto pointTableDto : pointList) {
			unitList.add(getSeriesPropertyData(
				0,
				true,
				null,
				pointTableDto.getUnit(),
				pointTableDto.getName(),
				pointTableDto.getUnitMark(),
				pointTableDto.getMin(),
				pointTableDto.getMax(),
				pointTableDto.getFormat(),
				pointTableDto.getProvider() + "_" + pointTableDto.getHolder(),
				pointTableDto.getConvert()));
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
			String holder,
			String convert) {
		SeriesPropertyData spd = new SeriesPropertyData();
		spd.setIndex(index);
		spd.setVisible(visible);
		spd.setColor(color);
		spd.setUnit(unit);
		spd.setName(name);
		spd.setMark(mark);
		spd.setMin(getMin(min));
		spd.setMax(getMax(max));
		spd.setVerticalFormat(getVerticalFormat(verticalFormat));
		spd.setHolder(holder);
		spd.setConvert(getConvert(convert));
		return spd;
	}

	private Float getMin(Float min) {
		return min != null ? min : new Float("0");
	}

	private Float getMax(Float max) {
		return max != null ? max : new Float("1");
	}

	private String getVerticalFormat(String verticalFormat) {
		return verticalFormat != null ? verticalFormat : "0";
	}

	private String getConvert(String convert) {
		return convert != null ? convert : "";
	}
}
