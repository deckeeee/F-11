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

import org.F11.scada.test.util.TimestampUtil;
import org.seasar.dao.unit.S2DaoTestCase;

public class SentMailServiceImplTest extends S2DaoTestCase {
	SentMailDao dao;
	SentMailAddressDao addressDao;
	
	SentMailService service;

	public SentMailServiceImplTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("SentMailServiceImplTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'org.F11.scada.tool.sentmail.SentMailServiceImpl.findAllBySentMailCondition(SentMailCondition)'
	 */
	public void testFindAllBySentMailConditionTx() throws Exception {
		SentMailDto dto = new SentMailDto();
		dto.setProvider("P1");
		dto.setHolder("D_1900000_Digital");
		dto.setSentdate(TimestampUtil.parse("2006/01/01 10:00:00"));
		dto.setValue(true);
		assertEquals(1, dao.insert(dto));
		
		SentMailCondition condition = new SentMailCondition();
		condition.setLimit(50);
		insertAddress(condition);
		List datas = service.findAllBySentMailCondition(condition);
		assertNotNull(datas);
		SentMailDto sentMailDto = (SentMailDto) datas.get(0);
		assertTrue(sentMailDto.getAlarmEmailSentId() > 0);
		assertEquals("P1", sentMailDto.getProvider());
		assertEquals("D_1900000_Digital", sentMailDto.getHolder());
		assertEquals(TimestampUtil.parse("2006/01/01 10:00:00"), sentMailDto.getSentdate());
		assertTrue(sentMailDto.isValue());
		List addresses = sentMailDto.getAddresses();
		assertNotNull(addresses);
		assertEquals(2, addresses.size());
	}

	private void insertAddress(SentMailCondition condition) {
		List datas = dao.findAllBySentMailCondition(condition);
		SentMailDto sentMailDto = (SentMailDto) datas.get(0);
		SentMailAddressDto dto = new SentMailAddressDto();
		dto.setAlarmEmailSentId(sentMailDto.getAlarmEmailSentId());
		dto.setAddress("hoge@example.com");
		assertEquals(1, addressDao.insert(dto));
		dto = new SentMailAddressDto();
		dto.setAlarmEmailSentId(sentMailDto.getAlarmEmailSentId());
		dto.setAddress("foo@example.com");
		assertEquals(1, addressDao.insert(dto));
	}
}
