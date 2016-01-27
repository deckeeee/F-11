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

import java.util.Date;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.applet.symbol.LineGraphSymbol.LinePath;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.test.util.TestUtil;

/**
 * @author hori
 */
public class LineGraphSymbolTest extends TestCase {
	DataProvider dp;

	/**
	 * Constructor for LineGraphSymbolTest.
	 * @param arg0
	 */
	public LineGraphSymbolTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		dp = TestUtil.createDataProvider();
		Manager.getInstance().addDataProvider(dp);
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
		prop.setProperty("x", "10");
		prop.setProperty("y", "20");
		prop.setProperty("width", "200");
		prop.setProperty("height", "100");
		prop.setProperty("x_min_scale", "0");
		prop.setProperty("x_max_scale", "200");
		prop.setProperty("y_min_scale", "200");
		prop.setProperty("y_max_scale", "0");
		LineGraphSymbol fix = new LineGraphSymbol(prop);

		SymbolProperty lprop = new SymbolProperty();
		lprop.setProperty("line_width", "5");
		lprop.setProperty("foreground", "red");
		LinePath linePath = new LinePath(prop, lprop);
		linePath.addLine("0", "0");
		linePath.addLine("P1_D_500_BcdSingle","P1_D_501_BcdSingle");
		linePath.addLine("200", "100");

		fix.addLinePath(linePath);

		lprop = new SymbolProperty();
		lprop.setProperty("line_width", "3");
		lprop.setProperty("foreground", "green");
		linePath = new LinePath(prop, lprop);
		linePath.addLine("0", "0");
		linePath.addLine("P1_D_502_BcdSingle","P1_D_503_BcdSingle");
		linePath.addLine("200", "200");

		fix.addLinePath(linePath);

		DataHolder dh = dp.getDataHolder("D_500_BcdSingle");
		WifeDataAnalog wa = (WifeDataAnalog) dh.getValue();
		dh.setValue(wa.valueOf(1000), new Date(), WifeQualityFlag.GOOD);
		dh = dp.getDataHolder("D_501_BcdSingle");
		wa = (WifeDataAnalog) dh.getValue();
		dh.setValue(wa.valueOf(3000), new Date(), WifeQualityFlag.GOOD);
		dh = dp.getDataHolder("D_502_BcdSingle");
		wa = (WifeDataAnalog) dh.getValue();
		dh.setValue(wa.valueOf(3000), new Date(), WifeQualityFlag.GOOD);
		dh = dp.getDataHolder("D_503_BcdSingle");
		wa = (WifeDataAnalog) dh.getValue();
		dh.setValue(wa.valueOf(1000), new Date(), WifeQualityFlag.GOOD);
		
		SymbolsDlg_Test dlg = new SymbolsDlg_Test("LineGraphSymbolTest", "•\Ž¦");
		dlg.addSymbol(fix);
		dlg.show();
		if (!dlg.getStat())
			fail();
	}

}
