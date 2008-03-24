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

package org.F11.scada.tool.page.trend;

import java.rmi.RemoteException;

import org.F11.scada.tool.io.PageListBean;
import org.F11.scada.tool.page.parser.DOMPageDefine;
import org.F11.scada.tool.page.parser.trend.SeriesBean;
import org.F11.scada.tool.page.parser.trend.TrendGraphDefine;
import org.F11.scada.util.RmiUtil;

public class TrendServiceImpl implements TrendService {
	public TrendServiceImpl(int regport, int objport) {
		RmiUtil.registryServer(this, TrendService.class, regport, objport);
	}

	public void addSeries(PageListBean page, SeriesBean series)
			throws RemoteException {
		try {
			DOMPageDefine pageDefine = new DOMPageDefine(page.getPageXmlPath());
			TrendGraphDefine trendDefine = pageDefine.getTrendGraphDefine();
			trendDefine.addSeries(series);
			pageDefine.send();
		} catch (Exception e) {
			throw new RemoteException("トレンド定義送信時にエラー発生 : ", e);
		}
	}

	public void removeSeries(PageListBean page, int id) throws RemoteException {
		try {
			DOMPageDefine pageDefine = new DOMPageDefine(page.getPageXmlPath());
			TrendGraphDefine trendDefine = pageDefine.getTrendGraphDefine();
			trendDefine.removeSeries(id);
			pageDefine.send();
		} catch (Exception e) {
			throw new RemoteException("トレンド定義送信時にエラー発生 : ", e);
		}
	}

	public void replaceSeries(PageListBean page, SeriesBean series)
			throws RemoteException {
		try {
			DOMPageDefine pageDefine = new DOMPageDefine(page.getPageXmlPath());
			TrendGraphDefine trendDefine = pageDefine.getTrendGraphDefine();
			trendDefine.replaceSeries(series);
			pageDefine.send();
		} catch (Exception e) {
			throw new RemoteException("トレンド定義送信時にエラー発生 : ", e);
		}
	}

}
