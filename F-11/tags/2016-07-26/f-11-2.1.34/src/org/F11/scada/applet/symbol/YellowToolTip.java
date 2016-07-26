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

package org.F11.scada.applet.symbol;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JToolTip;

/**
 * 背景が薄黄色で文字が黒色のツールチップクラスです。
 * @author maekawa
 *
 */
class YellowToolTip extends JToolTip {
	private static final long serialVersionUID = -4526532818538159764L;
	private static Color backColor = new Color(255, 255, 224);
	
	YellowToolTip(JComponent component) {
		setComponent(component);
	}

	protected void paintComponent(Graphics g) {
		setBackground(backColor);
		setForeground(Color.BLACK);
		super.paintComponent(g);
	}
}
