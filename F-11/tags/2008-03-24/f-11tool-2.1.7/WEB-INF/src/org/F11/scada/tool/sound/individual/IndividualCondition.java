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

import org.seasar.dao.pager.DefaultPagerCondition;

/**
 * 警報音設定の検索条件DTOです。検索条件のゲッターはページャが利用するものと、前回検索条件の保存用(getSearch～)があります。 *
 * 
 * @author maekawa
 * 
 */
public class IndividualCondition extends DefaultPagerCondition {
	private static final long serialVersionUID = 2166789202590134030L;

	private String unit;
	private String name;
	private String attribute;
	private String attributetype;
	private String individualtype;

	public String getUnit() {
		return null == unit || "".equals(unit) ? null : "%" + unit + "%";
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getSearchUnit() {
		return unit;
	}

	public String getName() {
		return null == name || "".equals(name) ? null : "%" + name + "%";
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSearchName() {
		return name;
	}

	public String getAttribute() {
		return null == attribute || "".equals(attribute) ? null : "%"
			+ attribute
			+ "%";
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getSearchAttribute() {
		return attribute;
	}

	public String getAttributetype() {
		return null == attributetype || "".equals(attributetype)
			? null
			: attributetype.equalsIgnoreCase("all") ? null : attributetype
				.equalsIgnoreCase("true") ? "1" : "0";
	}

	public void setAttributetype(String attributetype) {
		this.attributetype = attributetype;
	}

	public String getIndividualtype() {
		return null == individualtype || "".equals(individualtype)
			? null
			: individualtype.equalsIgnoreCase("all") ? null : individualtype
				.equalsIgnoreCase("true") ? "1" : "0";
	}

	public void setIndividualtype(String individualtype) {
		this.individualtype = individualtype;
	}

	@Override
	public String toString() {
		return String
			.format(
				"unit=%s, name=%s, attribute=%s, attributetype=%s, individualtype=%s%n",
				unit,
				name,
				attribute,
				attributetype,
				individualtype);
	}
}
