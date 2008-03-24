/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.tool.point.name;

import java.util.Iterator;
import java.util.List;

import org.seasar.dao.unit.S2DaoTestCase;

public class PointNameDaoTest extends S2DaoTestCase {
	PointNameDao dao;

	public PointNameDaoTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("PointNameDaoTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'org.F11.scada.tool.point.name.PointNameDao.findAllByPointNameCondition(PointNameCondition)'
	 */
	public void testFindAllByPointNameCondition() {
		PointNameCondition condition = new PointNameCondition();
		condition.setLimit(PointNameCondition.NONE_LIMIT);
		List list = dao.findAllByPointNameCondition(condition);
		assertNotNull(list);
		System.out.println(list);
		assertTrue(list.size() > 0);
		int noUsed = 0;
		for (Iterator i = list.iterator(); i.hasNext();) {
			PointNameBean bean = (PointNameBean) i.next();
			if (!bean.isUsed()) {
				noUsed++;
			}
		}
		assertEquals("noused‚Í1Œ", 1, noUsed);
	}
}
