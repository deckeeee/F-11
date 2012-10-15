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

package org.F11.scada.xwife.applet.alarm;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.F11.scada.xwife.applet.PageChanger;

public class LockAction extends AbstractAction {
	private static final long serialVersionUID = -5819725371821834481L;
	private PageChanger pageChanger;

	public LockAction(String name, Icon icon, PageChanger pageChanger) {
		super(name, icon);
		this.pageChanger = pageChanger;
	}

	public void actionPerformed(ActionEvent actionEvent) {
		if (pageChanger.isDisplayLock())
			pageChanger.setDisplayLock(false);
		else
			pageChanger.setDisplayLock(true);
	}
}