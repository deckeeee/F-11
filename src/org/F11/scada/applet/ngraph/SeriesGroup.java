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

import java.util.List;

/**
 * シリーズのグループデータ
 * 
 * @author maekawa
 *
 */
public class SeriesGroup {
	private final String groupName;
	private final List<SeriesProperties> seriesProperties;

	public SeriesGroup(String groupName, List<SeriesProperties> seriesProperties) {
		this.groupName = groupName;
		this.seriesProperties = seriesProperties;
	}

	public String getGroupName() {
		return groupName;
	}

	public List<SeriesProperties> getSeriesProperties() {
		return seriesProperties;
	}
}
