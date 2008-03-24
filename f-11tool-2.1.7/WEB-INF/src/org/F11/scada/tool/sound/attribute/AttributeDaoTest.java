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

package org.F11.scada.tool.sound.attribute;

import java.util.List;

import org.seasar.dao.unit.S2DaoTestCase;

public class AttributeDaoTest extends S2DaoTestCase {
	private AttributeDao dao;

	public AttributeDaoTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("AttributeDaoTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetAllAttribute() throws Exception {
		List datas = dao.getAllAttribute();
		assertNotNull(datas);
	}

	public void testGetAttribute() throws Exception {
		AttributeDto dto = new AttributeDto();
		dto.setAttribute(new Integer(0));
		dto = dao.getAttribute(dto);
		assertNotNull(dto);
	}

	public void testUpdateAttributeTx() throws Exception {
		AttributeDto dto = new AttributeDto();
		dto.setAttribute(new Integer(0));
		dto = dao.getAttribute(dto);

		dto.setSoundType(new Integer(1));
		dao.updateAttribute(dto);

		dto = new AttributeDto();
		dto.setAttribute(new Integer(0));
		dto = dao.getAttribute(dto);
		assertEquals(new Integer(0), dto.getAttribute());
		assertEquals("Œx•ñ", dto.getName());
		assertEquals(new Integer(1), dto.getSoundType());
	}
}
