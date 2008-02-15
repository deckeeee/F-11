package org.F11.scada.theme;

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

import java.awt.Font;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public class DefaultWifeTheme extends DefaultMetalTheme {

	public DefaultWifeTheme() {
	}

	public String getName() { return "Wife"; }

	private final ColorUIResource primary1 =
		new ColorUIResource(102, 102, 153);
	private final ColorUIResource primary2 =
		new ColorUIResource(128, 128, 192);
	private final ColorUIResource primary3 =
		new ColorUIResource(159, 159, 235);

	private FontUIResource controlFont =
		new FontUIResource("Dialog", Font.PLAIN, 12);
	private FontUIResource systemFont =
		new FontUIResource("Dialog", Font.PLAIN, 12);
	private FontUIResource userFont =
		new FontUIResource("Dialog", Font.PLAIN, 12);
	private FontUIResource smallFont =
		new FontUIResource("Dialog", Font.PLAIN, 10);

	protected ColorUIResource getPrimary1() {
		return primary1;
	}
	protected ColorUIResource getPrimary2() {
		return primary2;
	}
	protected ColorUIResource getPrimary3() {
		return primary3;
	}

	public FontUIResource getControlTextFont() {
		return controlFont;
	}
	public FontUIResource getSystemTextFont() {
		return systemFont;
	}
	public FontUIResource getUserTextFont() {
		return userFont;
	}
	public FontUIResource getMenuTextFont() {
		return controlFont;
	}
	public FontUIResource getWindowTitleFont() {
		return controlFont;
	}
	public FontUIResource getSubTextFont() {
		return smallFont;
	}
}
