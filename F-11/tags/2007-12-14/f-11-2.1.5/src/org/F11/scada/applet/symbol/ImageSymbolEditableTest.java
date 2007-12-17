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
import java.util.HashMap;
import java.util.Map;

import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;

/**
 * @author user
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ImageSymbolEditableTest extends TestCase {
	DataProvider dp;
	DataHolder dh1;
	DataHolder dh2;

	/**
	 * Constructor for ImageSymbolEditableTest.
	 * @param arg0
	 */
	public ImageSymbolEditableTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		WifeDataDigital wd = WifeDataDigital.valueOfFalse(0);
		
		dp = new DummyProvider();
		dh1 = new DataHolder();

		dh1.setDataHolderName("DH00");
		dh1.setValueClass(WifeData.class);
		dh1.setValue(wd, new Date(), WifeQualityFlag.GOOD);

		WifeDataDigital wd2 = WifeDataDigital.valueOfFalse(1);

		dh2 = new DataHolder();

		dh2.setDataHolderName("DH01");
		dh2.setValueClass(WifeData.class);
		dh2.setValue(wd2, new Date(), WifeQualityFlag.GOOD);

		dp.setDataProviderName("P1");
		dp.addDataHolder(dh1);
		dp.addDataHolder(dh2);
		Manager.getInstance().addDataProvider(dp);
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		DataProvider dp = Manager.getInstance().getDataProvider("P1");
		Manager.getInstance().removeDataProvider(dp);
		super.tearDown();
	}

	public void testUpdateProperty() {
		SymbolsDlg_Test dlg = new SymbolsDlg_Test("ImageSymbolEditableTest", "クリックでダイアログ表示");

		SymbolProperty prop = new SymbolProperty();
		prop.setProperty("visible", "true");
		prop.setProperty("blink", "false");
		prop.setProperty("x", "10");
		prop.setProperty("y", "20");
		prop.setProperty("value", "/images/AHU_G.png");
		prop.setProperty("dlgname", "0");
		prop.setProperty("dlgtitle", "test-title");
		ImageSymbolEditable Fixture = new ImageSymbolEditable(prop);
		Map atts = new HashMap();
		atts.put("buttontext", null);
		Fixture.addDestination(atts);
		Fixture.addValueSetter(new FixedDigitalSetter("P1", "DH00", "true"));
		atts.clear();
		atts.put("buttontext", "2");
		Fixture.addDestination(atts);
		Fixture.addValueSetter(new FixedDigitalSetter("P1", "DH01", "true"));
		Fixture.setEditable(new boolean[]{true});
		dlg.addSymbol(Fixture);

		prop = new SymbolProperty();
		prop.setProperty("visible", "true");
		prop.setProperty("blink", "false");
		prop.setProperty("x", "10");
		prop.setProperty("y", "50");
		prop.setProperty("value", "/images/AHU_R.png");
		prop.setProperty("dlgname", "0");
		prop.setProperty("dlgtitle", "test-title");
		Fixture = new ImageSymbolEditable(prop);
		atts = new HashMap();
		atts.put("buttontext", null);
		Fixture.addDestination(atts);
		Fixture.addValueSetter(new FixedDigitalSetter("P1", "DH00", "true"));
		atts.clear();
		atts.put("buttontext", null);
		Fixture.addDestination(atts);
		Fixture.addValueSetter(new FixedDigitalSetter("P1", "DH01", "true"));
		Fixture.setEditable(new boolean[]{true});
		dlg.addSymbol(Fixture);
		
		dlg.show();
		if (!dlg.getStat())
			fail();
	}
	
	
	public class DummyProvider extends AbstractDataProvider {
		private static final long serialVersionUID = 1946003371783964696L;
		private final Class[][] TYPE_INFO =
			{ { DataHolder.class, WifeData.class }
		};
		/**
		 * @see jp.gr.javacons.jim.DataProvider#getProvidableDataHolderTypeInfo()
		 */
		public Class[][] getProvidableDataHolderTypeInfo() {
			return TYPE_INFO;
		}
		public void asyncRead(DataHolder dh) throws DataProviderDoesNotSupportException {
		}
		public void asyncRead(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
		}
		public void asyncWrite(DataHolder dh) throws DataProviderDoesNotSupportException {
		}
		public void asyncWrite(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
		}
		public void syncRead(DataHolder dh) throws DataProviderDoesNotSupportException {
		}
		public void syncRead(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
		}
		public void syncWrite(DataHolder dh) throws DataProviderDoesNotSupportException {
		}
		public void syncWrite(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
		}
		
	}

}
