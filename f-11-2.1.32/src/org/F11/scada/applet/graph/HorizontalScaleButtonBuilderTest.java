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
 */
package org.F11.scada.applet.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;

import junit.framework.TestCase;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class HorizontalScaleButtonBuilderTest extends TestCase {

    public static void main(String[] args) {
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for HorizontalScaleButtonBuilderTest.
     * @param arg0
     */
    public HorizontalScaleButtonBuilderTest(String arg0) {
        super(arg0);
    }

    public void testCreate() {
        HorizontalScaleButtonBuilder builder =
            new HorizontalScaleButtonBuilder("/org/F11/scada/applet/graph/Test.xml");
        HorizontalScaleButtonFactory factory = builder.create();
        TestGraphPropertyModel model = new TestGraphPropertyModel();
        Collection collection = factory.getHorizontalScaleButtons(model);
        for (Iterator i = collection.iterator(); i.hasNext();) {
            JButton button = (JButton) i.next();
//            assertNotNull(button.getIcon());
            button.doClick();
            assertEquals(4, model.setHorizontalScaleCount);
            assertEquals((1 * 60 * 60 * 1000), model.setHorizontalScaleWidth);
            assertEquals(0, model.setListHandlerIndex);
            assertEquals("yyyy/MM/dd", model.firstFormat);
            assertEquals("HH:mm:ss", model.secondFormat);
        }
    }

    public void testFileNotFound() throws Exception {
        HorizontalScaleButtonBuilder builder =
            new HorizontalScaleButtonBuilder("/org/F11/scada/applet/graph/HogeHogeFoo.xml");
        try {
            builder.create();
            fail();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    
    public void testFileNullSet() throws Exception {
        HorizontalScaleButtonBuilder builder = new HorizontalScaleButtonBuilder(null);
        builder.create();
    }

    static class TestGraphPropertyModel implements GraphPropertyModel {
        int setHorizontalScaleCount;
        long setHorizontalScaleWidth;
        int setListHandlerIndex;
        String firstFormat;
        String secondFormat;
        
        public void addPropertyChangeListener(PropertyChangeListener l) {
        }
        public void addPropertyChangeListener(String propertyName,
                PropertyChangeListener listener) {
        }
        public void addSeriesProperty(GraphSeriesProperty property) {
        }
        public GraphPropertyModel deepCopy() {
            return null;
        }
        public Color[] getColors() {
            return null;
        }
        public String getDataHolderName(int series) {
            return null;
        }
        public String getDataProviderName(int series) {
            return null;
        }
        public Color getExplanatoryColor() {
            return null;
        }
        public Font getExplanatoryFont() {
            return null;
        }
        public int getFoldCount() {
            return 0;
        }
        public Insets getGraphiViewInsets() {
            return null;
        }
        public int getGroup() {
            return 0;
        }
        public void nextGroup() {
		}
		public void prevGroup() {
		}
		public int[] getGroupColumnIndex() {
            return null;
        }
        public String getGroupName() {
            return null;
        }
        public Collection getGroupNames() {
            return null;
        }
        public int getGroupSize() {
            return 0;
        }
        public int getHorizontalPixcelWidth() {
            return 0;
        }
        public int getHorizontalScaleCount() {
            return 0;
        }
        public long getHorizontalScaleWidth() {
            return 0;
        }
        public Insets getInsets() {
            return null;
        }
        public String getListHandlerName() {
            return null;
        }
        public String getPointMark(int series) {
            return null;
        }
        public String getPointName(int series) {
            return null;
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
            return 0;
        }
        public int getSeriesSize() {
            return 0;
        }
        public ExplanatoryNotesText getSymbol(int series) {
            return null;
        }
        public double getVerticalMaximum(int series) {
            return 0;
        }
        public double getVerticalMinimum(int series) {
            return 0;
        }
        public int getVerticalScaleCount() {
            return 0;
        }
        public int getVerticalScaleHeight() {
            return 0;
        }
        public void removePropertyChangeListener(PropertyChangeListener l) {

        }
        public void removePropertyChangeListener(String propertyName,
                PropertyChangeListener listener) {

        }
        public void setGroup(int group) {

        }
        public void setHorizontalScaleCount(int horizontalScaleCount) {
            this.setHorizontalScaleCount = horizontalScaleCount;
        }

        public void setHorizontalScaleWidth(long horizontalScaleWidth) {
            this.setHorizontalScaleWidth = horizontalScaleWidth;
        }
        public void setListHandlerIndex(int index) {
            this.setListHandlerIndex = index;
        }
        public void setReferenceTime(int series, int fold,
                Timestamp referenceTime) {

        }
        public void setReferenceTime(int series, Timestamp referenceTime) {

        }
        public void setReferenceTime(Timestamp time) {

        }
        public void setReferenceValue(int series, double referenceValue) {

        }
        public void setReferenceValue(int series, int fold,
                double referenceValue) {

        }
        public void setVerticalMaximum(int series, double verticalMaximum) {

        }
        public void setVerticalMinimum(int series, double verticalMinimum) {

        }
        public String getFirstFormat() {
            return firstFormat;
        }
        public String getSecondFormat() {
            return secondFormat;
        }
        public void setFirstFormat(String format) {
            firstFormat = format;
        }
        public void setSecondFormat(String format) {
            secondFormat = format;
        }
        public VerticallyScaleProperty getVerticallyScaleProperty() {
        	return new VerticallyScaleProperty(Color.GRAY, Color.BLACK, Color.WHITE);
        }
    }
}
