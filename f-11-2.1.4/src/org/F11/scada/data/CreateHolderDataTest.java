/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/CreateHolderDataTest.java,v 1.5.2.1 2004/11/29 07:12:47 frdm Exp $
 * $Revision: 1.5.2.1 $
 * $Date: 2004/11/29 07:12:47 $
 * 
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
package org.F11.scada.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class CreateHolderDataTest extends TestCase {
	CreateHolderData data;
	ConvertValue convertValue;
	Map demandData;
	WifeData wifeData;
	Date date;
	
	/**
	 * Constructor for CreateHolderDataTest.
	 * @param arg0
	 */
	public CreateHolderDataTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		convertValue = ConvertValue.valueOfANALOG(0, 100, 0, 4000, "##0");
		demandData = new HashMap();
		demandData.put("DEMAND", "H2");
		wifeData = WifeDataDigital.valueOfTrue(0);
		date = new Date();
		data =
			new CreateHolderData(
				"H1",
				wifeData,
				convertValue,
				demandData,
				date,
				WifeQualityFlag.GOOD,
				"P1");
	}
	
	public void testCreateHolderData() throws Exception {
		try {
			new CreateHolderData(null, wifeData, null, null, date, WifeQualityFlag.GOOD, "P1");
			fail();
		} catch (IllegalArgumentException ex) {}

		try {
			new CreateHolderData("H1", null, null, null, date, WifeQualityFlag.GOOD, "P1");
			fail();
		} catch (IllegalArgumentException ex) {}

		try {
			new CreateHolderData("H1", wifeData, null, null, date, WifeQualityFlag.GOOD, "P1");
		} catch (IllegalArgumentException ex) {
			fail();
		}

		try {
			new CreateHolderData("H1", wifeData, null, null, null, WifeQualityFlag.GOOD, "P1");
			fail();
		} catch (IllegalArgumentException ex) {
		}

		try {
			new CreateHolderData("H1", wifeData, null, null, date, null, "P1");
			fail();
		} catch (IllegalArgumentException ex) {
		}
	}

	public void testGetHolder() {
		assertEquals("H1", data.getHolder());
	}

	public void testGetConvertValue() {
		assertEquals(convertValue, data.getConvertValue());
	}

	public void testGetDemandData() {
		assertEquals(demandData, data.getDemandData());
		Map d = data.getDemandData();
		try {
			d.put("1", "1");
			fail();
		} catch (UnsupportedOperationException e) {
		}

		CreateHolderData ch = new CreateHolderData("H1", wifeData, null, null, date, WifeQualityFlag.GOOD, "P1");
		assertEquals(0, ch.getDemandData().size());
	}

	public void testGetWifeData() {
		assertEquals(wifeData, data.getWifeData());
	}

	/*
	 * String toString のテスト()
	 */
	public void testToString() {
//		System.out.println(data.toString());
//		assertEquals("provider=P1, holderName=H1, convertValue={cnvMin=0.0 cnvMax=100.0 inMin=0.0 inMax=4000.0 format=##0 type=ANALOG PFtype=DECIMAL}, demandData={DEMAND=H2}, wifeData=0, date=Thu Nov 25 11:39:18 JST 2004, qualityFlag=GOOD", data.toString());
	}

	public void testGetDate() {
		assertEquals(date, data.getDate());
	}

	public void testGetQualityFlag() {
		assertEquals(WifeQualityFlag.GOOD, data.getQualityFlag());
	}

/*
	public void testEquals() throws Exception {
		CreateHolderData d =
			new CreateHolderData(
				"H1",
				wifeData,
				convertValue,
				demandData);
		assertTrue(d.equals(data));
	}
	
	public void testHashCode() {
		CreateHolderData d =
			new CreateHolderData(
				"H1",
				wifeData,
				convertValue,
				demandData);
		assertEquals(d.hashCode(), data.hashCode());
	}
*/	
	public void testSerialize() throws Exception {
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(data);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		CreateHolderData d = (CreateHolderData) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertEquals("H1", d.getHolder());
		assertEquals(convertValue, d.getConvertValue());
		assertEquals(demandData, d.getDemandData());
		assertEquals(wifeData, d.getWifeData());

		temp.delete();
	}
	
}
