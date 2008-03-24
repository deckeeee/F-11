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

package org.F11.scada.tool.sound.individual;

import java.util.List;

import org.seasar.dao.unit.S2DaoTestCase;

public class IndividualDaoTest extends S2DaoTestCase {
	private IndividualDao dao;

	public IndividualDaoTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("IndividualDaoTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetItemTx() throws Exception {
		IndividualDto dto = dao.getItem("P1", "D_1900000_Digital");
		assertNotNull(dto);
		assertEquals(0, dto.getPoint());
		assertEquals("AHU-1-1", dto.getUnit());
		assertEquals("1F AŽº ‹ó’²‹@", dto.getName());
		assertEquals("Œx•ñ", dto.getAttribute());
		assertEquals(0, dto.getType());
		assertEquals(0, dto.getAttributetype());
		assertEquals("P1", dto.getProvider());
		assertEquals("D_1900000_Digital", dto.getHolder());
	}
	
	public void testUpdateIndividualTx() throws Exception {
		IndividualDto dto = dao.getItem("P1", "D_1900000_Digital");
		dto.setAlarmIndividualSettingId(0);
		dto.setType(1);
		assertEquals(1, dao.insertIndividual(dto));

		dto = dao.getItem("P1", "D_1900000_Digital");
		assertNotNull(dto);
		assertEquals(0, dto.getPoint());
		assertEquals("AHU-1-1", dto.getUnit());
		assertEquals("1F AŽº ‹ó’²‹@", dto.getName());
		assertEquals("Œx•ñ", dto.getAttribute());
		assertEquals(1, dto.getType());
		assertEquals(0, dto.getAttributetype());
		assertEquals("P1", dto.getProvider());
		assertEquals("D_1900000_Digital", dto.getHolder());
		
		dto.setType(0);
		assertEquals(1, dao.updateIndividual(dto));
		
		dto = dao.getItem("P1", "D_1900000_Digital");
		assertNotNull(dto);
		assertEquals(0, dto.getPoint());
		assertEquals("AHU-1-1", dto.getUnit());
		assertEquals("1F AŽº ‹ó’²‹@", dto.getName());
		assertEquals("Œx•ñ", dto.getAttribute());
		assertEquals(0, dto.getType());
		assertEquals(0, dto.getAttributetype());
		assertEquals("P1", dto.getProvider());
		assertEquals("D_1900000_Digital", dto.getHolder());
	}
	
	public void testFindAllItemTx() throws Exception {
		IndividualCondition dto = new IndividualCondition();
		dto.setLimit(2);
		
		List data = dao.findAllItem(dto);
		assertNotNull(data);
		assertEquals(2, data.size());
		System.out.println(data);
		
		dto.setOffset(2);
		data = dao.findAllItem(dto);
		assertNotNull(data);
		assertEquals(2, data.size());
		System.out.println(data);
	}
}
