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

/**
 * 
 */
package org.F11.scada.server.comment;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.exception.SQLRuntimeException;

public class PointCommentDaoTest extends S2TestCase {
	private PointCommentDao dao;

	protected void setUp() throws Exception {
		include("PointCommentDaoTest.dicon");
	}
	
	public void testInsertAndSelectTx() throws Exception {
		// Insert Recorde
		PointCommentDto dto = new PointCommentDto();
		dto.setProvider("P1");
		dto.setHolder("H1");
		dto.setComment("COMMENT");
		assertEquals(1, dao.insert(dto));
		
		// Select Recode
		dto.setId(1);
		PointCommentDto dto2 = dao.select(dto);
//		assertEquals(dto.getId(), dto2.getId());
		assertEquals(dto.getProvider(), dto2.getProvider());
		assertEquals(dto.getHolder(), dto2.getHolder());
		assertEquals(dto.getComment(), dto2.getComment());

		// Duplicate Error
		try {
			dao.insert(dto);
			fail("insert unique key");
		} catch (SQLRuntimeException e) {
			assertTrue(true);
		}
		
		// Not Found
		dto = new PointCommentDto();
		dto.setProvider("P1");
		dto.setHolder("H2");
		dto2 = dao.select(dto);
		assertNull(dto2);
	}

	public void testUpdateTx() throws Exception {
		PointCommentDto dto = new PointCommentDto();
		dto.setProvider("P1");
		dto.setHolder("H1");
		dto.setComment("COMMENT");
		
		assertEquals(1, dao.insert(dto));

		PointCommentDto findDto = new PointCommentDto();
		findDto.setProvider("P1");
		findDto.setHolder("H1");
		
		PointCommentDto dto2 = dao.select(findDto);
//		assertEquals(dto.getId(), dto2.getId());
		assertEquals(dto.getProvider(), dto2.getProvider());
		assertEquals(dto.getHolder(), dto2.getHolder());
		assertEquals(dto.getComment(), dto2.getComment());
		
		dto2.setComment("COMMENT2");
		assertEquals(1, dao.update(dto2));
		
		dto = dao.select(findDto);
//		assertEquals(dto.getId(), dto2.getId());
		assertEquals(dto.getProvider(), dto2.getProvider());
		assertEquals(dto.getHolder(), dto2.getHolder());
		assertEquals(dto.getComment(), dto2.getComment());
	}
}