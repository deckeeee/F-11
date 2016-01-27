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

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JButton;

import org.F11.scada.applet.schedule.ScheduleRowModel;
import org.F11.scada.xwife.applet.PageChanger;

public class BarScheduleDialog extends AbstractScheduleDialog {
	private static final long serialVersionUID = -1101138034759981773L;

	public BarScheduleDialog(
			Frame frame,
			ScheduleRowModel model,
			boolean isSort,
			boolean isLenient,
			PageChanger changer) {
		super(frame, model, isSort, isLenient, changer);
	}

	public BarScheduleDialog(
			Dialog dialog,
			ScheduleRowModel model,
			boolean isSort,
			boolean isLenient,
			PageChanger changer) {
		super(dialog, model, isSort, isLenient, changer);
	}

	protected JButton createOkButton(AbstractScheduleDialog scheduleDialog) {
		return new BarOkButton(scheduleDialog, isSort, isLenient);
	}

	protected JButton createTimeButton(
			AbstractScheduleDialog scheduleDialog,
			int time,
			boolean hour) {
		return new BarTimeButton(scheduleDialog, time, hour, changer);
	}
}
