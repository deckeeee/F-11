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

import org.F11.scada.EnvironmentManager;
import org.F11.scada.tool.sound.individual.IndividualDao;
import org.F11.scada.tool.sound.individual.IndividualDto;
import org.F11.scada.util.RmiUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AttributeSetServiceImpl implements AttributeSetService {
	private final Log log = LogFactory.getLog(AttributeSetServiceImpl.class);
	private AttributeDao attributeDao;
	private IndividualDao individualDao;
	private boolean isAttributeMode;

	public AttributeSetServiceImpl(int regPort, int objPort) {
		RmiUtil.registryServer(
			this,
			AttributeSetService.class,
			regPort,
			objPort);
		isAttributeMode =
			Boolean.valueOf(EnvironmentManager.get(
				"/server/alarm/sound/attributemode",
				"true"));
		log.info("属性モード = " + (isAttributeMode ? "属性優先" : "ポイント優先"));
	}

	public void setAttributeDao(AttributeDao attributeDao) {
		this.attributeDao = attributeDao;
	}

	public void setIndividualDao(IndividualDao individualDao) {
		this.individualDao = individualDao;
	}

	public int setAttribute(AttributeDto dto) {
		if (isAttributeMode) {
			return attributeDao.updateAttribute(dto);
		} else {
			return setIndividual(dto);
		}
	}

	private int setIndividual(AttributeDto dto) {
		int ret = attributeDao.updateAttribute(dto);
		List<IndividualDto> list = individualDao.getItems(dto.getAttribute());
		for (IndividualDto individualDto : list) {
			IndividualDto data =
				individualDao.selectIndividual(
					individualDto.getProvider(),
					individualDto.getHolder());
			individualDto.setType(dto.getSoundType());
			if (data == null) {
				individualDao.insertIndividual(individualDto);
			} else {
				individualDao.updateIndividual(individualDto);
			}
		}
		return ret;
	}
}
