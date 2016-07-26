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

package org.F11.scada.applet.ngraph.editor;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class PageDataTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetXmlString() {
		PageData pd = new PageData();
		pd.setWidth(100);
		pd.setHeight(200);
		pd.setName("TEST01");
		pd.setValue("/images/Back.png");
		Trend3Data trend3Data = new Trend3Data();
		trend3Data.setSeriesDatas(getSeriesDatas());
		pd.setTrend3Data(trend3Data);
		System.out.println(pd.getXmlString());
	}

	private List<SeriesData> getSeriesDatas() {
		ArrayList<SeriesData> l = new ArrayList<SeriesData>();
		SeriesData sd = new SeriesData();
		sd.setGroupName("GROUP1");
		sd.setGroupNo(1);
		sd.setSeriesProperties(getSeriesProperties());
		l.add(sd);
		return l;
	}

	private List<SeriesPropertyData> getSeriesProperties() {
		ArrayList<SeriesPropertyData> l = new ArrayList<SeriesPropertyData>();
		SeriesPropertyData sp = new SeriesPropertyData();
		l.add(sp);
		return l;
	}
}
