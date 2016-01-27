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
 * �x��ꗗ�̌������ʂ�ێ����܂��B
 * 
 * @author hori <hoti@users.sourceforge.jp>
 */
public class FindAlarmTable implements Serializable {
	private static final long serialVersionUID = 2125255100252997513L;
	/** �ő僌�R�[�h�� */
	private final long maxrec;
	/** �������� */
	private final Object[][] records;

	/**
	 *  
	 */
	public FindAlarmTable() {
		this(0, new Object[0][0]);
	}
	public FindAlarmTable(long maxrec) {
		this(maxrec, new Object[0][0]);
	}
	public FindAlarmTable(long maxrec, Object[][] records) {
		this.maxrec = maxrec;
		this.records = records;
	}

	public long getMaxrec() {
		return maxrec;
	}
	public Object[][] getRecords() {
		return records;
	}
}
