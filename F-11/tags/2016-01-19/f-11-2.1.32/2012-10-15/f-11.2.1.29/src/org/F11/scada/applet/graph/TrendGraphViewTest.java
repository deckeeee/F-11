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

package org.F11.scada.applet.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.plaf.metal.MetalLookAndFeel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.test.util.TimestampUtil;
import org.F11.scada.theme.DefaultWifeTheme;
import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.collections.primitives.DoubleList;
import org.xml.sax.SAXException;


public class TrendGraphViewTest {
	static {
		MetalLookAndFeel.setCurrentTheme(new DefaultWifeTheme());
	}

	public static void main(String[] args) {
		try {
			createJim();

			JFrame frame = new JFrame("Test");
			
			Container container = frame.getContentPane();
			container.setForeground(ColorFactory.getColor("black"));
			container.setBackground(ColorFactory.getColor("gray"));
			container.setLayout(null);

			GraphPropertyModel graphPropertyModel = new TestGraphPropertyModel();
			container.add(getGraphView(graphPropertyModel));
			container.add(getScale(graphPropertyModel));

			TestCondencerGraphPropertyModel condencerGraphPropertyModel =
				new TestCondencerGraphPropertyModel();
			container.add(getCondencerView(condencerGraphPropertyModel,
					new Rectangle(0, 460, 550, 50)));
			container.add(getCondencerView(condencerGraphPropertyModel,
					new Rectangle(0, 502, 550, 50)));
			container.add(getCondencerView(condencerGraphPropertyModel,
					new Rectangle(0, 544, 550, 50)));

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(700, 800);
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static VerticallyScale getCondencerScale(TestCondencerGraphPropertyModel condencerGraphPropertyModel, Rectangle rectangle) {
		VerticallyScale conScale = VerticallyScale.createRightStringScale(condencerGraphPropertyModel, 0, true, false, null);
		conScale.setScaleButtonVisible(false);
		conScale.setForeground(ColorFactory.getColor("black"));
		conScale.setBackground(ColorFactory.getColor("gray"));
		conScale.setBounds(rectangle);
		return conScale;
	}

	private static Component getCondencerView(
			TestCondencerGraphPropertyModel condencerGraphPropertyModel, 
			Rectangle rectangle)
			throws IOException, SAXException {
		TrendGraphView condencerview = new TrendGraphView(
				condencerGraphPropertyModel, new TestGraphModelFactory("con"), true, false, false);
		condencerview.setForeground(ColorFactory.getColor("white"));
		condencerview.setBackground(ColorFactory.getColor("navy"));
		condencerview.setStringColor(ColorFactory.getColor("black"));
		condencerview.setBounds(rectangle);
		return condencerview;
	}

	private static VerticallyScale getScale(GraphPropertyModel graphPropertyModel) {
		VerticallyScale rightVerticallyScale = VerticallyScale
				.createRightStringScale(graphPropertyModel, 0, false, true, null);
		rightVerticallyScale.setScaleButtonVisible(false);
		rightVerticallyScale.setForeground(ColorFactory.getColor("black"));
		rightVerticallyScale.setBackground(ColorFactory.getColor("gray"));
		rightVerticallyScale.setBounds(550, 3, 60, 500);
		return rightVerticallyScale;
	}

	private static TrendGraphView getGraphView(GraphPropertyModel graphPropertyModel) throws IOException, SAXException {
		TrendGraphView view = new TrendGraphView(
				graphPropertyModel, new TestGraphModelFactory("nomal"), true, false, true);
		view.setScrollBarVisible(false);
		view.setForeground(ColorFactory.getColor("white"));
		view.setBackground(ColorFactory.getColor("navy"));
		view.setStringColor(ColorFactory.getColor("black"));
		view.setBounds(0, 0, 550, 500);
		return view;
	}
	
	static void createJim() {
		try {
			DataProvider dp = TestUtil.createDP();
			DataHolder dh = TestUtil.createAnalogHolder("H1");
			dp.addDataHolder(dh);
			Manager.getInstance().addDataProvider(dp);
		} catch (DataProviderDoesNotSupportException e) {
			e.printStackTrace();
		}
	}
	
	private static class TestGraphModelFactory implements GraphModelFactory {
		private final String kind;

		TestGraphModelFactory(String kind) {
			this.kind = kind;
		}

		public GraphModel getGraphModel(String handlerName, List holderStrings) {
			if (!"con".equals(kind)) {
				return new TestGraphModel();
			} else {
				return new TestCondencerGraphModel();
			}
		}

		public GraphModel getGraphModel(String handlerName, List holderStrings, int maxMapSize) {
			if (!"con".equals(kind)) {
				return new TestGraphModel();
			} else {
				return new TestCondencerGraphModel();
			}
		}

		public String getModeName() {
			return "TestGraphModel";
		}
		
		private static class TestGraphModel implements GraphModel {
			private SwingPropertyChangeSupport listener;
			private TreeMap map;
			private Iterator iterator;
			TestGraphModel() {
				map = new TreeMap();

				addMap("2005/08/29 18:18:00", new double[]{10.0, 20.1, 30.2, 40.3, 50.4, 60.5});
				addMap("2005/08/29 18:19:00", new double[]{20.0, 30.1, 40.2, 50.3, 60.4, 70.5});
				addMap("2005/08/29 18:20:00", new double[]{30.0, 40.1, 50.2, 60.3, 70.4, 80.5});
				addMap("2005/08/29 18:21:00", new double[]{10.0, 20.1, 30.2, 40.3, 50.4, 60.5});
				addMap("2005/08/29 18:22:00", new double[]{20.0, 30.1, 40.2, 50.3, 60.4, 70.5});
				addMap("2005/08/29 18:23:00", new double[]{30.0, 40.1, 50.2, 60.3, 70.4, 80.5});
				listener = new SwingPropertyChangeSupport(this);
			}
			private void addMap(String time, double[] ds) {
				Timestamp key = TimestampUtil.parse(time);
				map.put(key, new LoggingData(key, createDoubleList(ds)));
			}
			private DoubleList createDoubleList(double[] ds) {
				ArrayDoubleList dl = new ArrayDoubleList();
				for (int i = 0; i < ds.length; i++) {
					dl.add(ds[i]);
				}
				return dl;
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {
				listener.addPropertyChangeListener(l);
			}

			public void findRecord(String name, Timestamp key) {
				iterator = map.values().iterator();
			}

			public Object firstKey(String name) {
				return map.firstKey();
			}

			public Object get(String name) {
				return iterator.next();
			}

			public Object lastKey(String name) {
				return map.lastKey();
			}

			public boolean next(String name) {
				return iterator.hasNext();
			}

			public void removePropertyChangeListener(PropertyChangeListener l) {
				listener.removePropertyChangeListener(l);
			}

			public void start() {
			}

			public void stop() {
			}
		}

		private static class TestCondencerGraphModel implements GraphModel {
			private SwingPropertyChangeSupport listener;
			private TreeMap map;
			private Iterator iterator;
			TestCondencerGraphModel() {
				map = new TreeMap();

				addMap("2005/08/29 18:18:00", new double[]{0.0});
				addMap("2005/08/29 18:19:00", new double[]{0.0});
				addMap("2005/08/29 18:20:00", new double[]{1.0});
				addMap("2005/08/29 18:21:00", new double[]{0.0});
				addMap("2005/08/29 18:22:00", new double[]{0.0});
				addMap("2005/08/29 18:23:00", new double[]{1.0});
				listener = new SwingPropertyChangeSupport(this);
			}
			private void addMap(String time, double[] ds) {
				Timestamp key = TimestampUtil.parse(time);
				map.put(key, new LoggingData(key, createDoubleList(ds)));
			}
			private DoubleList createDoubleList(double[] ds) {
				ArrayDoubleList dl = new ArrayDoubleList();
				for (int i = 0; i < ds.length; i++) {
					dl.add(ds[i]);
				}
				return dl;
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {
				listener.addPropertyChangeListener(l);
			}

			public void findRecord(String name, Timestamp key) {
				iterator = map.values().iterator();
			}

			public Object firstKey(String name) {
				return map.firstKey();
			}

			public Object get(String name) {
				return iterator.next();
			}

			public Object lastKey(String name) {
				return map.lastKey();
			}

			public boolean next(String name) {
				return iterator.hasNext();
			}

			public void removePropertyChangeListener(PropertyChangeListener l) {
				listener.removePropertyChangeListener(l);
			}

			public void start() {
			}

			public void stop() {
			}
		}
	}
	
	private static class TestGraphPropertyModel implements GraphPropertyModel {
		private SwingPropertyChangeSupport listener;
		TestGraphPropertyModel() {
			listener = new SwingPropertyChangeSupport(this);
		}

		public void addPropertyChangeListener(PropertyChangeListener l) {
			listener.addPropertyChangeListener(l);
		}

		public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
			listener.addPropertyChangeListener(propertyName, l);
		}

		public void addSeriesProperty(GraphSeriesProperty property) {
		}

		public GraphPropertyModel deepCopy() {
			return null;
		}

		public Color[] getColors() {
			return new Color[]{Color.RED, Color.YELLOW, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.WHITE};
		}

		public String getDataHolderName(int series) {
			return "H1";
		}

		public String getDataProviderName(int series) {
			return "P1";
		}

		public Color getExplanatoryColor() {
			return Color.RED;
		}

		public Font getExplanatoryFont() {
			return new Label().getFont();
		}

		public String getFirstFormat() {
			return "M/dd";
		}

		public int getFoldCount() {
			return 0;
		}

		public Insets getGraphiViewInsets() {
			return new Insets(10, 15, 60, 15);
		}

		public int getGroup() {
			return 0;
		}

		public void nextGroup() {
		}

		public void prevGroup() {
		}

		public int[] getGroupColumnIndex() {
			return new int[0];
		}

		public String getGroupName() {
			return "GROUP0";
		}

		public Collection getGroupNames() {
			return Arrays.asList(new String[]{"GROUP0", "GROUP1", "GROUP2", "GROUP3", "GROUP4", "GROUP5"});
		}

		public int getGroupSize() {
			return 6;
		}

		public int getHorizontalPixcelWidth() {
			return 500;
		}

		public int getHorizontalScaleCount() {
			return 6;
		}

		public long getHorizontalScaleWidth() {
			return 60000;
		}

		public Insets getInsets() {
			return new Insets(10, 0, 0, 0);
		}

		public String getListHandlerName() {
			return "";
		}

		public String getPointMark(int series) {
			return "";
		}

		public String getPointName(int series) {
			return "";
		}

		public Timestamp getReferenceTime() {
			return null;
		}

		public Timestamp getReferenceTime(int series, int fold) {
			return null;
		}

		public Timestamp getReferenceTime(int series) {
			return null;
		}

		public double getReferenceValue(int series, int fold) {
			return 0;
		}

		public double getReferenceValue(int series) {
			return 0;
		}

		public int getScaleOneHeightPixel() {
			return 10;
		}

		public String getSecondFormat() {
			return "HH:mm";
		}

		public int getSeriesSize() {
			return 6;
		}

		public ExplanatoryNotesText getSymbol(int series) {
			return null;
		}

		public double getVerticalMaximum(int series) {
			return 100;
		}

		public double getVerticalMinimum(int series) {
			return 0;
		}

		public int getVerticalScaleCount() {
			return 10;
		}

		public int getVerticalScaleHeight() {
			return 40;
		}

		public void removePropertyChangeListener(PropertyChangeListener l) {
			listener.removePropertyChangeListener(l);
		}

		public void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
			listener.removePropertyChangeListener(propertyName, l);
		}

		public void setFirstFormat(String format) {
		}

		public void setGroup(int group) {
		}

		public void setHorizontalScaleCount(int horizontalScaleCount) {
		}

		public void setHorizontalScaleWidth(long horizontalScaleWidth) {
		}

		public void setListHandlerIndex(int index) {
		}

		public void setReferenceTime(int series, int fold, Timestamp referenceTime) {
		}

		public void setReferenceTime(int series, Timestamp referenceTime) {
		}

		public void setReferenceTime(Timestamp time) {
		}

		public void setReferenceValue(int series, double referenceValue) {
		}

		public void setReferenceValue(int series, int fold, double referenceValue) {
		}

		public void setSecondFormat(String format) {
		}

		public void setVerticalMaximum(int series, double verticalMaximum) {
		}

		public void setVerticalMinimum(int series, double verticalMinimum) {
		}
		public VerticallyScaleProperty getVerticallyScaleProperty() {
			return new VerticallyScaleProperty(Color.GRAY, Color.BLACK, Color.WHITE);
		}
	}
	
	private static class TestCondencerGraphPropertyModel implements GraphPropertyModel {
		private SwingPropertyChangeSupport listener;
		TestCondencerGraphPropertyModel() {
			listener = new SwingPropertyChangeSupport(this);
		}

		public void addPropertyChangeListener(PropertyChangeListener l) {
			listener.addPropertyChangeListener(l);
		}

		public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
			listener.addPropertyChangeListener(propertyName, l);
		}

		public void addSeriesProperty(GraphSeriesProperty property) {
		}

		public GraphPropertyModel deepCopy() {
			return null;
		}

		public Color[] getColors() {
			return new Color[]{Color.RED, Color.YELLOW, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.WHITE};
		}

		public String getDataHolderName(int series) {
			return "H1";
		}

		public String getDataProviderName(int series) {
			return "P1";
		}

		public Color getExplanatoryColor() {
			return Color.RED;
		}

		public Font getExplanatoryFont() {
			return new Label().getFont();
		}

		public String getFirstFormat() {
			return "M/dd";
		}

		public int getFoldCount() {
			return 0;
		}

		public Insets getGraphiViewInsets() {
			return new Insets(1, 15, 1, 15);
		}

		public int getGroup() {
			return 0;
		}

		public void nextGroup() {
		}

		public void prevGroup() {
		}

		public int[] getGroupColumnIndex() {
			return new int[0];
		}

		public String getGroupName() {
			return "GROUP0";
		}

		public Collection getGroupNames() {
			return Arrays.asList(new String[]{"GROUP0", "GROUP1", "GROUP2", "GROUP3", "GROUP4", "GROUP5"});
		}

		public int getGroupSize() {
			return 6;
		}

		public int getHorizontalPixcelWidth() {
			return 500;
		}

		public int getHorizontalScaleCount() {
			return 6;
		}

		public long getHorizontalScaleWidth() {
			return 60000;
		}

		public Insets getInsets() {
			return new Insets(5, 0, 0, 0);
		}

		public String getListHandlerName() {
			return "";
		}

		public String getPointMark(int series) {
			return "";
		}

		public String getPointName(int series) {
			return "";
		}

		public Timestamp getReferenceTime() {
			return null;
		}

		public Timestamp getReferenceTime(int series, int fold) {
			return null;
		}

		public Timestamp getReferenceTime(int series) {
			return null;
		}

		public double getReferenceValue(int series, int fold) {
			return 0;
		}

		public double getReferenceValue(int series) {
			return 0;
		}

		public int getScaleOneHeightPixel() {
			return 10;
		}

		public String getSecondFormat() {
			return "HH:mm";
		}

		public int getSeriesSize() {
			return 1;
		}

		public ExplanatoryNotesText getSymbol(int series) {
			return null;
		}

		public double getVerticalMaximum(int series) {
			return 1;
		}

		public double getVerticalMinimum(int series) {
			return 0;
		}

		public int getVerticalScaleCount() {
			return 1;
		}

		public int getVerticalScaleHeight() {
			return 40;
		}

		public void removePropertyChangeListener(PropertyChangeListener l) {
			listener.removePropertyChangeListener(l);
		}

		public void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
			listener.removePropertyChangeListener(propertyName, l);
		}

		public void setFirstFormat(String format) {
		}

		public void setGroup(int group) {
		}

		public void setHorizontalScaleCount(int horizontalScaleCount) {
		}

		public void setHorizontalScaleWidth(long horizontalScaleWidth) {
		}

		public void setListHandlerIndex(int index) {
		}

		public void setReferenceTime(int series, int fold, Timestamp referenceTime) {
		}

		public void setReferenceTime(int series, Timestamp referenceTime) {
		}

		public void setReferenceTime(Timestamp time) {
		}

		public void setReferenceValue(int series, double referenceValue) {
		}

		public void setReferenceValue(int series, int fold, double referenceValue) {
		}

		public void setSecondFormat(String format) {
		}

		public void setVerticalMaximum(int series, double verticalMaximum) {
		}

		public void setVerticalMinimum(int series, double verticalMinimum) {
		}
		public VerticallyScaleProperty getVerticallyScaleProperty() {
			return new VerticallyScaleProperty(Color.GRAY, Color.BLACK, Color.WHITE);
		}
	}
}
