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

package org.F11.scada.misc.convert;

import java.util.List;

import org.seasar.dao.unit.S2DaoTestCase;

public class EMailAttributeDaoTest extends S2DaoTestCase {
	private EMailAttributeDao dao;

	public EMailAttributeDaoTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		include("EMailDao.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'org.F11.scada.misc.convert.EMailAttributeDao.select()'
	 */
	public void testSelect() throws Exception {
		List l = dao.select();
		System.out.println(l);
		assertEquals(1, l.size());
		EMailAttributeDto dto = (EMailAttributeDto) l.get(0);
		assertEquals(0, dto.getEmailAttributeSettingId());
		assertEquals(0, dto.getAttributeId());
		assertEquals(0, dto.getEmailGroupId());
		assertEquals("1, foo@example.com", dto.getEmailAddress());
	}
}
