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
 *
 */
package org.F11.scada.applet.symbol;

import org.F11.scada.applet.schedule.ScheduleModel;
import org.F11.scada.data.ConvertValue;

/**
 * @author hori
 */
public interface ScheduleEditable extends Editable {
	public ScheduleModel getScheduleModel();
	public void addScheduleHolder(String providerName, String holderName);
	public void addScheduleHolder(String id);
	public ConvertValue getConvertValue();
	public String getValue();
	public void setValue(String value);
	void setGroupHolder(String id);
}
