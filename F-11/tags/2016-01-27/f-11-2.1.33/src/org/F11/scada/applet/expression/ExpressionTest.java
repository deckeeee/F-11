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
package org.F11.scada.applet.expression;

import java.util.Date;

import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.WifeException;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.test.util.TestUtil;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ExpressionTest extends TestCase {
	private Expression Fixture = new Expression();
	DataProvider dp;
	DataHolder dh;

	/**
	 * Constructor for ExpressionTest.
	 * @param arg0
	 */
	public ExpressionTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		WifeDataAnalog wa = WifeDataAnalog.valueOfBcdSingle(300.0);
		ConvertValue con = ConvertValue.valueOfANALOG(-100, 300, 0, 400, "###0.0");

		assertEquals(300.0, wa.doubleValue(), 0.0);
		assertEquals(200.0, con.convertDoubleValue(wa.doubleValue()), 0.0);
		
		dp = new DummyProvider();
		dp.setDataProviderName("P1");

		dh = new DataHolder();
		dh.setDataHolderName("DH11");
		dh.setValueClass(WifeData.class);
		dh.setValue(wa, new Date(), WifeQualityFlag.GOOD);
		dh.setParameter("convert", con);
		dp.addDataHolder(dh);

		dh = TestUtil.createDigitalHolder("DIGITAL_00");
		dh.setValue(WifeDataDigital.valueOfTrue(0), new Date(), WifeQualityFlag.GOOD);
		dp.addDataHolder(dh);

		Manager.getInstance().addDataProvider(dp);
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		Manager.getInstance().removeDataProvider(dp);
		dp.removeDataHolder(dh);
	}

    public void test001() throws WifeException {
		Fixture.toPostfix("PI");
		assertEquals("3.141592653589793", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("1+2");
		assertEquals(3.0, Fixture.doubleValue(), 0);
		Fixture.toPostfix("3-4");
		assertEquals(-1.0, Fixture.doubleValue(), 0);
		Fixture.toPostfix("5*6");
		assertEquals(30.0, Fixture.doubleValue(), 0);
		Fixture.toPostfix("7/8");
		assertEquals(0.875, Fixture.doubleValue(), 0);
		Fixture.toPostfix("9^5");
		assertEquals(59049.0, Fixture.doubleValue(), 0);

		Fixture.toPostfix("abs-21");
		assertEquals("21.0", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("ABS 20");
		assertEquals("20.0", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("sin 0");
		assertEquals("0.0", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("sin 0.5235987755982989");
		assertEquals("0.5", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("SIN 0.9272952180016123");
		assertEquals("0.8", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("SIN 1.5707963267948966");
		assertEquals("1.0", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("cos 0");
		assertEquals("1.0", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("cos 1.0471975511965979");
		assertEquals(0.5D, Fixture.doubleValue(), 0.5D);
		Fixture.toPostfix("COS 0.6435011087932843");
		assertEquals("0.8", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("COS 1.5707963267948966");
		assertEquals(0.0, Fixture.doubleValue(), 7E-17);
		Fixture.toPostfix("tan 0");
		assertEquals("0.0", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("tan 0.4636476090008061");
		assertEquals("0.5", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("TAN 0.7853981633974483");
		assertEquals(1.0, Fixture.doubleValue(), 0.0000000000000002);

		Fixture.toPostfix("asin 0");
		assertEquals("0.0", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("asin 0.5");
		assertEquals("0.5235987755982989", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("ASIN 0.8");
		assertEquals("0.9272952180016123", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("ASIN 1.0");
		assertEquals("1.5707963267948966", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("acos 1.0");
		assertEquals("0.0", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("acos 0.5");
		assertEquals("1.0471975511965979", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("ACOS 0.8");
		assertEquals("0.6435011087932843", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("ACOS 0.0");
		assertEquals("1.5707963267948966", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("atan 0.0");
		assertEquals("0.0", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("atan 0.5");
		assertEquals("0.4636476090008061", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("ATAN 1.0");
		assertEquals("0.7853981633974483", String.valueOf(Fixture.doubleValue()));

		Fixture.toPostfix("SQRT 1.0");
		assertEquals("1.0", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("SQRT 2.0");
		assertEquals("1.4142135623730951", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("SQRT 3.0");
		assertEquals("1.7320508075688772", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("SQRT 4.0");
		assertEquals("2.0", String.valueOf(Fixture.doubleValue()));
		Fixture.toPostfix("SQRT 5.0");
		assertEquals("2.23606797749979", String.valueOf(Fixture.doubleValue()));
    }

    public void test002() throws WifeException {
		Fixture.toPostfix("true");
		assertEquals(true, Fixture.booleanValue());
		Fixture.toPostfix("TRUE");
		assertEquals(true, Fixture.booleanValue());
		Fixture.toPostfix("false");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("FALSE");
		assertEquals(false, Fixture.booleanValue());

		Fixture.toPostfix("!true");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("!false");
		assertEquals(true, Fixture.booleanValue());

		Fixture.toPostfix("10<11");
		assertEquals(true, Fixture.booleanValue());
		Fixture.toPostfix("11<11");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("12<11");
		assertEquals(false, Fixture.booleanValue());

		Fixture.toPostfix("10<=11");
		assertEquals(true, Fixture.booleanValue());
		Fixture.toPostfix("11<=11");
		assertEquals(true, Fixture.booleanValue());
		Fixture.toPostfix("12<=11");
		assertEquals(false, Fixture.booleanValue());

		Fixture.toPostfix("10>11");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("11>11");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("12>11");
		assertEquals(true, Fixture.booleanValue());

		Fixture.toPostfix("10>=11");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("11>=11");
		assertEquals(true, Fixture.booleanValue());
		Fixture.toPostfix("12>=11");
		assertEquals(true, Fixture.booleanValue());

		Fixture.toPostfix("10==11");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("11==11");
		assertEquals(true, Fixture.booleanValue());
		Fixture.toPostfix("12==11");
		assertEquals(false, Fixture.booleanValue());

		Fixture.toPostfix("10!=11");
		assertEquals(true, Fixture.booleanValue());
		Fixture.toPostfix("11!=11");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("12!=11");
		assertEquals(true, Fixture.booleanValue());

		Fixture.toPostfix("false&&false");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("false&&true");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("true&&false");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("true&&true");
		assertEquals(true, Fixture.booleanValue());

		Fixture.toPostfix("false||false");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("false||true");
		assertEquals(true, Fixture.booleanValue());
		Fixture.toPostfix("true||false");
		assertEquals(true, Fixture.booleanValue());
		Fixture.toPostfix("true||true");
		assertEquals(true, Fixture.booleanValue());
    }

	public void test003() throws WifeException {
		Fixture.toPostfix("1/2-3^4*5+SIN(PI/6)+7.89E0");
		assertEquals("-396.11", String.valueOf(Fixture.doubleValue()));

		Fixture.toPostfix("true&&!false||!true&&false");
		assertEquals(true, Fixture.booleanValue());
		Fixture.toPostfix("false&&!true||!false&&true");
		assertEquals(true, Fixture.booleanValue());
		Fixture.toPostfix("true&&!true||!true&&true");
		assertEquals(false, Fixture.booleanValue());
		Fixture.toPostfix("false&&!false||!false&&false");
		assertEquals(false, Fixture.booleanValue());
	}

	public void test004() throws Exception {
		try {
			Fixture.toPostfix("1/2-3^test4*5+SIN(PI/6)+7.89E0");
			assertEquals("-396.11", String.valueOf(Fixture.doubleValue()));
			fail();
		} catch(Exception e) {
		}

		try {
			Fixture.toPostfix("true&&!false test||!true&&false");
			assertEquals(true, Fixture.booleanValue());
			fail();
		} catch(Exception e) {
		}
	}

    public void test005() throws WifeException {
		Fixture.toPostfix("P1_DH11");
		assertEquals(200.0, Fixture.doubleValue(), 0.0);
		Fixture.toPostfix("P1_DH11 == 200.0");
		assertTrue(Fixture.booleanValue());
		Fixture.toPostfix("P1_DH11 + P1_DH11");
		assertEquals(400.0, Fixture.doubleValue(), 0);
		Fixture.toPostfix("P1_DH11 > 100");
		assertTrue(Fixture.booleanValue());
		Fixture.toPostfix("P1_DH11 == P1_DH11");
		assertTrue(Fixture.booleanValue());
		Fixture.toPostfix("P1_DH11 < 100");
		assertFalse(Fixture.booleanValue());
    }

    public void testDigitalData() throws Exception {
    	Fixture.toPostfix("P1_DIGITAL_00");
    	assertTrue(Fixture.booleanValue());
    	Fixture.toPostfix("!P1_DIGITAL_00");
    	assertFalse(Fixture.booleanValue());
    	Fixture.toPostfix("P1_DIGITAL_00 && P1_DIGITAL_00");
    	assertTrue(Fixture.booleanValue());
    	Fixture.toPostfix("P1_DIGITAL_00 && !P1_DIGITAL_00");
    	assertFalse(Fixture.booleanValue());
    	Fixture.toPostfix("P1_DIGITAL_00 || !P1_DIGITAL_00");
    	assertTrue(Fixture.booleanValue());
    	Fixture.toPostfix("!P1_DIGITAL_00 || !P1_DIGITAL_00");
    	assertFalse(Fixture.booleanValue());

		Fixture.toPostfix("P1_DH11 == 200.0 && P1_DIGITAL_00");
		assertTrue(Fixture.booleanValue());
    }

	public class DummyProvider extends AbstractDataProvider {
		private static final long serialVersionUID = -9159259921897075318L;
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
