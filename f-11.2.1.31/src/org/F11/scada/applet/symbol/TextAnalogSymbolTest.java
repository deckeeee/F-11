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

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import jp.gr.java_conf.skrb.gui.lattice.LatticeConstraints;
import jp.gr.java_conf.skrb.gui.lattice.LatticeLayout;
import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.Service;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeQualityFlag;

/**
 * @author user
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TextAnalogSymbolTest extends TestCase {
	DataProvider dp;
	DataHolder dh1;
	DataHolder dh2;

	/**
	 * Constructor for TextAnalogSymbolTest.
	 * @param arg0
	 */
	public TextAnalogSymbolTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		WifeDataAnalog wa = WifeDataAnalog.valueOfBcdSingle(300.0);
		ConvertValue con = ConvertValue.valueOfANALOG(-100, 300, 0, 400, "0.0");

		assertEquals(300.0, wa.doubleValue(), 0.0);
		assertEquals(200.0, con.convertDoubleValue(wa.doubleValue()), 0.0);
		
		dp = new DummyProvider();
		dh1 = new DataHolder();

		dh1.setDataHolderName("DH11");
		dh1.setValueClass(WifeData.class);
		dh1.setValue(wa, new Date(), WifeQualityFlag.GOOD);
		dh1.setParameter("convert", con);
		
		WifeDataAnalog wa2 = WifeDataAnalog.valueOfBcdSingle(51.0);
		ConvertValue con2 = ConvertValue.valueOfLELA(-0.5, -1, 0, 100, " 0.00");

		dh2 = new DataHolder();

		dh2.setDataHolderName("DH12");
		dh2.setValueClass(WifeData.class);
		dh2.setValue(wa2, new Date(), WifeQualityFlag.GOOD);
		dh2.setParameter("convert", con2);

		dp.setDataProviderName("P1");
		dp.addDataHolder(dh1);
		dp.addDataHolder(dh2);
		Manager.getInstance().addDataProvider(dp);
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		Manager.getInstance().removeDataProvider(dp);
		dp.removeDataHolder(dh2);
		dp.removeDataHolder(dh1);
	}

	public void testUpdateProperty() {
		SymbolProperty prop = new SymbolProperty();
		prop.setProperty("visible","true");
		prop.setProperty("blink","false");
		prop.setProperty("x","30");
		prop.setProperty("y","20");
		prop.setProperty("h_aligin", "LEFT");
		prop.setProperty("opaque", "true");
		prop.setProperty("foreground", "green");
		prop.setProperty("background", "red");
		prop.setProperty("font", "SansSerif");
		prop.setProperty("font_style", "PLAIN");
		prop.setProperty("font_size", "20");
		prop.setProperty("width", "100");
		prop.setProperty("height", "30");
		prop.setProperty("value", "P1_DH11");
		TextAnalogSymbol Fixture = new TextAnalogSymbol(prop);
		Fixture.setBorder(BorderFactory.createLoweredBevelBorder());
		prop = new SymbolProperty();
		prop.setProperty("visible","true");
		prop.setProperty("blink","false");
		prop.setProperty("x","30");
		prop.setProperty("y","60");
		prop.setProperty("h_aligin", "RIGHT");
		prop.setProperty("opaque", "true");
		prop.setProperty("foreground", "blue");
		prop.setProperty("background", "yellow");
		prop.setProperty("font", "SansSerif");
		prop.setProperty("font_style", "PLAIN");
		prop.setProperty("font_size", "20");
		prop.setProperty("width", "100");
		prop.setProperty("height", "30");
		prop.setProperty("value", "P1_DH12");
		TextAnalogSymbol Fixture2 = new TextAnalogSymbol(prop);
		Fixture2.setBorder(BorderFactory.createLoweredBevelBorder());
		
		SymbolsDlg_Test dlg = new SymbolsDlg_Test("TextAnalogSymbolTest", "•\Ž¦");
		dlg.addSymbol(Fixture);
		dlg.addSymbol(Fixture2);
		dlg.show();
		if (!dlg.getStat())
			fail();
		
		assertEquals(1, Fixture.dataReferencers.size());
		assertEquals(1, Fixture2.dataReferencers.size());
		Fixture.disConnect();
		assertEquals(0, Fixture.dataReferencers.size());
		Fixture2.disConnect();
		assertEquals(0, Fixture2.dataReferencers.size());
	}
	

	public void testLayout() {
		SymbolProperty prop = new SymbolProperty();
		prop.setProperty("visible","true");
		prop.setProperty("blink","false");
//		prop.setProperty("x","30");
//		prop.setProperty("y","20");
		prop.setProperty("h_aligin", "LEFT");
		prop.setProperty("opaque", "true");
		prop.setProperty("foreground", "green");
		prop.setProperty("background", "red");
		prop.setProperty("font", "SansSerif");
		prop.setProperty("font_style", "PLAIN");
		prop.setProperty("font_size", "20");
//		prop.setProperty("width", "100");
//		prop.setProperty("height", "30");
		prop.setProperty("value", "P1_DH11");
		TextAnalogSymbol Fixture = new TextAnalogSymbol(prop);
		Fixture.setBorder(BorderFactory.createLoweredBevelBorder());
		prop = new SymbolProperty();
		prop.setProperty("visible","true");
		prop.setProperty("blink","false");
//		prop.setProperty("x","30");
//		prop.setProperty("y","60");
		prop.setProperty("h_aligin", "RIGHT");
		prop.setProperty("opaque", "true");
		prop.setProperty("foreground", "blue");
		prop.setProperty("background", "yellow");
		prop.setProperty("font", "SansSerif");
		prop.setProperty("font_style", "PLAIN");
		prop.setProperty("font_size", "20");
//		prop.setProperty("width", "100");
//		prop.setProperty("height", "30");
		prop.setProperty("value", "P1_DH12");
		TextAnalogSymbol Fixture2 = new TextAnalogSymbol(prop);
		Fixture2.setBorder(BorderFactory.createLoweredBevelBorder());
		
		SymbolsDlg_Test dlg = new SymbolsDlg_Test("TextAnalogSymbolTest", "•\Ž¦");

		JPanel panel = new JPanel(new LatticeLayout(8, 2));
		LatticeConstraints c = new LatticeConstraints();
		c.x = 0; c.y = 0; c.width = 3;
		panel.add(Fixture, c);

		c.x = 5; c.y = 1; c.width = 3;
		panel.add(Fixture2, c);

		dlg.addComponent(panel);
		dlg.show();
		if (!dlg.getStat())
			fail();
	}
	
	public class DummyProvider extends AbstractDataProvider implements Runnable, Service {
		private static final long serialVersionUID = -228642320732202866L;
		private final Class[][] TYPE_INFO =
			{ { DataHolder.class, WifeData.class }
		};
		private Thread thread;

		DummyProvider() {
			start();
		}
		/**
		 * @see jp.gr.javacons.jim.DataProvider#getProvidableDataHolderTypeInfo()
		 */
		public Class[][] getProvidableDataHolderTypeInfo() {
			return TYPE_INFO;
		}
		
		public void run() {
			Thread ct = Thread.currentThread();
			while (ct == this.thread) {
				DataHolder[] dhs = getDataHolders();
				try {
					asyncRead(dhs);
				} catch (DataProviderDoesNotSupportException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}

		public void start() {
			this.thread = new Thread(this);
			this.thread.start();
		}

		public void stop() {
			this.thread = null;
		}

		public void asyncRead(DataHolder dh)
			throws DataProviderDoesNotSupportException {

			double value = Math.random() * 100D;
			WifeDataAnalog wa = WifeDataAnalog.valueOfBcdSingle(value);
			dh.setValue(wa, new Date(), WifeQualityFlag.GOOD);
		}

		public void asyncRead(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
			for (int i = 0; i < dhs.length; i++) {
				asyncRead(dhs[i]);
			}
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
