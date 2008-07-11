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

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * DefaultGraphPropertyModelのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DefaultGraphPropertyModelTest extends TestCase {
	GraphPropertyModel model1;
	GraphPropertyModel model2;
	
	GraphSeriesProperty property;

	/**
	 * Constructor for DefaultGraphPropertyModelTest.
	 * @param arg0
	 */
	public DefaultGraphPropertyModelTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		Insets insets = new Insets(60, 10, 50, 10);
		Insets graphiViewInsets = new Insets(60, 30, 50, 30);
		Color[] colors = new Color[]{Color.red, Color.green};
		String[] handlerName = new String[]{"log_table_minute", "log_table_hour"};

		model1 = new DefaultGraphPropertyModel(10, 45, 4, 3600000, insets,
				graphiViewInsets, colors, handlerName, 0, 560, 5, Color.BLACK,
				new Font("dialog", Font.PLAIN, 12), "yyyy/MM/dd", "HH:mm:ss",
				new VerticallyScaleProperty(Color.GRAY, Color.BLACK, Color.WHITE));

		insets = new Insets(61, 11, 51, 11);
		graphiViewInsets = new Insets(61, 31, 51, 31);
		colors = new Color[]{Color.blue, Color.green};
		handlerName = new String[]{"log_table_minute1", "log_table_hour1"};

		model2 = new DefaultGraphPropertyModel(11, 46, 5, 3600001, insets,
				graphiViewInsets, colors, handlerName, 0, 560, 5, Color.BLACK,
				new Font("dialog", Font.PLAIN, 12), "yyyy/MM/dd", "HH:mm:ss",
				new VerticallyScaleProperty(Color.GRAY, Color.BLACK, Color.WHITE));
		
		property = new GraphSeriesProperty(
			1,
			new double[]{0},
			new double[]{100},
			new double[]{0},
			new double[]{4000},
			new String[]{"100"},
			new String[]{"200"},
			0,
			"気温",
			new String[] {"機器1"},
			new String[] {"℃"},
			new ExplanatoryNotesText[]{new ExplanatoryNotesText("P1", "H1")}
		);
	}

	public void testDeepCopy() {
		GraphPropertyModel model = model1.deepCopy();
		assertEquals(model1, model);
		assertTrue(model1 != model);
	}

	public void testHashCode() {
		Insets insets = new Insets(60, 10, 50, 10);
		Insets graphiViewInsets = new Insets(60, 30, 50, 30);
		Color[] colors = new Color[]{Color.red, Color.green};
		String[] handlerName = new String[]{"log_table_minute", "log_table_hour"};

		GraphPropertyModel model = new DefaultGraphPropertyModel(10, 45, 4,
				3600000, insets, graphiViewInsets, colors, handlerName, 0, 560,
				5, Color.BLACK, new Font("dialog", Font.PLAIN, 12),
				"yyyy/MM/dd", "HH:mm:ss",
				new VerticallyScaleProperty(Color.GRAY, Color.BLACK, Color.WHITE));
		assertEquals(model.hashCode(), model1.hashCode());
		assertTrue(model1.hashCode() != model2.hashCode());
	}

	public void testGetVerticalScaleCount() {
		assertEquals(10, model1.getVerticalScaleCount());
	}

	public void testGetVerticalScaleHeight() {
		assertEquals(45, model1.getVerticalScaleHeight());
	}

	public void testGetHorizontalScaleCount() {
		assertEquals(4, model1.getHorizontalScaleCount());
	}

	public void testGetHorizontalScaleWidth() {
		assertEquals(3600000, model1.getHorizontalScaleWidth());
	}

	public void testSetHorizontalScaleCount() {
		ListenerTest listener = new ListenerTest();
		model1.addPropertyChangeListener(GraphPropertyModel.X_SCALE_CHANGE_EVENT, listener);
		
		model1.setHorizontalScaleCount(100);
		assertEquals(100, model1.getHorizontalScaleCount());
		assertEquals(1, listener.getOnEvent());
	}

	public void testSetHorizontalScaleWidth() {
		ListenerTest listener = new ListenerTest();
		model1.addPropertyChangeListener(GraphPropertyModel.X_SCALE_CHANGE_EVENT, listener);
		
		model1.setHorizontalScaleWidth(100);
		assertEquals(100, model1.getHorizontalScaleWidth());
		assertEquals(1, listener.getOnEvent());
	}

	public void testGetInsets() {
		Insets insets = new Insets(60, 10, 50, 10);
		assertEquals(insets, model1.getInsets());
	}

	public void testGetGraphiViewInsets() {
		Insets graphiViewInsets = new Insets(60, 30, 50, 30);
		assertEquals(graphiViewInsets, model1.getGraphiViewInsets());
	}

	public void testGetColors() {
		Color[] colors = new Color[]{Color.red, Color.green};
		assertTrue(Arrays.equals(colors, model1.getColors()));
	}

	public void testGetGroupSize() {
		assertEquals(0, model1.getGroupSize());
	}

	public void testSetGroup() {
		try {
			model1.setGroup(1);
			fail();
		} catch (IllegalArgumentException ex) {
		}

		ListenerTest listener = new ListenerTest();
		model1.addPropertyChangeListener(listener);
				
		model1.addSeriesProperty(property);
		model1.addSeriesProperty(property);
		model1.setGroup(1);
		
		assertEquals(2, listener.getOnEvent());
	}

	public void testGetSeriesSize() {
		try {
			model1.getSeriesSize();
			fail();
		} catch (IllegalArgumentException ex) {
		}
		model1.addSeriesProperty(property);
		assertEquals(1, model1.getSeriesSize());
	}

	public void testGetVerticalMinimum() {
		try {
			model1.getVerticalMinimum(0);
			fail();
		} catch (IllegalArgumentException ex) {
		}
		model1.addSeriesProperty(property);
		assertEquals(0, model1.getVerticalMinimum(0), 0);
	}

	public void testGetVerticalMaximum() {
		try {
			model1.getVerticalMaximum(0);
			fail();
		} catch (IllegalArgumentException ex) {
		}
		model1.addSeriesProperty(property);
		assertEquals(100, model1.getVerticalMaximum(0), 0);
	}

	public void testSetVerticalMinimum() {
		ListenerTest listener = new ListenerTest();
		model1.addPropertyChangeListener(listener);
		
		try {
			model1.setVerticalMinimum(0, 100);
			fail();
		} catch (IllegalArgumentException ex) {
		}
		model1.addSeriesProperty(property);
		model1.setVerticalMinimum(0, 100);
		assertEquals(100, model1.getVerticalMinimum(0), 0);
		assertEquals(2, listener.getOnEvent());
	}

	public void testSetVerticalMaximum() {
		ListenerTest listener = new ListenerTest();
		model1.addPropertyChangeListener(listener);
		
		try {
			model1.setVerticalMaximum(0, 1000);
			fail();
		} catch (IllegalArgumentException ex) {
		}
		model1.addSeriesProperty(property);
		model1.setVerticalMinimum(0, 1000);
		assertEquals(1000, model1.getVerticalMinimum(0), 0);
		assertEquals(2, listener.getOnEvent());
	}

	public void testGetDataProviderName() {
		try {
			model1.getDataProviderName(0);
			fail();
		} catch (IllegalArgumentException ex) {
		}
		model1.addSeriesProperty(property);
		assertEquals("100", model1.getDataProviderName(0));
	}

	public void testGetDataHolderName() {
		try {
			model1.getDataHolderName(0);
			fail();
		} catch (IllegalArgumentException ex) {
		}
		model1.addSeriesProperty(property);
		assertEquals("200", model1.getDataHolderName(0));
	}

	public void testGetReferenceValue() {
		try {
			model1.getReferenceValue(0);
			fail();
		} catch (IllegalArgumentException ex) {
		}
		model1.addSeriesProperty(property);
		assertEquals(0, model1.getReferenceValue(0), 0);
	}

	/*
	 * void setReferenceValue のテスト(int, double)
	 */
	public void testSetReferenceValueID() {
		ListenerTest listener = new ListenerTest();
		model1.addPropertyChangeListener(listener);
		
		try {
			model1.setReferenceValue(0, 1000);
			fail();
		} catch (IllegalArgumentException ex) {
		}

		property = new GraphSeriesProperty(
			2,
			new double[]{0, 0},
			new double[]{100, 100},
			new double[]{0, 0},
			new double[]{4000, 4000},
			new String[]{"100", "100"},
			new String[]{"200", "200"},
			0,
			"気温",
			new String[] {"機器1", "機器1"},
			new String[] {"℃", "℃"},
			new ExplanatoryNotesText[]{new ExplanatoryNotesText("P1", "H1")}
		);

		model1.addSeriesProperty(property);
		model1.setReferenceValue(0, 1000);
		model1.setReferenceValue(1, 2000);
		assertEquals(1000, model1.getReferenceValue(0), 0);
		assertEquals(2000, model1.getReferenceValue(1), 0);
		assertEquals(3, listener.getOnEvent());
	}

	public void testGetGroupColumnIndex() {
	}

	public void testSetListHandlerIndex() {
		model1.setListHandlerIndex(0);
		assertEquals("log_table_minute", model1.getListHandlerName());
		model1.setListHandlerIndex(1);
		assertEquals("log_table_hour", model1.getListHandlerName());
		
		try {
			model1.setListHandlerIndex(2);
			fail();
		} catch (IllegalArgumentException ex) {
		}

		try {
			model1.setListHandlerIndex(-1);
			fail();
		} catch (IllegalArgumentException ex) {
		}
	}

	/*
	 * boolean equals のテスト(Object)
	 */
	public void testEqualsObject() {
		Insets insets = new Insets(60, 10, 50, 10);
		Insets graphiViewInsets = new Insets(60, 30, 50, 30);
		Color[] colors = new Color[]{Color.red, Color.green};
		String[] handlerName = new String[]{"log_table_minute", "log_table_hour"};

		GraphPropertyModel model = new DefaultGraphPropertyModel(10, 45, 4,
				3600000, insets, graphiViewInsets, colors, handlerName, 0, 560,
				5, Color.BLACK, new Font("dialog", Font.PLAIN, 12),
				"yyyy/MM/dd", "HH:mm:ss",
				new VerticallyScaleProperty(Color.GRAY, Color.BLACK, Color.WHITE));
		assertEquals(model, model1);
		assertTrue(model1 != model2);
		assertTrue(model != model2);
		model.addSeriesProperty(property);
		assertTrue(model != model1);

		model = new DefaultGraphPropertyModel(10, 45, 4, 3600000, insets,
				graphiViewInsets, colors, new String[] { "log_table_minute" },
				0, 560, 5, Color.BLACK, new Font("dialog", Font.PLAIN, 12),
				"yyyy/MM/dd", "HH:mm:ss",
				new VerticallyScaleProperty(Color.GRAY, Color.BLACK, Color.WHITE));
		assertTrue(model != model1);
	}

	public void testSetReferenceTime() {
		ListenerTest listener = new ListenerTest();
		model1.addPropertyChangeListener(listener);
		Timestamp t = new Timestamp(System.currentTimeMillis());
		
		try {
			model1.setReferenceTime(0, t);
			fail();
		} catch (IllegalArgumentException ex) {
		}
		model1.addSeriesProperty(property);
		model1.setReferenceTime(0, t);
		assertEquals(t, model1.getReferenceTime(0));
		assertEquals(2, listener.getOnEvent());
	}

	public void testGetGroupName() {
		model1.addSeriesProperty(property);
		model1.addSeriesProperty(
			new GraphSeriesProperty(
				1,
				new double[]{0},
				new double[]{100},
				new double[]{0},
				new double[]{4000},
				new String[]{"100"},
				new String[]{"200"},
				0,
				"電力",
				new String[] {"機器2"},
				new String[] {"℃"},
				new ExplanatoryNotesText[]{new ExplanatoryNotesText("P1", "H1")})
				);
		assertEquals("気温", model1.getGroupName());
		model1.setGroup(1);
		assertEquals("電力", model1.getGroupName());
	}


	public void testGetPointNameMark() {
		model1.addSeriesProperty(property);
		model1.addSeriesProperty(
			new GraphSeriesProperty(
				1,
				new double[]{0},
				new double[]{100},
				new double[]{0},
				new double[]{4000},
				new String[]{"100"},
				new String[]{"200"},
				0,
				"電力",
				new String[] {"機器2"},
				new String[] {"％"},
				new ExplanatoryNotesText[]{new ExplanatoryNotesText("P1", "H1")}));
		assertEquals("機器1", model1.getPointName(0));
		assertEquals("℃", model1.getPointMark(0));
		model1.setGroup(1);
		assertEquals("機器2", model1.getPointName(0));
		assertEquals("％", model1.getPointMark(0));
	}
	
	public void testGetSymbol() throws Exception {
		model1.addSeriesProperty(property);
		model1.addSeriesProperty(
			new GraphSeriesProperty(
				1,
				new double[]{0},
				new double[]{100},
				new double[]{0},
				new double[]{4000},
				new String[]{"100"},
				new String[]{"200"},
				0,
				"電力",
				new String[] {"機器2"},
				new String[] {"％"},
				new ExplanatoryNotesText[]{new ExplanatoryNotesText("P1", "H1")}));
				
		assertTrue(model1.getSymbol(0) instanceof ExplanatoryNotesText);
	}
	
	public void testReferenceTime() throws Exception {
		ListenerTest l = new ListenerTest();
		model1.addPropertyChangeListener(l);
		assertNull(model1.getReferenceTime());
		Timestamp time = new Timestamp(System.currentTimeMillis());
		model1.setReferenceTime(time);
		assertNotNull(model1.getReferenceTime());
		assertEquals(time, model1.getReferenceTime());
		assertEquals(1, l.getOnEvent());
	}
	
	public void testGetHorizontalPixcelWidth() {
		assertEquals(560, model1.getHorizontalPixcelWidth());
	}
	
	public void testGetScaleOneHeightPixel() {
		assertEquals(5, model1.getScaleOneHeightPixel());
	}


	/**
	 * プロパティリスナーのテストクラスです。
	 */
	static class ListenerTest implements PropertyChangeListener {
		private volatile int onEvent;
		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			onEvent++;
		}
		
		public int getOnEvent() {
			return onEvent;
		}
	}
}
