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

package org.F11.scada.applet.ngraph;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.collections.primitives.DoubleCollections;
import org.apache.commons.collections.primitives.DoubleList;

/**
 * 表示しているデータ
 * 
 * @author maekawa
 *
 */
public class LogData implements Serializable {
	private static final long serialVersionUID = 1621006178387927799L;
	public static final LogData ZERO =
		new LogData(new Date(0L), DoubleCollections.EMPTY_DOUBLE_LIST);
	private final Date date;
	private final DoubleList values;

	public LogData(Date date, DoubleList values) {
		this.date = date;
		this.values = values;
	}

	public Date getDate() {
		return date;
	}

	public DoubleList getValues() {
		return values;
	}

	@Override
	public String toString() {
		Format f = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
		return f.format(date) + " : " + values;
	}
}
