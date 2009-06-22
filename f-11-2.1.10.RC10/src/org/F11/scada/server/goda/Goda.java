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

package org.F11.scada.server.goda;

import java.awt.Component;

import javax.swing.JLabel;

import org.F11.scada.scheduling.Schedule;
import org.F11.scada.scheduling.Scheduler;
import org.F11.scada.xwife.explorer.ExplorerElement;
import org.apache.log4j.Logger;

public class Goda extends JLabel implements ExplorerElement {
	private static final long serialVersionUID = -4487848899521268648L;
	private final Logger logger = Logger.getLogger(Goda.class);
	private final Scheduler scheduler;

	public Goda() {
		scheduler = new Scheduler();
		logger.info("start GODA");
		setText("GODA");
	}

	public void schedule(Schedule schedule) {
		scheduler.schedule(schedule);
	}

	public Component getComponent() {
		return this;
	}
}
