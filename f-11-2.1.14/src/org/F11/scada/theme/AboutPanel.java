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

package org.F11.scada.theme;

import java.awt.Component;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.F11.scada.xwife.explorer.ExplorerElement;

public class AboutPanel extends JLabel implements ExplorerElement {
	private static final long serialVersionUID = -2204318201180400737L;

	public AboutPanel() {
		super(getHtml());
		setBorder(BorderFactory.createEmptyBorder(30, 30, 80, 30));
		setBackground(SystemColor.window);
		setOpaque(true);
	}

	private static String getHtml() {
		return Version.getLicense();
	}

	public Component getComponent() {
		return this;
	}
}
