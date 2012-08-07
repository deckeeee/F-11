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

package org.F11.scada.server.register;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * PageXmlUtilのテストケース
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageXmlUtilTest extends TestCase {
	private final Logger logger = Logger.getLogger(PageXmlUtilTest.class);

	public PageXmlUtilTest(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(PageXmlUtilTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetHolderStrings() {
		String xml = "<f11:page_map xmlns:f11=\"http://www.F-11.org/scada\">"
				+ "<f11:page><f11:textanalogsymbol value=\"P1_D_500_BcdSingle+P1_D_501_BcdSingle\" h_aligin=\"RIGHT\" visible=\"true\" foreground=\"black\" font=\"SansSerif\" font_style=\"plain\" font_size=\"16\" border=\"BebelDown\" format=\"##0.0\" x=\"418\" y=\"210\">"
				+ "<f11:if value=\"P1_D_1903000_Digital\">"
				+ "<f11:property foreground=\"red\" blink=\"true\" visible=\"false\" />"
				+ "<f11:if value=\"P1_D_1903001_Digital\">"
				+ "<f11:property foreground=\"cyan\" blink=\"true\"/>"
				+ "<f11:property foreground=\"black\"/>" + "</f11:if>"
				+ "</f11:if>"
				+ "</f11:textanalogsymbol></f11:page></f11:page_map>";
		Set p = PageXmlUtil.getHolderStrings(xml);
		logger.info(p);
		assertTrue(p.contains(new HolderString("P1_D_500_BcdSingle")));
		assertTrue(p.contains(new HolderString("P1_D_501_BcdSingle")));
		assertTrue(p.contains(new HolderString("P1_D_1903000_Digital")));
		assertTrue(p.contains(new HolderString("P1_D_1903001_Digital")));
		assertFalse(p.contains(new HolderString("P1_D_1903002_Digital")));
	}

	public void testGetHolderStringsReader() throws Exception {
		Set s = PageXmlUtil
				.getHolderStrings(new BufferedReader(
						new InputStreamReader(
								PageXmlUtilTest.class
										.getResourceAsStream("/org/F11/scada/server/register/test.xml"))));
		logger.info(s);
		assertTrue(s.contains(new HolderString("P1_D_1900000_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900001_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900002_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900003_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900004_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900005_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900006_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900007_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900008_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900009_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900010_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900011_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900012_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900013_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900014_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900015_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900016_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900017_Digital")));
		assertFalse(s.contains(new HolderString("P1_D_1900018_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900019_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900020_Digital")));
		assertTrue(s.contains(new HolderString("P1_D_1900021_Digital")));
	}

	public void testGetHolderStringsReader2() throws Exception {
		Set s = PageXmlUtil
				.getHolderStrings(new BufferedReader(
						new InputStreamReader(
								PageXmlUtilTest.class
										.getResourceAsStream("/org/F11/scada/server/register/test2.xml"))));
		assertTrue(s.contains(new HolderString("P1_D_900_Schedule")));
		assertTrue(s.contains(new HolderString("P1_D_1020_Schedule")));
		assertTrue(s.contains(new HolderString("P1_D_1140_Schedule")));
		assertTrue(s.contains(new HolderString("P1_D_1260_Schedule")));
		assertTrue(s.contains(new HolderString("P1_D_1380_Schedule")));
		assertTrue(s.contains(new HolderString("P1_D_1900000_Digital")));
		logger.info(s);
	}

	public void testIsCache() throws Exception {
		assertFalse(PageXmlUtil
				.isCache(new BufferedReader(
						new InputStreamReader(
								PageXmlUtilTest.class
										.getResourceAsStream("/org/F11/scada/server/register/test.xml")))));
		assertTrue(PageXmlUtil
				.isCache(new BufferedReader(
						new InputStreamReader(
								PageXmlUtilTest.class
										.getResourceAsStream("/org/F11/scada/server/register/test2.xml")))));
		assertTrue(PageXmlUtil
				.isCache(new BufferedReader(
						new InputStreamReader(
								PageXmlUtilTest.class
										.getResourceAsStream("/org/F11/scada/server/register/test3.xml")))));
	}
}
