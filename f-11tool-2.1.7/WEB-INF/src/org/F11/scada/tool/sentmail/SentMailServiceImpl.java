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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SentMailServiceImpl implements SentMailService {
	private SentMailDao dao;
	private SentMailAddressDao addressDao;

	public void setDao(SentMailDao dao) {
		this.dao = dao;
	}

	public void setAddressDao(SentMailAddressDao addressDao) {
		this.addressDao = addressDao;
	}

	public List findAllBySentMailCondition(SentMailCondition dto) {
		return addAddresses(dao.findAllBySentMailCondition(dto));
	}
	
	private List addAddresses(List dto) {
		ArrayList setedDto = new ArrayList(dto.size());
		for (Iterator i = dto.iterator(); i.hasNext();) {
			SentMailDto sentMailDto = (SentMailDto) i.next();
			sentMailDto.setAddresses(
					addressDao.findBySentId(sentMailDto.getAlarmEmailSentId()));
			setedDto.add(sentMailDto);
		}
		return setedDto;
	}
}
