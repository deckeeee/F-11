/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/alarm/AlarmDefineTest.java,v 1.6.2.1 2005/01/17 05:57:21 frdm Exp $
 * $Revision: 1.6.2.1 $
 * $Date: 2005/01/17 05:57:21 $
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
 */

package org.F11.scada.parser.alarm;

import junit.framework.TestCase;

import org.F11.scada.applet.symbol.ColorFactory;
import org.apache.commons.beanutils.BeanUtils;

/**
 * AlarmDefineのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmDefineTest extends TestCase {

	/**
	 * Constructor for AlarmDefineTest.
	 * @param arg0
	 */
	public AlarmDefineTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
	}
	
	public void testAlarmDefine() throws Exception {
		AlarmDefine ad = new AlarmDefine("AlarmDefineTest.xml");

		assertEquals(
			"dialog",
			BeanUtils.getNestedProperty(
				ad,
				"alarmConfig.alarmNewsConfig.fontConfig.type"));
		assertEquals(
			"24",
			BeanUtils.getNestedProperty(
				ad,
				"alarmConfig.alarmNewsConfig.fontConfig.point"));
		assertEquals(
			"bold",
			BeanUtils.getNestedProperty(
				ad,
				"alarmConfig.alarmNewsConfig.fontConfig.style"));

		assertEquals(
			"Serif",
			BeanUtils.getNestedProperty(
				ad,
				"alarmConfig.alarmTableConfig.fontConfig.type"));
		assertEquals(
			"12",
			BeanUtils.getNestedProperty(
				ad,
				"alarmConfig.alarmTableConfig.fontConfig.point"));
		assertEquals(
			"plain",
			BeanUtils.getNestedProperty(
				ad,
				"alarmConfig.alarmTableConfig.fontConfig.style"));
				
		assertEquals(ColorFactory.getColor("navy"), ad.getAlarmConfig().getAlarmNewsConfig().getBackGroundColor());
		
		assertEquals(ColorFactory.getColor("navy"), ad.getAlarmConfig().getAlarmTableConfig().getBackGroundColor());
		assertEquals(ColorFactory.getColor("red"), ad.getAlarmConfig().getAlarmTableConfig().getHeaderBackGroundColor());
		assertEquals(ColorFactory.getColor("white"), ad.getAlarmConfig().getAlarmTableConfig().getHeaderForeGroundColor());
		
		assertNotNull(ad.getAlarmConfig());
		assertNull(ad.getAlarmConfig().getPage());
		assertNull(ad.getAlarmConfig().getToolBar());
		assertEquals("page01", ad.getAlarmConfig().getInitConfig().getInitPage());

		assertEquals("Happiness!", ad.getAlarmConfig().getTitleConfig().getText());
		assertEquals("/images/titleIcon.png", ad.getAlarmConfig().getTitleConfig().getImage());
	}
	
	
	public void testAlarmDefine2() throws Exception {
		AlarmDefine ad = new AlarmDefine("AlarmDefineTest2.xml");

		assertEquals(
			"dialog",
			BeanUtils.getNestedProperty(
				ad,
				"alarmConfig.alarmNewsConfig.fontConfig.type"));
		assertEquals(
			"24",
			BeanUtils.getNestedProperty(
				ad,
				"alarmConfig.alarmNewsConfig.fontConfig.point"));
		assertEquals(
			"bold",
			BeanUtils.getNestedProperty(
				ad,
				"alarmConfig.alarmNewsConfig.fontConfig.style"));

		assertEquals(
			"Serif",
			BeanUtils.getNestedProperty(
				ad,
				"alarmConfig.alarmTableConfig.fontConfig.type"));
		assertEquals(
			"12",
			BeanUtils.getNestedProperty(
				ad,
				"alarmConfig.alarmTableConfig.fontConfig.point"));
		assertEquals(
			"plain",
			BeanUtils.getNestedProperty(
				ad,
				"alarmConfig.alarmTableConfig.fontConfig.style"));
		
		assertEquals(ColorFactory.getColor("lightgrey"), ad.getAlarmConfig().getAlarmNewsConfig().getBackGroundColor());

		assertEquals(ColorFactory.getColor("white"), ad.getAlarmConfig().getAlarmTableConfig().getBackGroundColor());
		assertEquals(ColorFactory.getColor("lightgrey"), ad.getAlarmConfig().getAlarmTableConfig().getHeaderBackGroundColor());
		assertEquals(ColorFactory.getColor("black"), ad.getAlarmConfig().getAlarmTableConfig().getHeaderForeGroundColor());
		assertEquals("Webサーバーコネクションエラー", ad.getAlarmConfig().getServerErrorMessage().getMessage());

		assertEquals(2, ad.getAlarmConfig().getPage().getCacheMax());
		
		assertNotNull(ad.getAlarmConfig().getToolBar());
		assertFalse(ad.getAlarmConfig().getToolBar().isDisplayLogin());
	}
	
}
