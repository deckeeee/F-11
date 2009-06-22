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

package org.F11.scada.xwife.applet.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.xwife.applet.AbstractWifeApplet;

public class AlarmSoundLockButton extends JButton {
	private static final long serialVersionUID = 8692460921260246988L;

	public AlarmSoundLockButton(final AbstractWifeApplet wifeApplet) {
		super(GraphicManager.get("/images/sdlock.png"));
		setToolTipText("åxïÒâπã÷é~");

		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (wifeApplet.isAlarmSoundLock()) {
					wifeApplet.setAlarmSoundLock(false);
					setToolTipText("åxïÒâπã÷é~");
					setIcon(GraphicManager.get("/images/sdlock.png"));
				} else {
					wifeApplet.setAlarmSoundLock(true);
					setToolTipText("åxïÒâπã÷é~âèú");
					setIcon(GraphicManager.get("/images/sdunlock.png"));
				}
			}
		});
	}
}
