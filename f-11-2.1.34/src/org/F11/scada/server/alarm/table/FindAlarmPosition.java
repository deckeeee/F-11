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
package org.F11.scada.server.alarm.table;

import java.io.Serializable;

/**
 * 警報一覧のレコード位置を保持します。
 * 
 * @author hori <hoti@users.sourceforge.jp>
 */
public class FindAlarmPosition implements Serializable {
	private static final long serialVersionUID = -1176133926477442214L;
	/** 検索件数 */
	private final long limit;
	/** 検索位置 */
	private final long offset;
	/** 最大レコード数 */
	private final long maxrec;

	/**
	 *  
	 */
	private FindAlarmPosition(long limit, long offset, long maxrec) {
		this.limit = limit;
		this.offset = offset;
		this.maxrec = maxrec;
	}
	public static final FindAlarmPosition createFindAlarmPosition(long limit) {
		return new FindAlarmPosition(limit, 0, 0);
	}
	public FindAlarmPosition setMaxrec(long maxrec) {
		return new FindAlarmPosition(limit, offset, maxrec);
	}

	public long getLimit() {
		return limit;
	}
	public long getOffset() {
		return offset;
	}

	public FindAlarmPosition getNext() {
		if (maxrec <= offset + limit)
			return this;
		return new FindAlarmPosition(limit, offset + limit, maxrec);
	}

	public FindAlarmPosition getPrev() {
		if (offset < limit)
			return this;
		return new FindAlarmPosition(limit, offset - limit, maxrec);
	}

	public String toString() {
		return "FindAlarmPosition[limit=" + limit + ",offset=" + offset + ",maxrec=" + maxrec + "]";
	}
}
