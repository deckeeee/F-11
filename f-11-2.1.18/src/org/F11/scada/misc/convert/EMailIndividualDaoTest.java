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

public class EMailIndividualDaoTest extends S2DaoTestCase {
	private EMailIndividualDao dao;

	public EMailIndividualDaoTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("EMailDao.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'org.F11.scada.misc.convert.EMailIndividualDao.select()'
	 */
	public void testSelect() {
		List l = dao.select();
		System.out.println(l);
		assertEquals(1, l.size());
		EMailIndividualDto dto = (EMailIndividualDto) l.get(0);
		assertEquals(0, dto.getEmailIndividualSettingId());
		assertEquals("P1", dto.getProvider());
		assertEquals("D_1900000_Digital", dto.getHolder());
		assertEquals(0, dto.getEmailGroupId());
		assertEquals("1, hogehoge@example.com", dto.getEmailAddress());
	}
}
