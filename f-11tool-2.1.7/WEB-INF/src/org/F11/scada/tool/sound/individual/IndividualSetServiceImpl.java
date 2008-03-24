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

import org.F11.scada.util.RmiUtil;

public class IndividualSetServiceImpl implements IndividualSetService {
	private IndividualDao individualDao;

	public IndividualSetServiceImpl(int regPort, int objPort) {
		RmiUtil.registryServer(
				this,
				IndividualSetService.class,
				regPort,
				objPort);
	}

	public void setIndividualDao(IndividualDao individualDao) {
		this.individualDao = individualDao;
	}

	public int setIndividual(IndividualDto dto) {
		IndividualDto data = individualDao.selectIndividual(
				dto.getProvider(),
				dto.getHolder());
		if (data == null) {
			return individualDao.insertIndividual(dto);
		} else {
			return individualDao.updateIndividual(dto);
		}
	}
}
