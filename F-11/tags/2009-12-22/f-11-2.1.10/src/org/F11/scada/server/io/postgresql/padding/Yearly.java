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

package org.F11.scada.server.io.postgresql.padding;

import java.sql.Timestamp;
import java.util.Calendar;

import org.F11.scada.server.io.postgresql.PostgreSQLUtility;

public class Yearly extends AbstractPaddingLogic {

	public Yearly(PostgreSQLUtility utility) {
		super(utility);
	}

	public Timestamp afterTime(Timestamp timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		cal.add(Calendar.YEAR, 1);
		return new Timestamp(cal.getTimeInMillis());
	}

	public Timestamp beforeTime(Timestamp timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		cal.add(Calendar.YEAR, -1);
		return new Timestamp(cal.getTimeInMillis());
	}
}
