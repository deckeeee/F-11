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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

public class EMailIndividualConvertImpl implements EMailIndividualConvert {
	private EMailIndividualDao dao;
	private String file;

	public void setDao(EMailIndividualDao dao) {
		this.dao = dao;
	}
	
	public void setFile(String file) {
		this.file = file;
	}

	public void convert() {
		List list = dao.select();
		for (Iterator i = list.iterator(); i.hasNext();) {
			EMailIndividualDto dto = (EMailIndividualDto) i.next();
			write(getconvertedDto(dto));
		}
	}

	private void write(List convertedDto) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
			for (Iterator i = convertedDto.iterator(); i.hasNext();) {
				EMailIndividualDto dto = (EMailIndividualDto) i.next();
				out.println(getInsertString(dto));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private String getInsertString(EMailIndividualDto dto) {
		StringBuffer b = new StringBuffer();
		b.append(
				"INSERT INTO email_individual_setting_table (" +
				"provider, holder, email_group_id, email_address) VALUES(");
		b.append("'").append(dto.getProvider()).append("'").append(", ")
		.append("'").append(dto.getHolder()).append("'").append(", ")
		.append(dto.getEmailGroupId()).append(", '")
		.append(dto.getEmailAddress()).append("'");
		b.append(");");
		return b.toString();
	}

	private List getconvertedDto(EMailIndividualDto dto) {
		ArrayList groupIdDto = new ArrayList();
		ArrayList mailaddresDto = new ArrayList();
		getConvertedDto(dto, groupIdDto, mailaddresDto);
		groupIdDto.addAll(mailaddresDto);
		return groupIdDto;
	}

	private void getConvertedDto(EMailIndividualDto dto, List groupIdDto, List mailaddresDto) {
		StringTokenizer st = new StringTokenizer(dto.getEmailAddress(), ",");
		StringBuffer address = new StringBuffer();
		ArrayList groupIds = new ArrayList();
		while (st.hasMoreTokens()) {
			String mailaddress = st.nextToken().trim();
			try {
				Integer master_id = new Integer(mailaddress);
				groupIds.add(master_id);
			} catch (NumberFormatException e) {
				if (0 < address.length())
					address.append(", ");
				address.append(mailaddress);
			}
		}
		getGroupIdDto(groupIdDto, groupIds, dto);
		getAddressDto(mailaddresDto, address.toString(), dto);
	}

	private void getAddressDto(List mailaddresDto, String address, EMailIndividualDto olddto) {
		EMailIndividualDto dto = new EMailIndividualDto();
		dto.setProvider(olddto.getProvider());
		dto.setHolder(olddto.getHolder());
		dto.setEmailAddress(address);
		mailaddresDto.add(dto);
	}

	private void getGroupIdDto(List groupIdDto, List groupIds, EMailIndividualDto olddto) {
		for (Iterator i = groupIds.iterator(); i.hasNext();) {
			Integer groupId = (Integer) i.next();
			EMailIndividualDto dto = new EMailIndividualDto();
			dto.setProvider(olddto.getProvider());
			dto.setHolder(olddto.getHolder());
			dto.setEmailGroupId(groupId.intValue());
			dto.setEmailAddress("");
			groupIdDto.add(dto);
		}
	}
	
	public static void main(String[] args) {
		S2Container container = S2ContainerFactory
				.create("org/F11/scada/misc/convert/EMailIndividualConvert.dicon");
		EMailIndividualConvert convert = (EMailIndividualConvert) container
				.getComponent(EMailIndividualConvert.class);
		convert.convert();
	}
}
