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

public class ScreenLockButton extends JButton {
	private static final long serialVersionUID = 695040666580116852L;

	public ScreenLockButton(final AbstractWifeApplet wifeApplet) {
		super(GraphicManager.get("/images/sclock.png"));
		setToolTipText("������ʐؑ֋֎~");

		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (wifeApplet.isDisplayLock()) {
					wifeApplet.setDisplayLock(false);
					setToolTipText("������ʐؑ֋֎~");
					setIcon(GraphicManager.get("/images/sclock.png"));
				} else {
					wifeApplet.setDisplayLock(true);
					setToolTipText("������ʐؑ֋֎~����");
					setIcon(GraphicManager.get("/images/scunlock.png"));
				}
			}
		});
	}
}
