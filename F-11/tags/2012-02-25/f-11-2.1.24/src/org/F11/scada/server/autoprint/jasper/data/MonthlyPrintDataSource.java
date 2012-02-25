/*
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
 */

package org.F11.scada.server.autoprint.jasper.data;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * 月報印刷に使用するデータソースを生成する処理クラスです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class MonthlyPrintDataSource
		extends AbstractPrintDataSource {

	private final DateFormat fmt = DateFormat.getDateTimeInstance();
	// Only in a test, it is used.
	private Timestamp startTimestamp;
	// Only in a test, it is used.
	private Timestamp endTimestamp;

	/**
	 * 月報データの開始日を現在の日時より算出します。
	 * @return 月報データの開始日を現在の日時より算出します。
	 */
	protected Timestamp getStartTimestamp() {
		if (this.startTimestamp != null) {
			return this.startTimestamp;
		}

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		if(log.isInfoEnabled()) {
			log.info(fmt.format(cal.getTime()));
		}

		return new Timestamp(cal.getTimeInMillis());
	}

	/**
	 * 月報データの終了日を現在の日時より算出します。
	 * @return 月報データの終了日を現在の日時より算出します。
	 */
	protected Timestamp getEndTimestamp() {
		if (this.endTimestamp != null) {
			return this.endTimestamp; 
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		if(log.isInfoEnabled()) {
			log.info(fmt.format(cal.getTime()));
		}

		return new Timestamp(cal.getTimeInMillis());
	}
	
	// Only in a test, it is used.
	void setStartTimestamp(Timestamp startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	// Only in a test, it is used.
	void setEndTimestamp(Timestamp endTimestamp) {
		this.endTimestamp = endTimestamp;
	}
}
