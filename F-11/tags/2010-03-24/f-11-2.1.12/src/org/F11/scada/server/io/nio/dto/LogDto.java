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

package org.F11.scada.server.io.nio.dto;

import java.sql.Timestamp;

public class LogDto {
	private long id;
	private Timestamp writedate;
	private int revision;
	private String holderid;
	private double value;

	public LogDto() {}
	
	public LogDto(Timestamp writedate, int revision, String holderid, double value) {
		this.writedate = writedate;
		this.revision = revision;
		this.holderid = holderid;
		this.value = value;
	}

	public String getHolderid() {
		return holderid;
	}
	public void setHolderid(String holderid) {
		this.holderid = holderid;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getRevision() {
		return revision;
	}
	public void setRevision(int revision) {
		this.revision = revision;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public Timestamp getWritedate() {
		return writedate;
	}
	public void setWritedate(Timestamp writedate) {
		this.writedate = writedate;
	}
	/**
	 * このオブジェクトの内容でwritedate, revision, holderid, valueのObjectの配列を返します。
	 * @return このオブジェクトの内容でwritedate, revision, holderid, valueのObjectの配列を返します。
	 */
	public Object[] toObjectArray() {
		return new Object[]{writedate, new Integer(revision), holderid, new Double(value)};
	}
	public String toString() {
		return "id=" + id + ", writedate=" + writedate + ", revision=" + revision + ", holderid=" + holderid + ", value=" + value;
	}
}
