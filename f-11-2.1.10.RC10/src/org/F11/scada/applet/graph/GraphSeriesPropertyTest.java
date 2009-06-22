/*
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
package org.F11.scada.applet.graph;

import java.sql.Timestamp;

import junit.framework.TestCase;

/**
 * GraphSeriesPropertyのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class GraphSeriesPropertyTest extends TestCase {
	GraphSeriesProperty series;

	/**
	 * Constructor for GraphSeriesPropertyTest.
	 * @param arg0
	 */
	public GraphSeriesPropertyTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		series =
			new GraphSeriesProperty(
				1,
				new double[] { 0, },
				new double[] { 100, },
				new double[] { 0, },
				new double[] { 4000, },
				new String[] { "P1", },
				new String[] { "D_500_BcdSingle", },
				0,
				"気温",
				new String[] {"機器1"},
				new String[] {"℃"},
				new ExplanatoryNotesText[]{new ExplanatoryNotesText("P1", "H1")});
	}

	public void testHashCode() {
		assertEquals(
			new GraphSeriesProperty(
				1,
				new double[] { 0, },
				new double[] { 100, },
				new double[] { 0, },
				new double[] { 4000, },
				new String[] { "P1", },
				new String[] { "D_500_BcdSingle", },
				0,
				"気温",
				new String[] {"機器1"},
				new String[] {"℃"},
				new ExplanatoryNotesText[]{new ExplanatoryNotesText("P1", "H1")})
				.hashCode(),
				series.hashCode());
	}

	/*
	 * void GraphSeriesProperty のテスト(GraphSeriesProperty)
	 */
	public void testGraphSeriesPropertyGraphSeriesProperty() {
		GraphSeriesProperty copy = new GraphSeriesProperty(series);
		assertEquals(series, copy);
	}

	public void testSeries() {
		assertEquals(1, series.getSeriesSize());
		assertEquals(0, series.getVerticalMinimum(0), 0);
		assertEquals(100, series.getVerticalMaximum(0), 0);
		assertEquals(0, series.getVerticalInputMinimum(0), 0);
		assertEquals(4000, series.getVerticalInputMaximum(0), 0);

		series.setVerticalMinimum(0, 1000);
		assertEquals(1000, series.getVerticalMinimum(0), 0);
		series.setVerticalMaximum(0, 100000);
		assertEquals(100000, series.getVerticalMaximum(0), 0);

		assertEquals("P1", series.getDataProviderName(0));
		assertEquals("D_500_BcdSingle", series.getDataHolderName(0));
		assertEquals(0, series.getReferenceValue(0, 0), 0);
		series.setReferenceValue(0, 0, 1000);
		assertEquals(1000, series.getReferenceValue(0, 0), 0);
		assertEquals("気温", series.getSeriesName());
		assertEquals("機器1", series.getPointName(0));
		assertEquals("℃", series.getPointMark(0));
	}

	/*
	 * boolean equals のテスト(Object)
	 */
	public void testEqualsObject() {
		GraphSeriesProperty obj =
			new GraphSeriesProperty(
				1,
				new double[] { 0, },
				new double[] { 100, },
				new double[] { 0, },
				new double[] { 4000, },
				new String[] { "P1", },
				new String[] { "D_500_BcdSingle", },
				0,
				"気温",
				new String[] {"機器1"},
				new String[] {"℃"},
				new ExplanatoryNotesText[]{new ExplanatoryNotesText("P1", "H1")});
		assertTrue(series.equals(obj));
	}

	public void testGetReferenceTime() {
		assertEquals(new Timestamp(0), series.getReferenceTime(0, 0));
	}

	public void testSetReferenceTime() {
		long now = System.currentTimeMillis();
		series.setReferenceTime(0, 0, new Timestamp(now));
		assertEquals(new Timestamp(now), series.getReferenceTime(0, 0));
	}

	/*
	 * 折り返しデータのテスト
	 */
	public void testECopyEquals() {
		GraphSeriesProperty obj2 =
			new GraphSeriesProperty(series);

		assertTrue(series.hashCode() == obj2.hashCode());
		assertTrue(series.equals(obj2));

		obj2.setReferenceValue(0, 0, 1.0);
		
		assertFalse(series.hashCode() == obj2.hashCode());
		assertFalse(series.equals(obj2));
	}
	
	public void testReferenceFoldValue() {
		GraphSeriesProperty obj =
			new GraphSeriesProperty(
				1,
				new double[] { 0, },
				new double[] { 100, },
				new double[] { 0, },
				new double[] { 4000, },
				new String[] { "P1", },
				new String[] { "D_500_BcdSingle", },
				2,
				"気温",
				new String[] {"機器1"},
				new String[] {"℃"},
				new ExplanatoryNotesText[]{new ExplanatoryNotesText("P1", "H1")});

		obj.setReferenceValue(0, 1, 2000);
		obj.setReferenceValue(0, 2, 1000);
		assertEquals(2000, obj.getReferenceValue(0, 1), 0);
		assertEquals(1000, obj.getReferenceValue(0, 2), 0);
		
		long now = System.currentTimeMillis();
		obj.setReferenceTime(0, 1, new Timestamp(now));
		assertEquals(new Timestamp(now), obj.getReferenceTime(0, 1));
	}
}
