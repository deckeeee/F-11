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
package org.F11.scada.applet.symbol;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog4;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.test.util.TestUtil;

/**
 * @author hori
 */
public class TextAnalog4SymbolEditableTest extends TestCase {
	DataProvider dp;

	/**
	 * Constructor for TextAnalog4SymbolTest.
	 * @param arg0
	 */
	public TextAnalog4SymbolEditableTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		dp = TestUtil.createDataProvider();
		Manager.getInstance().addDataProvider(dp);

		WifeDataAnalog4 wa = WifeDataAnalog4.valueOfBcdSingle(new double[] {4,2,1,3});
		DataHolder dh = new DataHolder();
		dh.setValueClass(WifeData.class);
		dh.setValue(wa, new java.util.Date(), WifeQualityFlag.GOOD);
		dh.setDataHolderName("WDA4");
		ConvertValue conv = ConvertValue.valueOfANALOG(0, 100, 0, 100, "0.0");
		dh.setParameter("convert", conv);
		dp.addDataHolder(dh);

		dh = new DataHolder();
		dh.setValueClass(WifeData.class);
		dh.setValue(wa, new java.util.Date(), WifeQualityFlag.GOOD);
		dh.setDataHolderName("WDALELA4");
		conv = ConvertValue.valueOfLELA(0.5, 1.0, 0, 100, " 0.00");
		dh.setParameter("convert", conv);
		dp.addDataHolder(dh);
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		Manager.getInstance().removeDataProvider(dp);
	}

	public void testUpdateProperty() {
		SymbolProperty prop = new SymbolProperty();
		prop.setProperty("visible", "true");
		prop.setProperty("blink", "false");
		prop.setProperty("x", "30");
		prop.setProperty("y", "20");
		prop.setProperty("h_aligin", "LEFT");
		prop.setProperty("opaque", "true");
		prop.setProperty("foreground", "green");
		prop.setProperty("background", "red");
		prop.setProperty("font", "SansSerif");
		prop.setProperty("font_style", "PLAIN");
		prop.setProperty("font_size", "14");
		prop.setProperty("width", "100");
		prop.setProperty("height", "30");
		prop.setProperty("value", "P1_WDA4");
		prop.setProperty("index", "0");
		TextAnalog4SymbolEditable Fixture = new TextAnalog4SymbolEditable(prop);
		Fixture.setSecondDialogName("1");
		Fixture.addDestination(null);
		Fixture.addValueSetter(new VariableAnalog4Setter("P1", "WDA4"));
		Fixture.setEditable(new boolean[]{true});
		prop = new SymbolProperty();
		prop.setProperty("visible", "true");
		prop.setProperty("blink", "false");
		prop.setProperty("x", "30");
		prop.setProperty("y", "60");
		prop.setProperty("h_aligin", "RIGHT");
		prop.setProperty("opaque", "true");
		prop.setProperty("foreground", "blue");
		prop.setProperty("background", "yellow");
		prop.setProperty("font", "SansSerif");
		prop.setProperty("font_style", "PLAIN");
		prop.setProperty("font_size", "20");
		prop.setProperty("width", "100");
		prop.setProperty("height", "30");
		prop.setProperty("value", "P1_WDA4");
		prop.setProperty("index", "1");
		TextAnalog4SymbolEditable Fixture2 = new TextAnalog4SymbolEditable(prop);
		Fixture2.setSecondDialogName("1");
		Fixture2.addDestination(null);
		Fixture2.addValueSetter(new VariableAnalog4Setter("P1", "WDA4"));
		Fixture2.setEditable(new boolean[]{true});
		prop = new SymbolProperty();
		prop.setProperty("visible", "true");
		prop.setProperty("blink", "false");
		prop.setProperty("x", "140");
		prop.setProperty("y", "20");
		prop.setProperty("h_aligin", "LEFT");
		prop.setProperty("opaque", "true");
		prop.setProperty("foreground", "green");
		prop.setProperty("background", "red");
		prop.setProperty("font", "SansSerif");
		prop.setProperty("font_style", "PLAIN");
		prop.setProperty("font_size", "20");
		prop.setProperty("width", "100");
		prop.setProperty("height", "30");
		prop.setProperty("value", "P1_WDA4");
		prop.setProperty("index", "2");
		TextAnalog4SymbolEditable Fixture3 = new TextAnalog4SymbolEditable(prop);
		Fixture3.setSecondDialogName("1");
		Fixture3.addDestination(null);
		Fixture3.addValueSetter(new VariableAnalog4Setter("P1", "WDA4"));
		Fixture3.setEditable(new boolean[]{true});
		prop = new SymbolProperty();
		prop.setProperty("visible", "true");
		prop.setProperty("blink", "false");
		prop.setProperty("x", "140");
		prop.setProperty("y", "60");
		prop.setProperty("h_aligin", "RIGHT");
		prop.setProperty("opaque", "true");
		prop.setProperty("foreground", "blue");
		prop.setProperty("background", "yellow");
		prop.setProperty("font", "SansSerif");
		prop.setProperty("font_style", "PLAIN");
		prop.setProperty("font_size", "20");
		prop.setProperty("width", "100");
		prop.setProperty("height", "30");
		prop.setProperty("value", "P1_WDA4");
		prop.setProperty("index", "3");
		TextAnalog4SymbolEditable Fixture4 = new TextAnalog4SymbolEditable(prop);
		Fixture4.setSecondDialogName("1");
		Fixture4.addDestination(null);
		Fixture4.addValueSetter(new VariableAnalog4Setter("P1", "WDA4"));
		Fixture4.setEditable(new boolean[]{true});

		prop = new SymbolProperty();
		prop.setProperty("visible", "true");
		prop.setProperty("blink", "false");
		prop.setProperty("x", "140");
		prop.setProperty("y", "100");
		prop.setProperty("h_aligin", "RIGHT");
		prop.setProperty("opaque", "true");
		prop.setProperty("foreground", "blue");
		prop.setProperty("background", "yellow");
		prop.setProperty("font", "SansSerif");
		prop.setProperty("font_style", "PLAIN");
		prop.setProperty("font_size", "20");
		prop.setProperty("width", "100");
		prop.setProperty("height", "30");
		prop.setProperty("value", "P1_WDALELA4");
		prop.setProperty("index", "0");
		TextAnalog4SymbolEditable Fixture5 = new TextAnalog4SymbolEditable(prop);
		Fixture5.setSecondDialogName("2");
		Fixture5.addDestination(null);
		Fixture5.addValueSetter(new VariableAnalog4Setter("P1", "WDALELA4"));
		Fixture5.setEditable(new boolean[]{true});

		SymbolsDlg_Test dlg = new SymbolsDlg_Test("TextAnalog4SymbolEditableTest", "•\Ž¦");
		dlg.addSymbol(Fixture);
		dlg.addSymbol(Fixture2);
		dlg.addSymbol(Fixture3);
		dlg.addSymbol(Fixture4);
		dlg.addSymbol(Fixture5);
		dlg.show();
		if (!dlg.getStat())
			fail();
		
		Manager manager = Manager.getInstance();
		assertTrue(manager.hasReferenceMaintainer(Fixture.dataReferencer));
		assertTrue(manager.hasReferenceMaintainer(Fixture2.dataReferencer));
		assertTrue(manager.hasReferenceMaintainer(Fixture3.dataReferencer));
		assertTrue(manager.hasReferenceMaintainer(Fixture4.dataReferencer));
		assertTrue(manager.hasReferenceMaintainer(Fixture5.dataReferencer));
		Fixture.disConnect();
		assertFalse(manager.hasReferenceMaintainer(Fixture.dataReferencer));
		Fixture2.disConnect();
		assertFalse(manager.hasReferenceMaintainer(Fixture2.dataReferencer));
		Fixture3.disConnect();
		assertFalse(manager.hasReferenceMaintainer(Fixture3.dataReferencer));
		Fixture4.disConnect();
		assertFalse(manager.hasReferenceMaintainer(Fixture4.dataReferencer));
		Fixture5.disConnect();
		assertFalse(manager.hasReferenceMaintainer(Fixture5.dataReferencer));
	}

}
