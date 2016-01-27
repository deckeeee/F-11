/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.server.command;

import static java.util.Calendar.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.F11.scada.server.alarm.DataValueChangeEventKey;

/**
 * 日報(週報)印刷用クラスです。
 */
public class WeeklyPrintCommand extends AbstractPrintCommand {
	/** スレッドプール実行クラス */
	private static Executor executor = Executors.newCachedThreadPool();

	public void execute(DataValueChangeEventKey evt) {
		String csvname = csv_dir + csv_head;
		if (0 < csv_mid.length()) {
			SimpleDateFormat sf = new SimpleDateFormat(csv_mid);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(evt.getTimeStamp().getTime());
			if (MONDAY == cal.get(DAY_OF_WEEK)) {
				cal.add(DATE, -1);
			}

			while (MONDAY != cal.get(DAY_OF_WEEK)) {
				cal.add(DATE, -1);
			}

			csvname += sf.format(cal.getTime());
		}
		csvname += csv_foot;

		try {
			executor.execute(new ListOutPrintTask(evt, "日報(週報)", csvname));
		} catch (RejectedExecutionException e) {
		}
	}
}
