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

package org.F11.scada.server.logging.report.schedule;

import java.util.Calendar;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

public class DayOutputState implements OutputState {
	private final Logger logger = Logger.getLogger(DayOutputState.class);
	private Calendar current;

	/*
	 * @see org.F11.scada.server.logging.report.schedule.OutputState#isOutput()
	 */
	public boolean isOutput() {
		if (!CsvScheduleUtil.isOutputMode()) {
			return true;
		}

		Calendar cal = Calendar.getInstance();
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH);
		int d = cal.get(Calendar.DAY_OF_MONTH);
		cal.clear();
		cal.set(y, m, d);
		if (null == current || !current.equals(cal)) {
			if (null != current) {
				FastDateFormat f = FastDateFormat
						.getInstance("yyyy/MM/dd HH:mm:ss");
				logger.info("current=" + f.format(current) + ", now="
						+ f.format(cal));
			}
			current = cal;
			return true;
		}
		return false;
	}
}
