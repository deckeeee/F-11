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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.TestCase;

import org.F11.scada.applet.ngraph.HorizontalScaleButtonProperty;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class TrendRuleSetTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParse() throws Exception {
		PageData page = new PageData();
		Digester digester = new Digester();
		digester.addRuleSet(new TrendRuleSet());
		digester.push(page);

		BufferedReader xml =
			new BufferedReader(
				new InputStreamReader(
					TrendRuleSetTest.class
						.getResourceAsStream("/org/F11/scada/applet/ngraph/editor/trend4.xml"),
					"UTF-8"));
		try {
			digester.parse(xml);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		assertEquals(new Integer(1028), page.getWidth());
		assertEquals(new Integer(800), page.getHeight());
		assertEquals("TRND0004", page.getName());
		assertEquals("/images/base/back.png", page.getValue());
		assertEquals("true", page.getCache());

		Trend3Data td3 = page.getTrend3Data();
		assertEquals(new Integer(0), td3.getX());
		assertEquals(new Integer(0), td3.getY());
		assertEquals(new Integer(1028), td3.getWidth());
		assertEquals(new Integer(800), td3.getHeight());
		assertEquals(new Integer(1), td3.getHorizontalForAllSpanMode());
		assertEquals(new Integer(1), td3.getHorizontalForSelectSpanMode());
		assertEquals("%1$tm", td3.getDateFormat());
		assertEquals("%1$tH", td3.getTimeFormat());
		assertEquals(new Integer(1), td3.getVerticalScale());
		assertEquals(new Integer(1), td3.getVerticalCount());
		assertEquals(new Integer(1), td3.getScalePixcelSize());
		assertEquals("50, 80, 60, 50", td3.getInsets());
		assertEquals("Monospaced-PLAIN-1", td3.getFont());
		assertEquals("black", td3.getLineColor());
		assertEquals("gray", td3.getBackGround());
		assertEquals("white", td3.getVerticalScaleColor());
		assertEquals("trend1.xml", td3.getPagefile());
		assertEquals("yellow, magenta, cyan, red, lime, white", td3
			.getSeriesColors());
		assertEquals(new Integer(2000), td3.getMaxRecord());
		assertTrue(td3.isVisibleReferenceLine());
		assertTrue(td3.isVisibleScroolbar());
		assertTrue(td3.isVisibleSeries());
		assertTrue(td3.isVisibleStatus());
		assertTrue(td3.isVisibleToolbar());
		assertTrue(td3.isVisibleVerticalString());
		assertTrue(td3.isCompositionMode());
		assertTrue(td3.isAllSpanDisplayMode());

		List<SeriesData> sds = td3.getSeriesDatas();
		assertEquals(2, sds.size());

		SeriesData sd1 = sds.get(0);
		assertEquals(new Integer(1), sd1.getGroupNo());
		assertEquals("空調", sd1.getGroupName());

		List<SeriesPropertyData> spds = sd1.getSeriesProperties();
		assertEquals(6, spds.size());
		SeriesPropertyData spd = spds.get(0);
		assertEquals(new Integer(0), spd.getIndex());
		assertEquals(Boolean.TRUE, spd.isVisible());
		assertEquals("yellow", spd.getColor());
		assertEquals("AHU-1-1", spd.getUnit());
		assertEquals("空調機-1-1", spd.getName());
		assertEquals("", spd.getMark());
		assertEquals("%3.0f", spd.getVerticalFormat());
		assertEquals(100F, spd.getMax());
		assertEquals(0F, spd.getMin());
		assertEquals("P1_D_500_BcdSingle", spd.getHolder());

		SeriesData sd2 = sds.get(1);
		assertEquals(new Integer(2), sd2.getGroupNo());
		assertEquals("デマンド", sd2.getGroupName());

		List<HorizontalScaleButtonProperty> buttonPropertys =
			td3.getHorizontalScaleButtonProperty();
		assertEquals(6, buttonPropertys.size());
		HorizontalScaleButtonProperty hbp = buttonPropertys.get(0);
		assertEquals("4時間", hbp.getButtonText());
		assertEquals("1分", hbp.getLabelText());
		assertEquals(new Integer(5), hbp.getHorizontalCount());
		assertEquals(new Integer(112), hbp.getHorizontalAllSpanMode());
		assertEquals(new Integer(168), hbp.getHorizontalSelectSpanMode());
		assertEquals(new Long(18000000), hbp.getHorizontalLineSpan());
		assertEquals(new Long(60000), hbp.getRecordeSpan());
		assertEquals("log_table_minute", hbp.getLogName());

		PageChangeButton pageChangeButton = page.getPageChangeButton();
		assertNotNull(pageChangeButton);
		assertEquals(new Integer(1110), pageChangeButton.getX());
		assertEquals(new Integer(40), pageChangeButton.getY());
		assertEquals(Boolean.TRUE, pageChangeButton.getOpaque());
		assertEquals(new Integer(69), pageChangeButton.getWidth());
		assertEquals(new Integer(20), pageChangeButton.getHeight());
		assertEquals("/images/pb_main.png", pageChangeButton.getValue());
		assertEquals("main", pageChangeButton.getChangeto());
	}
}
