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

package org.F11.scada.applet.dialog.schedule;


/**
 * OK ボタンクラスです
 */
class DefaultOkButton extends AbstractOkButton {
	private static final long serialVersionUID = 6913668124309820409L;

	DefaultOkButton(AbstractScheduleDialog scheduleDialog, boolean isSort, boolean isLenient) {
		super(scheduleDialog, isSort, isLenient);
	}

	protected boolean isNotRegalTime(int[][] times) {
		for (int i = 0; i < times.length; i++) {
			int onTime = times[i][0];
			int offTime = times[i][1];
			if (!isLenient && onTime != 0 && offTime != 0 && onTime > offTime) {
				return true;
			}
		}
		return false;
	}
}
