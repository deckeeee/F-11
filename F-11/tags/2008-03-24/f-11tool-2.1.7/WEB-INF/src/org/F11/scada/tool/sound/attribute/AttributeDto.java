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

import java.io.Serializable;

public class AttributeDto implements Serializable {
	private static final long serialVersionUID = -7184245664998342898L;

	public static final String TABLE = "attribute_table";

	/** ëÆê´ID */
	private Integer attribute;
	/** ëÆê´ñº */
	private String name;
	/** ëÆê´åxïÒâπÉ^ÉCÉv */
	private Integer soundType;

	public Integer getAttribute() {
		return attribute;
	}

	public void setAttribute(Integer attribute) {
		this.attribute = attribute;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSoundType() {
		return soundType;
	}

	public void setSoundType(Integer soundType) {
		this.soundType = soundType;
	}

	public String toString() {
		return attribute + " " + name + " " + soundType;
	}
}
