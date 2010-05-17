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

package org.F11.scada.server.entity;

/**
 * item_tableÇÃdata_typeëÆê´ENUMÉNÉâÉX
 * 
 * @author maekawa
 *
 */
public enum DataType {
	DIGITAL, BCDHALF1, BCDHALF2, BCD1W, BCD2W
	, HEXHALF1, HEXHALF2, HEX1W, HEX2W, SHXHALF1
	, SHXHALF2, SHX1W, SHX2W, FLOAT, DOUBLE
	, CALENDAR, SCHEDULE, TIMESTAMP, ANA4BCD, ANA4HEX
	, ANA4SHX, WIFI, STRING
}
