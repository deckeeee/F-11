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
 * CANCEL ボタンクラスです
 */
class CancelButton extends AbstractScheduleButton {
	private static final long serialVersionUID = -6361841021910640391L;

	CancelButton(AbstractScheduleDialog scheduleDialog) {
		super(scheduleDialog);
		init();
	}

	private void init() {
		addActionListener(this.scheduleDialog);
		setText("CANCEL");
		setInoutKeyMap("ESCAPE");
	}

	/**
	 * ボタンが押下された時の処理を記述します。
	 */
	public void pushButton() {
		this.scheduleDialog.dispose();
	}
}
