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

package org.F11.scada.tool.sentmail;

import java.util.List;

import org.seasar.dao.unit.S2DaoTestCase;

public class SentMailAddressDaoTest extends S2DaoTestCase {
	SentMailAddressDao dao;

	public SentMailAddressDaoTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("SentMailAddressDaoTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFindBySentIdTx() throws Exception {
		SentMailAddressDto dto = new SentMailAddressDto();
		dto.setAlarmEmailSentId(1);
		dto.setAddress("hoge@example.com");
		assertEquals(1, dao.insert(dto));
		dto = new SentMailAddressDto();
		dto.setAlarmEmailSentId(1);
		dto.setAddress("foo@example.com");
		assertEquals(1, dao.insert(dto));
		
		List dtos = dao.findBySentId(1);
		assertNotNull(dtos);
		assertEquals(2, dtos.size());
	}
}
