/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.dao;

import java.util.List;

import org.F11.scada.server.dto.CareerDto;
import org.seasar.dao.unit.S2DaoTestCase;

public class CareerDaoTest extends S2DaoTestCase {
	private CareerDao dao;

	protected void setUp() throws Exception {
		include("CareerDao.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetCareer() {
		List<CareerDto> dto =
			dao.getCareer("2009-02-17 00:00:00", "2009-02-17 23:59:59");
		System.out.println(dto.size());
	}

	public void testGetPinpointCareer() {
		List<CareerDto> dto =
			dao
					.getPinpointCareer(
							"(i.provider = 'P1' AND i.holder = 'D_1900000_Digital') " +
							"OR (i.provider = 'P1' AND i.holder = 'D_1900001_Digital')",
							"10");
		System.out.println(dto.size());
		System.out.println(dto);
	}
}
